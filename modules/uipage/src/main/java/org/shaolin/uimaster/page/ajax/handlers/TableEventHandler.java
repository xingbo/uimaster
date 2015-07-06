package org.shaolin.uimaster.page.ajax.handlers;

import org.hibernate.criterion.Order;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.ajax.Table;
import org.shaolin.uimaster.page.ajax.TableConditions;

/**
 * Table Event Handler.
 * 
 * @author swu
 */
public class TableEventHandler implements IAjaxHandler {

	public TableEventHandler() {
	}

	public String trigger(AjaxContext context) throws AjaxHandlerException {
		AjaxActionHelper.createAjaxContext(context);
		String uiid = context.getRequest().getParameter(AjaxContext.AJAX_UIID);
		String actionName = context.getRequest().getParameter("_actionName");
		Table comp = context.getTable(uiid);
		if (comp == null) {
			throw new AjaxHandlerException(uiid + " Table does not exist!");
		}
		if (actionName.endsWith("pull")) {
			int offset = Integer
					.valueOf(context.getRequest().getParameter("start"));
			int count = Integer
					.valueOf(context.getRequest().getParameter("length"));
			int columnIndex = Integer.valueOf(context.getRequest().getParameter(
					"order[0][column]"));
			String v = context.getRequest().getParameter("order[0][dir]");// :desc/asc
			boolean isAscending = v.equals("asc");
			
			TableConditions conditions = comp.getConditions();
			conditions.setCount(count);
			conditions.setOffset(offset);
			
			// TODO: only one order support temporory.
			String colId = comp.getColumnId(columnIndex);
			if (colId != null) {
				conditions.clearOrder();
				conditions.addOrder(isAscending ? Order.asc(colId) : Order
						.desc(colId));
			}
			return comp.refresh();
		} else {
			throw new AjaxHandlerException("Unsupported table action: " + actionName);
		}
	}
}
