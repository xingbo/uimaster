package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;


/**
 * The evaluation context for merge several evaluation context
 *
 * @author Xiao Yi
 */
public class DelegateEvaluationContext implements EvaluationContext
{
	protected List evaluationContextList;
	
	public DelegateEvaluationContext()
	{
		evaluationContextList = new ArrayList();
	}
	
	public void addDelegateEvaluationContext(EvaluationContext evaluationContext)
	{
		evaluationContextList.add(evaluationContext);
	}
	
	public void removeDelegateEvaluationContext(EvaluationContext evaluationContext)
	{
		evaluationContextList.remove(evaluationContext);
	}
	
	public void clearAllDelegateEvaluationContext()
	{
		evaluationContextList.clear();
	}
	
	public EvaluationContext[] getAllDelegateEvaluationContext()
	{
		return (EvaluationContext[])evaluationContextList.toArray(new EvaluationContext[]{});
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
		for(int i = 0, n = evaluationContextList.size(); i < n; i++)
		{
			try
			{
				EvaluationContext evaluationContext = (EvaluationContext)evaluationContextList.get(i);
				Object result = evaluationContext.getVariableValue(name);
				return result;
			}
			catch(EvaluationException ex)
			{
			}
		}
		
		throw new EvaluationException(ExceptionConstants.EBOS_OOEE_005,new Object[]{name});
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
		for(int i = 0, n = evaluationContextList.size(); i < n; i++)
		{
			try
			{
				EvaluationContext evaluationContext = (EvaluationContext)evaluationContextList.get(i);
				evaluationContext.setVariableValue(name, value);
			}
			catch(EvaluationException ex)
			{
			}
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
		for(int i = 0, n = evaluationContextList.size(); i < n; i++)
		{
			try
			{
				EvaluationContext evaluationContext = (EvaluationContext)evaluationContextList.get(i);
				return evaluationContext.invokeMethod(name, argClasses, argObjects);
			}
			catch(EvaluationException ex)
			{
			}
		}
		
		throw new EvaluationException(ExceptionConstants.EBOS_OOEE_017,new Object[]{name});
	}

}
