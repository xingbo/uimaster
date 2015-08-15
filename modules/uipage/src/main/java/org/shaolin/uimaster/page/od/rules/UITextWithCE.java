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

import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.TextWidget;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.widgets.HTMLLabelType;
import org.shaolin.uimaster.page.widgets.HTMLTextWidgetType;

public class UITextWithCE implements IODMappingConverter {
	private HTMLTextWidgetType uiText;
	private String uiid;
	private String ceType;
	private IConstantEntity ceValue;

	public static UITextWithCE createRule() {
		return new UITextWithCE();
	}
	
	public String getRuleName() {
		return this.getClass().getName();
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

	public String getCEType() {
		return this.ceType;
	}

	public void setCEType(String CEType) {
		this.ceType = CEType;
	}

	public IConstantEntity getCEValue() {
		return this.ceValue;
	}

	public void setCEValue(IConstantEntity CEValue) {
		this.ceValue = CEValue;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();

		dataClassInfo.put("CEType", String.class);
		dataClassInfo.put("CEValue", IConstantEntity.class);

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
				this.uiText = ((HTMLTextWidgetType) paramValue.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("CEType")) {
				this.ceType = ((String) paramValue.get("CEType"));
			}
			if (paramValue.containsKey("CEValue")) {
				this.ceValue = ((IConstantEntity) paramValue.get("CEValue"));
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
			paramValue.put("CEType", this.ceType);
			paramValue.put("CEValue", this.ceValue);
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
				this.uiText.setValue(this.ceValue.getValue());
				if (this.uiText instanceof HTMLLabelType) {
					((HTMLLabelType) this.uiText).setDisplayValue(this.ceValue
							.getDisplayName());
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
			this.ceValue = CEUtil.getConstantEntity(value, this.ceType);
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