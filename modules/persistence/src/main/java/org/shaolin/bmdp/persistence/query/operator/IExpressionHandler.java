package org.shaolin.bmdp.persistence.query.operator;

import org.shaolin.bmdp.persistence.query.generator.QueryParsingContext;
import org.shaolin.javacc.sql.exception.QueryParsingException;

/**
 * A handler registered against a query operator to parse 
 * the expression.
 * 
 */
public interface IExpressionHandler 
{
    public  String  parse(String leftColumn, Object rightColumn,
        Operator anOpr, Object value, QueryParsingContext parsingContext) throws QueryParsingException;
    
    public  String  parseUpdate(String leftColumn, Object rightColumn, Operator anOpr,
        Object value, QueryParsingContext parsingContext) throws QueryParsingException;

    public int getParamCopyCount();
    
}
