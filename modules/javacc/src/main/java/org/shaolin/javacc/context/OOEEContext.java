package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The ooee context class for parsing and evaluation
 * 
 */
public class OOEEContext implements MultipleParsingContext, MultipleEvaluationContext,
        CustomFieldParsingContext, CustomFieldEvaluationContext, Cloneable
{
    private static Logger logger = LoggerFactory.getLogger(OOEEContext.class);
    
    /* the context objects used for parsing custom variables */
    private HashMap<String, ParsingContext> parsingContextObjects;

    private ParsingContext defaultParsingContext;

    /* the context objects used for evaluating custom variables */
    private HashMap<String, EvaluationContext> evaluationContextObjects;

    private EvaluationContext defaultEvaluationContext;

    protected OOEEContext()
    {
        parsingContextObjects = new HashMap<String, ParsingContext>();
        evaluationContextObjects = new HashMap<String, EvaluationContext>();
    }

    public void setDefaultParsingContext(ParsingContext parsingContext)
    {
        this.defaultParsingContext = parsingContext;
    }

    public void setDefaultEvaluationContext(EvaluationContext evaluationContext)
    {
        this.defaultEvaluationContext = evaluationContext;
    }

    /**
     * Get all prefix tags of nested parsing context
     * 
     * @return the prefix tags, list of String
     */
    public Collection<String> getAllContextTags()
    {
        return parsingContextObjects.keySet();
    }

    /**
     * Set the parsing context object of the specified prefix tag
     * 
     * @param tag
     *            the prefix tag of the context
     * @param parsingContext
     *            the context object
     */
    public void setParsingContextObject(String tag, ParsingContext parsingContext)
    {
        if ( tag == null || tag.equals("") )
        {
            logger.warn("The prefix tag for the parsing context you set is null or equals \"\", "
                    + "please make sure it's right. Maybe you should set this parsing context as "
                    + "the default parsing context.", new RuntimeException());
        }
        parsingContextObjects.put(tag, parsingContext);
    }

    /**
     * Get the parsing context object of the specified prefix tag
     * 
     * @param tag
     *            the prefix tag of the context
     * @return the context object
     */
    public ParsingContext getParsingContextObject(String tag)
    {
        return (ParsingContext) parsingContextObjects.get(tag);
    }

    /**
     * Remove the parsing context object of the specified prefix tag
     * 
     * @param tag
     *            the prefix tag of the context
     * @return the context object
     */
    public ParsingContext removeParsingContextObject(String tag)
    {
        return (ParsingContext) parsingContextObjects.remove(tag);
    }

    /**
     * Set the evaluation context object of the specified prefix tag
     * 
     * @param tag
     *            the prefix tag of the context
     * @param evaluationContext
     *            the context object
     */
    public void setEvaluationContextObject(String tag, EvaluationContext evaluationContext)
    {
        if ( tag == null || tag.equals("") )
        {
            logger.warn("The prefix tag for the evaluation context you set is null or equals \"\", "
                    + "please make sure it's right. Maybe you should set this evaluation context as "
                    + "the default evaluation context.",  new RuntimeException());
        }
        evaluationContextObjects.put(tag, evaluationContext);
    }

    /**
     * Get the evaluation context object of the specified prefix tag
     * 
     * @param tag
     *            the prefix tag of the context
     * @return the context object
     */
    public EvaluationContext getEvaluationContextObject(String tag)
    {
        return (EvaluationContext) evaluationContextObjects.get(tag);
    }

    /**
     * Remove the evaluation context object of the specified prefix tag
     * 
     * @param tag
     *            the prefix tag of the context
     * @return the context object
     */
    public EvaluationContext removeEvaluationContextObject(String tag)
    {
        return (EvaluationContext) evaluationContextObjects.remove(tag);
    }

    private String findParsingContext(String name)
    {
        String contextTag = null;

        for (Iterator<String> it = parsingContextObjects.keySet().iterator(); 
        		it.hasNext();)
        {
            String tag = it.next();
            if (name.startsWith(tag))
            {
                contextTag = tag;
                break;
            }
        }

        return contextTag;
    }

    /**
     * Get the variable type class for the given varialbe name in parsing time
     * 
     * @param name
     *            variable name
     * @return the variable type class
     * @throws ParsingException
     *             can't get the variable definition
     */
    public Class<?> getVariableClass(String name) throws ParsingException
    {
        String contextTag = findParsingContext(name);
        Class<?> result;
		if (contextTag == null) {
			if (defaultParsingContext == null) {
				throw new ParsingException(ExceptionConstants.EBOS_OOEE_031,
						new Object[] { name });
			} else {
				result = defaultParsingContext.getVariableClass(name);
			}
		} else {
			ParsingContext context = (ParsingContext) parsingContextObjects
					.get(contextTag);
			String variableName = name.substring(contextTag.length());
			result = context.getVariableClass(variableName);
		}

        return result;
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

        for (Iterator<String> keyIterator = parsingContextObjects.keySet().iterator(); 
        		keyIterator.hasNext();)
        {
            String prefix = keyIterator.next();
            ParsingContext parsingContext = (ParsingContext) parsingContextObjects.get(prefix);
            Collection variableNames = parsingContext.getAllVariableNames();
            for (Iterator nameIterator = variableNames.iterator(); nameIterator.hasNext();)
            {
                String varName = (String) nameIterator.next();
                allVariableNames.add(prefix + varName);
            }
        }

        if (defaultParsingContext != null)
        {
            allVariableNames.addAll(defaultParsingContext.getAllVariableNames());
        }

        return allVariableNames;
    }

    /**
     * Find the implementation method for the function with the given name and
     * argument type classes
     * 
     * @param name
     *            function name
     * @param argClasses
     *            list of argument type classes
     * @return the implementation method
     * @throws ParsingException
     *             can't find a proper method
     */
    public Method findMethod(String name, List argClasses) throws ParsingException
    {
        String contextTag = findParsingContext(name);
        Method result;
		if (contextTag == null) {
			if (defaultParsingContext == null) {
				throw new ParsingException(ExceptionConstants.EBOS_OOEE_031,
						new Object[] { name });
			} else {
				result = defaultParsingContext.findMethod(name, argClasses);
			}
		} else {
			ParsingContext context = (ParsingContext) parsingContextObjects
					.get(contextTag);
			String funcName = name.substring(contextTag.length());
			result = context.findMethod(funcName, argClasses);
		}
        return result;
    }

    private String findEvaluationContext(String name)
    {
        String contextTag = null;

        for (Iterator it = evaluationContextObjects.keySet().iterator(); it.hasNext();)
        {
            String tag = (String) it.next();
            if (name.startsWith(tag))
            {
                contextTag = tag;
                break;
            }
        }

        return contextTag;
    }

    /**
     * Get the variable value for the given varialbe name in evaluation time
     * 
     * @param name
     *            variable name
     * @return the variable value object
     * @throws EvaluationException
     *             can't get the variable value
     */
    public Object getVariableValue(String name) throws EvaluationException
    {
        String contextTag = findEvaluationContext(name);

        Object result;

        if (contextTag == null)
        {
            if (defaultEvaluationContext == null)
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_002,new Object[]{name});
              //  throw new EvaluationException("Can't find corresponding context for variable "
                //        + name);
            }
            else
            {
                result = defaultEvaluationContext.getVariableValue(name);
            }
        }
        else
        {
            EvaluationContext context = (EvaluationContext) evaluationContextObjects
                    .get(contextTag);

            String variableName = name.substring(contextTag.length());

            result = context.getVariableValue(variableName);
        }

        return result;
    }

    /**
     * Set the variable value for the given varialbe name in evaluation time
     * 
     * @param name
     *            variable name
     * @param value
     *            the variable value object
     * @throws EvaluationException
     *             can't get the variable value
     */
    public void setVariableValue(String name, Object value) throws EvaluationException
    {
        String contextTag = findEvaluationContext(name);

        if (contextTag == null)
        {
            if (defaultEvaluationContext == null)
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_002,new Object[]{name});
            //    throw new EvaluationException("Can't find corresponding context for variable "
              //          + name);
            }
            else
            {
                defaultEvaluationContext.setVariableValue(name, value);
            }
        }
        else
        {
            EvaluationContext context = (EvaluationContext) evaluationContextObjects
                    .get(contextTag);

            String variableName = name.substring(contextTag.length());

            context.setVariableValue(variableName, value);
        }
    }

    /**
     * Invoke the implementation method for the function with the given name,
     * argument type classes and argument objects
     * 
     * @param name
     *            function name
     * @param argClasses
     *            list of argument type classes
     * @param argObjects
     *            list of argument objects
     * @return the invocation result object
     * @throws EvaluationException
     *             invoke method error
     */
    public Object invokeMethod(String name, List argClasses, List argObjects)
            throws EvaluationException
    {
        String contextTag = findEvaluationContext(name);

        Object result;

        if (contextTag == null)
        {
            if (defaultEvaluationContext == null)
            {
            	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_002,new Object[]{name});
               // throw new EvaluationException("Can't find corresponding context for variable "
                 //       + name);
            }
            else
            {
                result = defaultEvaluationContext.invokeMethod(name, argClasses, argObjects);
            }
        }
        else
        {
            EvaluationContext context = (EvaluationContext) evaluationContextObjects
                    .get(contextTag);

            String funcName = name.substring(contextTag.length());

            result = context.invokeMethod(funcName, argClasses, argObjects);
        }

        return result;
    }

    /**
     * Find a custom field type with the the specified owner class and specified
     * field name
     * 
     * @param ownerClass
     *            the class type of the field owner
     * @param fieldName
     *            the name of the field
     * @return the class type of the field
     * @throws ParsingException
     *             if can't find a proper field
     */
    public Class getFieldClass(Class ownerClass, String fieldName) throws ParsingException
    {
        for (Iterator it = parsingContextObjects.values().iterator(); it.hasNext();)
        {
            ParsingContext parsingContext = (ParsingContext) it.next();
            if (parsingContext instanceof CustomFieldParsingContext)
            {
                try
                {
                    Class fieldClass = ((CustomFieldParsingContext) parsingContext).getFieldClass(
                            ownerClass, fieldName);
                    return fieldClass;
                }
                catch (ParsingException ex)
                {
                }
            }
        }

        if (defaultParsingContext instanceof CustomFieldParsingContext)
        {
            try
            {
                Class fieldClass = ((CustomFieldParsingContext) defaultParsingContext)
                        .getFieldClass(ownerClass, fieldName);
                return fieldClass;
            }
            catch (ParsingException ex)
            {
            }
        }

        throw new ParsingException(ExceptionConstants.EBOS_OOEE_033,new Object[]{fieldName,ownerClass});
    }

    /**
     * Get a custom field value with the the specified owner value and specified
     * field name
     * 
     * @param ownerClass
     *            the class type of the field owner
     * @param ownerValue
     *            the value of the field owner
     * @param fieldName
     *            the name of the field
     * @return the value of the field
     * @throws ParsingException
     *             if can't find a proper field
     */
    public Object getFieldValue(Class ownerClass, Object ownerValue, String fieldName)
            throws EvaluationException
    {
        for (Iterator it = evaluationContextObjects.values().iterator(); it.hasNext();)
        {
            EvaluationContext evaluationContext = (EvaluationContext) it.next();
            if (evaluationContext instanceof CustomFieldEvaluationContext)
            {
                try
                {
                    Object fieldValue = ((CustomFieldEvaluationContext) evaluationContext)
                            .getFieldValue(ownerClass, ownerValue, fieldName);
                    return fieldValue;
                }
                catch (EvaluationException ex)
                {
                }
            }
        }

        if (defaultEvaluationContext instanceof CustomFieldEvaluationContext)
        {
            try
            {
                Object fieldValue = ((CustomFieldEvaluationContext) defaultEvaluationContext)
                        .getFieldValue(ownerClass, ownerValue, fieldName);
                return fieldValue;
            }
            catch (EvaluationException ex)
            {
            }
        }

        throw new EvaluationException(ExceptionConstants.EBOS_OOEE_035,new Object[]{fieldName,ownerClass});
    }

    /**
     * Set a custom field value with the the specified owner value and specified
     * field name
     * 
     * @param ownerClass
     *            the class type of the field owner
     * @param ownerValue
     *            the value of the field owner
     * @param fieldName
     *            the name of the field
     * @param fieldValue
     *            the value of the field
     * @return the value of the field
     * @throws ParsingException
     *             if can't find a proper field
     */
    public void setFieldValue(Class ownerClass, Object ownerValue, String fieldName,
            Object fieldValue) throws EvaluationException
    {
        for (Iterator it = evaluationContextObjects.values().iterator(); it.hasNext();)
        {
            EvaluationContext evaluationContext = (EvaluationContext) it.next();
            if (evaluationContext instanceof CustomFieldEvaluationContext)
            {
                try
                {
                    ((CustomFieldEvaluationContext) evaluationContext).setFieldValue(ownerClass,
                            ownerValue, fieldName, fieldValue);
                    return;
                }
                catch (EvaluationException ex)
                {
                }
            }
        }

        if (defaultEvaluationContext instanceof CustomFieldEvaluationContext)
        {
            try
            {
                ((CustomFieldEvaluationContext) defaultEvaluationContext).setFieldValue(ownerClass,
                        ownerValue, fieldName, fieldValue);
                return;
            }
            catch (EvaluationException ex)
            {
            }
        }

        throw new EvaluationException(ExceptionConstants.EBOS_OOEE_035,new Object[]{fieldName,ownerClass});
    }

    /**
     * Clone a new OOEEContext
     */
    public Object clone()
    {
        OOEEContext cloneContext = null;
        try
        {
            cloneContext = (OOEEContext) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError();
        }
        cloneContext.defaultParsingContext = null;
        cloneContext.defaultEvaluationContext = null;
        return cloneContext;
    }

}
