package org.shaolin.javacc.statement;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The class for the breakstatement node
 * 
 */
public class BreakStatement extends Statement 
{

    /**
     * The execution of the break statement
     * @throws Exception 
     */
	public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        execResult.setResultCode(StatementConstants.breakEnding);
        return execResult;

    }
    
    public void parse(OOEESmtParsingContext parsingContext) 
    {
    
    }
    
    public void traverse(Traverser traverser)
    {
        
    }
       
	public String getBreakID()
	{
        return breakID;
	}
	
	public void setBreakID(String newBreakID)
	{
        this.breakID = newBreakID;
	}
    
    public Statement getParentSmt()
    {
        return parentSmt;
    }
    
    public void setParentSmt(Statement newParentSmt)
    {
        this.parentSmt = newParentSmt;
    }

    /*The bread IDENTIFIER for the break statement*/
    private String breakID = null;
    
    /*The parent loop/switch statement of the break statement*/
    private Statement parentSmt = null;

    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
