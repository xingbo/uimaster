package org.shaolin.bmdp.persistence.query.generator;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.UnparseVisitor;
import org.shaolin.javacc.sql.exception.OQLException;
import org.shaolin.javacc.sql.exception.QueryParsingException;
import org.shaolin.javacc.sql.node.OQLExpression;
import org.shaolin.javacc.sql.parser.OQLExprParser;

public class QueryUtil {

	private static final Logger logger = Logger.getLogger(QueryUtil.class);

	public static final int FROM_CURRENT = 1;
	public static final int FROM_HISTORY = 2;

	private QueryUtil() {
	}

	public static OQLExpression parseExpression(String expr)
			throws OQLException {
		try {
			OQLExpression oqlExpr = OQLExprParser.parseExpression(expr);
			if (logger.isDebugEnabled()) {
				StringWriter writer = new StringWriter();
				IOQLNodeVisitor visitor = new UnparseVisitor(writer);
				oqlExpr.visitWith(visitor);
				logger.debug(writer.toString());
			}
			return oqlExpr;
		} catch (Throwable t) {
			throw new OQLException("OQLException :{0} {1}", t, new Object[] {
							expr, t.getMessage() });
		}
	}
	
	public static String buildFrom(List<TableInstance> fromList) {
		return buildMultiClause(fromList, SQLConstants.COMMA
				+ SQLConstants.SPACE);
	}

	public static String buildFilter(List<String> filterList) {
		return buildMultiClause(filterList, SQLConstants.SPACE
				+ SQLConstants.AND + SQLConstants.SPACE);
	}

	public static String buildMultiClause(List<?> clauseList, String sep) {
		return buildMultiClause(clauseList, sep, null);
	}

	public static String buildMultiClause(List<?> clauseList, String sep,
			int[] index) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, n = clauseList.size(); i < n; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(clauseList.get(i));
			if (index != null) {
				sb.append(SQLConstants.SPACE);
				sb.append(SQLConstants.COLUMN_ALIAS);
				sb.append(index[0]++);
			}
		}
		return new String(sb);
	}
	
	public static String buildMultiClause(List<?> clauseList, String sep,
			List excludeClauseList, int[] index) {
		StringBuffer sb = new StringBuffer();
		if (clauseList != null) {
			boolean isFirst = true;
			for (int i = 0, n = clauseList.size(); i < n; i++) {
				Object clause = clauseList.get(i);
				if (!excludeClauseList.contains(clause)) {
					if (isFirst) {
						isFirst = false;
					} else {
						sb.append(sep);
					}
					sb.append(clause);
					if (index != null) {
						sb.append(SQLConstants.SPACE);
						sb.append(SQLConstants.COLUMN_ALIAS);
						sb.append(index[0]++);
					}
				}
			}
		}
		return sb.length() == 0 ? (String) null : new String(sb);
	}
	

    public static String buildExpressionSql(OQLExpression oqlExpr, 
        QueryParsingContext parsingContext, boolean canReferenceEncodedField, 
        String strExpr, Operator op, Object value,
        QueryExpression expr) throws OQLException
    {
        try
        {
            StringWriter leftColumnWriter = new StringWriter();
            IOQLNodeVisitor visitor =
                new QueryParserVisitor(leftColumnWriter,
                    parsingContext, false, 
                    canReferenceEncodedField, op, value, expr);
            oqlExpr.visitWith(visitor);
            return leftColumnWriter.toString();
        }
        catch(QueryParsingException e)
        {
        	throw new OQLException("OQLException :{0} {1}", 
        			e, new Object[]{strExpr,e.getMessage()});
        }
    }
    
    public static Object buildValueSql(QueryExpression expr,
        QueryParsingContext parsingContext)
        throws QueryParsingException, OQLException
    {
        Object value = expr.getValue();
        Operator op = expr.getOperator();

        if (op == Operator.BETWEEN ||
            op == Operator.BETWEEN.reverse() ||
            op == Operator.IS_NULL ||
            op == Operator.IS_NULL.reverse())//between ... and ...
        {
            //for Operator.IS_NULL: must be null
            //for Operator.BETWEEN: will be processed in ExpressionHandlers
            return null;
        }
        
        if (expr.isExpressionValue())
        {
            return parseValueExpression(expr, parsingContext,
                value, parsingContext.getExpressionDatabaseType());
        }
        else if (value instanceof SearchQuery)
        {
            SearchQuery subQuery = (SearchQuery)value;
            subQuery.setParser(new QueryParser(subQuery, parsingContext));
            subQuery.parse();
            
            return SQLConstants.OPEN_BRACKET + subQuery.getSql() + SQLConstants.CLOSE_BRACKET;
        }
        else
        {
            if (parsingContext.getExpressionValueAsParam())
            {
                return DataParser.parseParamValue(value,
                        parsingContext, isOperatorHasFunction(op));
            }
            int databaseType = parsingContext.getExpressionDatabaseType();
            return DataParser.parseValue(value, parsingContext.getExpressionPFName(),
                    databaseType, parsingContext.getQueryDebugString());
        }
    }


	public static boolean isOperatorHasFunction(Operator op) {
		return op == Operator.EQUALS_IGNORE_CASE
				|| op == Operator.EQUALS_IGNORE_CASE.reverse()
				|| op == Operator.CONTAINS_PARTIAL_IGNORE_CASE
				|| op == Operator.CONTAINS_PARTIAL_IGNORE_CASE.reverse()
				|| op == Operator.CONTAINS_WORD_IGNORE_CASE
				|| op == Operator.CONTAINS_WORD_IGNORE_CASE.reverse();
	}

	public static boolean isStringOperator(Operator op) {
		return op == Operator.EQUALS_IGNORE_CASE
				|| op == Operator.EQUALS_IGNORE_CASE.reverse()
				|| op == Operator.START_WITH
				|| op == Operator.START_WITH.reverse()
				|| op == Operator.CONTAINS_PARTIAL
				|| op == Operator.CONTAINS_PARTIAL.reverse()
				|| op == Operator.CONTAINS_PARTIAL_IGNORE_CASE
				|| op == Operator.CONTAINS_PARTIAL_IGNORE_CASE.reverse()
				|| op == Operator.CONTAINS_WORD
				|| op == Operator.CONTAINS_WORD.reverse()
				|| op == Operator.CONTAINS_WORD_IGNORE_CASE
				|| op == Operator.CONTAINS_WORD_IGNORE_CASE.reverse();
	}

	private static String parseValueExpression(QueryExpression expr,
			QueryParsingContext parsingContext, Object value,
			int exprDatabaseType) throws OQLException
	    {
	        if (value instanceof String)
	        {
	            String valueStr = (String)value;
	            OQLExpression oqlExpr = parseExpression(valueStr);
	            if (oqlExpr.isParam())
	            {
	                parsingContext.setExpressionParamType(exprDatabaseType);
	            }
	            return buildExpressionSql(oqlExpr,
	                parsingContext, false, valueStr, null, null, expr);
	        }
	        if (value instanceof Collection)
	        {
	            StringBuffer sb = new StringBuffer(SQLConstants.OPEN_BRACKET);
	            boolean first = true;
	            for (Iterator it = ((Collection)value).iterator(); it.hasNext();)
	            {
	                Object item = it.next();
	                
	                if (first)
	                {
	                    first = false;
	                }
	                else
	                {
	                    sb.append(SQLConstants.COMMA);
	                }
	                sb.append(parseValueExpression(expr, parsingContext,
	                    item, exprDatabaseType));
	            }
	            sb.append(SQLConstants.CLOSE_BRACKET);
	            return new String(sb);
	        }
	        throw new OQLException("OQLException :{0} {1}",
	        		new Object[]{expr.getAttribute(),"This expression's value is an expression and should be string type or a collection of string"});
	    }
	
	public static String parseUpdate(QueryParsingContext parsingContext,
			String filterString) {
		List joinClauses = parsingContext.getExpressionJoinClauses();
		if (joinClauses == null) {
			return filterString;
		}

		// build join
		String joinString = buildFilter(joinClauses);

		// build from
		List tableInstances = parsingContext.getTableInstances();
		tableInstances.remove(0); // remove update table instance
		String fromString = QueryUtil.buildFrom(tableInstances);

		StringBuffer sb = new StringBuffer();
		sb.append("EXISTS(SELECT 1 FROM ");
		sb.append(fromString);
		sb.append(" WHERE ");
		sb.append(joinString);
		sb.append(" AND (");
		sb.append(filterString);
		sb.append("))");
		return new String(sb);
	}

	public static boolean isFromCurrent(int setting) {
		return (setting & FROM_CURRENT) != 0;
	}

	public static boolean isFromHistory(int setting) {
		return (setting & FROM_HISTORY) != 0;
	}
}
