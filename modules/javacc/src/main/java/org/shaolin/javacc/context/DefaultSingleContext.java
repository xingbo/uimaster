package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;


/**
 * The default single context implementation, merge parsingcontext, evaluation
 * context together
 * 
 * @author Xiao Yi
 */

public class DefaultSingleContext implements ParsingContext, EvaluationContext,
        CustomFieldParsingContext, CustomFieldEvaluationContext, Cloneable
{
    private HashMap variableTypes;
    private HashMap variableObjects;

    public DefaultSingleContext()
    {
        variableTypes = new HashMap();
        variableObjects = new HashMap();
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
        if (!variableTypes.containsKey(name))
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_032,new Object[]{name});
        }

        Class result = (Class) variableTypes.get(name);

        // here in order to enable auto refresh of classes, reget the class
        // using the class name
        if (result != null)
        {
            try
            {
                result = ExpressionUtil.findClass(result.getName());
            }
            catch (ParsingException ex)
            {
            }
        }

        return result;
    }

    /**
     * Set the definition type class of a variable with the specified name
     * 
     * @param name
     *            the variable name
     * @param c
     *            the variable type class
     */
    public void setVariableClass(String name, Class c)
    {
        variableTypes.put(name, c);
    }

    /**
     * Remove the definition type class of a variable with the specified name
     * 
     * @param name
     *            the variable name
     * 
     */
    public void removeVariableClass(String name)
    {
        variableTypes.remove(name);
    }

    /**
     * Get all variable names in this parsing context
     * 
     * @return a list of string represents all variable names in this parsing
     *         context
     */
    public Collection getAllVariableNames()
    {
        return variableTypes.keySet();
    }

    /**
     * Get the value of a variable with the specified name
     * 
     * @param name
     *            the variable name
     * @return the variable value object
     * @throws EvaluationException
     *             if can't find the variable with this name
     */
    public Object getVariableValue(String name) throws EvaluationException
    {
        if (!variableObjects.containsKey(name))
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_003,new Object[]{name});
         //   throw new EvaluationException("Can't find object for variable " + name);
        }

        Object result = variableObjects.get(name);

        return result;
    }

    /**
     * Init the variable's value as null, since setVariableValue method will
     * check whether there exists a variable with the specified name, so user
     * must first call this method to init a variable
     * 
     */
    public void initVariable(String name)
    {
        variableObjects.put(name, null);
    }

    /**
     * Set the value of a variable with the specified name
     * 
     * @param name
     *            the variable name
     * @param value
     *            the variable value object
     * @throws EvaluationException
     *             if can't find the variable with this name
     */
    public void setVariableValue(String name, Object value) throws EvaluationException
    {
        /*
         * if(!variableObjects.containsKey(name)) { throw new
         * EvaluationException("Can't find object for variable " + name); }
         */
        variableObjects.put(name, value);
    }

    /**
     * Find a method with the the specified name and specified argument type
     * classes There's no custom method support in DefaultParsingContext, so it
     * always throws ParsingException
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
    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_046);
    //    throw new ParsingException("No plugin method supported in DefaultEvaluationContext");
    }

    /**
     * Invoke a method with the the specified name and specified argument type
     * classes and objects
     * 
     * @param name
     *            the method name
     * @param argClasses
     *            list of argument type classes
     * @param argObjects
     *            list of argument objects
     * @return the invocation result object
     * @throws EvaluationException
     *             if can't find the variable with this name
     */
    public Object invokeMethod(String name, List argClasses, List argObjects)
            throws EvaluationException
    {
    	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_018);
       // throw new EvaluationException("No plugin method supported in DefaultEvaluationContext");
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
    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_044);
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
    	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_016);
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
    	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_016);
    }

    /**
     * Clone a new DefaultSingleContext
     */
    public Object clone()
    {
        DefaultSingleContext cloneContext = null;
        try
        {
            cloneContext = (DefaultSingleContext) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError();
        }
        cloneContext.variableTypes = (HashMap) variableTypes.clone();
        cloneContext.variableObjects = (HashMap) variableObjects.clone();
        return cloneContext;
    }

}
