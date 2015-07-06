package org.shaolin.bmdp.persistence.query.generator;

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.persistence.provider.DBMSProviderFactory;
import org.shaolin.bmdp.persistence.provider.OracleProvider;
import org.shaolin.bmdp.persistence.query.mapping.ICategoryDescriptor;
import org.shaolin.bmdp.persistence.query.mapping.IClassDescriptor;
import org.shaolin.bmdp.persistence.query.mapping.IFieldDescriptor;
import org.shaolin.bmdp.persistence.query.mapping.JavaToSqlCommand;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.exception.QueryParsingException;
import org.shaolin.javacc.sql.node.OQLBinaryExpression;
import org.shaolin.javacc.sql.node.OQLCategoryField;
import org.shaolin.javacc.sql.node.OQLCommonField;
import org.shaolin.javacc.sql.node.OQLConditionalExpression;
import org.shaolin.javacc.sql.node.OQLExtendField;
import org.shaolin.javacc.sql.node.OQLFieldName;
import org.shaolin.javacc.sql.node.OQLFieldPart;
import org.shaolin.javacc.sql.node.OQLFunction;
import org.shaolin.javacc.sql.node.OQLIsNullExpression;
import org.shaolin.javacc.sql.node.OQLLiteral;
import org.shaolin.javacc.sql.node.OQLName;
import org.shaolin.javacc.sql.node.OQLParam;
import org.shaolin.javacc.sql.node.OQLSystemField;
import org.shaolin.javacc.sql.node.OQLTypeName;
import org.shaolin.javacc.sql.node.OQLUnaryExpression;

public class QueryParserVisitor implements IOQLNodeVisitor
{

    private TableInstance preTableInstance = null;
    private String preFieldName = null;

    private Class currentClass = null;
    private String currentCustRDB = null;
    private TableInstance tableInstance = null;
    private IClassDescriptor clazzDesc = null;
    private ICategoryDescriptor catDesc = null;

    private String fieldName = null;
    private IFieldDescriptor fieldDesc = null;

    private boolean visitingName = false;
    private Class castFieldClass = null;
    private String castCustRDB = null;

    private TableInstance collectionTableInstance = null;

    private String columnName = null;

    private int fieldPartIndex = 0;
    private boolean primitiveField = false;

    private boolean isSelect = false;

    private boolean canReferenceEncodedField = false;
    private Operator op = null;
    private Object value = null;
    private QueryExpression expr = null;

    //query scope context
    private QueryParsingContext context = null;
    private List tables = null;

    private PrintWriter writer = null;

    private static  HashMap extFieldColumns = new HashMap(8);
    private static  HashMap extFieldTypes = new HashMap(8);

    static
    {
        extFieldColumns.put(int.class, "INTVALUE");
        extFieldColumns.put(boolean.class, "INTVALUE");
        extFieldColumns.put(long.class, "LONGVALUE");
        extFieldColumns.put(double.class, "DOUBLEVALUE");
        extFieldColumns.put(String.class, "STRINGVALUE");
        extFieldColumns.put(java.util.Date.class, "DATEVALUE");
        extFieldColumns.put(java.sql.Timestamp.class, "DATEVALUE");
        extFieldColumns.put(byte[].class, "BINARYVALUE");

        extFieldTypes.put(int.class, FieldType.INT);
        extFieldTypes.put(boolean.class, FieldType.BOOLEAN);
        extFieldTypes.put(long.class, FieldType.LONG);
        extFieldTypes.put(double.class, FieldType.DOUBLE);
        extFieldTypes.put(String.class, FieldType.STRING);
        extFieldTypes.put(java.util.Date.class, FieldType.DATETIME);
        extFieldTypes.put(java.sql.Timestamp.class, FieldType.DATETIME);
        extFieldTypes.put(byte[].class, FieldType.BINARY);
    }

    public QueryParserVisitor(Writer writer,
        QueryParsingContext context, 
        boolean isSelect)
    {
        this(writer, context, isSelect, false, null, null, null);
    }

    public QueryParserVisitor(Writer writer,
        QueryParsingContext context, 
        boolean isSelect, boolean canReferenceEncodedField,
        Operator op, Object value, QueryExpression expr)
    {
        this.writer = new PrintWriter(writer);
        //this.con = con;

        this.context = context;
        this.tables = context.getFrom();

        this.isSelect = isSelect;

        this.canReferenceEncodedField = canReferenceEncodedField;
        this.op = op;
        this.value = value;
        this.expr = expr;
    }

    public void startVisitBinaryExpression(OQLBinaryExpression oqlExpr) throws QueryParsingException
    {
        if (oqlExpr.getNexts().size() > 0)
        {
            writer.print(SQLConstants.OPEN_BRACKET);
        }
    }

    public void visitBinaryExpressionOp(String op) throws QueryParsingException
    {
        //TODO
        //SearchConstants.CONCAT | SearchConstants.PLUS ?
        writer.print(SQLConstants.SPACE + op + SQLConstants.SPACE);
    }

    public void endVisitBinaryExpression(OQLBinaryExpression oqlExpr) throws QueryParsingException
    {
        if (oqlExpr.getNexts().size() > 0)
        {
            writer.print(SQLConstants.CLOSE_BRACKET);
        }
    }

    public void visitCategoryField(OQLCategoryField oqlExpr) throws QueryParsingException
    {
        String in = oqlExpr.getIn();
        //must be 'in'
        if (!SQLConstants.IN.equalsIgnoreCase(in))
        {
            throw new QueryParsingException("Invalid category field,''in'' expected");
        }
        //must be 'collection contains'
        if (op != Operator.COLLECTION_CONTAINS)
        {
            throw new QueryParsingException("Invalid category search operator:" +
                op);
        }

        String objectType = oqlExpr.getObjType();
        String categoryName = oqlExpr.getCategoryName();

        resolveCategoryField(objectType, categoryName);

        StringBuffer sb = new StringBuffer();
        if (objectType.equalsIgnoreCase(SQLConstants.PARENTS))
        {
            boolean isItem = catDesc.getItemClass() != null &&
                catDesc.getItemClass().isAssignableFrom(currentClass);
            List descList = isItem ? catDesc.getItemParentPKDescriptorList() :
                catDesc.getNodeParentPKDescriptorList();
            /**
            if (value == null)
            {
                DefaultCategoryDescriptor.appendColumns(sb, descList,
                    DefaultCategoryDescriptor.SEARCH_NULL,
                    isItem ? catDesc.getItemParentIdNullValue() :
                        catDesc.getNodeParentIdNullValue(), tableInstance.getAlias());
            }
            else
            {
                DefaultCategoryDescriptor.appendColumns(sb, descList,
                    DefaultCategoryDescriptor.SEARCH, null, tableInstance.getAlias());

                if (!catDesc.getNodeClass().isInstance(value))
                {
                    throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_216,new Object[]{value,catDesc.getCategoryName()});
                //    throw new QueryParsingException("entity:" +
                  //      value + " is not a legal node entity for category:" +
                    //    catDesc.getCategoryName());
                }
//                bindCategoryObject(
//                    Arrays.asList(Mapper.getRootClassDescriptor(catDesc.
//                        getNodeClassDescriptor()).getPrimaryKeyFieldDescriptors()),
//                    value, context, false);
            }
            */
        }
        else if (objectType.equalsIgnoreCase(SQLConstants.CHILD_NODES))
        {
            if (!catDesc.getNodeClass().isInstance(value))
            {
                throw new QueryParsingException("Entity:{0}is not a legal node entity of category {1}",
                		new Object[]{value,catDesc.getCategoryName()});
            }

            /**
            List nodeParentPKList = catDesc.getNodeParentPKDescriptorList();
            Object[] valueParentPK = new Object[nodeParentPKList.size()];
            for (int i = 0, n = nodeParentPKList.size(); i < n; i++)
            {
                IFieldDescriptor fieldDesc = (IFieldDescriptor)
                    nodeParentPKList.get(i);
                valueParentPK[i] = Mapper.getFieldValue(value, fieldDesc);
            }
            if (CategoryUtil.equals(valueParentPK, catDesc.getNodeParentIdJavaNullValue()))
            {
                sb.append("1=2"); //false
            }
            else
            {
                IFieldDescriptor[] pkField = Mapper.getRootClassDescriptor(catDesc.
                    getNodeClassDescriptor()).getPrimaryKeyFieldDescriptors();
                DefaultCategoryDescriptor.appendColumns(sb, Arrays.asList(pkField),
                    DefaultCategoryDescriptor.SEARCH, null, tableInstance.getAlias());

                bindCategoryObject(catDesc.getNodeParentPKDescriptorList(),
                    value, context, dbHandler, false);
            }
            */
        }
        else if (objectType.equalsIgnoreCase(SQLConstants.CHILD_ITEMS))
        {
            if (catDesc.getItemClass() == null ||
                !catDesc.getItemClass().isInstance(value))
            {
                throw new QueryParsingException("Entity:{0}is not a legal node entity of category {1}",
                		new Object[]{value,catDesc.getCategoryName()});
            }

            /**
            List itemParentPKList = catDesc.getItemParentPKDescriptorList();
            Object[] valueParentPK = new Object[itemParentPKList.size()];
            for (int i = 0, n = itemParentPKList.size(); i < n; i++)
            {
                IFieldDescriptor fieldDesc = (IFieldDescriptor)
                    itemParentPKList.get(i);
                valueParentPK[i] = Mapper.getFieldValue(value, fieldDesc);
            }

            if (CategoryUtil.equals(valueParentPK, catDesc.getItemParentIdJavaNullValue()))
            {
                sb.append("1=2"); //false
            }
            else
            {
                DefaultCategoryDescriptor.appendColumns(sb, catDesc.getNodePKDescriptorList(),
                    DefaultCategoryDescriptor.SEARCH, null, tableInstance.getAlias());

                bindCategoryObject(catDesc.getItemParentPKDescriptorList(),
                    value, context, dbHandler, false);
            }
            */
        }
        else if (objectType.equalsIgnoreCase(SQLConstants.ANCESTORS))
        {
            boolean isItem = catDesc.getItemClass() != null &&
                catDesc.getItemClass().isAssignableFrom(currentClass);
            /**
            String nodeTableName = Mapper.getTableName(catDesc.getNodeClassDescriptor());
            collectionTableInstance = tableInstance.getFieldTableInstance(
                SQLConstants.ANCESTORS + SQLConstants.EXCLAMATION + categoryName, nodeTableName);

            String join = null;
            String startWith = null;
            if (isItem)
            {
                //join
                StringBuffer joinSb = new StringBuffer();
                DefaultCategoryDescriptor.join(joinSb, null,
                    null, catDesc.getNodePKDescriptorList(),
                    catDesc.getItemParentPKDescriptorList(),
                    collectionTableInstance.getAlias(), tableInstance.getAlias(),
                    false, true, false);
                joinSb.append(" AND ");
                joinSb.append(tableInstance.getAlias());
                joinSb.append(".BOID IS NOT NULL");
                join = new String(joinSb);

                //start with
                StringBuffer startWithSb = new StringBuffer();
                if (value != null)
                {
                    DefaultCategoryDescriptor.appendColumns(startWithSb,
                        catDesc.getNodePKDescriptorList(),
                        DefaultCategoryDescriptor.SEARCH,
                        null, collectionTableInstance.getAlias());
                }
                else
                {
                    DefaultCategoryDescriptor.appendColumns(startWithSb,
                        catDesc.getNodeParentPKDescriptorList(),
                        DefaultCategoryDescriptor.SEARCH_NULL,
                        catDesc.getNodeParentIdNullValue(), collectionTableInstance.getAlias());
                }
                startWith = new String(startWithSb);
            }
            else
            {
                //join
                List nodePKDescList = catDesc.getNodePKDescriptorList();

                StringBuffer joinSb = new StringBuffer();
                DefaultCategoryDescriptor.join(joinSb, null, null, nodePKDescList,
                    nodePKDescList, collectionTableInstance.getAlias(), tableInstance.getAlias(),
                    false, false, false);
                join = new String(joinSb);

                //start with
                StringBuffer startWithSb = new StringBuffer();
                if (value != null)
                {
                    DefaultCategoryDescriptor.appendColumns(startWithSb,
                        catDesc.getNodeParentPKDescriptorList(),
                        DefaultCategoryDescriptor.SEARCH, null, collectionTableInstance.getAlias());
                }
                else
                {
                    DefaultCategoryDescriptor.appendColumns(startWithSb,
                        catDesc.getNodeParentPKDescriptorList(),
                        DefaultCategoryDescriptor.SEARCH_NULL,
                        catDesc.getNodeParentIdNullValue(), collectionTableInstance.getAlias());
                }
                startWith = new String(startWithSb);
            }
            //connect by
            StringBuffer connectBySb = new StringBuffer();
            DefaultCategoryDescriptor.appendConnectCondition(connectBySb, null, null,
                catDesc.getNodePKDescriptorList(), catDesc.getNodeParentPKDescriptorList(),
                collectionTableInstance.getAlias());
            String connectBy = new String(connectBySb);

            if (value != null)
            {
//                bindCategoryObject(catDesc.getNodePKDescriptorList(),
//                    value, context, dbHandler, true);
            }

            //true
            sb.append("1=1");

            setCategoryInfoToContext(startWith, join, connectBy);
            */
        }
        else if (objectType.equalsIgnoreCase(SQLConstants.DESCENDANT_NODES))
        {
            if (!catDesc.getNodeClass().isInstance(value))
            {
                throw new QueryParsingException("Entity:{0}is not a legal node entity of category {1}",
                		new Object[]{value,catDesc.getCategoryName()});
            }

            /**
            List nodeParentPKList = catDesc.getNodeParentPKDescriptorList();
            Object[] valueParentPK = new Object[nodeParentPKList.size()];
            for (int i = 0, n = nodeParentPKList.size(); i < n; i++)
            {
                IFieldDescriptor fieldDesc = (IFieldDescriptor)
                    nodeParentPKList.get(i);
                valueParentPK[i] = Mapper.getFieldValue(value, fieldDesc);
            }
            if (CategoryUtil.equals(valueParentPK, catDesc.getNodeParentIdJavaNullValue()))
            {
                sb.append("1=2"); //false
            }
            else
            {
                String nodeTableName = Mapper.getTableName(catDesc.getNodeClassDescriptor());
                collectionTableInstance = tableInstance.getFieldTableInstance(
                    SQLConstants.DESCENDANTS + SQLConstants.EXCLAMATION + categoryName, nodeTableName);

                //join
                StringBuffer joinSb = new StringBuffer();
                DefaultCategoryDescriptor.join(joinSb, null,
                    null, catDesc.getNodeParentPKDescriptorList(),
                    catDesc.getNodePKDescriptorList(),
                    collectionTableInstance.getAlias(), tableInstance.getAlias(),
                    false, false, false);
                String join = new String(joinSb);

                //start with
                StringBuffer startWithSb = new StringBuffer();
                DefaultCategoryDescriptor.appendColumns(startWithSb,
                    catDesc.getNodePKDescriptorList(),
                    DefaultCategoryDescriptor.SEARCH, null, collectionTableInstance.getAlias());
                String startWith = new String(startWithSb);

                //connect by
                StringBuffer connectBySb = new StringBuffer();
                DefaultCategoryDescriptor.appendConnectCondition(connectBySb, null, null,
                    catDesc.getNodeParentPKDescriptorList(), catDesc.getNodePKDescriptorList(),
                    collectionTableInstance.getAlias());
                String connectBy = new String(connectBySb);

                bindCategoryObject(catDesc.getNodePKDescriptorList(),
                    value, context, dbHandler, true);

                //true
                sb.append("1=1");

                setCategoryInfoToContext(startWith, join, connectBy);
            }
            */
        }
        else if (objectType.equalsIgnoreCase(SQLConstants.DESCENDANT_ITEMS))
        {
            if (catDesc.getItemClass() == null ||
                !catDesc.getItemClass().isInstance(value))
            {
                throw new QueryParsingException("Entity:{0}is not a legal node entity of category {1}",
                		new Object[]{value,catDesc.getCategoryName()});
            }

            /** 
            List itemParentPKList = catDesc.getItemParentPKDescriptorList();
            Object[] valueParentPK = new Object[itemParentPKList.size()];
            for (int i = 0, n = itemParentPKList.size(); i < n; i++)
            {
                IFieldDescriptor fieldDesc = (IFieldDescriptor)
                    itemParentPKList.get(i);
                valueParentPK[i] = Mapper.getFieldValue(value, fieldDesc);
            }
            if (CategoryUtil.equals(valueParentPK, catDesc.getItemParentIdJavaNullValue()))
            {
                sb.append("1=2"); //false
            }
            else
            {
                String nodeTableName = Mapper.getTableName(catDesc.getNodeClassDescriptor());
                collectionTableInstance = tableInstance.getFieldTableInstance(
                    SQLConstants.DESCENDANTS + SQLConstants.EXCLAMATION + categoryName, nodeTableName);

                //join
                StringBuffer joinSb = new StringBuffer();
                DefaultCategoryDescriptor.join(joinSb, null,
                    null, catDesc.getNodePKDescriptorList(),
                    catDesc.getNodePKDescriptorList(),
                    collectionTableInstance.getAlias(), tableInstance.getAlias(),
                    false, false, false);
                String join = new String(joinSb);

                //start with
                StringBuffer startWithSb = new StringBuffer();
                DefaultCategoryDescriptor.appendColumns(startWithSb,
                    catDesc.getNodePKDescriptorList(),
                    DefaultCategoryDescriptor.SEARCH, null, collectionTableInstance.getAlias());
                String startWith = new String(startWithSb);

                //connect by
                StringBuffer connectBySb = new StringBuffer();
                DefaultCategoryDescriptor.appendConnectCondition(connectBySb, null, null,
                    catDesc.getNodeParentPKDescriptorList(), catDesc.getNodePKDescriptorList(),
                    collectionTableInstance.getAlias());
                String connectBy = new String(connectBySb);

                bindCategoryObject(catDesc.getItemParentPKDescriptorList(),
                    value, context, dbHandler, true);
				
                //true
                sb.append("1=1");

                setCategoryInfoToContext(startWith, join, connectBy);
            }
            */
        }
        else
        {
            throw new QueryParsingException("Invalid category object type:" + objectType);
        }
        columnName = new String(sb);

        tableInstance = null;
        currentClass = null;
        currentCustRDB = null;
    }

    public void visitCommonField(OQLCommonField oqlExpr) throws QueryParsingException
    {
        fieldName = oqlExpr.getFieldName();
		if (tableInstance != null) {
			// column field
			columnName = tableInstance.getAlias() + SQLConstants.DOT
					+ fieldName.toUpperCase();
		} else {
			// table name
			if (preFieldName == null) {
				preFieldName = fieldName;
			} else {
				columnName = preFieldName + SQLConstants.DOT
						+ fieldName.toUpperCase();
			}
		}

        /**
        if (compositeFieldDesc != null)
        {
            IFieldDescriptor cFieldDesc =
                compositeFieldDesc.getCompositeFieldMapping(fieldName);
            if (cFieldDesc == null)
            {
                throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_181,new Object[]{fieldName,preFieldName});
//                throw new QueryParsingException(
//                    "Can't find field:" + fieldName +
//                    " in field:" + preFieldName);
            }
            if (cFieldDesc instanceof CompositeFieldDescriptor)
            {
                compositeFieldDesc = (CompositeFieldDescriptor)fieldDesc;
                return;
            }
            if (cFieldDesc instanceof CompressedFieldDescriptor)
            {
                throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_195,new Object[]{fieldName});
               // throw new QueryParsingException("Can't search compressed field:" + fieldName);
            }
            if (cFieldDesc instanceof EncodedFieldDescriptor)
            {
                encodedFieldDetected(cFieldDesc, fieldName);
            }
            else
            {
                columnName = tableInstance.getAlias() +
                    SQLConstants.DOT + cFieldDesc.getColumnNames()[0];
                context.setExpressionDatabaseType(cFieldDesc.getColumnTypes()[0]);
                context.setExpressionPFName(cFieldDesc.getPFFieldName());
                if (cFieldDesc instanceof ConvertFieldDescriptor && isSelect)
                {
                    //1. Only int to String conversion is supported
                    //   by ConvertFieldDescriptor now. So we just use
                    //   database TO_CHAR routine here
                    //   TODO: support other data types' conversion
                    columnName = DBMSProviderFactory.getProvider().getToCharFunction(columnName);
                }
            }
            compositeFieldDesc = null;
            currentClass = null;
            currentCustRDB = null;
            return;
        }
        fieldDesc = null;
        resolveCommonField();

        if (fieldDesc instanceof CompositeFieldDescriptor)
        {
            compositeFieldDesc = (CompositeFieldDescriptor)fieldDesc;
            return;
        }
        if (fieldDesc instanceof CompressedFieldDescriptor)
        {
            throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_195,new Object[]{fieldName});
       //     throw new QueryParsingException("Can't search compressed field:" + fieldName);
        }
        if (fieldDesc instanceof XMLObjFieldDescriptor)
        {
          //  throw new QueryParsingException("Can't search xml obj field:" + fieldName);
        }
        if (fieldDesc instanceof CollectionFieldDescriptor)
        {
            CollectionFieldDescriptor colFieldDesc =
                (CollectionFieldDescriptor)fieldDesc;
            String collectionTableName = colFieldDesc.getCollectionTableName();
            collectionTableInstance =
                tableInstance.getFieldTableInstance(fieldName, collectionTableName);
            columnName = collectionTableInstance.getAlias() +
                SQLConstants.DOT + colFieldDesc.getItemOIDColumnName();
            context.setExpressionDatabaseType(Types.BIGINT);
//            collectionTableInstance.getContext().addExpressionJoinClause(
//                tableInstance.getAlias() +
//                SQLConstants.DOT +
//                ResourceNames.COLUMN_OID +
//                SQLConstants.SPACE +
//                SQLConstants.EQUAL +
//                SQLConstants.SPACE +
//                collectionTableInstance.getAlias() +
//                SQLConstants.DOT +
//                ResourceNames.COLUMN_OID);

            tableInstance = null;
            return;
        }
        if (fieldDesc instanceof EncodedFieldDescriptor)
        {
            encodedFieldDetected(fieldDesc, fieldName);
            Field field = clazzDesc.getField(fieldName);
            currentClass = field.getType();
            currentCustRDB = null;
            primitiveField = true;
            return;
        }
        //common, constant, sequence, obj ref, convert
        columnName = tableInstance.getAlias() +
            SQLConstants.DOT + fieldDesc.getColumnNames()[0];
        context.setExpressionDatabaseType(fieldDesc.getColumnTypes()[0]);
        context.setExpressionPFName(fieldDesc.getPFFieldName());
        if (fieldDesc instanceof ConvertFieldDescriptor && isSelect)
        {
            //1. Only int to String conversion is supported
            //   by ConvertFieldDescriptor now. So we just use
            //   database TO_CHAR routine here
            //   TODO: support other data types' conversion
            columnName = DBMSProviderFactory.getProvider().getToCharFunction(columnName);
        }

        if (fieldDesc instanceof ObjRefFieldDescriptor)
        {
            ObjRefFieldDescriptor objRefFieldDesc =
                (ObjRefFieldDescriptor)fieldDesc;
            try {
				currentClass = BEUtil.getBEImplementClass(objRefFieldDesc.getRefEntityName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            currentCustRDB = null;
        }
        else
        {
            Field field = clazzDesc.getField(fieldName);
            currentClass = field.getType();
            currentCustRDB = null;
            primitiveField = true;
        }
        */
    }

    public void visitSystemField(OQLSystemField oqlExpr) throws QueryParsingException
    {
        fieldName = oqlExpr.getFieldName();
        resolveSystemField();
        /**
        CollectionFieldDescriptor colFieldDesc = tableInstance.getCollectionFieldDescriptor();
        if (colFieldDesc == null)
        {
            if (fieldName.equalsIgnoreCase("oid"))
            {
                columnName = tableInstance.getAlias() +
                    SQLConstants.DOT + ResourceNames.COLUMN_OID;
                context.setExpressionDatabaseType(Types.BIGINT);
            }
            else
            {
                throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_211,new Object[]{fieldName});
             //   throw new QueryParsingException("Unknown system field:" + fieldName);
            }
        }
        else
        {
            if (fieldName.equalsIgnoreCase("oid"))
            {
                columnName = tableInstance.getAlias() +
                    SQLConstants.DOT + colFieldDesc.getOIDColumnName();
                context.setExpressionDatabaseType(Types.BIGINT);
            }
            else if (fieldName.equalsIgnoreCase("item_oid"))
            {
                columnName = tableInstance.getAlias() +
                    SQLConstants.DOT + colFieldDesc.getItemOIDColumnName();
                context.setExpressionDatabaseType(colFieldDesc.getItemOIDColumnType());
            }
            else
            {
                throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_211,new Object[]{fieldName});
              //  throw new QueryParsingException("Unknown system field:" + fieldName);
            }
        }
        primitiveField = true;
         */
    }

    public void visitConditionalExpression(OQLConditionalExpression oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.SPACE + oqlExpr.getOperator() + SQLConstants.SPACE);
    }

    public void visitExtendField(OQLExtendField oqlExpr) throws QueryParsingException
    {
        fieldName = oqlExpr.getFieldName();
        resolveExtendField();

        String cName = getExtFieldColumn(castFieldClass);
        FieldType fieldType = getExtFieldType(castFieldClass);
        /**
        columnName = SQLConstants.OPEN_BRACKET +     // (
                    SQLConstants.SELECT +            // select
                    SQLConstants.SPACE +             //
                    SQLConstants.EXT_TABLE_ALIAS +   // et
                    SQLConstants.DOT +               // .
                    cName +                             // cName
                    SQLConstants.SPACE +             //
                    SQLConstants.FROM +              // from
                    SQLConstants.SPACE +             //
                    ORMapperController.schema +
                    ResourceNames.TABLE_EXTEND_FIELD +  // EBOS_OR_EXTFIELD
                    SQLConstants.SPACE +             //
                    SQLConstants.EXT_TABLE_ALIAS +   // et
                    SQLConstants.SPACE +             //
                    SQLConstants.WHERE +             // where
                    SQLConstants.SPACE +             //
                    SQLConstants.EXT_TABLE_ALIAS +   // et
                    SQLConstants.DOT +               // .
                    ResourceNames.COLUMN_OID +          // Boid
                    SQLConstants.SPACE +             //
                    SQLConstants.EQUAL +             // =
                    SQLConstants.SPACE +             //
                    tableInstance.getAlias() +          // tableAlias
                    SQLConstants.DOT +               // .
                    ResourceNames.COLUMN_OID +          // Boid
                    SQLConstants.SPACE +             //
                    SQLConstants.AND +               // and
                    SQLConstants.SPACE +             //
                    SQLConstants.EXT_TABLE_ALIAS +   // et
                    SQLConstants.DOT +               // .
                    ResourceNames.COLUMN_FIELDNAME +    // FIELD_NAME
                    SQLConstants.SPACE +             //
                    SQLConstants.EQUAL +             // =
                    SQLConstants.SPACE +             //
                    SQLConstants.SINGLE_QUOTE +      // '
                    fieldName +                         // fieldName
                    SQLConstants.SINGLE_QUOTE +      // '
                    SQLConstants.SPACE +             //
                    SQLConstants.AND +               // and
                    SQLConstants.SPACE +             //
                    SQLConstants.EXT_TABLE_ALIAS +   // et
                    SQLConstants.DOT +               // .
                    ResourceNames.COLUMN_FIELDTYPE +    // FIELDTYPE
                    SQLConstants.SPACE +             //
                    SQLConstants.EQUAL +             // =
                    SQLConstants.SPACE +             //
                    fieldType.getType() +               // fieldType
                    SQLConstants.CLOSE_BRACKET;      // )
                    */
        //use the default binding logic
        context.setExpressionDatabaseType(ExtendedSQLTypes.UNKNOWN);
        currentClass = null;
        currentCustRDB = null;
    }

    public void startVisitFieldName(OQLFieldName oqlExpr) throws QueryParsingException
    {
        fieldPartIndex = 0;
        primitiveField = false;
    }

    public void endVisitFieldName(OQLFieldName oqlExpr) throws QueryParsingException
    {

    }

    public void startVisitCast(OQLFieldPart oqlExpr) throws QueryParsingException
    {
    }

    public void endVisitCast(OQLFieldPart oqlExpr) throws QueryParsingException
    {
    }

    public void startVisitFunction(OQLFunction oqlExpr) throws QueryParsingException
    {
        String text = oqlExpr.getFunctionName().toUpperCase();
        if (!text.equals(SQLConstants.COUNT) &&
            !text.equals(SQLConstants.SUM) &&
            !text.equals(SQLConstants.MAX) &&
            !text.equals(SQLConstants.MIN) &&
            !text.equals(SQLConstants.AVG) &&
            !text.equals("RANDOM"))
        {
            throw new QueryParsingException("Unsupported function:" + text);
        }

        if (text.equals("RANDOM"))
        {
            writer.print(DBMSProviderFactory.getProvider().getRandomFunction());
        }
        else
        {
            writer.print(text);
        }
        writer.print(SQLConstants.OPEN_BRACKET);
        if (oqlExpr.isCountAllFunction())
        {
            writer.print(SQLConstants.STAR);
        }
    }

    public void endVisitFunction(OQLFunction oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.CLOSE_BRACKET);
    }

    public void visitIsNullExpression(OQLIsNullExpression oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.SPACE + SQLConstants.IS_NULL);
    }

    public void visitLiteral(OQLLiteral oqlExpr) throws QueryParsingException
    {
        writer.print(oqlExpr.getImage());
    }

    public void visitName(OQLName oqlExpr) throws QueryParsingException
    {
    }

    public void startVisitName(OQLName oqlExpr) throws QueryParsingException
    {
        collectionTableInstance = null;

        currentClass = null;
        currentCustRDB = null;
        tableInstance = null;

        fieldName = null;
        fieldDesc = null;
        columnName = null;

        if (oqlExpr.getTypeName() != null)
        {
            visitingName = true;
        }
    }

    public void endVisitName(OQLName oqlExpr) throws QueryParsingException
    {
//        if (compositeFieldDesc != null)
//        {
//            throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_214,new Object[]{fieldName});
//            //throw new QueryParsingException("composite field:" +
//              //  fieldName + " can't be directly referenced");
//        }
        writer.print(columnName);
    }

    public void visitParam(OQLParam oqlExpr) throws QueryParsingException
    {
        String paramName = oqlExpr.getParamName();
        context.addExpressionParam(paramName);
        //special logic for oracle
        if (DBMSProviderFactory.getProviderId() == DBMSProviderFactory.ORACLE)
        {
            if (context.getExpressionParamType() == ExtendedSQLTypes.TIMESTAMP2)
            {
                writer.print(SQLConstants.SPACE + OracleProvider.TO_TIMESTAMP +
                        SQLConstants.QUESTION + OracleProvider.TO_TIMESTAMP_FORMAT +
                        SQLConstants.SPACE);
            }
            else if (context.getExpressionParamType() == Types.TIMESTAMP)
            {
                writer.print(SQLConstants.SPACE + OracleProvider.TO_DATE +
                        SQLConstants.QUESTION + OracleProvider.TO_DATE_FORMAT +
                        SQLConstants.SPACE);
            }
            else
            {
                writer.print(SQLConstants.SPACE + SQLConstants.QUESTION +
                        SQLConstants.SPACE);
            }
        }
        else
        {
            writer.print(SQLConstants.SPACE + SQLConstants.QUESTION +
                    SQLConstants.SPACE);
        }

    }

    public void visitFieldPart(OQLFieldPart oqlExpr) throws QueryParsingException
    {
        fieldPartIndex++;
        if (fieldPartIndex > 1) //not first field part
        {
            checkCast();
//            if (compositeFieldDesc == null)
//            {
//                preTableInstance = tableInstance;
//                tableInstance = null;
//            }
            preFieldName = fieldName;
            fieldName = null;
        }
    }

    public void visitTypeName(OQLTypeName oqlExpr) throws QueryParsingException
    {
        if (visitingName)
        {
            tableInstance = context.resolveByTypeName(oqlExpr.toString());
            currentClass = context.getResolvedClass();
            currentCustRDB = null;
            visitingName = false;
        }
        else
        {
            try {
				castFieldClass = BEUtil.getBEImplementClass(oqlExpr.getTypeName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            castCustRDB = oqlExpr.getCustRDBName();
        }
    }

    public void visitUnaryExpression(OQLUnaryExpression oqlExpr) throws QueryParsingException
    {
        if (oqlExpr.getOperator() != null)
        {
            writer.print(oqlExpr.getOperator());
        }
    }

    private void resolveSystemField() throws QueryParsingException
    {
        checkPrimitiveField();
        checkCollectionTable();
        checkCompositeField();
        checkEncodedField();

        //first field part
        if (currentClass == null && tableInstance == null)
        {
            if (tables.size() == 1)
            {
                tableInstance = (TableInstance)tables.get(0);
                currentClass = context.getClassByAlias(tableInstance.getAlias());
                currentCustRDB = null;
            }
            else
            {
                throw new QueryParsingException("Ambiguous system field reference:"
                    + fieldName);
            }
        }
        buildJoin();
    }

    private void resolveCategoryField(String objectType, String categoryName)
        throws QueryParsingException
    {
        checkPrimitiveField();
        checkCollectionTable();
        checkCompositeField();
        checkEncodedField();
        checkCollectionFieldTableInstance();

        //first field part
        if (currentClass == null)
        {
            if (tables.size() == 1)
            {
                tableInstance = (TableInstance)tables.get(0);
                currentClass = context.getClassByAlias(tableInstance.getAlias());
                currentCustRDB = null;
            }
            else
            {
                throw new QueryParsingException("Ambiguous category field reference:"
                    + objectType + " in " + categoryName);
            }
        }
        buildJoin();

//        catDesc = Mapper.getCategoryDescriptor(categoryName);
    }

    private void resolveCommonField() throws QueryParsingException
    {
        checkPrimitiveField();
        checkEncodedField();
        checkCollectionTable();
        checkCollectionFieldTableInstance();

        if (currentClass == null) //must be first field part
        {
            tableInstance = context.resolveByFieldName(fieldName);
            currentClass = context.getResolvedClass();
            currentCustRDB = null;
            //fieldDesc = context.getResolvedFieldDescriptor();
        }

//        if (fieldDesc == null)
//        {
//            clazzDesc = Mapper.getClassDescriptor(currentClass);
//            fieldDesc = clazzDesc.getFieldDescriptor(fieldName);
//            if (fieldDesc == null)
//            {
//                throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_190,
//                        new Object[]{fieldName,currentClass.getName()});
//            }
//        }

        buildJoin();
    }

    private void resolveExtendField() throws QueryParsingException
    {
        checkPrimitiveField();
        checkCollectionTable();
        checkCompositeField();
        checkEncodedField();
        checkCollectionFieldTableInstance();

        //first field part
        if (currentClass == null)
        {
            if (tables.size() == 1)
            {
                tableInstance = (TableInstance)tables.get(0);
                currentClass = context.getClassByAlias(tableInstance.getAlias());
                currentCustRDB = null;
            }
            else
            {
                throw new QueryParsingException("Ambiguous extend field reference:"
                    + fieldName);
            }
        }

        if (castFieldClass == null)
        {
            throw new QueryParsingException(
                "Type of extend field:" + fieldName +
                " is not specified");
        }
        buildJoin();
    }

    private void buildJoin() throws QueryParsingException
    {
    	/**
        if (compositeFieldDesc == null)
        {
            if (currentClass != null) //if table instance is not a relation table
            {
                clazzDesc = Mapper.getClassDescriptor(currentClass);
            }

            if (fieldPartIndex > 1)
            {
                String tblName = null;
                if (currentCustRDB == null)
                {
                    tblName = Mapper.getTableName(clazzDesc);
                }
                else
                {
                    tblName = (String)clazzDesc.getCustomerRDB().get(currentCustRDB);
                    if (tblName == null)
                    {
                        throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_198,new Object[]{currentCustRDB,clazzDesc.getEntityName()});
                   //     throw new QueryParsingException("CustomerRDB:" +
                     //       currentCustRDB + " not found in " + clazzDesc.getEntityName());
                    }
                }
                tableInstance = preTableInstance.getFieldTableInstance(preFieldName, tblName);
//                tableInstance.getContext().addExpressionJoinClause(columnName +
//                    SQLConstants.SPACE +
//                    SQLConstants.EQUAL +
//                    SQLConstants.SPACE +
//                    tableInstance.getAlias() +
//                    SQLConstants.DOT +
//                    ResourceNames.COLUMN_OID);
            }
        }
        */
    }

    private void checkPrimitiveField()
        throws QueryParsingException
    {
        if (primitiveField)
        {
            throw new QueryParsingException("Can't reference field:" +
                fieldName + " of a primitive field");
        }
    }

    private void checkCast()
        throws QueryParsingException
    {
        if (castFieldClass != null)
        {
            //for extend field, current class can be null
            if (currentClass != null)
            {
                if (currentClass != castFieldClass)
                {
                    if (!currentClass.isPrimitive())
                    {
                        if (!currentClass.isAssignableFrom(castFieldClass))
                        {
                            throw new QueryParsingException(
                                "Can't cast " + currentClass.getName() +
                                " to " + castFieldClass.getName());
                        }
                    }
                    else
                    {
                        throw new QueryParsingException("cannot cast {0} to {1}",
                        		new Object[]{currentClass.getName(),castFieldClass.getName()});
                    }
                }
            }
            currentClass = castFieldClass;
            currentCustRDB = castCustRDB;
            castFieldClass = null;
            castCustRDB = null;
        }
    }

    private void checkCollectionTable()
        throws QueryParsingException
    {
        if (collectionTableInstance != null)
        {
            throw new QueryParsingException("Can't reference fields " +
                "of a collection or category field");
        }
    }

    private void checkCompositeField()
        throws QueryParsingException
    {
//        if (compositeFieldDesc != null)
//        {
//            throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_193,new Object[]{fieldName});
//          //  throw new QueryParsingException("Can't reference field:" +
//            //    fieldName + " of composite field");
//        }
    }

    private void checkEncodedField()
        throws QueryParsingException
    {
//        if (encodedFieldDesc != null)
//        {
//            throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_194,new Object[]{fieldName});
//       //     throw new QueryParsingException("Can't reference field:" +
//         //       fieldName + " of encoded field");
//        }
    }

    private void checkCollectionFieldTableInstance()
        throws QueryParsingException
    {
//        if (tableInstance != null &&
//            tableInstance.getCollectionFieldDescriptor() != null)
//        {
//            throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_205);
//        }
    }

    private void encodedFieldDetected(IFieldDescriptor fieldDesc, String fieldName)
        throws QueryParsingException
    {
        String sql = null;

        /**
        //descriptor
        encodedFieldDesc = (EncodedFieldDescriptor)fieldDesc;

        //field index
        int encodedFieldIndex = -1;
        for (int i = 0, n = encodedFieldDesc.getFieldNames().length; i < n; i++)
        {
            if (encodedFieldDesc.getFieldNames()[i].equals(fieldName))
            {
                encodedFieldIndex = i;
            }
        }
        IEncoder encoder = encodedFieldDesc.getEncoder();
        if (op != null && canReferenceEncodedField)
        {
            boolean exceptionThrown = true;
            try
            {
                sql = encodedFieldDesc.getEncoder().buildQueryString(
                    encodedFieldIndex, op, value);

                context.setEncodedFieldDetected(true);
                exceptionThrown = false;
            }
            finally
            {
                if (exceptionThrown)
                {
                    if (encoder instanceof ISearchableEncoder)
                    {
                        sql = ((ISearchableEncoder)encoder).buildQueryString(
                            encodedFieldIndex);
                        columnName = MessageFormat.format(sql,
                            (Object[])getColumnNamesWithAlias(encodedFieldDesc.getColumnNames(),
                                tableInstance.getAlias()));
                        return;
                    }
                }
            }
        }
        else if (encoder instanceof ISearchableEncoder)
        {
            sql = ((ISearchableEncoder)encoder).buildQueryString(
                encodedFieldIndex);
        }
        else
        {
            throw new QueryParsingException(ExceptionConstants.EBOS_ORMAPPER_215,new Object[]{fieldName});
          //  throw new QueryParsingException("encoded field:" + fieldName +
            //    " can only be referenced as a simple name and in where clause or having clause");
        }
        columnName = MessageFormat.format(sql,
            (Object[])getColumnNamesWithAlias(encodedFieldDesc.getColumnNames(),
                tableInstance.getAlias()));
         */
    }

    private void setCategoryInfoToContext(String startWith, String join, String connectBy)
    throws QueryParsingException
    {
        context.setJoin(join);
        context.setConnectBy(connectBy);
        context.addStartWith(expr, startWith);
    }

    private static String getExtFieldColumn(Class fieldClass)
    {
        String name = (String)extFieldColumns.get(fieldClass);
        if (name == null)
        {
            //object reference
            return "LONGVALUE";
        }
        return name;
    }

    private static FieldType getExtFieldType(Class fieldClass)
    {
        FieldType fieldType = (FieldType)extFieldTypes.get(fieldClass);
        if (fieldType == null)
        {
            return FieldType.OBJ_REF;
        }
        return fieldType;
    }

    private static String[] getColumnNamesWithAlias(String columnNames[], String alias)
    {
        String[] cNames = new String[columnNames.length + 1];
        for (int i = 0, n = columnNames.length; i < n; i++)
        {
            cNames[i] = alias + "." + columnNames[i];
        }
        cNames[columnNames.length] = alias;
        return cNames;
    }

    private static void bindCategoryObject(List fieldDescList, Object value,
        QueryParsingContext parsingContext, boolean isStartWith)
        throws QueryParsingException
    {
        JavaToSqlCommand cmd = new JavaToSqlCommand();
        for (int i = 0, n = fieldDescList.size(); i < n; i++)
        {
            IFieldDescriptor fieldDesc = (IFieldDescriptor)
                fieldDescList.get(i);
//            Object fieldValue = Mapper.getFieldValue(value, fieldDesc);
//            Mapper.setValue(fieldValue, fieldDesc, cmd, null);
        }
        Object[] sqlValue = cmd.toArray();
        if (isStartWith)
        {
            parsingContext.addStartWithValue(sqlValue);
        }
        else
        {
            for (int i = 0, n = sqlValue.length; i < n; i++)
            {
                String paramName = "_inp" + parsingContext.getInternalParamGenerator().getNewIndex();
                parsingContext.addExpressionParam(paramName);
                parsingContext.bindParam(paramName,
                        DataParser.valueToBoundParam(sqlValue[i], 
                        		ExtendedSQLTypes.UNKNOWN, parsingContext.getQueryDebugString()));
            }
        }
    }

}
