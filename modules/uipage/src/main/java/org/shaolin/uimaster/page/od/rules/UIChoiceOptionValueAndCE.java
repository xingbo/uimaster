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

import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLChoiceType;

public class UIChoiceOptionValueAndCE implements IODMappingConverter {
	private HTMLChoiceType uiChoice;
	private String ceType;
	private boolean containsNotSpecified;
	private List excludeValue;
	private String notSpecifiedDisplayValue;

	public UIChoiceOptionValueAndCE() {
		this.containsNotSpecified = true;
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

		dataClassInfo.put("CEType", String.class);
		dataClassInfo.put("ContainsNotSpecified", Boolean.TYPE);
		dataClassInfo.put("IntValuePrefix", String.class);
		dataClassInfo.put("ExcludeValue", List.class);
		dataClassInfo.put("NotSpecifiedDisplayValue", String.class);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("CEType", param);

		return dataClassInfo;
	}

	public Map<String, Class<?>> getUIEntityClassInfo() {
		HashMap<String, Class<?>> uiClassInfo = new HashMap<String, Class<?>>();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLChoiceType.class);

		return uiClassInfo;
	}

	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiChoice = ((HTMLChoiceType) paramValue.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey("ExcludeValue")) {
				this.excludeValue = ((List) paramValue.get("ExcludeValue"));
			}
			if (paramValue.containsKey("CEType")) {
				this.ceType = ((String) paramValue.get("CEType"));
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
			paramValue.put(UI_WIDGET_TYPE, this.uiChoice);
			paramValue.put("ExcludeValue", this.excludeValue);
			paramValue.put("CEType", this.ceType);
			paramValue.put("ContainsNotSpecified", Boolean.valueOf(this.containsNotSpecified));
			paramValue.put("NotSpecifiedDisplayValue", this.notSpecifiedDisplayValue);
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}
			throw new UIConvertException("EBOS_ODMAPPER_071", t,
					new Object[] { getUIHTML().getUIID() });
		}
		return paramValue;
	}

	public void pushDataToWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
		try {
			this.uiChoice.setOptionValues(CEUtil.listCEValues(this.ceType,
					this.containsNotSpecified, this.excludeValue));

			this.uiChoice.setOptionDisplayValues(CEUtil.listCEDisplayValues(
					this.ceType, this.containsNotSpecified,
					this.excludeValue, this.notSpecifiedDisplayValue));
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_072", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void pullDataFromWidget(HTMLSnapshotContext htmlContext) throws UIConvertException {
		// empty
	}

	
	public static UIChoiceOptionValueAndCE createRule() {
		return new UIChoiceOptionValueAndCE();
	}
}