package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLExtendField extends OQLSimpleField
{
    public OQLExtendField(String fieldName)
    {
        this.fieldName = fieldName;
    }
    
    public String getFieldName()
    {
        return fieldName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitExtendField(this);
    }
    
    private String fieldName = null;

    private static final long serialVersionUID = 2606896145662708323L;
}
