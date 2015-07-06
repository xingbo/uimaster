package org.shaolin.javacc.statement;

import java.io.Serializable;
import javax.swing.tree.*;

import org.shaolin.javacc.context.*;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The base class for all the statements in the syntax tree
 * 
 */
public abstract class Statement implements Serializable
{	
    /**
     * Execute the statement, will be implemented by each statement 
     * @throws ClassNotFoundException 
     * @throws EvaluationException 
     * @throws ParsingException 
     * @throws Exception 
     *
     *
     */
    abstract public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext);
    
    abstract public void parse(OOEESmtParsingContext parsingContext) throws ParsingException;
    
    abstract public void traverse(Traverser traverser);
    
    
    /**
     * Paint the ast tree of the statement
     * 
     */    
    public void paintAST(DefaultMutableTreeNode root)
    {
        root.add(new DefaultMutableTreeNode("Statement"));
    }

    public Statement()
    {
    	
    }
    
    public void setParentBlock(Statement newBlock)
    {
        this.parentBlock = newBlock;
    }
    
    public Statement getParentBlock()
    {
        return parentBlock;
    }
    
      
    /*The parent block of each statement*/
    protected Statement parentBlock = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
