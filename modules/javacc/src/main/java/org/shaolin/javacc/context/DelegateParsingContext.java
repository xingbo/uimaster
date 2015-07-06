package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;


/**
 * The parsing context for merge several parsing context
 * 
 * @author Xiao Yi
 */
public class DelegateParsingContext implements ParsingContext
{
    protected List parsingContextList;

    public DelegateParsingContext()
    {
        parsingContextList = new ArrayList();
    }

    public void addDelegateParsingContext(ParsingContext parsingContext)
    {
        parsingContextList.add(parsingContext);
    }

    public void removeDelegateParsingContext(ParsingContext parsingContext)
    {
        parsingContextList.remove(parsingContext);
    }

    public void clearAllDelegateParsingContext()
    {
        parsingContextList.clear();
    }

    public ParsingContext[] getAllDelegateParsingContext()
    {
        return (ParsingContext[]) parsingContextList.toArray(new ParsingContext[] {});
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
        for (int i = 0, n = parsingContextList.size(); i < n; i++)
        {
            try
            {
                ParsingContext parsingContext = (ParsingContext) parsingContextList.get(i);
                return parsingContext.getVariableClass(name);
            }
            catch (ParsingException ex)
            {
            }
        }

        throw new ParsingException(ExceptionConstants.EBOS_OOEE_032,new Object[]{name});
       //throw new ParsingException("Can't find definition for variable " + name);
    }

    /**
     * Get all variable names in this parsing context
     * 
     * @return a list of string represents all variable names in this parsing
     *         context
     */
    public Collection getAllVariableNames()
    {
        List allVariableNames = new ArrayList();

        for (int i = 0, n = parsingContextList.size(); i < n; i++)
        {
            ParsingContext parsingContext = (ParsingContext) parsingContextList.get(i);
            allVariableNames.addAll(parsingContext.getAllVariableNames());
        }

        return allVariableNames;
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
        for (int i = 0, n = parsingContextList.size(); i < n; i++)
        {
            try
            {
                ParsingContext parsingContext = (ParsingContext) parsingContextList.get(i);
                return parsingContext.findMethod(name, argClasses);
            }
            catch (ParsingException ex)
            {
            }
        }
        throw new ParsingException(ExceptionConstants.EBOS_OOEE_045,new Object[]{name});
    }

}
