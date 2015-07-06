package org.shaolin.javacc.statement;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.symbol.ExpressionNode;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for the assert statement node
 * 
 */
public class AssertStatement extends Statement 
{

	public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {
        ExecutionResult execResult = new ExecutionResult();
        return execResult;
    }
    
    public void parse(OOEESmtParsingContext parsingContext) 
    {
   
    }
    public ExpressionNode getAssertExp()
    {
        return assertExp;
    }
    
    public void setAssertExp(ExpressionNode newAssertExp)
    {
        this.assertExp = newAssertExp;
    }
    
    public ExpressionNode getValueExp()
    {
        return valueExp;
    }
    
    public void setValuetExp(ExpressionNode newValueExp)
    {
        this.valueExp = newValueExp;
    }
    
    public void traverse(Traverser traverser)
    {
        if (assertExp != null)
        {
            assertExp.traverse(traverser);
        }
        if (valueExp != null)
        {
            valueExp.traverse(traverser);
        }
    }
   
	/*The assert expression in the assert statement*/
    private ExpressionNode assertExp = null;
    
    /*The value expression in the assert statement*/
    private ExpressionNode valueExp = null;

    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
