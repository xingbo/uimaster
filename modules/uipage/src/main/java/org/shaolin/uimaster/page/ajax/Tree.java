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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.od.ODContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shaolin Wu
 */
public class Tree extends Widget implements Serializable {
	private static final long serialVersionUID = -1744731434666233557L;
	
	private static final Logger logger = LoggerFactory.getLogger(Tree.class);

	private final TreeConditions conditions = new TreeConditions();

	private Map<String, Object> dataModel = new HashMap<String, Object>();
	
	private String selectedParentNode;
	
	private String selectedNodeName;
	
	private final ExpressionType initExpr;
	
	public Tree(String tableId, HttpServletRequest request) {
		super(tableId, null);
		this.initExpr = null;
	}

	public Tree(String id, Layout layout, ExpressionType initExpr) {
		super(id, layout);
		this._setWidgetLabel(id);
		this.initExpr = initExpr;
	}

	public void setDataModel(Map<String, Object> newModel) {
		this.dataModel = new HashMap<String, Object>();
		this.dataModel.putAll(newModel);
	}
	
	public void addItem(String key, Object object) {
		this.dataModel.put(key, object);
	}

	public void addAttribute(String name, Object value, boolean update) {
		if ("selectedNode".equals(name)) {
			conditions.setSelectedId(value.toString());
		} else if ("selectedParentNode".equals(name)) {
			selectedParentNode = value.toString();
		} else if ("selectedNodeName".equals(name)) {
			selectedNodeName = value.toString();
		} else {
			super.addAttribute(name, value, update);
		}
	}
	
	public String getSelectedNodeName() {
		return selectedNodeName;
	}
	
	public String getSelectedParentItemId() {
		return selectedParentNode;
	}

	public Object getSelectedObject() {
		if (this.dataModel == null) {
			return null;
		}
		return this.dataModel.get(this.getSelectedItemId());
	}
	
	public String getSelectedItemId() {
		return conditions.getSelectedId();
	}

	public List<String> getSelectedItems() {
		// TODO:
		return null;
	}

	public TreeConditions getConditions() {
		return conditions;
	}

	/**
	 * After when called addRow,removeRow,removeAll,updateRow, we have to call
	 * this method refreshing data set.
	 */
	public void refresh() {
		try {
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
			evaContext.setVariableValue("treeCondition", conditions);
			evaContext.setVariableValue("page", AjaxActionHelper.getAjaxContext());
			evaContext.setVariableValue("tree", this);
			ooeeContext.setDefaultEvaluationContext(evaContext);
			ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);
			List<TreeItem> result = (List<TreeItem>)initExpr.evaluate(ooeeContext);
			
			JSONArray jsonArray = new JSONArray(result);
			IDataItem dataItem = AjaxActionHelper.createDataItem();
			dataItem.setUiid(this.getId());
			dataItem.setJsHandler(IJSHandlerCollections.TREE_REFRESH);
			dataItem.setData(jsonArray.toString());
			dataItem.setFrameInfo(this.getFrameInfo());

			AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
			ajaxContext.addDataItem(dataItem);
		} catch (Exception e) {
			logger.error("error occurrs while refreshing tree: " + this.getId(), e);
		}
	}

}
