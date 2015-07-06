package org.shaolin.bmdp.persistence.query.operator;

import org.shaolin.bmdp.datamodel.rdbdiagram.CEEqualsOperatorType;
import org.shaolin.bmdp.persistence.query.generator.QueryExpression;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;

public class CEEqualsOperatorGenerator implements IOperatorExpressionGenerator<CEEqualsOperatorType> {

	@Override
	public QueryExpressionNodeList getStaticConditionExpression(String toField, 
			String valueExpression, CEEqualsOperatorType operator) {
		QueryExpression expression = new QueryExpression(toField, Operator.EQUALS, valueExpression, true);
		QueryExpressionNodeList result = new QueryExpressionNodeList(expression);
		return result;
	}

	@Override
	public QueryExpressionNodeList getSearchConditionExpression(String toField,
			Object fieldValue, CEEqualsOperatorType operator, boolean useParamBinding) {
		QueryExpressionNodeList result = null;

		if (fieldValue instanceof IConstantEntity) {
			String ceValue = ((IConstantEntity) fieldValue).getValue();
			if (!IConstantEntity.CONSTANT_DEFAULT_VALUE.equals(ceValue)) {
				QueryExpression expression = new QueryExpression(toField,
						Operator.EQUALS, fieldValue);
				if (useParamBinding) {
					expression.setValueAsParam(true);
				}
				result = new QueryExpressionNodeList(expression);
			}
		}

		return result;
	}

	public QueryExpressionNodeList getCompatibleConditionExpression(String toField,
			Object fieldValue, CEEqualsOperatorType operator, boolean useParamBinding) {
		QueryExpressionNodeList result = null;

		if (fieldValue instanceof IConstantEntity) {
			IConstantEntity unspecified = 
					CEUtil.getConstantEntity(fieldValue.getClass().getName());
			QueryExpression defaultExpression = new QueryExpression(toField,
					Operator.EQUALS, unspecified);
			result = new QueryExpressionNodeList(defaultExpression);

			String ceValue = ((IConstantEntity) fieldValue).getValue();
			if (!IConstantEntity.CONSTANT_DEFAULT_VALUE.equals(ceValue)) {
				QueryExpression expression = new QueryExpression(toField,
						Operator.EQUALS, fieldValue);
				if (useParamBinding) {
					expression.setValueAsParam(true);
				}
				result.append(expression, LogicalOperator.OR);
			}
		}

		return result;
	}

	@Override
	public boolean validateValue(Object toFieldValue, Object conditionValue,
			CEEqualsOperatorType operator) {
		if (!(conditionValue instanceof IConstantEntity)
				|| !(toFieldValue instanceof IConstantEntity)) {
			return false;
		}
		boolean isValidate = false;
		String conditionString = ((IConstantEntity) conditionValue).getValue();
		if (IConstantEntity.CONSTANT_DEFAULT_VALUE.equals(conditionString)) {
			isValidate = true;
		} else {
			String toFieldString = ((IConstantEntity) toFieldValue).getValue();
			isValidate = conditionString.equals(toFieldString);
		}
		return isValidate;
	}

}
