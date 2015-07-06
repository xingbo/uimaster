package org.shaolin.javacc.sql.node;

import java.util.List;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLFieldName implements IOQLExpressionNode
{
    public OQLFieldName(List fieldParts)
    {
        this.fieldParts = fieldParts;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.startVisitFieldName(this);
        for (int i = 0, n = fieldParts.size(); i < n; i++)
        {
            OQLFieldPart fieldPart = (OQLFieldPart)fieldParts.get(i);
            fieldPart.visitWith(v);
        }
        v.endVisitFieldName(this);
    }
    
    public boolean isCategoryField()
    {
        return fieldParts.size() == 1 &&
            ((OQLFieldPart)fieldParts.get(0)).isCategoryField();
    }
    
    private List fieldParts = null;

    private static final long serialVersionUID = 4377191884870158089L;
}
