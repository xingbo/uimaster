package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.List;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class CheckBoxGroup extends MultiChoice implements Serializable
{
    private static final long serialVersionUID = -5579954616879516797L;

    public CheckBoxGroup(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public CheckBoxGroup(String id, Layout layout)
    {
        super(id, layout);
    }

    public boolean isVerticalLayout()
    {
        return Boolean.parseBoolean((String)getAttribute("verticalLayout"));
    }

    public void setVerticalLayout(boolean verticalLayout)
    {
        if(this.isVerticalLayout() != verticalLayout)
        {
            addAttribute("verticalLayout", Boolean.valueOf(verticalLayout).toString(), true);
        }
    }

    public String generateHTML()
    {
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
            if (isReadOnly())
            {
                String value = "";
                StringBuffer valueBuf = new StringBuffer(512);
                if (this.isValueMask())
                {
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        if (equal((String)options.get(i)))
                        {
                            valueBuf.append(WebConfig.getHiddenValueMask());
                            valueBuf.append(",");
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        if (equal((String)options.get(i)))
                        {
                            valueBuf.append(displayOptions.get(i).toString());
                            valueBuf.append(",");
                        }
                    }
                }
                if (valueBuf.length() > 0)
                {
                    value = valueBuf.substring(0, valueBuf.length() - 1);
                }
                html.append(HTMLUtil.htmlEncode(value));
                html.append("<input type=\"hidden\" name=\"");
                html.append(getId());
                html.append("\" value=\"");
                html.append(HTMLUtil.formatHtmlValue(value));
                html.append("\"");
                generateAttributes(html);
                generateEventListeners(html);
                html.append(" />");
            }
            else
            {
                boolean verticalLayout = Boolean
                        .parseBoolean((String)getAttribute("verticalLayout"));
                for (int i = 0; i < displayOptions.size(); i++)
                {
                    String entryValue = this.isValueMask() ? WebConfig.getHiddenValueMask() : 
                    	HTMLUtil.formatHtmlValue(options.get(i).toString());
                    String entryDisplayValue = this.isValueMask() ? WebConfig.getHiddenValueMask() : 
                    	HTMLUtil.htmlEncode(displayOptions.get(i).toString());
                    html.append("<input type=\"checkbox\" name=\"");
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
                    if (equal((String)options.get(i)))
                    {
                        html.append(" checked");
                    }
                    html.append(" />");
                    html.append("<label for=\"");
                    html.append(entryValue);
                    html.append("\">");
                    html.append(entryDisplayValue);
                    html.append("</label>");
                    if (verticalLayout)
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
    
    public Object removeConstraint(String name)
    {
        if (name.toLowerCase().equals("mustcheck"))
        {
            return super.removeConstraint("mustCheck");
        }
        else
        {
            return super.removeConstraint(name);
        }
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.checkboxgroup({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }
}
