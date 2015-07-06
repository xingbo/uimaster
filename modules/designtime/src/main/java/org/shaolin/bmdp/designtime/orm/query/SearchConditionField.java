package org.shaolin.bmdp.designtime.orm.query;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.rdbdiagram.CEEqualsOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LogicOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.OperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.StarLikeOperatorType;
import org.shaolin.bmdp.persistence.InvalidSearchQueryException;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryEvaluationContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryParsingContext;
import org.shaolin.bmdp.persistence.query.generator.SearchQueryUtil;
import org.shaolin.bmdp.persistence.query.operator.IOperatorExpressionGenerator;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.bmdp.persistence.query.operator.OperatorGeneratorFactory;
import org.shaolin.javacc.statement.CompilationUnit;

public class SearchConditionField {
	private CompilationUnit valueExpression;
	private String toFieldString;
	private String valueExpressionString;
	private OperatorType operator;
	@SuppressWarnings("rawtypes")
	private IOperatorExpressionGenerator operatorGenerator;
	private CompilationUnit conditionExpression;
	private boolean useParamBinding = true;

	public SearchConditionField(String toFieldString,
			ConditionFieldValueType conditionFieldValue,
			boolean hasUseParamBinding, boolean isUseParamBinding,
			SearchQueryParsingContext parsingContext)
			throws InvalidSearchQueryException {
		try {
			this.toFieldString = toFieldString;
			this.useParamBinding = (hasUseParamBinding && isUseParamBinding)
					|| (!hasUseParamBinding && parsingContext
							.getUseParamBinding());

			FieldValueType fieldValue = conditionFieldValue.getValue();

			valueExpression = SearchQueryUtil.getFieldValueExpression(
					fieldValue, parsingContext);
			valueExpressionString = SearchQueryUtil
					.getOQLExpressionString(valueExpression);

			this.operator = conditionFieldValue.getOperator();
			this.operatorGenerator = OperatorGeneratorFactory
					.getOperatorExpressionGenerator(operator);

			ExpressionType condition = conditionFieldValue.getCondition();
			if (condition != null) {
				conditionExpression = SearchQueryUtil.parseExpression(
						condition, parsingContext);
			} else {
				conditionExpression = null;
			}
		} catch (Throwable t) {
			if (t instanceof InvalidSearchQueryException) {
				throw (InvalidSearchQueryException) t;
			} else {
				throw new InvalidSearchQueryException("fail to construct search condition", t);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public QueryExpressionNodeList getSearchConditionExpression(
			SearchQueryEvaluationContext evaluationContext) {
		boolean conditionSatisfied = true;
		QueryExpressionNodeList expression = null;
		if (conditionSatisfied) {
			Object valueExpressionValue = "?";

			if ("null".equals(toFieldString)) {
				toFieldString = null;
			}
			expression = operatorGenerator.getSearchConditionExpression(
					toFieldString, valueExpressionValue, operator,
					useParamBinding);

			if (expression != null && operator.isIsReverse()) {
				expression.reverse();
			}
		}

		return expression;
	}

	public QueryExpressionNodeList getStaticFilterExpression() {
		@SuppressWarnings("unchecked")
		QueryExpressionNodeList expression = operatorGenerator
				.getStaticConditionExpression(getToFieldExpression(),
						valueExpressionString, operator);
		if (expression != null && operator.isIsReverse()) {
			expression.reverse();
		}

		return expression;
	}

	/**
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean validateResultData(SearchQueryEvaluationContext context) {
		return true;
	}

	public Operator getOperator() {
		if (operator instanceof LogicOperatorType) {
			return Operator
					.fromString(((LogicOperatorType) operator).getType());
		}
		if (operator instanceof StarLikeOperatorType) {
			return Operator.fromString(((StarLikeOperatorType) operator)
					.getType());
		}
		if (operator instanceof CEEqualsOperatorType) {
			return Operator.EQUALS;
		}
		return Operator.EQUALS;
	}

	public boolean isListOperator() {
		return false;
	}

	public String getToFieldExpression() {
		return toFieldString;
	}

	public String getConditionExpression() {
		return conditionExpression != null ? conditionExpression
				.getExpressionString() : "";
	}

	public String getJavaValueExpression() {
		String expr = valueExpression.getExpressionString();
		if (expr.indexOf("(") != -1
				|| expr.indexOf("{") != -1
				|| expr.indexOf("[") != -1) {
			// java method.
			return expr;
		}
		int index = expr.indexOf(".");
		if (index != -1) {
			StringBuffer sb = new StringBuffer();
			sb.append(expr.substring(0, index + 1));
			String field1 = expr.substring(index + 1, index + 2).toUpperCase();
			String field2 = expr.substring(index + 2);
			sb.append("get");
			sb.append(field1);
			sb.append(field2);
			sb.append("()");
			return sb.toString();
		}
		return expr;
	}

}
