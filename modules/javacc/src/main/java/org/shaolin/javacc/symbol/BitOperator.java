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
 * The class for bit operator
 *
 * @author Xiao Yi
 */
public class BitOperator extends Operator
{
    public BitOperator(String type)
    {
        super(type);
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode lChild = (ExpressionNode)getChild(0);
        ExpressionNode rChild = (ExpressionNode)getChild(1);
        
        Class lClass = lChild.checkType(context);
        Class rClass = rChild.checkType(context);
        
        if(ExpressionUtil.isReferenceType(lClass) || ExpressionUtil.isReferenceType(rClass))
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_074,new Object[]{type,lClass,rClass});
        	//throw new ParsingException("operator " + type + " can't be applied to " + lClass + " and " + rClass);
        }
        else
        if(!ExpressionUtil.isNumeric(lClass) || !ExpressionUtil.isNumeric(rClass))
        {
        	if(lClass == boolean.class && rClass == boolean.class)
        	{
        		setValueClass(boolean.class);
        	}
        	else
        	{
        		throw new ParsingException(ExceptionConstants.EBOS_OOEE_074,new Object[]{type,lClass,rClass});
        		//throw new ParsingException("operator " + type + " can't be applied to " + lClass + " and " + rClass);
        	}
        }
        else
        {
        	setValueClass(ExpressionUtil.getNumericReturnClass(lClass, rClass));
        	String returnName = getValueClassName();
        	if(returnName.equals("double") || returnName.equals("float"))
        	{
        		throw new ParsingException(ExceptionConstants.EBOS_OOEE_074,new Object[]{type,lClass,rClass});
        		//throw new ParsingException("operator " + type + " can't be applied to " + lClass + " and " + rClass);
        	}
        }
        
        //optimization
        if(lChild.isConstant() && rChild.isConstant())
        {
            super.isConstant = true;
            Object lObject = lChild.getConstantValue();
            Object rObject = rChild.getConstantValue();
            Object value = calculateBitResult(lObject, rObject);
            super.setConstantValue(value);
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

        ExpressionNode lchild = (ExpressionNode)getChild(0);
        ExpressionNode rchild = (ExpressionNode)getChild(1);
        
        lchild.evaluate(context);
        
        Object lObject = context.stackPop();
        
        rchild.evaluate(context);
        
        Object rObject = context.stackPop();
	    
        context.stackPush(calculateBitResult(lObject, rObject));
    }
    
    private Object calculateBitResult(Object lObject, Object rObject)
    {
		Object valueObject = null;
		
		if(super.valueClass == boolean.class)
		{
			boolean lValue = ((Boolean)lObject).booleanValue();
			boolean rValue = ((Boolean)rObject).booleanValue();
			boolean result = false;
			
			if(type.equals(BIT_XOR_STRING))
			{
				result = lValue ^ rValue;
			}
			else
			if(type.equals(BIT_AND_STRING))
			{
				result = lValue & rValue;
			}
			else
			if(type.equals(BIT_OR_STRING))
			{
				result = lValue | rValue;
			}
		
			valueObject = result? Boolean.TRUE : Boolean.FALSE;
		}
		else		
		if(super.valueClass == int.class)
		{
			int lValue = ((Integer)lObject).intValue();
			int rValue = ((Integer)rObject).intValue();
			int result = 0;
			
			if(type.equals(BIT_XOR_STRING))
			{
				result = lValue ^ rValue;
			}
			else
			if(type.equals(BIT_AND_STRING))
			{
				result = lValue & rValue;
			}
			else
			if(type.equals(BIT_OR_STRING))
			{
				result = lValue | rValue;
			}
		
			valueObject = new Integer(result);
		}
		else
		if(super.valueClass == long.class)
		{
			long lValue = ((Long)lObject).longValue();
			long rValue = ((Long)rObject).longValue();
			long result = 0;
			
			if(type.equals(BIT_XOR_STRING))
			{
				result = lValue ^ rValue;
			}
			else
			if(type.equals(BIT_AND_STRING))
			{
				result = lValue & rValue;
			}
			else
			if(type.equals(BIT_OR_STRING))
			{
				result = lValue | rValue;
			}
		
			valueObject = new Long(result);
		}
			    
	    return valueObject;
	}    

	private static final String BIT_XOR_STRING = "^";
	private static final String BIT_AND_STRING = "&";	
	private static final String BIT_OR_STRING = "|";
	
	static final long serialVersionUID = 0x66532D0C6B7FB005L;    

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
