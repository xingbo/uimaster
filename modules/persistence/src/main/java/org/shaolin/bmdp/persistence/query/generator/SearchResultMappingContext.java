package org.shaolin.bmdp.persistence.query.generator;

import java.util.List;

import org.shaolin.bmdp.datamodel.rdbdiagram.FieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SQLFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SimpleFieldValueType;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.PersistenceRuntimeException;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.bmdp.runtime.be.IPersistentEntity;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.statement.ExpressionCompilationUnit;

public class SearchResultMappingContext {

	// the search result fromValueExpression
	private CompilationUnit fromFieldExpression;

	// whether the from value expression is a field
	// if it is, then we can directly use fromFieldOQLString in a sql for
	// constrained object
	// otherwise, use the field expression to validate the constrained object
	private boolean isFieldResult;

	// the from field oql string
	private String fromFieldOQLString;

	// the to field expression
	private CompilationUnit toFieldExpression;

	// the expression to set the field value
	private CompilationUnit setToFieldExpression;

	private boolean useParamBinding = true;

	public SearchResultMappingContext(SQLFieldMappingType SearchResultContextMapping,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		initSearchResultContext(SearchResultContextMapping, parsingContext);
	}

	public void initSearchResultContext(
			SQLFieldMappingType searchResultContextMapping,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		try {
			useParamBinding = parsingContext.getUseParamBinding();
			String toFieldPath = searchResultContextMapping
					.getToDataFieldPath();
			this.toFieldExpression = SearchQueryUtil.getToFieldExpression(
					toFieldPath, parsingContext);
			this.setToFieldExpression = parsingContext
					.buildSetFieldExpression(toFieldExpression);

			FieldValueType fieldValue = searchResultContextMapping.getValue();
			this.fromFieldExpression = SearchQueryUtil.getFieldValueExpression(
					fieldValue, parsingContext);
			if (!(fromFieldExpression instanceof ExpressionCompilationUnit)) {
				throw new InvalidSearchQueryException("the expression of FromField must not be a block expression");
			}

			isFieldResult = SearchQueryUtil
					.isExpressionField(((ExpressionCompilationUnit) fromFieldExpression)
							.getExpressionNode());
			if (fieldValue instanceof SimpleFieldValueType) {
				String fromExpressionString = ((SimpleFieldValueType) fieldValue)
						.getValueFieldPath();
				String[] paths = fromExpressionString.split("\\.");
				if (paths.length == 2) {
					Class resultClass = parsingContext
							.getVariableClass(paths[0]);
					if (resultClass.isInterface()
							&& IPersistentEntity.class
									.isAssignableFrom(resultClass)) {
						/**
						 * String beName =
						 * BEUtil.getBENameByInterface(resultClass.getName());
						 * 
						 * IClassDescriptor desc = ORMapperController
						 * .getDescriptor(beName);
						 * 
						 * if (desc instanceof IFieldOwnerDescriptor)
						 * 
						 * {
						 * 
						 * String cPath = ((IFieldOwnerDescriptor) desc)
						 * .getConfigPath();
						 * 
						 * if (cPath != null)
						 * 
						 * {
						 * 
						 * IFieldDescriptor fieldDesc = desc
						 * .getFieldDescriptor(paths[1]);
						 * 
						 * this.configPath = fieldDesc.getConfigPath();
						 * 
						 * this.pfFieldName = fieldDesc.getPFFieldName();
						 * 
						 * }
						 * 
						 * }
						 */
					}
				}
			}

			if (isFieldResult) {
				fromFieldOQLString = SearchQueryUtil
						.getOQLExpressionString(fromFieldExpression);
			}
		} catch (Throwable t) {
			if (t instanceof InvalidSearchQueryException) {
				throw (InvalidSearchQueryException) t;
			} else {
				throw new InvalidSearchQueryException("fail to initialize search result context", t);
			}
		}
	}

	public void getSelectedFieldNames(OQLFieldLister fieldLister) {
		fromFieldExpression.traverse(fieldLister);
	}

	public List<?> getResultDataValue(
			SearchQueryEvaluationContext evaluationContext,
			List constrainedObjectList) {
		try {
			Object fromValue = StatementEvaluator.evaluate(fromFieldExpression,
					evaluationContext);
			if (constrainedObjectList != null) {
				for (int i = constrainedObjectList.size() - 1; i >= 0; i--) {
					SearchQueryEvaluationContext constrainedContext = (SearchQueryEvaluationContext) constrainedObjectList
							.get(i);
					Object constrainedValue = StatementEvaluator.evaluate(
							toFieldExpression, constrainedContext);
					if ((fromValue == null && constrainedValue == null)
							|| (fromValue != null && fromValue
									.equals(constrainedValue)))

					{
						continue;
					} else {
						constrainedObjectList.remove(i);
					}
				}
			}

			evaluationContext.setTargetFieldValue(fromValue);
			StatementEvaluator
					.evaluate(setToFieldExpression, evaluationContext);
			return constrainedObjectList;
		} catch (Throwable t) {
			throw new PersistenceRuntimeException("fail to get result data value for expression:{0}", t,
					new Object[] { fromFieldExpression.toString() });
		}
	}

	public QueryExpressionNodeList getConstrainedFilter(
			SearchQueryEvaluationContext evaluationContext) {
		QueryExpressionNodeList constrainedFilter = null;
		try {
			if (isFieldResult) {
				Object constrainedValue = StatementEvaluator.evaluate(
						toFieldExpression, evaluationContext);
				if (constrainedValue != null) {
					QueryExpression constrainedExpression = new QueryExpression(
							fromFieldOQLString, Operator.EQUALS,
							constrainedValue);

					if (useParamBinding) {
						constrainedExpression.setValueAsParam(true);
					}
					constrainedFilter = new QueryExpressionNodeList(
							constrainedExpression);
				}
			}
		} catch (Throwable t) {
			throw new PersistenceRuntimeException("fail to make constrained filter", t);
		}
		return constrainedFilter;
	}

}
