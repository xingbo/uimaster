package org.shaolin.bmdp.persistence.query.condition;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.rdbdiagram.CompositeConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FilterMappingType;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.persistence.query.generator.QueryParser;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryEvaluationContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryParsingContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryUtil;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class CompositeSearchConditionFilter implements ISearchConditionFilter {

	private LogicalOperator logicalOperator;

	private List<ISearchConditionFilter> nestedFilters = new ArrayList<ISearchConditionFilter>();

	public CompositeSearchConditionFilter(
			CompositeConditionMappingType compositeMappingType,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		try {
			logicalOperator = LogicalOperator.fromString(compositeMappingType
					.getLogicalOperator());

			List<FilterMappingType> mappings = compositeMappingType
					.getFieldMappings();
			for (FilterMappingType mapping : mappings) {
				FilterMappingType fieldMappingType = mapping;
				ISearchConditionFilter nestedFilter = SearchConditionFilterFactory
						.createSearchConditionFilter(fieldMappingType,
								parsingContext);
				nestedFilters.add(nestedFilter);
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

		for (ISearchConditionFilter nestedConditionFilter : nestedFilters) {
			QueryExpressionNodeList filter = nestedConditionFilter
					.getSearchConditionExpression(evaluationContext);
			if (filter != null) {
				totalFilter = SearchQueryUtil.appendExpressionNodeList(
						totalFilter, filter, logicalOperator);
			}
		}
		return totalFilter;
	}

	public QueryExpressionNodeList getStaticFilterExpression() {
		QueryExpressionNodeList totalFilter = null;
		for (ISearchConditionFilter nestedConditionFilter : nestedFilters) {
			QueryExpressionNodeList filter = nestedConditionFilter
					.getStaticFilterExpression();
			if (filter != null) {
				totalFilter = SearchQueryUtil.appendExpressionNodeList(
						totalFilter, filter, logicalOperator);
			}
		}
		return totalFilter;
	}

	/**
	 * @param context
	 * @param resultDataList
	 * @return
	 */
	public boolean validateSearchQueryResult(
			SearchQueryEvaluationContext context) {
		boolean isValidate;
		if (LogicalOperator.OR.equals(logicalOperator)) {
			isValidate = false;
			for (ISearchConditionFilter nestedConditionFilter : nestedFilters) {
				if (nestedConditionFilter.validateSearchQueryResult(context)) {
					isValidate = true;
					break;
				}
			}
		} else {
			isValidate = true;
			for (ISearchConditionFilter nestedConditionFilter : nestedFilters) {
				if (!nestedConditionFilter.validateSearchQueryResult(context)) {
					isValidate = false;
					break;
				}
			}
		}
		return isValidate;
	}

	public String getConditionExpressionCode(QueryParser parser, 
			SearchQueryEvaluationContext context, boolean isComposited) 
			throws QueryParsingException {
		ArrayList<String> sqls = new ArrayList<String>();
		ArrayList<String> conditions = new ArrayList<String>();
		ArrayList<String> parameters = new ArrayList<String>();
		for (ISearchConditionFilter nestedConditionFilter : nestedFilters) {
			String value = nestedConditionFilter.getConditionExpressionCode(parser, context, true);
			int coditionIndex = value.indexOf("[JAVACODITION]");
			int paramIndex = value.indexOf("[PARAM]");
			String sql = value.substring(0, coditionIndex);
			String condition = value.substring(coditionIndex + "[JAVACODITION]".length(), paramIndex);
			String param = value.substring(paramIndex + "[PARAM]".length());
			sqls.add(sql);
			conditions.add(condition);
			parameters.add(param);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("        if (");
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
		sb.append("        } else { \n");
		sb.append("            SQLConditionUtil.cutCondition(sql, \"");
		sb.append(SQLConstants.OPEN_BRACKET);
		for (String s : sqls) {
			sb.append(s);
			sb.append(SQLConstants.SPACE);
			sb.append(logicalOperator.toString());
			sb.append(SQLConstants.SPACE);
		}
		cutString(sb, SQLConstants.SPACE + logicalOperator.toString() + SQLConstants.SPACE);
		sb.append(SQLConstants.CLOSE_BRACKET);
		sb.append("\");\n");
		sb.append("        }\n");
		return sb.toString();
	}
	
	protected void cutString(StringBuffer sb, String delim) {
		int sbLength = sb.length();
		if (sbLength > 0) {
			sb.delete(sbLength - delim.length(), sbLength);
		}
	}
	
}
