package org.shaolin.uimaster.page.javacc;

//imports
import java.io.Serializable;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionEvaluator;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.od.ODPageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class for expression evaluator
 */

public class VariableEvaluator implements Serializable {
	
	private static final long serialVersionUID = -2404748954100069004L;

	private static Logger logger = LoggerFactory.getLogger(VariableEvaluator.class);

	private EvaluationContext eContext;

	public VariableEvaluator(final EvaluationContext eContext) {
		this.eContext = eContext;
	}

	public String evaluateReadOnly(String expressionString) {
		// TODO:
		if (expressionString == null) {
			return Boolean.FALSE.toString();
		}
		if (expressionString.equals("parent")) {
			return expressionString;
		} else if (!expressionString.equals("null")) {
			return Boolean.FALSE.toString();
		}
		return Boolean.valueOf(expressionString).toString();
	}

	public Object evaluateExpression(Expression expression)
			throws EvaluationException {
		Object result = ExpressionEvaluator.evaluate(expression, eContext);
		if (result == null) {
			return "";
		} else {
			return result;
		}
	}

	public EvaluationContext getExpressionContext() {
		return eContext;
	}
	
	public EvaluationContext getExpressionContext(String tag) {
		if (eContext instanceof ODPageContext) {
			return ((ODPageContext)eContext).getEvaluationContextObject(tag);
		} else if(eContext instanceof OOEEContext) {
			return ((OOEEContext)eContext).getEvaluationContextObject(tag);
		}
		return eContext;
	}

	public Object evaluateExpression(ExpressionType expression)
			throws EvaluationException {
		Object result = expression.evaluate(eContext);
		return result == null ? "" : result;
	}

}
