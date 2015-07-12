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
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.RadioButton;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLRadioButtonType extends HTMLSelectComponentType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLRadioButtonType.class);

    public HTMLRadioButtonType()
    {
    }

    public HTMLRadioButtonType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLRadioButtonType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    private String _getName2()
    {
        String prefix = getPrefix() == null ? "" : getPrefix();
        return prefix + getId();
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            generateWidget(context);
            context.generateHTML("<input type=radio name=\"");
            context.generateHTML(_getName2());
            context.generateHTML("\"");
            context.generateHTML(" id=\"");
            context.generateHTML(_getName2());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            if (getReadOnly() != null && getReadOnly().booleanValue())
            {
                context.generateHTML(" disabled=\"true\"");
            }
            context.generateHTML(" />");
            if (!this.isVisible())
                context.generateHTML("<span style=\"display:none\">");
            context.generateHTML("<label for=\"");
            context.generateHTML(_getName2());
            context.generateHTML("\">");
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.htmlEncode(getLabel()));
            }
            context.generateHTML("</label>");
            if (!this.isVisible())
            {
                context.generateHTML("</span>");
            }
            generateEndWidget(context);
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        RadioButton radioButton = new RadioButton(getName(), Layout.NULL);

        radioButton.setReadOnly(getReadOnly());
        radioButton.setUIEntityName(getUIEntityName());

        radioButton.setLabel(getLabel());

        radioButton.setListened(true);
        radioButton.setSelected(getValue());
        radioButton.setFrameInfo(getFrameInfo());

        return radioButton;
    }
    
    private static final long serialVersionUID = -4405215152580918889L;
}
