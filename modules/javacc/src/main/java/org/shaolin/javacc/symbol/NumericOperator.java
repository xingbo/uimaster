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
import org.shaolin.javacc.util.ExpressionUtil;



/**
 * The class for numeric operator
 *
 * @author Xiao Yi
 */
public class NumericOperator extends Operator
{
    public NumericOperator(String type)
    {
        super(type);
    }

    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        ExpressionNode lChild = (ExpressionNode)getChild(0);
        ExpressionNode rChild = (ExpressionNode)getChild(1);
        Class lChildClass = lChild.checkType(context);
        Class rChildClass = rChild.checkType(context);

        /*something special for String*/
        boolean isString = lChildClass == null ||
                           lChildClass == String.class ||
                           rChildClass == null ||
                           rChildClass == String.class;

        if(isString && type.equals(PLUS_STRING))
        {
            if(lChildClass == void.class || rChildClass == void.class)
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_050,new Object[]{type});
               // throw new ParsingException("Operands of Numeric Operator " + type + " must be number or String");
            }
            else
            {
                setValueClass(String.class);
            }
        }
        else
        {
            if(!ExpressionUtil.isNumeric(lChildClass))
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_051,new Object[]{type});
               // throw new ParsingException("Operands of Numeric Operator " + type + " must be number");
            }

            if(!ExpressionUtil.isNumeric(rChildClass))
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_051,new Object[]{type});
            //    throw new ParsingException("Operands of Numeric Operator " + type + " must be number");
            }

            setValueClass(ExpressionUtil.getNumericReturnClass(lChildClass, rChildClass));
        }

        //optimization code
        if(lChild.isConstant() && rChild.isConstant())
        {
            super.isConstant = true;
            Object lResult = lChild.getConstantValue();
            Object rResult = rChild.getConstantValue();
            Object result = calculateNumericResult(lResult, rResult);
            super.setConstantValue(result);
        }
        else
        {
            super.isConstant = false;
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

        ExpressionNode lChild = (ExpressionNode)getChild(0);
        lChild.evaluate(context);
        Object lResult = context.stackPop();

        ExpressionNode rChild = (ExpressionNode)getChild(1);
        rChild.evaluate(context);
        Object rResult = context.stackPop();

        try
        {
            Object valueObject = calculateNumericResult(lResult, rResult);            
            context.stackPush(valueObject);
        }
        catch(ArithmeticException e)
        {
            throw new EvaluationException(e);
        }        
    }

    private Object calculateNumericResult(Object lResult, Object rResult)
    {
        if (valueClass == String.class && type.equals(PLUS_STRING))
        {
            return String.valueOf(lResult) + String.valueOf(rResult);
        }

        if (isInstanceOf(lResult, rResult, Double.class))
        {
            return calculateDoubleResult(lResult, rResult);
        }
        if (isInstanceOf(lResult, rResult, Float.class))
        {
            return calculateFloatResult(lResult, rResult);
        }
        if (isInstanceOf(lResult, rResult, Long.class))
        {
            return calculateLongResult(lResult, rResult);
        }
        return calculateIntegerResult(lResult, rResult);
    }

    private Object calculateDoubleResult(Object lResult, Object rResult)
    {
        double l = (lResult instanceof Number) ?
                ((Number)lResult).doubleValue() :
                (double)((Character)lResult).charValue();
        double r = (rResult instanceof Number) ?
                ((Number)rResult).doubleValue() :
                (double)((Character)rResult).charValue();
        if (PLUS_STRING.equals(type))
        {
            return new Double(l + r);
        }
        if (MINUS_STRING.equals(type))
        {
            return new Double(l - r);
        }
        if (MUL_STRING.equals(type))
        {
            return new Double(l * r);
        }
        if (DIV_STRING.equals(type))
        {
            return new Double(l / r);
        }
        return new Double(l % r);
    }

    private Object calculateFloatResult(Object lResult, Object rResult)
    {
        float l = (lResult instanceof Number) ?
                ((Number)lResult).floatValue() :
                (float)((Character)lResult).charValue();
        float r = (rResult instanceof Number) ?
                ((Number)rResult).floatValue() :
                (float)((Character)rResult).charValue();
        if (PLUS_STRING.equals(type))
        {
            return new Float(l + r);
        }
        if (MINUS_STRING.equals(type))
        {
            return new Float(l - r);
        }
        if (MUL_STRING.equals(type))
        {
            return new Float(l * r);
        }
        if (DIV_STRING.equals(type))
        {
            return new Float(l / r);
        }
        return new Float(l % r);
    }

    private Object calculateLongResult(Object lResult, Object rResult)
    {
        long l = (lResult instanceof Number) ?
                ((Number)lResult).longValue() :
                (long)((Character)lResult).charValue();
        long r = (rResult instanceof Number) ?
                ((Number)rResult).longValue() :
                (long)((Character)rResult).charValue();
        if (PLUS_STRING.equals(type))
        {
            return new Long(l + r);
        }
        if (MINUS_STRING.equals(type))
        {
            return new Long(l - r);
        }
        if (MUL_STRING.equals(type))
        {
            return new Long(l * r);
        }
        if (DIV_STRING.equals(type))
        {
            return new Long(l / r);
        }
        return new Long(l % r);
    }

    private Object calculateIntegerResult(Object lResult, Object rResult)
    {
        int l = (lResult instanceof Number) ?
                ((Number)lResult).intValue() :
                (int)((Character)lResult).charValue();
        int r = (rResult instanceof Number) ?
                ((Number)rResult).intValue() :
                (int)((Character)rResult).charValue();
        if (PLUS_STRING.equals(type))
        {
            return new Integer(l + r);
        }
        if (MINUS_STRING.equals(type))
        {
            return new Integer(l - r);
        }
        if (MUL_STRING.equals(type))
        {
            return new Integer(l * r);
        }
        if (DIV_STRING.equals(type))
        {           
            return new Integer(l / r);  
        }
        return new Integer(l % r);
    }
    
    private boolean isInstanceOf(Object lResult, Object rResult, Class clazz)
    {
        return clazz.isInstance(lResult) || clazz.isInstance(rResult);
    }

    private static final String  MUL_STRING = "*";
    private static final String  DIV_STRING = "/";
    private static final String  PLUS_STRING = "+";
    private static final String  MINUS_STRING = "-";
    private static final String  MOD_STRING = "%";

    static final long serialVersionUID = 0xD41AA00A967B6261L;

    public static final String ___REVISION___ = "$Revision: 1.6 $";
}
