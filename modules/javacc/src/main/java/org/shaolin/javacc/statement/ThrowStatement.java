package org.shaolin.javacc.statement;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The calss for the throw statement node
 * 
 */
public class ThrowStatement extends Statement
{

    public ThrowStatement()
    {
  
    }
    
	public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        ExecutionResult execResult = new ExecutionResult();
        Throwable value = new Throwable();
        try 
        {
            value = (Throwable) throwExpNode.evaluate(evaluationContext);
        } 
        catch (EvaluationException e) 
        {
            execResult.setResultCode(StatementConstants.exceptionEnding);
            execResult.setCauseException(e.getCause());
            return execResult;
        }
        execResult.setResultCode(StatementConstants.exceptionEnding);
        execResult.setCauseException(value);
        return execResult;
    }
	
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        throwExpNode.parse(parsingContext);
        if(!Throwable.class.isAssignableFrom(throwExpNode.getValueClass()))
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_064,new Object[]{throwExpNode.getValueClass()});
    }
    
    public void traverse(Traverser traverser)
    {
        if (throwExpNode != null)
        {
            throwExpNode.traverse(traverser);
        }
    }
    
    public ExpressionNode getThrowExp()
    {
        return throwExpNode;
    }
     
    public void setThrowExp(ExpressionNode newThrowExp)
    {
        this.throwExpNode = newThrowExp;
    }
    
	private ExpressionNode throwExpNode = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;
   
}
