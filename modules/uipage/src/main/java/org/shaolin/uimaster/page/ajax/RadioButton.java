package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class RadioButton extends SelectWidget implements Serializable
{
    private static final long serialVersionUID = -2825915297099576508L;

    public RadioButton(String id, Layout layout)
    {
        super(id, layout);
    }

    public void addAttribute(String name, Object value, boolean update)
    {
        if ("selected".equals(name))
        {
            super.addAttribute(name, value, update);
            if ("true".equals(value) && AjaxActionHelper.getAjaxContext() != null)
            {
                int startIndex = this.getId().lastIndexOf("[");
                if (startIndex != -1 && this.getId().endsWith("]"))
                {
                    String startId = this.getId().substring(0, startIndex);
                    int count = 0;
                    RadioButton radio = null;
                    while ((radio = (RadioButton)AjaxActionHelper.getAjaxContext()
                            .getElementByAbsoluteId(startId + "[" + count + "]",
                                    this.getFrameInfo())) != null)
                    {
                        if (!radio.getId().equals(this.getId()))
                        {
                            radio.setSelected(false);
                        }
                        count++;
                    }
                }
            }
        }
        else if ("value".equals(name))
        {
            this.addAttribute("selected", String.valueOf(true), update);
        }
    }

    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.radiobutton({");
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
        html.append("<input type=\"radio\" name=\"");
        html.append(getId());
        html.append("\"");
        html.append(" id=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        generateEventListeners(html);

        if ( isReadOnly() )
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
