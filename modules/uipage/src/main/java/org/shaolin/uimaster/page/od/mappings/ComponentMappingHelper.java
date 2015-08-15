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
import java.util.Map;
import java.util.StringTokenizer;

import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;
import org.shaolin.uimaster.page.exception.ODException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component mapping helper.
 */
public class ComponentMappingHelper 
{
	private static final Logger logger = LoggerFactory.getLogger(ComponentMappingHelper.class);
	
	/**
	 * get component path class.
	 * 
	 * @param componentPath
	 * @param odContext
	 * @return
	 */
	public static Class getComponentPathClass(String componentPath,
			DefaultParsingContext pContext) {
		String expressionString = getIndexedComponentPath(componentPath, false,
				null);
		Class exprClass;
		try {
			CompilationUnit unit = StatementParser.parse(expressionString,
					pContext);
			exprClass = unit.getValueClass();
		} catch (ParsingException ex) {
			logger.error("Fail to get class name for component path '"
					+ componentPath + "' with expression '" + expressionString
					+ "'", ex);
			exprClass = null;
		}
		return exprClass;
	}
	
	/**
	 * if the component path is "a", it would be direct mapping. true
	 * if the component path is "a.b.c", it would be complex mapping. false
	 * 
	 * @param componentPath
	 * @return
	 */
	public static boolean isDirectComponentPath(String componentPath) {
		return (componentPath.indexOf(".") == -1);
	}
	
	/**
	 * suppose the component path is "a.b.c", get 'a' as object name.
	 * 
	 * @param componentPath
	 * @return
	 */
	public static String getObjectNameOfComponentPath(String componentPath) {
		String objectName = componentPath;
		StringTokenizer tokens = new StringTokenizer(componentPath, ".");
		objectName = tokens.hasMoreTokens() ? tokens.nextToken() : objectName;
		return objectName;
	}
	
	/**
	 * suppose the component path is "a.b.c", get 'b.c' as object name.
	 * if no '.', return "". 
	 * 
	 * @param componentPath
	 * @return
	 */
	public static String getSubComponentPath(String componentPath) {
		int start = componentPath.indexOf(".");
		if (start != -1) {
			return componentPath.substring(start + 1);
		}
		return "";
	}
	
	/**
	 * parsing Indexed Component Path to java expression.
	 * three ways in here.
	 * <br>1.component path is "a", expression is a.
	 * <br>2.component path is "a.b", expression is a.getB() / a.setB(param).
	 * <br>3.component path is "a.b.c", expression is a.getB().getC() / a.getB().setC(param).
	 * 
	 * @param componentPath
	 * @param isSet
	 * 		  indicates whether last one is a set expression or a get expression
	 * @param setParam
	 * @return
	 */
	public static String getIndexedComponentPath(String componentPath,
			boolean isSet, String setParam) {
		boolean isFirst = true;
		StringBuffer indexedPath = new StringBuffer();
		StringBuffer currentPath = new StringBuffer();
		for (StringTokenizer tokens = new StringTokenizer(componentPath, "."); tokens
				.hasMoreTokens();) {
			String token = tokens.nextToken();
			if (!isFirst) {
				indexedPath.append(".");
				currentPath.append(".");
			}
			currentPath.append(token);
			boolean genSet = !tokens.hasMoreTokens() && isSet;
			if (isFirst) {
				indexedPath.append(token);
				if (genSet) {
					indexedPath.append(" = ");
					indexedPath.append(setParam);
				}
			} else {
				String beanName = VariableUtil.getVariableBeanName(token);
				if (genSet) {
					indexedPath.append("set");
				} else {
					indexedPath.append("get");
				}
				indexedPath.append(beanName);
				indexedPath.append("(");
				boolean withIndex = false;

				if (genSet) {
					if (withIndex) {
						indexedPath.append(", ");
					}
					indexedPath.append(setParam);
				}
				indexedPath.append(")");
			}
			isFirst = false;
		}
		return indexedPath.toString();
	}
	
	/**
	 * 
	 * @param componentMappings
	 * @return
	 */
	public static Map setComponentMapping(
			ComponentMappingType[] componentMappings) {
		Map componentMappingCache = new HashMap();
		for (int i = 0, n = componentMappings.length; i < n; i++) {
			componentMappingCache.put(componentMappings[i].getName(),
					componentMappings[i]);
		}
		return componentMappingCache;
	}
	
	/**
	 * 
	 * @param expressionStr
	 * @param variableName
	 * @param value
	 * @return
	 * @throws ODException
	 */
	public static Object evalExpression(String expressionStr,
			String variableName, Object value) throws ODException {
		if (logger.isDebugEnabled())
			logger.warn("Evaluation expression: " + expressionStr);

		try {
			DefaultParsingContext parsingContext = new DefaultParsingContext();
			DefaultEvaluationContext evaluationContext = new DefaultEvaluationContext();

			OOEEContext context = OOEEContextFactory.createOOEEContext();
			context.setDefaultEvaluationContext(evaluationContext);
			context.setDefaultParsingContext(parsingContext);

			parsingContext.setVariableClass(variableName, value.getClass());
			evaluationContext.setVariableValue(variableName, value);

			CompilationUnit expr = StatementParser
					.parse(expressionStr, context);
			return expr.execute(context);
		} catch (EvaluationException e) {
			throw new ODException("Evaluate expression exception: "
					+ e.getMessage(), e);
		} catch (ParsingException e) {
			throw new ODException("Evaluate expression exception: "
					+ e.getMessage(), e);
		}
	}
	
	public static Class convertNumbertoPrimitiveType(Class clazz) {
		if (clazz == Long.class)
			return long.class;
		if (clazz == Integer.class)
			return int.class;
		if (clazz == Short.class)
			return short.class;
		if (clazz == Float.class)
			return float.class;
		if (clazz == Byte.class)
			return byte.class;
		if (clazz == Double.class)
			return double.class;
		return clazz;
	}
	
	public static Class getBigPrimitiveClass(Class primitiveClass) {
		Class clazz = null;
		if (primitiveClass == boolean.class)
			clazz = Boolean.class;
		else if (primitiveClass == byte.class)
			clazz = Byte.class;
		else if (primitiveClass == short.class)
			clazz = Short.class;
		else if (primitiveClass == char.class)
			clazz = Character.class;
		else if (primitiveClass == int.class)
			clazz = Integer.class;
		else if (primitiveClass == long.class)
			clazz = Long.class;
		else if (primitiveClass == float.class)
			clazz = Float.class;
		else if (primitiveClass == double.class)
			clazz = Double.class;
		return clazz;
	}
	
}
