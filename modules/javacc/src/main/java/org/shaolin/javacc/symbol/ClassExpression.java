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




/**
 * The class for .class expression node
 *
 * @author Xiao Yi
 */

public class ClassExpression extends ExpressionNode
{
    public ClassExpression()
    {
        super("ClassExpression");
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
        ExpressionNode node = getChild(0);
        
		Class valueClass = node.checkType(context);
		
		setValueClass(Class.class);
		
		return Class.class;
	}
	
	protected void evaluateNode(OOEEEvaluationContext context) throws EvaluationException
	{
        try
        {
            getValueClass();
            
	        ExpressionNode node = getChild(0);
			
			context.stackPush(node.getValueClass());
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
        buffer.appendSeperator(this, ".class");
	}
	
	/*
	public String toString()
	{
        ExpressionNode node = getChild(0);
        String result = node.toString() + ".class";
        return result;
	}
	*/
	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
