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
package org.shaolin.javacc.util;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.symbol.ExpressionNode;


/**
 * The util class to regenerate an expression string from the parsed expression node
 * 
 * @author Xiao Yi
 *
 */
public interface ExpressionStringBuffer
{
	public void appendExpressionNode(ExpressionNode node);
	public void appendSeperator(ExpressionNode currentNode, String seperator);

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
