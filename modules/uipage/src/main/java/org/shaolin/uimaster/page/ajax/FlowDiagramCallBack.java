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
package org.shaolin.uimaster.page.ajax;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.ajax.json.IDataItem;

/**
 * Due to javacc does not support the anonymous class definition in the script directly.
 * 
 * @author wushaol
 *
 */
public class FlowDiagramCallBack implements CallBack {

	private final String uiid;
	
	private final String entityPrefix;
	
	public FlowDiagramCallBack(String uiid) {
		this.entityPrefix = AjaxActionHelper.getAjaxContext().getEntityPrefix();
		this.uiid = uiid;
	}
	
	public void execute() {
        Table table = (Table)AjaxActionHelper.getAjaxContext().getElementByAbsoluteId(entityPrefix + uiid);
        IDataItem item = AjaxActionHelper.updateTableItem(entityPrefix + uiid, table.refresh());
        AjaxActionHelper.getAjaxContext().addDataItem(item);
	}
}
