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
 * The class for logic operator
 *
 * @author Xiao Yi
 */
public class LogicOperator extends Operator
{
    public LogicOperator(String type)
    {
        super(type);
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
		ExpressionNode lChild = (ExpressionNode)getChild(0);
		Class childClass = lChild.checkType(context);
        if(childClass != boolean.class)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_049,new Object[]{type});
           // throw new ParsingException("Operands of Logic Operator " + type + " must be boolean");
        }

		ExpressionNode rChild = (ExpressionNode)getChild(1);
		childClass = rChild.checkType(context);
        if(childClass != boolean.class)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_049,new Object[]{type});
           //throw new ParsingException("Operands of Logic Operator " + type + " must be boolean");
        }

        setValueClass(boolean.class);
        
        //optimization code
        if(lChild.isConstant() && rChild.isConstant())
        {
    		super.isConstant = true;
    		Object lObject = lChild.getConstantValue();
    		Object rObject = rChild.getConstantValue();
    		Object result = calculateLogicResult(lObject, rObject);
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
		ExpressionNode rChild = (ExpressionNode)getChild(1);

		lChild.evaluate(context);
		Object lResult = context.stackPop();

		if(!(lResult instanceof Boolean))
		{
			throw new EvaluationException(ExceptionConstants.EBOS_OOEE_008,new Object[]{lChild.toString(),lResult});
			//throw new EvaluationException("Expect boolean type value in Logic Operator for " + lChild.toString() + ", but get " + lResult);
		}
		
		boolean lValue = ((Boolean)lResult).booleanValue();
		
		Object valueObject;
		if((type.equals(AND_STRING) && !lValue) || (type.equals(OR_STRING)&& lValue))
		{
			valueObject = lResult;
		}
		else
		{
			rChild.evaluate(context);
			Object rResult = context.stackPop();
			
			if(!(rResult instanceof Boolean))
			{
				throw new EvaluationException(ExceptionConstants.EBOS_OOEE_007,new Object[]{rChild.toString(),rResult});
			//	throw new EvaluationException("Expect boolean type value in Logic Operator " + rChild.toString() + ", but get " + rResult);
			}
			
			valueObject = calculateLogicResult(lResult, rResult);
		}

		context.stackPush(valueObject);
	}

    private Object calculateLogicResult(Object lObject, Object rObject)
    {
        Object returnObject;
        
        boolean returnValue;

		boolean lResult = ((Boolean)lObject).booleanValue();
		boolean rResult = ((Boolean)rObject).booleanValue();
        
        if(type.equals(AND_STRING))
       	{
       	    returnValue = lResult && rResult;
       	}
       	else
       	{
       	    returnValue = lResult || rResult;
       	}
       	
		returnObject = returnValue? Boolean.TRUE : Boolean.FALSE;
		
       	return returnObject;
	}
  
	public static final String  AND_STRING = "&&";
	public static final String  OR_STRING = "||";
	
	static final long serialVersionUID = 0x66532D0C6B7FB005L;    

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
