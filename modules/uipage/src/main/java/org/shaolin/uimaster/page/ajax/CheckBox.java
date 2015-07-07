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

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class CheckBox extends SelectWidget implements Serializable
{
    private static final long serialVersionUID = 8271550039655779407L;

    public CheckBox(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public CheckBox(String id, Layout layout)
    {
        super(id, layout);
    }
   
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.checkbox({");
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
        html.append("<input type=\"checkbox\" name=\"");
        html.append(getId());
        html.append("\"");
        html.append(" id=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        generateEventListeners(html);

        if (this.isReadOnly())
        {
            html.append(" disabled=\"true\"");
        }
        html.append(" />");
        if (!this.isVisible())
        {
            html.append("<span style=\"display:none\">");
        }
        html.append("<label for=\"");
        html.append(getId());
        html.append("\">");
        if (this.isValueMask())
        {
            html.append(WebConfig.getHiddenValueMask());
        }
        else
        {
            html.append(HTMLUtil.htmlEncode(getLabel()));
        }
        html.append("</label>");
        if (!this.isVisible())
        {
            html.append("</span>");
        }
        return html.toString();
    }

}
