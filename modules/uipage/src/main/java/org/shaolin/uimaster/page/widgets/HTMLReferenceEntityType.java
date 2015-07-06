package org.shaolin.uimaster.page.widgets;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.utils.ClassLoaderUtil;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.PageDispatcher;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.RefForm;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;

public class HTMLReferenceEntityType extends HTMLWidgetType implements Serializable
{
    public static final String EVENT = "event";
    public static final String PROPERTY = "property";
    public static final String VARIABLE = "variable";

    private static Logger logger = Logger.getLogger(HTMLReferenceEntityType.class);
    private static final Map typeMap = new ConcurrentHashMap();

    private Map functionReconfigurationMap;
    private Map propertyReconfigurationMap;
    private Map variableReconfigurationMap;
    private String curUIEntityName;
    private String divPrefix;

    public HTMLReferenceEntityType()
    {
    }

    public HTMLReferenceEntityType(String type)
    {
        this.type = type;
    }

    public HTMLReferenceEntityType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLReferenceEntityType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public HTMLReferenceEntityType(HTMLSnapshotContext context, String id, String type)
    {
        super(context, id);
        String refE = getReferenceEntity();
        if (refE != null)
        {
            this.type = refE;
        }
        else
        {
            this.type = type;
        }
        setReferenceEntity(this.type);
    }

    public HTMLReferenceEntityType(HTMLSnapshotContext context, String id,
            HTMLReferenceEntityType referenceEntity)
    {
        super(context, id);
        if (referenceEntity != null)
        {
            this.type = referenceEntity.getType();
            setReferenceEntity(this.type);
        }
    }

    public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        curUIEntityName = getUIEntityName();
        divPrefix = context.getDIVPrefix();
        String name = getName();
        context.setHTMLPrefix(name + ".");
        context.setFormName(getReferenceEntity());
        context.setDIVPrefix(getDIVPrefix() + "-");
        if ( functionReconfigurationMap != null )
        {
            context.setReconfigFunction(functionReconfigurationMap);
        }
        if ( propertyReconfigurationMap != null )
        {
            context.setReconfigProperty(propertyReconfigurationMap);
        }
        if ( variableReconfigurationMap != null && !variableReconfigurationMap.isEmpty() )
        {
            Iterator iterator = variableReconfigurationMap.keySet().iterator();
            while ( iterator.hasNext() )
            {
                String key = (String)iterator.next();
                context.addReconfigVariable(key, variableReconfigurationMap.get(key));
            }
        }
    }

    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            HTMLWidgetType parentComponent = (HTMLWidgetType) context.getRequest().
                    getAttribute(HTMLSnapshotContext.REQUEST_PARENT_TAG_KEY);
            context.getRequest().setAttribute(HTMLSnapshotContext.REQUEST_PARENT_TAG_KEY, this);

            generateHTML(context);

            context.getRequest().setAttribute(HTMLSnapshotContext.REQUEST_PARENT_TAG_KEY, parentComponent);
            context.setFormName(curUIEntityName);
            context.setHTMLPrefix(getPrefix());
            context.setDIVPrefix(divPrefix);
        }
        catch (Exception e)
        {
            logger.error("Error occur when include uientity jsp page in " + curUIEntityName, e);
        }
    }

    private void setReferenceEntity(String referenceEntity)
    {
        String htmlName = getName();
        if (htmlName.endsWith("."))
        {
            htmlName = htmlName.substring(0, htmlName.length() - 1);
        }
        getContext().addHTMLAttribute(htmlName, "referenceEntity", referenceEntity);
    }

    public String getReferenceEntity()
    {
        String htmlName = getName();
        if (htmlName.endsWith("."))
        {
            htmlName = htmlName.substring(0, htmlName.length() - 1);
        }
        String referenceEntity = null;
        if (!"".equals(htmlName))
        {
            referenceEntity = (String) getContext().getHTMLAttribute(htmlName, "referenceEntity");
        }
        return referenceEntity == null ? (String) getAttribute("referenceEntity") : referenceEntity;
    }

    private boolean isHidden()
    {
        return "false".equals((String) getAllAttribute("visible"));
    }

    public void addFunctionReconfiguration(String name, String value)
    {//TODO should be deleted
        if (functionReconfigurationMap == null)
        {
            functionReconfigurationMap = new HashMap();
        }
        functionReconfigurationMap.put(name, value);
    }

    public void addPropertyReconfiguration(String name, Object value)
    {//TODO maybe should be private
        if (propertyReconfigurationMap == null)
        {
            propertyReconfigurationMap = new HashMap();
        }
        propertyReconfigurationMap.put(name, value);
    }

    public void setReconfiguration(Map reconfigurationMap, Map propMap, Map appendMap)
    {
        if ( reconfigurationMap != null )
        {
            functionReconfigurationMap = (Map)reconfigurationMap.get(EVENT);

            Set propertyNameSet = (Set)reconfigurationMap.get(PROPERTY);
            if ( propertyNameSet != null )
            {
                Iterator iterator = propertyNameSet.iterator();
                while ( iterator.hasNext() )
                {
                    String propName = (String)iterator.next();
                    Object value = appendMap == null ? null : appendMap.get(propName);
                    if ( value == null )
                    {
                        value = propMap.get(propName);
                    }
                    if ( value == null )
                    {
                        //TODO throw a JSPException?
                        //throw new JspException("Component set propertyName->ReconfigurationType reconfiguration, but not set propertyValue in attribute.");
                    }
                    addPropertyReconfiguration(propName, value);
                }
            }

            variableReconfigurationMap = (Map)reconfigurationMap.get(VARIABLE);
        }
    }

    private String type;

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public HTMLReferenceEntityType getHTMLComponent(String id)
    {
        HTMLReferenceEntityType htmlComponent = new HTMLReferenceEntityType(getContext(), id);
        htmlComponent.setPrefix(getName());
        return htmlComponent;
    }

    public HTMLReferenceEntityType getHTMLComponent(String id, int suffix)
    {
        HTMLReferenceEntityType htmlComponent = getHTMLComponent(id);
        return htmlComponent;
    }

    public HTMLWidgetType getHTMLComponent(String id, String type)
    {
        HTMLWidgetType htmlComponent = null;
        Class cls = getHTMLComponentType(type);
        if (cls != null)
        {
            try
            {
                htmlComponent = (HTMLWidgetType) cls.newInstance();
                htmlComponent.setContext(getContext());
                htmlComponent.setId(id);
                htmlComponent.setPrefix(getName());
            }
            catch (Exception e)
            {
                logger.error("init error for " + type, e);
            }
        }
        else
        {
            htmlComponent = new HTMLReferenceEntityType(getContext(), id);
            htmlComponent.setPrefix(getName());
        }
        return htmlComponent;
    }

    public HTMLWidgetType getHTMLComponent(String id, int suffix, String type)
    {
        HTMLWidgetType htmlComponent = getHTMLComponent(id, type);
        return htmlComponent;
    }

    public void setHTMLComponent(String id, HTMLReferenceEntityType referenceEntity)
    {
        getContext().addHTMLAttribute(getName() + id, "referenceEntity",
                referenceEntity.getType());
    }

    public void setHTMLComponent(String id, int suffix, HTMLReferenceEntityType referenceEntity)
    {
        getContext().addHTMLAttribute(getName() + id + "[" + suffix + "]", "referenceEntity",
                referenceEntity.getType());
    }

    private Class getHTMLComponentType(String typeName)
    {
        if (typeMap.containsKey(typeName))
        {
            return (Class) typeMap.get(typeName);
        }
        else
        {
            Class cls = null;
            try
            {
                cls = ClassLoaderUtil.loadClass(typeName);
                typeMap.put(typeName, cls);
            }
            catch (ClassNotFoundException e)
            {
            }
            return cls;
        }
    }

    private int depth = 0;

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    private void generateHTML(HTMLSnapshotContext context) throws JspException
    {
    	UIFormObject entity = HTMLUtil.parseUIEntity(getReferenceEntity());
    	
    	EvaluationContext evaContext = context.getODMapperContext(this.getPrefix() + this.getUIID());
    	if (evaContext == null) {
    		evaContext = new DefaultEvaluationContext();
    	}
        PageDispatcher dispatcher = new PageDispatcher(entity, evaContext);
        dispatcher.forward(context, depth, getReadOnly(), this);
    }
    
    public String getDIVPrefix()
    {
        StringBuffer prefixBuffer = new StringBuffer();
        if (divPrefix != null && divPrefix.length() > 0)
        {
            prefixBuffer.append(divPrefix);
        }

        String id = getId();
        prefixBuffer.append(id);

        return new String(prefixBuffer);
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
    	HTMLReferenceEntityType copy = new HTMLReferenceEntityType();
    	copy.type = this.type;
    	copy.curUIEntityName = this.curUIEntityName;
    	copy.divPrefix = this.divPrefix;
    	copy.setId(this.getId());
    	copy.setFrameInfo(this.getFrameInfo());
    	copy.setPrefix(this.getPrefix());
    	
        String refEntityName = this.getReferenceEntity();
        RefForm referenceEntity = new RefForm(getName(), refEntityName, Layout.NULL);
        referenceEntity.setCopy(copy);
        referenceEntity.setReadOnly(getReadOnly());
        referenceEntity.setListened(true);
        referenceEntity.setFrameInfo(getFrameInfo());
        return referenceEntity;
    }

    private static final long serialVersionUID = -6715298246482475095L;
}
