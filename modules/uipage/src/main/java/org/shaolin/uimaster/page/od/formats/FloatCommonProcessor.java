package org.shaolin.uimaster.page.od.formats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.uimaster.page.exception.FormatException;

public class FloatCommonProcessor implements IFormatProcessor {
	private static final Logger logger = Logger
			.getLogger(FloatCommonProcessor.class);

	public String convertDataToUI(Object data, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		DecimalFormat decimalFormat = new DecimalFormat(
				localeData.get("floatNumberFormat"),
				new DecimalFormatSymbols(locale));
		return decimalFormat.format(data);
	}

	public Object convertUIToData(String text, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException {
		DecimalFormat decimalFormat = new DecimalFormat(
				localeData.get("floatNumberFormat"),
				new DecimalFormatSymbols(locale));
		try {
			return decimalFormat.parse(text);
		} catch (ParseException e) {
			logger.error("Parsing error: text is " + text + " pattern is "
					+ localeData.get("floatNumberFormat"));
			throw new FormatException(ExceptionConstants.EBOS_ODMAPPER_006, e);
		}
	}
}