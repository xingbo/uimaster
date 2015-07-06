package org.shaolin.javacc.statement;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the statement expression list, contained some statement expressions
 * 
 */
public class StatementExpressionList extends Statement 
{
	public StatementExpressionList()
	{
		smtExpList = new ArrayList();
        parsedSmtExpList = new ArrayList();
	}
    
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        for(int i = 0; i < smtExpList.size(); i++)
        {
            ((StatementExpression)smtExpList.get(i)).parse(parsingContext);            
        }
    }
    
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        for(int i = 0; i < smtExpList.size(); i++)
        {
            execResult = ((StatementExpression)smtExpList.get(i)).execute(evaluationContext);
            if(execResult.getResultCode() != StatementConstants.normalEnding)
                return execResult;
        }
        return execResult;
    }
    
    
	public void traverse(Traverser traverser)
    {
	    for(int i = 0; i < smtExpList.size(); i++)
        {
	        StatementExpression statement = (StatementExpression)smtExpList.get(i);
	        statement.traverse(traverser);
        }
    }

    public List getSmtExpList()
	{
	    return smtExpList;
	}
	
	public void setSmtExpList(List newSmtExpList)
	{
	    this.smtExpList = newSmtExpList;
	}
	
    
    public void setParentBlock(Statement newBlock)
    {
        this.parentBlock = newBlock;
    }
    
    public Statement getParentBlock()
    {
        return parentBlock;
    }
    
    public List getParsedSmtExpList()
    {
        return parsedSmtExpList;
    }
    
    public void setParsedSmtExpList(List newParsedSmtExpList)
    {
        this.parsedSmtExpList = newParsedSmtExpList;
    }
    
    /*The parent block of statement expression list*/
    private Statement parentBlock = null;
    
    private List smtExpList;
    
    private List parsedSmtExpList = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
