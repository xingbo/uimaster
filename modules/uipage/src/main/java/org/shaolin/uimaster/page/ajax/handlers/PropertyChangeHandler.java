package org.shaolin.uimaster.page.ajax.handlers;

import org.apache.log4j.Logger;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.ajax.Widget;

/**
 * Property Change handler.
 * 
 * @author swu
 */
public class PropertyChangeHandler implements IAjaxHandler {
	private static Logger log = Logger.getLogger(PropertyChangeHandler.class);

	public PropertyChangeHandler() {
	}

	public String trigger(AjaxContext context) throws AjaxHandlerException {
		try {
			AjaxActionHelper.createAjaxContext(context);
			String uiid = context.getRequest().getParameter(
					AjaxContext.AJAX_UIID);
			String propertyName = context.getRequest().getParameter(
					"_valueName");
			String newValue = context.getRequest().getParameter("_value");
			Widget w = (Widget) context.getElement(uiid);
			if (!w.isSecure()) {
				//if the widget is in the secure mode, all attributes can't be updated.
				w.addAttribute(propertyName, newValue);
				if (log.isDebugEnabled())
					log.debug("uiid: " + uiid + ",propertyName: "
						+ context.getRequest().getParameter("_valueName")
						+ ",newValue: "
						+ context.getRequest().getParameter("_value"));
			}
		} finally {
			AjaxActionHelper.removeAjaxContext();
		}
		return "{}";
	}
}
