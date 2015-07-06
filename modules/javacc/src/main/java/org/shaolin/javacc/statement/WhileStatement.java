package org.shaolin.javacc.statement;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the while statement node
 * 
 */
public class WhileStatement extends Statement 
{		
    /**
     * The execution of the while statement
     * @throws Exception 
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        ExecutionResult execResult = new ExecutionResult();
        Object result = new Object();
        try 
        {
            result = whileExpressionNode.evaluate(evaluationContext);
        } 
        catch (EvaluationException e) 
        {
            execResult.setResultCode(StatementConstants.exceptionEnding);
            execResult.setCauseException(e.getCause());
            return execResult;
        }
        while(((Boolean)result).booleanValue())
        {
            execResult = whileStatement.execute(evaluationContext);
            if(execResult.getResultCode() == StatementConstants.exceptionEnding || execResult.getResultCode() == StatementConstants.returnEnding)
            {
                return execResult;
            }
            else if(execResult.getResultCode() == StatementConstants.breakEnding)
            {
                execResult.setResultCode(StatementConstants.normalEnding);
                return execResult;
            }
            try
            {
                result = whileExpressionNode.evaluate(evaluationContext);   
            }
            catch(EvaluationException e)
            {
                execResult.setResultCode(StatementConstants.exceptionEnding);
                execResult.setCauseException(e.getCause());
                return execResult;
            }
        }           
        return execResult;

    }
    
    /**
     * The parse of the while statement
     * @throws Exception 
     * @throws Exception 
     */
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        whileExpressionNode.parse(parsingContext);
        if(whileExpressionNode.getValueClass() != boolean.class)
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_029);
         //   throw new ParsingException("Can't convert the expression to boolean");
        whileStatement.parse(parsingContext);
    }
    
    
    
    public void traverse(Traverser traverser)
    {
        if (whileExpressionNode != null)
        {
            whileExpressionNode.traverse(traverser);
        }
        if (whileStatement != null)
        {
            whileStatement.traverse(traverser);
        }
    }

    public ExpressionNode getWhileExpression()
    {
        return whileExpressionNode;
    }
    
    public void setWhileExpression(ExpressionNode whileExp)
    {
        this.whileExpressionNode=whileExp;
    }
    
    public Statement getWhilsStatement()
    {
        return whileStatement;
    }
    
    public void setWhileStatement(Statement whileSmt)
    {
        this.whileStatement=whileSmt;
    }
    
    /*The condition expression in the while statement*/
    private ExpressionNode whileExpressionNode;

    /*The executed statement in the while statement*/
    private Statement whileStatement;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
