package org.shaolin.uimaster.page.widgets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.uimaster.page.HTMLSnapshotContext;

public abstract class HTMLMultiChoiceType extends HTMLChoiceType 
{

    public HTMLMultiChoiceType()
    {
    }
     
    public HTMLMultiChoiceType(HTMLSnapshotContext context)
    {
        super(context);
    }

	public HTMLMultiChoiceType(HTMLSnapshotContext context, String id)
	{
	    super(context, id);
	}
	 
    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
	{
	    if ( "value".equals(attributeName) )
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
	}
    
	protected boolean equal(String str)
	{
		List<String> value = getValue();
		for (int i = 0; i<value.size(); i++)
		{
			if (value.get(i).toString().equalsIgnoreCase(str))
			{
				return true;
			}
		}
		return false;
	}

	public List getValue() {
		List values = (List) getAllAttribute("value");
		return values == null ? new ArrayList() : values;
	}


	public void setValue(List<String> value)
	{
        setHTMLAttribute("value", value);
	}

    private static final long serialVersionUID = 8008004105287717253L;
}
