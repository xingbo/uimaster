package org.shaolin.javacc.context;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.shaolin.javacc.exception.ParsingException;


/**
 * The parsing context interface for parsing
 *
 */
public interface ParsingContext extends Serializable
{
	/**
	 *  Get the definition type class of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @return     the variable type class
	 *  @throws     ParsingException     if can't find the variable with this name
	 */
	public Class getVariableClass(String name) throws ParsingException;
	
	/**
	 *	Get all variable names in this parsing context
	 *
	 *	@return		a list of string represents all variable names in this parsing context
	 */
	public Collection getAllVariableNames();
	
	/**
	 *  Find a method with the the specified name and specified argument type classes
	 *
	 *  @param      name    the method name
	 *  @param      argClasses     list of argument type classes
	 *  @return     the found method
	 *  @throws     ParsingException     if can't find a proper method
	 */
	public Method findMethod(String name, List argClasses) throws ParsingException;
}
