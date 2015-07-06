package org.shaolin.uimaster.page.od.formats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.uimaster.page.exception.FormatException;

public class DateTimeCommonProcessor implements IFormatProcessor {
	private static final Logger logger = Logger
			.getLogger(DateTimeCommonProcessor.class);

	public String convertDataToUI(Object data, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		SimpleDateFormat simpleDateFormat = 
				new SimpleDateFormat(localeData.get("dateTimeFormat"), locale);
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
		SimpleDateFormat simpleDateFormat = 
				new SimpleDateFormat(localeData.get("dateTimeFormat"), locale);
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
					+ localeData.get("dateTimeFormat"));

			throw new FormatException(ExceptionConstants.EBOS_ODMAPPER_006, e);
		}
	}
}