package org.shaolin.javacc.context;

import java.util.*;
import java.lang.reflect.Method;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.Statement;


public class OOEESmtParsingContext implements ICacheableContext
{

    private ParsingContext parsingContext;

    private Map contextMap = new HashMap();

    private ICacheableContext cacheableCtx = null;

    public OOEESmtParsingContext(ParsingContext context, ICacheableContext cacheableContext)
    {
        parsingContext = context;
        this.cacheableCtx = cacheableContext;
    }

    public ParsingContext getParsingContext()
    {
        return parsingContext;
    }

    public void addParsingContext(Statement contextSmt,DefaultParsingContext context)
    {
        contextMap.put(contextSmt, context);
    }
    
    public DefaultParsingContext getParsingContext(Statement contextSmt)
    {
        return (DefaultParsingContext) contextMap.get(contextSmt);
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
        if (parsingContext == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_043);
           // throw new ParsingException("No Parsing Context Set");
        }
        else
        {
            return parsingContext.getVariableClass(name);
        }
    }

    /**
     * Get all variable names in this parsing context
     * 
     * @return a list of string represents all variable names in this parsing
     *         context
     */
    public Collection getAllVariableNames()
    {
        if (parsingContext == null)
        {
            return new ArrayList();
        }
        else
        {
            return parsingContext.getAllVariableNames();
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

    public Class findField(Class ownerClass, String fieldName) throws ParsingException
    {
        if (parsingContext instanceof CustomFieldParsingContext)
        {
            return ((CustomFieldParsingContext) parsingContext)
                    .getFieldClass(ownerClass, fieldName);
        }
        else
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_042);
        }
    }

    public void putClassObject(String className, Class classObject)
    {
        cacheableCtx.putClassObject(className, classObject);
    }

    public Class getClassObject(String className) throws ParsingException
    {
        return cacheableCtx.getClassObject(className);
    }
    
    public Class loadClass(String className) throws ParsingException
    {
        return cacheableCtx.loadClass(className);
    }
    
}
