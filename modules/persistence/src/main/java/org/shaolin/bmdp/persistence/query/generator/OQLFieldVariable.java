package org.shaolin.bmdp.persistence.query.generator;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;

public class OQLFieldVariable extends ExpressionNode {
	private String varName;
	private Class varClass;
	private String fieldString;

	private boolean isOIDField;

	public OQLFieldVariable(String fieldString, String varName, Class varClass) {
		super("OQLFieldVariable");
		this.fieldString = fieldString;
		this.varName = varName;
		this.varClass = varClass;

		super.isVariable = true;
		super.setValueClass(varClass);
		this.isOIDField = false;
	}

	public Class checkType(OOEEParsingContext context) throws ParsingException {
		return varClass;
	}

	protected void evaluateNode(OOEEEvaluationContext context)
			throws EvaluationException {
		Object value = null;

		Object fieldValue = context.getVariableValue(varName);

		try {
			if (varClass == boolean.class) {
				if (fieldValue instanceof Boolean) {
					value = (Boolean) fieldValue;
				} else {
					long numberValue = ((Number) fieldValue).longValue();
					value = (numberValue == 0l) ? Boolean.FALSE : Boolean.TRUE;
				}
			} else if (ExpressionUtil.isNumeric(varClass)) {
				value = ExpressionUtil.getNumericReturnObject(fieldValue,
						varClass);
			} else if (IConstantEntity.class.isAssignableFrom(varClass)) {
				if (fieldValue != null) {
					if (fieldValue instanceof String) {
						String stringValue = (String) fieldValue;
						value = CEUtil.getConstantEntity(varClass.getName(), stringValue);
					} else {
						// number
						Number numberValue = (Number) fieldValue;
						value = CEUtil.getConstantEntity(varClass.getName(),
								numberValue.intValue());
					}
				}
			} else if (varClass == String.class && fieldValue instanceof Number) {
				value = fieldValue.toString();
			} else {
				value = fieldValue;
			}

			context.stackPush(value);
		} catch (Throwable t) {
			throw new EvaluationException("fail to get the value of selected field", t);
		}
	}

	public String getVarName() {
		return varName;
	}

	public void appendToBuffer(ExpressionStringBuffer buffer) {
		buffer.appendSeperator(this, fieldString);
	}

}
