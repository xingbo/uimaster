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
import org.shaolin.uimaster.page.ajax.TextWidget;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLLabelType;
import org.shaolin.uimaster.page.widgets.HTMLTextWidgetType;

public class UIText implements IODMappingConverter {
	private HTMLTextWidgetType uiText;
	private String uiid;
	private String stringData;
	private String displayStringData;

	public static UIText createRule() {
		return new UIText();
	}
	
	public String getRuleName() {
		return UIText.class.getName();
	}

	public HTMLTextWidgetType getUIText() {
		return this.uiText;
	}

	public void setUIText(HTMLTextWidgetType UIText) {
		this.uiText = UIText;
	}

	private HTMLTextWidgetType getUIHTML() {
		return this.uiText;
	}

	public String getStringData() {
		return this.stringData;
	}

	public void setStringData(String StringData) {
		this.stringData = StringData;
	}

	public String getDisplayStringData() {
		return this.displayStringData;
	}

	public void setDisplayStringData(String DisplayStringData) {
		this.displayStringData = DisplayStringData;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();

		dataClassInfo.put("StringData", String.class);
		dataClassInfo.put("DisplayStringData", String.class);

		return dataClassInfo;
	}

	public Map<String, Class<?>> getUIEntityClassInfo() {
		HashMap<String, Class<?>> uiClassInfo = new HashMap<String, Class<?>>();

		uiClassInfo.put(UI_WIDGET_TYPE, HTMLTextWidgetType.class);

		return uiClassInfo;
	}
	
	public static Map<String, String> getRequiredUIParameter(String param) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put(UI_WIDGET_TYPE, param);

		return dataClassInfo;
	}
	
	public static Map<String, String> getRequiredDataParameters(String value) {
		HashMap<String, String> dataClassInfo = new LinkedHashMap<String, String>();

		dataClassInfo.put("StringData", value);
		
		return dataClassInfo;
	}

	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiText = ((HTMLTextWidgetType) paramValue.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("DisplayStringData")) {
				Object v = paramValue.get("DisplayStringData");
				this.displayStringData = v != null ? v.toString() : "";
			}
			if (paramValue.containsKey("StringData")) {
				Object v = paramValue.get("StringData");
				this.stringData = v != null ? v.toString() : "";
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
			paramValue.put(UI_WIDGET_TYPE, this.uiText);
			paramValue.put("StringData", this.stringData);
			paramValue.put("DisplayStringData", this.displayStringData);
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
			this.uiText.setValue(this.stringData);

			if (this.uiText instanceof HTMLLabelType) {
				if (this.displayStringData != null) {
					((HTMLLabelType) this.uiText)
							.setDisplayValue(this.displayStringData);
				} else {
					((HTMLLabelType) this.uiText)
							.setDisplayValue(this.stringData);
				}
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
		try {
			TextWidget textComp = (TextWidget) AjaxActionHelper
					.getCachedAjaxWidget(this.uiid, htmlContext);
			String value = textComp.getValue();
			this.stringData = value != null ? value.trim() : "";
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