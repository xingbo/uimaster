package org.shaolin.uimaster.page;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.exception.UIComponentNotFoundException;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.security.ComponentPermission;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object only created when a request received from the client. 
 * 
 * @author wushaol
 *
 */
public class HTMLSnapshotContext implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final String REQUEST_HTML_GENERATION_CONTEXT_KEY = "REQUEST_HTML_GENERATION_CONTEXT_KEY";
    public static final String REQUEST_LAYOUT_CURRENT_ROW_KEY = "REQUEST_LAYOUT_CURRENT_ROW_KEY";
    public static final String REQUEST_PARENT_TAG_KEY = "REQUEST_PARENT_TAG_KEY";
  
    public static final String REQUEST_LAYOUT_START = "-1";
    public static final String REQUEST_LAYOUT_END = "infinity";
    
    public static final String REQUEST_OWNER_READONLY = "REQUEST_OWNER_READONLY";
    public static final String REQUEST_PARENT_READONLY = "REQUEST_PARENT_READONLY";
	
    private static final Logger logger = LoggerFactory.getLogger(HTMLSnapshotContext.class);

    private static final String UIENTITY_NAME_KEY = "UIENTITY_NAME_KEY";

    private static final String ODMAPPER_NAME_KEY = "ODMAPPER_NAME_KEY";

    private static final String ODMAPPER_DATA_KEY = "ODMAPPER_DATA_KEY";

    private static final String ISDATATOUI_KEY = "ISDATATOUI_KEY";

    private static final String HTML_PREFIX_KEY = "HTML_PREFIX_KEY";

    private static final String HTML_ATTRIBUTE_MAP_KEY = "HTML_ATTRIBUTE_MAP_KEY";

    private static final String RECONFIGURATION_FUNCTION_KEY = "RECONFIGURATION_FUNCTION_KEY";

    private static final String RECONFIGURATION_VARIABLE_KEY = "RECONFIGURATION_VARIABLE_KEY";

    private static final String RECONFIGURATION_PROPERTY_KEY = "RECONFIGURATION_PROPERTY_KEY";

    private static final String JS_NAME_SET_KEY = "JS_NAME_SET_KEY";

    private static final String DIV_PREFIX_KEY = "DIV_PREFIX_KEY";
    
    private static boolean isNullWriter = false;

    private transient HttpServletRequest request;

    private transient HttpServletResponse response;

    private transient Writer out;

    private Map repository;

    private Map refEntityMap;

    private StringBuffer htmlBuffer;

    private ArrayList pageJs;

    private ArrayList pageCSS;

    private boolean jsonStyle;

    private boolean ajaxSubmit;

    private Map pageData;

    private Map ajaxWidgetMap;
    
    private Map componentPermissions;

    private boolean noResponse = false;

    private boolean isLeftToRight = true;

    private int valueMaskCounter;
    
    private PageWidgetsContext pageWidgetsContext;

    public HTMLSnapshotContext(HttpServletRequest request, HttpServletResponse response) 
    {
        this.request = request;
        this.response = response;
        this.repository = (Map)request
                .getAttribute(REQUEST_HTML_GENERATION_CONTEXT_KEY);
        if (this.repository == null)
        {
            resetRepository();
        }
        try {
			this.out = response.getWriter();
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		}
        this.ajaxSubmit = checkIfNeedAjaxSubmit(this.request);
    }

    public HTMLSnapshotContext(HttpServletRequest request)
    {
        this.request = request;
        this.repository = (Map)request
                .getAttribute(REQUEST_HTML_GENERATION_CONTEXT_KEY);
        if (this.repository == null)
        {
            resetRepository();
        }
        noResponse = true;
        this.ajaxSubmit = checkIfNeedAjaxSubmit(this.request);
    }
    
	public HTMLSnapshotContext(HttpServletRequest request, Writer writer) {
		this.request = request;
		this.repository = ((Map) request
				.getAttribute("REQUEST_HTML_GENERATION_CONTEXT_KEY"));

		if (this.repository == null) {
			resetRepository();
		}
		this.out = writer;
	}

    private boolean checkIfNeedAjaxSubmit(HttpServletRequest request)
    {
        String ajaxSubmitFlag = request.getParameter(WebflowConstants.AJAX_SUBMIT_FLAG);

        if (ajaxSubmitFlag != null && ajaxSubmitFlag.equalsIgnoreCase("true"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setAjaxWidgetMap(Map ajaxComponentMap)
    {
        this.ajaxWidgetMap = ajaxComponentMap;
    }

    public Map getAjaxWidgetMap()
    {
        return ajaxWidgetMap;
    }

    public void addAjaxWidget(String compID, Widget component)
    {
        ajaxWidgetMap.put(compID, component);
    }
    
    public void resetRepository()
    {
        this.repository = new HashMap();
        request.setAttribute(REQUEST_HTML_GENERATION_CONTEXT_KEY, this.repository);
    }

    public String getFrameInfo()
    {
        String frameInfo = (String)request.getAttribute("_framePagePrefix");
        if (frameInfo == null)
        {
            String framePrefix = (String)request.getParameter("_framePrefix");
            if (framePrefix != null && !framePrefix.equals("null"))
            {
                frameInfo = framePrefix;
            }
        }
        return frameInfo == null ? "" : frameInfo;
    }

    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    public HttpServletResponse getResponse()
    {
        return response;
    }

    public Writer getOut()
    {
        return out;
    }

    public void setFormName(String name)
    {
        repository.put(UIENTITY_NAME_KEY, name);
        repository.put(ODMAPPER_NAME_KEY, name);
    }

    public void setIsDataToUI(boolean isDataToUI)
    {
        repository.put(ISDATATOUI_KEY, Boolean.valueOf(isDataToUI));
    }

    public void setHTMLPrefix(String prefix)
    {
        repository.put(HTML_PREFIX_KEY, prefix);
    }

    public void setDIVPrefix(String prefix)
    {
        repository.put(DIV_PREFIX_KEY, prefix);
    }

    public void setReconfigFunction(Map reconfigurationMap)
    {
        String key = getHTMLPrefix() + RECONFIGURATION_FUNCTION_KEY;
        repository.put(key, reconfigurationMap);
    }

    public void setReconfigVariable(Map reconfigurationMap)
    {
        String key = getHTMLPrefix() + RECONFIGURATION_VARIABLE_KEY;
        repository.put(key, reconfigurationMap);
    }

    public void addReconfigVariable(String nameKey, Object value)
    {
        String key = getHTMLPrefix() + RECONFIGURATION_VARIABLE_KEY;
        Map reconfigurationMap = (Map)repository.get(key);
        if (reconfigurationMap == null)
        {
            reconfigurationMap = new HashMap();
            repository.put(key, reconfigurationMap);
        }
        reconfigurationMap.put(nameKey, value);
    }

    public void setReconfigProperty(Map reconfigurationMap)
    {
        String key = getHTMLPrefix() + RECONFIGURATION_PROPERTY_KEY;
        repository.put(key, reconfigurationMap);
    }

    public void addHTMLAttribute(String htmlName, String attributeName, Object attributeValue)
    {
        if (logger.isDebugEnabled())
            logger.debug("htmlName: {} attributeName: {} attributeValue: {}", 
                    new Object[] {htmlName, attributeName, attributeValue});

        Map htmlAttributeMap = (Map)repository.get(HTML_ATTRIBUTE_MAP_KEY);
        if (htmlAttributeMap == null)
        {
            htmlAttributeMap = new HashMap();
            repository.put(HTML_ATTRIBUTE_MAP_KEY, htmlAttributeMap);
        }

        Map attributeMap = (Map)htmlAttributeMap.get(htmlName);
        if (attributeMap == null)
        {
            attributeMap = new HashMap();
            htmlAttributeMap.put(htmlName, attributeMap);
        }

        attributeMap.put(attributeName, attributeValue);
    }

    public String getFormName()
    {
        return (String)repository.get(UIENTITY_NAME_KEY);
    }

    public String getODMapperName()
    {
        return (String)repository.get(ODMAPPER_NAME_KEY);
    }

    public void setODMapperData(Map data)
    {
        repository.put(ODMAPPER_DATA_KEY, data);
    }
    
    public Map<String, Object> getODMapperData()
    {
        return (Map)repository.get(ODMAPPER_DATA_KEY);
    }
    
    public void setODMapperContext(String formId, EvaluationContext context)
    {
    	if (!repository.containsKey("ODMAPPER_EVA_CONTEXT")) {
    		repository.put("ODMAPPER_EVA_CONTEXT", new HashMap<String, EvaluationContext>());
    	}
    	((HashMap)repository.get("ODMAPPER_EVA_CONTEXT")).put(formId, context);
    }
    
    /**
     * the context value is only available in once.
     * 
     * @param formId
     * @return
     */
    public EvaluationContext getODMapperContext(String formId)
    {
    	if (repository.containsKey("ODMAPPER_EVA_CONTEXT")) {
    		return (EvaluationContext)((HashMap)repository.get("ODMAPPER_EVA_CONTEXT")).remove(formId);
    	}
        return null;
    }

    public boolean getIsDataToUI()
    {
        return ((Boolean)repository.get(ISDATATOUI_KEY)).booleanValue();
    }

    public String getHTMLPrefix()
    {
        return (String)repository.get(HTML_PREFIX_KEY);
    }

    public String getDIVPrefix()
    {
        return (String)repository.get(DIV_PREFIX_KEY);
    }

    public String getReconfigFunction(String name)
    {
        return getReconfigFunction(getHTMLPrefix(), name);
    }

    public String getReconfigFunction(String prefix, String name)
    {
        String key = prefix + RECONFIGURATION_FUNCTION_KEY;
        Map reconfigurationMap = (Map)repository.get(key);
        return reconfigurationMap == null ? null : (String)reconfigurationMap.get(name);
    }

    public Object getReconfigVariable(String name)
    {
        return getReconfigVariable(getHTMLPrefix(), name);
    }

    public Object getReconfigVariable(String prefix, String name)
    {
        String key = prefix + RECONFIGURATION_VARIABLE_KEY;
        Map reconfigurationMap = (Map)repository.get(key);
        return reconfigurationMap == null ? null : reconfigurationMap.get(name);
    }

    public Object getReconfigProperty(String name)
    {
        return getReconfigProperty(getHTMLPrefix(), name);
    }

    public Object getReconfigProperty(String prefix, String name)
    {
        String key = prefix + RECONFIGURATION_PROPERTY_KEY;
        Map reconfigurationMap = (Map)repository.get(key);
        return reconfigurationMap == null ? null : reconfigurationMap.get(name);
    }

    public Map getHTMLAttributeMap(String htmlName)
    {
        Map htmlAttributeMap = (Map)repository.get(HTML_ATTRIBUTE_MAP_KEY);
        if (htmlAttributeMap == null)
        {
            return null;
        }
        return (Map)htmlAttributeMap.get(htmlName);
    }

    public void printHTMLAttributeValues()
    {
        try
        {
            if (logger.isTraceEnabled())
            {
                StringBuffer sb = new StringBuffer();
                
                Iterator<String> i = repository.keySet().iterator();
                while(i.hasNext()) {
                	String key = i.next();
                	Object object = repository.get(key);
                	sb.append("\n\nPrint all ").append(key).append("'s attributes.\n");
                	if (object instanceof String) {
                		sb.append("String pair: ").append(key).append("=").append(object);
                	} if (object instanceof Boolean) {
                		sb.append("Boolean pair: ").append(key).append("=").append(object);
                	} else if (object instanceof Map) {
		                Map htmlAttributeMap = (Map)object;
		                if (htmlAttributeMap != null && !htmlAttributeMap.isEmpty())
		                {
		                    int count = 1;
		                    Iterator it = htmlAttributeMap.entrySet().iterator();
		                    while (it.hasNext())
		                    {
		                        Map.Entry entry = (Map.Entry)it.next();
		                        String key1 = (String)entry.getKey();
		                        sb.append("the ");
		                        sb.append(count++);
		                        sb.append(" attribute name: ");
		                        sb.append(key1);
		                        Object value = entry.getValue();
		                        sb.append(", value: ");
		                        sb.append(value);
		                        sb.append("\n");
		                    }
		                }
                	}
                }
                
                logger.trace(sb.toString());
                this.pageWidgetsContext.printAllComponents();
            }
        }
        catch (Exception e)
        {

        }
    }

    public Object getHTMLAttribute(String htmlName, String attributeName)
    {
        Map htmlAttributeMap = (Map)repository.get(HTML_ATTRIBUTE_MAP_KEY);
        if (htmlAttributeMap == null)
        {
            return null;
        }

        Map attributeMap = (Map)htmlAttributeMap.get(htmlName);
        if (attributeMap == null)
        {
            return null;
        }
        return attributeMap.get(attributeName);
    }

    public void addJsName(String jsName)
    {
        Set jsNameSet = (Set)repository.get(JS_NAME_SET_KEY);
        if (jsNameSet == null)
        {
            jsNameSet = new HashSet();
            repository.put(JS_NAME_SET_KEY, jsNameSet);
        }
        jsNameSet.add(jsName);
    }

    public boolean containsJsName(String jsName)
    {
        Set jsNameSet = (Set)repository.get(JS_NAME_SET_KEY);
        return jsNameSet == null ? false : jsNameSet.contains(jsName);
    }

    public String getImageUrl(String entityName, String src)
    {
        StringBuffer sb = new StringBuffer();
        String imgRoot = WebConfig.getResourceContextRoot() + "/images";
        if (!WebConfig.getResourceContextRoot().equals(imgRoot))
        {
            sb.append(imgRoot);
        }  
        if(src == null) src = "";
        if (!src.startsWith("/"))
        {
            sb.append("/");
        }
        sb.append(src);
        return sb.toString();
    }

    public void generateJS(String value)
    {
        if (jsonStyle)
        {
            if (pageJs == null)
            {
                pageJs = new ArrayList();
            }
            pageJs.add(value);
        }
        else
        {
            generateHTML(value);
        }
    }

    public void generateCSS(String value)
    {
        if (jsonStyle)
        {
            if (pageCSS == null)
            {
                pageCSS = new ArrayList();
            }
            pageCSS.add(value);
        }
        else
        {
            generateHTML(value);
        }
    }

    public void generateData(String key, Object value)
    {
        if (jsonStyle)
        {
            if (pageData == null)
                pageData = new HashMap();
            if (pageData.containsKey(key))
            {
                Object oldValue = pageData.get(key);
                ArrayList valueList = new ArrayList();
                valueList.add(oldValue);
                valueList.add(value);
                pageData.put(key, valueList);
            }
            else
            {
                pageData.put(key, value);
            }
        }
    }

    public void appendHtmlBuffer(String value)
    {
        if (htmlBuffer == null)
        {
            htmlBuffer = new StringBuffer(4096);
        }
        htmlBuffer.append(value);
    }

    public void generateHTML(String value)
    {
        if (jsonStyle)
        {
            appendHtmlBuffer(value);
        }
        else if (!noResponse)
        {
            if (ajaxSubmit)
            {
                // save out content in buffer
                if(!isNullWriter)
                {
                	appendHtmlBuffer(value);
                }
            }
            else
            {
            	// write out to client directly
            	try
            	{
            		if (value == null) {
            			value = "";
            		}
            		out.write(value);
            		out.flush();
            	}
            	catch (IOException e)
            	{
            		CloseUtil.close(out);
            	}
            }
        }
    }

    public String getHTMLString()
    {
        return htmlBuffer.toString();
    }
    
    public String getJSString()
    {
    	if (pageJs != null) {
        	JSONObject obj = new JSONObject(pageJs);
        	return obj.toString();
        }
        return "";
    }

    public void setJSONStyle(boolean flag)
    {
        jsonStyle = flag;
    }

    public boolean isJSONStyle()
    {
        return jsonStyle;
    }

    public void setAjaxSubmit(boolean flag)
    {
        ajaxSubmit = flag;
    }

    public boolean isAjaxSubmit()
    {
        return ajaxSubmit;
    }

    public String getHtmlString()
    {
        return htmlBuffer.toString();
    }

    public StringBuffer getHtmlBuffer()
    {
        return htmlBuffer;
    }

    public static boolean isInstance(String type, Object o)
    {
		if (type == null || o == null) {
			return false;
		}

		if (o instanceof HTMLReferenceEntityType) {
			if (type.equals(((HTMLReferenceEntityType) o).getType())) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
    }

    public static String encode(boolean b)
    {
        return encode(String.valueOf(b));
    }

    public static String encode(char c)
    {
        return encode(String.valueOf(c));
    }

    public static String encode(double d)
    {
        return encode(String.valueOf(d));
    }

    public static String encode(float f)
    {
        return encode(String.valueOf(f));
    }

    public static String encode(int i)
    {
        return encode(String.valueOf(i));
    }

    public static String encode(long l)
    {
        return encode(String.valueOf(l));
    }

    public static String encode(Object o)
    {
        return encode(String.valueOf(o));
    }
    
	public static String encode(String s) {
		if (s == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0, n = s.length(); i < n; i++) {
			char c = s.charAt(i);
			if (c == '%') {
				sb.append("%25");
			} else if (c == '&') {
				sb.append("%26");
			} else if (c == '=') {
				sb.append("%3D");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

    public static String decode(String s)
    {
        if (s == null)
        {
            return null;
        }
        try
        {
            s = URLDecoder.decode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Error occur when encode " + s, e);
        }
        return s;
    }

    public static Map parseAttribute(String attributeString)
    {
        StringTokenizer st = new StringTokenizer(attributeString, "&");
        Map rtMap = new HashMap();
        while (st.hasMoreTokens())
        {
            String attribute = st.nextToken();
            int beginIndex = attribute.indexOf("=");
            if (beginIndex > 0)
            {
                String name = attribute.substring(0, beginIndex);
                String value = attribute.substring(beginIndex + 1);
                int beginIndex2 = name.indexOf("[");
                if (beginIndex2 > 0)
                {
                    name = name.substring(0, beginIndex2);
                    List valueList = (List)rtMap.get(name);
                    if (valueList == null)
                    {
                        valueList = new ArrayList();
                        rtMap.put(name, valueList);
                    }
                    valueList.add(decode(value));
                }
                else
                {
                    rtMap.put(name, decode(value));
                }
            }
        }
        return rtMap;
    }

    public void setRefEntityMap(Map refEntityMap)
    {
        this.refEntityMap = refEntityMap;
    }

    public Map getRefEntityMap()
    {
        return refEntityMap;
    }

    public void addRefEntity(String entityName, Object entityObj)
    {
        refEntityMap.put(entityName, entityObj);
    }

    public Object getRefEntity(String entityName)
    {
        Object entityObj = refEntityMap.get(entityName);
        if (entityObj == null)
        {
            entityObj = HTMLUtil.parseUIEntity(entityName);
            addRefEntity(entityName, entityObj);
        }
        return entityObj;
    }

    public void setLeftToRight(boolean isLeftToRight)
    {
        this.isLeftToRight = isLeftToRight;
    }

    public boolean isLeftToRight()
    {
        return isLeftToRight;
    }

    public void enterValueMask()
    {
        valueMaskCounter++;
    }

    public void leaveValueMask()
    {
        valueMaskCounter--;
    }

    public boolean isValueMask()
    {
        return valueMaskCounter > 0;
    }

    /**
     * The security controls configured on outside layer entity that covers the one configured on inside layer
     * If the process sequence is from outside to inside, putIfAbsent will be true
     * If the process sequence is from inside to outside(for ajax component), putIfAbsent will be false
     * 
     * @param compId         the widget id the security controls is configured on, stands for the full widget id such as uientity2.uientity1.button
     * @param viewPermission 
     * @param editPermission
     * @param putIfAbsent    
     * 
     */
    public void putCompPermission(String compId, String[] viewPermission, String[] editPermission,
            boolean putIfAbsent)
    {
		if (componentPermissions == null) {
			componentPermissions = new HashMap<String, ComponentPermission>();
		}

		ComponentPermission cp = (ComponentPermission) componentPermissions
				.get(compId);

		if (cp == null) {
			cp = new ComponentPermission(viewPermission, editPermission);
			componentPermissions.put(compId, cp);
		} else {
			if (putIfAbsent) {
				if (cp.getViewPermission().length == 0) {
					cp.setViewPermission(viewPermission);
				}
				if (cp.getEditPermission().length == 0) {
					cp.setEditPermission(editPermission);
				}
			} else {
				cp.setViewPermission(viewPermission);
				cp.setEditPermission(editPermission);
			}
		}
    }

    public ComponentPermission getCompPermission(String compId)
    {
        if (componentPermissions != null)
        {
            return (ComponentPermission)componentPermissions.get(compId);
        }
        else
        {
            return null;
        }
    }
    
    public HTMLWidgetType getHtmlWidget(String uiid) 
    		throws UIComponentNotFoundException {
		return pageWidgetsContext.getComponent(uiid);
	}

	public void setPageWidgetContext(PageWidgetsContext pageComponentContext) {
		this.pageWidgetsContext = pageComponentContext;
	}

	public PageWidgetsContext getPageWidgetContext() {
		return this.pageWidgetsContext ;
	}
}
