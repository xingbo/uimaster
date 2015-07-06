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
import org.shaolin.javacc.util.ExpressionStringBuffer;

/**
 * The super class for all operators
 *
 * @author Xiao Yi
 */
abstract class Operator extends ExpressionNode
{
    public Operator(String type)
    {
        super(type);
    }

	public void appendToBuffer(ExpressionStringBuffer buffer)
	{
		buffer.appendSeperator(this, "(");
		for(int i = 0, childNum = getChildNum(); i < childNum; i++)
		{
		    buffer.appendExpressionNode(getChild(i));
		    if(i != childNum - 1)
		    {
		    	buffer.appendSeperator(this, " ");
		        buffer.appendSeperator(this, type);
		    	buffer.appendSeperator(this, " ");
		    }
		}
		buffer.appendSeperator(this, ")");
	}
	
	/*
    public String toString()
    {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("(");
		for(int i = 0, childNum = getChildNum(); i < childNum; i++)
		{
		    buffer.append(getChild(i).toString());
		    if(i != childNum - 1)
		    {
		    	buffer.append(" ");
		        buffer.append(type);
		    	buffer.append(" ");		        
		    }
		}
		buffer.append(")");
		
    	return buffer.toString();
    }
    */
    
	static final long serialVersionUID = 0xE56F0154E80187A7L;

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
