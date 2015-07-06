package org.shaolin.javacc.sql.node;

public abstract class OQLSimpleField implements IOQLExpressionNode
{
    public boolean isCategoryField()
    {
        return false;
    }

    private static final long serialVersionUID = 2291418435386989200L;
}
