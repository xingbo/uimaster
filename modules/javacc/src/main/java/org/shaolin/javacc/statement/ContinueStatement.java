package org.shaolin.javacc.statement;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The class for the continue statement node
 * 
 */
public class ContinueStatement extends Statement
{

	public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        ExecutionResult execResult = new ExecutionResult();
        execResult.setResultCode(StatementConstants.continueEnding);
        return execResult;
    }
    
    public void parse(OOEESmtParsingContext parsingContext) 
    {
      
    }
    
    public void traverse(Traverser traverser)
    {
      
    }
	
	public String getContinueID()
    {
        return continueID;
    }
    
    public void setContinueID(String newContinueID)
    {
        this.continueID = newContinueID;
	}
    
    public Statement getParentSmt()
    {
        return parentSmt;
    }
    
    public void setParentSmt(Statement newParentSmt)
    {
        this.parentSmt = newParentSmt;
    }
    
    /*The parent loop/switch statement of the break statement*/
    private Statement parentSmt = null;
    
    /*The continue IDENTIFIER for the continue statement*/
	private String continueID = null;
	
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;
    
}
