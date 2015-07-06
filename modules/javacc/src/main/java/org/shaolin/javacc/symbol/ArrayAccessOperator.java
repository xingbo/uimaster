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
 * The class for index operator
 *
 * @author Xiao Yi
 */
public class ArrayAccessOperator extends ExpressionNode
{
    public ArrayAccessOperator()
    {
        super("ArrayAccessOperator");
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
    	super.isVariable = true;
	    
        ExpressionNode child = getChild(0);
        
        Class definedVarClass = child.checkType(context);
        while (definedVarClass.isArray()) {
        	definedVarClass = definedVarClass.getComponentType();
        }
        this.setValueClass(definedVarClass);
        
        ArrayExpression index = (ArrayExpression)getChild(1);
        
        if(index.checkType(context) == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_071);
        //    throw new ParsingException("index can't be nothing");
        }
        
        return valueClass;
    }

    public void evaluateNode(OOEEEvaluationContext context) throws EvaluationException
    {
        try
        {
            getValueClass();
        }
        catch(ParsingException e)
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }
        
        ExpressionNode child = getChild(0);
        child.evaluate(context);
        
        Object arrayObject;
        
        if(isValueAssignee())
        {
            arrayObject = context.stackPeek();
        }
        else
        {
            arrayObject = context.stackPop();
        }
        
        ArrayExpression index = (ArrayExpression)getChild(1);
        index.evaluate(context);
        
        int indexValue;
        
        if(isValueAssignee())
        {
            indexValue = ((Integer)context.stackPeek()).intValue();
        }
        else
        {
            indexValue = ((Integer)context.stackPop()).intValue();
        }

        try
        {
            context.stackPush(Array.get(arrayObject, indexValue));
        }
        catch(Exception e)
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }
    }
    
    protected void setVariableValue(OOEEEvaluationContext context, Object variableValue) throws EvaluationException
    {
        int indexValue = ((Integer)context.stackPop()).intValue();
        Object arrayObject = context.stackPop();
        Array.set(arrayObject, indexValue, variableValue);
    }
    
    public void appendToBuffer(ExpressionStringBuffer buffer)
    {
        ExpressionNode child = getChild(0);
        ExpressionNode index = getChild(1);
        
        buffer.appendExpressionNode(child);
        buffer.appendExpressionNode(index);
    }
    
    /*
    public String toString()
    {
        ExpressionNode child = getChild(0);
        ExpressionNode index = getChild(1);
        
        String result = child.toString() + index.toString();
        
        return result;
    }
    */
    
    static final long serialVersionUID = 0xDBCF3A60BF8CADADL;

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
