package org.shaolin.uimaster.page.ajax;

import java.awt.ComponentOrientation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.widgets.HTMLDynamicUIItem;

public class Panel extends Container implements Serializable
{
    private static final long serialVersionUID = -1544166229921639074L;

    private String background;

    private boolean hasDiv;
    
    private boolean hasErrorMessage;

    private String title;

    private String UIID;

    private String divPrefix = "";

    private List<CellLayout> layoutList;

    private int[] layoutSeq;

    public Panel(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        setUIID(uiid);
        this.addAttribute("class", "uimaster_panel");
        this.getHtmlLayout().addAttribute("class", "uimaster_container_cell");
        this.setSize(0, 1);
        this.setListened(true);
    }
    
    public Panel(String uiid, String title)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        setUIID(uiid);
        this.addAttribute("class", "uimaster_panel");
        this.getHtmlLayout().addAttribute("class", "uimaster_container_cell");
        this.setSize(0, 1);
        this.setTitle(title);
        this.setListened(true);
    }

    public Panel(String id, Layout layout)
    {
        super(id, layout);
        layoutList = new ArrayList<CellLayout>();
        this.setSize(0, 1);
    }

    public void addLayout(CellLayout layout)
    {
        layout.setParent(this);
        CellLayout first = layoutList.get(layout.getY());
        first.append(layout);
    }

    public CellLayout getLayout(int cIndex, int rIndex)
    {
        if ( rIndex >= layoutList.size() )
        {
            return null;
        }
        CellLayout layout = layoutList.get(rIndex);
        for ( int i = 0; i <= cIndex; i++ )
        {
            layout = layout.getNext();
            if ( layout == null )
            {
                return null;
            }
        }
        return layout;
    }

    public void setSize(int column, int row)
    {
        if (layoutSeq == null)
        {
            layoutSeq = new int[row];
            for ( int y = 0; y < row; y++ )
            {
                layoutList.add(new CellLayout());
                layoutSeq[y] = column;
            }
        }
        else
        {
            int oRow = layoutSeq.length;
            int[] temp = layoutSeq;
            layoutSeq = new int[row];
            List<CellLayout> tempList = layoutList;
            layoutList = new ArrayList<CellLayout>();
            int smaller = oRow < row ? oRow : row;
            for (int i = 0; i < smaller; i++)
            {
                layoutSeq[i] = temp[i];
                layoutList.add(tempList.get(i));
            }
            if ( oRow < row)
            {
                for (int j = 0, n = row - oRow; j < n; j++)
                {
                    layoutList.add(new CellLayout());
                    layoutSeq[j] = column;
                }
                
            }
        }
    }

    /**
     * @deprecated
     * @param background
     */
    public void setBackground(String background)
    {
        this.background = background;
    }

    /**
     * @deprecated
     * @return
     */
    public String getBackground()
    {
        return background;
    }

    /**
     * Unsupported AJAX.
     * @param hasDiv
     */
    public void setHasDiv(boolean hasDiv)
    {
        this.hasDiv = hasDiv;
    }

    public boolean isHasDiv()
    {
        return hasDiv;
    }

    public void setHasErrorMessage(boolean hasErrorMessage)
    {
    	this.hasErrorMessage = hasErrorMessage;
    }
    
    public boolean isHasErrorMessage()
    {
    	return hasErrorMessage;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
        addAttribute("title", title, true);
    }

    public void removeTitle()
    {
        this.title = "";
        removeAttribute("title");
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setUIID(String uIID)
    {
        UIID = uIID;
    }

    public String getUIID()
    {
        return UIID;
    }

    public void setDivPrefix(String divPrefix)
    {
        this.divPrefix = divPrefix;
    }

    public String getDivPrefix()
    {
        return divPrefix;
    }

    public void addAttribute(String name, Object value, boolean update)
    {
        if ( name.equals("hasDiv") )
        {
            setHasDiv("true".equals(value));
        }
        else
        {
            super.addAttribute(name, value, update);
        }
    }
    
    public void setFrameInfo(String frameinfo)
    {
        super.setFrameInfo(frameinfo);
        for ( int y = 0; y < layoutList.size(); y++ )
        {
            CellLayout layout = (layoutList.get(y)).getNext();

            while ( layout != null )
            {
                Widget comp = layout.getComponent(0);
                if (comp != null)
                {
                    comp.setFrameInfo(frameinfo);
                }
                layout = layout.getNext();
            }
        }
    }
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();

        generateWidget(html);
        if ( title != null )
        {
            html.append("<fieldset><legend>");
            html.append(title);
            html.append("</legend>");
        }
        if ( hasDiv )
        {
            html.append("<div id=\"");
            html.append(getId());
            html.append("\" class=\"panelSystemDiv\">");
        }
        html.append("<div");
        ComponentOrientation ce = ComponentOrientation.getOrientation(
            ResourceUtil.getLocaleObject(LocaleContext.getUserLocale()));
        if ( !ce.isLeftToRight() )
        {
            html.append(" dir=\"rtl\"");
        }
        if ( !hasDiv )
        {
            html.append(" id=\"");
            html.append(getId());
            html.append("\"");
        }
        generateAttributes(html);
        generateEventListeners(html);
        html.append(">");
        
        if (hasErrorMessage)
        {
        	html.append("<div id=\"");
        	html.append(getId());
        	html.append("-warn-placeholder");
        	html.append("\" class=\"err-hidden\">");
        	html.append("</div>");
        }
        
        for ( int y = 0; y < layoutList.size(); y++ )
        {
            CellLayout layout = (layoutList.get(y)).getNext();

            while ( layout != null )
            {
                html.append(layout.generateHTML());
                layout = layout.getNext();
            }
            html.append("<div class=\"hardreturn\"></div>");
        }

        html.append("</div>");
        if (hasDiv)
        {
            html.append("</div>");
        }
        if ( title != null )
        {
            html.append("</fieldset>");
        }

        return html.toString();
    }
    
    protected void generateAttribute(String name, Object value, StringBuffer sb)
    {
        if ("editable".equals(name))
        {
        }
        else
        {
            super.generateAttribute(name, value, sb);
        }
    }

    public String generateJS()
    {
        StringBuffer js = new StringBuffer(300);
        //List all components in the panel 
        List<String> subCompNameList = new ArrayList<String>();
        
        for ( int x = 0; x < layoutList.size(); x++ )
        {
            CellLayout layout = (layoutList.get(x)).getNext();

            while ( layout != null )
            {
                for ( int y = 0; y < layout.getComponentSize(); y++)
                {
                    js.append(layout.getComponent(y).generateJS());//DONT generate the js of sub comps
                    subCompNameList.add(layout.getComponent(y).getId());
                }
                layout = layout.getNext();
            }
        }
        
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.panel({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"],subComponents:[");
        for ( int i = 0, n = subCompNameList.size(); i < n; i++ )
        {
            if ( i > 0 )
            {
                js.append(",");
            }
            js.append("\"");
            js.append(subCompNameList.get(i));
            js.append("\"");
        }
        js.append("]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }
    
    public void prepend(Widget comp) 
    {
        if(comp == null)
        {
            throw new IllegalArgumentException("Prepaneded widget cannot be null!");
        }
        if(comp == this)
        {
            return;
        }
        if(comp instanceof Layout)
        {
            throw new IllegalArgumentException("Layout widget cannot be directly added!");
        }
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if(ajaxContext == null)
        {
            return;
        }
        if(this._isReadOnly())
        {
            throw new IllegalStateException("This panel is in ReadOnly mode, prepend method is locked!");
        }
        
        CellLayout layout = (CellLayout)comp.getHtmlLayout();
        String layoutID = "div-" + divPrefix + UIID + "-" + layoutSeq[0] + "_0";
        layout.setId(layoutID);
        layout.setX(layoutSeq[0]++);
        layout.setY(0);
        layout.setParent(this);
        CellLayout first = layoutList.get(0);
        layout.setNext(first.getNext());
        first.setNext(layout);
        
        if(!ajaxContext.existElement(this))// new panel, only caches sub comps without the generation immediately.
        {
            if(!ajaxContext.existElement(comp))
            {
                _addComponent(comp, ajaxContext);
            }
            return;
        }
        
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        if(ajaxContext.existElement(comp))
        {
            comp.getHtmlLayout().remove();
            comp.setFrameInfo(getFrameInfo());
        }
        else
        {
            boolean success = _addComponent(comp, ajaxContext);
            if (!success)
            {
                return;
            }
            dataItem.setData(comp.getHtmlLayout().generateHTML());
            dataItem.setJs(comp.generateJS());
        }
        dataItem.setUiid(comp.getId());
        dataItem.setParent(getId());
        dataItem.setFrameInfo(getFrameInfo());
        dataItem.setJsHandler(IJSHandlerCollections.HTML_PREPEND);
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    /**
     * append a widget in a single row.
     * 
     */
    public void appendToSingleRow(Widget comp)
    {
        if(this._isReadOnly())
        {
            throw new IllegalStateException("This panel is in ReadOnly mode, appendToSingleRow method is locked!");
        }
        
        this.newLine();
        this.append(comp);
    }
    
    public void append(Widget comp)
    {
        if(comp == null)
        {
            throw new IllegalArgumentException("Appended widget cannot be null!");
        }
        if(comp == this)
        {
            return;
        }
        if(comp instanceof Layout)
        {
            throw new IllegalArgumentException("Layout widget cannot be directly added!");
        }
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if(ajaxContext == null)
        {
            return;
        }
        if(this._isReadOnly())
        {
            throw new IllegalStateException("This panel is in ReadOnly mode, append method is locked!");
        }
        
        int rIndex = layoutList.size() - 1;
        CellLayout layout = (CellLayout)comp.getHtmlLayout();
        String layoutID = "div-" + divPrefix + UIID + "-" + layoutSeq[rIndex] + "_" + rIndex;
        layout.setId(layoutID);
        layout.setX(layoutSeq[rIndex]++);
        layout.setY(rIndex);
        layout.setParent(this);
        CellLayout first = layoutList.get(rIndex);
        first.append(layout);

        if(!ajaxContext.existElement(this))// new panel,
        {
            if(!ajaxContext.existElement(comp))
            {
                _addComponent(comp, ajaxContext);
            }
            return;
        }
        
        IDataItem dataItem = AjaxActionHelper.createDataItem();
//        if(ajaxContext.existElement(comp))
//        {
//            comp.getHtmlLayout().remove();
//            comp.setFrameInfo(getFrameInfo());
//        }
//        else
//        {
        boolean success = _addComponent(comp, ajaxContext);
        if (!success)
        {
            return;
        }
        dataItem.setData(comp.getHtmlLayout().generateHTML());
        dataItem.setJs(comp.generateJS());
//        }
        
        dataItem.setUiid(comp.getId());
        dataItem.setParent(getId());
        dataItem.setFrameInfo(getFrameInfo());
        dataItem.setJsHandler(IJSHandlerCollections.HTML_APPEND);
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    public void newLine()
    {
        layoutList.add(new CellLayout());
        int[] tempSeq = layoutSeq;
        layoutSeq = new int[tempSeq.length + 1];
        for ( int i = 0; i < tempSeq.length; i++ )
        {
            layoutSeq[i] = tempSeq[i];
        }
        layoutSeq[tempSeq.length] = 0;

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if(ajaxContext != null && ajaxContext.existElement(this))
        {
            IDataItem dataItem = AjaxActionHelper.createDataItem();
            dataItem.setJsHandler(IJSHandlerCollections.HARDRETURN);
            dataItem.setUiid(this.getId());
            dataItem.setData("<div class=\"hardreturn\"></div>");
            dataItem.setFrameInfo(getFrameInfo());
            ajaxContext.addDataItem( dataItem );
        }
    }

    void setIDForNewLayout(CellLayout layout)
    {
        int rIndex = layout.getY();
        int cIndex = layoutSeq[rIndex]++;
        String layoutID = "div-" + divPrefix + UIID + "-" + cIndex + "_" + rIndex;
        layout.setId(layoutID);
    }

    private List<HTMLDynamicUIItem> items;
    
    public boolean hasDynamicUI() {
    	return items != null;
    }
    
    public void setDynamicUI(List<HTMLDynamicUIItem> items) {
    	this.items = items;
    }
    
    public String retriveData() {
    	if (items == null) {
    		return "";
    	}
    	int i = 0;
    	StringBuffer sb = new StringBuffer();
    	sb.append("[");
    	for (HTMLDynamicUIItem item : items) {
    		String uiid = this.getId() + "-dynamicUI" + i++;
    		sb.append(item.retriveData(uiid)).append(",");
    	}
    	sb.deleteCharAt(sb.length() - 1);
    	sb.append("]");
    	return sb.toString();
    }
    
}
