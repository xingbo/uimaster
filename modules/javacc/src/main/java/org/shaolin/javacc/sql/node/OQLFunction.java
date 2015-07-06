package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLFunction extends OQLSimpleExpression
{
    public OQLFunction(String functionName, OQLBinaryExpression param, boolean isCountAll)
    {
        this.functionName = functionName;
        this.param = param;
        this.isCountAll = isCountAll;
    }
    
    public String getFunctionName()
    {
        return functionName;
    }
    
    //is count(*) ?
    public boolean isCountAllFunction()
    {
        return isCountAll;
    }
        
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.startVisitFunction(this);
        if (param != null)
        {
            param.visitWith(v);
        }
        v.endVisitFunction(this);
    }
    
    private String functionName;
    private OQLBinaryExpression param;
    private boolean isCountAll;

    private static final long serialVersionUID = -5040441814886417126L;
}
