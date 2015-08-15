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
package org.shaolin.uimaster.page.od;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.page.CallComponentMappingOp;
import org.shaolin.bmdp.datamodel.page.OpType;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionEvaluator;
import org.shaolin.javacc.ExpressionParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.uimaster.page.javacc.UIVariableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODContextHelper 
{
    private static Logger logger = LoggerFactory.getLogger(ODContextHelper.class);
    
    /**
     * if returned null, please skip operation.
     * TODO: need to improve this method! it's probably exist one more CallComponentMappingOp objects 
     * and it isn't only existing in the first position of array.
     *
     * @param ops
     * @return
     */
	public static CallComponentMappingOp getIsCallAllMapping(List<OpType> ops)
	{
		if(ops.size() == 1 && ops.get(0) instanceof CallComponentMappingOp)
		{
        	CallComponentMappingOp cMappingOp = (CallComponentMappingOp)ops.get(0);
        	List<String> mappingNames = cMappingOp.getMappingNames();
        	if(mappingNames.size() == 0)
        		return cMappingOp;
		}
		return null;
	}
    
    /**
     * 
     * @param variables
     * @return
     */
    public static DefaultParsingContext getParsingContext(List<ParamType> variables) throws ClassNotFoundException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("Create a parsing context for variable types.");
    	
    	DefaultParsingContext parsingContext = new DefaultParsingContext();
        if (variables.size() == 0)
            return parsingContext;

        for (ParamType variable: variables)
        {
			Class<?> clazz = getVariableClass(variable);
        	if (clazz != null)
    		{
        		parsingContext.setVariableClass( variable.getName(), clazz);
    			if(logger.isDebugEnabled())
					logger.debug("Variable name: {}, class: {}",
							new Object[] { variable.getName(), clazz.getName() });
    		} else {
				throw new I18NRuntimeException(
						ExceptionConstants.EBOS_ODMAPPER_063, new Object[] {
								variable.getName(),
								variable.getType().getEntityName() });
    		}
        }
        return parsingContext;
    }

    public static Map getClassMap(List<VariableType> variables)throws ClassNotFoundException
    {
        if (variables.size() == 0)
            return Collections.EMPTY_MAP;
        if(logger.isDebugEnabled())
    		logger.debug("Create a map for variable types.");
    	
        Map classMap = new HashMap();
        for (VariableType variable: variables)
        {
        	Class clazz = getVariableClass(variable);
    		if (clazz != null)
            {
    			classMap.put(variable.getName(), clazz);
            	if(logger.isDebugEnabled())
            		logger.debug("Variable name: {}, class: {}", 
            				new Object[] {variable.getName(), clazz.getName()});
            } else {
				throw new I18NRuntimeException(
						ExceptionConstants.EBOS_ODMAPPER_063, new Object[] {
								variable.getName(),
								variable.getType().getEntityName() });
            }
        }
        return classMap;
    }
    
	public static Class getVariableClass(VariableType variableType) 
	{
		if( variableType == null )
			throw new I18NRuntimeException(ExceptionConstants.EBOS_ODMAPPER_062);
		if( variableType.getCategory() == null )
			throw new I18NRuntimeException(ExceptionConstants.EBOS_ODMAPPER_061,new Object[]{variableType.getName()});
		
		String varClassName = UIVariableUtil.getVariableClassName(variableType);
		try {
			return ExpressionUtil.findClass(varClassName);
		} catch (ParsingException ex) {
			throw new EntityNotFoundException(
					ExceptionConstants.EBOS_COMMON_007, ex,
					new Object[] { varClassName });
		}
	}
    
    /**
     * 
     * @param variables
     * @param value
     * @return
     * @throws EvaluationException 
     */
    public static DefaultEvaluationContext getEvalContext(List<ParamType> variables, Map value) throws EvaluationException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("Create a evaluation context for variable types.");
    	
    	DefaultEvaluationContext evalContext = new DefaultEvaluationContext();
        if (variables.size() == 0)
            return evalContext;

        for (VariableType variable: variables)
        {
        	String key = variable.getName();
            evalContext.setVariableValue(key, value.get(key));
            if(logger.isDebugEnabled())
        	{
        		logger.debug("Variable name: {}, value: {}", 
        				new Object[]{key, value.get(key)});
        	}
        }

        return evalContext;
    }
    
    public static Object evalVariableExpression(VariableType variable) throws EvaluationException, ParsingException
    {
    	ExpressionType expressionType = variable.getDefault();
        if ( expressionType == null || expressionType.getExpressionString().length() == 0 )
        {
        	return BEUtil.getDefaultValueOfPrimitiveType(variable.getType().getEntityName());
        }
        else
        {
            OOEEContext context = OOEEContextFactory.createOOEEContext();
            
            DefaultEvaluationContext evaluationContext = new DefaultEvaluationContext();
            context.setDefaultEvaluationContext(evaluationContext);
            
            DefaultParsingContext parsingContext = new DefaultParsingContext();
            context.setDefaultParsingContext(parsingContext);
            
            Class clazz = VariableUtil.getVariableClass(variable);
            parsingContext.setVariableClass(variable.getName(), clazz);
            
            // TODO: performance issue.
            Expression expr = ExpressionParser.parse(expressionType.getExpressionString(), context);
            return ExpressionEvaluator.evaluate(expr, context);
        }
    }
    
    public static Object evalExpression(ExpressionType expression, ParsingContext parsingContext,
    		EvaluationContext evaluationContext )
    throws EvaluationException, ParsingException
    {
    	OOEEContext context = OOEEContextFactory.createOOEEContext();
    	context.setDefaultEvaluationContext(evaluationContext);
    	context.setDefaultParsingContext(parsingContext);
    	
    	// TODO: performance issue.
    	Expression expr = ExpressionParser.parse(expression.getExpressionString(), context);
        return ExpressionEvaluator.evaluate(expr, context);
    }
    
    public static String evalDataLocale(ExpressionType dLocaleExpr,
            ODContext ctxt, String tag, List localeConfigs) throws EvaluationException, ParsingException
    {
        if (dLocaleExpr != null)
        {
            Object obj = evalExpression(dLocaleExpr, ctxt, ctxt);
            if (obj != null)
            {
                return obj.toString();
            }
        }
        if (localeConfigs != null)
        {
            EvaluationContext eCtxt = ctxt.getEvaluationContextObject(tag);
            for (int i = 0, n = localeConfigs.size(); i < n; i++)
            {
                String[] localeConfig = (String[])localeConfigs.get(i);

                String varName = localeConfig[0];
                Object varValue = eCtxt.getVariableValue(varName);
                if (varValue != null)
                {
                    String fieldName = localeConfig[1];
                    Object fieldValue = getFieldValue(varValue, fieldName);
                    
                    if (fieldValue != null)
                    {
                        return fieldValue.toString();
                    }
                }
            }
        }
        return LocaleContext.getDataLocale();
    }

	private static Object getFieldValue(Object obj, String fieldName) {
		Field field = getField(obj.getClass(), fieldName);
		if (field == null) {
			return null;
		}

		try {
			return field.get(obj);
		} catch (IllegalAccessException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static Field getField(Class clazz, String fieldName) {
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				return field;
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}

			clazz = clazz.getSuperclass();
		}
		return null;
	}

}
