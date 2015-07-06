package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLContainerType extends HTMLWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLContainerType.class);
    
    public HTMLContainerType()
    {
    }

    public HTMLContainerType(HTMLSnapshotContext context)
    {
        super(context);
    }

	public HTMLContainerType(HTMLSnapshotContext context, String id)
	{
	    super(context, id);
	}

	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
	{
		try
		{
	        context.generateHTML("<div");

			generateAttributes(context);
    		if (getHTMLLayout() instanceof HTMLXYLayoutType)
    		{
    		    getHTMLLayout().generateAttributes(context);
    		}
			generateEventListeners(context);
			context.generateHTML(">");
		}
		catch (Exception e) 
		{
			logger.error("error. in entity: " + getUIEntityName(), e);
		}
	}

	public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
	{
		try
		{
    		context.generateHTML("</div>");
			context.getRequest().setAttribute(HTMLSnapshotContext.REQUEST_LAYOUT_CURRENT_ROW_KEY, 
					HTMLSnapshotContext.REQUEST_LAYOUT_END);
		}
		catch (Exception e) 
		{
			logger.error("error. in entity: " + getUIEntityName(), e);
		}
	}

	public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
	{
        if ("width".equals(attributeName))
        {
            String attrValue = getLengthValue((String)attributeValue);
            if (attrValue != null)
            {
                addStyle("width", attrValue);
            }
        }
        else if ("height".equals(attributeName))
        {
            String attrValue = getLengthValue((String)attributeValue);
            if (attrValue != null)
            {
                addStyle("height", attrValue);
            }
        }
        else if ( "column".equals(attributeName) || "row".equals(attributeName) || "value".equals(attributeName) )
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
	}

	private String getLengthValue(String stringValue)
	{
	    try
	    {
	        double doubleValue = Double.parseDouble(stringValue);
	        if (doubleValue == -1)
	        {
	            return "100%";
	        }
	        else if ( doubleValue ==0 || doubleValue >= 1 )
	        {
	            return String.valueOf((int) doubleValue) + "px";
	        }
	        else if ( doubleValue > 0 && doubleValue < 1 )
	        {
	            return (int) (doubleValue * 100) + "%";
	        }
	        else
	        {
	            return null;
	        }
	    }
	    catch (NumberFormatException e)
	    {
	        logger.warn(e.getMessage(), e);
	        return null;
	    }
	}

}
