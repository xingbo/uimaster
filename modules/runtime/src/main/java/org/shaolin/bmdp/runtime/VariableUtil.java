/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.bmdp.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.utils.StringUtil;
import org.shaolin.javacc.StatementEvaluator;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.util.ExpressionUtil;

public final class VariableUtil {
	
	public static Object createVariableObject(VariableType variable)
			throws EntityNotFoundException {
		String entityName = variable.getType().getEntityName();
		return createObject(entityName, variable.getCategory());
	}

	public static Object createObject(String entityName, VariableCategoryType categoryType)
			throws EntityNotFoundException {
		if (VariableCategoryType.BUSINESS_ENTITY == categoryType) {
			return BEUtil.createBEObject(entityName);
		} else if (VariableCategoryType.CONSTANT_ENTITY == categoryType) {
			return CEUtil.getConstantEntity(entityName);
		} else if (VariableCategoryType.JAVA_CLASS == categoryType
				|| VariableCategoryType.JAVA_PRIMITIVE == categoryType) {
			try {
				Class<?> objectClass = Class.forName(entityName);
				if (objectClass.isInterface()) {
					return null;
				}

				if (objectClass == boolean.class) {
					return Boolean.FALSE;
				} else if (objectClass == byte.class) {
					return new Byte((byte) 0);
				} else if (objectClass == short.class) {
					return new Short((short) 0);
				} else if (objectClass == int.class) {
					return new Integer(0);
				} else if (objectClass == long.class) {
					return new Long(0);
				} else if (objectClass == float.class) {
					return new Float(0);
				} else if (objectClass == double.class) {
					return new Double(0);
				} else if (objectClass == char.class) {
					return new Character((char) 0);
				} else {
					return objectClass.newInstance();
				}
			} catch (Exception ex) {
				throw new EntityNotFoundException(
						ExceptionConstants.EBOS_COMMON_003, ex,
						new Object[] { entityName });
			}
		} else if (VariableCategoryType.JOIN_TABLE == categoryType) {
			return null;
		} else {
			throw new EntityNotFoundException(
					ExceptionConstants.EBOS_COMMON_008, new Object[] { categoryType.value(),
							entityName });
		}
	}

	public static String getVariableBeanName(String varName) {
		return StringUtil.getBeanName(varName);
	}

	public static String getVariableClassName(Class clazz) {

		StringBuffer classNameBuffer = new StringBuffer();

		while (clazz.isArray()) {
			classNameBuffer.append("[]");
			clazz = clazz.getComponentType();
		}

		return clazz.getName() + classNameBuffer.toString();
	}

	public static String getVariableClassName(VariableType variable)
			throws EntityNotFoundException {
		VariableCategoryType categoryType = variable.getCategory();
		String variableType = variable.getType().getEntityName();

		String variableClassName = null;

		if (categoryType == null) {
			categoryType = VariableCategoryType.JAVA_CLASS;
		}
		if (categoryType == VariableCategoryType.BUSINESS_ENTITY) {
//			String packageName = variableType.substring(0, variableType.length() - variableType.lastIndexOf('.') - 1);
//			String name = variableType.substring(variableType.length() - variableType.lastIndexOf('.'));
			variableClassName = variableType + "Impl";
		} else if (categoryType == VariableCategoryType.CONSTANT_ENTITY) {
			variableClassName = variableType;
		} else if (categoryType == VariableCategoryType.JAVA_CLASS
				|| VariableCategoryType.JAVA_PRIMITIVE == categoryType) {
			variableClassName = variableType;
		} 

		return variableClassName;
	}

	public static Class<?> getVariableClass(VariableType variable)
			throws EntityNotFoundException {
		String varClassName = getVariableClassName(variable);
		try {
			return ExpressionUtil.findClass(varClassName);
		} catch (ParsingException ex) {
			throw new EntityNotFoundException(
					ExceptionConstants.EBOS_COMMON_007, ex,
					new Object[] { varClassName });
		}
	}

	public static String getVariableDeclareString(VariableType variableData)
			throws EntityNotFoundException {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getVariableClassName(variableData));
		buffer.append(" ");
		buffer.append(variableData.getName());
		if (variableData.getDefault() != null) {
			buffer.append(" = ");
			buffer.append(variableData.getDefault().getExpressionString());
		}

		return buffer.toString();
	}

	// Variable <--> Value
	public static Object getVariableInitialValue(VariableType variable) {
		return getVariableInitialValue(getVariableClass(variable));
	}

	public static Object getVariableInitialValue(Class objectClass) {
		if (objectClass == boolean.class) {
			return Boolean.FALSE;
		} else if (objectClass == byte.class) {
			return new Byte((byte) 0);
		} else if (objectClass == short.class) {
			return new Short((short) 0);
		} else if (objectClass == int.class) {
			return new Integer(0);
		} else if (objectClass == long.class) {
			return new Long(0);
		} else if (objectClass == float.class) {
			return new Float(0);
		} else if (objectClass == double.class) {
			return new Double(0);
		} else if (objectClass == char.class) {
			return new Character((char) 0);
		} else {
			return null;
		}
	}

	public static Map createVariableClassMap(List<VariableType> vars) {
		Map varClass = new HashMap();
		for (VariableType var: vars) {
			varClass.put(var.getName(), getVariableClass(var));
		}
		return varClass;
	}

	public static Map createVariableInitialValueMap(List<VariableType> vars) {
		Map varValue = new HashMap();
		for (VariableType var: vars) {
			varValue.put(var.getName(), getVariableInitialValue(var));
		}
		return varValue;
	}

	public static Map createVariableNullValueMap(List<VariableType> vars) {
		Map varValue = new HashMap();
		for (VariableType var: vars) {
			varValue.put(var.getName(), null);
		}
		return varValue;
	}

	// Variable <--> ParsingContext && Variable <--> EvaluationContext
	public static ParsingContext createParsingContext(List<VariableType> vars) {
		return new DefaultParsingContext(createVariableClassMap(vars));
	}

	public static EvaluationContext createEvaluationContext(List<VariableType> vars) {
		return createEvaluationContext(vars, true);
	}

	public static EvaluationContext createEvaluationContext(
			List<VariableType> vars, boolean needDefaultValue) {
		Map varValue;
		if (needDefaultValue) {
			varValue = createVariableDefaultValueMap(vars);
		} else {
			varValue = createVariableNullValueMap(vars);
		}

		return new DefaultEvaluationContext(varValue);
	}

	public static Map createVariableDefaultValueMap(List<VariableType> vars) {
		Map varValue = new HashMap();
		for (VariableType var: vars) {
			Object defaultValue = getVariableDefaultValue(var);
			varValue.put(var.getName(), defaultValue);
		}
		return varValue;
	}

	public static Object getVariableDefaultValue(VariableType variable) {
		Class c = getVariableClass(variable);
		Object value;

		if (variable.getDefault() != null) {
			String expression = variable.getDefault().getExpressionString();
			CompilationUnit unit = null;
			try {
				unit = StatementParser.parse(expression);
				value = StatementEvaluator.evaluate(unit);
			} catch (Exception ex) {
				value = null;
			}
		} else {
			value = getVariableInitialValue(c);
		}

		return value;
	}
}
