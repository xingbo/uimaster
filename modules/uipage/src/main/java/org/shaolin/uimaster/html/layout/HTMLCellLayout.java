package org.shaolin.uimaster.html.layout;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.shaolin.bmdp.datamodel.page.UIContainerType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLPanelType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

public class HTMLCellLayout extends HTMLLayout implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String container = "";
	
	/**
	 * only for tomcat session serialization check.
	 * @deprecated
	 */
	public HTMLCellLayout() {
		super(null, null);
	}

    public HTMLCellLayout(UIContainerType container, UIFormObject entity, OOEEContext parsingContext)
    {
        super(entity, parsingContext);
        
        init(container, null);
    }

    public void setContainer(String container)
    {
        this.container = container;
    }

    public void generateHTML(HTMLSnapshotContext context, int depth, Boolean readOnly,
            Map appendMap, VariableEvaluator ee, IUISkin uiskinObj, 
            HTMLWidgetType parentComponent) throws JspException
    {
        HTMLLayoutType htmllayout = HTMLUtil.getHTMLLayoutType("CellLayoutType");
        htmllayout.setContext(context);
        htmllayout.setParentComponent(parentComponent);
        htmllayout.addAttribute("x", "0");
        htmllayout.addAttribute("y", "0");
        htmllayout.setTableColumnCount(columnCount);
        htmllayout.setTableRowCount(rowCount);
        
        if ( uiskinObj != null )
        {
            htmllayout.addAttribute(uiskinObj.getAttributeMap(htmllayout));
        }
        
        try {
			generateHidden(context, depth + 1, appendMap, ee, htmllayout);
		} catch (EvaluationException e) {
			throw new JspException(e);
		}
        
        for ( int i = 0, n = layoutComstraintList.size(); i < n; i++ )
        {
            AbstractHTMLLayout layout = (AbstractHTMLLayout)layoutComstraintList.get(i);
            layout.setContainer(container);
            layout.generate(context, depth, readOnly, appendMap, ee, uiskinObj, parentComponent, null);
        }
        if ( layoutComstraintList.size() != 0 )
        {
            HTMLUtil.generateTab(context, depth);
            context.generateHTML("<div class=\"hardreturn\"></div>");
        }
    }

}
