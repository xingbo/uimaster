package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class AFile extends TextWidget implements Serializable
{
    private static final long serialVersionUID = -8002328531496168701L;

    private String storedPath = null;
    
    private String suffix = "";
    
	public AFile(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }
    
    public AFile(String uiid, String path)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, path, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }
    
    public AFile(String id, Layout layout)
    {
        super(id, layout);
        this._setWidgetLabel(id);
    }
    
    public void setStoredPath(String storedPath) {
    	this.storedPath = storedPath;
    }
    
    public String getStoredPath() {
    	return this.storedPath;
    }
    
    public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.file({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        html.append("<script language=\"javascript\">document.forms[0].encoding=\"multipart/form-data\";</script>");
        html.append("<input type=\"file\" name=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        generateEventListeners(html);
        html.append(" value=\"");
        String value = HTMLUtil.formatHtmlValue(getValue());
        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : value);
        html.append("\" />");  
        
        return html.toString();
    }

}
