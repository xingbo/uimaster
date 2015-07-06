package org.shaolin.javacc.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.statement.Statement;


public class OOEESmtEvaluationContext 
{
    private EvaluationContext evaluationContext;

    private Map contextMap = new HashMap();
    
    public OOEESmtEvaluationContext(EvaluationContext context)
    {
        evaluationContext = context;
    }
       
    public void setEvaluationContext(EvaluationContext evaluationContext)
    {
        this.evaluationContext = evaluationContext;
    }
    
    public EvaluationContext getEvaluationContext()
    {
        return evaluationContext;
    }
    
    public DefaultEvaluationContext getEvaluationConext(Statement stmt)
    {
        return (DefaultEvaluationContext) contextMap.get(stmt);
    }
    
    public void putEvaluationConext(Statement stmt, DefaultEvaluationContext context)
    {
        contextMap.put(stmt, context);
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
        if(evaluationContext == null)
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_015);
            //throw new EvaluationException("No Evaluation Context Set");
        }
        else
        {
            return evaluationContext.getVariableValue(name);
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
        if(evaluationContext == null)
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_OOEE_015);
        }
        else
        {
            evaluationContext.setVariableValue(name, value);
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
          //  throw new EvaluationException("No Custom Field Evaluation Context Set");
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
           // throw new EvaluationException("No Custom Field Evaluation Context Set");
        }       
    }

}
