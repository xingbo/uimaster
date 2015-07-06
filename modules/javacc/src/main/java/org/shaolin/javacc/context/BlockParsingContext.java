package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.ContextStatement;
import org.shaolin.javacc.util.ExpressionUtil;


public class BlockParsingContext extends DefaultParsingContext
{
    private ContextStatement contextSmt;

    private OOEESmtParsingContext parsingContext = null;

    public BlockParsingContext(ContextStatement context, OOEESmtParsingContext ooeeContext)
    {
        this.contextSmt = context;
        this.parsingContext = ooeeContext;
    }

    /**
     * Get the definition type class of a variable with the specified name
     * 
     * @param name
     *            the variable name
     * @return the variable type class
     * @throws ParsingException
     *             if can't find the variable with this name
     */
    public Class getVariableClass(String name) throws ParsingException
    {
    	variableTypes.clear();
        variableTypes.putAll(parsingContext.getParsingContext(contextSmt).getVariableTypes());
        ContextStatement tempCtxSmt = contextSmt;
        while (!variableTypes.containsKey(name) && tempCtxSmt != null)
        {
            tempCtxSmt = (ContextStatement) tempCtxSmt.getParentBlock();
            if (tempCtxSmt != null)
                variableTypes.putAll(parsingContext.getParsingContext(tempCtxSmt).getVariableTypes());
        }
        if (tempCtxSmt != null)
        {
            Class result = (Class) variableTypes.get(name);

            // here in order to enable auto refresh of classes, reget the class
            // using the class name
            if (result != null)
            {
                try
                {
                    result = ExpressionUtil.findClass(result.getName(), parsingContext);
                }
                catch (ParsingException ex)
                {
                }
            }
            return result;
        }
        else
        {
            Class result = parsingContext.getParsingContext().getVariableClass(name);
            if (result != null)
            {
                try
                {
                    result = ExpressionUtil.findClass(result.getName(), parsingContext);
                }
                catch (ParsingException ex)
                {
                }
            }
            return result;
        }
    }
    
    /**
     * Find a method with the the specified name and specified argument type
     * classes
     * 
     * @param name
     *            the method name
     * @param argClasses
     *            list of argument type classes
     * @return the found method
     * @throws ParsingException
     *             if can't find a proper method
     */
    public Method findMethod(String name, List argClasses) throws ParsingException
    {
        if (parsingContext == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_043);
        }
        else
        {
            return parsingContext.findMethod(name, argClasses);
        }
    }

}
