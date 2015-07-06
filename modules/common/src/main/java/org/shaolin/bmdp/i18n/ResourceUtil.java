package org.shaolin.bmdp.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.shaolin.bmdp.exceptions.ResourceBundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResourceUtil {

	private static Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

	private static final Map<String, String> localeConfigMap = 
			new HashMap<String, String>();

	private static final String LOCAL = "local";

	private static String defaultConfig = "default";

	private static String defaultLocale = "en_US";

	public static void init(String defaultLocale, Map<String, String> localeConfigMap) {
		ResourceUtil.defaultLocale = defaultLocale;
		ResourceUtil.localeConfigMap.putAll(localeConfigMap);
	}
	
	public static String getDefaultLocale() {
		return defaultLocale;
	}

	public static String getDefaultConfig() {
		return defaultConfig;
	}
	
	public static String getResource(String bundle, String key) {
		return getResource(null, bundle, key, LOCAL);
	}

	public static String getResource(String localeConfig,
			String bundle, String key) {
		return getResource(localeConfig, bundle, key, "");
	}
	
	public static String getResource(String localeConfig,
			String bundle, String key, Object[] args) {
		return getFormatValue(getResource(localeConfig, bundle, key, ""),
				args);
	}

	private static String getResource(String localeConfig, String bundle,
			String key, String type) {
		if (!validate(key)) {
			return null;
		}

		if (localeConfig == null) {
			localeConfig = defaultConfig;
		}

		String resource = getResourceByProperties(localeConfig, bundle, key);
		if (logger.isTraceEnabled() && resource != null) {
			logger.trace("Get the resource: {}. pass-in localeConfig: {}, bundle: {}, key: {}", 
						new Object[]{resource, localeConfig, bundle, key});
		}

		if (resource == null && localeConfig != null) {
			localeConfig = defaultConfig;
			resource = getResourceByProperties(localeConfig, bundle, key);

			if (logger.isTraceEnabled() && resource != null) {
				logger.trace("Get the resource: {}. default localeConfig: {}, bundle: {}, key: {}", 
						new Object[] {resource, localeConfig, bundle, key});
			}
		}

		return resource;
	}

	private static String getFormatValue(String value, Object[] args) {
		if (value != null) {
			value = MessageFormat.format(value, args);
		}
		return value;
	}
	
	public static String getResourceFromProperties(String localeConfig,
			String bundle, String key) {
		if (!validate(key)) {
			return null;
		}

		String resource = getResourceByProperties(localeConfig, bundle, key);

		if (resource == null && localeConfig != null) {
			localeConfig = defaultConfig;
			resource = getResourceByProperties(localeConfig, bundle, key);
		}
		return resource;
	}

	private static String getResourceByProperties(String localeConfig,
			String bundle, String key) {
		Locale l = getLocaleObject(localeConfig);

		String value = null;
		try {
			ResourceBundle rb = ResourceBundle.getBundle(bundle, l);
			value = rb.getString(key);
		} catch (NullPointerException e) {
		} catch (MissingResourceException e) {
		}
		return value;
	}

	// local invoke methods
	public static Locale getLocaleObject(String localeConfig) {
		String locale = getLocale(localeConfig);
		return getLocaleObjectFromString(locale);
	}

	public static String getLocale(String localeConfig) {
		if (localeConfig == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("localeConfig is null, so use the default config: "
						+ defaultConfig);
			}
			return defaultLocale;
		} else {
			String locale = (String) localeConfigMap.get(localeConfig);
			if (locale == null) {
				logger.warn("Can't find the localeConfig: {}, use the defaultConfig: {}", 
							new Object[] {localeConfig, defaultConfig});
				locale = defaultLocale;
			}
			return locale;
		}
	}

	public static Locale getLocaleObjectFromString(String locale) {
		String[] languageAndCountry = locale.split("_");
		if (languageAndCountry.length > 2) {
			return new Locale(languageAndCountry[0], languageAndCountry[1],
					languageAndCountry[2]);
		}
		if (languageAndCountry.length > 1) {
			return new Locale(languageAndCountry[0], languageAndCountry[1]);
		}
		return new Locale(languageAndCountry[0]);
	}

	public static boolean validate(String key) {
		if (key == null || key.length() == 0) {
			return false;
		}
		return true;
	}

	public static void validate(String locale, String key)
			throws ResourceBundleException {
		if (locale == null || "".equals(locale)) {
			throw new ResourceBundleException(
					ExceptionConstants.EBOS_COMMON_041);
		}

		if (key == null || "".equals(key)) {
			throw new ResourceBundleException(
					ExceptionConstants.EBOS_COMMON_040);
		}
	}

	public static void validate(String locale, String bundle, String key)
			throws ResourceBundleException {
		if (locale == null || "".equals(locale)) {
			throw new ResourceBundleException(
					ExceptionConstants.EBOS_COMMON_041);
		}

		if (bundle == null || "".equals(bundle)) {
			throw new ResourceBundleException(
					ExceptionConstants.EBOS_COMMON_039);
		}

		if (key == null || "".equals(key)) {
			throw new ResourceBundleException(
					ExceptionConstants.EBOS_COMMON_040);
		}
	}

	public static String mergeKey(String localeConfig, String bundle, String key) {
		if (bundle == null) {
			return localeConfig + "||" + key;
		} else {
			return localeConfig + "||" + bundle + "||" + key;
		}
	}

	// util method
	public static String mergeBundleAndKey(String bundle, String key) {
		return bundle + "||" + key;
	}

}