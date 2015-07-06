package org.shaolin.javacc.symbol;

//imports
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;


/**
 * The class for fieldname node
 * 
 * @author Xiao Yi
 */

public class FieldName extends ExpressionNode {
	public FieldName() {
		super("FieldName");
		fieldName = "";
		isLength = false;
		isField = false;
		isCustomField = false;
		fieldClassName = null;
		isStaticField = false;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		isLikeClassName = checkLikeClassName();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setVariableName(String vName) {
		variableName = vName;
		isVariable = true;
	}

	public boolean isLikeClassName() {
		return isLikeClassName;
	}

	public boolean isField() {
		return isField;
	}

	public boolean isCustomField() {
		return isCustomField;
	}

	public Class checkType(OOEEParsingContext context) throws ParsingException {
		FieldExpression parentField = (FieldExpression) parent;
		Class parentClass = parentField.getValueClass();
		boolean isParentClass = parentField.isClass();

		Class clazz = null;
		// the parent class is found, then try to resolve this field as an inner
		// class, a field
		if (isLikeClassName) {
			clazz = resolveAsInnerClass(parentField, parentClass, isParentClass);
			if (clazz == null) {
				clazz = resolveAsField(parentField, parentClass, isParentClass,
						context);
			}
		} else {
			clazz = resolveAsField(parentField, parentClass, isParentClass,
					context);
			if (clazz == null) {
				clazz = resolveAsInnerClass(parentField, parentClass,
						isParentClass);
			}
		}
		if (clazz == null) {
			throw new ParsingException(ExceptionConstants.EBOS_OOEE_079,
					new Object[] { toString(), parentClass });
			// throw new ParsingException("Can't resolve symbol " + toString()
			// + " for " + parentClass);
		}
		return clazz;
	}

	private Class resolveAsInnerClass(FieldExpression parentField,
			Class parentClass, boolean isParentClass) {
		if (isParentClass) {
			String className = parentClass.getName() + "$" + fieldName;
			try {
				setValueClass(ExpressionUtil.findClass(className, true));
				return valueClass;
			} catch (ParsingException e) {
				// class not found
				setValueClass(null);
			}
		}
		return null;
	}

	private Class resolveAsField(FieldExpression parentField,
			Class parentClass, boolean isParentClass, OOEEParsingContext context) {
		if (fieldName.equals("length") && !isParentClass
				&& parentClass.isArray()) {
			isLength = true;
			isField = true;
			setValueClass(int.class);
			return int.class;
		}

		Class fieldClass = null;
		try {
			Field field = ExpressionUtil.findField(parentClass, fieldName,
					isParentClass);
			fieldClass = field.getType();

			if (!Modifier.isFinal(field.getModifiers())) {
				super.isVariable = true;
			}
		} catch (ParsingException ex) {
		}

		// add custom field support
		if (fieldClass == null) {
			try {
				fieldClass = context.findField(parentClass, fieldName);
				isCustomField = true;
				super.isVariable = true;
			} catch (ParsingException ex) {
				isCustomField = false;
			}
		}

		if (fieldClass != null) {
			setValueClass(fieldClass);
			fieldClassName = parentClass.getName();
			isStaticField = isParentClass;
			isField = true;
			parentField.setIsClass(false);
		} else
		// field not found
		{
			isField = false;
		}

		return valueClass;
	}

	protected void evaluateNode(OOEEEvaluationContext context)
			throws EvaluationException {
		try {
			getValueClass();
		} catch (ParsingException e) {
			throw new EvaluationException(ExceptionConstants.EBOS_000, e);
		}

		if (isField) {
			if (isLength) {
				Object currentObject = context.stackPop();
				context.stackPush(new Integer(Array.getLength(currentObject)));
			} else {
				Class fieldClass = null;
				try {
					if (fieldClassName != null) {
						fieldClass = ExpressionUtil.findClass(fieldClassName);
					}
				} catch (ParsingException e) {
					throw new EvaluationException(ExceptionConstants.EBOS_000,
							e);
				}

				try {
					Object currentObject;

					if (!isStaticField) {
						if (isValueAssignee()) {
							currentObject = context.stackPeek();
						} else {
							currentObject = context.stackPop();
						}
					} else {
						currentObject = null;
					}

					if (isCustomField) {
						context.stackPush(context.getFieldValue(fieldClass,
								currentObject, fieldName));
					} else {
						Field field = ExpressionUtil.findField(fieldClass,
								fieldName, isStaticField);
						context.stackPush(field.get(currentObject));
					}
				} catch (Exception e) {
					throw new EvaluationException(ExceptionConstants.EBOS_000,
							e);
				}
			}
		} else if (super.isVariable) {
			try {
				context.stackPush(context.getVariableValue(variableName));
			} catch (EvaluationException e) {
				throw e;
			}
		}
	}

	public boolean isVariableNode() {
		return !isField && super.isVariable;
	}

	protected void setVariableValue(OOEEEvaluationContext context,
			Object variableValue) throws EvaluationException {
		if (isField) {
			Object currentObject;
			if (!isStaticField) {
				currentObject = context.stackPop();
			} else {
				currentObject = null;
			}

			Class fieldClass = null;
			try {
				if (fieldClassName != null) {
					fieldClass = ExpressionUtil.findClass(fieldClassName);
				}
			} catch (ParsingException e) {
				throw new EvaluationException(ExceptionConstants.EBOS_000, e);
			}

			if (isCustomField) {
				context.setFieldValue(fieldClass, currentObject, fieldName,
						variableValue);
			} else {
				try {
					Field field = ExpressionUtil.findField(fieldClass,
							fieldName, isStaticField);
					field.set(currentObject, variableValue);
				} catch (Exception e) {
					throw new EvaluationException(ExceptionConstants.EBOS_000,
							e);
				}
			}
		} else {
			context.setVariableValue(variableName, variableValue);
		}
	}

	public void appendToBuffer(ExpressionStringBuffer buffer) {
		buffer.appendSeperator(this, fieldName);
	}

	private boolean checkLikeClassName() {
		if (!Character.isUpperCase(fieldName.charAt(0))) {
			return false;
		}
		for (int i = 1, n = fieldName.length(); i < n; i++) {
			char c = fieldName.charAt(i);
			if (!Character.isUpperCase(c) && !Character.isDigit(c) && c != '_') {
				return true;
			}
		}
		return false;
	}

	/*
	 * public String toString() { return fieldName; }
	 */

	/* whether this node is the length property of an array */
	private boolean isLength;

	/* whether this node is resolved as a field of a class */
	private boolean isField;

	/* whether this node is resolved as a customer field */
	private boolean isCustomField;

	/* the name of the class the field belongs to */
	private String fieldClassName;

	/* whether it's a static field */
	private boolean isStaticField;

	/* field name */
	private String fieldName;

	/* variable name */
	private String variableName;

	private boolean isLikeClassName = false;

	static final long serialVersionUID = 0xD41AA00A967B6261L;

}
