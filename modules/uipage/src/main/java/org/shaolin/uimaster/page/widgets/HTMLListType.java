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
import java.util.List;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.AList;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLListType extends HTMLMultiChoiceType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLListType.class);

    public HTMLListType()
    {
    }

    public HTMLListType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLListType(HTMLSnapshotContext context, String id)
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
            generateWidget(context);
            context.generateHTML("<select name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            context.generateHTML(" id=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(">");

            List<String> displayOptions = getOptionDisplayValues();
            List<String> options = getOptionValues();
            if (displayOptions == null)
            {
                displayOptions = options;
            }
            if (displayOptions != null && options != null)
            {
                if (displayOptions.size() == 0)
                {
                    displayOptions = options;
                }
                if (displayOptions.size() != options.size())
                {
                    logger.error("display size && output size not equal!", new Exception());
                }
                for (int i = 0; i < displayOptions.size(); i++)
                {
                    context.generateHTML("<option value=\"");
                    if (context.isValueMask())
                    {
                        context.generateHTML(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        String optionValue = (options.get(i) == null) ? "_null" : String
                                .valueOf(options.get(i));
                        context.generateHTML(HTMLUtil.formatHtmlValue(optionValue));
                    }
                    context.generateHTML("\"");
                    if (equal((String)options.get(i)))
                    {
                        context.generateHTML(" selected");
                    }
                    context.generateHTML(">");
                    if (context.isValueMask())
                    {
                        context.generateHTML(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        context.generateHTML(HTMLUtil
                                .formatHtmlValue(displayOptions.get(i) == null ? "_null" : String
                                        .valueOf(displayOptions.get(i))));
                    }
                    context.generateHTML("</option>");
                }
            }
            context.generateHTML("</select>");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName,
            Object attributeValue) throws IOException
    {
        if ("multiple".equals(attributeName))
        {
            if ("true".equals(String.valueOf(getAllAttribute("multiple"))))
            {
                context.generateHTML(" multiple");
            }
        }
        else if ("size".equals(attributeName))
        {
            context.generateHTML(" size=" + String.valueOf(getAllAttribute("size")));
        } 
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        AList list = new AList(getName(), null);

        list.setReadOnly(getReadOnly());
        list.setUIEntityName(getUIEntityName());

        list.setOptions(getOptionDisplayValues(), getOptionValues());
        list.setValues(getValue());

        setAJAXConstraints(list);
        setAJAXAttributes(list);
        
        list.setListened(true);
        list.setFrameInfo(getFrameInfo());

        return list;
    }

    private static final long serialVersionUID = -7867495752450617201L;
	
}
