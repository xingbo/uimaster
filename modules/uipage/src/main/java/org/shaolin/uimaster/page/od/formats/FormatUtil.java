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
package org.shaolin.uimaster.page.od.formats;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.uimaster.page.exception.FormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatUtil {
	
	private static final Logger logger = LoggerFactory
			.getLogger(FormatUtil.class);

	public static final String DATE_TIME = "dateTime";

	public static final String DATE = "date";

	public static final String NUMBER = "number";

	public static final String FLOAT_NUMBER = "floatNumber";

	public static final String CURRENCY = "currency";

	private static Map<String, IFormatProcessor> dataTypeMap = 
			new HashMap<String, IFormatProcessor>();

	private static final Registry registry = Registry.getInstance();
	
	static {
		IFormatProcessor dateProcessor = new DateCommonProcessor();
		IFormatProcessor datetimeProcessor = new DateTimeCommonProcessor();
		IFormatProcessor numberProcessor = new NumberCommonProcessor();
		IFormatProcessor floatProcessor = new FloatCommonProcessor();
		IFormatProcessor currencyProcessor = new CurrencyCommonProcessor();
		dataTypeMap.put(DATE_TIME, datetimeProcessor);
		dataTypeMap.put(DATE, dateProcessor);
		dataTypeMap.put(NUMBER, numberProcessor);
		dataTypeMap.put(FLOAT_NUMBER, floatProcessor);
		dataTypeMap.put(CURRENCY, currencyProcessor);
	}

	public static String convertDataToUI(String dataType, Object data,
			String localeConfig, Map propValues)
			throws FormatException {
		if (!dataTypeMap.containsKey(dataType)) {
			throw new FormatException(ExceptionConstants.EBOS_ODMAPPER_008,
					new Object[] { dataType });
		}

		if (localeConfig == null || "".equals(localeConfig)) {
			if (logger.isTraceEnabled()) {
				logger.trace("<convertDataToUI> use user locale: {}",
						LocaleContext.getUserLocale());
			}
			localeConfig = LocaleContext.getUserLocale();
		}

		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = ResourceUtil.getDefaultConfig();
		}

		Map<String, String> localeData = registry.getNodeItems(
				"/System/i18n/" + localeConfig);

		IFormatProcessor processor = dataTypeMap.get(dataType);
		if (logger.isTraceEnabled()) {
			logger.trace("processor: " + processor.getClass());
		}

		String localeName = (String) localeData.get("locale");
		Locale locale = ResourceUtil.getLocaleObjectFromString(localeName);
		if (logger.isTraceEnabled()) {
			logger.trace(
					"<convertDataToUI> dataType: {} data: {} localeConfig: {} localeName: {}",
					new Object[] { dataType, data, localeConfig, localeName });
		}
		
		return processor.convertDataToUI(data, locale, localeData, propValues);
	}

	public static Object convertUIToData(String dataType, String text,
			String localeConfig, Map propValues)
			throws FormatException {
		if (!dataTypeMap.containsKey(dataType)) {
			throw new FormatException(ExceptionConstants.EBOS_ODMAPPER_008,
					new Object[] { dataType });
		}

		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = LocaleContext.getUserLocale();
		}

		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = ResourceUtil.getDefaultConfig();
		}

		Map<String, String> localeData = registry.getNodeItems(
				"/System/i18n/" + localeConfig);

		IFormatProcessor processor = dataTypeMap.get(dataType);
		if (logger.isTraceEnabled()) {
			logger.trace("processor: " + processor.getClass());
		}

		String localeName = (String) localeData.get("locale");
		Locale locale = ResourceUtil.getLocaleObjectFromString(localeName);
		if (logger.isTraceEnabled()) {
			logger.trace(
					"<convertUIToData> dataType: {} text: {} localeConfig: {} localeName: {}",
					new Object[] { dataType, text, localeConfig, localeName });
		}
		return processor.convertUIToData(text, locale, localeData, propValues);
	}

	public static Object convertUIToData(String dataType, String text) throws FormatException {
		return convertUIToData(dataType, text, null, null);
	}
	
	public static Map<String, Object> getCurrencyStyle(String localeConfig) {
		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = LocaleContext.getDataLocale();
		}
		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = LocaleContext.getUserLocale();
		}
		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = ResourceUtil.getDefaultConfig();
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("<getCurrencySymbol> localeConfig: {}",
					new Object[] { localeConfig });
		}
		// optimize the reading.
		if (registry.existInFastCache(localeConfig)) {
			return (Map<String, Object>)registry.readFromFastCache(localeConfig);
		}
		
		Map<String, Object> currencyStyle = new HashMap<String, Object>();

		Map<String, String> localeData = registry.getNodeItems(
				"/System/i18n/" + localeConfig);
		currencyStyle.put("currencyFormat", localeData.get("currencyFormat"));
		currencyStyle.put("currencySymbol", localeData.get("currencySymbol"));
		if ("true".equals(localeData.get("isLeft"))) {
			currencyStyle.put("isLeft", Boolean.TRUE);
		} else {
			currencyStyle.put("isLeft", Boolean.FALSE);
		}
		registry.putInFastCache(localeConfig, currencyStyle);
		return currencyStyle;
	}

	public static Map getConstraintFormat(String localeConfig, String formatName) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					"<getConstraintFormat> formatName: {} localeConfig: {}",
					new Object[] { formatName, localeConfig });
		}

		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = LocaleContext.getUserLocale();
		}

		if (localeConfig == null || "".equals(localeConfig)) {
			localeConfig = ResourceUtil.getDefaultConfig();
		}

		Map<String, String> localeData = registry.getNodeItems(
				"/System/i18n/" + localeConfig);
		String imageSymbol = localeData.get("constraintSymbol");
		Map<String, Object> constraintStyle = new HashMap<String, Object>();
		constraintStyle.put("constraintSymbol", imageSymbol);

		if ("true".equals(localeData.get("isLeft"))) {
			constraintStyle.put("isLeft", Boolean.TRUE);
		} else {
			constraintStyle.put("isLeft", Boolean.FALSE);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<getConstraintFormat> constraintSymbol: {} isLeft: {}",
					new Object[] {imageSymbol, ((Boolean) constraintStyle.get("isLeft"))
									.toString() });
		}

		return constraintStyle;
	}

	public static String getCurrency(double currency, String localeConfig,
			boolean needCurrencyName) throws FormatException {
		String stringData = convertDataToUI(CURRENCY, new Double(currency),
				localeConfig, null);

		Map currencyStyle = getCurrencyStyle(localeConfig);

		String currencySymbol = (String) currencyStyle.get("currencySymbol");
		boolean isLeft = ((Boolean) currencyStyle.get("isLeft")).booleanValue();

		if (needCurrencyName) {
			String userLocale = LocaleContext.getUserLocale();
			if (userLocale == null || "".equals(userLocale)) {
				userLocale = ResourceUtil.getDefaultConfig();
			}

			int htmlNumber = currencySymbol.charAt(0);

			String currencyName = ResourceUtil.getResource(userLocale,
					"Common", "CURRENCY_NAME_"
							+ htmlNumber);

			if (isLeft) {
				stringData = currencySymbol + stringData + currencyName;
			} else {
				stringData = currencyName + stringData + currencySymbol;
			}
		} else {
			if (isLeft) {
				stringData = currencySymbol + stringData;
			} else {
				stringData = stringData + currencySymbol;
			}
		}

		return stringData;
	}

}
