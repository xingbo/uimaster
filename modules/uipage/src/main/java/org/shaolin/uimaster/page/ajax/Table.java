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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.page.UITableColumnType;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.javacc.UIVariableUtil;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.report.ImportTableToExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTable didn't have supported 'addRow', 'removeRow', 'updateRow' in
 * Auto-search mode. when we used these methods, we have to implement a
 * user-define SPI. Code Example: <br>
 * <br>
 * bmiasia.ebos.appbase.ajax.type.DTable dtable =
 * (bmiasia.ebos.appbase.ajax.type.DTable)@page.getElement("Form"); <br>
 * if( dtable == null ) <br>
 * { <br>
 * java.util.Map map = new java.util.HashMap(); <br>
 * map.put("sqName","bmiasia.ebos.appbase.test.sq.NormalEmployee");//according
 * to od component. <br>
 * map.put("serviceName","..."); <br>
 * dtable = new bmiasia.ebos.appbase.ajax.type.DTable("dtable1", map); <br>
 * Panel rootPanel = (Panel)@page.getElement("Form"); <br>
 * rootPanel.append(dtable); <br>
 * <br>
 * else <br>
 * { <br>
 * ... <br>
 * dtable.addRow(rowObject); <br>
 * dtable.refresh();// have to refresh. <br>
 * <br>
 * <br>
 * 
 * 
 * @author Shaolin Wu
 */
public class Table extends Widget implements Serializable {
	private static final long serialVersionUID = -1744731434666233557L;

	private static final Logger logger = LoggerFactory.getLogger(Table.class);
	
	private TableConditions conditions = TableConditions.createCondition();

	private transient List<Object> listData;

	private List<Object> addItems;
	
	private List<Object> deleteItems;
	
	private List<Object> updateItems;
	
	private ExpressionType queryExpr;

	private ExpressionType totalExpr;
	
	private List<UITableColumnType> columns;

	public Table(String tableId, HttpServletRequest request) {
		super(tableId, null);
	}

	public Table(String id, Layout layout) {
		super(id, layout);
		this._setWidgetLabel(id);
	}

	public void addAttribute(String name, Object value, boolean update)
    {
		if ("selectedIndex".equals(name)) {
			conditions.setCurrentSelectedIndex(Integer.valueOf(value.toString()));
		} else if ("conditions".equals(name)) {
			try {
				JSONArray array = new JSONArray(value.toString());
				int length = array.length();
				for (int i=0; i<length; i++) {
					JSONObject item = array.getJSONObject(i);
					String _name = item.getString("name");
					String _value = item.getString("value");
					this.updateFilter(_name.trim(), _value.trim());
				}
			} catch (JSONException e) {
				logger.error("error occurrs while synchronizing the value from the page.", e);
			}
		} else if ("bodyJson".equals(name)) {
			try {
				// sync the body updated cells.
				JSONArray array = new JSONArray(value.toString());
				int length = array.length();
				for (int i=0; i<length; i++) {
					JSONObject item = array.getJSONObject(i);
					if (!item.has("updated")) {
						continue;
					}
					Object rowObject = this.listData.get(i);
					for (UITableColumnType col : columns) {
						this.updateCell(col, col.getBeFieldId(), item.getString(col.getBeFieldId()).trim(), rowObject);
					}
					if (this.addItems == null || !isNewAdded(rowObject)) {
						if (this.updateItems == null) {
							this.updateItems = new ArrayList<Object>();
						}
						if (!this.updateItems.contains(rowObject)) {
							this.updateItems.add(rowObject);
						}
					}
				}
			} catch (JSONException e) {
				logger.error("error occurrs while synchronizing the value from the page.", e);
			}
		} else {
			super.addAttribute(name, value, update);
		}
    }
	
	private boolean isNewAdded(Object rowObject) {
		if (this.addItems != null) {
			return this.addItems.contains(rowObject);
		}
		return false;
	}
	
	public List<Object> getSelectedRows() {
		if (conditions.getSelectedIndex() == null 
				|| conditions.getSelectedIndex().length == 0) {
			return Collections.emptyList();
		}
		List<Object> selectedRows = new ArrayList<Object>();
		for (int i : conditions.getSelectedIndex()) {
			selectedRows.add(listData.get(i));
		}
		return selectedRows;
	}
	
	public Object getSelectedRow() {
		if (conditions.getCurrentSelectedIndex() < 0 
				|| conditions.getCurrentSelectedIndex() > listData.size()) {
			return null;
		}
		return listData.get(conditions.getCurrentSelectedIndex());
	}

	public int getSelectedIndex() {
		return conditions.getCurrentSelectedIndex();
	}
	
	public List<Object> getListData() {
		if (listData == null) {
			listData = new ArrayList<Object>();
		}
		return listData;
	}

	public void setListData(List<Object> listData) {
		this.listData = listData;
	}

	public List<Object> getAddItems() {
		if (addItems == null) {
			addItems = new ArrayList<Object>();
		}
		return addItems;
	}

	public List<Object> getDeleteItems() {
		if (deleteItems == null) {
			deleteItems = new ArrayList<Object>();
		}
		return deleteItems;
	}

	public List<Object> getUpdateItems() {
		if (updateItems == null) {
			updateItems = new ArrayList<Object>();
		}
		return updateItems;
	}
	
	public TableConditions getConditions() {
		return conditions;
	}

	public void setConditions(TableConditions conditions) {
		if (conditions != null) {
			this.conditions = conditions;
		}
	}
	
	public void setColumns(List<UITableColumnType> columns) {
		this.columns = columns;
	}
	
	public void setQueryExpr(ExpressionType queryExpr) {
		this.queryExpr = queryExpr;
	}

	public void setTotalExpr(ExpressionType totalExpr) {
		this.totalExpr = totalExpr;
	}

	public String getColumnId(int index) {
		int count=0;
		for (UITableColumnType col : columns) {
			if (count++ == index){
				String v = col.getBeFieldId();
				return v.substring(v.lastIndexOf(".") + 1);
			}
		}
		return null;
	}
	
	private void updateCell(UITableColumnType col, String field, String value, Object rowObject) {
		try {
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
			evaContext.setVariableValue("rowBE", rowObject);
			evaContext.setVariableValue("value", value);
			evaContext.setVariableValue("filterId", field);
			ooeeContext.setDefaultEvaluationContext(evaContext);
			ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);
			
			if (col.getUpdateCondition() != null) {
				col.getUpdateCondition().getExpression().evaluate(ooeeContext);
			}
		} catch (Exception e) {
			logger.error("error occurrs while updating table conditions. "  + this.getId(), e);
		}
	}
	
	private void updateFilter(String field, String value) {
		for (UITableColumnType col : columns) {
			if (col.getBeFieldId().equals(field) 
					|| (col.getUiType().getType().equals("DateRange") 
							&& (col.getUiType().getStartCondition().equals(field) 
									|| col.getUiType().getEndCondition().equals(field)))){
				try {
					OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
					DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
					evaContext.setVariableValue("rowBE", 
								conditions.getCondition() != null 
								? conditions.getCondition() : conditions.getAdditionalCondition());
					evaContext.setVariableValue("value", value);
					evaContext.setVariableValue("filterId", field);
					ooeeContext.setDefaultEvaluationContext(evaContext);
					ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);
					
					if (col.getUpdateCondition() != null) {
						col.getUpdateCondition().getExpression().evaluate(ooeeContext);
					}
				} catch (Exception e) {
					logger.error("error occurrs while updating table conditions."  + this.getId(), e);
				}
				break;
			}
		}
	}
	
	public void addRow(Object object) {
		this.listData.add(object);
		if (this.addItems == null) {
			this.addItems = new ArrayList<Object>();
		}
		this.addItems.add(object);
		
		IDataItem dataItem = AjaxActionHelper.createDataItem();
		dataItem.setUiid(this.getId());
		dataItem.setJsHandler(IJSHandlerCollections.TABLE_UPDATE);
		dataItem.setData(this.refresh());
		dataItem.setFrameInfo(this.getFrameInfo());

		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
		ajaxContext.addDataItem(dataItem);
	}
	
	public Object deleteRow(int index) {
		if (this.listData.size() <= index) {
			return null;
		}
		Object obj = this.listData.remove(index);
		if (this.deleteItems == null) {
			this.deleteItems = new ArrayList<Object>();
		}
		this.deleteItems.add(obj);
		if (this.addItems != null) {
			this.addItems.remove(obj);
		}
		if (this.updateItems != null) {
			this.updateItems.remove(obj);
		}
		
		IDataItem dataItem = AjaxActionHelper.createDataItem();
		dataItem.setUiid(this.getId());
		dataItem.setJsHandler(IJSHandlerCollections.TABLE_UPDATE);
		dataItem.setData(this.refresh());
		dataItem.setFrameInfo(this.getFrameInfo());

		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
		ajaxContext.addDataItem(dataItem);
		
		return obj;
	}
	
	/**
	 * After when called addRow,removeRow,removeAll,updateRow, we have to call
	 * this method refreshing data set.
	 */
	public String refresh() {
		try {
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
			evaContext.setVariableValue("tableCondition", conditions);
			evaContext.setVariableValue("page", AjaxActionHelper.getAjaxContext());
			evaContext.setVariableValue("table", this);
			ooeeContext.setDefaultEvaluationContext(evaContext);
			ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);

			Object value = totalExpr.evaluate(ooeeContext);
			
			StringBuffer sb = new  StringBuffer();
	        sb.append("{\"recordsFiltered\":");
	        sb.append(value);
	        sb.append(",\"recordsTotal\":");
	        sb.append(value);
	        sb.append(",\"selectedIndex\":");
	        sb.append(this.getSelectedIndex());
	        sb.append(",\"pageIndex\":");
	        sb.append(this.conditions.getOffset());
	        sb.append(",");
	        sb.append("\"data\":[");
	        List<Object> rows = (List<Object>)queryExpr.evaluate(ooeeContext);
	        this.listData = rows;
	        for (Object be : rows) {
	        	evaContext.setVariableValue("rowBE", be);
	        	
	        	sb.append("[");
	        	for (UITableColumnType col : columns) {
	        		Object cellValue = col.getRowExpression().getExpression().evaluate(
							ooeeContext);
	        		if (cellValue == null) {
						cellValue = "";
					}
	        		sb.append("\"");
	        		sb.append(cellValue);
	        		sb.append("\",");
	        	}
	        	sb.deleteCharAt(sb.length()-1);
	        	sb.append("],");
	        }
	        if (rows.size() > 0) {
	        	sb.deleteCharAt(sb.length()-1);
	        }
	        sb.append("]}");
	        
	        return sb.toString();
		} catch (Exception e) {
			logger.error("error occurrs while refreshing table: " + this.getId(), e);
		}
		return "";
	}
	
	private List<Object[]> getEvaluatedResult() {
		List<Object[]> result = new ArrayList<Object[]>();
		try {
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
			evaContext.setVariableValue("tableCondition", conditions);
			evaContext.setVariableValue("page", AjaxActionHelper.getAjaxContext());
			evaContext.setVariableValue("table", this);
			ooeeContext.setDefaultEvaluationContext(evaContext);
			ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);

	        List<Object> rows = (List<Object>)queryExpr.evaluate(ooeeContext);
	        this.listData = rows;
	        for (Object be : rows) {
	        	evaContext.setVariableValue("rowBE", be);
	        	
				int i = 0;
	        	Object[] rowValues = new Object[columns.size()];
	        	for (UITableColumnType col : columns) {
	        		Object cellValue = col.getRowExpression().getExpression().evaluate(
							ooeeContext);
	        		if (cellValue == null) {
						cellValue = "";
					}
	        		rowValues[i++] = cellValue;
	        	}
	        	result.add(rowValues);
	        }
		} catch (Exception e) {
			logger.error("error occurrs while evaluating table values.", e);
		}
		return result;
	}
	
	public void importAsExcel() {
		
	}
	
	public void exportAsExcel(OutputStream output) throws IOException {
		int i = 0;
		String[] columnTitles = new String[columns.size()];
		for (UITableColumnType col : columns) {
			columnTitles[i++] = UIVariableUtil.getI18NProperty(col.getTitle());
		}
		String name = "Data Report";
		ImportTableToExcel importTable = new ImportTableToExcel(getEvaluatedResult());
		importTable.createWorkbook(name, columnTitles).write(output);
	}

}
