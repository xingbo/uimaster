package org.shaolin.uimaster.page.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.spi.IConstantService;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.CheckBoxGroup;
import org.shaolin.uimaster.page.ajax.ComboBox;
import org.shaolin.uimaster.page.ajax.RadioButtonGroup;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;

/**
 * The dynamic ui item is going to be generated as the described HTML widget.
 * 
 * @author wushaol
 *
 */
public class HTMLDynamicUIItem {

	/**
	 * help is not available
	 */
	protected java.lang.String uiEntityName;

	/**
	 * help is not available
	 */
	protected java.lang.String uipanel;

	/**
	 * help is not available
	 */
	protected java.lang.String filter;

	/**
	 * help is not available
	 */
	protected java.lang.String labelName;

	/**
	 * help is not available
	 */
	protected java.lang.String ceName;

	protected ExpressionType uiToData;
	
	protected ExpressionType dataToUi;
	
	
	/**
	 * help is not available
	 */
	protected int ceSelectMode = 0;

	public static final int LIST = 0;
	public static final int RADIOBUTTONGROUP = 1;
	public static final int CHECKBOXGROUP = 2;

	public java.lang.String getLabelName() {
		return labelName;
	}

	public void setLabelName(java.lang.String labelName) {
		this.labelName = labelName;
	}

	public java.lang.String getCeName() {
		return ceName;
	}

	public void setCeName(java.lang.String ceName) {
		this.ceName = ceName;
	}

	public int getCeSelectMode() {
		return ceSelectMode;
	}

	public void setCeSelectMode(int ceSelectMode) {
		this.ceSelectMode = ceSelectMode;
	}

	public java.lang.String getUiEntityName() {
		return uiEntityName;
	}

	public void setUiEntityName(java.lang.String uiEntityName) {
		this.uiEntityName = uiEntityName;
	}

	public java.lang.String getUipanel() {
		return uipanel;
	}

	public void setUipanel(java.lang.String uipanel) {
		this.uipanel = uipanel;
	}

	public java.lang.String getFilter() {
		return filter;
	}

	public void setFilter(java.lang.String filter) {
		this.filter = filter;
	}
	
	public ExpressionType getUiToData() {
		return uiToData;
	}

	public void setUiToData(ExpressionType uiToData) {
		this.uiToData = uiToData;
	}

	public ExpressionType getDataToUi() {
		return dataToUi;
	}

	public void setDataToUi(ExpressionType dataToUi) {
		this.dataToUi = dataToUi;
	}
	
	public void generate(String jsonValue, String uiid, HTMLLayoutType layout, 
			VariableEvaluator ee, HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) throws Exception {
		List<IConstantEntity> value = HTMLDynamicUIItem.toCElist(this.getCeName(), jsonValue);
		IConstantEntity constant = AppContext.get().getConstantService().getConstantEntity(this.getCeName());
		Map<Integer, String> avps = constant.getAllConstants(false);
		
		if (this.getCeSelectMode() == HTMLDynamicUIItem.LIST) {
			HTMLComboBoxType list = new HTMLComboBoxType(context, uiid); 
			list.setPrefix(context.getHTMLPrefix());
			list.setHTMLLayout(layout);
			list.setFrameInfo(context.getFrameInfo());
			list.addAttribute("UIStyle", "uimaster_rightform_widget");
			
			list.setValue(value == null || value.size() == 0? "":value.get(0).getIntValue()+"");
			list.setOptionValues(new ArrayList(avps.keySet()));
			list.setOptionDisplayValues(new ArrayList(avps.values()));
			
			Widget newWidget = list.createAjaxWidget(ee);
			if (newWidget != null) {
	        	context.addAjaxWidget(newWidget.getId(), newWidget);
	        }
			
			list.generateBeginHTML(context, ownerEntity, depth);
			list.generateEndHTML(context, ownerEntity, depth);
			
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.RADIOBUTTONGROUP) {
			HTMLRadioButtonGroupType list = new HTMLRadioButtonGroupType(context, uiid); 
			list.setPrefix(context.getHTMLPrefix());
			list.setHTMLLayout(layout);
			list.setFrameInfo(context.getFrameInfo());
			list.addAttribute("horizontalLayout", "true");
			
			list.setValue(value == null || value.size() == 0? "":value.get(0).getIntValue()+"");
			list.setOptionValues(new ArrayList(avps.keySet()));
			list.setOptionDisplayValues(new ArrayList(avps.values()));
			
			Widget newWidget = list.createAjaxWidget(ee);
			if (newWidget != null) {
	        	context.addAjaxWidget(newWidget.getId(), newWidget);
	        }
			
			list.generateBeginHTML(context, ownerEntity, depth);
			list.generateEndHTML(context, ownerEntity, depth);
			
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.CHECKBOXGROUP) {
			HTMLCheckBoxGroupType list = new HTMLCheckBoxGroupType(context, uiid); 
			list.setPrefix(context.getHTMLPrefix());
			list.setHTMLLayout(layout);
			list.setFrameInfo(context.getFrameInfo());
			list.addAttribute("horizontalLayout", "true");
			
			ArrayList<String> temp = new ArrayList<String>(value.size());
			for (IConstantEntity v : value) {
				temp.add(v.getIntValue() + "");
			}
			list.setValue(temp);
			list.setOptionValues(new ArrayList(avps.keySet()));
			list.setOptionDisplayValues(new ArrayList(avps.values()));
			
			Widget newWidget = list.createAjaxWidget(ee);
			if (newWidget != null) {
	        	context.addAjaxWidget(newWidget.getId(), newWidget);
	        }
			
			list.generateBeginHTML(context, ownerEntity, depth);
			list.generateEndHTML(context, ownerEntity, depth);
		} 
	}
	
	public Object retriveData(String uiid) {
		if (this.getCeSelectMode() == HTMLDynamicUIItem.LIST) {
			ComboBox box = AjaxActionHelper.getAjaxContext().getComboBox(uiid);
			return "{\"name\":\""+this.getCeName()+"\",\"value\":\""+box.getValue()+"\"}"; 
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.RADIOBUTTONGROUP) {
			RadioButtonGroup group = AjaxActionHelper.getAjaxContext().getRadioBtnGroup(uiid);
			return "{\"name\":\""+this.getCeName()+"\",\"value\":\""+group.getValue()+"\"}"; 
		} else if (this.getCeSelectMode() == HTMLDynamicUIItem.CHECKBOXGROUP) {
			CheckBoxGroup group = AjaxActionHelper.getAjaxContext().getCheckBoxGroup(uiid);
			List<String> values = group.getValues();
			StringBuffer sb = new StringBuffer();
			if (values != null && values.size() > 0) {
				for (String v : values) {
					sb.append(v).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			return "{\"name\":\""+this.getCeName()+"\",\"value\":\""+sb.toString()+"\"}"; 
		}
		throw new IllegalStateException("Unsupported dynamic UI widget.");
	}
	
	public static String toJsonArray(List<String> items) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (String i : items) {
			sb.append(i).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
	
	public static List<IConstantEntity> toCElist(String ceName, String value) throws JSONException {
		if (value == null || value.trim().isEmpty()) {
			return Collections.emptyList();
		}
		
		ArrayList<IConstantEntity> ceValues = new ArrayList<IConstantEntity>();
		JSONArray array = new JSONArray(value);
		int length = array.length();
		for (int i=0; i<length; i++) {
			JSONObject item = array.getJSONObject(i);
			if (item.get("name").equals(ceName)) {
				IConstantService cs = AppContext.get().getConstantService();
				IConstantEntity constantEntity = cs.getConstantEntity(ceName);
				String values = (String)item.get("value");
				if (values.indexOf(',') != -1) {
					String[] vs= values.split(",");
					for (String v: vs) {
						ceValues.add(constantEntity.getByIntValue(Integer.valueOf(v)));
					}
				} else {
					ceValues.add(constantEntity.getByIntValue(Integer.valueOf(values)));
				}
				break;	
			}
		}
		return ceValues;
	}
}
