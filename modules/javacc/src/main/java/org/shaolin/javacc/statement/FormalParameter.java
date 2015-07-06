package org.shaolin.javacc.statement;

import java.io.Serializable;

import org.shaolin.javacc.symbol.ExpressionNode;

/**
 * The class for the formal parameter node
 * 
 */
public class FormalParameter implements Serializable
{
	
    public ExpressionNode getType()
	{
        return type;
	}
	
    public void setType(ExpressionNode newType)
    {
        this.type = newType;
    }
    
    public String getParameterID()
    {
        return parameterID;
    }
    
    public void setParameterID(String newParameterID)
    {
        this.parameterID = newParameterID;
    }
    
    
    public void setParentBlock(Statement newBlock)
    {
        this.parentBlock = newBlock;
    }
    
    public Statement getParentBlock()
    {
        return parentBlock;
    }
    
    /*The type for the formal parameter*/
    private ExpressionNode type = null;
    
    /*The IDENTIFIER for the formal parameter*/
    private String parameterID = null;
    
    /*The parent block of formal parameter*/
    protected Statement parentBlock = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
