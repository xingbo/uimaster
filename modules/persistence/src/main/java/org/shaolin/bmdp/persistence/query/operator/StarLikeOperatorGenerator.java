package org.shaolin.bmdp.persistence.query.operator;

import org.shaolin.bmdp.datamodel.rdbdiagram.StarLikeOperatorType;
import org.shaolin.bmdp.persistence.query.generator.QueryExpression;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;

public class StarLikeOperatorGenerator implements IOperatorExpressionGenerator<StarLikeOperatorType> {
	
	@Override
	public QueryExpressionNodeList getStaticConditionExpression(String toField,
			String valueExpression, StarLikeOperatorType operator) {
		QueryExpression expression = null;
		if (operator.getType().equals(Operator.START_WITH_LEFT.toString())) {
			expression = new QueryExpression(toField, Operator.START_WITH_LEFT, valueExpression, true);
		} else if (operator.getType().equals(Operator.START_WITH_RIGHT.toString())) {
			expression = new QueryExpression(toField, Operator.START_WITH_RIGHT, valueExpression, true);
		} else {
			expression = new QueryExpression(toField, Operator.START_WITH, valueExpression, true);
		}
		
		QueryExpressionNodeList result = new QueryExpressionNodeList(expression);
		return result;
	}

	@Override
	public QueryExpressionNodeList getSearchConditionExpression(String toField,
			Object fieldValue, StarLikeOperatorType operator, boolean useParamBinding) {
		QueryExpressionNodeList result = null;

		if (fieldValue != null) {
			String fieldString = String.valueOf(fieldValue);
			if (fieldString.endsWith("**")) {
				String prefix = fieldString.substring(0,
						fieldString.length() - 2);
				// START_WITH can use index, so we do not bind the param
				QueryExpression expression = new QueryExpression(toField,
						Operator.START_WITH, prefix);
				result = new QueryExpressionNodeList(expression);
			} else if (fieldString.endsWith("*")) {
				String prefix = fieldString.substring(0,
						fieldString.length() - 1);
				QueryExpression expression1 = new QueryExpression(toField + " like '"
						+ prefix + "%'");
				QueryExpression expression2 = new QueryExpression(toField + " like '"
						+ prefix + "%.%'");
				expression2.reverse();
				result = new QueryExpressionNodeList(expression1);
				result.append(expression2, LogicalOperator.AND);
			} else if (operator.getType().equals(Operator.START_WITH_LEFT.toString())) {
				// left must be generated in the condition code.
				QueryExpression expression1 = new QueryExpression(
						toField, Operator.START_WITH_LEFT, fieldString);
				result = new QueryExpressionNodeList(expression1);
			} else if (operator.getType().equals(Operator.START_WITH_RIGHT.toString())) {
				// right must be generated in the condition code.
				QueryExpression expression1 = new QueryExpression(
						toField, Operator.START_WITH_RIGHT, fieldString);
				result = new QueryExpressionNodeList(expression1);
			} else {
				QueryExpression expression = new QueryExpression(toField,
						Operator.START_WITH, fieldString);
				if (useParamBinding) {
					expression.setValueAsParam(true);
				}
				result = new QueryExpressionNodeList(expression);
			}
		}

		return result;
	}

	public QueryExpressionNodeList getCompatibleConditionExpression(String toField,
			Object fieldValue, StarLikeOperatorType operator, boolean useParamBinding) {
		String fieldString = String.valueOf(fieldValue);
		QueryExpression expression = new QueryExpression(toField, Operator.EQUALS,
				fieldString);
		if (useParamBinding) {
			expression.setValueAsParam(true);
		}
		QueryExpressionNodeList result = new QueryExpressionNodeList(expression);

		boolean first = true;
		int index;
		while ((index = fieldString.lastIndexOf('.')) != -1) {
			fieldString = fieldString.substring(0, index);
			if (first) {
				expression = new QueryExpression(toField, Operator.EQUALS,
						fieldString + ".*");
				if (useParamBinding) {
					expression.setValueAsParam(true);
				}
				result.append(expression, LogicalOperator.OR);
				first = false;
			}
			expression = new QueryExpression(toField, Operator.EQUALS, fieldString
					+ ".**");
			if (useParamBinding) {
				expression.setValueAsParam(true);
			}
			result.append(expression, LogicalOperator.OR);
		}

		if (first) {
			expression = new QueryExpression(toField, Operator.EQUALS, "*");
			result.append(expression, LogicalOperator.OR);
		}

		expression = new QueryExpression(toField, Operator.EQUALS, "**");
		result.append(expression, LogicalOperator.OR);

		return result;
	}

	@Override
	public boolean validateValue(Object toFieldValue, Object conditionValue,
			StarLikeOperatorType operator) {
		boolean isValidate = false;

		String conditionString = String.valueOf(conditionValue);
		String toFieldString = String.valueOf(toFieldValue);

		if (conditionString.endsWith("**")) {
			String prefix = conditionString.substring(0,
					conditionString.length() - 2);
			isValidate = toFieldString.startsWith(prefix);
		} else if (conditionString.endsWith("*")) {
			String prefix = conditionString.substring(0,
					conditionString.length() - 1);
			isValidate = toFieldString.startsWith(prefix);
			if (isValidate) {
				String suffix = toFieldString.substring(prefix.length());
				isValidate = (suffix.indexOf('.') == -1);
			}
		} else {
			isValidate = toFieldString.equals(conditionString);
		}

		return isValidate;
	}

}
