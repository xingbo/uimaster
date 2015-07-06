package org.shaolin.uimaster.page.ajax;

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class TextField extends TextWidget implements java.io.Serializable
{
    private static final long serialVersionUID = 3270946912858832750L;

    protected String cssClass;

    public TextField(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }
    
    public TextField(String uiid, String value)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, value, new CellLayout());
        this._setWidgetLabel(uiid);
        this.setListened(true);
    }

    public TextField(String id, Layout layout)
    {
        super(id, layout);
        this._setWidgetLabel(id);
    }

    public void addAttribute(String name, Object value, boolean update)
    {
        if (name.equals("editable"))
        {
            if ( value == null ? false : !"true".equals(value) )
            {
                setReadOnly(Boolean.TRUE);
            }
        }
        else if (name.equals("prompt"))
        {
            super.addAttribute("title", value, update);
        }
        else if (name.equals("maxLength"))
        {
            super.addAttribute("maxlength", value, update);
        }
        else if (name.equals("txtFieldLength"))
        {
            super.addAttribute("size", value, update);
        }
        else if (name.equals("class"))
        {
            cssClass = (String)value;
            super.addAttribute("class", cssClass, update);
        }
        else
        {
            super.addAttribute(name, value, update);
        }
    }

    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.textfield({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }

    protected String generateJSFromSuper()
    {
        return super.generateJS();
    }

    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();
        generateWidget(html);
        String currencySymbol = getCurrencySymbol();
        if (currencySymbol == null || currencySymbol.equals(""))
        {
            generateContent(html);
        }
        else if (isSymbolLeft())
        {
            generateCurrencySymbol(html);
            generateContent(html);
        }
        else
        {
            generateContent(html);
            generateCurrencySymbol(html);
        }

        return html.toString();
    }

    protected void generateAttribute(String name, Object value, StringBuffer sb)
    {
        if ("editable".equals(name))
        {
            if ("false".equals(String.valueOf(value)))
            {
                sb.append(" readOnly=\"true\"");
            }
        }
        else if ("maxLength".equals(name))
        {
            sb.append(" maxlength=\"");
            sb.append(value);
            sb.append("\"");
        }
        else if ("txtFieldLength".equals(name))
        {
            sb.append(" size=\"");
            sb.append(value);
            sb.append("\"");
        }
        else if ("prompt".equals(name))
        {
            String attrValue = (String)value;
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
    private void generateContent(StringBuffer sb)
    {
        sb.append("<input type=\"text\" name=\"");
        sb.append(getId());
        sb.append("\" class=\"");
        if (isReadOnly())
        {
            sb.append("uimaster_textField_readOnly ");
            if (cssClass != null && !cssClass.trim().equals("null"))
            {
                sb.append(cssClass);
            }
            sb.append("\" readOnly=\"true\"");
            addAttribute("allowBlank", "true", false);
        }
        else
        {
            sb.append("uimaster_textField ");
            if (cssClass != null && !cssClass.trim().equals("null"))
            {
                sb.append(cssClass);
            }
            sb.append("\"");
        }
        generateAttributes(sb);
        generateEventListeners(sb);
        sb.append(" value=\"");
        String value = HTMLUtil.formatHtmlValue(getValue());
        sb.append(this.isValueMask() ? WebConfig.getHiddenValueMask() : value);
        sb.append("\" />");
    }
}
