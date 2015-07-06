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
 * The class for "++" and "--" operator
 *
 * @author Xiao Yi
 */
public class IncrDecrOperator extends Operator
{
    public IncrDecrOperator(String type)
    {
        super(type);
        isPrefix = false;
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode child = (ExpressionNode)getChild(0);
        
        setValueClass(child.checkType(context));
        
        if(!child.isVariable())
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_048,new Object[]{type});
         //   throw new ParsingException("Operand of Operator " + type + " must be variable, not value");
        }
        
        child.setIsValueAssignee(true);
        
	    if(!ExpressionUtil.isNumeric(valueClass))
	    {
	    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_047,new Object[]{type});
	     //   throw new ParsingException("Operand of Operator " + type + " must be number");
	    }
        
        setValueClass(ExpressionUtil.getNumericReturnClass(valueClass, int.class));
        
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
        
        try
        {
            valueObject = ExpressionUtil.getNumericReturnObject(valueObject, valueClass);
        }
        catch(ParsingException e)
        {
            throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }

        Object newObject = null;
        if (valueObject instanceof Character)
        {
            char c = ((Character)valueObject).charValue();
            newObject = new Character(isIncrement() ? ++c : --c);
        }
        else
        {
            Number numberObject = (Number)valueObject;
            if (valueObject instanceof Integer)
            {
                int i = numberObject.intValue();
                newObject = new Integer(isIncrement() ? ++i : --i);
            }
            else if (valueObject instanceof Long)
            {
                long l = numberObject.longValue();
                newObject = new Long(isIncrement() ? ++l : --l);
            }
            else if (valueObject instanceof Float)
            {
                float f = numberObject.floatValue();
                newObject = new Float(isIncrement() ? ++f : --f);
            }
            else if (valueObject instanceof Double)
            {
                double d = numberObject.doubleValue();
                newObject = new Double(isIncrement() ? ++d : --d);
            }
            else if (valueObject instanceof Byte)
            {
                byte b = numberObject.byteValue();
                newObject = new Byte(isIncrement() ? ++b : --b);
            }
            else
            {
                short s = numberObject.shortValue();
                newObject = new Short(isIncrement() ? ++s : --s);
            }
        }
        
        child.setVariableValue(context, newObject);
        
        if (isPrefix)
        {
            valueObject = newObject;
        }

        context.stackPush(valueObject);
	}    


	public void setIsPrefix(boolean isPrefix)
	{
	    this.isPrefix = isPrefix;
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        ExpressionNode child = (ExpressionNode)getChild(0);
        
        if(isPrefix)
        {
            buffer.appendSeperator(this, type);
            buffer.appendExpressionNode(child);
        }
        else
        {
            buffer.appendExpressionNode(child);
            buffer.appendSeperator(this, type);
        }
	}
	
	private boolean isIncrement()
	{
	    return INCREASE_STRING.equals(type);
	}

	/*
	public String toString()
	{
        String result;
        
        ExpressionNode child = (ExpressionNode)getChild(0);
        
        result = child.toString();
        
        if(isPrefix)
        {
            result = type + result;    
        }
        else
        {
            result += type;
        }
        
        return result;       
	}
	*/
	
    private boolean isPrefix;
    
    private static final String INCREASE_STRING = "++";
    private static final String DECREASE_STRING = "--";
	static final long serialVersionUID = 0x66532D0C6B7FB005L;    

    public static final String ___REVISION___ = "$Revision: 1.6 $";
}
