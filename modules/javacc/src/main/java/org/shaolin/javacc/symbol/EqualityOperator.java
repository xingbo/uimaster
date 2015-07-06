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
 * The class for relation operator
 *
 * @author Xiao Yi
 */
public class EqualityOperator extends Operator
{
	public EqualityOperator(String type)
	{
		super(type);
		operandType = -1;
	}
	
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		ExpressionNode lChild = (ExpressionNode)getChild(0);
		ExpressionNode rChild = (ExpressionNode)getChild(1);
		
		Class lClass = lChild.checkType(context);
		Class rClass = rChild.checkType(context);
		
		if(ExpressionUtil.isNumeric(lClass) && ExpressionUtil.isNumeric(rClass))
		{
			operandType = NUMERIC_TYPE;
		}
		else
		if(lClass == boolean.class && rClass == boolean.class)
		{
			operandType = BOOLEAN_TYPE;
		}
		else
		if(ExpressionUtil.isReferenceType(lClass) && ExpressionUtil.isReferenceType(rClass))
		{
			operandType = REFERENCE_TYPE;
		}
		else
		{
			throw new ParsingException(ExceptionConstants.EBOS_OOEE_076,new Object[]{lClass,rClass});
			//throw new ParsingException(lClass + " and " + rClass + " can't be operands of operator " + type);
		}
		
		//optimization code
		if(lChild.isConstant() && rChild.isConstant())
		{
		    super.isConstant = true;
		    Object lResult = lChild.getConstantValue();
		    Object rResult = rChild.getConstantValue();
		    Object result = calculateEqualityResult(lResult, rResult);
		    
		    super.setConstantValue(result);
		}
		else
		{
		    super.isConstant = false;
		}
		
		setValueClass(boolean.class);
		
		return boolean.class;
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
		ExpressionNode rChild = (ExpressionNode)getChild(1);
		
		lChild.evaluate(context);
		Object lResult = context.stackPop();
		
		rChild.evaluate(context);
		Object rResult = context.stackPop();
		
        context.stackPush(calculateEqualityResult(lResult, rResult));
    }
    
    private Object calculateEqualityResult(Object lResult, Object rResult)
    {
        Object resultObject;
        
        boolean result;
		
		if(operandType == NUMERIC_TYPE)
		{
			double lValue = ((Number)lResult).doubleValue();
			double rValue = ((Number)rResult).doubleValue();
			result = (lValue == rValue);
		}
		else
		if(operandType == BOOLEAN_TYPE)
		{
			boolean lValue = ((Boolean)lResult).booleanValue();
			boolean rValue = ((Boolean)rResult).booleanValue();
			result = (lValue == rValue);
		}
		else
		{
			result = (lResult == rResult);
		}
		
		if(type.equals(NOTEQUAL_STRING))
		{
			result = !result;
		}

        resultObject = result? Boolean.TRUE : Boolean.FALSE;
        		
		return resultObject;
	}

	private int operandType;
	
	public static final String  EQUAL_STRING = "==";
	public static final String  NOTEQUAL_STRING = "!=";
	
	private static final int NUMERIC_TYPE = 0;
	private static final int BOOLEAN_TYPE = 1;
	private static final int REFERENCE_TYPE = 2;
	
	static final long serialVersionUID = 0xEF8D8749650DA2F8L;

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
