package org.shaolin.javacc.statement;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.shaolin.javacc.context.*;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The class for block node, contains a list of block statement
 * 
 */
public class Block extends ContextStatement
{

    public Block()
    {
        blockStatementList=new ArrayList();     
    }
    
    /**
     * Paint the ast tree of the block statement
     * 
     * @author LQZ
     */
    public void paintAST(DefaultMutableTreeNode root)
    {
    	DefaultMutableTreeNode block=new DefaultMutableTreeNode("Block");
        for(int i = 0; i < blockStatementList.size(); i++)
        {
        	BlockStatement blockSmt=(BlockStatement)blockStatementList.get(i);      		
            blockSmt.paintAST(block);
        }
        root.add(block);
    }       
    
    public List getBlockSmtList()
    {
        return blockStatementList;	
    }
    
    public void setBlockSmtList(List newBlockSmt)
    {
        this.blockStatementList=newBlockSmt;
    }
    
    /**
     * The execution of the block
     * 
     * @author LQZ
     * @throws Exception 
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) 
    {

        if(evaluationContext.getEvaluationConext(this) == null)
        {
            evaluationContext.putEvaluationConext(this, new DefaultEvaluationContext());
        }
        ExecutionResult execResult = new ExecutionResult();
        for(int i = 0; i < blockStatementList.size(); i++)
        {
            execResult = ((BlockStatement)blockStatementList.get(i)).execute(evaluationContext);
            if(execResult.getResultCode() != StatementConstants.normalEnding)
            {
                return execResult;
            }
        }
        return execResult;
        
    }
    
    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException 
    {
        if(parsingContext.getParsingContext(this) == null)
        {
            parsingContext.addParsingContext(this, new DefaultParsingContext());
        }
        for(int i = 0; i < blockStatementList.size(); i++)
        {
            ((BlockStatement)blockStatementList.get(i)).parse(parsingContext); 
        }
    }        
    public void traverse(Traverser traverser)
    {
        for(int i = 0; i < blockStatementList.size(); i++)
        {
          BlockStatement blockStatement =   (BlockStatement)blockStatementList.get(i);
          blockStatement.traverse(traverser);
        }
    }
    
    /*The list contained all the statements in the block */
    private List blockStatementList;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
