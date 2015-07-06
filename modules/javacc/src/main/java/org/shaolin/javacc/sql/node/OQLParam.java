package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLParam extends OQLSimpleExpression
{
    public OQLParam(String paramName)
    {
        this.paramName = paramName;
    }
    
    public String getParamName()
    {
        return paramName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitParam(this);
    }
    
    public boolean isParam()
    {
        return true;
    }
    
    private String paramName = null;

    private static final long serialVersionUID = -6474357129892220171L;
}
