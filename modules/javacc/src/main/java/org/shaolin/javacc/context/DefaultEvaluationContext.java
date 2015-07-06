package org.shaolin.javacc.context;

import java.util.*;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;


/**
 * The default evaluation context implementation
 */
public class DefaultEvaluationContext implements EvaluationContext, CustomFieldEvaluationContext, Cloneable
{
    protected Map variableObjects;
    
    public DefaultEvaluationContext()
    {
        variableObjects = new HashMap();
    }
    
    public DefaultEvaluationContext(Map varValues)
    {
    	if(varValues == null)
    	{
    		throw new I18NRuntimeException(ExceptionConstants.EBOS_OOEE_023);
    	}
    	
    	this.variableObjects = varValues;
    }
    
    public Map getVariableObjects()
    {
        return variableObjects;
    }

    public boolean hasVariable(String name) {
    	return variableObjects.containsKey(name);
    }
    
	/**
	 *  Get the value of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @return     the variable value object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
    public Object getVariableValue(String name) throws EvaluationException
    {
        if(!variableObjects.containsKey(name))
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_003,new Object[]{name});
        }

        return variableObjects.get(name);
    }

    /**
     *  Init the variable's value as null, since setVariableValue method will check
     *  whether there exists a variable with the specified name, so user must first
     *  call this method to init a variable 
     *
     */
    public void initVariable(String name)
    {
        variableObjects.put(name, null);
    }
    
	/**
	 *  Set the value of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @param      value   the variable value object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
    public void setVariableValue(String name, Object value) throws EvaluationException
    {
        /*
        if(!variableObjects.containsKey(name))
        {
           // throw new EvaluationException("Can't find object for variable " + name);
        }
		*/
        variableObjects.put(name, value);
    }
    
	/**
	 *  Invoke a method with the the specified name and specified argument type classes and objects
	 *
	 *  @param      name    the method name
	 *  @param      argClasses     list of argument type classes
	 *  @param      argObjects     list of argument objects
	 *  @return     the invocation result object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
    public Object invokeMethod(String name, List argClasses, List argObjects) throws EvaluationException
    {
    	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_018);
    }

	/**
	 *  Set a custom field value with the the specified owner value and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      ownerValue     the value of the field owner
	 *  @param      fieldName      the name of the field
	 *  @param      fieldValue     the value of the field
	 *  @return     the value of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */	
	public void setFieldValue(Class ownerClass, Object ownerValue, String fieldName, Object fieldValue) throws EvaluationException
	{
		throw new EvaluationException(ExceptionConstants.EBOS_OOEE_016);
	}
	    
	/**
	 *  Get a custom field value with the the specified owner value and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      ownerValue     the value of the field owner
	 *  @param      fieldName      the name of the field
	 *  @return     the value of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */	
	public Object getFieldValue(Class ownerClass, Object ownerValue, String fieldName) throws EvaluationException
	{
		throw new EvaluationException(ExceptionConstants.EBOS_OOEE_016);
	}

    /**
     *  Clone a new DefaultEvaluationContext with all existing variable values
     */
    public Object clone()
    {
        DefaultEvaluationContext cloneContext = null;
        try
        {
            cloneContext = (DefaultEvaluationContext)super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            throw new InternalError();
        }
        if (variableObjects != null)
        {
            cloneContext.variableObjects = new HashMap(variableObjects.size());
            cloneContext.variableObjects.putAll(variableObjects);
        }
        return cloneContext;
    }

}
