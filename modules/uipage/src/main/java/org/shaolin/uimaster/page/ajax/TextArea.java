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

public class TextArea extends TextWidget implements Serializable
{
    private static final long serialVersionUID = 7184128621017147296L;
    
    private String cssClass;

    public TextArea(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }
    
    public TextArea(String uiid, String value)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, value, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }

    public TextArea(String id, Layout layout)
    {
        super(id, layout);
        this._setWidgetLabel(id);
    }

    public void addAttribute(String name, Object value, boolean update)
    {
        if ( name.equals("editable") )
        {
            if ( value == null ? false : !"true".equals(value) )
            {
                setReadOnly(Boolean.TRUE);
            }
        }
        else if ( name.equals("prompt") )
        {
            super.addAttribute("title", value, true);
        }
        else if ( name.equals("class") )
        {
            cssClass = (String)value;
            super.addAttribute("class", cssClass, true);
        }
        else
        {
            super.addAttribute(name, value, update);
        }
    }
    
    public void setRows(int row)
    {
        super.addAttribute("rows", row);
    }
    
    public void setCols(int col)
    {
        super.addAttribute("cols", col);
    }
    
    public void append(String str)
    {
        String body = this.getValue()+str;
        this.setValue(body);
    }
    
    public void clear()
    {
        this.setValue("");
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.textarea({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }
    
    protected void generateAttribute(String name, Object value, StringBuffer sb)
    {
        String attrValue = (String)value;
        if ("editable".equals(name))
        {
            if ("false".equals(String.valueOf(attrValue)))
            {
                sb.append(" readOnly=\"true\"");
            }
        }
        else if ("prompt".equals(name))
        {
            if ( attrValue != null && !attrValue.trim().equals("") )
            {
                sb.append(" title=\"");
                sb.append(attrValue);
                sb.append("\"");
            }
        }
        else
        {
            super.generateAttribute(name, value, sb);
        }
    }
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        html.append("<textarea name=\"");
        html.append(getId());
        html.append("\" class=\"");
        if ( isReadOnly() )
        {
            html.append("uimaster_textArea_readOnly ");
            if ( cssClass != null && !cssClass.trim().equals("null") )
            {
                html.append(cssClass);
            }
            html.append("\" readOnly=\"true\"");
            addAttribute("allowBlank", "true", false);
        }
        else
        {
            html.append("uimaster_textArea ");
            if ( cssClass != null && !cssClass.trim().equals("null") )
            {
                html.append(cssClass);
            }
            html.append("\"");
        }
        html.append("\"");
        generateAttributes(html);
        if ( getAttribute("rows") == null )
        {
            html.append(" rows=\"4\"");
        }
        if ( getAttribute("cols") == null )
        {
            html.append(" cols=\"30\"");
        }
        generateEventListeners(html);
        html.append(">");
        String value = HTMLUtil.formatHtmlValue(getValue());
        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : value);
        html.append("</textarea>");

        return html.toString();
    }

}
