package org.shaolin.javacc.context;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.lang.reflect.Method;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.ParsingException;


/**
 * an parsing context implementation to combine multiple parsing contexts together
 * 
 * @author Xiao Yi
 *
 */
public class CompoundParsingContext implements ParsingContext
{
	protected List contextList;
	
	/**
	 * @return Returns the contextList.
	 */
	public List getContextList() {
		return contextList;
	}
	
	/**
	 * @param contextList The contextList to set.
	 */
	public void setContextList(List contextList) {
		this.contextList = contextList;
	}
	
	public CompoundParsingContext()
	{
		contextList = new ArrayList();
	}
	
	public CompoundParsingContext(ParsingContext context)
	{
		this();
		addParsingContext(context);
	}
	
	public void addParsingContext(ParsingContext context)
	{
		contextList.add(context);
	}

	public Method findMethod(String name, List argClasses) throws ParsingException 
	{
		Method m = null;

		for (Iterator it = contextList.iterator(); it.hasNext(); )
		{
			ParsingContext c = (ParsingContext)it.next();
			
			m = null;

			try
			{
				m = c.findMethod(name, argClasses);
			}
			catch (ParsingException pe)
			{
				;
			}

			if (m != null)
			{
				break;
			}
		}

		if (m == null)
		{
			throw new ParsingException(ExceptionConstants.EBOS_OOEE_045,new Object[]{name});
		}
		else
		{
			return m;
		}
	}

	public Collection getAllVariableNames() 
	{
		List variableNames = new ArrayList();

		for (Iterator it = contextList.iterator(); it.hasNext(); )
		{
			ParsingContext c = (ParsingContext)it.next();
			variableNames.addAll(c.getAllVariableNames());
		}
		
		return variableNames;
	}

	public Class getVariableClass(String name) throws ParsingException 
	{
		for (Iterator it = contextList.iterator(); it.hasNext(); )
		{
			ParsingContext c = (ParsingContext)it.next();
			if (c.getAllVariableNames().contains(name))
			{
				return c.getVariableClass(name);
			}
		}

		throw new ParsingException(ExceptionConstants.EBOS_OOEE_032,new Object[]{name});
	}

}
