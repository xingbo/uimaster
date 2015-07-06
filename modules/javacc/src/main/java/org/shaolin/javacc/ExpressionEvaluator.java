package org.shaolin.javacc;

//imports
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;


/**
 * The class for expression evaluator
 *
 */

public class ExpressionEvaluator
{
	
    /**
     *  Evaluate the expression using default OOEEContext as the Evaluation Context
     *
     *  @return     the evaluation result object
     *  @throws     EvaluationException     if something error in evaluation
     *  
     */
    public static Object evaluate(Expression expr) throws EvaluationException
    {
        return evaluate(expr, OOEEContextFactory.createOOEEContext());
    }
    
    /**
     *  Evaluate the expression using the given Evaluation Context
     *
     *  @return     the evaluation result object
     *  @throws     EvaluationException     if something error in evaluation
     *  
     */
    public static Object evaluate(Expression expr, EvaluationContext context) throws EvaluationException
    {
        try
        {
	        Object result = expr.evaluate(context);
	        
	        return result;
	    }
	    catch(Throwable t)
	    {
	        if (expr == null)
	        {
	        	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_009);
	        }
	        else
	        {
	        	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_010,t,new Object[]{expr.getExpressionString()});
	    	   // throw new EvaluationException("Fail to evaluate expression:" + expr.getExpressionString(), t);
	    	}
	    }
    }

}
