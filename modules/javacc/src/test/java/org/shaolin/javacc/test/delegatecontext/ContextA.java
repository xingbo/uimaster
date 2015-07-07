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
package org.shaolin.javacc.test.delegatecontext;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.shaolin.javacc.context.*;
import org.shaolin.javacc.exception.*;

//ooee

public class ContextA  implements ParsingContext, EvaluationContext
{
	private String variableA;
	
	private ICacheableContext context ;
	
	public ContextA()
	{
		variableA = null;
		context = new DefaultCacheableContext();
	}
	
	/**
	 *  Get the definition type class of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @return     the variable type class
	 *  @throws     ParsingException     if can't find the variable with this name
	 */
	public Class getVariableClass(String name) throws ParsingException
	{
		if("var".equals(name))
		{
			return String.class;
		}
		else
		{
			throw new ParsingException("Can't find variable " + name);
		}
	}
	
	/**
	 *	Get all variable names in this parsing context
	 *
	 *	@return		a list of string represents all variable names in this parsing context
	 */
	public Collection getAllVariableNames()
	{
		ArrayList allVars = new ArrayList();
		allVars.add("var");
		return allVars;
	}

	/**
	 *  Get the value of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @return     the variable value object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
	public Object getVariableValue(String name) throws EvaluationException
	{
		if("var".equals(name))
		{
			return variableA;
		}
		else
		{
			throw new EvaluationException("Can't find variable " + name);
		}
	}

	/**
	 *  Set the value of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @param      value   the variable value object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
	public void setVariableValue(String name, Object value) throws EvaluationException
	{
		if("var".equals(name))
		{
			variableA = (String)value;
		}
		else
		{
			throw new EvaluationException("Can't find variable " + name);
		}
	}
	
	public Method findMethod(String name, List argClasses) throws ParsingException
	{
		Method foundMethod = null;
		
		if("setValue".equals(name))
		{
			if(argClasses.size() == 1)
			{
				Class argClass = (Class)argClasses.get(0);
				if(argClass == String.class)
				{
					try
					{
						foundMethod = ContextA.class.getMethod("setVarAValue", new Class[]{String.class});
					}
					catch(NoSuchMethodException ex)
					{
						//probably won't happen
					}
				}
			}
		}

		if(foundMethod == null)
		{
			throw new ParsingException("Can't find function " + name);
		}
				
		return foundMethod;
	}

	/**
	 *  Invoke a method with the the specified name and specified argument type classes and objects
	 *
	 *  @param      name    the method name
	 *  @param      argClasses     list of argument type classes
	 *  @param      argObjects     list of argument objects
	 *  @return     the invocation result object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
	public Object invokeMethod(String name, List argClasses, List argObjects) throws EvaluationException
	{
		boolean foundMethod = false;
		
		if("setValue".equals(name))
		{
			if(argClasses.size() == 1)
			{
				Class argClass = (Class)argClasses.get(0);
				if(argClass == String.class)
				{
					foundMethod = true;
				}
			}
		}
		
		if(!foundMethod)
		{
			throw new EvaluationException("Can't find function " + name);
		}
		else
		{
			String argValue = (String)argObjects.get(0);
			setVarAValue(argValue);
		}
		
		return null;
	}

	public void setVarAValue(String value)
	{
		variableA = value;
	}

    public Object getClassObject(String className) throws ParsingException
    {
        return context.getClassObject(className);
    }

    public void putClassObject(String className, Class obj)
    {
        context.putClassObject(className, obj);
    }
}
