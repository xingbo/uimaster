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
