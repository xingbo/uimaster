package org.shaolin.javacc.context;

//imports
import java.util.List;

import org.shaolin.javacc.exception.EvaluationException;


/**
 * The evaluation context interface for evaluation
 *
 * @author Xiao Yi
 */
public interface EvaluationContext
{
	/**
	 *  Get the value of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @return     the variable value object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
	public Object getVariableValue(String name) throws EvaluationException;

	/**
	 *  Set the value of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @param      value   the variable value object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
	public void setVariableValue(String name, Object value) throws EvaluationException;
	
	/**
	 *  Invoke a method with the the specified name and specified argument type classes and objects
	 *
	 *  @param      name    the method name
	 *  @param      argClasses     list of argument type classes
	 *  @param      argObjects     list of argument objects
	 *  @return     the invocation result object
	 *  @throws     EvaluationException     if can't find the variable with this name
	 */
	public Object invokeMethod(String name, List argClasses, List argObjects) throws EvaluationException;

}
