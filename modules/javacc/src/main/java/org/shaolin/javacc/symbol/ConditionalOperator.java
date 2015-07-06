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
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;




/**
 * The class for conditional expression node
 *
 * @author Xiao Yi
 */

public class ConditionalOperator extends Operator
{
    public ConditionalOperator()
    {
        super("ConditionalOperator");
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode condition = getChild(0);
        
        Class conditionClass = condition.checkType(context);
        
        boolean isConditionConstant = condition.isConstant();
        
        if(conditionClass != boolean.class)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_067,new Object[]{condition.toString()});
        }
        
        ExpressionNode trueNode = getChild(1);
        ExpressionNode falseNode = getChild(2);
        
        Class trueClass = trueNode.checkType(context);
        Class falseClass = falseNode.checkType(context);

        boolean isTrueConstant = trueNode.isConstant();
        boolean isFalseConstant = falseNode.isConstant();
        
        if(trueClass == falseClass)
        {
        	setValueClass(trueClass);
        }
        else
        if(ExpressionUtil.isNumeric(trueClass) && ExpressionUtil.isNumeric(falseClass))
        {
        	if((trueClass == short.class && falseClass == byte.class) ||
        	   (falseClass == short.class && trueClass == byte.class))
        	{
        		setValueClass(short.class);
        	}
        	else
        	{
        		int precision1 = ExpressionUtil.getNumericPrecision(trueClass);
        		int precision2 = ExpressionUtil.getNumericPrecision(falseClass);
        		if(precision1 < ExpressionUtil.INTEGER_PRECISION && isFalseConstant && falseClass == int.class)
        		{
        		    setValueClass(trueClass);
        		}
        		else
        		if(precision2 < ExpressionUtil.INTEGER_PRECISION && isTrueConstant && trueClass == int.class)
        		{
        			setValueClass(falseClass);
        		}
        		else
        		{
        			setValueClass(ExpressionUtil.getNumericReturnClass(trueClass, falseClass));
        		}
        	}
        }
        else
        if(trueClass == null)
        {
        	setValueClass(falseClass);
        }
        else
        if(falseClass == null)
        {
        	setValueClass(trueClass);
        }
		else
		if(trueClass.isAssignableFrom(falseClass))
		{
			setValueClass(trueClass);
		}
		else
		if(falseClass.isAssignableFrom(trueClass))
		{
			setValueClass(falseClass);
		}
		else
		{
			throw new ParsingException(ExceptionConstants.EBOS_OOEE_075,new Object[]{trueClass,falseClass});
			//throw new ParsingException("wrong class for conditional operator between " + trueClass + " and " + falseClass);
		}
		
		//optimization code
		if(isConditionConstant && isTrueConstant && isFalseConstant)
		{
		    super.isConstant = true;
		    boolean conditionValue = ((Boolean)condition.getConstantValue()).booleanValue();
		    Object constantValue;
		    if(conditionValue)
		    {
		        constantValue = trueNode.getConstantValue();
		    }
		    else
		    {
		        constantValue = falseNode.getConstantValue();
		    }
		    super.setConstantValue(constantValue);
		}
		else
		{
		    super.isConstant = false;
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
		
        ExpressionNode condition = getChild(0);
        
        condition.evaluate(context);
        
        Object valueObject = context.stackPop();
		
		boolean choice = ((Boolean)valueObject).booleanValue();
		
		ExpressionNode runNode;
		if(choice)
		{
	        runNode = getChild(1);
		}
		else
		{
	        runNode = getChild(2);
		}
		
		runNode.evaluate(context);
		
		valueObject = context.stackPop();
		
		try
		{
        	if(ExpressionUtil.isNumeric(valueClass))
        	{
        	    valueObject = ExpressionUtil.getNumericReturnObject(valueObject, valueClass);
        	}
        }
		catch(ParsingException e)
		{
			throw new EvaluationException(ExceptionConstants.EBOS_000,e);
		}
		
		context.stackPush(valueObject);
    }
    
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        ExpressionNode condition = getChild(0);
        ExpressionNode trueNode = getChild(1);
        ExpressionNode falseNode = getChild(2);
        
		buffer.appendSeperator(this, "(");
		buffer.appendSeperator(this, "(");
        buffer.appendExpressionNode(condition);
        buffer.appendSeperator(this, ")");
        buffer.appendSeperator(this, "?");
        buffer.appendSeperator(this, "(");
        buffer.appendExpressionNode(trueNode);
        buffer.appendSeperator(this, ")");
        buffer.appendSeperator(this, " : ");
        buffer.appendSeperator(this, "(");
        buffer.appendExpressionNode(falseNode);
        buffer.appendSeperator(this, ")");
        buffer.appendSeperator(this, ")");
	}
	
    /*
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        ExpressionNode condition = getChild(0);
        ExpressionNode trueNode = getChild(1);
        ExpressionNode falseNode = getChild(2);
        
		buffer.append("(");
		buffer.append("(");
        buffer.append(condition.toString());
        buffer.append(")");
        buffer.append("?");
        buffer.append("(");
        buffer.append(trueNode.toString());
        buffer.append(")");
        buffer.append(" : ");
        buffer.append("(");
        buffer.append(falseNode.toString());
        buffer.append(")");
        buffer.append(")");
        
        return buffer.toString();
    }
 	*/
 	   
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
