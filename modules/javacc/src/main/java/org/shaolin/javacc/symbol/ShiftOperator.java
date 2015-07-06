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
import org.shaolin.javacc.util.ExpressionUtil;



/**
 * The class for shift operator
 *
 * @author Xiao Yi
 */
public class ShiftOperator extends Operator
{
    public ShiftOperator(String type)
    {
        super(type);
        shiftClassName = null;
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode lChild = (ExpressionNode)getChild(0);
        ExpressionNode rChild = (ExpressionNode)getChild(1);
        
        setValueClass(lChild.checkType(context));
        Class shiftClass = rChild.checkType(context);
        
        setValueClass(ExpressionUtil.getNumericReturnClass(valueClass, valueClass));
        shiftClass = ExpressionUtil.getNumericReturnClass(shiftClass, shiftClass);
        
        if(!ExpressionUtil.isNumeric(valueClass) || (ExpressionUtil.getNumericPrecision(valueClass) > ExpressionUtil.LONG_PRECISION)
           || !ExpressionUtil.isNumeric(shiftClass) || (ExpressionUtil.getNumericPrecision(shiftClass) > ExpressionUtil.LONG_PRECISION))
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_051,new Object[]{type});
           // throw new ParsingException("operands of operator " + type + " must be integral number");
        }
        
        if(shiftClass != null)
        {
            shiftClassName = shiftClass.getName();
        }
        
        if(lChild.isConstant() && rChild.isConstant())
        {
            super.isConstant = true;
            Object lObject = lChild.getConstantValue();
            Object rObject = rChild.getConstantValue();
            lObject = ExpressionUtil.getNumericReturnObject(lObject, valueClass);
            rObject = ExpressionUtil.getNumericReturnObject(rObject, shiftClass);
            Object result = calculateShiftResult(lObject, rObject);
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

        ExpressionNode lChild = (ExpressionNode)getChild(0);
        lChild.evaluate(context);
        Object valueObject = context.stackPop();
        
        ExpressionNode rChild = (ExpressionNode)getChild(1);
		rChild.evaluate(context);
        Object shiftObject = context.stackPop();
        
        Class shiftClass = null;
        try
        {
            if(shiftClassName != null)
            {
                shiftClass = ExpressionUtil.findClass(shiftClassName);
            }
        }
        catch(ParsingException e)
        {
            throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }

        try
        {
            valueObject = ExpressionUtil.getNumericReturnObject(valueObject, valueClass);
            shiftObject = ExpressionUtil.getNumericReturnObject(shiftObject, shiftClass);
        }
        catch(ParsingException e)
        {
            throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }
        
        valueObject = calculateShiftResult(valueObject, shiftObject);
        
        context.stackPush(valueObject);
    }
    
    private Object calculateShiftResult(Object valueObject, Object shiftObject)
    {
        Object result;
            
        int shift;
        shift = ((Number)shiftObject).intValue();
        
        if(valueClass == int.class)
        {
            int value = ((Number)valueObject).intValue();
            if(type.equals(LEFT_SHIFT_STRING))
            {
                value = value << shift;
            }
            else
            if(type.equals(RIGHT_SHIFT_STRING))
            {
                value = value >> shift;
            }
            else
            if(type.equals(SIGN_RIGHT_SHIFT_STRING))
            {
                value = value >>> shift;
            }
            result = new Integer(value);
        }
        else
        {
            long value = ((Number)valueObject).longValue();
            if(type.equals(LEFT_SHIFT_STRING))
            {
                value = value << shift;
            }
            else
            if(type.equals(RIGHT_SHIFT_STRING))
            {
                value = value >> shift;
            }
            else
            if(type.equals(SIGN_RIGHT_SHIFT_STRING))
            {
                value = value >>> shift;
            }
            result = new Long(value);
        }
        
	    return result;
	}
	
	private String shiftClassName;
	
	private static final String LEFT_SHIFT_STRING = "<<";
	private static final String RIGHT_SHIFT_STRING = ">>";
	private static final String SIGN_RIGHT_SHIFT_STRING = ">>>";

	static final long serialVersionUID = 0x66532D0C6B7FB005L;    

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
