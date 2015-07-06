package org.shaolin.javacc.sql.node;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class OQLCategoryField extends OQLSimpleField
{
    public OQLCategoryField(String objType, String in, String categoryName)
    {
        this.objType = objType;
        this.in = in;
        this.categoryName = categoryName;
    }
    
    public String getObjType()
    {
        return objType;
    }
    
    public String getIn()
    {
        return in;
    }
    
    public String getCategoryName()
    {
        return categoryName;
    }
    
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException
    {
        v.visitCategoryField(this);
    }
    
    public boolean isCategoryField()
    {
        return true;
    }
    
    private String objType = null;
    private String in = null;
    private String categoryName = null;

    private static final long serialVersionUID = 1773305039644769866L;
}
