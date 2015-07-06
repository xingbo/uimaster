package org.shaolin.javacc.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.statement.ContextStatement;

public class BlockEvaluationContext extends DefaultEvaluationContext 
{
    
    public BlockEvaluationContext(ContextStatement context, OOEESmtEvaluationContext ooeeContext)
    {
        this.contextSmt = context;
        this.evaluationContext = ooeeContext;
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
        variableObjects = evaluationContext.getEvaluationConext(contextSmt).getVariableObjects();
        ContextStatement tempCtxSmt = contextSmt;
        while(!variableObjects.containsKey(name) && tempCtxSmt != null)
        {
            tempCtxSmt = (ContextStatement) tempCtxSmt.getParentBlock();
            if(tempCtxSmt != null)
                variableObjects = evaluationContext.getEvaluationConext(tempCtxSmt).getVariableObjects();
        }
        if(tempCtxSmt != null)
        {
            Object result = variableObjects.get(name);
            contextMap.put(name, variableObjects);
            return result;
        }
        else
        {
            Object result = evaluationContext.getEvaluationContext().getVariableValue(name);
            return result;
        }

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
        if(contextMap.containsKey(name))
        {
            variableObjects = (Map) contextMap.get(name);
            variableObjects.put(name, value);
        }
        else 
        {
            evaluationContext.getEvaluationContext().setVariableValue(name, value);
        }
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
		if(evaluationContext == null)
		{
			throw new EvaluationException(ExceptionConstants.EBOS_OOEE_015);
		}
		else
		{
			return evaluationContext.invokeMethod(name, argClasses, argObjects);
		}
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
		if(evaluationContext instanceof CustomFieldEvaluationContext)
		{
			return ((CustomFieldEvaluationContext)evaluationContext).getFieldValue(ownerClass, ownerValue, fieldName);			
		}
		else
		{
			throw new EvaluationException(ExceptionConstants.EBOS_OOEE_014);
		}		
	}
	
	/**
	 *  Set a custom field value with the the specified owner value and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      ownerValue     the value of the field owner
	 *  @param      fieldName      the name of the field
	 *  @return     the value of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */	
	public void setFieldValue(Class ownerClass, Object ownerValue, String fieldName, Object fieldValue) throws EvaluationException
	{
		if(evaluationContext instanceof CustomFieldEvaluationContext)
		{
			((CustomFieldEvaluationContext)evaluationContext).setFieldValue(ownerClass, ownerValue, fieldName, fieldValue);			
		}
		else
		{
			throw new EvaluationException(ExceptionConstants.EBOS_OOEE_014);
		}		
	}

       
    private ContextStatement contextSmt ;
    
    private Map contextMap = new HashMap();
    
    private OOEESmtEvaluationContext evaluationContext = null;
    
}
