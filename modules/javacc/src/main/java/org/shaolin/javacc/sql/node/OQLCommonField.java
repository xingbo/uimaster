package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLCommonField extends OQLSimpleField
{
    public OQLCommonField(String fieldName)
    {
        this.fieldName = fieldName;
    }
    
    public String getFieldName()
    {
        return fieldName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitCommonField(this);
    }
    
    private String fieldName = null;

    private static final long serialVersionUID = 6428241496689361207L;
}
