package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLTypeName implements IOQLExpressionNode
{
    public OQLTypeName(String typeName, String custRDBName)
    {
        this.typeName = typeName;
        this.custRDBName = custRDBName;
    }
    
    public String getTypeName()
    {
        return typeName;
    }
    
    public String getCustRDBName()
    {
        return custRDBName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitTypeName(this);
    }
    
    public String toString()
    {
        if (custRDBName == null)
        {
            return typeName;
        }
        return typeName + "[" + custRDBName + "]";
    }
    
    private String typeName = null;
    private String custRDBName = null;

    public static final String ___REVISION___ = "$Revision: 1.4 $";

    private static final long serialVersionUID = -5008992505127572835L;
}
