package org.shaolin.bmdp.persistence.query.condition;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldValueType;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.persistence.query.generator.QueryParser;
import org.shaolin.bmdp.persistence.query.generator.QueryParser.ParsedFilter;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryEvaluationContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryParsingContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryUtil;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.javacc.sql.exception.QueryParsingException;
import org.shaolin.javacc.statement.CompilationUnit;

public class SimpleSearchConditionFilter implements ISearchConditionFilter {
	
	private CompilationUnit toFieldExpression;
	
	private String toFieldString;

	private List<SearchConditionField> searchConditionFields 
		= new ArrayList<SearchConditionField>();

	public SimpleSearchConditionFilter(
			ConditionFieldMappingType conditionFieldMapping,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		try {
			String toFieldPath = conditionFieldMapping.getToDataFieldPath();

			toFieldExpression = SearchQueryUtil.getToFieldExpression(
					toFieldPath, parsingContext);
			toFieldString = SearchQueryUtil
					.getOQLExpressionString(toFieldExpression);

			searchConditionFields.clear();
			List<ConditionFieldValueType> conditionFieldValues = conditionFieldMapping
					.getConditionValues();
			for (ConditionFieldValueType condition : conditionFieldValues) {
				SearchConditionField conditionField = new SearchConditionField(
						toFieldString, toFieldExpression, condition,
						conditionFieldMapping.isUseParamBinding(),
						conditionFieldMapping.isUseParamBinding(),
						parsingContext);
				searchConditionFields.add(conditionField);
			}
		} catch (Throwable t) {
			if (t instanceof InvalidSearchQueryException) {
				throw (InvalidSearchQueryException) t;
			} else {
				throw new InvalidSearchQueryException("fail to construct search condition", t);
			}
		}
	}

	public QueryExpressionNodeList getSearchConditionExpression(
			SearchQueryEvaluationContext evaluationContext) {
		QueryExpressionNodeList totalFilter = null;

		for (SearchConditionField conditionField : searchConditionFields) {
			QueryExpressionNodeList filter = conditionField
					.getSearchConditionExpression(evaluationContext);
			totalFilter = SearchQueryUtil.appendExpressionNodeList(totalFilter,
					filter, LogicalOperator.OR);
		}
		return totalFilter;
	}

	public QueryExpressionNodeList getStaticFilterExpression() {
		QueryExpressionNodeList totalFilter = null;
		for (SearchConditionField conditionField : searchConditionFields) {
			QueryExpressionNodeList filter = conditionField
					.getStaticFilterExpression();
			totalFilter = SearchQueryUtil.appendExpressionNodeList(totalFilter,
					filter, LogicalOperator.OR);
		}
		return totalFilter;
	}

	public List<SearchConditionField> getSearchConditionField() {
		return this.searchConditionFields;
	}
	
	/**
	 * @param context
	 * @param resultDataList
	 * @return
	 */
	public boolean validateSearchQueryResult(
			SearchQueryEvaluationContext context) {
		boolean isValidate;

		if (searchConditionFields.size() == 0) {
			isValidate = true;
		} else {
			isValidate = false;
			for (SearchConditionField conditionField : searchConditionFields) {
				if (conditionField.validateResultData(context)) {
					isValidate = true;
					break;
				}
			}
		}

		return isValidate;
	}
	
	public String getConditionExpressionCode(QueryParser parser, 
			SearchQueryEvaluationContext context, boolean isComposited) 
			throws QueryParsingException {
		StringBuffer sb = new StringBuffer();
		for (SearchConditionField condition: searchConditionFields) {
			ParsedFilter filter = parser.parseExpressionNodeList(condition.getSearchConditionExpression(context), false);
			if (isComposited) {
				sb.append(filter.toString());
				sb.append(" AND ");
			} else {
				sb.append("        if (");
				sb.append(condition.getConditionExpression());
				sb.append(") {\n");
				if (condition.getOperator() == Operator.BETWEEN) {
					sb.append("            List<?> _btlist = ");
					sb.append(condition.getJavaValueExpression());
					sb.append(";\n");
					sb.append("            SQLConditionUtil.handleBetweenCondition(_btlist, parameters);\n");
				} else if (condition.getOperator() == Operator.IN) {
					sb.append("            List<?> _inlist = ");
					sb.append(condition.getJavaValueExpression());
					sb.append(";\n");
					sb.append("            SQLConditionUtil.handleINCondition(sql, \"");
					sb.append(filter.toString());
					sb.append("\", _inlist, parameters);\n");
				} else {
					sb.append("            parameters.add(");
					sb.append(condition.getJavaValueExpression());
					sb.append(");\n");
				}
				sb.append("        } else { \n");
				sb.append("            SQLConditionUtil.cutCondition(sql, \"");
				sb.append(filter.toString());
				sb.append("\");\n");
				sb.append("        }\n");
			}
		}
		
		if (isComposited) {
			cutString(sb, " AND ");
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
				sb.append("            parameters.add(");
				sb.append(condition.getJavaValueExpression());
				sb.append(");\n");
			}
		} 
		
		return sb.toString();
	}

	protected void cutString(StringBuffer sb, String delim) {
		int sbLength = sb.length();
		if (sbLength > 0) {
			sb.delete(sbLength - delim.length(), sbLength);
		}
	}
}
