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
import org.shaolin.javacc.symbol.FieldName;


/**
 * The util class to get all possible exceptions thrown by an expression
 *
 * @author Xiao Yi
 */
public class VariableLister implements Traverser
{
    public VariableLister()
    {
        variableList = new ArrayList();
    }
    
    public void traverse(ExpressionNode node)
    {
        if(node instanceof FieldName)
        {
            FieldName fieldNode = (FieldName)node;
            if(fieldNode.isVariableNode())
            {
                String varName = fieldNode.getFieldName();
                if(!variableList.contains(varName))
                {
                    variableList.add(varName);
                }
            }
        }
    }
    
    public String[] getAllVariables()
    {
        return (String[])variableList.toArray(new String[]{});
    }
    
    public void reset()
    {
        variableList.clear();
    }
    
    private List variableList;    

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
