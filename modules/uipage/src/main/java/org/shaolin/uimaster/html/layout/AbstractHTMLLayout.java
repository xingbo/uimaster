package org.shaolin.uimaster.html.layout;

import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

abstract public class AbstractHTMLLayout
{
    private static Logger logger = Logger.getLogger(AbstractHTMLLayout.class);

    protected UIFormObject ownerEntity = null;
    
    protected int cellX;
    
    protected int cellY;
    
    protected String colWidth = null;
    
    protected String rowHeight = null;
    
    protected int colCount;
    
    protected int rowCount;
    
    protected boolean newLine = false;
    
    protected boolean firstLine = false;
    
    protected String container = "";
    
    public AbstractHTMLLayout(UIFormObject entity)
    {
        this.ownerEntity = entity;
    }
    
    public void setCellX(int cellX)
    {
        this.cellX = cellX;
    }
    
    public void setCellY(int cellY)
    {
        this.cellY = cellY;
    }

    public void setColWidth(String colWidth)
    {
        this.colWidth = colWidth;
    }

    public void setRowHeight(String rowHeight)
    {
        this.rowHeight = rowHeight;
    }
    
    public void setColCount(int colCount)
    {
        this.colCount = colCount;
    }
    
    public void setRowCount(int rowCount)
    {
        this.rowCount = rowCount;
    }
    
    public void setNewLine()
    {
        newLine = true;
    }

    public void setFirstLine()
    {
        firstLine = true;
    }
    
    public void setContainer(String container)
    {
        this.container = container;
    }
    
    abstract public void generate(HTMLSnapshotContext context, int depth, Boolean readOnly, 
            Map appendMap, VariableEvaluator ee, IUISkin uiskinObj, 
            HTMLWidgetType parentComponent, String rowUIStyle) throws JspException;
    
}
