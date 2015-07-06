package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

import org.shaolin.uimaster.page.AjaxActionHelper;

public class Empty extends Widget implements Serializable
{
    private static final long serialVersionUID = -865322261124397002L;

    public Empty(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public Empty(String id, Layout layout)
    {
        super(id, layout);
    }
    
    public String generateHTML()
    {
        return "&nbsp;";
    }

}
