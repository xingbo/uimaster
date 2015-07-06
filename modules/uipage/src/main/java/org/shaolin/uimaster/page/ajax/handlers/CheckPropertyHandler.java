package org.shaolin.uimaster.page.ajax.handlers;

import org.apache.log4j.Logger;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.AjaxActionHelper;

/**
 * Check Property handler.
 * 
 * @author
 */
public class CheckPropertyHandler implements IAjaxHandler {
	private static Logger log = Logger.getLogger(CheckPropertyHandler.class);

	public CheckPropertyHandler() {
	}

	public String trigger(AjaxContext context) throws AjaxHandlerException {
		try {
			AjaxActionHelper.createAjaxContext(context);

			String chunkName = context.getRequest().getParameter("_chunkName");
			String nodeName = context.getRequest().getParameter("_nodeName");
			String outName = context.getRequest().getParameter("_outName");

			// TODO::
			boolean isNeed = false;

			if (log.isDebugEnabled())
				log.debug("_chunkName: " + chunkName + ", _nodeName: "
						+ nodeName + ", _outName: " + outName
						+ ", isAjaxHandling: " + isNeed);

			return "{'value':'" + String.valueOf(isNeed) + "'}";
		} finally {
			AjaxActionHelper.removeAjaxContext();
		}
	}
}
