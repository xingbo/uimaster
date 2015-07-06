package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLIsNullExpression extends OQLExpression
{
    public OQLIsNullExpression(OQLBinaryExpression expr)
    {
        this.expr = expr;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        expr.visitWith(v);
        v.visitIsNullExpression(this);
    }
    
    private OQLBinaryExpression expr = null;

    private static final long serialVersionUID = 3821889063772047629L;
}
