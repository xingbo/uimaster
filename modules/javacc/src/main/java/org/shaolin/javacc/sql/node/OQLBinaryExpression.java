package org.shaolin.javacc.sql.node;

import java.util.List;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLBinaryExpression extends OQLExpression
{
    public OQLBinaryExpression(OQLUnaryExpression firstExpr, List nexts)
    {
        this.firstExpr = firstExpr;
        this.nexts = nexts;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.startVisitBinaryExpression(this);
        firstExpr.visitWith(v);
        for (int i = 0, n = nexts.size(); i < n; i += 2)
        {
            String op = (String)nexts.get(i);
            v.visitBinaryExpressionOp(op);
            OQLUnaryExpression unaryExpr = (OQLUnaryExpression)nexts.get(i + 1);
            unaryExpr.visitWith(v);
        }
        v.endVisitBinaryExpression(this);
    }
    
    public List getNexts()
    {
        return nexts;
    }
    
    public boolean isName()
    {
        return nexts.isEmpty() && firstExpr.isName();
    }
    
    public boolean isCategoryField()
    {
        return nexts.isEmpty() && firstExpr.isCategoryField();
    }
    
    public boolean isParam()
    {
        return nexts.isEmpty() && firstExpr.isParam();
    }
    
    private OQLUnaryExpression firstExpr = null;
    private List nexts = null;

    public static final String ___REVISION___ = "$Revision: 1.5 $";

    private static final long serialVersionUID = 1494397394889314880L;
}
