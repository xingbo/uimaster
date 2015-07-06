package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLConditionalExpression extends OQLExpression
{
    public OQLConditionalExpression(OQLBinaryExpression leftPart,
        String op, OQLBinaryExpression rightPart)
    {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.op = op;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        leftPart.visitWith(v);
        v.visitConditionalExpression(this);
        rightPart.visitWith(v);
    }
    
    public String getOperator()
    {
        return op;
    }
    
    private OQLBinaryExpression leftPart = null;
    private OQLBinaryExpression rightPart = null;
    private String op = null;

    private static final long serialVersionUID = 9025396631212991907L;
}
