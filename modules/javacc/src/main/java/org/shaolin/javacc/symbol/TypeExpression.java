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




/**
 * The class for type expression node
 *
 * @author Xiao Yi
 */

public class TypeExpression extends ExpressionNode
{
    public TypeExpression()
    {
        super("TypeExpression");
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		ExpressionNode typeNode = getChild(0);
		
        setValueClass(typeNode.checkType(context));
        
		//check whether the class name represents a valid class
		if(typeNode instanceof FieldExpression)
		{
		    FieldExpression name = (FieldExpression)typeNode;
		    if(!name.isClass())
		    {
		    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_034,new Object[]{name.toString()});
		     //   throw new ParsingException("Can't find the class " + name.toString() + " for new expression");
		    }
		}
        
        int childNum = getChildNum();
        
        //here for array type
        if(childNum > 1)
        {
            int[] dimensions = new int[childNum - 1];
            Object arrayObject = Array.newInstance(valueClass, dimensions);
            setValueClass(arrayObject.getClass());
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
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
	    ExpressionNode node = getChild(0);
	    buffer.appendExpressionNode(node);
	    
	    int childNum = getChildNum();
	    for(int i = 1; i < childNum; i++)
	    {
	    	buffer.appendSeperator(this, "[]");
	    }
	}
	
	/*
	public String toString()
	{
	    ExpressionNode node = getChild(0);
	    String result = node.toString();
	    
	    int childNum = getChildNum();
	    for(int i = 1; i < childNum; i++)
	    {
	        result += "[]";
	    }
	    
	    return result;
	}
	*/
	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
