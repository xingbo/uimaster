package org.shaolin.bmdp.persistence.query.condition;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FilterMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LogicOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SubQueryMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SubQueryMappingType.FromData;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.JoinTableInstance;
import org.shaolin.bmdp.persistence.query.generator.QueryExpression;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.persistence.query.generator.QueryParser;
import org.shaolin.bmdp.persistence.query.generator.SearchQuery;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryEvaluationContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryParsingContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryUtil;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.sql.exception.QueryParsingException;
import org.shaolin.javacc.statement.CompilationUnit;

public class SubQueryConditionFilter implements ISearchConditionFilter {
	
	private boolean isDistinct;
	private Operator searchOperator;
	private boolean isReverse;
	private String toDataFieldPath;
	private String selection;
	private CompilationUnit conditionCompilationUnit;
	private List<String> queryFromList = new ArrayList<String>();
	private QueryExpressionNodeList joinConditionFilterExpression;
	private List<ISearchConditionFilter> searchConditionFilters = new ArrayList<ISearchConditionFilter>();
	private SearchQuery query = null;
	
	public SubQueryConditionFilter(SubQueryMappingType subQueryMappingType,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		isDistinct = subQueryMappingType.isIsDistinct();
		LogicOperatorType logicOperator = (LogicOperatorType) subQueryMappingType
				.getOperator();
		searchOperator = Operator.fromString(logicOperator.getType());
		isReverse = logicOperator.isIsReverse();

		if (searchOperator != Operator.IN && searchOperator != Operator.EXISTS) {
			throw new InvalidSearchQueryException("Unsupported sub query operator :{0}",
					new Object[] { searchOperator });
		}

		List<SubQueryMappingType.FromData> fromDatas = subQueryMappingType
				.getFromDatas();
		for (SubQueryMappingType.FromData fromDatum : fromDatas) {
			String name = fromDatum.getName();
			String custRDBName = fromDatum.getCustomerRDBName();
			TargetEntityType type = fromDatum.getType();
			String fromString = null;
			if (custRDBName != null && custRDBName.trim().length() != 0) {
				fromString = type.getEntityName() + "[" + custRDBName
						+ "] as " + name;
			} else {
				fromString = type.getEntityName() + " as " + name;
			}
			queryFromList.add(fromDatum.getCategory().toString() + " " + fromString);

			if (fromDatum.getCategory() == VariableCategoryType.JOIN_TABLE) {
				parsingContext.setVariableClass(name, JoinTableInstance.class);
			} else {
				Class fromDatumClass = VariableUtil.getVariableClass(fromDatum);
				parsingContext.setVariableClass(name, fromDatumClass);
			}
		}

		try {
			if (searchOperator == Operator.EXISTS) {
				toDataFieldPath = null;
				if (selection == null) {
					selection = "*";
				} else {
					selection = SearchQueryUtil
							.getOQLExpressionString(SearchQueryUtil
									.getToFieldExpression(subQueryMappingType
											.getSelectedDataFieldPath(),
											parsingContext));
				}
			} else {
				// Opeartor.IN
				toDataFieldPath = SearchQueryUtil
						.getOQLExpressionString(SearchQueryUtil
								.getToFieldExpression(subQueryMappingType
										.getToDataFieldPath(), parsingContext));
				selection = subQueryMappingType.getSelectedDataFieldPath();
				if (toDataFieldPath == null || selection == null) {
					throw new InvalidSearchQueryException("please specify toDataFieldPath and selection");
				}
			}
			ExpressionType condition = subQueryMappingType.getCondition();
			if (condition != null) {
				conditionCompilationUnit = SearchQueryUtil.parseExpression(
						condition, parsingContext);
			} else {
				conditionCompilationUnit = null;
			}
		} catch (ParsingException t) {
			throw new InvalidSearchQueryException("fail to construct sub query", t);
		}
		
		/**
		if (subQueryMappingType.getLinkMapping() != null) {
			// create join condition filter
			LinkConditionMappingType linkConditionMapping = subQueryMappingType
					.getLinkMapping();
			List<ConditionFieldMappingType> linkConditionFieldMappings = linkConditionMapping
					.getFieldMapping();
			for (ConditionFieldMappingType mapping : linkConditionFieldMappings) {
				ISearchConditionFilter conditionFilter = SearchConditionFilterFactory
						.createSearchConditionFilter(mapping, parsingContext);
				QueryExpressionNodeList conditionExpression = conditionFilter
						.getStaticFilterExpression();
				joinConditionFilterExpression = SearchQueryUtil
						.appendExpressionNodeList(
								joinConditionFilterExpression,
								conditionExpression, LogicalOperator.AND);
			}
		}
		 */
		
		List<FilterMappingType> searchConditionFieldMappings = subQueryMappingType
				.getFieldMappings();
		for (FilterMappingType mapping : searchConditionFieldMappings) {
			ISearchConditionFilter conditionFilter = SearchConditionFilterFactory
					.createSearchConditionFilter(mapping, parsingContext);
			searchConditionFilters.add(conditionFilter);
		}
		for (FromData fromDatum : fromDatas) {
			String name = fromDatum.getName();
			parsingContext.removeVariableClass(name);
		}
	}

	public QueryExpressionNodeList getSearchConditionExpression(
			SearchQueryEvaluationContext evaluationContext) {
		boolean conditionSatisfied = true;
		if (conditionCompilationUnit != null) {
			/**
			try {
				Object conditionExpressionValue = StatementEvaluator.evaluate(
						conditionCompilationUnit, evaluationContext);
				conditionSatisfied = Boolean.TRUE
						.equals(conditionExpressionValue);
			} catch (EvaluationException ex) {
				throw new PersistenceRuntimeException(
						ExceptionConstants.EBOS_PERSISTENCE_042, ex);
			}
			*/
		}
		if (!conditionSatisfied) {
			return null;
		}
		SearchQuery query0 = null;
		// set select from
		for (int i = 0, n = queryFromList.size(); i < n; i++) {
			String queryFromString = (String) queryFromList.get(i);
			if (query0 == null) {
				query0 = new SearchQuery(queryFromString, false, false);
				query0.setDistinct(isDistinct);
			} else {
				query0.addFrom(queryFromString);
			}
		}

		query0.setSelection(selection);
		QueryExpressionNodeList filter = null;
		if (joinConditionFilterExpression != null) {
			query0.setJoinFilter(new QueryExpressionNodeList(joinConditionFilterExpression));
		}

		for (int i = 0, n = searchConditionFilters.size(); i < n; i++) {
			ISearchConditionFilter conditionFilter = (ISearchConditionFilter) searchConditionFilters
					.get(i);
			QueryExpressionNodeList conditionExpression = conditionFilter
					.getSearchConditionExpression(evaluationContext);
			filter = SearchQueryUtil.appendExpressionNodeList(filter,
					conditionExpression, LogicalOperator.AND);
		}

		query0.setFilter(filter);
		QueryExpression exp = new QueryExpression(toDataFieldPath, searchOperator,
				query0);
		if (isReverse) {
			exp.reverse();
		}
		
		this.query = query0;
		return new QueryExpressionNodeList(exp);
	}

	public QueryExpressionNodeList getStaticFilterExpression() {
		SearchQuery query = null;
		// set select from
		for (int i = 0, n = queryFromList.size(); i < n; i++) {
			String queryFromString = (String) queryFromList.get(i);
			if (query == null) {
				query = new SearchQuery(queryFromString, false, false);
				query.setDistinct(isDistinct);
			} else {
				query.addFrom(queryFromString);
			}
		}

		query.setSelection(selection);
		QueryExpressionNodeList filter = null;
		if (joinConditionFilterExpression != null) {
			query.setJoinFilter(new QueryExpressionNodeList(joinConditionFilterExpression));
		}

		for (int i = 0, n = searchConditionFilters.size(); i < n; i++) {
			ISearchConditionFilter conditionFilter = (ISearchConditionFilter) searchConditionFilters
					.get(i);
			QueryExpressionNodeList conditionExpression = conditionFilter
					.getStaticFilterExpression();
			filter = SearchQueryUtil.appendExpressionNodeList(filter,
					conditionExpression, LogicalOperator.AND);
		}

		query.setFilter(filter);
		QueryExpression exp = new QueryExpression(toDataFieldPath, searchOperator,
				query);
		if (isReverse) {
			exp.reverse();
		}
		return new QueryExpressionNodeList(exp);
	}

	/**
	 * @param context
	 * @param resultDataList
	 * @return
	 */
	public boolean validateSearchQueryResult(
			SearchQueryEvaluationContext context) {
		throw new I18NRuntimeException(ExceptionConstants.EBOS_000);
	}
	
	public String getConditionExpressionCode(QueryParser parser, 
			SearchQueryEvaluationContext context, boolean isComposited) 
			throws QueryParsingException {
		StringBuffer sb = new StringBuffer();
		for (ISearchConditionFilter nestedConditionFilter : searchConditionFilters) {
			sb.append(nestedConditionFilter.getConditionExpressionCode(
					query.getParser(), context, isComposited));
		}
		return sb.toString();
	}
}
