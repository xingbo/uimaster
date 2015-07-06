package org.shaolin.bmdp.i18n;

import java.util.LinkedList;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;

public class LocaleContext {
	private final String userLocale;

	private static LocaleContext defaultLocale = null;

	private final LinkedList<String> localeStack = new LinkedList<String>();

	private static final ThreadLocal<LocaleContext> localeContextHolder = 
			new ThreadLocal<LocaleContext>();

	public LocaleContext(String userLocale) {
		this.userLocale = userLocale;
	}

	public String getULocale() {
		return userLocale;
	}

	public void pushDLocale(String locale) {
		localeStack.add(locale);
	}

	public String getDLocale() {
		if (localeStack.isEmpty()) {
			return null;
		}
		return (String) localeStack.getLast();
	}

	public String popDLocale() {
		if (localeStack.isEmpty()) {
			return null;
		}
		return (String) localeStack.removeLast();
	}

	public static void createLocaleContext(String locale) {
		localeContextHolder.set(new LocaleContext(locale));
	}

	public static LocaleContext getLocaleContext() {
		LocaleContext lc = localeContextHolder.get();
		if (lc == null) {
			lc = defaultLocale;
		}
		return lc;
	}

	public static void clearLocaleContext() {
		localeContextHolder.set(null);
	}

	public static String getUserLocale() {
		LocaleContext lCtxt = getLocaleContext();
		if (lCtxt != null) {
			return lCtxt.getULocale();
		}
		return null;
	}

	public static String getDataLocale() {
		LocaleContext lCtxt = getLocaleContext();
		if (lCtxt != null) {
			return lCtxt.getDLocale();
		}
		return null;
	}

	public static void pushDataLocale(String locale) {
		LocaleContext lCtxt = getLocaleContext();
		if (lCtxt == null) {
			throw new I18NRuntimeException(ExceptionConstants.EBOS_COMMON_027);
		}
		lCtxt.pushDLocale(locale);
	}

	public static String popDataLocale() {
		LocaleContext lCtxt = getLocaleContext();
		if (lCtxt != null) {
			return lCtxt.popDLocale();
		}
		return null;
	}

	public static void setDefaultLocale(String locale) {
		if (defaultLocale == null) {
			defaultLocale = new LocaleContext(locale);
		}
	}

}
