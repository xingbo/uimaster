package org.shaolin.javacc.context;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;


/**
 * The default parsing context implementation
 * 
 * @author Xiao Yi
 */
 
public class DefaultParsingContext implements ParsingContext, CustomFieldParsingContext, Cloneable
{
    //key:	varName String, value: varClass Class
    protected final Map variableTypes;	
    
    public DefaultParsingContext()
    {
        variableTypes = new HashMap();
    }
    
    public DefaultParsingContext(Map variableTypes)
    {
        this.variableTypes = new HashMap(variableTypes);
    }

    public Map getVariableTypes()
    {
    	return variableTypes;
    }
    
	/**
	 *  Get the definition type class of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @return     the variable type class
	 *  @throws     ParsingException     if can't find the variable with this name
	 */
    public Class getVariableClass(String name) throws ParsingException
    {
        if(!variableTypes.containsKey(name))
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_032);
        //    throw new ParsingException("Can't find definition for variable " + name);
        }
        
        Class result = (Class)variableTypes.get(name);
        
        //here in order to enable auto refresh of classes, reget the class using the class name
        if(result != null)
        {
        	try
        	{
        		result = ExpressionUtil.findClass(result.getName());
        	}
        	catch(ParsingException ex)
        	{
        	}
        }
        
        return result;
    }
    
	/**
	 *  Set the definition type class of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  @param      c       the variable type class
	 */
    public void setVariableClass(String name, Class c)
    {
        variableTypes.put(name, c);
    }

	/**
	 *  Remove the definition type class of a variable with the specified name
	 *
	 *  @param      name    the variable name
	 *  
	 */    
    public void removeVariableClass(String name)
    {
    	variableTypes.remove(name);
    }

	/**
	 *	Get all variable names in this parsing context
	 *
	 *	@return		a list of string represents all variable names in this parsing context
	 */
	public Collection getAllVariableNames()
	{
		return variableTypes.keySet();
	}

	/**
	 *	Remove all variable defintion in this parsing context
	 *
	 */
	public void removeAllVariables()
	{
		variableTypes.clear();
	}

	/**
	 *  Find a method with the the specified name and specified argument type classes
	 *  There's no custom method support in DefaultParsingContext, so it always throws ParsingException
	 *
	 *  @param      name    the method name
	 *  @param      argClasses     list of argument type classes
	 *  @return     the found method
	 *  @throws     ParsingException     if can't find a proper method
	 */
    public Method findMethod(String name, List argClasses) throws ParsingException
    {
    	throw new ParsingException(ExceptionConstants.EBOS_OOEE_046);
    }

	/**
	 *  Find a custom field type with the the specified owner class and specified field name
	 *
	 *  @param      ownerClass     the class type of the field owner
	 *  @param      fieldName      the name of the field
	 *  @return     the class type of the field
	 *  @throws     ParsingException     if can't find a proper field
	 */	
	public Class getFieldClass(Class ownerClass, String fieldName) throws ParsingException
	{
		throw new ParsingException(ExceptionConstants.EBOS_OOEE_044);
	}
	
    
    /**
     *  Clone a new DefaultParsingContext with all existing variable definition
     */
    public Object clone()
    {
        DefaultParsingContext cloneContext = null;
        try
        {
            cloneContext = (DefaultParsingContext)super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            throw new InternalError();
        }
        if (variableTypes != null)
        {
            cloneContext.variableTypes.clear();
            cloneContext.variableTypes.putAll(variableTypes);
        }
        return cloneContext;
    }

}
