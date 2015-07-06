package org.shaolin.javacc.sql.node;

//imports
import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLSystemField extends OQLSimpleField
{
    public OQLSystemField(String fieldName)
    {
        this.fieldName = fieldName;
    }
    
    public String getFieldName()
    {
        return fieldName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitSystemField(this);
    }
    
    private String fieldName = null;

    private static final long serialVersionUID = 2980454113069505249L;
}
