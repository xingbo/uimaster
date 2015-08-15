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
import java.util.Map;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.SelectWidget;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLSelectComponentType;

public class UISelect implements IODMappingConverter {
	private HTMLSelectComponentType uiSelect;
	private String uiid;
	private boolean value;

	public static UISelect createRule() {
		return new UISelect();
	}
	
	public String getRuleName() {
		return this.getClass().getName();
	}

	public HTMLSelectComponentType getUISelect() {
		return this.uiSelect;
	}

	public void setUISelect(HTMLSelectComponentType UISelect) {
		this.uiSelect = UISelect;
	}

	private HTMLSelectComponentType getUIHTML() {
		return this.uiSelect;
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean Value) {
		this.value = Value;
	}

	public Map getDataEntityClassInfo() {
		HashMap dataClassInfo = new LinkedHashMap();

		dataClassInfo.put("Value", Boolean.TYPE);

		return dataClassInfo;
	}

	public Map getUIEntityClassInfo() {
		HashMap uiClassInfo = new HashMap();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLSelectComponentType.class);

		return uiClassInfo;
	}
	
	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String value) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("Value", value);

		return dataClassInfo;
	}

	public void setInputData(Map paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiSelect = ((HTMLSelectComponentType) paramValue
						.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("Value")) {
				this.value = ((Boolean) paramValue.get("Value")).booleanValue();
			}
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}

			throw new UIConvertException("EBOS_ODMAPPER_070", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public Map getOutputData() throws UIConvertException {
		Map paramValue = new HashMap();
		try {
			paramValue.put(UI_WIDGET_TYPE, this.uiSelect);
			paramValue.put("Value", Boolean.valueOf(this.value));
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
			this.uiSelect.setValue(this.value);
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
			SelectWidget selectComp = (SelectWidget) AjaxActionHelper
					.getCachedAjaxWidget(this.uiid, htmlContext);
			this.value = selectComp.isSelected();
		} catch (Throwable t) {
			if (t instanceof UIConvertException) {
				throw ((UIConvertException) t);
			}
			throw new UIConvertException("EBOS_ODMAPPER_073", t,
					new Object[] { getUIHTML().getUIID() });
		}
	}

	public void callAllMappings(boolean isDataToUI) throws UIConvertException {
	}
}