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
 * The class for array expression node
 *
 * @author Xiao Yi
 */

public class ArrayExpression extends ExpressionNode
{
    public ArrayExpression()
    {
        super("ArrayExpression");
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
        if(getChildNum() > 0)
        {
	        // check whether the index value is a type of int
            ExpressionNode node = getChild(0);
            Class sizeClass = node.checkType(context);

            if(!ExpressionUtil.isNumeric(sizeClass) || ExpressionUtil.getNumericPrecision(sizeClass) > ExpressionUtil.INTEGER_PRECISION)
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_062,new Object[]{node.toString(),sizeClass});
              //  throw new ParsingException("can't cast " + node.toString() + " type: " + sizeClass + " to int");
            }
            else
            {
                setValueClass(int.class);
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
        
        if(getChildNum() > 0)
        {
            // return the index value, must be an nonnegative int
            ExpressionNode node = getChild(0);
            
            node.evaluate(context);
            
            int size = ((Number)context.stackPop()).intValue();
            
            if(size < 0)
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_013);
             //   throw new EvaluationException("Negative Array Size");
            }
            
            context.stackPush(new Integer(size));
        }
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        buffer.appendSeperator(this, "[");
        
        if(getChildNum() > 0)
        {
            ExpressionNode node = getChild(0);
            buffer.appendExpressionNode(node);
        }
        
        buffer.appendSeperator(this, "]");
	}
	
	/*
	public String toString()
	{
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("[");
        
        if(getChildNum() > 0)
        {
            ExpressionNode node = getChild(0);
            buffer.append(node.toString());
        }
        
        buffer.append("]");
        
        return buffer.toString();
	}
	*/
	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
