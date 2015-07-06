package org.shaolin.uimaster.page.cache;

import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.od.ODContext;

public abstract class ODObject {

	protected String name = null;

	protected String uiEntityName = "";

	/**
	 * 'page od' op context.
	 * 
	 */
	protected transient OpExecuteContext opContext;

	public ODObject() {
		opContext = new OpExecuteContext();
	}

	protected void clearODObject() {
		uiEntityName = "";
		opContext = null;
		opContext = new OpExecuteContext();
	}

	public String getUIEntityName() {
		return uiEntityName;
	}

	/**
	 * Get local parsing context from this od entity.
	 * 
	 * od entity od page
	 * 
	 * @return
	 */
	public DefaultParsingContext getLocalPContext() {
		return (DefaultParsingContext) opContext
				.getParsingContextObject(ODContext.LOCAL_TAG);
	}

	/**
	 * Get global parsing context from this od entity.
	 * 
	 * od interface entity. od page
	 * 
	 * @return
	 */
	public DefaultParsingContext getGlobalPContext() {
		return (DefaultParsingContext) opContext
				.getParsingContextObject(ODContext.GLOBAL_TAG);
	}

}
