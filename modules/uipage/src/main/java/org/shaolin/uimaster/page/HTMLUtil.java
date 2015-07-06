package org.shaolin.uimaster.page;

import java.awt.ComponentOrientation;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.jsp.JspException;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.page.ExpressionPropertyType;
import org.shaolin.bmdp.datamodel.page.PropertyType;
import org.shaolin.bmdp.datamodel.page.PropertyValueType;
import org.shaolin.bmdp.datamodel.page.ResourceBundlePropertyType;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.UISkinType;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.bmdp.utils.ClassLoaderUtil;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.html.layout.IUISkin;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.security.ComponentPermission;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLUtil
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLUtil.class);

    public static final String MAPPING_NAME_PREFIX = "org.shaolin.uimaster.page.widgets.HTML";

    private static final Map<String, Class<?>> htmlUIClassMap = new ConcurrentHashMap<String, Class<?>>();

    private static final Map<String, Class<?>> skinMap = new ConcurrentHashMap<String, Class<?>>();

    private static boolean isFormatHTML = WebConfig.isFormatHTML();

    /**
	 * change to html code
	 * 
	 * the performance is good!
	 * 
	 * @param input
	 *            code to be changed
	 * 
	 * @return html code
	 * 
	 */
	public static String formatHtmlValue(String input) {
		if (input == null) {
			return "null";
		}
		for (int i = 0, n = input.length(); i < n; i++) {
			char c = input.charAt(i);
			if (c == '>') {
				return _changeCode(input, i, n, "&gt;");
			}
			if (c == '<') {
				return _changeCode(input, i, n, "&lt;");
			}
			if (c == '"') {
				return _changeCode(input, i, n, "&quot;");
			}
		}
		return input;
	}

	private static String _changeCode(String input, int i, int n, String escape) {
		StringBuffer sb = new StringBuffer(input.substring(0, i));
		sb.append(escape);
		for (i++; i < n; i++) {
			char c = input.charAt(i);
			switch (c) {
			case '>':
				sb.append("&gt;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			default:
				sb.append(c);
			}
		}
		return new String(sb);
	}

	public static String htmlEncode(String input) {
		return formatHtmlValue(input);
	}
    
    /**
     * This method will replace \\ to \ to avoid html escape issue
     * 
     * @param origin
     */
    public static String handleEscape(String origin)
    {
        StringBuffer sb = new StringBuffer(origin.length());
        int index1 = 0;
        int index2 = 0;
        while (true)
        {
            index1 = origin.indexOf("\\\\", index2);
            if (index1 < 0)// to the end
            {
                sb.append(origin.substring(index2));
                break;
            }
            else
            {
                sb.append(origin.substring(index2, index1));
                sb.append("\\");
                index2 = index1 + 2;
            }
        }
        return sb.toString();
    }

    public static String getWebRoot()
    {
        return WebConfig.getWebContextRoot();
    }

	public static String[] parseArguments(String argument) {
		List<String> list = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(argument, "||");
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}

		String[] params = new String[list.size()];
		list.toArray(params);
		return params;
	}

	public static String[] splitBundleKey(String bundleKey) {
		int index = bundleKey.indexOf("||");
		if (index == -1) {
			return null;
		}

		String[] s = new String[2];
		s[0] = bundleKey.substring(0, index);
		s[1] = bundleKey.substring(index + 2);

		return s;
	}
    
    public static void forward(HTMLSnapshotContext context, String pageName) throws JspException
    {
    	UIPageObject pageObject = parseUIPage(pageName);
		PageDispatcher dispatcher = new PageDispatcher(pageObject);
		dispatcher.forward(context);
    }

    public static UIPageObject parseUIPage(String pageName)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("get uipage: {} from cache", pageName);
        }
        return PageCacheManager.getUIPageObject(pageName);
    }

    public static UIFormObject parseUIEntity(String entityName)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("get uientity: {} from cache", entityName);
        }

        return PageCacheManager.getUIFormObject(entityName);
    }

    public static UIFormObject getUIForm(String entityName)
    {
        return PageCacheManager.getUIFormObject(entityName);
    }

    public static HTMLWidgetType getHTMLUIComponent(String UIID, HTMLSnapshotContext context,
            String type)
    {
        Map propMap = new HashMap();
        propMap.put("type", type);
        return getHTMLUIComponent(UIID, context, propMap, null, null, null, null, true);
    }

    public static HTMLWidgetType getHTMLUIComponent(String UIID, HTMLSnapshotContext context,
            Map propMap, Map eventMap, Boolean readOnly, Map tempMap, HTMLLayoutType htmlLayout,
            boolean fromOD)
    {
        HTMLWidgetType htmlComponent = null;
        String uiComponentType = (String)propMap.get("type");
        try
        {
            if (htmlUIClassMap.containsKey(uiComponentType))
            {
                htmlComponent = (HTMLWidgetType)((Class)htmlUIClassMap.get(uiComponentType))
                        .newInstance();
            }
            else
            {
                String typeString = MAPPING_NAME_PREFIX + uiComponentType;
                Class cls = ClassLoaderUtil.loadClass(typeString);
                htmlUIClassMap.put(uiComponentType, cls);
                htmlComponent = (HTMLWidgetType)cls.newInstance();
            }
        }
        catch (Exception e)
        {
            logger.error(
                    "error occured when get HTMLUIComponent for type: "
                            + uiComponentType + " \n error message is " + e.getMessage(), e);
        }

        htmlComponent.setContext(context);
        htmlComponent.setId(UIID);
        htmlComponent.setReadOnly(readOnly);
        htmlComponent.setPrefix(context.getHTMLPrefix());
        htmlComponent.setHTMLLayout(htmlLayout);
        htmlComponent.addAttribute(propMap);
        htmlComponent.addAttribute(tempMap);
        htmlComponent.addEventListener(eventMap);
        htmlComponent.setFrameInfo(context.getFrameInfo());

        if (!fromOD)
        {
            addSecurityControl(context, htmlComponent);
        }

        return htmlComponent;
    }

    public static HTMLLayoutType getHTMLLayoutType(String uiLayoutType)
    {
        HTMLLayoutType htmlLayout = null;
        try
        {
            if (htmlUIClassMap.containsKey(uiLayoutType))
            {
                htmlLayout = (HTMLLayoutType)((Class)htmlUIClassMap.get(uiLayoutType))
                        .newInstance();
            }
            else
            {
                String typeString = "org.shaolin.uimaster.page.widgets.HTML" + uiLayoutType;
                Class cls = ClassLoaderUtil.loadClass(typeString);
                htmlUIClassMap.put(uiLayoutType, cls);
                htmlLayout = (HTMLLayoutType)cls.newInstance();
            }
        }
        catch (Exception e)
        {
            logger.error(
                    "error occured when get HTMLLayoutType for type: "
                            + uiLayoutType + " \n error message is " + e.getMessage(), e);
        }

        return htmlLayout;
    }

    public static IUISkin getUISkinObj(UISkinType uiskin, VariableEvaluator ee)
    {
        IUISkin uiskinObj = null;
        if (uiskin != null)
        {
            String uiskinName = uiskin.getSkinName();
            try
            {
                if (skinMap.containsKey(uiskinName))
                {
                    uiskinObj = (IUISkin)((Class)skinMap.get(uiskinName)).newInstance();
                }
                else
                {
                    Class cls = ClassLoaderUtil.loadClass(uiskinName);
                    skinMap.put(uiskinName, cls);
                    uiskinObj = (IUISkin)cls.newInstance();
                }
                
                List<PropertyType> params = uiskin.getParams();
                for (PropertyType param: params)
                {
                    String paramName = param.getName();
                    PropertyValueType paramValue = param.getValue();
                    if (paramValue instanceof StringPropertyType)
                    {
                        String value = ((StringPropertyType)paramValue).getValue();
                        uiskinObj.setParam(paramName, value);
                    }
                    else if (paramValue instanceof ExpressionPropertyType)
                    {
                        Object value = ee.evaluateExpression(((ExpressionPropertyType)paramValue).getExpression());
                        uiskinObj.setParam(paramName, value.toString());
                    }
                    else if (paramValue instanceof ResourceBundlePropertyType)
                    {
                        String userLocale = LocaleContext.getUserLocale();
                        String bundle = ((ResourceBundlePropertyType)paramValue).getBundle();
                        String key = ((ResourceBundlePropertyType)paramValue).getKey();
                        String value = ResourceUtil.getResource(userLocale, bundle, key);
                        uiskinObj.setParam(paramName, value);
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("error occured when get uiskin: "
                        + uiskinName + " \n error message is " + e.getMessage(), e);
            }
        }

        return uiskinObj;
    }

    public static IUISkin getSystemUISkinObj(HTMLWidgetType htmlComponent)
    {
        IUISkin uiskinObj = null;
        String uiskin = WebConfig.getSystemUISkin(htmlComponent.getClass().getName());
        if (uiskin != null)
        {
            String uiskinName = uiskin;
            int beginIndex = uiskin.indexOf("?");
            if (beginIndex > -1)
            {
                uiskinName = uiskin.substring(0, beginIndex);
            }
            try
            {
                if (skinMap.containsKey(uiskinName))
                {
                    uiskinObj = (IUISkin)((Class)skinMap.get(uiskinName)).newInstance();
                }
                else
                {
                    Class cls = ClassLoaderUtil.loadClass(uiskinName);
                    skinMap.put(uiskinName, cls);
                    uiskinObj = (IUISkin)cls.newInstance();
                }
                htmlComponent.addAttribute(uiskinObj.getAttributeMap(htmlComponent));

                if (beginIndex > -1)
                {
                    String uiskinParams = uiskin.substring(beginIndex + 1);

                    StringTokenizer st = new StringTokenizer(uiskinParams, "&");
                    while (st.hasMoreTokens())
                    {
                        String uiskinParam = st.nextToken();
                        int beginIndex2 = uiskinParam.indexOf("=");
                        if (beginIndex2 == -1)
                        {
                            // logger.error("UISkin param error for " + uiskin, new Exception());
                            continue;
                        }
                        String paramName = uiskinParam.substring(0, beginIndex2);
                        String paramValue = uiskinParam.substring(beginIndex2 + 1);
                        uiskinObj.setParam(paramName, paramValue);
                    }
                }
            }
            catch (Exception e)
            {
                logger.error(
                        "error occured when get system uiskin: "
                                + uiskinName + " \n error message is " + e.getMessage(), e);
            }
        }

        return uiskinObj;
    }

    public static void getJSInclude(String entityName, Map<String, String> jsIncludeMap, List<String> jsIncludeList, boolean includeCommon)
    {
    	if (includeCommon) {
	        String[] commons = WebConfig.getCommonJs();
	        for (String common: commons)
	        {
	            String importJSCode = "<script type=\"text/javascript\" src=\"" + common
	                    + "?_timestamp=";
	            jsIncludeMap.put(common, importJSCode);
	            jsIncludeList.add(common);
	        }
    	}
        String[] singleCommons = WebConfig.getSingleCommonJS(entityName);
        for (String single: singleCommons)
        {
            String importJSCode = "<script type=\"text/javascript\" src=\"" + single
                    + "?_timestamp=";
            jsIncludeMap.put(single, importJSCode);
            jsIncludeList.add(single);
        }
        
        String jsFileName = WebConfig.getImportJS(entityName);
        String importJSCode = "<script type=\"text/javascript\" src=\"" + jsFileName
                + "?_timestamp=";
        jsIncludeMap.put(jsFileName, importJSCode);
        jsIncludeList.add(jsFileName);
    }

    public static void importJS(HTMLSnapshotContext context, String entityName, int depth)
            throws JspException
    {
        String jsRootPath = getWebRoot();
        String jsFileName = WebConfig.getImportJS(entityName);
        generateJSHTML(context, jsRootPath, jsFileName, depth);

        String[] commons = WebConfig.getCommonJs();
        for (String common: commons) {
        	generateJSHTML(context, jsRootPath, common, depth);
        }
        String[] singleCommons = WebConfig.getSingleCommonJS(entityName);
        for (String single: singleCommons) {
			generateJSHTML(context, jsRootPath, single, depth);
		}
    }

    public static void generateJSHTML(HTMLSnapshotContext context, String jsRootPath,
            String jsFileName, int depth) throws JspException
    {
		if (context.containsJsName(jsFileName)) {
			return;
		} else {
			context.addJsName(jsFileName);
		}

		if (jsFileName.endsWith(".js")) {
			context.generateJS("<script type=\"text/javascript\" src=\"");
			context.generateJS(jsRootPath);
			context.generateJS(jsFileName);
			context.generateJS("?_timestamp=");
			context.generateJS(WebConfig.getTimeStamp());
			context.generateJS("\"></script>");
			generateTab(context, depth);
		}
    }

    public static Map evaluateExpression(Map propMap, Map expMap, Map tempMap,
            VariableEvaluator ee) throws EvaluationException
    {
		if (expMap != null && !expMap.isEmpty()) {
			if (tempMap == null) {
				tempMap = new HashMap();
			}
			Iterator it = expMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String propName = (String) entry.getKey();
				Object propValue = entry.getValue();
				if (propValue instanceof List) {// for ArrayPropertyType
					List<String> tempList = null;
					if (tempMap.containsKey(propName)) {
						tempList = (List) tempMap.get(propName);
					}
					List originList = (List) (propMap).get(propName);
					List valueList = (List) propValue;
					String[] values = new String[valueList.size()];
					for (int i = 0, n = valueList.size(); i < n; i++) {
						ExpressionType expression = (ExpressionType) valueList
								.get(i);
						if (expression != null) {
							if (logger.isTraceEnabled()) {
								logger.trace("evaluate expression: {}", expression.getExpressionString());
							}
							Object value = ee.evaluateExpression(expression);
							values[i] = value.toString();
						} else {
							if (tempList != null) {
								values[i] = tempList.get(i);
							} else {
								values[i] = (String) originList.get(i);
							}
						}
					}
					tempList = Arrays.asList(values);

					tempMap.put(propName, tempList);
				} else if (!tempMap.containsKey(propName)) {
					ExpressionType expression = (ExpressionType) propValue;
					if (logger.isTraceEnabled()) {
						logger.trace("evaluate expression: {}", expression.getExpressionString());
					}
					Object expValue = ee.evaluateExpression(expression);
					tempMap.put(propName, expValue);
				}
			}
		}
        return tempMap;
    }

    public static Map internationalization(Map propMap, Map i18nMap, Map tempMap)
    {
        return internationalization(propMap, i18nMap, tempMap, null);
    }

    public static Map internationalization(Map propMap, Map i18nMap, Map tempMap,
            HTMLSnapshotContext context)
    {
        if (i18nMap != null && !i18nMap.isEmpty())
        {
            String locale = LocaleContext.getUserLocale();
            if (tempMap == null)
            {
                tempMap = new HashMap();
            }
            Iterator it = i18nMap.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry)it.next();
                String propName = (String)entry.getKey();
                Object propValue = entry.getValue();
                if (propValue instanceof List)
                {// for ArrayPropertyType
                    List tempList = null;
                    if (tempMap.containsKey(propName))
                    {
                        tempList = (List)tempMap.get(propName);
                    }
                    List originList = (List)propMap.get(propName);
                    List valueList = (List)propValue;
                    String[] values = new String[valueList.size()];
                    for (int i = 0, n = valueList.size(); i < n; i++)
                    {
                        String bundle = (String)valueList.get(i);
                        if (bundle != null)
                        {
                            String key = (String)originList.get(i);
                            String value;
                            if (context != null)
                            {
                                if (propName.equals("selectedValueConstraint")
                                        || propName.equals("selectedValuesConstraint"))
                                {
                                    value = ResourceUtil.getResource(locale, bundle, key);
                                    context.generateHTML("<input type=\"hidden\" name=\"__resourcebundle\" value=\"");
                            		context.generateHTML(bundle);
                            		context.generateHTML("||");
                            		context.generateHTML(key);
                            		context.generateHTML("\" msg=\"");
                            		context.generateHTML(HTMLUtil.formatHtmlValue(value));
                            		context.generateHTML("\">");
                                }
                            }
                            if (key == null)
                            {
                                logger.error("Component set bundle name but not set key in attribute.");
                                value = "no key";
                            }
                            else
                            {
                                value = ResourceUtil.getResource(locale, bundle, key);
                                if (context != null)
                                {
                                    if (propName.equals("selectedValueConstraint")
                                            || propName.equals("selectedValuesConstraint"))
                                    {
                                        context.generateHTML("<input type=\"hidden\" name=\"__resourcebundle\" value=\"");
                                        context.generateHTML(bundle);
                                        context.generateHTML("||");
                                        context.generateHTML(key);
                                        context.generateHTML("\" msg=\"");
                                        context.generateHTML(HTMLUtil.formatHtmlValue(value));
                                        context.generateHTML("\">");
                                    }
                                }
                            }
                            values[i] = value;
                        }
                        else
                        {
							if (tempList != null) {
								values[i] = (String) tempList.get(i);
							} else {
								values[i] = (String) originList.get(i);
							}
                        }
                    }
                    tempList = Arrays.asList(values);
                    tempMap.put(propName, tempList);
                }
                else
                {
                    String bundle = (String)propValue;
                    String key = (String)propMap.get(propName);
                    String value;
                    if (context != null)
                    {
                        if (propName.equals("selectedValueConstraintText")
                                || propName.equals("selectedValuesConstraintText")
                                || propName.endsWith("-msg--"))
                        {
                            value = ResourceUtil.getResource(locale, bundle, key);
                            context.generateHTML("<input type=\"hidden\" name=\"__resourcebundle\" value=\"");
                            context.generateHTML(bundle);
                            context.generateHTML("||");
                            context.generateHTML(key);
                            context.generateHTML("\" msg=\"");
                            context.generateHTML(HTMLUtil.formatHtmlValue(value));
                            context.generateHTML("\">");
                        }
                    }
                    if (key == null)
                    {
                        logger.error("Component set bundle name but not set key in attribute.");
                        value = "no key";
                    }
                    else
                    {
                        value = ResourceUtil.getResource(locale, bundle, key);
                        if (context != null)
                        {
                            if (propName.equals("allowBlankText") || propName.equals("lengthText")
                                    || propName.equals("regexText")
                                    || propName.equals("selectedValueConstraintText")
                                    || propName.equals("selectedValuesConstraintText")
                                    || propName.endsWith("-msg--"))
                            {
                                context.generateHTML("<input type=\"hidden\" name=\"__resourcebundle\" value=\"");
                                context.generateHTML(bundle);
                                context.generateHTML("||");
                                context.generateHTML(key);
                                context.generateHTML("\" msg=\"");
                                context.generateHTML(HTMLUtil.formatHtmlValue(value));
                                context.generateHTML("\">");
                            }
                        }
                    }
                    tempMap.put(propName, value);
                }
            }
        }
        return tempMap;
    }

	public static Map merge(Map target, Map source) {
		if (source != null) {
			if (target == null) {
				target = source;
			} else {
				target.putAll(source);
			}
		}

		return target;
	}

	public static void generateTab(HTMLSnapshotContext context, int depth) {
		if (isFormatHTML) {
			StringBuffer sb = new StringBuffer("\n");
			for (int i = 0; i < depth; i++) {
				sb.append("\t");
			}
			context.generateHTML(sb.toString());
		}
	}

    public static DecimalFormatSymbols getLocaleFormatSymbol(String localString)
    {
        String[] languageAndCountry = localString.split("_");
        Locale locale = new Locale(languageAndCountry[0], languageAndCountry[1]);
        return new DecimalFormatSymbols(locale);
    }

	public static String getLanguageCode(String localeString) {
		String[] languageAndCountry = localeString.split("_");
		String language = languageAndCountry[0];
		if (language.equals("zh")) {
			if (languageAndCountry.length > 1) {
				return language + "-" + languageAndCountry[1];
			} else {
				return "zh-CN";
			}
		} else if (language.equals("pt")) {
			return "pt-BR";
		} else if (language.equals("sr")) {
			if (languageAndCountry.length > 1) {
				if (languageAndCountry[1].equals("SR")) {
					return "sr-SR";
				} else {
					return "sr";
				}
			} else {
				return "sr";
			}
		}
		return language;
	}

    public static boolean isLeftToRight(String userLocale)
    {
        ComponentOrientation ce = ComponentOrientation.getOrientation(ResourceUtil
                .getLocaleObject(userLocale));
        return ce.isLeftToRight();
    }

    public static String isLeftToRightTag(String userLocale)
    {
        ComponentOrientation ce = ComponentOrientation.getOrientation(ResourceUtil
                .getLocaleObject(userLocale));
        return "<input type=\"hidden\" id=\"isLeftToRight\" name=\"isLeftToRight\" value=\""
                + ce.isLeftToRight() + "\" >";
    }

    public static void generateJSBundleConstants(HTMLSnapshotContext context)
    {
        generateBundle(context, "Common", "AJAX_EXCEPTION_REQUEST_WAIT");
        generateBundle(context, "Common", "VERIFY_FAIL");
        generateBundle(context, "Common", "ALLOW_BLANK");
        generateBundle(context, "Common", "REGULAR_EXPRESSION");
        generateBundle(context, "Common", "MINIMUM_LENGTH");
        generateBundle(context, "Common", "MUST_CHECK");
        generateBundle(context, "Common", "SELECT_VALUE");
    }

    private static void generateBundle(HTMLSnapshotContext context, String bundle, String key)
    {
        String locale = LocaleContext.getUserLocale();
        String value = ResourceUtil.getResource(locale, bundle, key);
        context.generateHTML("<input type=\"hidden\" name=\"__resourcebundle\" value=\"" + bundle
                + "||" + key + "\" msg=\"" + HTMLUtil.formatHtmlValue(value) + "\">\n");
    }

    private static void addSecurityControl(HTMLSnapshotContext context,
            HTMLWidgetType htmlComponent)
    {
        ComponentPermission cp = context.getCompPermission(htmlComponent.getName(false));

        // merge view permission
        String[] viewPermissions = preCheckViewPermission(cp, htmlComponent);

        if (viewPermissions.length > 0)
        {
            // view permision configured
            if (!checkViewPermission(viewPermissions))
            {
                // no view permission
                // set visible = false
            	if (logger.isTraceEnabled()) {
            		logger.trace("the user doesn't own the permission to view the component: "
                        + htmlComponent.getName(false));
            	}
                htmlComponent.setVisible(false);
                // also disable validations on it
                htmlComponent.disableValidation();
            }
            else
            {
                // with view permission
                // set visible = true
                logger.debug("the user owns the permission to view the component: "
                        + htmlComponent.getName(false));
                htmlComponent.setVisible(true);

                if (htmlComponent.isEditPermissionEnabled())
                {
                    // merge edit permission
                    String[] editPermissions = preCheckEditPermission(cp, htmlComponent);

                    addEditableControl(editPermissions, htmlComponent);
                }
            }
        }
        // no view permission configured
        // do not change the visible property of component
        else if (htmlComponent.isEditPermissionEnabled())
        {
        	if (logger.isTraceEnabled()) {
        		logger.trace("no view permission configured on the component: "
                    + htmlComponent.getName(false));
        	}
        	
            // check the edit permission
            String[] editPermissions = preCheckEditPermission(cp, htmlComponent);

            addEditableControl(editPermissions, htmlComponent);
        }
    }

    /**
     * this method check whether the user owns the edit permission and add the editable control on
     * the component
     * 
     * @param editPermission
     * @param htmlComponent
     */
    private static void addEditableControl(String[] editPermissions, HTMLWidgetType htmlComponent)
    {
        if (editPermissions.length > 0)
        {
            // edit permission configured
            if (!checkEditPermission(editPermissions))
            {
            	if (logger.isTraceEnabled()) {
            		logger.trace("the user doesn't own the permission to edit the component: "
                        + htmlComponent.getName(false));
            	}
                // no edit permission
                htmlComponent.setEditable(false);
                htmlComponent.setReadOnly(true);
            }
            else
            {
                // with edit permission
                // set editable = true; readonly = false
            	if (logger.isTraceEnabled()) {
            		logger.trace("the user owns the permission to edit the component: "
                        + htmlComponent.getName(false));
            	}
                htmlComponent.setEditable(true);
                htmlComponent.setReadOnly(false);
            }
        }
        else
        {
            // no edit permission configured
            // doesn't change the editable property of component
        	if (logger.isTraceEnabled()) {
	            logger.trace("no edit permission configured on the component: "
	                    + htmlComponent.getName(false));
        	}
        }
    }

    public static String[] getViewPermission(ComponentPermission cp, String[] propPermission)
    {
        String[] viewPermission = new String[0];
        if (cp != null)
        {
            viewPermission = cp.getViewPermission();
        }

        if (viewPermission.length == 0 && propPermission != null && propPermission.length > 0)
        {
            viewPermission = propPermission;
        }

        return viewPermission;
    }

    public static String[] getViewPermission(ComponentPermission cp, VariableEvaluator ee,
            Map propMap, Map expMap, Map appendMap) throws EvaluationException
    {
        if (cp != null && cp.getViewPermission().length > 0)
        {
            logger.debug("read view permission from security_runconfig.xml");
            return cp.getViewPermission();
        }

        String propName = "viewPermission";
        Object propValue;
        if (appendMap != null)
        {
            propValue = appendMap.get(propName);
            if (propValue instanceof List)
            {
                logger.debug("read view permission(List) from reconfigurable variables");
                List<String> propValueList = (List<String>)propValue;
                return propValueList.toArray(new String[propValueList.size()]);
            }
            else if (propValue instanceof String)
            {
                logger.debug("read view permission(String) from reconfigurable variables");
                return new String[]{(String)propValue};
            }
            else
            {
                logger.debug("read view permission from reconfigurable variables failed");
            }
        }

        if (expMap != null)
        {
            propValue = expMap.get(propName);
            if (propValue instanceof ExpressionType)
            {
                ExpressionType expression = (ExpressionType)propValue;
                Object expValue = ee.evaluateExpression(expression);
                if (expValue instanceof List)
                {
                    logger.debug("evaluation view permission(List) from user code");
                    List<String> expList = (List<String>)expValue;
                    return expList.toArray(new String[expList.size()]);
                }
                else if (expValue instanceof String && expValue.toString().trim().length() != 0)
                {
                    logger.debug("evaluation view permission(String) from user code");
                    return new String[]{(String)expValue};
                }
            }
            logger.debug("read view permission from user code failed");
        }

        if (propMap != null)
        {
            propValue = propMap.get(propName);
            if (propValue instanceof List)
            {
                logger.debug("read view permission(List) from component propmap");
                List<String> propValueList = (List<String>)propValue;
                return propValueList.toArray(new String[propValueList.size()]);
            }
            else if (propValue instanceof String)
            {
                logger.debug("read view permission(String) from component propmap");
                return new String[]{(String)propValue};
            }
            else
            {
                logger.debug("read view permission from component propmap failed");
            }
        }

        return null;
    }

    public static boolean checkViewPermission(String[] viewPermissions)
    {
//        logger.debug("check whether the user owns the viewpmerission: " + viewPermissions);
//        return SecurityUtil.checkFunctionOrJAASRole(viewPermissions);
    	return false;
    }

    public static String[] getEditPermission(ComponentPermission cp, String[] propPermission)
    {
        String[] editPermission = new String[0];
        if (cp != null)
        {
            editPermission = cp.getEditPermission();
        }

        if (editPermission.length == 0 && propPermission != null && propPermission.length > 0)
        {
            editPermission = propPermission;
        }

        return editPermission;
    }

    public static boolean checkEditPermission(String[] editPermission)
    {
        logger.debug("check whether the user owns the editpmerission: " + editPermission);
//        return SecurityUtil.checkFunctionOrJAASRole(editPermission);
        //TODO:
        return true;
    }

    public static String[] preCheckViewPermission(ComponentPermission cp,
            HTMLWidgetType htmlComponent)
    {
        String[] viewPermissions = new String[0];
        Object compViewPermission = htmlComponent.getAttribute("viewPermission");
        if (compViewPermission != null)
        {
            if (compViewPermission instanceof String)
            {
                viewPermissions = getViewPermission(cp, new String[]{(String)compViewPermission});
            }
            else if (compViewPermission instanceof List)
            {
                List<String> compViewPermissionList = (List<String>)compViewPermission;
                String[] tempViewPermission = new String[compViewPermissionList.size()];
                viewPermissions = getViewPermission(cp,
                        compViewPermissionList.toArray(tempViewPermission));
            }
            else
            {
                // should never be here
                logger.error("The return type of HTMLComponentType.getAttribute(viewPermission) is neither String or List.");
            }
        }
        else
        {
            viewPermissions = getViewPermission(cp, new String[0]);
        }
        return viewPermissions;
    }

    public static String[] preCheckEditPermission(ComponentPermission cp,
            HTMLWidgetType htmlComponent)
    {
        String[] editPermissions = new String[0];
        Object compEditPermission = htmlComponent.getAttribute("editPermission");
        if (compEditPermission != null)
        {
            if (compEditPermission instanceof String)
            {
                editPermissions = getEditPermission(cp, new String[]{(String)compEditPermission});
            }
            else if (compEditPermission instanceof List)
            {
                List<String> compEditPermissionList = (List<String>)compEditPermission;
                String[] tempEditPermission = new String[compEditPermissionList.size()];
                editPermissions = getEditPermission(cp,
                        compEditPermissionList.toArray(tempEditPermission));
            }
            else
            {
                // should never be here
                logger.error("The return type of HTMLComponentType.getAttribute(editPermission) is neither String or List.");
            }
        }
        else
        {
            editPermissions = getEditPermission(cp, new String[0]);
        }
        return editPermissions;
    }
}
