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
package org.shaolin.uimaster.page.od.rules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.MultiChoice;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLMultiChoiceType;

public class UIMultipleChoice implements IODMappingConverter {
	private HTMLMultiChoiceType uiMultipleChoice;
	private String uiid;
	private List<String> value;
	private List<String> optionValues;
	private List<String> optionDisplayValues;

	public static UIMultipleChoice createRule() {
		return new UIMultipleChoice();
	}
	
	public String getRuleName() {
		return this.getClass().getName();
	}

	public HTMLMultiChoiceType getUIMultipleChoice() {
		return this.uiMultipleChoice;
	}

	public void setUIMultipleChoice(HTMLMultiChoiceType UIMultipleChoice) {
		this.uiMultipleChoice = UIMultipleChoice;
	}

	private HTMLMultiChoiceType getUIHTML() {
		return this.uiMultipleChoice;
	}

	public List<String> getValue() {
		return this.value;
	}

	public void setValue(List<String> Value) {
		this.value = Value;
	}

	public List<String> getOptionValues() {
		return this.optionValues;
	}

	public void setOptionValues(List<String> OptionValues) {
		this.optionValues = OptionValues;
	}

	public List<String> getOptionDisplayValues() {
		return this.optionDisplayValues;
	}

	public void setOptionDisplayValues(List<String> OptionDisplayValues) {
		this.optionDisplayValues = OptionDisplayValues;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();

		dataClassInfo.put("Value", List.class);
		dataClassInfo.put("OptionValues", List.class);
		dataClassInfo.put("OptionDisplayValues", List.class);

		return dataClassInfo;
	}

	public Map<String, Class<?>> getUIEntityClassInfo() {
		HashMap<String, Class<?>> uiClassInfo = new HashMap<String, Class<?>>();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLMultiChoiceType.class);

		return uiClassInfo;
	}
	
	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String values, String optionValues) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("Value", values);
		dataClassInfo.put("OptionValues", optionValues);
		dataClassInfo.put("OptionDisplayValues", optionValues);

		return dataClassInfo;
	}

	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiMultipleChoice = ((HTMLMultiChoiceType) paramValue
						.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("Value")) {
				this.value = ((List) paramValue.get("Value"));
			}
			if (paramValue.containsKey("OptionValues")) {
				this.optionValues = ((List) paramValue.get("OptionValues"));
			}
			if (paramValue.containsKey("OptionDisplayValues")) {
				this.optionDisplayValues = ((List) paramValue.get("OptionDisplayValues"));
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
			paramValue.put(UI_WIDGET_TYPE, this.uiMultipleChoice);
			paramValue.put("Value", this.value);
			paramValue.put("OptionValues", this.optionValues);
			paramValue.put("OptionDisplayValues", this.optionDisplayValues);
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
			this.uiMultipleChoice.setValue(this.value);
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
			MultiChoice selectComp = (MultiChoice) AjaxActionHelper
					.getCachedAjaxWidget(this.uiid, htmlContext);
			this.value = selectComp.getValues();
			this.optionValues = selectComp.getOptionValues();
			this.optionDisplayValues = selectComp.getOptionDisplayValues();
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

	private void callChoiceOption(boolean isDataToUI, HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			Map<String, Object> converter_in_data = new HashMap<String, Object>();
			converter_in_data.put(UI_WIDGET_TYPE, this.uiMultipleChoice);
			converter_in_data.put("OptionValues", this.optionValues);
			converter_in_data.put("OptionDisplayValues",this.optionDisplayValues);

			IODMappingConverter converter = new UIChoiceOptionValue();
			converter.setInputData(converter_in_data);
			if (isDataToUI) {
				converter.pushDataToWidget(htmlContext);
			} else {
				converter.pullDataFromWidget(htmlContext);

				List<String> ref_OptionValues = null;
				List<String> ref_OptionDisplayValues = null;
				Map output = converter.getOutputData();
				if (output.containsKey("OptionValues")) {
					ref_OptionValues = (List) output
							.get("OptionValues");
				}
				if (output.containsKey("OptionDisplayValues")) {
					ref_OptionDisplayValues = (List) output
							.get("OptionDisplayValues");
				}
				this.optionValues = ref_OptionValues;
				this.optionDisplayValues = ref_OptionDisplayValues;
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