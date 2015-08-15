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
package org.shaolin.uimaster.page.od.mappings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.javacc.util.traverser.VariableRenamer;

/**
 * util Class for generating file content of odmapper converter java file
 * 
 */
public class ExpressionGeneratorUtil {
	/**
	 * Get java class for a variable from the definition
	 * 
	 * @param variableData
	 *            the variable definition
	 * @return the java class for this variable
	 */
	public static Class getVariableClass(VariableType variableData) {
		return VariableUtil.getVariableClass(variableData);
	}

	/**
	 * replace an expression, replace the specific variable with a different
	 * given name
	 * 
	 * @param unit
	 *            the compilationUnit
	 * @param varRenamers
	 *            the variable renamer set
	 */
	public static void renameExpression(CompilationUnit unit,
			HashMap varRenamers) {
		for (Iterator it = varRenamers.values().iterator(); it.hasNext();) {
			VariableRenamer renamer = (VariableRenamer) it.next();
			unit.traverse(renamer);
		}
	}

	/**
	 * get the expression string after an expression's variables are renamed
	 * 
	 * @param expression
	 *            the expression
	 * @param varRenamers
	 *            the variable renamer set
	 * @param ooeeContext
	 *            the parse context
	 */
	public static String getExpressionString(ExpressionType expression,
			HashMap varRenamers, OOEEContext ooeeContext) {
		String expressionString = null;

		if (expression != null) {
			try {
				CompilationUnit unit = StatementParser.parse(
						expression.getExpressionString(), ooeeContext);
				renameExpression(unit, varRenamers);
				expressionString = unit.toString();
			} catch (ParsingException ex) {
				ex.printStackTrace();
			}
		}

		return expressionString;
	}

	/**
	 * get the total string for a variable
	 * 
	 * @param level
	 * @param isDeclare
	 * @param suffixString
	 */
	public static String getIndexString(int level, boolean isDeclare,
			String suffixString) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 1; i < level; i++) {
			if (isDeclare) {
				buffer.append("int ");
			}
			buffer.append("i");
			buffer.append(i);

			if (i != level - 1 || suffixString != null) {
				buffer.append(", ");
			}
		}

		if (suffixString != null) {
			buffer.append(suffixString);
		}

		return buffer.toString();
	}

	/**
	 * the a default value string for a specific class
	 * 
	 * @param valueClass
	 *            the specific class
	 * @return the default value string for the class
	 */
	public static String getClassDefaultValueString(Class valueClass) {
		if (valueClass == boolean.class) {
			return "false";
		} else if (valueClass == List.class) {
			return "new ArrayList()";
		} else if (valueClass.isPrimitive()) {
			return "0";
		} else {
			return "null";
		}
	}

	/**
	 * get the corresponding wrapper class name for a primitive type class
	 * 
	 * @param primitiveClass
	 *            the primitive type class
	 * @return the wrapper class name
	 */
	public static String getWrapperClassName(Class primitiveClass) {
		String wrapperClassName = null;

		if (primitiveClass == boolean.class) {
			wrapperClassName = "Boolean";
		} else if (primitiveClass == byte.class) {
			wrapperClassName = "Byte";
		} else if (primitiveClass == short.class) {
			wrapperClassName = "Short";
		} else if (primitiveClass == char.class) {
			wrapperClassName = "Character";
		} else if (primitiveClass == int.class) {
			wrapperClassName = "Integer";
		} else if (primitiveClass == long.class) {
			wrapperClassName = "Long";
		} else if (primitiveClass == float.class) {
			wrapperClassName = "Float";
		} else if (primitiveClass == double.class) {
			wrapperClassName = "Double";
		}

		return wrapperClassName;
	}

	/**
	 * get the wrapper converter class name for a specific primitive type class
	 * 
	 * @param primitiveClass
	 *            the primitive type class
	 * @return the wrapper converter class name
	 */
	public static String getWrapperConvertClassName(Class primitiveClass) {
		String getWrapperConvertClassName = null;

		if (primitiveClass != null && primitiveClass.isPrimitive()) {
			if (primitiveClass == boolean.class) {
				getWrapperConvertClassName = "Boolean";
			} else if (primitiveClass == char.class) {
				getWrapperConvertClassName = "Character";
			} else {
				getWrapperConvertClassName = "Number";
			}
		}

		return getWrapperConvertClassName;
	}

	/**
	 * get the method to convert from the wrapper class object to the primitive
	 * type class value
	 * 
	 * @param primitiveClass
	 *            the primitive type class
	 * @return the convert method name
	 */
	public static String getWrapperConvertMethod(Class primitiveClass) {
		String wrapperConvertMethod = null;

		if (primitiveClass == boolean.class) {
			wrapperConvertMethod = "booleanValue";
		} else if (primitiveClass == byte.class) {
			wrapperConvertMethod = "byteValue";
		} else if (primitiveClass == short.class) {
			wrapperConvertMethod = "shortValue";
		} else if (primitiveClass == char.class) {
			wrapperConvertMethod = "charValue";
		} else if (primitiveClass == int.class) {
			wrapperConvertMethod = "intValue";
		} else if (primitiveClass == long.class) {
			wrapperConvertMethod = "longValue";
		} else if (primitiveClass == float.class) {
			wrapperConvertMethod = "floatValue";
		} else if (primitiveClass == double.class) {
			wrapperConvertMethod = "doubleValue";
		}

		return wrapperConvertMethod;
	}

	/**
	 * get the expression string to convert a class
	 * 
	 * @param expressionString
	 *            the to-wrapper expression
	 * @param expressionClass
	 *            the class of the expression
	 * @return the wrapped expression string
	 */
	public static String getToWrapperClassString(String expressionString,
			Class expressionClass) {
		if (expressionClass != null && expressionClass.isPrimitive()
				&& expressionClass != void.class) {
			if (expressionClass == boolean.class) {
				return "Boolean.valueOf(" + expressionString + ")";
			}
			String wrapperClassName = getWrapperClassName(expressionClass);
			return "new " + wrapperClassName + "(" + expressionString + ")";
		}
		return expressionString;
	}

	public static String getClassName(Class c) {
		if (c == null || c == Void.class) {
			return "void";
		}
		if (!c.isArray()) {
			return c.getName();
		} else {
			String className = c.getName();
			String type = "";
			int depth = 0;
			for (int i = 0; i < className.length(); i++) {
				char ch = className.charAt(i);
				if (ch == '[') {
					++depth;
				} else {
					switch (ch) {
					case 'L':
						type = className.substring(i + 1,
								className.length() - 1);
						break;
					case 'Z':
						type = "boolean";
						break;
					case 'I':
						type = "int";
						break;
					case 'J':
						type = "long";
						break;
					case 'C':
						type = "char";
						break;

					case 'D':
						type = "double";
						break;
					case 'F':
						type = "float";
						break;
					case 'B':
						type = "byte";
						break;
					case 'S':
						type = "short";
						break;
					default:
						// should never happen;
						type = "void";
						break;
					}
					break;
				}
			}

			for (int i = 0; i < depth; i++) {
				type += "[]";
			}

			return type;
		}
	}

	/**
	 * get the convert expression string to convert an object to the primitive
	 * type class value
	 * 
	 * @param expressionString
	 *            the expression string
	 * @param expressionClass
	 *            the expression class
	 * 
	 * @return the convert expression string
	 */
	public static String getFromWrapperClassString(String expressionString,
			Class expressionClass) {
		if (expressionClass != null) {
			if (expressionClass.isPrimitive()) {
				String wrapperClassName = getWrapperConvertClassName(expressionClass);
				String wrapperConvertMethod = getWrapperConvertMethod(expressionClass);
				if (wrapperClassName.equals(expressionString)) {
					expressionString = "(" + expressionString + ")."
							+ wrapperConvertMethod + "()";
				} else {
					expressionString = "((" + wrapperClassName + ")"
							+ expressionString + ")." + wrapperConvertMethod
							+ "()";
				}
			} else {
				String expressionClassName = VariableUtil
						.getVariableClassName(expressionClass);
				expressionString = "(" + expressionClassName + ")"
						+ expressionString;
			}
		}

		return expressionString;
	}

	public static String getFromWrapperClassString(String expressionString,
			Class<?> expressionClass, String contextFlag) {
		if (expressionClass != null) {
			StringBuffer sb = new StringBuffer();
			if (expressionClass.isPrimitive()) {
				String wrapperClassName = getWrapperConvertClassName(expressionClass);
				String wrapperConvertMethod = getWrapperConvertMethod(expressionClass);
				if (wrapperClassName.equals(expressionString)) {
					sb.append("(").append(contextFlag).append(expressionString).append(").");
					sb.append(wrapperConvertMethod).append("()");
				} else {
					sb.append("((").append(wrapperClassName).append(")");
					sb.append(contextFlag).append(expressionString).append(").");
					sb.append(wrapperConvertMethod).append("()");
				}
			} else {
				sb.append("(").append(VariableUtil.getVariableClassName(expressionClass)).append(")");
				sb.append(contextFlag).append(expressionString);
			}
			
			return sb.toString();
		} else {
			return expressionString;
		}

	}

	/**
	 * get the get method expression for a ui componnet
	 * 
	 * @param componentId
	 *            the ui component id
	 * @param level
	 *            the ui component level
	 * @return the get method expression
	 */
	public static String getUIComponentGetMethodString(String componentId,
			int level) {
		StringBuffer path = new StringBuffer();

		for (StringTokenizer tokens = new StringTokenizer(componentId, "."); tokens
				.hasMoreTokens();) {
			String componentName = tokens.nextToken();

			path.append("get");
			path.append(VariableUtil.getVariableBeanName(componentName));
			path.append("(");
			if (!tokens.hasMoreTokens() && level > 1) {
				path.append("i1");
			}
			path.append(")");
			if (tokens.hasMoreTokens()) {
				path.append(".");
			}
		}

		return path.toString();
	}

}
