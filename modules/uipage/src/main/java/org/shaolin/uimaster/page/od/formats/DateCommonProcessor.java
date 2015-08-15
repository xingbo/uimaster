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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.uimaster.page.exception.FormatException;

public class DateCommonProcessor implements IFormatProcessor {
	private static final Logger logger = Logger
			.getLogger(DateCommonProcessor.class);

	public String convertDataToUI(Object data, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				localeData.get("dateFormat"), locale);
		if (propValues != null) {
			Object offsetObj = propValues.get("_clientTimeZoneOffset");
			if (offsetObj != null && offsetObj instanceof Integer) {
				try {
					Integer offset = (Integer) offsetObj;
					TimeZone tz = TimeZone.getDefault();
					tz.setRawOffset(offset.intValue());
					simpleDateFormat.setTimeZone(tz);
				} catch (Exception e) {
				}
			}
		}
		return simpleDateFormat.format(data);
	}

	public Object convertUIToData(String text, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				localeData.get("dateFormat"), locale);
		if (propValues != null) {
			Object offsetObj = propValues.get("_clientTimeZoneOffset");
			// WebflowConstants.CLIENT_TIMEZONE_OFFSET
			if (offsetObj != null && offsetObj instanceof Integer) {
				try {
					Integer offset = (Integer) offsetObj;
					TimeZone tz = TimeZone.getDefault();
					tz.setRawOffset(offset.intValue());
					simpleDateFormat.setTimeZone(tz);
				} catch (Exception e) {
				}
			}
		}
		try {
			return simpleDateFormat.parse(text);
		} catch (ParseException e) {
			logger.error("Parsing error: text is " + text + " pattern is "
					+ localeData.get("dateFormat"));
			throw new FormatException(ExceptionConstants.EBOS_ODMAPPER_006, e);
		}
	}
}