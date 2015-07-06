package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLFieldPart implements IOQLExpressionNode
{
    public OQLFieldPart(OQLTypeName typeName, OQLSimpleField simpleField)
    {
        this.typeName = typeName;
        this.simpleField = simpleField;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitFieldPart(this);
        if (typeName != null)
        {
            v.startVisitCast(this);
            typeName.visitWith(v);
            v.endVisitCast(this);
        }
        simpleField.visitWith(v);
    }
    
    public boolean isCategoryField()
    {
        return simpleField.isCategoryField();
    }
    
    private OQLTypeName typeName = null;
    private OQLSimpleField simpleField = null;

    private static final long serialVersionUID = -3353259950206780089L;
}
