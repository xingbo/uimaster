package org.shaolin.uimaster.page.od.rules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.TextWidget;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.formats.FormatUtil;
import org.shaolin.uimaster.page.widgets.HTMLLabelType;
import org.shaolin.uimaster.page.widgets.HTMLTextWidgetType;

public class UITextWithCurrency implements IODMappingConverter {
	private HTMLTextWidgetType uiText;
	private String uiid;
	private double currency;
	private String localeConfig;
	private Map<String, Object> propValues;
	private boolean displayZero;
	private boolean displaySymbol;

	public static UITextWithCurrency createRule() {
		return new UITextWithCurrency();
	}
	
	public UITextWithCurrency() {
		this.displaySymbol = true;
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

	public double getCurrency() {
		return this.currency;
	}

	public void setCurrency(double Currency) {
		this.currency = Currency;
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

	public boolean getDisplayZero() {
		return this.displayZero;
	}

	public void setDisplayZero(boolean displayZero) {
		this.displayZero = displayZero;
	}

	public boolean getDisplaySymbol() {
		return this.displaySymbol;
	}

	public void setDisplaySymbol(boolean displaySymbol) {
		this.displaySymbol = displaySymbol;
	}

	public Map<String, Class<?>> getDataEntityClassInfo() {
		HashMap<String, Class<?>> dataClassInfo = new LinkedHashMap<String, Class<?>>();

		dataClassInfo.put("Currency", Double.TYPE);
		dataClassInfo.put("DisplayStringData", String.class);
		dataClassInfo.put("CurrencyFormat", String.class);
		dataClassInfo.put("LocaleConfig", String.class);
		dataClassInfo.put("PropValues", Map.class);
		dataClassInfo.put("displayZero", Boolean.TYPE);
		dataClassInfo.put("StringData", String.class);
		dataClassInfo.put("NumberObject", Object.class);
		dataClassInfo.put("StyleMap", Map.class);
		dataClassInfo.put("displaySymbol", Boolean.TYPE);

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

		dataClassInfo.put("Currency", value);
		
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
			if (paramValue.containsKey("Currency")) {
				this.currency = ((Number) paramValue.get("Currency"))
						.doubleValue();
			}
			// options
			if (paramValue.containsKey("PropValues")) {
				this.propValues = ((Map) paramValue.get("PropValues"));
			}
			if (paramValue.containsKey("LocaleConfig")) {
				this.localeConfig = ((String) paramValue
						.get("LocaleConfig"));
			}
			if (paramValue.containsKey("displayZero")) {
				this.displayZero = ((Boolean) paramValue.get("displayZero"))
						.booleanValue();
			}
			if (paramValue.containsKey("displaySymbol")) {
				this.displaySymbol = ((Boolean) paramValue
						.get("displaySymbol")).booleanValue();
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
			paramValue.put("Currency", Double.valueOf(this.currency));
			paramValue.put("PropValues", this.propValues);
			paramValue.put("LocaleConfig", this.localeConfig);
			paramValue.put("displayZero", Boolean.valueOf(this.displayZero));
			paramValue.put("displaySymbol",Boolean.valueOf(this.displaySymbol));
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
			if (this.displaySymbol) {
				Map<String, Object> styleMap = FormatUtil.getCurrencyStyle(this.localeConfig);
				this.uiText.setLocale(this.localeConfig);
				this.uiText.setCurrencySymbol((String) styleMap.get("currencySymbol"));
				this.uiText.setCurrencyFormat((String) styleMap.get("currencyFormat"));
				this.uiText.setIsSymbolLeft((Boolean) styleMap.get("isLeft"));
			}

			if ((this.displayZero) || (this.currency != 0.0D)) {
				String value = FormatUtil.convertDataToUI(FormatUtil.CURRENCY, new Double(
								this.currency), this.localeConfig, this.propValues);
				this.uiText.setValue(value);
				if (this.uiText instanceof HTMLLabelType) {
					((HTMLLabelType) this.uiText).setDisplayValue(value);
				}
			}
			this.uiText.setIsCurrency(true);
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
			if (value == null || "".equals(value.trim())) {
				this.currency = 0.0D;
			} else {
				Object numberObject = FormatUtil.convertUIToData(FormatUtil.CURRENCY, 
							value, this.localeConfig, this.propValues);
				this.currency = ((Number) numberObject).doubleValue();
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