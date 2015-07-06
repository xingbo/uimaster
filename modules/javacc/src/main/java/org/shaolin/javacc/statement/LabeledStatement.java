package org.shaolin.javacc.statement;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The class for the Labeled statement node
 * 
 */
public class LabeledStatement extends Statement 
{	
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        ExecutionResult execResult = new ExecutionResult();
        execResult = labeledStatement.execute(evaluationContext);
        return execResult;
    }
    
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        labeledStatement.parse(parsingContext);
    }
    
    public Statement getLabeledStatment()
    {
        return labeledStatement;
    }
    
    public void setLabeledStatement(Statement labeledSmt)
    {
        this.labeledStatement=labeledSmt;
    }
    
    public void traverse(Traverser traverser)
    {
       if (labeledStatement != null)
       {
           labeledStatement.traverse(traverser);
       }
    }
	

    /*The statement of the labeled statement*/
    private Statement labeledStatement;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
