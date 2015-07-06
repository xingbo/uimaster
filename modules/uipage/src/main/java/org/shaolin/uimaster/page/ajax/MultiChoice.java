package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

abstract public class MultiChoice extends Choice implements Serializable
{
    private static final long serialVersionUID = 3779890420561677855L;

    public MultiChoice(String id, Layout layout)
    {
        super(id, layout);
    }

    public void addAttribute(String name, Object value, boolean update)
    {
        if ( name.equals("values") )
        {
            if ( value instanceof List )
            {
                super.addAttribute(name, value, update);
            }
            else if ( value instanceof String )
            {
                List<String> values = getValues();
                values.clear();
                if( ((String) value).length() > 0)
                {
                    String[] valueArray = ((String) value).split(";");
                    for(int i=0; i<valueArray.length; i++)
                    {
                        values.add(valueArray[i]);
                    }
                }
                setValues(values);
            }
        }
        else
        {
            super.addAttribute(name, value, update);
        }
    }

    public void generateAttribute(String name, Object value, StringBuffer sb)
    {
        if ( !name.equals("values") )
        {
            super.generateAttribute(name, value, sb);
        }
    }
    
    public void addValue(String value)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        List<String> values = this.getValues();
        if(this.optionValues.contains(value) && !values.contains(value))
        {
            values.add(value);
            this.setValues(values);
        }
    }
    
    public void clearValues()
    {
        addAttribute("values", new ArrayList<String>());
    }
    
    public void removeValue(String value)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        List<String> values = this.getValues();
        if(values.contains(value))
        {
            values.remove(value);
            this.setValues(values);
        }
    }

    public void setValues(List<String> values)
    {
        if(this._isReadOnly())
        {
            return;
        }
        
        if(values == null || this.optionValues == null)
        {
            return;
        }
        //if values.length == 0, that means clear all values.
        for(int i = 0; i < values.size(); i++)//filter illegal item.
        {
            if( !this.optionValues.contains(values.get(i)) )
            {
                values.remove(i);
                i=0;//to prevent out of array index exception.
            }
        }

        super.addAttribute("values", values);
    }

    public void checkConstraint() {
    	Object value = getAttribute("values");
    	if (value == null) {
			throw new IllegalStateException("UI Constraint fails in: " 
					+ this.getConstraint("selectedValuesConstraintText")
					+ ",UIID: " + this.getId());
    	}
    	List<String> values = (List<String>)value;
    	if(this.hasConstraint("selectedValuesConstraint")) {
    		String[] selectedValues = (String[])
    				this.getConstraint("selectedValuesConstraintReal");
    		for (String v: selectedValues) {
    			if (!values.contains(v)) {
    				throw new IllegalStateException("UI Constraint fails in: " 
    						+ this.getConstraint("selectedValuesConstraintText")
    						+ ",UIID: " + this.getId());
    			}
    		}
    	}
	}
    
    public List<String> getValues()
    {
    	checkConstraint();
    	
        List<String> values = (List<String>)getAttribute("values");
        return values == null? new ArrayList(): values;
    }

    protected boolean equal(String str)
    {
        List<String> values = getValues();
        if ( values == null )
        {
            return false;
        }
        for ( int i = 0; i < values.size(); i++ )
        {
            if ( values.get(i).toString().equalsIgnoreCase(str) )
            {
                return true;
            }
        }
        return false;
    }
}
