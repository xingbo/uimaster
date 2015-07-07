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

public class RadioButtonGroup extends SingleChoice implements Serializable
{
    private static final long serialVersionUID = -8629479224652156061L;

    public RadioButtonGroup(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public RadioButtonGroup(String id, Layout layout)
    {
        super(id, layout);
    }
    
    public boolean isHorizontalLayout()
    {
        return Boolean.parseBoolean((String)getAttribute("horizontalLayout"));
    }

    public void setHorizontalLayout(boolean horizontalLayout)
    {
        if(this.isHorizontalLayout() != horizontalLayout)
        {
            addAttribute("horizontalLayout", Boolean.valueOf(horizontalLayout).toString(), true);
        }
    }

    public String generateHTML()
    {
        String value = getValue();
        List displayOptions = getOptionDisplayValues();
        List options = getOptionValues();
        if (displayOptions == null)
        {
            displayOptions = options;
        }

        StringBuffer html = new StringBuffer();

        if (isVisible())
        {
            html.append("<p>");
        }
        else
        {
            html.append("<p style=\"display:none\">");
        }
        generateWidget(html);
        if (displayOptions != null && options != null)
        {
            if (displayOptions.size() == 0)
            {
                displayOptions = options;
            }

            if ( isReadOnly() )
            {
                int position = options.indexOf(value);//whether contains this value.
                if( position != -1)
                {
                    if (this.isValueMask())
                    {
                        html.append(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        html.append(HTMLUtil.htmlEncode(displayOptions.get(position)
                                .toString()));
                    }
                    html.append("<input type=\"hidden\" name=\"");
                    html.append(getId());
                    html.append("\"");
                    html.append(" value=\"");
                    if (this.isValueMask())
                    {
                        html.append(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        html.append(HTMLUtil.formatHtmlValue(value));
                    }
                    html.append("\"");
                    generateAttributes(html);
                    generateEventListeners(html);
                    html.append(" />");
                }
                else
                {
                    html.append("<input type=\"hidden\" name=\"");
                    html.append(getId());
                    html.append("\"");
                    html.append(" value='' />");
                }
            }
            else
            {
                boolean horizontalLayout = Boolean
                        .parseBoolean((String)getAttribute("horizontalLayout"));
                for (int i = 0; i < displayOptions.size(); i++)
                {
                    String entryValue = this.isValueMask() ? WebConfig.getHiddenValueMask() : 
                    	HTMLUtil.formatHtmlValue(options.get(i).toString());
                    String entryDisplayValue = this.isValueMask() ? WebConfig.getHiddenValueMask() : 
                    	HTMLUtil.htmlEncode(displayOptions.get(i).toString());
                    html.append("<input type=\"radio\" name=\"");
                    html.append(getId());
                    html.append("\"");
                    html.append(" id=\"");
                    html.append(entryValue);
                    html.append("\"");
                    html.append("value=\"");
                    html.append(entryValue);
                    html.append("\"");
                    generateAttributes(html);
                    generateEventListeners(html);
                    if (value != null && value.equalsIgnoreCase(options.get(i).toString()))
                    {
                        html.append(" checked");
                    }
                    html.append(" />");
                    html.append("<label for=\"");
                    html.append(entryValue);
                    html.append("\">");
                    html.append(entryDisplayValue);
                    html.append("</label>");
                    if (!horizontalLayout)
                    {
                        html.append("<br />");
                    }
                }
            }
        }
        html.append("</p>");

        return html.toString();
    }

    public void addConstraint(String name, Object[] value, String message)
    {
        if (name != null)
        {
            if (name.toLowerCase().equals("mustcheck"))
            {
                if (message != null)
                {
                    super.addConstraint("mustCheckText", "'"+packMessageText(message)+"'", false);
                }
                super.addConstraint("mustCheck", joinArray(value));
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
        js.append("=new UIMaster.ui.radiobuttongroup({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }
}
