package org.shaolin.bmdp.designtime.orm.query;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.CompositeConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FilterMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LinkConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LinkFieldType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LogicOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.OrderingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchQueryType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SubQueryMappingType;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.JoinTableInstance;
import org.shaolin.bmdp.persistence.query.condition.ISearchConditionFilter;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryParsingContext;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.sql.SQLConstants;

public class SearchQeuryContext {
	
	private SearchQueryParsingContext totalParsingContext = new SearchQueryParsingContext();
	
	private String returnType;
	
	private String inputParams;
	
	private String conditions;
	
	private SingleTableInfo tableInfo;
	
	private String firstCriteria;
	private String joinCriteria = ""; // optional in single table mode.
	
	private String orderBy = "";
	
	/**
	 * whether single table or multiple tables query.
	 */
	private boolean isSingleQuery = true;
	
	public class SingleTableInfo {
		public String tableAlias;
		public Class<?> pElementType;
	}
	
	public SearchQeuryContext(SearchQueryType definition) {
		this.returnType = VariableUtil.getVariableClassName(definition.getSearchResult());
		
		VariableType searchResult = definition.getSearchResult();
		totalParsingContext.setVariableClass(searchResult.getName(), 
				VariableUtil.getVariableClass(searchResult));
		totalParsingContext.setUseParamBinding(true);
		
		StringBuffer sb = new StringBuffer();
		List<SearchQueryType.FromData> fromData = definition.getFromDatas();
		if (fromData == null || fromData.size() == 0) {
			throw new IllegalStateException("The FROM table must be defined for table query.");
		}
		if (fromData.size() == 1) {
			this.isSingleQuery = true;
		} else {
			this.isSingleQuery = false;
		}
		SingleTableInfo info = new SingleTableInfo();
		info.tableAlias = fromData.get(0).getName();
		info.pElementType = VariableUtil.getVariableClass(fromData.get(0));
		this.tableInfo = info;
		
		StringBuffer headCriteria = new StringBuffer();
		String criteriaName = info.tableAlias + "Criteria";
		headCriteria.append("            Criteria ").append(criteriaName).append(" = this._createCriteria(session, ");
		headCriteria.append(info.pElementType.getName()).append(".class, \"").append(info.tableAlias).append("\");\n");
		this.firstCriteria = headCriteria.toString();
		
		for (SearchQueryType.FromData fromDatum : fromData) {
			String name = fromDatum.getName();
			String custRDBName = fromDatum.getCustomerRDBName();
			TargetEntityType type = fromDatum.getType();
			String fromString = null;
			if (custRDBName != null && custRDBName.trim().length() != 0) {
				fromString = type.getEntityName() + "[" + custRDBName + "] as " + name;
			} else {
				fromString = type.getEntityName() + " as " + name;
			}

			Class fromDatumClass = VariableUtil.getVariableClass(fromDatum);
			if (fromDatum.getCategory() == VariableCategoryType.JOIN_TABLE) {
				totalParsingContext.setVariableClass(name, JoinTableInstance.class);
			} else {
				totalParsingContext.setVariableClass(name, fromDatumClass);
			}
			
		}
		totalParsingContext.initSetVariable();
		
		if (!this.isSingleQuery) {
			StringBuffer joinInfo = new StringBuffer();
			LinkConditionMappingType linkMapping = definition.getLinkMapping();
			if (linkMapping == null || linkMapping.getLinks().size() == 0) {
				throw new IllegalStateException("The link mapping must be defined for multiple table query.");
			}
			for (LinkFieldType link: linkMapping.getLinks()) {
				String leftTable = link.getLeft().substring(0, link.getLeft().lastIndexOf('.'));
				String leftCriteria = leftTable + "Criteria";
				String rightTable = link.getRight().substring(0, link.getRight().lastIndexOf('.'));
				String rightCriteria = rightTable + "Criteria";
				joinInfo.append("            Criteria ").append(rightCriteria).append(" = this._createJoinAlias(");
				joinInfo.append(leftCriteria).append(", \"").append(link.getRefField()).append("\", \"").append(rightTable).append("\");\n");
				joinInfo.append("            ");
				joinInfo.append(rightCriteria).append(".add(Restrictions.eqProperty(\"");
				joinInfo.append(link.getLeft()).append("\", \"").append(link.getRight()).append("\"));\n");
			}
			this.joinCriteria = joinInfo.toString();
		}
		
		SearchConditionMappingType searchConditionMapping = definition.getSearchConditionMapping();
		SearchQueryParsingContext parsingContext = new SearchQueryParsingContext(
				totalParsingContext.getVariableTypes());
		parsingContext.setUseParamBinding(totalParsingContext
				.getUseParamBinding());
		
		List<VariableType> searchConditionVariables = 
				searchConditionMapping.getSearchConditionDatas();
		for (int i = 0; i < searchConditionVariables.size(); i++) {
			VariableType var = searchConditionVariables
					.get(i);
			String searchConditionVarName = var.getName();
			Class clazzType = VariableUtil.getVariableClass(var);
			Object defaultValue = VariableUtil.createVariableObject(var);
			parsingContext.setVariableClass(searchConditionVarName,clazzType);

			sb.append(clazzType.getName());
			sb.append(" ").append(searchConditionVarName);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		this.inputParams = sb.toString();
		
		StringBuffer condition = new StringBuffer();
		List<ConditionFieldMappingType> searchConditionFieldMappings = searchConditionMapping
				.getFieldMappings();
		for (ConditionFieldMappingType mapping : searchConditionFieldMappings) {
			try {
				parseConditionFilter(mapping, parsingContext, false, condition);
			} catch (InvalidSearchQueryException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();
			}
		}
		this.conditions = condition.toString();
		
		StringBuffer orderbyStr = new StringBuffer();
		orderbyStr.append("            if (orders == null) {\n");
		if (definition.getOrderBies() != null && !definition.getOrderBies().isEmpty()) {
			List<OrderingType> orderingTypes = definition.getOrderBies();
			for (OrderingType ordering : orderingTypes) {
				String orderByFieldName = ordering.getField();
				int index = orderByFieldName.indexOf('.');
				String alias = orderByFieldName.substring(0, index);
				String col = orderByFieldName.substring(index + 1).toUpperCase();
				orderbyStr.append("                // add default orders.\n");
				orderbyStr.append("                ").append(alias);
				if (ordering.isIsAscending()) {
					orderbyStr.append("Criteria.addOrder(Order.asc(\"");
				} else {
					orderbyStr.append("Criteria.addOrder(Order.desc(\"");
				}
				orderbyStr.append(orderByFieldName).append("\"));\n");
			}
		}
		orderbyStr.append("            } else {\n");
		orderbyStr.append("                this._addOrders(").append(criteriaName).append(", orders);\n");
		orderbyStr.append("            }\n");
		orderBy = orderbyStr.toString();
		
	}
	
	private void parseConditionFilter(ConditionFieldMappingType mapping, 
			SearchQueryParsingContext parsingContext, boolean isComposited, 
			StringBuffer sb) 
			throws ParsingException, InvalidSearchQueryException {
		if (mapping instanceof CompositeConditionMappingType) {
			CompositeConditionMappingType ccMaping = (CompositeConditionMappingType)mapping;
			LogicalOperator logicalOperator = LogicalOperator.fromString(ccMaping.getLogicalOperator());
			
			ArrayList<String> conditions = new ArrayList<String>();
			ArrayList<String> parameters = new ArrayList<String>();
			List<ISearchConditionFilter> nestedFilters = new ArrayList<ISearchConditionFilter>();
			List<FilterMappingType> mappings = ccMaping.getFieldMappings();
			for (FilterMappingType m : mappings) {
				parseConditionFilter((ConditionFieldMappingType)m, parsingContext, true, sb);
				
				int coditionIndex = sb.indexOf("[JAVACODITION]");
				int paramIndex = sb.indexOf("[PARAM]");
				String param = sb.substring(paramIndex + "[PARAM]".length(), sb.length());
				String condition = sb.substring(coditionIndex + "[JAVACODITION]".length(), paramIndex);
				sb.delete(coditionIndex, sb.length());
				
				conditions.add(condition);
				parameters.add(param);
			}
			
			sb.append("            if (");
			for (String c : conditions) {
				sb.append(c);
				sb.append(SQLConstants.SPACE);
				sb.append(logicalOperator.getJavaSymbol());
				sb.append(SQLConstants.SPACE);
			}
			cutString(sb, SQLConstants.SPACE + logicalOperator.getJavaSymbol() + SQLConstants.SPACE);
			sb.append(") {\n");
			for (String param : parameters) {
				sb.append(param);
			}
			sb.append("            }\n");
			
		} else if (mapping instanceof SubQueryMappingType) {
			SubQueryMappingType ccMaping = (SubQueryMappingType)mapping;
			LogicOperatorType logicOperator = (LogicOperatorType) ccMaping.getOperator();
			Operator opt = Operator.fromString(logicOperator.getType());
			if (opt != Operator.IN && opt != Operator.EXISTS) {
				throw new IllegalStateException("Sub query only allows IN or EXISTS operators");
			}
			
			// TODO:
			
		} else {
			ConditionFieldMappingType ccMaping = (ConditionFieldMappingType)mapping;
			
			List<SearchConditionField> searchConditionFields = new ArrayList<SearchConditionField>();
			
			String toFieldPath = ccMaping.getToDataFieldPath();
			List<ConditionFieldValueType> conditionFieldValues = ccMaping.getConditionValues();
			for (ConditionFieldValueType condition : conditionFieldValues) {
				SearchConditionField conditionField = new SearchConditionField(
						toFieldPath, condition,
						ccMaping.isUseParamBinding(),
						ccMaping.isUseParamBinding(),
						parsingContext);
				searchConditionFields.add(conditionField);
			}
			
			if (isComposited) {
				//cutString(sb, " AND ");
				//apply java condition temporary.
				sb.append("[JAVACODITION]");
				for (SearchConditionField condition: searchConditionFields) {
					sb.append(condition.getConditionExpression());
					sb.append(" && ");
				}
				cutString(sb, " && ");
				//apply parameter temporary.
				sb.append("[PARAM]");
				for (SearchConditionField condition: searchConditionFields) {
					String fieldName = condition.getToFieldExpression();
					String tableName = fieldName.substring(0, fieldName.lastIndexOf('.'));
					
					sb.append("                ").append(tableName).append("Criteria.add(createCriterion(");
					sb.append(getStringOperator(condition.getOperator())).append(", \"");
					sb.append(fieldName).append("\", ");
					sb.append(condition.getJavaValueExpression());
					sb.append("));\n");
				}
			} else {
				for (SearchConditionField condition: searchConditionFields) {
					String fieldName = condition.getToFieldExpression();
					String tableName = fieldName.substring(0, fieldName.lastIndexOf('.'));
					
					sb.append("            if (");
					sb.append(condition.getConditionExpression());
					sb.append(") {\n");
					sb.append("                ").append(tableName).append("Criteria.add(createCriterion(");
					sb.append(getStringOperator(condition.getOperator())).append(", \"");
					sb.append(condition.getToFieldExpression()).append("\", ");
					sb.append(condition.getJavaValueExpression());
					sb.append("));\n");
					sb.append("            }\n");
				}
			}
		} 
	}
	
	protected String getStringOperator(Operator optType) {
		if (Operator.EQUALS == optType) {
			return "Operator.EQUALS";
		} else if (Operator.EQUALS_IGNORE_CASE == optType) {
			return "Operator.EQUALS_IGNORE_CASE";
		} else if (Operator.IS_NULL == optType) {
			return "Operator.IS_NULL";
		} else if (Operator.GREATER_THAN == optType) {
			return "Operator.GREATER_THAN";
		} else if (Operator.GREATER_THAN_OR_EQUALS == optType) {
			return "Operator.GREATER_THAN_OR_EQUALS";
		} else if (Operator.LESS_THAN == optType) {
			return "Operator.LESS_THAN";
		} else if (Operator.LESS_THAN_OR_EQUALS == optType) {
			return "Operator.LESS_THAN_OR_EQUALS";
		} else if (Operator.IN == optType) {
			return "Operator.IN";
		} else if (Operator.BETWEEN == optType) {
			return "Operator.BETWEEN";
		} else if (Operator.START_WITH == optType
				|| Operator.START_WITH_LEFT == optType) {
			return "Operator.START_WITH";
		} else if (Operator.START_WITH_IGNORE_CASE == optType) {
			return "Operator.START_WITH_IGNORE_CASE";
		} else if (Operator.START_WITH_RIGHT == optType) {
			return "Operator.START_WITH_RIGHT";
		} else if (Operator.CONTAINS_WORD == optType) {
			return "Operator.CONTAINS_WORD";
		} else if (Operator.CONTAINS_WORD_IGNORE_CASE == optType) {
			return "Operator.CONTAINS_WORD_IGNORE_CASE";
		} else if (Operator.CONTAINS_PARTIAL == optType) {
			return "Operator.CONTAINS_PARTIAL";
		} else if (Operator.CONTAINS_PARTIAL_IGNORE_CASE == optType) {
			return "Operator.CONTAINS_PARTIAL_IGNORE_CASE";
		}
		throw new IllegalStateException("Unsupported SQL OperatorType : "
				+ optType);
	}

	protected void cutString(StringBuffer sb, String delim) {
		int sbLength = sb.length();
		if (sbLength > 0) {
			sb.delete(sbLength - delim.length(), sbLength);
		}
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public String getDefaultInputParams() {
		return inputParams;
	}
	
	public String getConditions() {
		return conditions;
	}

	public SingleTableInfo getTableInfo() {
		return tableInfo;
	}
	
	public boolean isSingleQuery() {
		return isSingleQuery;
	}
	
	public String getFirstCriteriaName() {
		return tableInfo.tableAlias + "Criteria";
	}
	
	public String getFirstCriteriaData() {
		return firstCriteria;
	}
	
	public String getJoinCriteriaData() {
		return joinCriteria;
	}
	
	public String getOrderByData() {
		return orderBy;
	}
}
