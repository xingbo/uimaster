package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLName extends OQLSimpleExpression
{
    public OQLName(OQLTypeName typeName, OQLFieldName fieldName)
    {
        this.typeName = typeName;
        this.fieldName = fieldName;
    }
    
    public OQLTypeName getTypeName()
    {
        return typeName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.startVisitName(this);
        if (typeName != null)
        {
            typeName.visitWith(v);
        }
        v.visitName(this);
        fieldName.visitWith(v);
        v.endVisitName(this);
    }
    
    public boolean isName()
    {
        return true;
    }
    
    public boolean isCategoryField()
    {
        return fieldName.isCategoryField();
    }
    
    private OQLTypeName typeName = null;
    private OQLFieldName fieldName = null;

    private static final long serialVersionUID = 3996323820719246212L;
}
