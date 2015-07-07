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

public class Link extends Label implements Serializable
{
    private static final long serialVersionUID = 8559652140736414500L;
    
    private String href;

    public Link(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }
    
    public Link(String id, Layout layout)
    {
        super(id, layout);
    }

    public void setHref(String href)
    {
        this.href = href;
        addAttribute("href",this.href);
    }

    public String getHref()
    {
        return href;
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.link({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        html.append("<input type=\"hidden\" name=\"");
        html.append(getId());
        html.append("\" value=\"");
        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : HTMLUtil.formatHtmlValue(getValue()));
        html.append("\" />");
        html.append("<a href=\"");
        html.append(href);
        html.append("\"");
        generateAttributes(html);
        generateEventListeners(html);
        html.append(">");
        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : HTMLUtil.formatHtmlValue(getDisplayValue()));
        html.append("</a>");

        return html.toString();
    }

    /**
     * Whether this component can have editPermission.
     */
    public boolean isEditPermissionEnabled()
    {
        return false;
    }

}
