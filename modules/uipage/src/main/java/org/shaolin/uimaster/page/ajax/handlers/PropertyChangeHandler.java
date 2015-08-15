/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
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
