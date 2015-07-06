package org.shaolin.javacc.context;

import org.shaolin.javacc.exception.EvaluationException;

/**
 * the plugin interface to provide an customer field evaluation function
 * 
 * @author  Xiao Yi
 */
public interface CustomFieldEvaluationContext {
	/**
	 *  Get a custom field value with the the specified owner value and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      ownerValue     the value of the field owner
	 *  @param      fieldName      the name of the field
	 *  @return     the value of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */	
	public Object getFieldValue(Class ownerClass, Object ownerValue, String fieldName) throws EvaluationException;

	/**
	 *  Set a custom field value with the the specified owner value and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      ownerValue     the value of the field owner
	 *  @param      fieldName      the name of the field
	 *  @return     the value of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */		
	public void setFieldValue(Class ownerClass, Object ownerValue, String fieldName, Object fieldValue) throws EvaluationException;	

}
