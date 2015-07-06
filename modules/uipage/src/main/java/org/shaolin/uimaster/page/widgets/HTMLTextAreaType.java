package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.TextArea;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLTextAreaType extends HTMLTextWidgetType
{
	private static final long serialVersionUID = -2216731075469117671L;
	
    private static Logger logger = LoggerFactory.getLogger(HTMLTextAreaType.class);

    public HTMLTextAreaType()
    {
    }

    public HTMLTextAreaType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLTextAreaType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            generateWidget(context);
            if ( getReadOnly() != null && getReadOnly().booleanValue() )
            {
                addAttribute("allowBlank", "true");
                addAttribute("readOnly", "true");
            }
            context.generateHTML("<textarea name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            if (getAllAttribute("rows") == null)
            {
                context.generateHTML(" rows=\"4\"");
            }
            if (getAllAttribute("cols") == null)
            {
                context.generateHTML(" cols=\"30\"");
            }
            generateEventListeners(context);
            context.generateHTML(">");
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            }
            context.generateHTML("</textarea>");
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
        TextArea textArea = new TextArea(getName(), Layout.NULL);

        textArea.setReadOnly(getReadOnly());
        textArea.setUIEntityName(getUIEntityName());

        // we don't expect to anything except the pure value 
        // what we really need in the backend.
        textArea.setValue(getValue());

        setAJAXConstraints(textArea);
        setAJAXAttributes(textArea);
        
        textArea.setListened(true);
        textArea.setFrameInfo(getFrameInfo());

        return textArea;
    }
    
}
