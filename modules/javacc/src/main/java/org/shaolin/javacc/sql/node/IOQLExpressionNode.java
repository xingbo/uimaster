package org.shaolin.javacc.sql.node;

import java.io.Serializable;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public interface IOQLExpressionNode extends Serializable
{
    public void visitWith(IOQLNodeVisitor v) throws QueryParsingException;

}
