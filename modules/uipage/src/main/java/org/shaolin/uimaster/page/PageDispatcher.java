package org.shaolin.uimaster.page;

import java.awt.ComponentOrientation;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.shaolin.bmdp.datamodel.page.ReconfigurablePropertyType;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.uimaster.html.layout.IUISkin;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.shaolin.uimaster.page.exception.AjaxException;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.od.PageODProcessor;
import org.shaolin.uimaster.page.od.formats.FormatUtil;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageDispatcher {

	private static Logger logger = LoggerFactory.getLogger(PageDispatcher.class);
	
	private final UIFormObject uiEntity;
	
	private final UIPageObject pageObject;
	
	private final EvaluationContext evaContext;
	
	public PageDispatcher(UIFormObject uiEntity, EvaluationContext evaContext) {
		this.uiEntity = uiEntity;
		this.pageObject = null;
		this.evaContext = evaContext;
	}
	
	public PageDispatcher(UIPageObject pageObject) {
		this.uiEntity = null;
		this.pageObject = pageObject;
		this.evaContext = new DefaultEvaluationContext();
	}
	
	/**
	 * HTMLUIEntity.forward
	 * 
	 * @param context
	 * @param depth
	 * @throws JspException
	 */
	public void forward(HTMLSnapshotContext context, int depth) throws JspException
    {
        forward(context, depth, null, null);
    }

	/**
	 * HTMLUIEntity.forward
	 * 
	 * @param context
	 * @param depth
	 * @param readOnly
	 * @param parent
	 * @throws JspException
	 */
    public void forward(HTMLSnapshotContext context, int depth, Boolean readOnly,
            HTMLReferenceEntityType parent) throws JspException
    {
        try
        {
        	if (uiEntity == null) {
        		logger.warn("Please invoke UIEntityObject.forward");
        		return;
        	}
			if (logger.isDebugEnabled()) {
				logger.debug("<---HTMLUIEntity.forward--->start to access the uientity: "
						+ uiEntity.getName());
			}
			// set a null value for all tables as a mark.
			if (evaContext instanceof DefaultEvaluationContext 
					&& !((DefaultEvaluationContext)evaContext).hasVariable("tableCondition")) {
				evaContext.setVariableValue("tableCondition", null);
			}
            VariableEvaluator ee = new VariableEvaluator(this.evaContext);
            
            Map appendComponentMap = new HashMap();
            setReconfigurablePropertyValue(context, appendComponentMap);

            Map propMap = uiEntity.getComponentProperty(uiEntity.getBodyName());
            Map eventMap = uiEntity.getComponentEvent(uiEntity.getBodyName());
            Map i18nMap = uiEntity.getComponentI18N(uiEntity.getBodyName());
            Map expMap = uiEntity.getComponentExpression(uiEntity.getBodyName());

            Map tempMap = null;
            tempMap = HTMLUtil.evaluateExpression(propMap, expMap, tempMap, ee);
            tempMap = HTMLUtil.internationalization(propMap, i18nMap, tempMap, context);
            tempMap = HTMLUtil.merge(tempMap, (Map)appendComponentMap.get(uiEntity.getBodyName()));

            String selfReadOnly = (String)propMap.get("readOnly");
            Boolean realReadOnly;
			if (readOnly == null) {
				realReadOnly = Boolean.valueOf(ee.evaluateReadOnly(selfReadOnly));
			} else {
				realReadOnly = readOnly;
			}
            if (logger.isTraceEnabled())
            {
                logger.trace("The readOnly value for component: "
                        + uiEntity.getBodyName() + " in the uientity: " + uiEntity.getName() + " is "
                        + (realReadOnly == null ? "null" : realReadOnly.toString()));
            }

            HTMLWidgetType htmlComponent;
            if (parent != null) {
            	htmlComponent = context.getHtmlWidget(parent.getName() + "." + uiEntity.getBodyName());
            	htmlComponent.setHTMLLayout(parent.getHTMLLayout());

                String visible = (String)parent.getAttribute("visible");
                if (visible != null)
                {
                    if (!"false".equals(visible))
                    {
                        visible = "true";
                    }
                    htmlComponent.addAttribute("visible", visible);
                }
            } else {
            	htmlComponent = context.getHtmlWidget(uiEntity.getBodyName());
            }
            htmlComponent.addAttribute(propMap);
            htmlComponent.addAttribute(tempMap);
            htmlComponent.addEventListener(eventMap);
            realReadOnly = htmlComponent.getReadOnly();

            IUISkin uiskinObj = uiEntity.getUISkinObj(uiEntity.getBodyName(), ee, htmlComponent);
            if (uiskinObj != null)
            {
                htmlComponent.addAttribute(uiskinObj.getAttributeMap(htmlComponent));
                htmlComponent.preIncludePage(context);
                try
                {
                    uiskinObj.generatePreCode(htmlComponent);
                }
                catch (Exception e)
                {
                    logger.error("uiskin error: ", e);
                }
            }
            else
            {
                htmlComponent.preIncludePage(context);
            }

            if (uiskinObj == null || !uiskinObj.isOverwrite())
            {
                htmlComponent.generateBeginHTML(context, uiEntity, depth);
            }

            Widget newAjax = htmlComponent.createAjaxWidget(ee);
            if (newAjax != null) {
            	context.addAjaxWidget(newAjax.getId(), newAjax);
            }
            
            uiEntity.getBodyLayout().generateHTML(context, depth + 1, realReadOnly, 
            		appendComponentMap, ee, uiskinObj, htmlComponent);
            HTMLUtil.generateTab(context, depth);

            if (uiskinObj == null || !uiskinObj.isOverwrite())
            {
                htmlComponent.generateEndHTML(context, uiEntity, depth);
            }
			if (uiskinObj != null) {
				try {
					uiskinObj.generatePostCode(htmlComponent);
				} catch (Exception e) {
					logger.error("uiskin error: ", e);
				}
			}
            htmlComponent.postIncludePage(context);

        }
        catch (Exception e)
        {
            throw new JspException("<---HTMLUIEntity.forward--->Be interrupted when access uientity: " + 
            			uiEntity.getName(), e);
        }
        finally
        {
        }
    }
    
    /**
     * HTMLUIPage.forward
     * 
     * @param context
     * @throws JspException
     */
    public void forward(HTMLSnapshotContext context) throws JspException
    {//portlet not processed here
        try
        {
        	if (pageObject == null) {
        		logger.warn("Please invoke UIPageObject.forward");
        		return;
        	}
            String entityName = pageObject.getEntityName();
			if (logger.isDebugEnabled()) {
				logger.debug(
						"<---HTMLUIPage.forward--->start to access the uipage: {}",
						entityName);
			}
            String userLocale = LocaleContext.getUserLocale();
            ComponentOrientation ce = ComponentOrientation.getOrientation(
                    ResourceUtil.getLocaleObject(userLocale));
            context.setLeftToRight(ce.isLeftToRight());

			if (logger.isDebugEnabled()) {
				logger.debug("Call od to set data for the uipage: {}",
						entityName);
			}
            String frameName = context.getRequest().getParameter(WebflowConstants.FRAME_NAME);
            boolean frameMode = false;
            String superPrefix = null;
            String framePrefix = context.getRequest().getParameter("_framePrefix");
            String frameTarget = context.getRequest().getParameter("_frameTarget");
			if (frameTarget != null && !frameTarget.equals("null")) {
				superPrefix = frameTarget;
			} else if (framePrefix != null && !framePrefix.equals("null")
					&& framePrefix.length() != 0) {
				superPrefix = framePrefix;
				if (frameName != null) {
					superPrefix += '.' + frameName;
				}
			} else if (frameName != null) {
				superPrefix = frameName;
			}

            PageODProcessor pageODProcessor = new PageODProcessor(context, entityName);
            EvaluationContext evaContext = pageODProcessor.process();

            Map referenceEntityMap = new HashMap();
            referenceEntityMap.put(entityName, pageObject.getUi());
            pageObject.getUi().parseReferenceEntity(referenceEntityMap);
            context.setRefEntityMap(referenceEntityMap);

            String charset = Registry.getInstance().getEncoding();
            String actionPath = getActionPath(context);
            context.getRequest().setAttribute(WebflowConstants.FORM_URL, actionPath);
            
            String prefix = context.getHTMLPrefix();
            if ( prefix != null )
            {
                while ( prefix.indexOf(".") >= 0 )
                {
                    prefix = prefix.replace('.', '-');
                }
                context.setDIVPrefix(prefix);
            }
            else
            {
                context.setDIVPrefix("");
            }
            
            //prepare the static values for client javascript
            Calendar currentDate = Calendar.getInstance();
            Map constraintStyleMap = FormatUtil.getConstraintFormat(null,null);
            String language = HTMLUtil.getLanguageCode(ResourceUtil.getLocale(userLocale));
            DecimalFormatSymbols dfs = HTMLUtil.getLocaleFormatSymbol(ResourceUtil.getLocale(userLocale));

            context.generateHTML("<!DOCTYPE html>\n");
            context.generateHTML("<html>\n<head>\n<title>");
            context.generateHTML(entityName);
            //is the title need i18n? -- the name should not be i18n, but the <title> should be i18n
            //currently all uipages are embedded in frame, so the title can't be seen by user
            context.generateHTML("</title>\n");
            context.generateHTML("<script type=\"text/javascript\">\nvar defaultname;\nvar USER_CONSTRAINT_IMG=\"");
            context.generateHTML((String)constraintStyleMap.get("constraintSymbol"));
            context.generateHTML("\";\nvar USER_CONSTRAINT_LEFT=");
            context.generateHTML(((Boolean)constraintStyleMap.get("isLeft")).toString());
            context.generateHTML(";\nvar LANG=\"");
            context.generateHTML(language);
            context.generateHTML("\";\nvar CURRENCY_GROUP_SEPARATOR=\""+dfs.getGroupingSeparator()+"\"");
            context.generateHTML(";\nvar CURRENCY_MONETARY_SEPARATOR=\""+dfs.getMonetaryDecimalSeparator()+"\"");
            context.generateHTML(";\nvar CURTIME=");
            context.generateHTML(String.valueOf(currentDate.getTimeInMillis()));
            context.generateHTML(";\nvar TZOFFSET=");
            context.generateHTML(String.valueOf(currentDate.getTimeZone().getOffset(currentDate.getTimeInMillis())));
            context.generateHTML(";\nvar WEB_CONTEXTPATH=\"");
            context.generateHTML(WebConfig.getWebContextRoot());
            context.generateHTML("\";\nvar RESOURCE_CONTEXTPATH=\"");
            context.generateHTML(WebConfig.getResourceContextRoot());
            context.generateHTML("\";\nvar FRAMEWRAP=\"");
            context.generateHTML(WebConfig.replaceWebContext(WebConfig.getFrameWrap()));
            context.generateHTML("\";\nvar IS_SERVLETMODE=true;\nvar AJAX_SERVICE_URL=\"");
	        context.generateHTML(WebConfig.replaceWebContext(WebConfig.getAjaxServiceURI()));
	        context.generateHTML("\";\n</script>\n");
            context.generateHTML("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
            context.generateHTML(charset);
            context.generateHTML("\">\n");
            context.generateHTML("<meta http-equiv=\"x-ua-compatible\" content=\"ie=7\" />\n");
            if (logger.isDebugEnabled())
            {
                logger.debug("import css to the uipage: " + entityName);
            }
            context.generateHTML(WebConfig.replaceCssWebContext(pageObject.getPageCSS().toString()));
            if (logger.isDebugEnabled())
            {
                logger.debug("import js to the uipage: " + entityName);
            }
            importAllJS(context, 0);

            if (!frameMode)
            { 
                context.generateHTML("<script type=\"text/javascript\">\n");
                context.generateHTML("function initPage() \n{\n");
                context.generateHTML("    UIMaster.ui.mask.open();\n");
                context.generateHTML("    UIMaster.addResource(\"" + entityName + "\");\n");
                context.generateHTML("    getElementList();\n    defaultname = new ");
                context.generateHTML(entityName.replaceAll("\\.", "_"));
                context.generateHTML("(\"\");\n    defaultname.initPageJs();\n}\nfunction finalizePage() \n{\n    defaultname.finalizePageJs();\n    releaseMem();\n}\n</script>\n");
                context.generateHTML("</head>\n");
                context.generateHTML("<body onload=\"initPage()\" onunload=\"finalizePage()\">\n");
                context.generateHTML(genLoaderMask());
            }
            
            HTMLUtil.generateJSBundleConstants(context);
            context.generateHTML("<form action=\"" + actionPath + "\" method=\"post\" name=\"everything\" style=\"display:none;\" onsubmit=\"return false;\"");
            if (superPrefix != null)
            {
                context.generateHTML(" _framePrefix=\"" + superPrefix + "\"");
            }
            context.generateHTML(">\n");
            
            context.generateHTML("<input type=\"hidden\" name=\"_pagename\" value=\"");
            context.generateHTML(entityName);
            context.generateHTML("\"/>\n<input type=\"hidden\" name=\"_outname\" value=\"\"/>\n");
            context.generateHTML("<input type=\"hidden\" id=\"isLeftToRight\" name=\"isLeftToRight\" value=\"");
			if (context.isLeftToRight()) {
				context.generateHTML("true\"/>\n");
			} else {
				context.generateHTML("false\"/>\n");
			}

			// ajax handler.
            HttpSession session = context.getRequest().getSession();
            AjaxProcessor ajxProcessor = (AjaxProcessor) session.getAttribute("_ajaxProcessorInWebflow");
            if (ajxProcessor != null)
            {
                try
                {
                    session.removeAttribute("_ajaxProcessorInWebflow");
                    ajxProcessor.execute();
                }
                catch (AjaxException e)
                {
                    //Nothing to do.
                }
            }
            Map ajaxWidgetMap = AjaxActionHelper.getAjaxWidgetMap(session);
            if(ajaxWidgetMap == null)
            {
                ajaxWidgetMap = new HashMap();
                Map pageComponentMap = new HashMap();
                session.setAttribute(AjaxContext.AJAX_COMP_MAP, ajaxWidgetMap);
                if (superPrefix == null)
                {
                    ajaxWidgetMap.put(AjaxContext.GLOBAL_PAGE, pageComponentMap);
                }
                else
                {
                    ajaxWidgetMap.put(superPrefix, pageComponentMap);
                }
                context.setAjaxWidgetMap(pageComponentMap);
            }
            else
            {
                if (frameTarget == null || frameTarget.equals("null"))
                {
                    frameTarget = superPrefix;
                }

                if(frameTarget == null || frameTarget.equals("null"))
                {
                    if(logger.isDebugEnabled())
                        logger.debug("Remove all components in cache of ui map.");
                    ajaxWidgetMap.clear();
                    Map pageComponentMap = new HashMap();
                    ajaxWidgetMap.put("#GLOBAL#", pageComponentMap);
                    context.setAjaxWidgetMap(pageComponentMap);
                }
                else
                {
                    Iterator iterator = ajaxWidgetMap.entrySet().iterator();
                    while(iterator.hasNext())
                    {
                        Map.Entry entry = (Map.Entry)iterator.next();
                        String uiid = (String) entry.getKey();
                        if(uiid.startsWith(frameTarget + "."))
                        {
                            if(logger.isDebugEnabled())
                                logger.debug("Remove component["+uiid+"] in cache of ui map.");
                            iterator.remove();
                        }
                    }
                    Map pageComponentMap = new HashMap();
                    ajaxWidgetMap.put(frameTarget, pageComponentMap);
                    context.setAjaxWidgetMap(pageComponentMap);
                }
            }

            if (superPrefix != null)
            {
                context.getRequest().setAttribute("_framePagePrefix", superPrefix);
            }

            //
            PageDispatcher dispatcher = new PageDispatcher(pageObject.getUi(), evaContext);
            dispatcher.forward(context, 0);
            
            //initAjaxCalling(context, isServletMode, superPrefix);
            
            context.generateHTML("\n</form>");
            if (!frameMode)
            {
                context.generateHTML("\n</body>\n</html>\n");
            }
            else
            {
            }

            context.generateHTML("");
            // save real html page in session
            if (context.isAjaxSubmit())
            {   
                if (logger.isDebugEnabled()) {
                    logger.debug("Ajax Submit - The framePrefix is: " + framePrefix + 
                            " , The frameTarget is: " + frameTarget + " .");
                }
                
                String timeStamp = String.valueOf(System.currentTimeMillis());
                session.setAttribute(WebflowConstants.TEMP_RESPONSE_DATA, context.getHtmlString());
                session.setAttribute(WebflowConstants.TEMP_RESPONSE_KEY, timeStamp);
                // generate ajax response
                String ajaxResponse = AjaxActionHelper.generateSuccessfulJSONResponse(
                        context.getRequest().getParameter("_framePrefix"),
                        context.getRequest().getParameter("_frameTarget"),
                        timeStamp);
                try
                {
                    context.getOut().write(ajaxResponse);
                }
                catch(IOException e)
                {
                    logger.error("Fail to get a response writer", e);
                }
            }
            if (logger.isDebugEnabled()) {
            	debugEnableToSerializable(context.getAjaxWidgetMap());
            }
        }
        catch ( Exception e )
        {
            logger.warn("<---HTMLUIPage.forward--->Be interrupted when access uipage: " + pageObject.getEntityName());
            throw new JspException(e);
        }
    }
    
    private String getActionPath(HTMLSnapshotContext context) throws JspException
    {
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();;

        if ( logger.isDebugEnabled() )
        {
            logger.debug("Get actionPath from servletContext for the uipage: {}", pageObject.getEntityName());
        }
        String actionPath = WebConfig.replaceWebContext(WebConfig.getActionPath());
        if ( actionPath == null )
        {
            logger.error("****actionPath is not set!!!, please check the webflow engine servlet is initiated or parameter \"actionPath\" is set in web.xml!!!");
            throw new JspException("****actionPath is not set!!!, please check the webflow engine servlet is initiated or parameter \"actionPath\" is set in web.xml!!!");
        }

        StringBuffer results = new StringBuffer(actionPath);
        if ( logger.isDebugEnabled() )
        {
            logger.debug("Action path:" + actionPath);
        }

        boolean hasParams = ( actionPath.indexOf("?") != -1 );
        HttpSession session = request.getSession(false);
        if ( session != null )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("Append param timestamp to action path");
            }
            appendParam(results, "_timestamp", WebConfig.getTimeStamp(), hasParams);
            hasParams = true;
            session.setAttribute("_timestamp", new HashSet());
        }
        String tempStr = (String)request.getAttribute(WebflowConstants.SOURCE_CHUNK_NAME);
        if ( tempStr != null )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("Append param sourcechunkname to action path");
            }
            appendParam(results, WebflowConstants.SOURCE_CHUNK_NAME, tempStr, hasParams);
            hasParams = true;
        }
        tempStr = (String)request.getAttribute(WebflowConstants.SOURCE_NODE_NAME);
        if ( tempStr != null )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("Append param sourcenodename to action path");
            }
            appendParam(results, WebflowConstants.SOURCE_NODE_NAME, tempStr, hasParams);
            hasParams = true;
        }

        if( session.getAttribute(WebflowConstants.USER_SESSION_KEY) != null )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("Append param needCheckSessionTimeOut to action path");
            }
            appendParam(results, "_needCheckSessionTimeOut", "true", hasParams);
        }

        return response.encodeURL(results.toString());
    }
    
    private StringBuffer appendParam(StringBuffer sb, String name, String value, boolean hasParams)
    {
        if ( name == null )
        {
            return sb;
        }

        if (hasParams)
            sb.append("&");
        else
            sb.append("?");

        sb.append(name);
        sb.append("=");
        sb.append(value);

        return sb;
    }
    
    private void debugEnableToSerializable(Map ajaxComponentMap) 
    {
        logger.debug("Created ui ajax widgets(only data widgets).");
        Iterator iterator = ajaxComponentMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            Object value = entry.getValue();
            if(value instanceof Map)
            {
                Iterator iterator1 = ((Map)value).entrySet().iterator();
                while(iterator1.hasNext())
                {
                    Map.Entry compEntry = (Map.Entry)iterator1.next();
                    logger.debug("Ajax widget name: "+compEntry.getKey());
                    logger.debug("Ajax widget: "+compEntry.getValue());
                }
            }
            else
            {
                logger.debug("Ajax widget: "+((Widget)value).getId() + ":" + value);
            }
        }
    }
    

    private String genLoaderMask()
    {
        StringBuffer maskHtml = new StringBuffer();
        maskHtml.append("<div id=\"ui-mask-shadow\" class=\"ui-overlay\" style=\"display:block;\">\n");
        maskHtml.append("<div class=\"ui-widget-overlay\"></div>\n");
        maskHtml.append("<div id=\"ui-mask-content\" class=\"outer\" style=\"display:block;border:4px solid #8DB9DB;\">\n");
        maskHtml.append("<div class=\"inner\"><p class=\"ui-info-msg\"><span></span>");
        maskHtml.append("Processing...Please wait</p></div>\n");
        maskHtml.append("</div>\n");
        maskHtml.append("</div>\n");
        
        return maskHtml.toString();
    }

    public void setReconfigurablePropertyValue(HTMLSnapshotContext context, Map tempMap)
    {
        if (!uiEntity.getReconfigurablePropMap().isEmpty())
        {
        	if (logger.isDebugEnabled())
            {
                logger.debug("set the real value for reconfigurable property of components' in the uientity: {}", 
                        uiEntity.getName());
            }
            Iterator iterator = uiEntity.getReconfigurablePropMap().keySet().iterator();
            while (iterator.hasNext())
            {
                String compName = (String)iterator.next();
                Map tMap;
                if (tempMap.containsKey(compName))
                {
                    tMap = (Map)tempMap.get(compName);
                }
                else
                {
                    tMap = new HashMap();
                }
                List propList = (List)uiEntity.getReconfigurablePropMap().get(compName);
                for (int i = 0, n = propList.size(); i < n; i++)
                {
                    ReconfigurablePropertyType prop = (ReconfigurablePropertyType)propList.get(i);
                    String propertyName = prop.getPropertyName();
                    String newPropertyName = prop.getNewPropertyName();
                    Object reconfigValue = context.getReconfigProperty(newPropertyName);
                    if (reconfigValue != null)
                    {
                        tMap.put(propertyName, reconfigValue);
                    }
                }
                if (!tMap.isEmpty())
                {
                    tempMap.put(compName, tMap);
                }
            }
        }
    }
	
    private void importAllJS(HTMLSnapshotContext context, int depth) throws JspException
    {
        Map refEntityMap = context.getRefEntityMap();
        UIFormObject tEntityObj = (UIFormObject)refEntityMap.remove(pageObject.getEntityName());
        importSelfJS(tEntityObj, context, depth);
        Iterator entityIterator = refEntityMap.values().iterator();
        while ( entityIterator.hasNext() )
        {
            UIFormObject entityObj = (UIFormObject)entityIterator.next();
            importSelfJS(entityObj, context, depth);
        }
        refEntityMap.put(pageObject.getEntityName(), tEntityObj);
    }
    
    public void importSelfJS(UIFormObject tEntityObj, HTMLSnapshotContext context, int depth) throws JspException
    {
        Iterator jsFileNameIterator = tEntityObj.getJsIncludeList().iterator();
        while (jsFileNameIterator.hasNext())
        {
            String jsFileName = (String)jsFileNameIterator.next();
            if (context.containsJsName(jsFileName))
            {
                continue;
            }
            else
            {
                context.addJsName(jsFileName);
            }

            if (jsFileName.endsWith(".js"))
            {
                long timestamp = 1;
                if (timestamp >= 0)
                {
                    String importJSCode = (String)tEntityObj.getJsIncludeMap().get(jsFileName);
                    importJSCode = WebConfig.replaceJsWebContext(importJSCode);
                    context.generateJS(importJSCode + timestamp + "\"></script>");
                    HTMLUtil.generateTab(context, depth);
                }
            }
        }
    }

    public String importSelfJs(HTMLSnapshotContext context, UIFormObject tEntityObj)
    {
        StringBuffer sb = new StringBuffer();
        Iterator jsFileNameIterator = tEntityObj.getJsIncludeList().iterator();
        while (jsFileNameIterator.hasNext())
        {
            String jsFileName = (String)jsFileNameIterator.next();
            if (!context.containsJsName(jsFileName))
            {
                context.addJsName(jsFileName);
            }
            if (jsFileName.endsWith(".js"))
            {
                sb.append("UIMaster.require(\"").append(HTMLUtil.getWebRoot());
                sb.append(jsFileName).append("?_timestamp=").append(WebConfig.getTimeStamp())
                        .append("\",true);");
            }
        }
        return sb.toString();
    }

}
