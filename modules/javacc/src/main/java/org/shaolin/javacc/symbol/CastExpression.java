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
 * The class for cast expression node
 *
 * @author Xiao Yi
 */

public class CastExpression extends ExpressionNode
{
    public CastExpression()
    {
        super("CastExpression");
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
        ExpressionNode type = getChild(0);
        
        setValueClass(type.checkType(context));
        
        ExpressionNode expr = getChild(1);
        
        Class fromClass = expr.checkType(context);
        
        if(!ExpressionUtil.isCastableFrom(valueClass, fromClass))
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_070,new Object[]{fromClass,valueClass});
          //  throw new ParsingException("inconvertable cast from class " + fromClass + " to " + valueClass);
        }
        
        //optimization code
        if(valueClass.isPrimitive() && expr.isConstant())
        {
            super.isConstant = true;
            
            Object constantValue = expr.getConstantValue();
            
        	if(ExpressionUtil.isNumeric(valueClass))
        	{
                constantValue = ExpressionUtil.getNumericReturnObject(constantValue, valueClass);
            }

            super.setConstantValue(constantValue);
        }
        else
        {
            super.isConstant = false;
        }
        
		//fix bug
        if(expr instanceof FieldExpression && parent instanceof FieldExpression)
        {
        	boolean isClass = ((FieldExpression)expr).isClass();
        	((FieldExpression)parent).setIsClass(isClass);
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

        ExpressionNode expr = getChild(1);
        
        expr.evaluate(context);
        
        Object valueObject = context.stackPop();
        
    	if(ExpressionUtil.isNumeric(valueClass))
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
        else
        if(ExpressionUtil.isReferenceType(valueClass))
        {
        	if(valueObject != null && !valueClass.isAssignableFrom(valueObject.getClass()))
        	{
        		throw new EvaluationException(ExceptionConstants.EBOS_OOEE_006,new Object[]{valueObject.getClass(),valueClass});
        	}
        }
        
        context.stackPush(valueObject);
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
		buffer.appendSeperator(this, "(");
		buffer.appendSeperator(this, "(");
		ExpressionNode type = getChild(0);
		buffer.appendExpressionNode(type);
		buffer.appendSeperator(this, ")");
		ExpressionNode expr = getChild(1);
		buffer.appendSeperator(this, "(");
		buffer.appendExpressionNode(expr);
		buffer.appendSeperator(this, ")");
		buffer.appendSeperator(this, ")");
	}
	
	/*
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("(");
		buffer.append("(");
		ExpressionNode type = getChild(0);
		buffer.append(type.toString());
		buffer.append(")");
		ExpressionNode expr = getChild(1);
		buffer.append("(");
		buffer.append(expr.toString());
		buffer.append(")");
		buffer.append(")");

		return buffer.toString();
	}
	*/
	
    static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
