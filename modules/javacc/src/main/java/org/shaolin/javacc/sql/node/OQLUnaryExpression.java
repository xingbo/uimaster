package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLUnaryExpression implements IOQLExpressionNode
{
    public OQLUnaryExpression(String unaryOp, OQLExpression expr)
    {
        this.op = unaryOp;
        this.expr = expr;
    }
    
    public String getOperator()
    {
        return op;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitUnaryExpression(this);
        expr.visitWith(v);
    }
    
    public boolean isName()
    {
        return op == null && expr.isName();
    }
    
    public boolean isCategoryField()
    {
        return op == null && expr.isCategoryField();
    }
    
    public boolean isParam()
    {
        return op == null && expr.isParam();
    }
    
    private String op = null;
    private OQLExpression expr = null;

    private static final long serialVersionUID = -3137798247902195835L;
}
