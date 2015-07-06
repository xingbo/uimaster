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



/**
 * The class for primitive type expression node
 *
 * @author Xiao Yi
 */

public class PrimitiveType extends ExpressionNode
{
    public PrimitiveType()
    {
        super("PrimitiveType");
    }
    
	public Class checkType(OOEEParsingContext context) throws ParsingException
	{
		if(type.equals(BOOLEAN))
		{
		    setValueClass(boolean.class);
		}
		else
		if(type.equals(CHARACTER))
		{
		    setValueClass(char.class);
		}
		else
		if(type.equals(BYTE))
		{
		    setValueClass(byte.class);
		}
		else
		if(type.equals(SHORT))
		{
		    setValueClass(short.class);
		}
		else
		if(type.equals(INTEGER))
		{
		    setValueClass(int.class);
		}
		else
		if(type.equals(LONG))
		{
		    setValueClass(long.class);
		}
		else
		if(type.equals(FLOAT))
		{
		    setValueClass(float.class);
		}
		else
		if(type.equals(DOUBLE))
		{
		    setValueClass(double.class);
		}
		else
		if(type.equals(VOID))
		{
		    setValueClass(void.class);
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
	
    private static String BOOLEAN = "boolean";
    private static String CHARACTER = "char";
    private static String FLOAT = "float";
    private static String DOUBLE = "double";
    private static String BYTE = "byte";
    private static String SHORT = "short";
    private static String INTEGER = "int";
    private static String LONG = "long";
    private static String VOID = "void";
    	
	static final long serialVersionUID = 0x2EC43588D56D770EL;

    public static final String ___REVISION___ = "$Revision: 1.3 $";
}
