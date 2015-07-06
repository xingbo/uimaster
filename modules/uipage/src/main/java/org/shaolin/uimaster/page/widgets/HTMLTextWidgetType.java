package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HTMLTextWidgetType extends HTMLWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLTextWidgetType.class);

    public HTMLTextWidgetType()
    {
    }

    public HTMLTextWidgetType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLTextWidgetType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName,
            Object attributeValue) throws IOException
    {
        if ("text".equals(attributeName) || "value".equals(attributeName))
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
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

    public String getCurrencySymbol()
    {
        String currencySymbol = (String)getAllAttribute("currencySymbol");

        return currencySymbol == null ? "" : currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol)
    {
        setHTMLAttribute("currencySymbol", currencySymbol);
    }

    public boolean getIsSymbolLeft()
    {
        String isLeft = (String)getAllAttribute("isLeft");

        return (isLeft == null || isLeft.equals("true")) ? true : false;
    }

    public void setIsSymbolLeft(Boolean isLeft)
    {
        if (isLeft == null)
        {
            isLeft = Boolean.TRUE;
        }
        setHTMLAttribute("isLeft", isLeft.toString());
    }
    
    public void setIsCurrency(boolean isCurrency)
    {
		setHTMLAttribute("isCurrency", (Boolean.valueOf(isCurrency)).toString());
    }
    
    public boolean getIsCurrency()
    {
    	String isCurrency = (String)getAllAttribute("isCurrency");
    	return (isCurrency != null && isCurrency.equalsIgnoreCase("true")) ? true : false;    	
    }

    public String getLocale()
    {
        String locale = (String)getAllAttribute("locale");

        return locale == null ? "" : locale;
    }

    public void setLocale(String locale)
    {
        setHTMLAttribute("locale", locale == null ? "" : locale);
    }

    public String getCurrencyFormat()
    {
        String locale = (String)getAllAttribute("currencyFormat");

        return locale == null ? "" : locale;
    }

    public void setCurrencyFormat(String format)
    {
        setHTMLAttribute("currencyFormat", format == null ? "" : format);
    }

    private static final long serialVersionUID = 1705497534002626203L;
}
