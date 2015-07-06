package org.shaolin.javacc.statement;

import javax.swing.tree.DefaultMutableTreeNode;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for if statement node
 * 
 */
public class IfStatement extends Statement
{
    /**
     * Paint the ast tree of the if statement
     * 
     * @author LQZ
     */
    public void paintAST(DefaultMutableTreeNode root)
    {
        DefaultMutableTreeNode ifSmt = new DefaultMutableTreeNode("If Statement");
        ifSmt.add(new DefaultMutableTreeNode("If Expression:" + ifExpressionNode.toString()));
        DefaultMutableTreeNode ifNode = new DefaultMutableTreeNode("If:");
        ifStatement.paintAST(ifNode);
        ifSmt.add(ifNode);
        if (elseStatement != null)
        {
            DefaultMutableTreeNode elseNode = new DefaultMutableTreeNode("Else:");
            elseStatement.paintAST(elseNode);
            ifSmt.add(elseNode);
        }
        root.add(ifSmt);
    }

    /**
     * The execution of the if statement
     * 
     * @author LQZ
     * @throws Exception
     * 
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        Object result = new Object();
        try
        {
            result = ifExpressionNode.evaluate(evaluationContext);
        }
        catch (EvaluationException e)
        {
            execResult.setResultCode(StatementConstants.exceptionEnding);
            execResult.setCauseException(e.getCause());
            return execResult;
        }
        if (((Boolean) result).booleanValue())
        {
            execResult = ifStatement.execute(evaluationContext);
            if (execResult.getResultCode() != StatementConstants.normalEnding)
                return execResult;

        }
        else
        {
            if (elseStatement != null)
            {
                execResult = elseStatement.execute(evaluationContext);
                if (execResult.getResultCode() != StatementConstants.normalEnding)
                    return execResult;
            }
        }
        return execResult;

    }

    /**
     * The parse of the if statement
     * 
     * @throws Exception
     * 
     */
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        ifExpressionNode.parse(parsingContext);
        if (ifExpressionNode.getValueClass() != boolean.class)
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_029);
          //  throw new ParsingException("Can't convert the expression to boolean");
        ifStatement.parse(parsingContext);
        if (elseStatement != null)
            elseStatement.parse(parsingContext);
    }

    public void traverse(Traverser traverser)
    {
        if (ifExpressionNode != null)
        {
            ifExpressionNode.traverse(traverser);
        }
        if (ifStatement != null)
        {
            ifStatement.traverse(traverser);
        }
        if (elseStatement != null)
        {
            elseStatement.traverse(traverser);
        }
    }

    public ExpressionNode getIfExpression()
    {
        return ifExpressionNode;
    }

    public void setIfExpression(ExpressionNode ifExp)
    {
        this.ifExpressionNode = ifExp;
    }

    public Statement getIfStatement()
    {
        return ifStatement;
    }

    public void setIfStatement(Statement ifSmt)
    {
        this.ifStatement = ifSmt;
    }

    public Statement getElseStatement()
    {
        return elseStatement;
    }

    public void setElseStatement(Statement elseSmt)
    {
        this.elseStatement = elseSmt;
    }

    /* The expression used to judge which path in if statement */
    private ExpressionNode ifExpressionNode;

    /* The statement will be executed when the expression returns true */
    private Statement ifStatement = null;

    /* The statement will be executed when the expression returns false */
    private Statement elseStatement = null;


    /* The serial ID */
    private static final long serialVersionUID = 1357924680L;

}
