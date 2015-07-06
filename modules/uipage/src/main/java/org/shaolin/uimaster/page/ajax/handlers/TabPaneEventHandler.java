package org.shaolin.uimaster.page.ajax.handlers;

import org.apache.log4j.Logger;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.ajax.TabPane;

/**
 * Property Change handler.
 * 
 * @author swu
 */
public class TabPaneEventHandler implements IAjaxHandler {
	private static Logger log = Logger.getLogger(TabPaneEventHandler.class);

	public TabPaneEventHandler() {
	}

	public String trigger(AjaxContext context) throws AjaxHandlerException {
		try {
			AjaxActionHelper.createAjaxContext(context);
			String uiid = context.getRequest().getParameter(
					AjaxContext.AJAX_UIID);
			String propertyName = context.getRequest().getParameter(
					"_valueName");
			String index = context.getRequest().getParameter("_value");
			TabPane comp = (TabPane) context.getElement(uiid);
			if (log.isDebugEnabled())
				log.debug("uiid: " + uiid + ",propertyName: "
						+ propertyName + ",newValue: " + index);
			if ("remveTabId".equals(propertyName)) {
				AjaxActionHelper.getAjaxContext().removeFramePage(index);
				return "";
			} else {
				return comp.loadContent(Integer.valueOf(index));
			}
		} catch (Exception e) {
			throw new AjaxHandlerException("Error", e);
		} finally {
			AjaxActionHelper.removeAjaxContext();
		}
	}
}
