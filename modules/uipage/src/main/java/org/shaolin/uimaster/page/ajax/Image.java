package org.shaolin.uimaster.page.ajax;

import java.io.File;
import java.io.Serializable;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class Image extends TextWidget implements Serializable
{
    private static final long serialVersionUID = 3140747849841049235L;
    
    private String src;
    
    private boolean isgallery;

    public Image(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }
    
    public Image(String uiid, String src)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setSrc(src);
        this.setListened(true);
    }
    
    public Image(String id, Layout layout)
    {
        super(id, layout);
    }

    public void setIsGallery(boolean isgallery) {
    	this.isgallery = isgallery;
    }
    
    public void setSrc(String src)
    {
    	if(src == null)
    	{
    		this.src = "";
    	}
    	else
    	{
    		String webRoot = HTMLUtil.getWebRoot();
    		StringBuffer sb = new StringBuffer(webRoot);
    		
    		String uientityName = getUIEntityName();
    		String imgRoot;
    		if ( uientityName != null )
    		{
    			imgRoot = WebConfig.getResourceContextRoot() + "/images";
    		}
    		else
    		{
    			imgRoot = "/images";
    		}
    		
    		if ( !webRoot.equals(imgRoot) )
    		{
    			sb.append(imgRoot);
    		}
    		if ( !src.startsWith("/") )
    		{
    			sb.append("/");
    		}
    		sb.append(src);
    		this.src = sb.toString();
    	}
        addAttribute("src",this.src);
    }

    public String getSrc()
    {
        return src;
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.image({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        
        if (isgallery) {
        	html.append("<div id=\"");
        	html.append(getId());
            html.append("\">");
            String path = this.getAttribute("src").toString();
            
            String root = WebConfig.getResourceContextRoot();
            File directory = new File(WebConfig.getResourcePath() + path);
            if (directory.exists()) {
            	String[] images = directory.list();
            	for (String i : images) {
            		String item = root + path + "/" +  i;
            		html.append("<a href=\"" + item + "\"><img src=\"" + item + "\"/></a>");
            	}
            }
            html.append("</div>");
        } else {
	        html.append("<input type=\"hidden\" name=\"");
	        html.append(getId());
	        html.append("\">");
	        html.append("<img");
	        html.append(" src=\"");
	        html.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : src);
	        html.append("\"");
	        generateAttributes(html);
	        generateEventListeners(html);
	        html.append(" />");
        }
        
        return html.toString();
    }
    
    public void refresh() {
    	
    	
    }

    /**
     * Whether this component can have editPermission.
     */
    public boolean isEditPermissionEnabled()
    {
        return false;
    }

}
