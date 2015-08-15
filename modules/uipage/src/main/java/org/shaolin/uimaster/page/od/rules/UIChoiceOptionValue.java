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

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLChoiceType;

public class UIChoiceOptionValue implements IODMappingConverter {
	private HTMLChoiceType uiChoice;
	private List<String> optionValues;
	private List<String> optionDisplayValues;

	public static UIChoiceOptionValue createRule() {
		return new UIChoiceOptionValue();
	}
	
	public String getRuleName() {
		return this.getClass().getName();
	}

	public HTMLChoiceType getUIChoice() {
		return this.uiChoice;
	}

	public void setUIChoice(HTMLChoiceType UIChoice) {
		this.uiChoice = UIChoice;
	}

	private HTMLChoiceType getUIHTML() {
		return this.uiChoice;
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

		dataClassInfo.put("OptionValues", List.class);
		dataClassInfo.put("OptionDisplayValues", List.class);

		return dataClassInfo;
	}

	public Map<String, Class<?>> getUIEntityClassInfo() {
		HashMap<String, Class<?>> uiClassInfo = new HashMap<String, Class<?>>();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLChoiceType.class);

		return uiClassInfo;
	}
	
	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("OptionValues", param);
		dataClassInfo.put("OptionDisplayValues", param);

		return dataClassInfo;
	}

	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiChoice = ((HTMLChoiceType) paramValue.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey("OptionValues")) {
				this.optionValues = ((List) paramValue.get("OptionValues"));
			}
			if (paramValue.containsKey("OptionDisplayValues")) {
				this.optionDisplayValues = ((List) paramValue
						.get("OptionDisplayValues"));
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
			paramValue.put(UI_WIDGET_TYPE, this.uiChoice);
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
			if (this.optionValues != null) {
				this.uiChoice.setOptionValues(this.optionValues);
			}
			if (this.optionDisplayValues != null) {
				this.uiChoice.setOptionDisplayValues(this.optionDisplayValues);
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}
			throw new UIConvertException("EBOS_ODMAPPER_072", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void pullDataFromWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
	}

}