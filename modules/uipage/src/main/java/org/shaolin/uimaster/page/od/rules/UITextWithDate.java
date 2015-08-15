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

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.Calendar;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.formats.FormatUtil;
import org.shaolin.uimaster.page.widgets.HTMLDateType;
import org.shaolin.uimaster.page.widgets.HTMLTextWidgetType;

public class UITextWithDate implements IODMappingConverter {
	private HTMLDateType uiDate;
	private String uiid;
	private Date date;
	private boolean isDateOnly;
	private String displayStringData;
	private String stringData;
	private String localeConfig;
	private Map<String, Object> propValues;

	public static UITextWithDate createRule() {
		return new UITextWithDate();
	}
	
	public String getRuleName() {
		return this.getClass().getName();
	}

	public HTMLDateType getUIText() {
		return this.uiDate;
	}

	public void setUIText(HTMLDateType UIText) {
		this.uiDate = UIText;
	}

	private HTMLDateType getUIHTML() {
		return this.uiDate;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date Date) {
		this.date = Date;
	}

	public String getDisplayStringData() {
		return this.displayStringData;
	}

	public void setDisplayStringData(String DisplayStringData) {
		this.displayStringData = DisplayStringData;
	}

	public String getStringData() {
		return this.stringData;
	}

	public void setStringData(String StringData) {
		this.stringData = StringData;
	}

	public String getLocaleConfig() {
		return this.localeConfig;
	}

	public void setLocaleConfig(String LocaleConfig) {
		this.localeConfig = LocaleConfig;
	}

	public Map<String, Object> getPropValues() {
		return this.propValues;
	}

	public void setPropValues(Map<String, Object> PropValues) {
		this.propValues = PropValues;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();

		dataClassInfo.put("Date", Date.class);
		dataClassInfo.put("DateFormat", String.class);
		dataClassInfo.put("IsDateOnly", String.class);
		dataClassInfo.put("DisplayStringData", String.class);
		dataClassInfo.put("StringData", String.class);
		dataClassInfo.put("LocaleConfig", String.class);
		dataClassInfo.put("PropValues", Map.class);
		dataClassInfo.put("DateObject", Object.class);

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

		dataClassInfo.put("Date", value);
		
		return dataClassInfo;
	}

	public void setInputData(Map<String, Object> paramValue)
			throws UIConvertException {
		try {
			if (paramValue.containsKey(UI_WIDGET_TYPE)) {
				this.uiDate = ((HTMLDateType) paramValue.get(UI_WIDGET_TYPE));
			}
			if (paramValue.containsKey(UI_WIDGET_ID)) {
				this.uiid = (String) paramValue.get(UI_WIDGET_ID);
			}
			if (paramValue.containsKey("Date")) {
				this.date = ((Date) paramValue.get("Date"));
			}
			if (paramValue.containsKey("IsDateOnly")) {
				Object v = paramValue.get("IsDateOnly");
				if (v instanceof Boolean) {
					this.isDateOnly = (Boolean)v;
				} else {
					this.isDateOnly = Boolean.valueOf(v.toString());
				}
			}
			if (paramValue.containsKey("PropValues")) {
				this.propValues = ((Map) paramValue.get("PropValues"));
			}
			if (paramValue.containsKey("StringData")) {
				this.stringData = ((String) paramValue.get("StringData"));
			}
			if (paramValue.containsKey("DisplayStringData")) {
				this.displayStringData = ((String) paramValue
						.get("DisplayStringData"));
			}
			if (paramValue.containsKey("LocaleConfig")) {
				this.localeConfig = ((String) paramValue
						.get("LocaleConfig"));
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
			paramValue.put(UI_WIDGET_TYPE, this.uiDate);
			paramValue.put("Date", this.date);
			paramValue.put("IsDateOnly", this.isDateOnly);
			paramValue.put("PropValues", this.propValues);
			paramValue.put("StringData", this.stringData);
			paramValue.put("DisplayStringData", this.displayStringData);
			paramValue.put("LocaleConfig", this.localeConfig);
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
			if (this.date == null) {
				return;
			}
			String dataType = FormatUtil.DATE_TIME;
			if (this.isDateOnly) {
				dataType = FormatUtil.DATE;
			}	
			this.stringData = FormatUtil.convertDataToUI(dataType,
					this.date, this.localeConfig, this.propValues);
			this.uiDate.setValue(this.stringData);
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
			Calendar calendar = (Calendar) AjaxActionHelper
					.getCachedAjaxWidget(this.uiid, htmlContext);
			String value = calendar.getValue();
			this.stringData = value != null ? value.trim() : "";
			if ("".equals(stringData)) {
				this.date = null;
				return;
			}
			try {
				String dataType = FormatUtil.DATE_TIME;
				if (this.isDateOnly) {
					dataType = FormatUtil.DATE;
				}
				Object dateObject = FormatUtil.convertUIToData(
						dataType, value, this.localeConfig, this.propValues);

				this.date = ((Date)dateObject);
			} catch (Exception e) {
				this.date = new Date();
			}
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