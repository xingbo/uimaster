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
import java.lang.reflect.Array;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;




/**
 * The class for array init expression node
 *
 * @author Xiao Yi
 */

public class ArrayInitExpression extends ExpressionNode
{
    public ArrayInitExpression()
    {
        super("ArrayInitExpression");
        dimensionNum = 0;
        needConvert = false;
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		setValueClass(parent.getValueClass());
		
		if(parent instanceof ArrayDimsAndInits)
		{
		    dimensionNum = ((ArrayDimsAndInits)parent).getDimensionNum();
		}
		else
		if(parent instanceof ArrayInitExpression)
		{
		    dimensionNum = ((ArrayInitExpression)parent).getDimensionNum();
		}
		
        dimensionNum--;
        
        if(dimensionNum < 0)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_069,new Object[]{getValueClassName()});
        }
        
        Class componentClass = valueClass;

        if(dimensionNum > 0)
        {
            componentClass = Array.newInstance(componentClass, new int[dimensionNum]).getClass();
        }
        
        for(int i = 0, childNum = getChildNum(); i < childNum; i++)
        {
            ExpressionNode node = getChild(i);
            Class childClass = node.checkType(context);
            
            if(!ExpressionUtil.isAssignableFrom(componentClass, childClass))
            {
                if(node instanceof Literal &&
                   ExpressionUtil.isNumeric(componentClass) && ExpressionUtil.isNumeric(childClass)
                   && ExpressionUtil.getNumericPrecision(componentClass) < ExpressionUtil.INTEGER_PRECISION
                   && ExpressionUtil.getNumericPrecision(childClass) == ExpressionUtil.INTEGER_PRECISION)
                {
                    needConvert = true;
                }
                else
                {
                	throw new ParsingException(ExceptionConstants.EBOS_OOEE_069,new Object[]{getValueClassName()});
                 //   throw new ParsingException("illegal initialize for " + getValueClassName());
                }                
            }
        }
        
        setValueClass(Array.newInstance(componentClass, 1).getClass());
        
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
        
        Class componentClass = valueClass.getComponentType();
        
        int childNum = getChildNum();
        
        Object valueObject = Array.newInstance(componentClass, childNum);
        
        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode node = getChild(i);
            
            node.evaluate(context);
            
            Object childObject = context.stackPop();

            if(needConvert)
            {
                try
                {
                    childObject = ExpressionUtil.getNumericReturnObject(childObject, componentClass);
                }
                catch(ParsingException e)
                {
                	throw new EvaluationException(ExceptionConstants.EBOS_000,e);
                }
            }
            
            Array.set(valueObject, i, childObject);
        }
        
   	    context.stackPush(valueObject);
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        buffer.appendSeperator(this, "{");
        
        int childNum = getChildNum();
        
        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode node = getChild(i);
            buffer.appendExpressionNode(node);
            if( i != childNum - 1)
            {
                buffer.appendSeperator(this, ", ");
            }
        }
        
        buffer.appendSeperator(this, "}");
	}
	
	/*
	public String toString()
	{
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("{");
        
        int childNum = getChildNum();
        
        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode node = getChild(i);
            buffer.append(node.toString());
            if( i != childNum - 1)
            {
                buffer.append(", ");
            }
        }
        
        buffer.append("}");
        
        return buffer.toString();		
	}
	*/
	
	public int getDimensionNum()
	{
	    return dimensionNum;
	}	
	
	/* indicate whether it needs conversion in numeric assignment from int to short, char, byte */
	private boolean needConvert;
	
	/* the dimension num of the initializing object */
	private int dimensionNum;
	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
