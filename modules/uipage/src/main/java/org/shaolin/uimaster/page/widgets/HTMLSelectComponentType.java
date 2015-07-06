package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HTMLSelectComponentType extends HTMLWidgetType
{
	private static final Logger logger = LoggerFactory.getLogger(HTMLSelectComponentType.class); 
    
    public HTMLSelectComponentType()
    {
    }
     
    public HTMLSelectComponentType(HTMLSnapshotContext context)
    {
        super(context);
    }

	public HTMLSelectComponentType(HTMLSnapshotContext context, String id)
	{
	    super(context, id);
	}
	
    public String getLabel()
    {
        return (String) getAllAttribute("label");
    }

	public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
	{
        if ("selected".equals(attributeName))
        {
            if ("true".equals((String)attributeValue))
            {
                context.generateHTML(" CHECKED");
            }
        }
        else if ("label".equals(attributeName))
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
	}
    
	public boolean getValue() {
		return "true".equals((String) getAllAttribute("selected"));
	}


	public void setValue(boolean value)
	{
	    setHTMLAttribute("selected", String.valueOf(value));
	}
}
