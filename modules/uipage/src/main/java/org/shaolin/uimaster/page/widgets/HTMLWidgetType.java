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
package org.shaolin.uimaster.page.widgets;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.ValidatorPropertyType;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HTMLWidgetType implements Serializable
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLWidgetType.class);
    private static final long serialVersionUID = -6119707922874957783L;
    
    private String id;
    private String prefix;
    private String name;
    private String frameInfo;
    private Boolean readOnly;
    private Map<String, Object> attributeMap;
    private Map<String, String> styleMap;
    private Map eventListenerMap;
    private HTMLLayoutType htmlLayout;
    protected HTMLSnapshotContext context;

    public HTMLWidgetType()
    {
    }

    public HTMLWidgetType(HTMLSnapshotContext context)
    {
        this.setContext(context);
        this.setFrameInfo(context.getFrameInfo());
    }

    public HTMLWidgetType(HTMLSnapshotContext context, String id)
    {
        this.setContext(context);
        this.setId(id);
        this.setFrameInfo(context.getFrameInfo());
    }

    public void reset()
    {
        this.id = null;
        this.prefix = null;
        this.name = null;
        this.readOnly = null;
        this.attributeMap = null;
        this.styleMap = null;
        this.eventListenerMap = null;
        this.context = null;
        this.htmlLayout = null;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void setReadOnly(Boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public String getId()
    {
        return id;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public Boolean getReadOnly()
    {
        return readOnly;
    }

    public String getName()
    {
        return getName(true);
    }
    
    public String getName(boolean withSuffix)
    {
        if (id == null)
        {
            return null;
        }
        if (name != null)
        {
            return name;
        }
        StringBuffer nameBuffer = new StringBuffer();
        if (prefix != null && prefix.length() > 0)
        {
            nameBuffer.append(prefix);
        }
        nameBuffer.append(id);
        name = new String(nameBuffer);
        return name;
    }

    public String getFrameInfo() {
        return frameInfo;
    }

    public void setFrameInfo(String frameInfo) {
        this.frameInfo = frameInfo;
    }

    public String getUIEntityName()
    {
        return context.getFormName();
    }

    public boolean getIsDataToUI()
    {
        return context.getIsDataToUI();
    }

    public void addAttribute(Map attributeMap)
    {
        if (attributeMap == null)
        {
            return;
        }
        if (this.attributeMap == null)
        {
            this.attributeMap = new HashMap();
        }
        this.attributeMap.putAll(attributeMap);
        this.attributeMap.remove("readOnly");
    }

    public void addAttribute(String name, Object value)
    {
        if (attributeMap == null)
        {
            attributeMap = new HashMap<String, Object>();
        }

        int endIndex = name.indexOf("[");
        if (endIndex > -1)
        {
            name = name.substring(0, endIndex);
            Object valueList = attributeMap.get(name);
            if (valueList != null)
            {
                if (valueList instanceof List)
                {
                    ((List) valueList).add(value);
                }
                else
                {
                    logger.error(getName() + "'s " + name + " is not list attribute");
                }
            }
            else
            {
                valueList = new ArrayList();
                ((List) valueList).add(value);
                attributeMap.put(name, valueList);
            }
        }
        else
        {
            attributeMap.put(name, value);
        }
    }

    public Object getAttribute(String name)
    {
        return attributeMap == null ? null : attributeMap.get(name);
    }

    public Map getAttributeMap()
    {
        return attributeMap;
    }

    public Object removeAttribute(String name)
    {
        return attributeMap == null ? null : attributeMap.remove(name);
    }

    public boolean containsAttribute(String name)
    {
        return attributeMap == null ? false : attributeMap.containsKey(name);
    }

    public void setHTMLAttribute(String name, Object value)
    {
        context.addHTMLAttribute(getName(), name, value);
    }

    public Object getHTMLAttribute(String name)
    {
        return context.getHTMLAttribute(getName(), name);
    }

    public Object getAllAttribute(String name)
    {
        Object o =  getHTMLAttribute(name);
        return o == null ? getAttribute(name) : o;
    }

    /**
     * @deprecated
     * @return
     */
    public String getHTMLValue()
    {
        if(logger.isInfoEnabled())
            logger.info("**************************getHTMLValue: "+getName());
        return context.getRequest().getParameter(getName());
    }

    /**
     * @deprecated
     * @return
     */
    public String[] getHTMLValues()
    {
        if(logger.isInfoEnabled())
            logger.info("**************************getHTMLValues: "+getName());
        return context.getRequest().getParameterValues(getName());
    }

    public void addStyle(String name, String value)
    {
        if (styleMap == null)
        {
            styleMap = new HashMap();
        }
        styleMap.put(name, value);
    }

    public String getStyle(String name)
    {
        return styleMap == null ? null : (String) styleMap.get(name);
    }

    public void addEventListener(Map eventMap)
    {
        if (eventMap == null)
        {
            return;
        }
        if (eventListenerMap == null)
        {
            eventListenerMap = new HashMap();
        }
        eventListenerMap.putAll(eventMap);
    }

    public void addEventListener(String event, String handler)
    {
        if (eventListenerMap == null)
        {
            eventListenerMap = new HashMap();
        }
        eventListenerMap.put(event, handler);
    }

    public String removeEventListenter(String event)
    {
        return eventListenerMap == null ? null : (String) eventListenerMap.remove(event);
    }
    
    public void setAJAXAttributes(Widget widget)
    {
        Map odAttributeMap = context.getHTMLAttributeMap(getName());
        if (odAttributeMap != null && !odAttributeMap.isEmpty())
        {
            for (Iterator it = odAttributeMap.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry)it.next();
                String attributeName = (String)entry.getKey();;
                Object attributeValue = entry.getValue();
                setAJAXAttribute(attributeName, attributeValue, widget);
            }
        }

        if (attributeMap != null && !attributeMap.isEmpty())
        {
            if (odAttributeMap == null || !odAttributeMap.containsKey("viewPermission"))
            {
            	Object attributeValue = attributeMap.get("viewPermission");
                setAJAXAttribute("viewPermission", attributeValue, widget);
            }
            if (odAttributeMap == null || !odAttributeMap.containsKey("editPermission"))
            {
            	Object attributeValue = attributeMap.get("editPermission");
                setAJAXAttribute("editPermission", attributeValue, widget);
            }
            if (odAttributeMap == null || !odAttributeMap.containsKey("visible"))
            {
            	Object attributeValue = attributeMap.get("visible");
                setAJAXAttribute("visible", attributeValue, widget);
            }
            if (odAttributeMap == null || !odAttributeMap.containsKey("editable"))
            {
            	Object attributeValue = attributeMap.get("editable");
                setAJAXAttribute("editable", attributeValue, widget);
            }
        }
    }

    public void setAJAXAttribute(String attributeName, Object attributeValue, Widget widget)
    {
        if ("viewPermission".equals(attributeName))
        {
            String[] viewPermissions = convertToStringArray(attributeValue);
            if ((viewPermissions != null) && (viewPermissions.length > 0))
            {
                widget.setViewPermissions(viewPermissions);
            }
            return;
        }
        else if ("editPermission".equals(attributeName))
        {
            String[] editPermissions = convertToStringArray(attributeValue);
            if ((editPermissions != null) && (editPermissions.length > 0))
            {
                widget.setEditPermissions(editPermissions);
            }
            return;
        }
        else if ("visible".equals(attributeName))
        {
        	String attrValue = String.valueOf(attributeValue);
            if ("false".equals(attrValue))
            {
                widget.setVisible(false);
            }
            return;
        }
        else if ("editable".equals(attributeName))
        {
        	String attrValue = String.valueOf(attributeValue);
            if ("false".equals(attrValue))
            {
                widget.addAttribute("disabled", "true");
            }
            return;
        }
    }

    protected void setAJAXConstraints(Widget widget)
    {
		if (attributeMap == null) {
			attributeMap = new HashMap<String, Object>();
			return;
		}
		
        Object init = attributeMap.get("initValidation");
        if (init != null)
        {
            widget.addConstraint("initValidation", init, null);
        }
        Object value = attributeMap.get("validator");
        if (value != null)
        {
            ValidatorPropertyType validator = (ValidatorPropertyType)value;
            String message = validator.getErrMsg();
            if (message == null)
            {
                if (validator.getI18NMsg() != null)
                {
                    message = ResourceUtil.getResource(LocaleContext.getUserLocale(),
                            validator.getI18NMsg().getBundle(), 
                            validator.getI18NMsg().getKey());
                }
            }
            List<StringPropertyType> params = validator.getParams();
            String[] parameters = new String[params.size()];
            for (int i = 0; i < params.size(); i++)
            {
                parameters[i] = params.get(i).getValue();
            }
            widget.addCustomValidator(validator.getFuncCode(), parameters, message);
        }
        
        if (attributeMap.containsKey("allowBlank")) {
	        widget.addConstraint("allowBlank", 
	        		attributeMap.get("allowBlank"), 
	        		(String)attributeMap.get("allowBlankText"));
        }
		if (this instanceof HTMLTextWidgetType) {
			widget.addConstraint("minLength", 
					attributeMap.get("minLength"), 
					(String)attributeMap.get("lengthText"));
			widget.addConstraint("regex", 
					attributeMap.get("regex"), 
					(String)attributeMap.get("regexText"));
			if (this instanceof HTMLTextAreaType) {
				widget.addConstraint("maxLength", 
						attributeMap.get("maxLength"), 
						(String)attributeMap.get("lengthText"));
			}
		} else if (this instanceof HTMLChoiceType) {
			if (this instanceof HTMLSingleChoiceType) {
				widget.addConstraint("selectedValueConstraint", 
						attributeMap.get("selectedValueConstraint"), 
						(String)attributeMap.get("selectedValueConstraintText"));
			} else if (this instanceof HTMLMultiChoiceType) {
				widget.addConstraint("selectedValuesConstraint", 
						(Object[])attributeMap.get("selectedValuesConstraint"), 
						(String)attributeMap.get("selectedValuesConstraintText"));
			}
		} else if (this instanceof HTMLSelectComponentType) {
			if (this instanceof HTMLCheckBoxType) {
				widget.addConstraint("mustCheck", 
						attributeMap.get("mustCheck"), 
						(String)attributeMap.get("mustCheckText"));
			}
		}
    }
    
    public String getReconfigurateFunction(String handler)
    {
        String functionPrefix = prefix;
        String reconfiguration = context.getReconfigFunction(functionPrefix, handler);
        while (reconfiguration != null)
        {
            if (functionPrefix.endsWith("."))
            {
                functionPrefix = functionPrefix.substring(0, functionPrefix.length() - 1);
            }
            int endIndex = functionPrefix.lastIndexOf(".");
            if (endIndex == -1)
            {
                functionPrefix = "";
            }
            else
            {
                functionPrefix = functionPrefix.substring(0 ,endIndex + 1);
            }
            handler = reconfiguration;
            reconfiguration = context.getReconfigFunction(functionPrefix, handler);
        }

        String functionName = "defaultname." + functionPrefix + handler;
        String parameter = "defaultname." + getName() + ",event";
        return "javascript:" + functionName + "(" + parameter + ")";
    }

    public String getEventListener(String name)
    {
        return eventListenerMap == null ? null : (String) eventListenerMap.get(name);
    }

    public HTMLLayoutType getHTMLLayout()
    {
        return htmlLayout;
    }

    public void setHTMLLayout(HTMLLayoutType htmlLayout)
    {
        this.htmlLayout = htmlLayout;
    }

    public abstract void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth);

    public abstract void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth);

    public void generateAttributes(HTMLSnapshotContext context) throws IOException
    {
        // gen attribute
        Map odAttributeMap = context.getHTMLAttributeMap(getName());
        if (odAttributeMap != null && !odAttributeMap.isEmpty())
        {
            for (Iterator it = odAttributeMap.keySet().iterator(); it.hasNext(); )
            {
                String attributeName = (String) it.next();
                Object attributeValue = odAttributeMap.get(attributeName);
                generateAttribute(context, attributeName, attributeValue);
            }
        }

        if (attributeMap != null && !attributeMap.isEmpty())
        {
            attributeMap.remove("initValidation");
            attributeMap.remove("validator");
            String type = (String)attributeMap.remove("type");
            if (type != null)
            {
                String defaultCssClass = "uimaster_" + type.substring(0, 1).toLowerCase() + type.substring(1, type.length()-4);
                if (getReadOnly() != null && getReadOnly().booleanValue())
                {
                    defaultCssClass += "_readonly";
                }
                String UIStyle = (String)attributeMap.get("UIStyle");
                if (UIStyle != null && !UIStyle.trim().equals("null"))
                {
                    UIStyle = defaultCssClass + " " + UIStyle;
                    attributeMap.put("UIStyle", UIStyle);
                }
                else
                {
                    attributeMap.put("UIStyle", defaultCssClass);
                }
            }
            for (Iterator it = attributeMap.keySet().iterator(); it.hasNext(); )
            {
                String attributeName = (String) it.next();
                if (odAttributeMap == null || !odAttributeMap.containsKey(attributeName))
                {
                    Object attributeValue = attributeMap.get(attributeName);
                    generateAttribute(context, attributeName, attributeValue);
                }
            }
        }

        // gen style attribute
        if (styleMap != null && !styleMap.isEmpty())
        {
            context.generateHTML(" style=\"");
            for (Iterator it = styleMap.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry)it.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                context.generateHTML(name);
                context.generateHTML(":");
                context.generateHTML(value);
                context.generateHTML(";");
            }
            context.generateHTML("\"");
        }
    }

    private String[] convertToStringArray(Object attributeValue)
    {
        if (attributeValue instanceof String)
        {
            return new String[]{(String)attributeValue};
        }
        else if (attributeValue instanceof List)
        {
            List<String> temp = (List<String>)attributeValue;
            String[] tempArray = new String[temp.size()];
            return temp.toArray(tempArray);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * This method will replace \\ to \ to avoid html escape issue
     * @param origin
     */
    private String handleEscape(String origin)
    {    
        StringBuffer sb = new StringBuffer(origin.length());
        int index1 = 0;
        int index2 = 0;
        while(true)
        {
            index1 = origin.indexOf("\\\\",index2);
            if(index1 < 0)//to the end
            {
                sb.append(origin.substring(index2));
                break;
            }
            else
            {
                sb.append(origin.substring(index2,index1));
                sb.append("\\");
                index2 = index1+2;
            }
        }
        return sb.toString();
    }

    public void generateEventListeners(HTMLSnapshotContext context) throws IOException
    {
        if (eventListenerMap == null)
        {
            return;
        }
        else
        {
            for (Iterator it = eventListenerMap.keySet().iterator();it.hasNext();)
            {
                String event = it.next().toString();
                context.generateHTML(" ");
                context.generateHTML(event);
                context.generateHTML("=\"");
                context.generateHTML(getReconfigurateFunction(getEventListener(event)));
                context.generateHTML("\"");
            }
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if("initValidation".equals(attributeName) || "validator".equals(attributeName) 
            || "viewPermission".equals(attributeName) || "editPermission".equals(attributeName))
        {
            return;
        }
        if(!(attributeValue instanceof String))
        {
        	attributeValue = attributeValue == null ? "": attributeValue.toString();
        }
        String attrValue = (String)attributeValue;
        if ("visible".equals(attributeName))
        {
            if ("false".equals(attrValue))
            {
                addStyle("display", "none");
            }
        }
        else if ("editable".equals(attributeName))
        {
            if ("false".equals(attrValue))
            {
                context.generateHTML(" disabled=\"true\"");
            }
        }
        else if ("UIStyle".equals(attributeName))
        {
            if (!"null".equals(attrValue))
            {
                context.generateHTML(" class=\"");
                context.generateHTML(attrValue);
                context.generateHTML("\"");
            }
        }
        else if("reconfiguration".equals(attributeName))
        {
            context.generateHTML(" reconfiguration='");
            context.generateHTML(attrValue);
            context.generateHTML("'");
        }
        else if("regex".equals(attributeName))
        {
            context.generateHTML(" regex=\"");
            context.generateHTML(HTMLUtil.formatHtmlValue(this.handleEscape(attrValue)));
            context.generateHTML("\"");
        }
        else if (attributeName.endsWith("-msg--"))
        {
            return;
        }
        else if ("value".equals(attributeName))
        {
            if(context.isValueMask())
            {
                context.generateHTML(" ");
                context.generateHTML(attributeName);
                context.generateHTML("=\"");
                context.generateHTML(HTMLUtil.formatHtmlValue(WebConfig.getHiddenValueMask()));
                context.generateHTML("\"");
            }
        }
        else
        {
            context.generateHTML(" ");
            context.generateHTML(attributeName);
            context.generateHTML("=\"");
            context.generateHTML(HTMLUtil.formatHtmlValue(attrValue));
            context.generateHTML("\"");
        }
    }

    public void generateWidget(HTMLSnapshotContext context)
    {
        String visibleValue = (String)getAllAttribute("visible");
        boolean unVisible = false;
        if ( visibleValue != null )
        {
            unVisible = visibleValue.equals("false");
        }
        if ( unVisible )
        {
            context.generateHTML("<span style=\"display:none;\">");
        }
        String widgetLabel = (String)removeAttribute("widgetLabel");
        if ( widgetLabel !=  null && !widgetLabel.equals("") )
        {
            String widgetLabelColor = (String)removeAttribute("widgetLabelColor");
            String widgetLabelFont = (String)removeAttribute("widgetLabelFont");

            context.generateHTML("<label");
            context.generateHTML(" id=\"" + getName() + "_widgetLabel\"");
            context.generateHTML(" style=\"display:block;");
            if ( widgetLabelColor != null )
            {
                context.generateHTML("color:");
                if (widgetLabelColor.startsWith("#"))
                {
                    context.generateHTML(widgetLabelColor);
                }
                else
                {
                    context.generateHTML("rgb(" + widgetLabelColor + ")");
                }
                context.generateHTML(";");
            }
            if ( widgetLabelFont != null )
            {
                String[] fontInfo  = widgetLabelFont.split(",");
                if (fontInfo.length != 3)
                {
                    logger.warn("widgetLabelFont error:" + widgetLabelFont);
                }
                else
                {
                    String fontStyle = "Plain".equals(fontInfo[2]) ? "normal" : fontInfo[2];
                    fontStyle += " " + fontInfo[1] + "pt";
                    fontStyle += " " + fontInfo[0];
                    context.generateHTML("font:");
                    context.generateHTML(fontStyle);
                    context.generateHTML(";");
                }
            }
            context.generateHTML("\" class=\"uimaster_widgetLabel\">");
            if ( widgetLabel.equals(" ") )
            {
                context.generateHTML("&nbsp;");
            }
            else
            {
                context.generateHTML(widgetLabel);
            }
            context.generateHTML("</label>");
        }

        if ( unVisible )
        {
            context.generateHTML("</span>");
        }
    }

    public void generateEndWidget(HTMLSnapshotContext context)
    {
    	String dlinkInfo = (String)getAllAttribute("dlinkInfo");
        if (dlinkInfo != null)
        {
        	int index = dlinkInfo.lastIndexOf(";");
        	String link = dlinkInfo.substring(0, index);
        	String comment = dlinkInfo.substring(index + 1);
        	context.generateHTML("<span><a href=\"javascript:dPageLink('");
        	context.generateHTML(link);
        	context.generateHTML("');\" title=\"");
        	context.generateHTML(comment);
        	context.generateHTML("\");\">");
        	context.generateHTML(comment);
        	context.generateHTML("</a></span>");
        }
    }
    
    public void preIncludePage(HTMLSnapshotContext context)
    {
        String preIncludePage = (String)removeAttribute("preIncludePage");
        if ( preIncludePage != null && !preIncludePage.trim().equals(""))
        {
            if ( !preIncludePage.startsWith("/") )
            {
                preIncludePage = "/" + preIncludePage;
            }
            try
            {
            	//TODO:
                //context.getPageContext().include(preIncludePage);
            }
            catch (Exception e)
            {
                logger.error("error occured when include pre page for component: " + getName()
                        + " in entity: " + getUIEntityName(), e);
            }
        }
    }

    public void postIncludePage(HTMLSnapshotContext context)
    {
        String postIncludePage = (String)removeAttribute("postIncludePage");
        if ( postIncludePage != null && !postIncludePage.trim().equals(""))
        {
            if ( !postIncludePage.startsWith("/") )
            {
                postIncludePage = "/" + postIncludePage;
            }
            try
            {
            	//TODO:
                //context.getPageContext().include(postIncludePage);
            }
            catch (Exception e)
            {
                logger.error("error occured when include post page for component: " + getName()
                        + " in entity: " + getUIEntityName(), e);
            }
        }
    }

    public void setContext(HTMLSnapshotContext context)
    {
        this.context = context;
    }

    public HTMLSnapshotContext getContext()
    {
        return context;
    }

    public boolean isVisible()
    {
        String visible = (String) getAllAttribute("visible");
        return visible == null ? true : "true".equals(visible);
    }

    public void setVisible(boolean visible)
    {
        setHTMLAttribute("visible", String.valueOf(visible));
    }

    public void disableValidation()
    {
        setHTMLAttribute("validationFlag", "disabled");
    }
    
    public boolean isEditable()
    {
        String editable = (String) getAllAttribute("editable");
        return editable == null ? true : "true".equals(editable);
    }

    public void setEditable(boolean editable)
    {
        setHTMLAttribute("editable", String.valueOf(editable));
    }

    public String getUIID()
    {
        return this.id;
    }
    
    /**
     * Whether this component can have editPermission.
     */
    public boolean isEditPermissionEnabled()
    {
        return true;
    }
    
    /**
     * this method uses for UI to Data operation.
     * @return
     */
	protected String getValueFromRequest() 
	{
		return this.context.getRequest().getParameter(this.getUIID());
	}
	
	/**
     * this method uses for UI to Data operation.
     * @return
     */
	protected String[] getValuesFromRequest() 
	{
		return this.context.getRequest().getParameterValues(this.getUIID());
	}
	
	public Widget createAjaxWidget(VariableEvaluator ee) {
		return null;
	}
}
