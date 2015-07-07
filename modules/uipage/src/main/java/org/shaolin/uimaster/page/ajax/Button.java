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

public class Button extends TextWidget implements Serializable
{
    private static final long serialVersionUID = 6492151356109754310L;
    
    public static final String NORMAL_BUTTON = "button";
    
    public static final String SUBMIT_BUTTON = "submit";
    
    public static final String RESET_BUTTON = "reset";
    
    private String buttonType = NORMAL_BUTTON;

    public Button(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }
    
    public Button(String uiid, String title)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, title, new CellLayout());
        this.setListened(true);
    }
    
    public Button(String id, Layout layout)
    {
        super(id, layout);
    }

    /**
     * Button types
     * <ul>
     * <li>button</li>
     * <li>submit</li>
     * <li>reset</li>
     * </ul>
     * @param buttonType
     */
    public void setButtonType(String buttonType)
    {
        this.buttonType = buttonType;
        addAttribute("buttonType", buttonType);
    }

    public String getButtonType()
    {
        return buttonType;
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.button({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();
        
        generateWidget(html);
        html.append("<input type=\"");
        html.append(buttonType);
        html.append("\" name=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        generateEventListeners(html);
        html.append(" value=\"");
        String value = HTMLUtil.formatHtmlValue(getValue());
        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : value);
        html.append("\"");
        if ( isReadOnly() )
        {
            html.append(" disabled=\"true\"");
        }
        html.append(" />");
        
        return html.toString();
    }

}
