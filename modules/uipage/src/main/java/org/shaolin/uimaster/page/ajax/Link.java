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
