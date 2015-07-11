package org.shaolin.uimaster.html.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.javacc.Expression;
import org.shaolin.javacc.ExpressionParser;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.exception.UIComponentNotFoundException;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.widgets.HTMLCellLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.shaolin.uimaster.page.widgets.HTMLTableType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLComponentLayout extends AbstractHTMLLayout
{
    private static Logger logger = LoggerFactory.getLogger(HTMLComponentLayout.class);
    
    private int colSpan;
    
    private int rowSpan;
    
    protected String UIID = null;
    
    private Map<String, String> attributeMap;
    
    protected List componentList;

    protected Map propMap;
    
    protected Map eventMap;
    
    protected Map i18nMap;
    
    protected Map expMap;
    
    private boolean isContainer = false;
    
    protected Expression visible;
    
    public HTMLComponentLayout(String UIID, UIFormObject entity)
    {
        super(entity);
        this.UIID = UIID;
        propMap = ownerEntity.getComponentProperty(UIID);
        eventMap = ownerEntity.getComponentEvent(UIID);
        i18nMap = ownerEntity.getComponentI18N(UIID);
        expMap = ownerEntity.getComponentExpression(UIID);
    }
    
    public String getUIID()
    {
        return UIID;
    }
    
    public void setIsContainer(boolean isContainer)
    {
        this.isContainer = isContainer;
    }
    
    /*
     * Do layout constraint transfer for each component, just copy value
     * to corresponding attribute.
     *
     */
    public void setConstraints(TableLayoutConstraintType constraint,  ParsingContext parsingContext)
    {
        attributeMap = new HashMap();
        
        this.cellX = constraint.getX();
        this.cellY = constraint.getY();
        this.colSpan = constraint.getWidth();
        if ( colSpan > 1 )
        {
            attributeMap.put("colSpan", String.valueOf(colSpan));
        }
        this.rowSpan = constraint.getHeight();
        if ( rowSpan > 1 )
        {
            attributeMap.put("rowSpan", String.valueOf(rowSpan));
        }
        if ( constraint.getAlign() != null )
        {
            String align = constraint.getAlign().toString();
            if ( align != null && !align.equals("full"))
            {
                attributeMap.put("align", align);
            }
        }
        if ( constraint.getValign() != null )
        {
            String valign = constraint.getValign().toString();
            if ( valign != null && valign.equals("center") )
            {
                valign = "middle";
            }
            if ( valign != null && !valign.equals("full"))
            {
                attributeMap.put("valign", valign);
            }
        }
        String cellUIStyle = constraint.getCellUIStyle();
        if ( cellUIStyle != null && !cellUIStyle.equals("") )
        {
            attributeMap.put("cellUIStyle", cellUIStyle);
        }
        else
        {
            attributeMap.put("cellUIStyle", "");
        }
        String cellUIClass = constraint.getCellUIClass();
        if ( cellUIClass != null && !cellUIClass.equals(""))
        {
            attributeMap.put("cellUIClass", cellUIClass);
        }
        String bgColor = constraint.getBgColor();
        if ( bgColor != null && !bgColor.equals(""))
        {
            attributeMap.put("bgColor", bgColor);
        }
        
        try
        {
    		String visibleStr = constraint.getVisible();
    		visible = ExpressionParser.parse(visibleStr, parsingContext);
        }
        catch (ParsingException e)
        {
            logger.error("<---HTMLComponentLayout.generate--->Exception occured when pass the expression for the attribute:", e);
        }
        
    }

    public void addComponent(HTMLComponentLayout component)
    {
        if ( componentList == null )
        {
            componentList = new ArrayList();
        }
        componentList.add(component);
        if ( component.isContainer )
        {
            this.isContainer = true;
        }
    }
    
    public void removeUsed(Map componentMap)
    {
        componentMap.remove(UIID);
        if ( componentList != null )
        {
            for ( int i = 0, length = componentList.size(); i < length; i++ )
            {
                HTMLComponentLayout comp = (HTMLComponentLayout)componentList.get(i);
                comp.removeUsed(componentMap);
            }
        }
    }

    public int getColSpan()
    {
        return colSpan;
    }

    public int getRowSpan()
    {
        return rowSpan;
    }
    
    public void setTDAttribute(String name, String value)
    {
        attributeMap.put(name, value);
    }
    
    public void generate(HTMLSnapshotContext context, int depth, Boolean readOnly, 
            Map appendMap, VariableEvaluator ee, IUISkin uiskinObj, 
            HTMLWidgetType parentComponent, String rowUIStyle) throws JspException
    {
        HTMLLayoutType layout = HTMLUtil.getHTMLLayoutType("CellLayoutType");
        ((HTMLCellLayoutType)layout).setContainer(container);
        ((HTMLCellLayoutType)layout).setIsContainer(isContainer);

		try {
			String tempVisible = ee.evaluateExpression(visible).toString();
			layout.addAttribute("visible", tempVisible);
		} catch (EvaluationException e) {
			throw new JspException(e);
		}
        
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
        layout.addAttribute(attributeMap);
        if ( uiskinObj != null )
        {
            layout.addAttribute(uiskinObj.getAttributeMap(layout));
        }
        
        //TODO:
//        String compId = context.getHTMLPrefix() + UIID;
//        String securityVisible = null;
//        ComponentPermission cp = context.getCompPermission(compId);
//        
//        String[] viewPermission = HTMLUtil.getViewPermission(cp, ee, propMap, expMap, (Map)appendMap.get(UIID));
//        
//        if ((viewPermission != null) && (viewPermission.length > 0) && !(HTMLUtil.checkViewPermission(viewPermission)))
//        {
//            //configured view permission and owns no view permission
//            logger.debug("the user doesn't own the permission to view the component: " + compId);
//            securityVisible = "false";
//            layout.addAttribute("visible",securityVisible);
//            context.enterValueMask();
//        }
        if ( newLine && !firstLine )
        {
            HTMLUtil.generateTab(context, depth);
            context.generateHTML("<div class=\"hardreturn\">&nbsp;</div>");
        }
        
        HTMLUtil.generateTab(context, depth);
        layout.generateBeginHTML(context, this.ownerEntity, depth);
        
        generateComponentHTML(context, depth + 1, readOnly, appendMap, ee, layout);
        
        HTMLUtil.generateTab(context, depth);
        layout.generateEndHTML(context, this.ownerEntity, depth);
        
        //TODO:
//        if(securityVisible != null)
//        {
//            context.leaveValueMask();
//        }
    }
    
    public void generateComponentHTML(HTMLSnapshotContext context, int depth, Boolean readOnly, 
            Map appendMap, VariableEvaluator ee, HTMLLayoutType htmlLayout) throws JspException
    {
    	HTMLUtil.generateTab(context, depth);
        HTMLWidgetType htmlComponent;
		try {
			htmlComponent = context.getHtmlWidget(context.getHTMLPrefix() + UIID);
		} catch (UIComponentNotFoundException e1) {
			throw new JspException(e1.getMessage(), e1);
		}
    	
        Map tempValuesMap = null;
        try {
        	if (htmlComponent instanceof HTMLTableType) {
        		if (ee.getExpressionContext() instanceof OOEEContext) {
        		((OOEEContext)ee.getExpressionContext()).getEvaluationContextObject(ODContext.LOCAL_TAG)
        			.setVariableValue("tableCondition", null);
        		} else if (ee.getExpressionContext() instanceof DefaultEvaluationContext) {
        			((DefaultEvaluationContext)ee.getExpressionContext())
        			.setVariableValue("tableCondition", null);
        		}
        	}
			tempValuesMap = HTMLUtil.evaluateExpression(propMap, expMap, tempValuesMap, ee);
		} catch (EvaluationException e1) {
			logger.warn("Failed to evaluate expressions in UI widget: " + context.getHTMLPrefix() + UIID);
			throw new JspException(e1);
		}
        tempValuesMap = HTMLUtil.internationalization(propMap, i18nMap, tempValuesMap, context);
        tempValuesMap = HTMLUtil.merge(tempValuesMap, (Map)appendMap.get(UIID));
        String selfReadOnly = (String)propMap.get("readOnly");
        Boolean realReadOnly = null;
		if (selfReadOnly == null || selfReadOnly.equals("parent")) {
			realReadOnly = readOnly;
		} else if (selfReadOnly.equals("self")) {
			realReadOnly = null;
		} else {
			realReadOnly = Boolean.valueOf(selfReadOnly);
		}
        if ( logger.isTraceEnabled() )
        {
        	String flag = (realReadOnly == null ? "null" : realReadOnly.toString());
        	if (flag.equals("true")) {
	            logger.trace("The readOnly value for component: {} in the uientity: {} is true",
	                    new Object[] {UIID, ownerEntity.getName()});
        	}
        }
        
        htmlComponent.setContext(context);
        htmlComponent.setId(UIID);
        htmlComponent.setReadOnly(readOnly);
        htmlComponent.setPrefix(context.getHTMLPrefix());
        htmlComponent.setHTMLLayout(htmlLayout);
        htmlComponent.addAttribute(propMap);
        htmlComponent.addAttribute(tempValuesMap);
        htmlComponent.addEventListener(eventMap);
        htmlComponent.setFrameInfo(context.getFrameInfo());
        
        if ( htmlComponent instanceof HTMLReferenceEntityType )
        {
            ((HTMLReferenceEntityType)htmlComponent).setDepth(depth);
            ((HTMLReferenceEntityType)htmlComponent).setReconfiguration(
                    ownerEntity.getReconfigurationMap(UIID, ee), propMap, tempValuesMap);
        }
        IUISkin uiskinObj = ownerEntity.getUISkinObj(UIID, ee, htmlComponent);
        if ( uiskinObj != null )
        {
            htmlComponent.addAttribute(uiskinObj.getAttributeMap(htmlComponent));
            htmlComponent.preIncludePage(context);
            try
            {
                uiskinObj.generatePreCode(htmlComponent);
            }
            catch ( Exception e )
            {
                logger.error("uiskin error: ", e);
            }
        }
        else
        {
            htmlComponent.preIncludePage(context);
        }
        
        Widget newWidget = htmlComponent.createAjaxWidget(ee);
        if ( newWidget != null )
        {
            context.addAjaxWidget(newWidget.getId(), newWidget);
        }
        
        if ( uiskinObj == null || !uiskinObj.isOverwrite() )
        {
            htmlComponent.generateBeginHTML(context, this.ownerEntity, depth);
            htmlComponent.generateEndHTML(context, this.ownerEntity, depth);
        }

        if ( uiskinObj != null )
        {
            try
            {
                uiskinObj.generatePostCode(htmlComponent);
            }
            catch (Exception e)
            {
                logger.error("uiskin error: ", e);
            }
        }
        htmlComponent.postIncludePage(context);
        
        if ( componentList != null )
        {
            for ( int i = 0, n = componentList.size(); i < n; i++ )
            {
                HTMLComponentLayout component=(HTMLComponentLayout)componentList.get(i);                
                component.generateComponentHTML(context, depth, realReadOnly, appendMap, ee, htmlLayout);
            }
        }
    }

}
