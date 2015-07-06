package org.shaolin.uimaster.html.layout;

import java.util.Map;

import javax.servlet.jsp.JspException;

import org.shaolin.bmdp.datamodel.page.UIContainerType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

public class HTMLTableLayout extends HTMLLayout
{
    public HTMLTableLayout(UIContainerType container, UIFormObject entity, OOEEContext parsingContext)
    {
        super(entity, parsingContext);
        
        init(container, null, true);
    }
    
    public HTMLTableLayout(UIContainerType container, UIFormObject entity, String cellUIStyle, OOEEContext parsingContext)
    {
        super(entity, parsingContext);
        
        init(container, cellUIStyle, true);
    }
    
    public void generateHTML(HTMLSnapshotContext context, int depth, Boolean readOnly, 
            Map appendMap, VariableEvaluator ee, IUISkin uiskinObj,
            HTMLWidgetType parentComponent, String rowUIStyle) throws JspException
    {
        for ( int i = 0, n = layoutComstraintList.size(); i < n; i++ )
        {
            AbstractHTMLLayout layout = (AbstractHTMLLayout)layoutComstraintList.get(i);
            layout.generate(context, depth, readOnly, appendMap, ee, uiskinObj, parentComponent, rowUIStyle);
        }
        if ( layoutComstraintList.size() != 0 )
        {
            HTMLUtil.generateTab(context, depth);
            context.generateHTML("</tr>");
        }
    }

}
