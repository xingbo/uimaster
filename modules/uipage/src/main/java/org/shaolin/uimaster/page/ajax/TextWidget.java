package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.json.IDataItem;

abstract public class TextWidget extends Widget implements Serializable
{
    private static final long serialVersionUID = 7730729344287217301L;
    
    private String currencySymbol;
    
    private boolean isSysmbolLeft;
    
    public TextWidget(String id, Layout layout)
    {
        super(id, layout);
    }
    
    public TextWidget(String id, String value, Layout layout)
    {
        super(id, layout);
        this.setValue(value);
    }

    protected void generateAttribute(String name, Object value, StringBuffer sb)
    {
		if (!name.equals("value")) {
			super.generateAttribute(name, value, sb);
		}
    }

    public void setValue(String value)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        addAttribute("value", value, false);
        
        if(!this.isListened())
        {
            return;
        }
        
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if (ajaxContext == null || !ajaxContext.existElement(this))
        {
            return;
        }
        
        String str =  HTMLUtil.handleEscape(String.valueOf(value));
        IDataItem dataItem = AjaxActionHelper.updateAttrItem(this.getId(), str);
        dataItem.setFrameInfo(getFrameInfo());
        // Value update is more special than other attribute.
        dataItem.setSibling("TEXTCOMPONENT_VALUE_UPDATE");
        ajaxContext.addDataItem(dataItem);
    }
    
    public void checkConstraint() {
    	String value = (String)getAttribute("value");
    	if(this.hasConstraint("allowBlank")) {
    		boolean allowBlank = Boolean.valueOf(this.getConstraint("allowBlank").toString());
    		if (!allowBlank && value == null) {
    			throw new IllegalStateException("UI Constraint fails in: " 
    							+ this.getConstraint("allowBlankText") 
    							+ ",UIID: " + this.getId());
    		}
    	}
    	if(this.hasConstraint("minLength")) {
    		int minLength = Integer.valueOf(this.getConstraint("minLength").toString());
    		if (value != null && minLength > value.length()) {
    			throw new IllegalStateException("UI Constraint fails in: " 
    							+ this.getConstraint("minLengthText") 
    							+ ",UIID: " + this.getId());
    		}
    	}
    	if(this.hasConstraint("maxLength")) {
    		int maxLength = Integer.valueOf(this.getConstraint("maxLength").toString());
    		if (value != null && maxLength < value.length()) {
    			throw new IllegalStateException("UI Constraint fails in: " 
    							+ this.getConstraint("maxLengthText") 
    							+ ",UIID: " + this.getId());
    		}
    	}
    	if(this.hasConstraint("regex")) {
    		String regex = (String)this.getConstraint("regex");
    		Pattern pattern = Pattern.compile(regex);
    		Matcher matcher = pattern.matcher(value);
    		if (!matcher.matches()) {
    			throw new IllegalStateException("UI Constraint fails in: " 
    							+ this.getConstraint("regexText") 
    							+ ",UIID: " + this.getId());
    		}
    	}
    }

    public String getValue()
    {
    	checkConstraint();
        return (String)getAttribute("value");
    }

    public void setCurrencySymbol(String currencySymbol)
    {
        this.currencySymbol = currencySymbol;
        addAttribute("currencySymbol", currencySymbol);
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    public void setSymbolLeft(boolean isSysmbolLeft)
    {
        this.isSysmbolLeft = isSysmbolLeft;
        addAttribute("isSysmbolLeft", isSysmbolLeft);
    }

    public boolean isSymbolLeft()
    {
        return isSysmbolLeft;
    }
    
    protected void generateCurrencySymbol(StringBuffer sb)
    {
        sb.append("<span class=\"currencySymbol\"");
        sb.append(" id=\"" + getId() + "_currencySymbol\" >");
        sb.append(currencySymbol);
        sb.append("</span>");
    }
}
