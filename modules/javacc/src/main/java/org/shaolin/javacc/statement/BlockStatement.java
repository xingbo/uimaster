package org.shaolin.javacc.statement;

import javax.swing.tree.DefaultMutableTreeNode;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.traverser.Traverser;


/**
 * The class for block statement node
 * 
 */
public class BlockStatement extends Statement
{

    /**
     * 
     * @author LQZ
     * @throws Exception
     */
    public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext)
    {
        ExecutionResult execResult = new ExecutionResult();
        if (this.localVar != null)
        {
            execResult = localVar.execute(evaluationContext);
        }
        else if (this.blockSmt != null)
        {
            execResult = blockSmt.execute(evaluationContext);
        }
        return execResult;
    }

    public void parse(OOEESmtParsingContext parsingContext) throws ParsingException
    {
        if (this.localVar != null)
        {
            localVar.parse(parsingContext);
        }
        else if (this.blockSmt != null)
        {
            blockSmt.parse(parsingContext);
        }
    }

    public void traverse(Traverser traverser)
    {
        if (this.localVar != null )
        {
           localVar.traverse(traverser);
        }
        else if (blockSmt != null)
        {
            blockSmt.traverse(traverser);
        }
    }

    /**
     * Paint the ast tree of the block statement element
     * 
     * @author LQZ
     */
    public void paintAST(DefaultMutableTreeNode root)
    {
        if (localVar == null && blockSmt != null)
            blockSmt.paintAST(root);
        else if (localVar != null)
            localVar.paintAST(root);
    }

    public LocalVariableDeclaration getLocalVar()
    {
        return localVar;
    }

    public void setLocalVar(LocalVariableDeclaration newLocalVar)
    {
        this.localVar = newLocalVar;
    }

    public Statement getBlockSmt()
    {
        return blockSmt;
    }

    public void setBlockSmt(Statement newBlockSmt)
    {
        this.blockSmt = newBlockSmt;
    }

    /* The variable declaration contained in block */
    private LocalVariableDeclaration localVar;

    /* The statement element contained in block statement */
    private Statement blockSmt;

    /* The serial ID */
    private static final long serialVersionUID = 1357924680L;

}
