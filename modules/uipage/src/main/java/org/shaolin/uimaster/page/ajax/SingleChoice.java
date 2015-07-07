/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
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
