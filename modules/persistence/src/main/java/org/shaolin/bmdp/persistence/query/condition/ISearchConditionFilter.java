package org.shaolin.bmdp.persistence.query.condition;

import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.persistence.query.generator.QueryParser;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryEvaluationContext;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public interface ISearchConditionFilter {
	
	QueryExpressionNodeList getSearchConditionExpression(
			SearchQueryEvaluationContext evaluationContext);

	QueryExpressionNodeList getStaticFilterExpression();

	boolean validateSearchQueryResult(SearchQueryEvaluationContext context);

	String getConditionExpressionCode(QueryParser parser, 
			SearchQueryEvaluationContext context, boolean isComposited) 
					throws QueryParsingException;
	
}
