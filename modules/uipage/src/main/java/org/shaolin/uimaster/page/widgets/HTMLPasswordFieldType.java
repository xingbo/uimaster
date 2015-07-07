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
import org.shaolin.uimaster.page.ajax.PasswordField;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLPasswordFieldType extends HTMLTextFieldType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLPasswordFieldType.class);

    public HTMLPasswordFieldType()
    {
    }

    public HTMLPasswordFieldType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLPasswordFieldType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public void generateEndHTML(HTMLSnapshotContext context, int depth)
    {
        try
        {
            generateWidget(context);
            if ( getReadOnly() != null && getReadOnly().booleanValue() )
            {
                addAttribute("allowBlank", "true");
                addAttribute("readOnly", "true");
            }
            context.generateHTML("<input type=\"password\" name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(" value=\"");
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            }
            context.generateHTML("\" />");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        PasswordField password = new PasswordField(getName(), Layout.NULL);

        password.setReadOnly(getReadOnly());
        password.setUIEntityName(getUIEntityName());

        password.setValue(getValue());

        password.setListened(true);
        password.setFrameInfo(getFrameInfo());

        return password;
    }

}
