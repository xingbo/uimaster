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
 * The class for assignment "=" operator
 *
 * @author Xiao Yi
 */
public class AssignmentOperator extends Operator
{
    public AssignmentOperator(String type)
    {
        super(type);
        needConvert = false;
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode lChild = (ExpressionNode)getChild(0);
        
        setValueClass(lChild.checkType(context));
        
        if(!lChild.isVariable())
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_041,new Object[]{type});
            //throw new ParsingException("Left Operand of Assignment " + type + " must be variable, not value");
        }
        
        lChild.setIsValueAssignee(true);
        
        ExpressionNode rChild = (ExpressionNode)getChild(1);

        Class rClass = rChild.checkType(context);
        
        if(!ExpressionUtil.isAssignableFrom(valueClass, rClass))
        {
            if(rChild instanceof Literal &&
               ExpressionUtil.isNumeric(valueClass) && ExpressionUtil.isNumeric(rClass)
               && ExpressionUtil.getNumericPrecision(valueClass) < ExpressionUtil.INTEGER_PRECISION
               && ExpressionUtil.getNumericPrecision(rClass) == ExpressionUtil.INTEGER_PRECISION)
            {
                needConvert = true;
            }
            else
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_027,new Object[]{rClass,valueClass});
              //  throw new ParsingException("Can't assign " + rClass + " to " + valueClass);
            }
        }
        else
        {
            if(ExpressionUtil.isNumeric(valueClass) && ExpressionUtil.isNumeric(rClass)
               && ExpressionUtil.getNumericPrecision(valueClass) != ExpressionUtil.getNumericPrecision(rClass))
            {
            	needConvert = true;
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

        ExpressionNode lChild = (ExpressionNode)getChild(0);
        lChild.evaluate(context);
        Object valueObject = context.stackPop();
        
        ExpressionNode rChild = (ExpressionNode)getChild(1);
        rChild.evaluate(context);
        valueObject = context.stackPop();
        
        if(needConvert)
        {
            try
            {
                valueObject = ExpressionUtil.getNumericReturnObject(valueObject, valueClass);
            }
            catch(ParsingException e)
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_000,e);
            }
        }

        lChild.setVariableValue(context, valueObject);
        
        context.stackPush(valueObject);
	}    

	/* indicate whether it needs conversion in numeric assignment from int to short, char, byte */
	private boolean needConvert;
	
	static final long serialVersionUID = 0x66532D0C6B7FB005L;    

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
