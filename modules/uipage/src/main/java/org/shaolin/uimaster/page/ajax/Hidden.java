package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;

public class Hidden extends TextWidget implements Serializable
{
    private static final long serialVersionUID = 7431046276471658474L;

    public Hidden(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public Hidden(String uiid, String value)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, value, new CellLayout());
        this.setListened(true);
    }
    
    public Hidden(String id, Layout layout)
    {
        super(id, layout);
    }
    
    public void setValue(String value)
    {
        addAttribute("value", value);
    }

    public String getValue()
    {
    	if (this.isSecure()) {
    		
    	}
        return (String)this.getAttribute("value");
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.hidden({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        html.append("<input type=\"hidden\" name=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        generateEventListeners(html);
        html.append(" value=\"");
        html.append(HTMLUtil.formatHtmlValue(getValue()));
        html.append("\" />");
        
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
