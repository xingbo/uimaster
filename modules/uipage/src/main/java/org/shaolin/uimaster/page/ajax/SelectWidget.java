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

abstract public class SelectWidget extends Widget implements Serializable
{
    private static final long serialVersionUID = 1871297456126957797L;

    private String label;

    public SelectWidget(String id, Layout layout)
    {
        super(id, layout);
        this._setWidgetLabel(id);
    }

	public void generateAttribute(String name, Object value, StringBuffer sb) {
		if (name.equals("selected")) {
			if (value != null && Boolean.valueOf((String) value).booleanValue()) {
				sb.append(" CHECKED");
			}
		} else {
			super.generateAttribute(name, value, sb);
		}
	}

    public void setLabel(String label)
    {
        this.label = label;
        addAttribute("label", label);
    }

    public String getLabel()
    {
        return label;
    }

	public void setSelected(boolean selected)
	{
	    if(this._isReadOnly())
	    {
	        return;
	    }
	    addAttribute("selected", String.valueOf(selected));
	}

	public void checkConstraint() {
    	String value = (String)getAttribute("selected");
    	if(this.hasConstraint("mustCheck")) {
    		boolean mustCheck = (Boolean)this.getConstraint("mustCheck");
    		if (mustCheck && value == null || !"true".equals(value)) {
    			throw new IllegalStateException("UI Constraint fails in: " 
    							+ this.getConstraint("mustCheckText")
    							+ ",UIID: " + this.getId());
    		}
    	}
	}
	
    public boolean isSelected()
    {
    	checkConstraint();
    	
        String selected = (String)getAttribute("selected");
        if (selected != null)
        {
            return Boolean.valueOf(selected).booleanValue();
        }
        else
        {
            return false;
        }
    }

}
