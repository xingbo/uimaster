package org.shaolin.javacc;

//imports
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.statement.CompilationUnit;


/**
 * The class for the statement evaluation
 * 
 * @author LQZ
 * 
 */
public class StatementEvaluator 
{
    /**
     * Evaluate the statement with the default evaluation context
     * 
     * @param compilationUnit
     * 
     * @throws EvaluationException
     */
    public static Object evaluate(CompilationUnit compilationUnit) throws EvaluationException
    {
        return evaluate(compilationUnit, OOEEContextFactory.createOOEEContext());
    }
    
    /**
     * Evaluate the statement with the given evaluation context
     * 
     * @param compilationUnit
     * @param context
     * 
     * @throws EvaluationException
     */
    public static Object evaluate(CompilationUnit compilationUnit, EvaluationContext context) throws EvaluationException
    {
        try
        {
            Object result = compilationUnit.execute(context);            
            return result;
        }
        catch(Throwable t)
        {
        	if (t instanceof EvaluationException)
        	{
        		throw (EvaluationException)t;
        	}
            if (compilationUnit == null)
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_012);
            }
            else
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_011,t);
            }
        }
    }

}
