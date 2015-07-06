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
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;




/**
 * The class for argumentlist expression node
 *
 * @author Xiao Yi
 */

public class ArgumentList extends ExpressionNode
{
    public ArgumentList()
    {
        super("ArgumentList");
    }
    
	//do nothing, let parent argument node check children's types
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		return null;
	}
	
	//do nothing, let parent argument node evaluate children's values
	protected void evaluateNode(OOEEEvaluationContext context) throws EvaluationException
	{
	}
	
	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
        int childNum = getChildNum();
        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode child = getChild(i);
            buffer.appendExpressionNode(child);
            if(i != childNum - 1)
            {
                buffer.appendSeperator(this, ", ");
            }
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
            if(i != childNum - 1)
            {
                buffer.append(", ");
            }
        }
	
		return buffer.toString();
	}
	*/
	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
