package org.shaolin.javacc;

//imports
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.parser.Parser;
import org.shaolin.javacc.statement.*;


/**
 * The class for the statement parsing
 * 
 * @author LQZ
 *
 */
public class StatementParser 
{

    /**
     * Parse the compilation unit with the default ooee context
     * 
     * @param statementString
     * @return CompilationUnit
     * @throws ParsingException
     */
    public static CompilationUnit parse(String statementString) throws ParsingException
    {
        return parse(statementString, OOEEContextFactory.createOOEEContext());
    }
    
    /**
     * Parse the compilation unit with the given context
     * 
     * @param statementString
     * @param parsingContext
     * @return CompilationUnit
     * @throws ParsingException
     */
    public static CompilationUnit parse(String statementString, ParsingContext parsingContext) throws ParsingException
    {
        try
        {
            CompilationUnit result = Parser.parseStatement(statementString);
            result.parse(parsingContext);
            return result;
        }
        catch(Throwable t)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_039,t,new Object[]{statementString});
        }
    }

}
