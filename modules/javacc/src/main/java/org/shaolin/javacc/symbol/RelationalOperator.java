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
public class RelationalOperator extends Operator
{
	public RelationalOperator(String type)
	{
		super(type);		
	}
	
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		ExpressionNode lChild = (ExpressionNode)getChild(0);
		ExpressionNode rChild = (ExpressionNode)getChild(1);
		
		Class lClass = lChild.checkType(context);
		Class rClass = rChild.checkType(context);
		
		if(!ExpressionUtil.isNumeric(lClass) || !ExpressionUtil.isNumeric(rClass))
		{
			throw new ParsingException(ExceptionConstants.EBOS_OOEE_074,new Object[]{type,lClass,rClass});
			//throw new ParsingException("operator " + type + " can't be applied to " + lClass + " and " + rClass );
		}
		
		setValueClass(boolean.class);

        //optimization code
        if(lChild.isConstant() && rChild.isConstant())
        {
    		super.isConstant = true;
    		Object lResult = lChild.getConstantValue();
    		Object rResult = rChild.getConstantValue();
    		Object result = calculateRelationalResult(lResult, rResult);
    		super.setConstantValue(result);
        }
        else
        {
            super.isConstant = false;
        }

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
		lChild.evaluate(context);
		Object lResult = context.stackPop();
		
		ExpressionNode rChild = (ExpressionNode)getChild(1);
		rChild.evaluate(context);
		Object rResult = context.stackPop();

        Object valueObject = calculateRelationalResult(lResult, rResult);
        context.stackPush(valueObject);
    }

    private Object calculateRelationalResult(Object lResult, Object rResult)
    {
        Object resultObject;		
		boolean result = false;
		double lValue = ((Number)lResult).doubleValue();
		double rValue = ((Number)rResult).doubleValue();
		
		if(type.equals(LARGER_STRING))
		{
			result = lValue > rValue;
		}
		else if(type.equals(LE_STRING))
		{
			result = lValue >= rValue;
		}
		else if(type.equals(SMALLER_STRING))
		{
			result = lValue < rValue;
		}
		else if(type.equals(SE_STRING))
		{
            result = lValue <= rValue;
		}
		
		resultObject = result ? Boolean.TRUE : Boolean.FALSE;
		
		return resultObject;
	}


	public final static String  LARGER_STRING = ">";
	public final static String  LE_STRING = ">=";
	public final static String  SMALLER_STRING = "<";
	public final static String  SE_STRING = "<=";
	
	static final long serialVersionUID = 0xEF8D8749650DA2F8L;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
