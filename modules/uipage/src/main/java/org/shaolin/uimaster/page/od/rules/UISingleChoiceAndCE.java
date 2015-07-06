package org.shaolin.uimaster.page.od.rules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.SingleChoice;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLChoiceType;
import org.shaolin.uimaster.page.widgets.HTMLSingleChoiceType;

public class UISingleChoiceAndCE implements IODMappingConverter {
	private HTMLSingleChoiceType uisingleChoice;
	private String uiid;
	private IConstantEntity ceValue;
	private String ceType;
	private boolean containsNotSpecified = true;
	private List excludeValue;
	private String notSpecifiedDisplayValue;

	public static UISingleChoiceAndCE createRule() {
		return new UISingleChoiceAndCE();
	}
	
	public UISingleChoiceAndCE() {
	}

	public String getRuleName() {
		return this.getClass().getName();
	}

	public HTMLSingleChoiceType getUISingleChoice() {
		return this.uisingleChoice;
	}

	public void setUISingleChoice(HTMLSingleChoiceType UISingleChoice) {
		this.uisingleChoice = UISingleChoice;
	}

	private HTMLSingleChoiceType getUIHTML() {
		return this.uisingleChoice;
	}

	public IConstantEntity getCEValue() {
		return this.ceValue;
	}

	public void setCEValue(IConstantEntity CEValue) {
		this.ceValue = CEValue;
	}

	public String getCEType() {
		return this.ceType;
	}

	public void setCEType(String CEType) {
		this.ceType = CEType;
	}

	public boolean getContainsNotSpecified() {
		return this.containsNotSpecified;
	}

	public void setContainsNotSpecified(boolean ContainsNotSpecified) {
		this.containsNotSpecified = ContainsNotSpecified;
	}

	public List getExcludeValue() {
		return this.excludeValue;
	}

	public void setExcludeValue(List excludeValue) {
		this.excludeValue = excludeValue;
	}

	public String getNotSpecifiedDisplayValue() {
		return this.notSpecifiedDisplayValue;
	}

	public void setNotSpecifiedDisplayValue(String NotSpecifiedDisplayValue) {
		this.notSpecifiedDisplayValue = NotSpecifiedDisplayValue;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();

		dataClassInfo.put("CEValue", IConstantEntity.class);
		dataClassInfo.put("CEType", String.class);
		dataClassInfo.put("ContainsNotSpecified", Boolean.TYPE);
		dataClassInfo.put("ContainsUnknown", Boolean.TYPE);
		dataClassInfo.put("ExcludeValue", List.class);
		dataClassInfo.put("NotSpecifiedDisplayValue", String.class);

		return dataClassInfo;
	}

	public Map getUIEntityClassInfo() {
		HashMap uiClassInfo = new HashMap();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLSingleChoiceType.class);

		return uiClassInfo;
	}
	
	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String value, String ceType) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("CEValue", value);
		dataClassInfo.put("CEType", ceType);
		
		return dataClassInfo;
	}

	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uisingleChoice = ((HTMLSingleChoiceType) paramValue
						.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("CEValue")) {
				this.ceValue = ((IConstantEntity) paramValue.get("CEValue"));
			}
			if (paramValue.containsKey("CEType")) {
				this.ceType = ((String) paramValue.get("CEType"));
			}
			if (paramValue.containsKey("ExcludeValue")) {
				this.excludeValue = ((List) paramValue.get("ExcludeValue"));
			}
			if (paramValue.containsKey("ContainsNotSpecified")) {
				this.containsNotSpecified = ((Boolean) paramValue
						.get("ContainsNotSpecified")).booleanValue();
			}
			if (paramValue.containsKey("NotSpecifiedDisplayValue")) {
				this.notSpecifiedDisplayValue = ((String) paramValue
						.get("NotSpecifiedDisplayValue"));
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_070", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public Map<String, Object> getOutputData() throws UIConvertException {
		Map<String, Object> paramValue = new HashMap<String, Object>();
		try {
			paramValue.put(UI_WIDGET_TYPE, this.uisingleChoice);
			paramValue.put("ExcludeValue", this.excludeValue);
			paramValue.put("CEType", this.ceType);
			paramValue.put("CEValue", this.ceValue);
			paramValue.put("ContainsNotSpecified",Boolean.valueOf(this.containsNotSpecified));
			paramValue.put("NotSpecifiedDisplayValue",this.notSpecifiedDisplayValue);
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_071", t,
					new Object[] { getUIHTML().getUIID() });
		}

		return paramValue;
	}

	public String[] getImplementInterfaceName() {
		return new String[0];
	}

	public void pushDataToWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			if (this.ceValue != null) {
				this.uisingleChoice.setValue(this.ceValue.getValue());
			}

			callChoiceOption(true, htmlContext);
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_072", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void pullDataFromWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			SingleChoice singleChoice = (SingleChoice) AjaxActionHelper
					.getCachedAjaxWidget(this.uiid, htmlContext);
			this.ceValue = CEUtil.getConstantEntity(singleChoice.getValue(), this.ceType);
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}
			throw new UIConvertException("EBOS_ODMAPPER_073", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void callAllMappings(boolean isDataToUI, HTMLSnapshotContext htmlContext) throws UIConvertException {
		callChoiceOption(isDataToUI, htmlContext);
	}

	public void callChoiceOption(boolean isDataToUI, HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			Map<String, Object> converter_in_data = new HashMap<String, Object>();
			converter_in_data.put(UI_WIDGET_TYPE, this.uisingleChoice);
			converter_in_data.put("CEType", this.ceType);
			converter_in_data.put("ContainsNotSpecified",Boolean.valueOf(this.containsNotSpecified));
			converter_in_data.put("ExcludeValue", this.excludeValue);
			converter_in_data.put("NotSpecifiedDisplayValue",this.notSpecifiedDisplayValue);
			
			IODMappingConverter converter = new UIChoiceOptionValueAndCE();
			converter.setInputData(converter_in_data);
			if (isDataToUI) {
				converter.pushDataToWidget(htmlContext);
			} else {
				converter.pullDataFromWidget(htmlContext);

				Map converter_out_data = converter.getOutputData();

				String ref_CEType = null;
				List ref_excludeValue = null;
				String[] ref_aParam = null;
				HTMLChoiceType ref_UIChoice = null;
				boolean ref_ContainsNotSpecified = false;
				String ref_NotSpecifiedDisplayValue = null;

				if (converter_out_data.containsKey("CEType")) {
					ref_CEType = (String) converter_out_data.get("CEType");
				}
				if (converter_out_data.containsKey("ExcludeValue")) {
					ref_excludeValue = (List) converter_out_data
							.get("ExcludeValue");
				}
				if (converter_out_data.containsKey("aParam")) {
					ref_aParam = (String[]) (String[]) converter_out_data
							.get("aParam");
				}
				if (converter_out_data.containsKey("UIChoice")) {
					ref_UIChoice = (HTMLChoiceType) converter_out_data
							.get("UIChoice");
				}
				if (converter_out_data.containsKey("ContainsNotSpecified")) {
					ref_ContainsNotSpecified = ((Boolean) converter_out_data
							.get("ContainsNotSpecified")).booleanValue();
				}
				if (converter_out_data.containsKey("NotSpecifiedDisplayValue")) {
					ref_NotSpecifiedDisplayValue = (String) converter_out_data
							.get("NotSpecifiedDisplayValue");
				}

				this.ceType = ref_CEType;
				this.containsNotSpecified = ref_ContainsNotSpecified;
				this.excludeValue = ref_excludeValue;
				this.notSpecifiedDisplayValue = ref_NotSpecifiedDisplayValue;
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_074", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}
}