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
package org.shaolin.uimaster.page;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.DataItem;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.IRequestData;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.ajax.json.RequestData;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author wushaol
 */
public class AjaxActionHelper {

	private static final ThreadLocal<AjaxContext> threadLocal = new ThreadLocal<AjaxContext>();

	/**
	 * create ajax context.
	 * 
	 * @param context
	 */
	public static void createAjaxContext(AjaxContext context) {
		threadLocal.set(context);
	}

	/**
	 * get ajax context
	 * 
	 * @return the ajaxcontext gotten by thread local
	 */
	public static AjaxContext getAjaxContext() {
		return threadLocal.get();
	}

	public static void removeAjaxContext() {
		threadLocal.set(null);
	}

	public static AjaxContext createAjaxContext(String entityUiid, String uiform, HttpServletRequest request) 
			throws EvaluationException {
		Map uiMap = AjaxActionHelper.getFrameMap(request);
		IRequestData requestData = AjaxActionHelper.createRequestData();
		requestData.setUiid(entityUiid);
		requestData.setEntityName(uiform);
		requestData.setEntityUiid(entityUiid);
        requestData.setFrameId("");
		Widget comp = (Widget)uiMap.get(requestData.getUiid());
        if (comp == null) {
            throw new IllegalStateException("Can not find this component[" + requestData.getUiid() + "] in the UI map!");
        }
        AjaxContext context = new AjaxContext(uiMap, requestData);
        context.initData();
        return context;
	}
	
	/**
	 * get frame map which is decide to the '_framePrefix' parameter.
	 * 
	 * @param request
	 * @return the frame map
	 */
	public static Map<?, ?> getFrameMap(HttpServletRequest request) {
		String framePrefix = request.getParameter("_framePrefix");
		Map<?, ?> ajaxComponentMap = (Map<?, ?>) request.getSession()
				.getAttribute(AjaxContext.AJAX_COMP_MAP);
		Map<?, ?> pageComponentMap;
		if (framePrefix == null || "null".equalsIgnoreCase(framePrefix)
				|| framePrefix.length() == 0) {
			pageComponentMap = (Map<?, ?>) ajaxComponentMap.get(AjaxContext.GLOBAL_PAGE);
		} else {
			pageComponentMap = (Map<?, ?>) ajaxComponentMap.get(framePrefix);
		}
		if (pageComponentMap == null) {
			LoggerFactory.getLogger(AjaxActionHelper.class).warn(
					"Cannot found frame map by this frame prefix["
							+ framePrefix + "].");
		}
		return pageComponentMap;
	}

	/**
	 * get frame map which is decide to the '_framePrefix' parameter.
	 * 
	 * @param framePrefix
	 * @param ajaxComponentMap
	 * @return
	 */
	public static Map<?, ?> getFrameMap(String framePrefix,
			Map<?, ?> ajaxComponentMap) {
		Map<?, ?> pageComponentMap;
		if (framePrefix == null || "null".equalsIgnoreCase(framePrefix)
				|| framePrefix.length() == 0) {
			pageComponentMap = (Map<?, ?>) ajaxComponentMap.get(AjaxContext.GLOBAL_PAGE);
		} else {
			pageComponentMap = (Map<?, ?>) ajaxComponentMap.get(framePrefix);
		}
		if (pageComponentMap == null) {
			LoggerFactory.getLogger(AjaxActionHelper.class).warn(
					"Cannot found frame map by this frame prefix["
							+ framePrefix + "].");
		}
		return pageComponentMap;
	}

	/**
	 * get ui map in whole page, it includes all frame maps.
	 * 
	 * @param session
	 * @return ui map which is in the whole page.
	 */
	public static Map<?, ?> getAjaxWidgetMap(HttpSession session) {
		if (session != null) {
			return (Map<?, ?>) session.getAttribute(AjaxContext.AJAX_COMP_MAP);
		}
		return Collections.EMPTY_MAP;
	}
	
	public static Widget getCachedAjaxWidget(String name,
			HTMLSnapshotContext htmlContext) {
		Map uiMap = AjaxActionHelper.getFrameMap(htmlContext.getRequest());
		Object obj = uiMap.get(name);
		if (obj == null) {
			throw new IllegalArgumentException("Can not be found this uiid["
					+ name + "] in UI map!");
		}
		return (Widget) obj;
	}


	public static IRequestData createRequestData() {
		return new RequestData();
	}

	public static IDataItem createDataItem() {
		IDataItem dataItem = new DataItem();
		dataItem.setJsHandler(IJSHandlerCollections.MSG_INFO);
		return dataItem;
	}

	public static IDataItem createErrorDataItem(String msg) {
		IDataItem dataItem = new DataItem();
		dataItem.setJsHandler(IJSHandlerCollections.MSG_ERROR);
		dataItem.setData(msg);
		return dataItem;
	}

	public static IDataItem createJavaItem() {
		IDataItem dataItem = new DataItem();
		dataItem.setJsHandler(IJSHandlerCollections.JAVA_OBJECT);
		return dataItem;
	}

	public static IDataItem createDataItem(IRequestData data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(data.getUiid());
		dataItem.setJsHandler(IJSHandlerCollections.MSG_INFO);
		return dataItem;
	}

	public static IDataItem createAppendItem(String parentId, String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setParent(parentId);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_APPEND);
		return dataItem;
	}

	public static IDataItem createPrependItem(String parentId, String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setParent(parentId);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_PREPEND);
		return dataItem;
	}

	public static IDataItem createInsertBeforeItem(String siblingId, String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setSibling(siblingId);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_INSERTBEFORE);
		return dataItem;
	}
	
	public static IDataItem createAppendItemToTab(String parentId, String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setParent(parentId);
		dataItem.setJsHandler(IJSHandlerCollections.TAB_APPEND);
		return dataItem;
	}

	public static IDataItem createInsertAfterItem(String siblingId, String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setSibling(siblingId);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_INSERTAFTER);
		return dataItem;
	}

	public static IDataItem createRemoveItem(String parentId, String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setParent(parentId);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_REMOVE);
		return dataItem;
	}

	public static IDataItem createReadOnlyItem(String uiid) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_UPDATE_READONLY);
		return dataItem;
	}

	public static IDataItem updateAttrItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_UPDATE_ATTR);
		return dataItem;
	}

	public static IDataItem updateEventItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_UPDATE_EVENT);
		return dataItem;
	}

	public static IDataItem updateCssItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_UPDATE_CSS);
		return dataItem;
	}

	public static IDataItem updateConstraintItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_UPDATE_CONST);
		return dataItem;
	}

	public static IDataItem removeAttrItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_REMOVE_ATTR);
		return dataItem;
	}

	public static IDataItem removeEventItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_REMOVE_EVENT);
		return dataItem;
	}

	public static IDataItem removeCssItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_REMOVE_CSS);
		return dataItem;
	}

	public static IDataItem removeConstraintItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.HTML_REMOVE_CONST);
		return dataItem;
	}

	public static IDataItem updateTableItem(String uiid, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setUiid(uiid);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.TABLE_UPDATE);
		return dataItem;
	}
	
	public static IDataItem createSessionTimeOut(String msg) {
		IDataItem dataItem = new DataItem();
		dataItem.setJsHandler(IJSHandlerCollections.SESSION_TIME_OUT);
		dataItem.setData(msg);
		return dataItem;
	}
	
	public static IDataItem createNoPermission(String msg) {
		IDataItem dataItem = new DataItem();
		dataItem.setJsHandler(IJSHandlerCollections.NO_PERMISSION);
		dataItem.setData(msg);
		return dataItem;
	}

	public static IDataItem createPermitSubmitItem(String sourceFrame,
			String targetFrame, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setFrameInfo(sourceFrame);
		dataItem.setParent(targetFrame);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.PERMIT_SUBMIT);
		return dataItem;
	}

	public static String generateSuccessfulJSONResponse(String sourceFrame,
			String targetFrame, String htmlKey) {
		IDataItem dataItem = createPermitSubmitItem(sourceFrame, targetFrame,
				htmlKey);
		return "[" + (new JSONObject(dataItem)).toString() + "]";
	}

	public static IDataItem createReSubmitItem(String sourceFrame,
			String targetFrame, String data) {
		IDataItem dataItem = new DataItem();
		dataItem.setFrameInfo(sourceFrame);
		dataItem.setParent(targetFrame);
		dataItem.setData(data);
		dataItem.setJsHandler(IJSHandlerCollections.PAGE_RE_SUBMIT);
		return dataItem;
	}

	public static String generateReSubmitJSONResponse(String sourceFrame,
			String targetFrame, String data) {
		IDataItem dataItem = createReSubmitItem(sourceFrame, targetFrame, data);
		return "[" + (new JSONObject(dataItem)).toString() + "]";
	}

}
