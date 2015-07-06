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
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;




/**
 * The class for argument expression node
 *
 * @author Xiao Yi
 */

public class Argument extends ExpressionNode
{
    public Argument()
    {
        super("Argument");
        argClassNames = new ArrayList();
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		setValueClass(parent.getValueClass());

		argClassNames.clear();

		if(getChildNum() > 0)
		{
		    ArgumentList argList = (ArgumentList)getChild(0);
		    int argNum = argList.getChildNum();
		    for(int i = 0; i < argNum; i++)
		    {
		        ExpressionNode node = argList.getChild(i);
		        Class argClass = node.checkType(context);
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
        
		if(getChildNum() > 0)
		{
		    ArgumentList argList = (ArgumentList)getChild(0);
		    for(int i = 0, argNum = argList.getChildNum(); i < argNum; i++)
		    {
		        ExpressionNode node = argList.getChild(i);
		        node.evaluate(context);
		    }
		}
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
		buffer.appendSeperator(this, "(");
		if(getChildNum() > 0)
		{
		    ArgumentList argList = (ArgumentList)getChild(0);
		    buffer.appendExpressionNode(argList);
		}
		buffer.appendSeperator(this, ")");
	}
	
	/*
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		if(getChildNum() > 0)
		{
		    ArgumentList argList = (ArgumentList)getChild(0);
		    buffer.append(argList.toString());
		}
		buffer.append(")");
		
		return buffer.toString();
	}
	*/
	
	public List getArgClasses() throws ParsingException
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
	
    /* the names of class types of the argument list */
    private List argClassNames;
    
    static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
