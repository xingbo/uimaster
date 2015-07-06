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
package org.shaolin.javacc.util.traverser;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.symbol.FieldExpression;
import org.shaolin.javacc.symbol.FieldName;


/**
 * The util class to get all possible exceptions thrown by an expression
 *
 * @author Xiao Yi
 */
public class FieldLister implements Traverser
{
    public FieldLister()
    {
        fieldList = new ArrayList();
    }
    
    public void traverse(ExpressionNode node)
    {
        if(node instanceof FieldExpression)
        {
            FieldExpression fieldExpression = (FieldExpression)node;
            int childNum = fieldExpression.getChildNum();
            ExpressionNode lastChildNode = fieldExpression.getChild(childNum - 1);
            if(lastChildNode instanceof FieldName)
            {
	            FieldName fieldNode = (FieldName)lastChildNode;
	            if(fieldNode.isField() || fieldNode.isCustomField() || fieldNode.isVariableNode())
	            {
	                fieldList.add(fieldExpression.toString());
	            }
	        }
        }
    }
    
    public String[] getAllFieldNames()
    {
        return (String[])fieldList.toArray(new String[]{});
    }
    
    public void reset()
    {
        fieldList.clear();
    }
    
    private List fieldList;

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
