package org.shaolin.javacc;

//imports
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultCacheableContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.ParsingException;


/**
 * The class for expression parser
 *
 * @author Xiao Yi
 */

public class ExpressionParser
{
    /**
     *  Parse the expression using default OOEEContext as the Parsing Context
     *
     *  @param      expressionString
     *  @return     parsedexpression
     *  @throws     ParsingException     if the expression string is invalid
     *  @see        #parse(String)
     */
    public static Expression parse(String expressionString) throws ParsingException
    {
        return parse(expressionString, OOEEContextFactory.createOOEEContext());
    }
    
    /**
     *  Parse the expression using the given Parsing Context
     *
     *  @param      expressionString
     *  @return     parsed expression
     *  @throws     ParsingException     if the expression string is invalid
     *  @see        #parse(String, ParsingContext)
     */
    public static Expression parse(String expressionString, ParsingContext context) throws ParsingException
    {
        try
        {
	        Expression result = new Expression(expressionString, context, new DefaultCacheableContext());
	        
	        return result;
	    }
	    catch(Throwable t)
	    {
	    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_039,t,new Object[]{expressionString});
	    }
    }

}
