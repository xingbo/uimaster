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

import org.shaolin.javacc.symbol.ExpressionNode;

/**
 * The util interface which performs traversing the syntax tree
 *
 * @author Xiao Yi
 */
public interface Traverser
{
    public void traverse(ExpressionNode node);

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
