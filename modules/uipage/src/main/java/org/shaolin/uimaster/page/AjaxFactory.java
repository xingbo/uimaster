package org.shaolin.uimaster.page;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.uimaster.page.ajax.handlers.IAjaxCommand;
import org.shaolin.uimaster.page.od.formats.FormatUtil;

/**
 * loading AJAX configuration.
 */
public class AjaxFactory
{
    private static final Logger logger = LoggerFactory.getLogger(AjaxFactory.class);
    
    /**
     * the service providers.
     */
    private static Map<String, IAjaxCommand> services = new HashMap<String, IAjaxCommand>();
    
    static
    { 
        try
        {
            // register common services.
            register("I18NService", new I18NService());
            register("DateFormatService", new DateFormatService());
            register("SetTimezoneOffset", new SetTimezoneOffset());
            register("CurrencyFormatService", new CurrencyFormatService());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
        if(logger.isDebugEnabled())
        {
            logger.debug("Finish AjaxFactory.");
        }
    }
    
	public static void register(String serviceName, IAjaxCommand serviceClass) {
		logger.debug("Register service: " + serviceName);
		services.put(serviceName, serviceClass);
	}
    
    /**
     * get service name.
     * 
     * @param serviceName
     * @return
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static IAjaxCommand getIAjaxCommand(String serviceName) throws InstantiationException, IllegalAccessException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Execution command: "+serviceName);
        }
        if(services.containsKey(serviceName))
        {
            return services.get(serviceName);
        }
        else
        {
        	throw new I18NRuntimeException("The {0} does not register service provider",new Object[]{serviceName});
        }
    }
    
    public static boolean isProvideService( String serviceName )
    {
        return services.containsKey(serviceName);
    }
    
    public static int serviceSize()
    {
        return services.size();
    }
    
	private static class I18NService implements IAjaxCommand {
		
		private final Logger logger = LoggerFactory.getLogger(I18NService.class);

		private static final String I18N_WIDGET_LABEL_INDICATOR = "$";
		private static final String I18N_WIDGET_LABEL_SEPARATOR = "#";

		@Override
		public Object execute(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String value = "";
			try {
				String keyInfo = request.getParameter("KEYINFO");
				String language = request.getParameter("LANGUAGE");
				String arguments = request.getParameter("ARGUMENTS");
				if ((keyInfo != null) && (keyInfo.length() > 0)) {
					keyInfo = keyInfo.trim();
					String[] bundleKey = HTMLUtil.splitBundleKey(keyInfo);

					String bundle = bundleKey[0];
					String key = bundleKey[1];
					String locale = null;

					if ((language != null) && (!"".equals(language))) {
						locale = language;
					} else {
						locale = LocaleContext.getUserLocale();
					}

					if ((arguments != null) && (arguments.length() > 0)) {
						String[] params = HTMLUtil.parseArguments(arguments);
						for (int i = 0; i < params.length; i++) {
							int position = params[i].indexOf(I18N_WIDGET_LABEL_SEPARATOR);
							int length = params[i].length();
							if ((!params[i].startsWith(I18N_WIDGET_LABEL_INDICATOR)) || (position <= 1)
									|| (length <= 5)) {
								continue;
							}
							String widgetLabelBundle = params[i].substring(1, position);
							String widgetLabelKey = params[i].substring( position + 1, length);
							String labelText = ResourceUtil.getResource(locale, widgetLabelBundle,
									widgetLabelKey, new Object[]{});

							params[i] = (labelText != null ? labelText : "");
						}
						value = ResourceUtil.getResource(locale, bundle, key, params);
					} else {
						value = ResourceUtil.getResource(locale, bundle, key);
					}
					if (logger.isTraceEnabled()) {
						logger.trace("Read the i18n attribute: locale={}, pair {}={}", new Object[] {locale, keyInfo, value});
					}
				}
			} catch (Exception ex) {
				logger.info(ex.getMessage(), ex);
			}

			if (value == null) {
				value = "";
			}
			return value;
		}
	}
	
	private static class DateFormatService implements IAjaxCommand {

		private final Logger logger = LoggerFactory.getLogger(DateFormatService.class);

		@Override
		public Object execute(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String value = "";
			try {
				String date = request.getParameter("DATE");
				String formatName = request.getParameter("FORMAT");
				String formattedDate = request.getParameter("DATESTRING");
				String dateType = request.getParameter("DATETYPE");
				String offset = request.getParameter("OFFSET");

				String localeConfig = LocaleContext.getUserLocale();
				if ((localeConfig == null) || (localeConfig.equals(""))) {
					localeConfig = ResourceUtil.getDefaultConfig();
				}

				if ((dateType == null) || (dateType.equals(""))) {
					dateType = FormatUtil.DATE;
				}
				if ((!dateType.equals("date"))
						&& (!dateType.equals("dateTime"))) {
					dateType = FormatUtil.DATE;
				}

				//TODO:
				value = FormatUtil.convertDataToUI(FormatUtil.DATE, date,
						localeConfig, null);
			} catch (Exception ex) {
				logger.info(ex.getMessage(), ex);
			}

			if (value == null) {
				value = "";
			}
			return value;
		}

	}
	
	private static class SetTimezoneOffset implements IAjaxCommand {
		private final Logger logger = LoggerFactory.getLogger(SetTimezoneOffset.class);

		public Object execute(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			try {
				String date = request.getParameter("OFFSET");
				int offset = Integer.parseInt(date) * -60000;
				request.getSession(true).setAttribute("_clientTimeZoneOffset",
						Integer.valueOf(offset));
			} catch (Exception ex) {
				logger.info(ex.getMessage(), ex);
			}
			return "";
		}
	}
	
	private static class CurrencyFormatService implements IAjaxCommand {
		private final Logger logger = LoggerFactory.getLogger(DateFormatService.class);

		public Object execute(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			String value = null;
			try {
				String date = request.getParameter("_id");
				String format = request.getParameter("_format");
				String locale = request.getParameter("_locale");
				String text = request.getParameter("_text");
				String number = request.getParameter("_number");
				Object obj;
				if (number != null) {
					obj = FormatUtil.convertDataToUI(FormatUtil.CURRENCY,
							new Double(number), locale, null);
				} else {
					obj = FormatUtil.convertUIToData(FormatUtil.CURRENCY, text,
							locale, null);
				}
				value = String.valueOf(obj);
			} catch (Exception ex) {
				logger.info(ex.getMessage(), ex);
			}

			if (value == null) {
				value = "";
			}
			return value;
		}
	}
}
