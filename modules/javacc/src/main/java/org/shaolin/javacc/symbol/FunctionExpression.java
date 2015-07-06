/*
 * Copyright 2000-2003 by BraveMinds, Inc.,
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of BraveMinds, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BraveMinds.
 */
 
//package
package org.shaolin.javacc.symbol;

//imports
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;



/**
 * The class for function node
 *
 * @author Xiao Yi
 */

public class FunctionExpression extends ExpressionNode
{
    public FunctionExpression()
    {
        super("Function");
        funcName = "";
        methodClassName = null;
        isStatic = false;
        isPlugin = false;
        argClassNames = new ArrayList();
    }
    
	public void setFuncName(String funcName)
	{
		this.funcName = funcName;
	}
	
	public String getFuncName()
	{
		return funcName;
	}
	
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
        FieldExpression parentField = (FieldExpression)parent;
        Class parentClass = parentField.getValueClass();
        
        isStatic = parentField.isClass();
        
        Argument arg = (Argument)getChild(0);
        arg.checkType(context);
        List argClasses = arg.getArgClasses();
        setArgClasses(argClasses);
        
        if(parentClass == null)
        {
            if (this == parentField.getChild(0))
            {
                isPlugin = true;
                try
                {
                    Method method = context.findMethod(funcName, argClasses);
                    setValueClass(method.getReturnType());
                    super.setThrownExceptions(method.getExceptionTypes());
                }
                catch(ParsingException e)
                {
                	throw new ParsingException(ExceptionConstants.EBOS_OOEE_063, e, new Object[]{funcName});
                 //   throw new ParsingException("can't find method " + funcName);                    
                }
            }
            else
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_040,new Object[]{toString()});
                //should never happen
            //    throw new ParsingException("Internal exception:" + toString());
            }
        }
        else
        {
            try
            {
                methodObject = ExpressionUtil.findMethod(parentClass, funcName, isStatic, argClasses);
                methodClassName = parentClass.getName();
                setValueClass(methodObject.getReturnType());
                parentField.setIsClass(false);
                super.setThrownExceptions(methodObject.getExceptionTypes());
            }
            catch(ParsingException e)
            {
                throw e;
            }
        }
            		
		return valueClass;
	}
	
	protected void evaluateNode(OOEEEvaluationContext context) throws EvaluationException
	{
        try
        {
            getValueClass();
        }
        catch(ParsingException e)
        {
            throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }

        List argClasses;
        
        try
        {
            argClasses = getArgClasses();
        }
        catch(ParsingException e)
        {
            throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }
        
        Argument arg = (Argument)getChild(0);
        
        arg.evaluate(context);
        
        List argObjectList = new ArrayList();
        
        for(int i = 0, n = argClasses.size(); i < n; i++)
        {
        	argObjectList.add(0, context.stackPop());
        }
        
		Object valueObject;
		
		if(isPlugin)
		{
		    valueObject = context.invokeMethod(funcName, argClasses, argObjectList);
		}
		else
		{
    		try
    		{
                Object[] argObjects = argObjectList.toArray(new Object[]{});
                if (methodObject == null)
                {
                    Class methodClass = null;
                    try
                    {
                        if(methodClassName != null)
                        {
                            methodClass = ExpressionUtil.findClass(methodClassName);
                        }
                    }
                    catch(ParsingException e)
                    {
                        throw new EvaluationException(ExceptionConstants.EBOS_000,e);
                    }
                    methodObject = ExpressionUtil.findMethod(methodClass, funcName, isStatic, argClasses);
                }
                Object currentObject = null;
                if(!isStatic)
                {
                    currentObject = context.stackPop();
                }
            	valueObject = methodObject.invoke(currentObject, argObjects);
    		}
    		catch(NullPointerException e)
    		{
    		    String parameterInfo = "arguments"+argObjectList.toString()+"";
    			String classInfo = (methodClassName == null?"":(methodClassName+"."))+funcName;
    			EvaluationException evalEx = new EvaluationException(ExceptionConstants.EBOS_OOEE_080,new Object[]{classInfo,parameterInfo});
    			throw new EvaluationException(ExceptionConstants.EBOS_000,evalEx);
    		}
    		catch(InvocationTargetException e)
            {
    		    String parameterInfo = "arguments"+argObjectList.toString()+"";
                String classInfo = (methodClassName == null?"":(methodClassName+"."))+funcName;
                EvaluationException evalEx = new EvaluationException(ExceptionConstants.EBOS_OOEE_081, e, new Object[]{classInfo,parameterInfo});
                throw new EvaluationException(ExceptionConstants.EBOS_000, evalEx);
            }
    		catch(Exception e)
    		{
    		    throw new EvaluationException(ExceptionConstants.EBOS_000,e);
    		}
    	}
    	
		context.stackPush(valueObject);
	}
	
    private void setArgClasses(List argClasses)
    {
        argClassNames.clear();
        for(int i = 0, n = argClasses.size(); i < n; i++)
        {
            Class argClass = (Class)argClasses.get(i);
            if(argClass != null)
            {
                argClassNames.add(argClass.getName());
            }
            else
            {
                argClassNames.add(null);
            }
        }
    }
    
    private List getArgClasses() throws ParsingException
    {
	    List argClasses = new ArrayList();
	    
	    for(int i = 0, n = argClassNames.size(); i < n; i++)
	    {
	        String argClassName = (String)argClassNames.get(i);
	        if(argClassName != null)
	        {
	            Class argClass = ExpressionUtil.findClass(argClassName);
	            argClasses.add(argClass);
	        }
	        else
	        {
	            argClasses.add(null);
	        }
	    }
	    
	    return argClasses;
    }
    
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        buffer.appendSeperator(this, funcName);
   	    ExpressionNode child = getChild(0);
        buffer.appendExpressionNode(child);
	}

    /*
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

       	buffer.append(funcName);
      	
   	    ExpressionNode child = getChild(0);
   	    buffer.append(child.toString());    
       	
        return buffer.toString();
    }
    */
    /* function name */
	private String funcName;
	
	/* the name of the class which method belongs to */
	private String methodClassName;
	
	/* whether the method is static */
	private boolean isStatic;
	
	/* whether this method is a user-defined plugin function */
	private boolean isPlugin;
	
	/* the argument class types */
	private List argClassNames;
	
	/* method object cache */
	private transient volatile Method methodObject;
	
	static final long serialVersionUID = 0xA1B5FCA811A6B17DL;

    public static final String ___REVISION___ = "$Revision: 1.12 $";
}
