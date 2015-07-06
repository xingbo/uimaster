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
 * The class for unary operator
 *
 * @author Xiao Yi
 */
public class UnaryOperator extends Operator
{
	public UnaryOperator(String type)
	{
		super(type);		
	}
	
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode child = (ExpressionNode)getChild(0);
        
        Class childClass = child.checkType(context);
        
		if(type.equals(NOT_STRING))
		{
            if(childClass != boolean.class)
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_053,new Object[]{type});
                //throw new ParsingException("Operands of Unary Operator " + type + " must be boolean");
            }
            setValueClass(boolean.class);
		}
		else
		{
		    if(!ExpressionUtil.isNumeric(childClass))
		    {
		    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_055,new Object[]{type});
		      //  throw new ParsingException("Operands of Unary Operator " + type + " must be number");
		    }
		    
    		if(type.equals(BIT_NOT_STRING))
    		{
    		    if(ExpressionUtil.getNumericPrecision(childClass) > ExpressionUtil.LONG_PRECISION)
    		    {
    		    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_054,new Object[]{type});
		           // throw new ParsingException("Operands of Unary Operator " + type + " must be integeral number");
    		    }
    		}
		    
		    setValueClass(ExpressionUtil.getNumericReturnClass(childClass, childClass));
		}
		
        if(child.isConstant())
        {
            super.isConstant = true;
            Object value = child.getConstantValue();
            Object result = calculateUnaryResult(value);
            super.setConstantValue(result);
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
        
		ExpressionNode child = (ExpressionNode)getChild(0);
		child.evaluate(context);
		
		Object valueObject = context.stackPop();

        valueObject = calculateUnaryResult(valueObject);
        
        context.stackPush(valueObject);
    }
    
    private Object calculateUnaryResult(Object valueObject)
    {
		if (type.equals(NOT_STRING))
		{
			return ((Boolean)valueObject).booleanValue() ? Boolean.FALSE : Boolean.TRUE;
		}
		if (type.equals(UNARY_PLUS_STRING))
		{
		    return valueObject;
		}
		if (valueObject instanceof Character)
		{
		    char c = ((Character)valueObject).charValue();
            if (BIT_NOT_STRING.equals(type))
            {
                return new Integer(~c);
            }
            return new Integer(-c);
		}
        Number numberObject = (Number)valueObject;
        if (valueObject instanceof Double)
        {
            return new Double(-numberObject.doubleValue());
        }
        if (valueObject instanceof Float)
        {
            return new Float(-numberObject.floatValue());
        }
        if (valueObject instanceof Long)
        {
            if (BIT_NOT_STRING.equals(type))
            {
                return new Long(~numberObject.longValue());
            }
            return new Long(-numberObject.longValue());
        }
        else
        {
            if (BIT_NOT_STRING.equals(type))
            {
                return new Integer(~numberObject.intValue());
            }
            return new Integer(-numberObject.intValue());
        }
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
	    buffer.appendSeperator(this, type);
	    
	    ExpressionNode child = getChild(0);
	    buffer.appendExpressionNode(child);
	}
	
	/*
	public String toString()
	{
	    ExpressionNode child = getChild(0);
	    String result = type + child.toString();

	    return result;
	}
	*/
	
	private static final String NOT_STRING = "!";
	private static final String BIT_NOT_STRING = "~";	
	private static final String UNARY_MINUS_STRING = "-";
	private static final String UNARY_PLUS_STRING = "+";
	
	static final long serialVersionUID = 0xDBCF3A60BF8CADADL;

    public static final String ___REVISION___ = "$Revision: 1.5 $";
}
