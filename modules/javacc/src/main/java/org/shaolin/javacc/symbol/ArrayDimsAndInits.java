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
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;




/**
 * The class for array dimensions and initializing expression node
 *
 * @author Xiao Yi
 */

public class ArrayDimsAndInits extends ExpressionNode
{
    public ArrayDimsAndInits()
    {
        super("ArrayDimsAndInits");
        dimensionNum = 0;
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		setValueClass(parent.getValueClass());

        int childNum = dimensionNum = getChildNum();
        
        ExpressionNode child = getChild(childNum - 1);
        
	    if(child instanceof ArrayInitExpression)
	    {
	        dimensionNum--;
	    }
	    
	    int[] dimensions = new int[dimensionNum];
	    
        for(int i = 0; i < dimensionNum; i++)
        {
            ArrayExpression array = (ArrayExpression)getChild(i);
            array.checkType(context);
        }
        
        if(childNum != dimensionNum)
        {
            ArrayInitExpression init = (ArrayInitExpression)getChild(childNum - 1);
            init.checkType(context);
        }
		
	    Object arrayObject = Array.newInstance(valueClass, dimensions);
	    setValueClass(arrayObject.getClass());

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
        
        int childNum = getChildNum();
        
        ExpressionNode child = getChild(childNum - 1);
        
	    if(child instanceof ArrayInitExpression)
	    {
	        child.evaluate(context);
	    }
        else
        {
            Class componentClass = valueClass;
                        
            List dimensionSizes = new ArrayList();
            
            for(int i = 0; i < dimensionNum; i++)
            {
                ArrayExpression array = (ArrayExpression)getChild(i);
                
                if(array.getChildNum() == 0)
                {
                    break;
                }
                else
                {
                    array.evaluate(context);
                    Integer size = (Integer)context.stackPop();
                    dimensionSizes.add(size);
                    componentClass = componentClass.getComponentType();
                }
            }
            
            int depth = dimensionSizes.size();
            
            int[] newDimensions = new int[depth];
            
            for(int i = 0; i < depth; i++)
            {
                newDimensions[i] = ((Integer)dimensionSizes.get(i)).intValue();
            }
            
            context.stackPush(Array.newInstance(componentClass, newDimensions));
        }
	}
	
	public int getDimensionNum()
	{
	    return dimensionNum;
	}

	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        int childNum = getChildNum();

        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode child = getChild(i);
            buffer.appendExpressionNode(child);
        }
	}
	
	/*	
	public String toString()
	{
        StringBuffer buffer = new StringBuffer();

        int childNum = getChildNum();

        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode child = getChild(i);
            buffer.append(child.toString());
        }
        
        return buffer.toString();
	}
	*/
	
	/* the dimension num for this array */
	private int dimensionNum;
	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
