package org.shaolin.javacc.context;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Method;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;


/**
 * an evaluation context implementation to combine multiple evaluation contexts together
 * 
 * @author Xiao Yi
 *
 */
public class CompoundEvaluationContext implements EvaluationContext
{
	static class ContextPair
	{
		ParsingContext parsingContext;
		EvaluationContext evaluationContext;
		
		public ContextPair() {
			super();
		}
		
		/**
		 * @param parsingContext
		 * @param evaluationContext
		 */
		public ContextPair(ParsingContext parsingContext,
				EvaluationContext evaluationContext) {
			super();
			this.parsingContext = parsingContext;
			this.evaluationContext = evaluationContext;
		}
		
		/**
		 * @return Returns the evaluationContext.
		 */
		public EvaluationContext getEvaluationContext() {
			return evaluationContext;
		}
		
		/**
		 * @param evaluationContext The evaluationContext to set.
		 */
		public void setEvaluationContext(EvaluationContext evaluationContext) {
			this.evaluationContext = evaluationContext;
		}
		
		/**
		 * @return Returns the parsingContext.
		 */
		public ParsingContext getParsingContext() {
			return parsingContext;
		}
		
		/**
		 * @param parsingContext The parsingContext to set.
		 */
		public void setParsingContext(ParsingContext parsingContext) {
			this.parsingContext = parsingContext;
		}
	}
	
	protected List contextPairList;
	 
	public CompoundEvaluationContext() {
		contextPairList = new ArrayList();
	}

	public CompoundEvaluationContext(List contextPairList) {
		this.contextPairList = contextPairList;
	}
	
	public void addContext(ParsingContext pc, EvaluationContext ec)
	{
		contextPairList.add(new ContextPair(pc, ec));
	}
	
	/* (non-Javadoc)
	 * @see bmiasia.ebos.ooee.context.EvaluationContext#getVariableValue(java.lang.String)
	 */
	public Object getVariableValue(String name) 
	throws EvaluationException 
	{
		Object value = null;
		
		for (Iterator it = contextPairList.iterator(); it.hasNext(); )
		{
			ContextPair cp = (ContextPair)it.next();
			if (cp.getParsingContext().getAllVariableNames().contains(name))
			{
				value = cp.getEvaluationContext().getVariableValue(name);
				break;
			}
		}
		
		return value;
	}

	/* (non-Javadoc)
	 * @see bmiasia.ebos.ooee.context.EvaluationContext#invokeMethod(java.lang.String, java.util.List, java.util.List)
	 */
	public Object invokeMethod(String name, List argClasses, List argObjects) 
	throws EvaluationException 
	{
		boolean invoked = false;
		Object returnValue = null;

		for (Iterator it = contextPairList.iterator(); it.hasNext(); )
		{
			ContextPair cp = (ContextPair)it.next();
			
			Method m = null;
			try
			{
				m = cp.getParsingContext().findMethod(name, argClasses);
			}
			catch (ParsingException ex)
			{
				// no such method
			}
			
			if (m != null)
			{
				returnValue = cp.getEvaluationContext().invokeMethod(name, argClasses, argObjects);
				invoked = true;
				break;
			}
		}
		
		if (invoked)
		{
			return returnValue;
		}
		else
		{
			throw new EvaluationException(ExceptionConstants.EBOS_OOEE_019,new Object[]{name});
		}
	}

	/* (non-Javadoc)
	 * @see bmiasia.ebos.ooee.context.EvaluationContext#setVariableValue(java.lang.String, java.lang.Object)
	 */
	public void setVariableValue(String name, Object value) throws EvaluationException {
		for (Iterator it = contextPairList.iterator(); it.hasNext(); )
		{
			ContextPair cp = (ContextPair)it.next();
			if (cp.getParsingContext().getAllVariableNames().contains(name))
			{
				cp.getEvaluationContext().setVariableValue(name, value);
				break;
			}
		}
	}

}
