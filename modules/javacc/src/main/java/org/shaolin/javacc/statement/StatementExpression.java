package org.shaolin.javacc.statement;

//imports
import javax.swing.tree.DefaultMutableTreeNode;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the statement expression node
 *
 */
public class StatementExpression extends Statement
{	
    /**
     * Paint the ast tree of the statement expression
     * 
     * @author LQZ
     */
    public void paintAST(DefaultMutableTreeNode root)
    {
        root.add(new DefaultMutableTreeNode(statementExpNode.toString()));
    }
    
    public ExpressionNode getStatementExp()
    {
        return statementExpNode;
    }
    
    public void setStatementExp(ExpressionNode smtExp)
    {
        this.statementExpNode=smtExp;
    }
       
    /**
     * The execution of the statement expression
     * 
     * @author LQZ
     * @throws Exception 
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        try 
        {
            statementExpNode.evaluate(evaluationContext);
        } 
        catch (EvaluationException e) 
        {
            execResult.setResultCode(StatementConstants.exceptionEnding);
            Throwable cause = e.getCause();
            if(cause != null)
            {
                execResult.setCauseException(cause);
            }
            else
            {
                execResult.setCauseException(e);
            }
        }
        return execResult;
    }
    
    /**
     * The parse of statement expression
     * 
     */
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        statementExpNode.parse(parsingContext);     
    }
    
    public void traverse(Traverser traverser)
    {
       if (statementExpNode != null)
       {
           statementExpNode.traverse(traverser);
       }
    }
    
    private ExpressionNode statementExpNode;

}
