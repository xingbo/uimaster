package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;


/**
 * The parsing context class for parsing
 * 
 * @author Xiao Yi
 */
public class OOEEParsingContext implements ICacheableContext
{
    private ParsingContext parsingContext;

    private List valueStack;

    private ICacheableContext cacheableContext = null;

    public OOEEParsingContext(ICacheableContext context)
    {
        parsingContext = null;
        valueStack = new ArrayList();
        this.cacheableContext = context;
    }

    public void setParsingContext(ParsingContext parsingContext)
    {
        this.parsingContext = parsingContext;
    }

    public ParsingContext getParsingContext()
    {
        return parsingContext;
    }

    public Object stackPeek()
    {
        return valueStack.get(valueStack.size() - 1);
    }

    public Object stackPop()
    {
        return valueStack.remove(valueStack.size() - 1);
    }

    public void stackPush(Object obj)
    {
        valueStack.add(obj);
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
         //   throw new ParsingException("No Parsing Context Set");
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
           // throw new ParsingException("No Parsing Context Set");
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

    public Class getClassObject(String className)  throws ParsingException
    {
        return this.cacheableContext.getClassObject(className);
    }

    public void putClassObject(String className, Class classObject)
    {
        cacheableContext.putClassObject(className, classObject);
    }
    
    public Class loadClass(String className) throws ParsingException
    {
        return cacheableContext.loadClass(className);
    }

}
