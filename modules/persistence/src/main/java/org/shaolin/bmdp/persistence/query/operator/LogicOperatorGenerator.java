package org.shaolin.bmdp.persistence.query.operator;

import java.util.List;

import org.shaolin.bmdp.datamodel.rdbdiagram.LogicOperatorType;
import org.shaolin.bmdp.persistence.query.generator.QueryExpression;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;

public class LogicOperatorGenerator implements IOperatorExpressionGenerator<LogicOperatorType> {

	@Override
	public QueryExpressionNodeList getStaticConditionExpression(String toField,
			String valueExpression, LogicOperatorType operator) {
		QueryExpressionNodeList result;

		LogicOperatorType logicOperator = (LogicOperatorType)operator;
		String type = logicOperator.getType();
		Operator searchOperator = Operator.fromString(type);
		
		if (Operator.IS_NULL == searchOperator) {
			QueryExpression expression = new QueryExpression(toField,
					searchOperator);
			result = new QueryExpressionNodeList(expression);
		} else {
			QueryExpression expression = new QueryExpression(toField,
					searchOperator, valueExpression, true);
			result = new QueryExpressionNodeList(expression);
		}

		return result;
	}

	@Override
	public QueryExpressionNodeList getSearchConditionExpression(String toField,
			Object fieldValue, LogicOperatorType operator, boolean useParamBinding) {
		QueryExpressionNodeList result;

		LogicOperatorType logicOperator = (LogicOperatorType) operator;
		String type = logicOperator.getType();
		Operator searchOperator = Operator.fromString(type);

		if (Operator.IS_NULL == searchOperator) {
			QueryExpression expression = new QueryExpression(toField,
					searchOperator);
			result = new QueryExpressionNodeList(expression);
		} else {
			// special for in operator
			QueryExpression expression;
			if (Operator.IN == searchOperator) {
//				List<?> valueList = (List<?>) fieldValue;
//				if (valueList.size() == 0) {
//					expression = new QueryExpression("1 = 0");
//				} else {
					expression = new QueryExpression(toField, searchOperator,
							fieldValue);
					if (useParamBinding) {
						expression.setValueAsParam(true);
					}
//				}
			} else {
				expression = new QueryExpression(toField, searchOperator,
						fieldValue);
				if (useParamBinding) {
					expression.setValueAsParam(true);
				}
			}
			result = new QueryExpressionNodeList(expression);
		}

		return result;
	}

	public QueryExpressionNodeList getCompatibleConditionExpression(
			String toField, Object fieldValue, LogicOperatorType operator,
			boolean useParamBinding) {
		return getSearchConditionExpression(toField, fieldValue, operator,
				useParamBinding);
	}

	@Override
	public boolean validateValue(Object toFieldValue, Object conditionValue,
			LogicOperatorType operator) {
		boolean isValidate = false;

		LogicOperatorType logicOperator = (LogicOperatorType) operator;
		String type = logicOperator.getType();
		Operator searchOperator = Operator.fromString(type);

		if (Operator.IS_NULL == searchOperator) {
			isValidate = (toFieldValue == null);
		} else if (Operator.GREATER_THAN == searchOperator) {
			double toFieldNumber = ((Number) toFieldValue).doubleValue();
			double conditionNumber = ((Number) conditionValue).doubleValue();
			isValidate = (toFieldNumber > conditionNumber);
		} else if (Operator.GREATER_THAN_OR_EQUALS == searchOperator) {
			double toFieldNumber = ((Number) toFieldValue).doubleValue();
			double conditionNumber = ((Number) conditionValue).doubleValue();
			isValidate = (toFieldNumber >= conditionNumber);
		} else if (Operator.LESS_THAN == searchOperator) {
			double toFieldNumber = ((Number) toFieldValue).doubleValue();
			double conditionNumber = ((Number) conditionValue).doubleValue();
			isValidate = (toFieldNumber < conditionNumber);
		} else if (Operator.LESS_THAN_OR_EQUALS == searchOperator) {
			double toFieldNumber = ((Number) toFieldValue).doubleValue();
			double conditionNumber = ((Number) conditionValue).doubleValue();
			isValidate = (toFieldNumber < conditionNumber);
		} else if (Operator.EQUALS == searchOperator) {
			isValidate = ((toFieldValue == null) && (conditionValue == null))
					|| ((toFieldValue != null) && (toFieldValue
							.equals(conditionValue)));
		} else if (Operator.EQUALS_IGNORE_CASE == searchOperator) {
			String toFieldString = String.valueOf(toFieldValue);
			String conditionString = String.valueOf(conditionValue);
			isValidate = toFieldString.equalsIgnoreCase(conditionString);
		} else if (Operator.IN == searchOperator) {
			List<?> conditionList = (List<?>) conditionValue;
			isValidate = conditionList.contains(toFieldValue);
		}

		return isValidate;
	}

}
