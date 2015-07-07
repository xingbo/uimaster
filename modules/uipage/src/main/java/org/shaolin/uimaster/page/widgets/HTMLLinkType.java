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
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Link;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLLinkType extends HTMLLabelType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLLinkType.class);

    public HTMLLinkType()
    {
    }

    public HTMLLinkType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLLinkType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public void generateEndHTML(HTMLSnapshotContext context)
    {
        try
        {
            generateWidget(context);
            context.generateHTML("<input type=hidden name=\"");
            context.generateHTML(getName());
            context.generateHTML("\" value=\"");
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            }
            context.generateHTML("\" />");
            context.generateHTML("<a href=\"");
            context.generateHTML(getHref());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(">");
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.formatHtmlValue(getDisplayValue()));
            }
            context.generateHTML("</a>");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if ( "href".equals(attributeName) )
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

    private String getHref()
    {
        String href = (String) getAllAttribute("href");
        if (href == null)
        {
            return "#";
        }

        return getReconfigurateFunction(href);
    }

    public String getReconfigurateFunction(String handler)
    {
        HTMLSnapshotContext context = getContext();
        String functionPrefix = getPrefix();
        String reconfiguration = context.getReconfigFunction(functionPrefix, handler);
        while (reconfiguration != null)
        {
            if (functionPrefix.endsWith("."))
            {
                functionPrefix = functionPrefix.substring(0, functionPrefix.length() - 1);
            }
            int endIndex = functionPrefix.lastIndexOf(".");
            if (endIndex == -1)
            {
                functionPrefix = "";
            }
            else
            {
                functionPrefix = functionPrefix.substring(0 ,endIndex + 1);
            }
            handler = reconfiguration;
            reconfiguration = context.getReconfigFunction(functionPrefix, handler);
        }

        String functionName = "defaultname." + functionPrefix + handler;
        String parameter = "defaultname." + getName();
        return "javascript:" + functionName + "(" + parameter + ")";
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
        Link link = new Link(getName(), null);

        link.setReadOnly(getReadOnly());
        link.setUIEntityName(getUIEntityName());

        link.setValue(getValue());
        link.setDisplayValue(getDisplayValue());

        link.setHref(getHref());

        link.setListened(true);
        link.setFrameInfo(getFrameInfo());

        return link;
    }

    private static final long serialVersionUID = 8101449149686235427L;
}
