
package org.shaolin.javacc.statement;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The class for the do statement node
 * 
 */
public class DoStatement extends Statement
{

    /**
     * The execution of the do statement
     * 
     * @throws Exception
     * @throws ParsingException
     * @throws EvaluationException
     * @throws ClassNotFoundException
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        Object result = new Object();
        do
        {
            execResult = doStatement.execute(evaluationContext);
            if (execResult.getResultCode() == StatementConstants.exceptionEnding
                    || execResult.getResultCode() == StatementConstants.returnEnding)
            {
                return execResult;
            }
            else if (execResult.getResultCode() == StatementConstants.breakEnding)
            {
                execResult.setResultCode(StatementConstants.normalEnding);
                return execResult;
            }
            try
            {
                result = doExpressionNode.evaluate(evaluationContext);
            }
            catch (EvaluationException e)
            {
                execResult.setResultCode(StatementConstants.exceptionEnding);
                execResult.setCauseException(e.getCause());
                return execResult;
            }
        }
        while (((Boolean) result).booleanValue());
        return execResult;

    }

    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        doExpressionNode.parse(parsingContext);
        if (doExpressionNode.getValueClass() != boolean.class)
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_029);
         //   throw new ParsingException("Can't convert the expression to boolean");
        doStatement.parse(parsingContext);

    }
    
    public void traverse(Traverser traverser)
    {
        if (doExpressionNode != null)
        {
            doExpressionNode.traverse(traverser);
        }
        if (doStatement != null)
        {
            doStatement.traverse(traverser);
        }
    }

    public Statement getDoStatement()
    {
        return doStatement;
    }

    public void setDoStatement(Statement doSmt)
    {
        this.doStatement = doSmt;
    }

    public ExpressionNode getDoExpression()
    {
        return doExpressionNode;
    }

    public void setDoExpression(ExpressionNode doExp)
    {
        this.doExpressionNode = doExp;
    }

    /* The excuted statement in the do statement */
    private Statement doStatement;

    /* The condition expression in the do statement */
    private ExpressionNode doExpressionNode;

    /* The serial ID */
    private static final long serialVersionUID = 1357924680L;

}
