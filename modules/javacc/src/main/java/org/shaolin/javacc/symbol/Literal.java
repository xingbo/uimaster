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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;




/**
 * The class for literal constant
 *
 * @author Xiao Yi
 */
public class Literal extends ExpressionNode
{
    public Literal()
    {
		super("Literal");
		super.isConstant = true;
    }
    
    public Literal(String value)
    {
    	this();
    	
    	if("null".equals(value))
    	{
	        setValueClass(null);
	        setConstantValue(null);
    	}
    	else if(value.endsWith("\"") && value.startsWith("\""))
    	{
    		setValueClass(String.class);
    		setConstantValue(value.substring(1, value.length() - 1));
    	}
    	else if(value.endsWith("'") && value.startsWith("'"))
    	{
    		setValueClass(char.class);
    		setConstantValue(new Character(value.charAt(1)));
    	}
    	else if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
    	{
    		setValueClass(boolean.class);
    		setConstantValue(Boolean.valueOf(value));
    	}
    	else if(value.indexOf(".") != -1)
    	{
    		if(value.endsWith("f") || value.endsWith("F"))
    		{
    			setValueClass(float.class);
    			setConstantValue(Float.valueOf(value));
    		}
    		else {
    			setValueClass(double.class);
    			setConstantValue(Double.valueOf(value));
    		}
    	}
    	else if(value.endsWith("l") || value.endsWith("L")){
    		setValueClass(long.class);
    		setConstantValue(Long.valueOf(value));
    	}
    	else {
    		setValueClass(int.class);
    		setConstantValue(Integer.valueOf(value));
    	}
	}    	

	public void setConstantValue(Object constantValue)
	{
		super.setConstantValue(constantValue);
	}
	
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		return super.getValueClass();
	}
	
	protected void evaluateNode(OOEEEvaluationContext context) throws EvaluationException
	{
		Object valueObject = getConstantValue();
		
		context.stackPush(valueObject);
	}
	
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
		String toString;
		
		Object constantValue = super.getConstantValue();
		
		if(constantValue != null)
		{
		    toString = constantValue.toString();
		    
		    try
		    {
    		    Class valueClass = getValueClass(); 
    		    if(valueClass == String.class)
    		    {
    		        toString = "\"" + toString + "\"";
    		    }
    		    else
    		    if(valueClass == char.class)
    		    {
    		        toString = "'" + toString + "'";
    		    }
    		}
    		catch(ParsingException e)
    		{
    		}
		}
		else
		{
		    toString = "null";
		}
		
	    buffer.appendSeperator(this, toString);
	}
	
	/*
	public String toString()
	{
		String toString;
		
		Object constantValue = super.getConstantValue();
		
		if(constantValue != null)
		{
		    toString = constantValue.toString();
		    
		    try
		    {
    		    Class valueClass = getValueClass(); 
    		    if(valueClass == String.class)
    		    {
    		        toString = "\"" + toString + "\"";
    		    }
    		    else
    		    if(valueClass == char.class)
    		    {
    		        toString = "'" + toString + "'";
    		    }
    		}
    		catch(ParsingException e)
    		{
    		}
		}
		else
		{
		    toString = "null";
		}
		
		
		return toString;
	}
	*/
	
	static final long serialVersionUID = 0xE68F159B46044A82L;

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
