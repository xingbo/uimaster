package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Calendar;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLDateType extends HTMLWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLDateType.class);

    private static final long serialVersionUID = -5232602952223828765L;
    
    private boolean isRange = false;
    
	public HTMLDateType()
    {
    }

    public HTMLDateType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLDateType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public boolean isRange() {
		return isRange;
	}

	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}
    
    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        generateWidget(context);
        generateContent(context);
    }

    public String getValue()
    {
        String value = (String)getAllAttribute("value");
        if (value == null)
        {
            value = (String)getAllAttribute("text");
        }
        return value == null ? "" : value;
    }

    public void setValue(String value)
    {
        setHTMLAttribute("value", value);
    }
    
    private void generateContent(HTMLSnapshotContext context)
    {
        try
        {
			if (getReadOnly() != null && getReadOnly().booleanValue()) {
				addAttribute("allowBlank", "true");
				addAttribute("readOnly", "true");
			}
            context.generateHTML("<input type=\"text\" name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(" value=\"");
            context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            context.generateHTML("\" />");
            if (isRange) {
            	context.generateHTML("<img src='");
				context.generateHTML(WebConfig.getResourceContextRoot() + "/images/controls/calendar/selectdate.gif");
				context.generateHTML("' />");
            } else {
	            if (getReadOnly() == null || !getReadOnly().booleanValue()) {
		            context.generateHTML("&nbsp;&nbsp;<img src='");
					context.generateHTML(WebConfig.getResourceContextRoot() + "/images/controls/calendar/selectdate.gif");
					context.generateHTML("' onclick='javascript:defaultname.");
					context.generateHTML(this.getPrefix() + this.getUIID());
					context.generateHTML(".open();'/>");
	            }
            }
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if ("initValidation".equals(attributeName) || "validator".equals(attributeName))
        {
        	return;
        }
        if ("editable".equals(attributeName))
        {
            if ("false".equals(String.valueOf(attributeValue)))
            {
                context.generateHTML(" readOnly=\"true\"");
            }
        }
        else if ("maxLength".equals(attributeName))
        {
            context.generateHTML(" maxlength=\"");
            context.generateHTML((String)attributeValue);
            context.generateHTML("\"");
        }
        else if ("txtFieldLength".equals(attributeName))
        {
            context.generateHTML(" size=\"");
            context.generateHTML((String)attributeValue);
            context.generateHTML("\"");
        }
        else if ("prompt".equals(attributeName))
        {
            if ( attributeValue != null && !((String)attributeValue).trim().equals("") )
            {
                context.generateHTML(" title=\"");
                context.generateHTML((String)attributeValue);
                context.generateHTML("\"");
            }
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        Calendar calendar = new Calendar(getName(), Layout.NULL);

        calendar.setReadOnly(getReadOnly());
        calendar.setUIEntityName(getUIEntityName());

        // we don't expect to anything except the pure value 
        // what we really need in the backend.
        calendar.setValue(getValue());

        // add necessary attribute especially the server side constraint check.
        setAJAXConstraints(calendar);
        setAJAXAttributes(calendar);
        
        calendar.setListened(true);
        calendar.setFrameInfo(getFrameInfo());

        return calendar;
    }

}
