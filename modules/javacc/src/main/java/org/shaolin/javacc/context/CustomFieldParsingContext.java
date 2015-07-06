package org.shaolin.javacc.context;

import org.shaolin.javacc.exception.ParsingException;

/**
 * the plugin interface to provide an customer field parse function 
 *
 * @author  Xiao Yi
 */
public interface CustomFieldParsingContext {
	/**
	 *  Find a custom field type with the the specified owner class and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      fieldName      the name of the field
	 *  @return     the class type of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */	
	public Class getFieldClass(Class ownerClass, String fieldName) throws ParsingException;    

}
