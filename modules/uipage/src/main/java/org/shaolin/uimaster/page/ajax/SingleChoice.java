package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.List;

abstract public class SingleChoice extends Choice implements Serializable
{
    private static final long serialVersionUID = 329245687316770737L;

    public SingleChoice(String id, Layout layout)
    {
        super(id, layout);
    }

    public void generateAttribute(String name, Object value, StringBuffer sb)
    {
        if ( !name.equals("value") )
        {
            super.generateAttribute(name, value, sb);
        }
    }
    
    public void setOptions(List<String> displayValues, List<String> optionValues)
    {
        super.setOptions(displayValues, optionValues);
        if(optionValues != null && optionValues.size() > 0)
            addAttribute("value", optionValues.get(0), true);//select the first one by default.
    }
    
    public void clearValue()
    {
        addAttribute("value", "");
    }
    
    public void setValue(String value)
    {
        if(this._isReadOnly())
        {
            return;
        }
        if(value == null || this.optionValues == null)
        {
            return;
        }
        if (checkValueExist(value))
        {
            addAttribute("value", value);
        }
    }

    boolean checkValueExist(String value)
    {
        for (Object option : optionValues)
        {
            if (String.valueOf(option).equals(value))
                return true;
        }

        return false;
    }
    
    public void checkConstraint() {
    	Object value = getAttribute("value");
    	if(this.hasConstraint("selectedValueConstraint")) {
    		String selectedValue = (String)this.getConstraint("selectedValueConstraint");
    		if (value == null || !selectedValue.equals(value)) {
    			throw new IllegalStateException("UI Constraint fails in: " 
    							+ this.getConstraint("selectedValueConstraintText")
    							 + ",UIID: " + this.getId());
    		}
    	}
	}

    public String getValue()
    {
    	checkConstraint();
    	
        String value = String.valueOf(getAttribute("value"));
        return value == null ? "" : value;
    }

}
