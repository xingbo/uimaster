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

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.Hidden;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLHiddenType extends HTMLTextWidgetType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLHiddenType.class);

    public HTMLHiddenType()
    {
    }

    public HTMLHiddenType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLHiddenType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            context.generateHTML("<input type=hidden name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(" value=\"");
            if (this.getAttribute("secure") == null || !Boolean.valueOf(this.getAttribute("secure").toString())) {
            	context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            }
            context.generateHTML("\" />");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    /**
     * Whether this component can have editPermission.
     */
    @Override
    public boolean isEditPermissionEnabled()
    {
        return false;
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        Hidden hidden = new Hidden(getName(), Layout.NULL);

        hidden.setReadOnly(getReadOnly());
        hidden.setUIEntityName(getUIEntityName());

        if (this.getAttribute("secure") != null) {
        	hidden.setIsSecure(Boolean.valueOf(this.getAttribute("secure").toString()));
        }
        hidden.setValue(getValue());
        
        hidden.setListened(true);
        hidden.setFrameInfo(getFrameInfo());

        return hidden;
    }
    
    private static final long serialVersionUID = 1875046878985040938L;
}
