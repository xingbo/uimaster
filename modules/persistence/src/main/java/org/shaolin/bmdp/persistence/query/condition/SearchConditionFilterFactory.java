package org.shaolin.bmdp.persistence.query.condition;

import org.shaolin.bmdp.datamodel.rdbdiagram.CompositeConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FilterMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SubQueryMappingType;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryParsingContext;

public final class SearchConditionFilterFactory {
	
	public static ISearchConditionFilter createSearchConditionFilter(
			FilterMappingType filterMappingType,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		if (filterMappingType instanceof CompositeConditionMappingType) {
			return new CompositeSearchConditionFilter(
					(CompositeConditionMappingType) filterMappingType,
					parsingContext);
		} else if (filterMappingType instanceof SubQueryMappingType) {
			return new SubQueryConditionFilter(
					(SubQueryMappingType) filterMappingType, parsingContext);
		} else if (filterMappingType instanceof ConditionFieldMappingType) {
			return new SimpleSearchConditionFilter(
					(ConditionFieldMappingType) filterMappingType,
					parsingContext);
		} else {
			throw new InvalidSearchQueryException("invalid filterMappingType:{0}",
					new Object[] { filterMappingType.getClass() });
		}
	}

}
