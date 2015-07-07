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
