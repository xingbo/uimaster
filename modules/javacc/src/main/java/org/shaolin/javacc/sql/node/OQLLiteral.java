package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLLiteral extends OQLSimpleExpression
{
    public OQLLiteral(String image)
    {
        this.image = image;
    }
    
    public String getImage()
    {
        return image;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitLiteral(this);
    }
    
    private String image = null;

    private static final long serialVersionUID = 7230722587187843834L;
}
