package org.shaolin.javacc.sql.node;

public abstract class OQLExpression implements IOQLExpressionNode
{
    //We should add getType() method to avoid these is() methods.
    
    public boolean isName()
    {
        return false;
    }
    
    public boolean isCategoryField()
    {
        return false;
    }
    
    public boolean isParam()
    {
        return false;
    }

    private static final long serialVersionUID = -5746053095979534408L;
}
