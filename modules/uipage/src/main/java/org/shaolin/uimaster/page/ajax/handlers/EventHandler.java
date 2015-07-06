package org.shaolin.uimaster.page.ajax.handlers;

import java.util.List;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.page.OpCallAjaxType;
import org.shaolin.bmdp.datamodel.page.OpInvokeWorkflowType;
import org.shaolin.bmdp.datamodel.page.OpType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.shaolin.uimaster.page.exception.AjaxInitializedException;

public class EventHandler implements IAjaxHandler {
	private static Logger log = Logger.getLogger(EventHandler.class);
	public static final String AJAX_ACTION_NAME = "_actionName";

	public String trigger(AjaxContext context) throws AjaxHandlerException {
		String actionName = null;
		try {
			actionName = context.getRequest().getParameter("_actionName");
			if ((actionName == null) || (actionName.length() == 0)) {
				throw new AjaxInitializedException(
						"The action name can not be empty!");
			}
			if (log.isDebugEnabled()) {
				log.debug("execute the function of ajax calling: " + actionName);
			}
			List<OpType> ops = null;
			Object value = null;
			if (PageCacheManager.isUIPage(context.getEntityName())) {
				UIPageObject uipage = PageCacheManager.getUIPageObject(context.getEntityName());
				ops = uipage.getUIForm().getEventHandler(actionName);
			} else {
				UIFormObject uiEntity = PageCacheManager.getUIFormObject(context.getEntityName());
				ops = uiEntity.getEventHandler(actionName);
			}
			if (ops == null) {
				return "";
			}
			for (OpType op : ops) {
				try {
					if (op instanceof OpCallAjaxType) {
						OpCallAjaxType callAjaxOp = (OpCallAjaxType) op;
						value = callAjaxOp.getExp().evaluate(context);
					} else if (op instanceof OpInvokeWorkflowType) {
						OpInvokeWorkflowType wfOp = (OpInvokeWorkflowType) op;
						Boolean flag = (Boolean)wfOp.getCondition().evaluate(context);
						if (flag.booleanValue()) {
							FlowEvent e = new FlowEvent(wfOp.getEventProducer());
							for (NameExpressionType nameExpr : wfOp.getOutDataMappings()) {
								e.setAttribute(nameExpr.getName(), nameExpr.getExpression().evaluate(context));
							}
							EventProcessor processor = AppContext.get().getService(EventProcessor.class);
							processor.process(e);
						}
					}
				} catch (EvaluationException ex) {
					log.warn("This statement can not be evaluated: \n" + op.toString());
					throw ex;
				}
			}

			context.synchVariables();
			if ((value != null) && (value.getClass() != Void.class)) {
				// if value is not null, return directly.
				if (log.isDebugEnabled()) {
					log.debug("evaluate result: " + value.toString());
				}
				if (((value instanceof Number)) || ((value instanceof String))
						|| ((value instanceof Boolean))) {
					return "{'value': '"+value.toString()+"'}";
				} else if ((value instanceof List)) {
					JSONArray json = new JSONArray((List) value);
					return json.toString();
				} else {
					JSONObject json = new JSONObject(value);
					return json.toString();
				}
			} else {
				if (log.isInfoEnabled()) {
					context.printUiMap();
				}
				return context.getDataAsJSON();
			}
		} catch (Throwable ex) {
			log.warn("Ajax executor has interrupted when execute " + actionName
					+ " ajax calling: " + ex.getMessage(), ex);
			throw new AjaxHandlerException("Ajax executor has interrupted.", ex);
		} 
	}

}
