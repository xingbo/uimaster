package org.shaolin.uimaster.page.ajax;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.ajax.json.IDataItem;

/**
 * Due to javacc does not support the anonymous class definition in the script directly.
 * 
 * @author wushaol
 *
 */
public class TableCallBack implements CallBack {

	private final String uiid;
	
	private final String entityPrefix;
	
	public TableCallBack(String uiid) {
		this.entityPrefix = AjaxActionHelper.getAjaxContext().getEntityPrefix();
		this.uiid = uiid;
	}
	
	public void execute() {
        Table table = (Table)AjaxActionHelper.getAjaxContext().getElementByAbsoluteId(entityPrefix + uiid);
        IDataItem item = AjaxActionHelper.updateTableItem(entityPrefix + uiid, table.refresh());
        AjaxActionHelper.getAjaxContext().addDataItem(item);
	}
}
