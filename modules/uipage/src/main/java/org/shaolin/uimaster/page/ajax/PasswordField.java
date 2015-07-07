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

public class PasswordField extends TextField implements Serializable
{
    private static final long serialVersionUID = 1514022479855468348L;

    public PasswordField(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }

    public PasswordField(String id, Layout layout)
    {
        super(id, layout);
        this._setWidgetLabel(id);
    }

    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.passwordfield({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJSFromSuper());
        js.append("});");
        return js.toString();
    }

    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        html.append("<input type=\"password\" name=\"");
        html.append(getId());
        html.append("\" class=\"");
        if (isReadOnly())
        {
            html.append("uimaster_passwordField_readOnly ");
            if (cssClass != null && !cssClass.trim().equals("null"))
            {
                html.append(cssClass);
            }
            html.append("\" readOnly=\"true\" ");
            addAttribute("allowBlank", "true", false);
        }
        else
        {
            html.append("uimaster_passwordField ");
            if (cssClass != null && !cssClass.trim().equals("null"))
            {
                html.append(cssClass);
            }
            html.append("\"");
        }
        generateAttributes(html);
        generateEventListeners(html);
        html.append(" value=\"");
        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : HTMLUtil.formatHtmlValue(getValue()));
        html.append("\" />");

        return html.toString();
    }

}
