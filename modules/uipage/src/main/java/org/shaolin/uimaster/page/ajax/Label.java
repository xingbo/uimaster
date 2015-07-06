
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class Label extends TextWidget implements Serializable
{
    private static final long serialVersionUID = -7331212213593911769L;
    
    private String displayValue;

    public Label(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }
    
    public Label(String uiid, String title)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, title, new CellLayout());
        this.setListened(true);
    }
    
    public Label(String id, Layout layout)
    {
        super(id, layout);
    }

    public void setDisplayValue(String displayValue)
    {
        this.displayValue = displayValue;
        addAttribute("displayValue",this.displayValue);
    }

    public String getDisplayValue()
    {
        return displayValue;
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.label({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        String currencySymbol = getCurrencySymbol();
        if ( currencySymbol == null || currencySymbol.equals("") )
        {
            html.append("<div>");
            generateContent(html);
            html.append("</div>");
        }
        else if ( isSymbolLeft() )
        {
            html.append("<div>");
            generateCurrencySymbol(html);
            generateContent(html);
            html.append("</div>");
        }
        else
        {
            html.append("<div>");
            generateContent(html);
            generateCurrencySymbol(html);
            html.append("</div>");
        }

        return html.toString();
    }
    
    private void generateContent(StringBuffer sb)
    {
        sb.append("<span");
        generateAttributes(sb);
        generateEventListeners(sb);
        sb.append(">");
        sb.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : HTMLUtil.htmlEncode(displayValue));
        sb.append("<input type=\"hidden\" name=\"");
        sb.append(getId());
        sb.append("\"");
        sb.append(" value=\"");
        String value = HTMLUtil.formatHtmlValue(getValue());
        sb.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : value);
        sb.append("\" />");
        sb.append("</span>");
    }

    /**
     * Whether this component can have editPermission.
     */
    public boolean isEditPermissionEnabled()
    {
        return false;
    }

}
