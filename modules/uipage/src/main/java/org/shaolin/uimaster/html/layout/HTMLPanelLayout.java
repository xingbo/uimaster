package org.shaolin.uimaster.html.layout;

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.exception.UIComponentNotFoundException;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.widgets.HTMLDynamicUIItem;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLPanelType;

public class HTMLPanelLayout extends HTMLComponentLayout
{
    private static Logger logger = Logger.getLogger(HTMLPanelLayout.class);

    private HTMLCellLayout layout = null;
    
    public HTMLPanelLayout(String UIID, UIFormObject entity)
    {
        super(UIID, entity);
    }

    public void setBody(UIPanelType panel, OOEEContext parsingContext)
    {
        layout = new HTMLCellLayout(panel, ownerEntity, parsingContext);
        layout.setContainer(UIID + "-");
    }

    public void generateComponentHTML(HTMLSnapshotContext context, int depth, Boolean readOnly,
            Map appendMap, VariableEvaluator ee, HTMLLayoutType htmlLayout) throws JspException
    {
        Map tempMap = null;
        try {
			tempMap = HTMLUtil.evaluateExpression(propMap, expMap, tempMap, ee);
		} catch (EvaluationException e1) {
			throw new JspException(e1);
		}
        tempMap = HTMLUtil.internationalization(propMap, i18nMap, tempMap, context);
        tempMap = HTMLUtil.merge(tempMap, (Map)appendMap.get(UIID));
        String selfReadOnly = (String)propMap.get("readOnly");
        Boolean realReadOnly;
        if (selfReadOnly == null || selfReadOnly.equals("parent"))
        {
            realReadOnly = readOnly;
        }
        else
        {
            realReadOnly = Boolean.valueOf(ee.evaluateReadOnly(selfReadOnly));
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("<---HTMLPanelLayout.generateComponentHTML--->The readOnly value for component: "
                    + UIID
                    + " in the uientity: "
                    + ownerEntity.getName()
                    + " is "
                    + (realReadOnly == null ? "null" : realReadOnly.toString()));
        }

        HTMLPanelType uiPanel;
		try {
			uiPanel = (HTMLPanelType)context.getHtmlWidget(context.getHTMLPrefix() + UIID);
		} catch (UIComponentNotFoundException e1) {
			throw new JspException(e1.getMessage(), e1);
		}
        uiPanel.setContext(context);
        uiPanel.setId(UIID);
        uiPanel.setReadOnly(realReadOnly);
        uiPanel.setPrefix(context.getHTMLPrefix());
        uiPanel.setHTMLLayout(htmlLayout);
        uiPanel.addAttribute(propMap);
        uiPanel.addAttribute(tempMap);
        uiPanel.addEventListener(eventMap);
        uiPanel.setFrameInfo(context.getFrameInfo());
        if (uiPanel.hasDynamicUI()) {
        	String filter = (String)tempMap.get("dynamicUIFilter");
        	List<HTMLDynamicUIItem> dynamicItems = ownerEntity.getDynamicItems(UIID, filter);
        	uiPanel.setDynamicItems(ee, dynamicItems);
        }
        
        HTMLUtil.generateTab(context, depth);
        IUISkin uiskinObj = ownerEntity.getUISkinObj(UIID, ee, uiPanel);
        if (uiskinObj != null)
        {
            uiPanel.addAttribute(uiskinObj.getAttributeMap(uiPanel));
            uiPanel.preIncludePage(context);
            try
            {
                uiskinObj.generatePreCode(uiPanel);
            }
            catch (Exception e)
            {
                logger.error("uiskin error: ", e);
            }
        }
        else
        {
            uiPanel.preIncludePage(context);
        }

        Widget newWidget = uiPanel.createAjaxWidget(ee);
        if (newWidget != null) {
        	context.addAjaxWidget(newWidget.getId(), newWidget);
        }
        
        if (uiskinObj == null || !uiskinObj.isOverwrite())
        {
            uiPanel.generateBeginHTML(context, this.ownerEntity, depth);
        }

        layout.generateHTML(context, depth + 1, realReadOnly, appendMap, ee, uiskinObj,
                uiPanel);

        HTMLUtil.generateTab(context, depth);
        if (uiskinObj == null || !uiskinObj.isOverwrite())
        {
            uiPanel.generateEndHTML(context, this.ownerEntity, depth);
        }
        if (uiskinObj != null)
        {
            try
            {
                uiskinObj.generatePostCode(uiPanel);
            }
            catch (Exception e)
            {
                logger.error("uiskin error: ", e);
            }
        }
        uiPanel.postIncludePage(context);

        if (componentList != null)
        {
            for (int i = 0, n = componentList.size(); i < n; i++)
            {
                ((HTMLComponentLayout)componentList.get(i)).generateComponentHTML(context, depth,
                        realReadOnly, appendMap, ee, htmlLayout);
            }
        }
    }

}
