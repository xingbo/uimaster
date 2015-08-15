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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.uimaster.page.exception.FormatException;

public class CurrencyCommonProcessor implements IFormatProcessor {
	private static final Logger logger = Logger
			.getLogger(CurrencyCommonProcessor.class);

	public String convertDataToUI(Object data, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		DecimalFormat decimalFormat = new DecimalFormat(
				localeData.get("currencyFormat"), new DecimalFormatSymbols(
						locale));
		return decimalFormat.format(data);
	}

	public Object convertUIToData(String text, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		DecimalFormat decimalFormat = new DecimalFormat(
				localeData.get("currencyFormat"), dfs);
		try {
			return decimalFormat.parse(text);
		} catch (ParseException e) {
			String pattern = dfs.getCurrencySymbol()
					+ localeData.get("currencyFormat");
			logger.debug("Try to parse with the whole pattern: " + pattern);

			decimalFormat = new DecimalFormat(pattern, dfs);
			try {
				return decimalFormat.parse(text);
			} catch (ParseException ee) {
				logger.error("Parsing error: text is " + text + " pattern is "
						+ pattern);

				throw new FormatException(ExceptionConstants.EBOS_ODMAPPER_006,
						ee);
			}
		}
	}

}