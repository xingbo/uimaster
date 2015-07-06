package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HTMLSingleChoiceType extends HTMLChoiceType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLSingleChoiceType.class);

    public HTMLSingleChoiceType()
    {
    }
 
    public HTMLSingleChoiceType(HTMLSnapshotContext context)
    {
        super(context);
    }

	public HTMLSingleChoiceType(HTMLSnapshotContext context, String id)
	{
	    super(context, id);
	}
	
    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
	{
	    if("value".equals(attributeName))
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
	}
    
    public String getValue()
	{
	    String value = (String) getAllAttribute("value");
        return value == null ? "":value;
	}

	public void setValue(String value)
	{
	    setHTMLAttribute("value", value);
	}

    private static final long serialVersionUID = 9069902870270456324L;
}
