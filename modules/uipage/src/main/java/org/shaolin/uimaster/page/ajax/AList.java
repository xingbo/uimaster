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

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class AList extends MultiChoice implements Serializable
{
    private static final long serialVersionUID = 5923914834585542330L;

    private boolean multiple = true;

    public AList(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public AList(String id, Layout layout)
    {
        super(id, layout);
    }

    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
        addAttribute("multiple", multiple?Boolean.TRUE:Boolean.FALSE);
    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public void addConstraint(String name, Object[] value, String message)
    {
        if (name != null)
        {
            if (name.toLowerCase().equals("selectvalue"))
            {
                if (message != null)
                {
                    super.addConstraint("selectValueText", "'"+packMessageText(message)+"'", false);
                }
                super.addConstraint("selectValue", joinArray(value));
            }
            else
            {
                super.addConstraint(name, value, message);
            }
        }
    }

    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.list({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }

    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        html.append("<select name=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        if (multiple)
        {
            html.append(" multiple");
        }
        generateEventListeners(html);
        html.append(">");

        List displayOptions = getOptionDisplayValues();
        List options = getOptionValues();
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
            for (int i = 0; i < displayOptions.size(); i++)
            {
                html.append("<option value=\"");
                if (this.isValueMask())
                {
                    html.append(WebConfig.getHiddenValueMask());
                }
                else
                {
                    String optionValue = (options.get(i) == null) ? "_null" : String
                            .valueOf(options.get(i));
                    html.append(HTMLUtil.formatHtmlValue(optionValue));
                }
                html.append("\"");
                if (equal((String)options.get(i)))
                {
                    html.append(" selected");
                }
                html.append(">");
                if (this.isValueMask())
                {
                    html.append(WebConfig.getHiddenValueMask());
                }
                else
                {
                    html.append(HTMLUtil.formatHtmlValue(displayOptions.get(i) == null ? "_null"
                            : String.valueOf(displayOptions.get(i))));
                }
                html.append("</option>");
            }
        }
        html.append("</select>");

        return html.toString();
    }

}
