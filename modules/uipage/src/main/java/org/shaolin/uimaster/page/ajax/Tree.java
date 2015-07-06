package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONObject;

/**
 * @author Shaolin Wu
 */
public class Tree extends Widget implements Serializable {
	private static final long serialVersionUID = -1744731434666233557L;

	private final TreeConditions conditions = new TreeConditions();

	private Map<String, Object> dataModel = new HashMap<String, Object>();
	
	private String selectedParentNode;
	
	private String selectedNodeName;
	
	public Tree(String tableId, HttpServletRequest request) {
		super(tableId, null);
	}

	public Tree(String id, Layout layout) {
		super(id, layout);
		this._setWidgetLabel(id);
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
	public void refresh(String item) {
	}

	private void refresh(ArrayList result) {
		JSONArray array = new JSONArray();
		for (Object object : result) {
			array.put(new JSONObject(object));
		}

		IDataItem dataItem = AjaxActionHelper.createDataItem();
		dataItem.setUiid(this.getId());
		dataItem.setJsHandler(IJSHandlerCollections.TREE_REFRESH);
		dataItem.setData(array.toString());
		dataItem.setFrameInfo(this.getFrameInfo());
		dataItem.setParent(this.getSelectedItemId());

		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
		ajaxContext.addDataItem(dataItem);
	}

}
