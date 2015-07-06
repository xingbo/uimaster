package org.shaolin.uimaster.html.layout;

import java.util.Map;

import javax.servlet.jsp.JspException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.widgets.HTMLCellLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;

public class HTMLEmptyLayout extends AbstractHTMLLayout
{
    
    public HTMLEmptyLayout(UIFormObject entity)
    {
        super(entity);
    }
    
    public void generate(HTMLSnapshotContext context, int depth, Boolean readOnly, 
            Map appendMap, VariableEvaluator ee, IUISkin uiskinObj,
            HTMLWidgetType parentComponent, String rowUIStyle) throws JspException
    {
        HTMLLayoutType layout = HTMLUtil.getHTMLLayoutType("CellLayoutType");
        ((HTMLCellLayoutType)layout).setContainer(container);
        
        layout.setContext(context);
        layout.setParentComponent(parentComponent);
        layout.setTableColumnCount(colCount);
        layout.setTableRowCount(rowCount);
        layout.addAttribute("x", String.valueOf(cellX));
        layout.addAttribute("y", String.valueOf(cellY));
        if ( colWidth != null )
        {
            layout.addAttribute("width", colWidth);
        }
        if ( rowHeight != null )
        {
            layout.addAttribute("height", rowHeight);
        }
        layout.addAttribute("cellUIStyle", "");
        if ( uiskinObj != null )
        {
            layout.addAttribute(uiskinObj.getAttributeMap(layout));
        }
        if ( newLine && !firstLine )
        {
            HTMLUtil.generateTab(context, depth);
            context.generateHTML("<div class=\"hardreturn\"></div>");
        }
        
        HTMLUtil.generateTab(context, depth);
        layout.generateBeginHTML(context, this.ownerEntity, depth);

        HTMLUtil.generateTab(context, depth + 1);
        context.generateHTML("&nbsp;");

        HTMLUtil.generateTab(context, depth);
        layout.generateEndHTML(context, this.ownerEntity, depth);
    }

}
