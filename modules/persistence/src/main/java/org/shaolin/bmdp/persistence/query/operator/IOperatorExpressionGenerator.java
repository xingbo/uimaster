package org.shaolin.bmdp.persistence.query.operator;

import org.shaolin.bmdp.datamodel.rdbdiagram.OperatorType;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;

public interface IOperatorExpressionGenerator<T extends OperatorType> {

	/**
	 * 
	 * @param toField
	 * @param valueExpression
	 * @param operator
	 * @return
	 */
	public QueryExpressionNodeList getStaticConditionExpression(String toField,
			String valueExpression, T operator);

	/**
	 * 
	 * @param toField
	 * @param fieldValue
	 * @param operator
	 * @param useParamBinding
	 * @return
	 */
	public QueryExpressionNodeList getSearchConditionExpression(String toField,
			Object fieldValue, T operator, boolean useParamBinding);

	/**
	 * 
	 * @param toFieldValue
	 * @param conditionValue
	 * @param operator
	 * @return
	 */
	public boolean validateValue(Object toFieldValue, Object conditionValue,
			T operator);

}
