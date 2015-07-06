package org.shaolin.uimaster.page.od.formats;

import java.util.Locale;
import java.util.Map;

import org.shaolin.uimaster.page.exception.FormatException;

public interface IFormatProcessor {
	public String convertDataToUI(Object data, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException;

	public Object convertUIToData(String text, Locale locale,
			Map<String, String> localeData, Map propValues)
			throws FormatException;
}