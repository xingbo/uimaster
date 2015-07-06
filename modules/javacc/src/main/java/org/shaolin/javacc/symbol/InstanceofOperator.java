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
 * The class for instanceof operator
 *
 * @author Xiao Yi
 */
public class InstanceofOperator extends Operator
{
    public InstanceofOperator()
    {
        super("instanceof");
        instanceClassName = null;
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
		ExpressionNode expr = getChild(0);
		Class exprClass = expr.checkType(context);

		ExpressionNode type = getChild(1);
		Class instanceClass = type.checkType(context);
		
		if(!ExpressionUtil.isReferenceType(exprClass) || !ExpressionUtil.isReferenceType(instanceClass) || !ExpressionUtil.isCastableFrom(instanceClass, exprClass))
		{
			throw new ParsingException(ExceptionConstants.EBOS_OOEE_037,new Object[]{exprClass,instanceClass});
		}

        instanceClassName = instanceClass.getName();
        setValueClass(boolean.class);
        
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

        Class instanceClass = null;
        try
        {
            instanceClass = ExpressionUtil.findClass(instanceClassName);
        }
        catch(ParsingException e)
        {
            throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }

		ExpressionNode expr = getChild(0);
		
		expr.evaluate(context);
		
		Object exprObject = context.stackPop();
    	
    	Object valueObject = instanceClass.isInstance(exprObject) ? Boolean.TRUE : Boolean.FALSE;
    	
    	context.stackPush(valueObject);
	}
	
	private String instanceClassName;

	static final long serialVersionUID = 0x66532D0C6B7FB005L;    

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
