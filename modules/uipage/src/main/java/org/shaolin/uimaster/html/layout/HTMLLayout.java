package org.shaolin.uimaster.html.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.page.ComponentConstraintType;
import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.bmdp.datamodel.page.TableLayoutType;
import org.shaolin.bmdp.datamodel.page.UIComponentType;
import org.shaolin.bmdp.datamodel.page.UIContainerType;
import org.shaolin.bmdp.datamodel.page.UIHiddenType;
import org.shaolin.bmdp.datamodel.page.UILayoutType;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.datamodel.page.UITabPaneType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HTMLLayout
{
    private static Logger logger = LoggerFactory.getLogger(HTMLLayout.class);

    protected UIFormObject entity = null;

    protected List layoutComstraintList = null;

    protected List unUsedReferenceEntity = null;

    protected int columnCount;

    protected int rowCount;

    protected OOEEContext parsingContext;

    private List hiddenList = null;
    
    /**
	 * only for tomcat session serialization check.
	 * @deprecated
	 */
    public HTMLLayout() {}

    public HTMLLayout(UIFormObject entity, OOEEContext parsingContext)
    {
        this.entity = entity;
        this.parsingContext = parsingContext;
        layoutComstraintList = new ArrayList();
    }

    protected void init(UIContainerType container, String cellUIStyle)
    {
        init(container, cellUIStyle, false);
    }
    
    /*
     * Transfer the xml layout constraint data to java flat cell layout data, like
     * rowNumber, colNumber, eachRowHeight, eachColWidth 
     *           ---------->
     * cellX, cellY, colConut, rowCount, colWidth, rowHeight, colSpan, rowSpan
     *
     * The original unordered sequence layout constraint data in xml is also 
     * transferd to appearence order by borwser rendering sequence.
     *
     */
    protected void init(UIContainerType container, String cellUIStyle, boolean isInObjectList)
    {
    	List<UIComponentType> components = container.getComponents();
        Map componentMap = new HashMap();
        hiddenList = new ArrayList();
        for (UIComponentType comp: components)
        {
            componentMap.put(comp.getUIID(), comp);
            if (comp instanceof UIHiddenType)
            {
                hiddenList.add(comp.getUIID());
            }
        }

        UILayoutType uilayout = (UILayoutType)container.getLayout();
        if (uilayout instanceof TableLayoutType)
        {
            TableLayoutType layout = (TableLayoutType)uilayout;
            columnCount = layout.getColumnWidthWeights().size();
            rowCount = layout.getRowHeightWeights().size();
            List<ComponentConstraintType> componentConstaint = container.getLayoutConstraints();
            Map<String, HTMLComponentLayout> layoutConstraintMap = new HashMap<String, HTMLComponentLayout>();
            for (ComponentConstraintType constaint : componentConstaint)
            {
                TableLayoutConstraintType layoutConstraint = (TableLayoutConstraintType)constaint
                        .getConstraint();
                String UIID = constaint.getComponentId();
                UIComponentType component = (UIComponentType)componentMap.get(UIID);
                if (component == null) {
                	throw new IllegalStateException("The ui widget does not exist in the cache. uiid: " + UIID);
                }
                boolean isContainer = false;
                if (component instanceof UIContainerType
                        || component instanceof UIReferenceEntityType 
                        || component instanceof UITabPaneType)
                {
                    isContainer = true;
                }
                HTMLComponentLayout componentLayout = generateHTMLLayout(component,
                        layoutConstraint, cellUIStyle);
                componentLayout.setIsContainer(isContainer);
                // use a number like key for convenient sort
                String key = layoutConstraint.getX() + "." + layoutConstraint.getY();
                HTMLComponentLayout compLayout = (HTMLComponentLayout)layoutConstraintMap.get(key);
                if (compLayout != null)
                {
                    if (compLayout.getColSpan() == componentLayout.getColSpan()
                            && compLayout.getRowSpan() == componentLayout.getRowSpan())
                    {
                        compLayout.addComponent(componentLayout);
                    }
                    else if (compLayout.getColSpan() <= componentLayout.getColSpan()
                            && compLayout.getRowSpan() <= componentLayout.getRowSpan())
                    {
                        layoutConstraintMap.put(key, componentLayout);
                    }
                }
                else
                {
                    layoutConstraintMap.put(key, componentLayout);
                }
            }

            boolean[][] bitmap = new boolean[columnCount][rowCount];
            boolean newLine = false;
            for (int y = 0; y < rowCount; y++)
            {
                newLine = true;
                for (int x = 0; x < columnCount; x++)
                {
                    double columnWidth = 0;
                    double rowHeight = 0;
                    if (!bitmap[x][y])
                    {
                        // isExist[x][y] = true;
                        String key = x + "." + y;
                        HTMLComponentLayout compLayout = (HTMLComponentLayout)
                        		layoutConstraintMap.remove(key);
                        if (compLayout != null)
                        {
                            try
                            {
                                for (int k1 = 0, n1 = compLayout.getColSpan(); k1 < n1; k1++)
                                {
                                    columnWidth = getSum(columnWidth, layout.getColumnWidthWeights().get(x
                                            + k1));
                                    for (int k2 = 0, n2 = compLayout.getRowSpan(); k2 < n2; k2++)
                                    {
                                        if (k1 == 0)
                                        {
                                            rowHeight = getSum(rowHeight, layout
                                                    .getRowHeightWeights().get(y + k2));
                                        }
                                        bitmap[x + k1][y + k2] = true;
                                    }
                                }
                            }
                            catch (IndexOutOfBoundsException e)
                            {
                                logger
                                        .error("There must be something wrong with the layout constraints of component: "
                                                + container.getUIID()
                                                + " in the uientity: "
                                                + entity.getName()
                                                + "\n For example, the column count is 4, then a component is on the last column but "
                                                + "its width is 2. Please check the uientity again.");
                            }
                            compLayout.setColCount(columnCount);
                            compLayout.setRowCount(rowCount);
                            if (newLine)
                            {
                                compLayout.setNewLine();
                            }
                            if (columnWidth >= 0)
                            {
                                compLayout.setColWidth(getTableWidthString(columnWidth));
                            }
                            if (rowHeight >= 0)
                            {
                                compLayout.setRowHeight(getTableWidthString(rowHeight));
                            }
                            layoutComstraintList.add(compLayout);
                            compLayout.removeUsed(componentMap);
                        }
                        else
                        {
                            HTMLEmptyLayout emptyLayout = new HTMLEmptyLayout(entity);
                            emptyLayout.setCellX(x);
                            emptyLayout.setCellY(y);
                            emptyLayout.setColCount(columnCount);
                            emptyLayout.setRowCount(rowCount);
                            if (newLine)
                            {
                                emptyLayout.setNewLine();
                            }
                            columnWidth = layout.getColumnWidthWeights().get(x);
                            rowHeight = layout.getRowHeightWeights().get(y);
                            if (columnWidth >= 0)
                            {
                                emptyLayout.setColWidth(getTableWidthString(columnWidth));
                            }
                            if (rowHeight >= 0)
                            {
                                emptyLayout.setRowHeight(getTableWidthString(rowHeight));
                            }
                            layoutComstraintList.add(emptyLayout);
                        }
                        newLine = false;
                    }
                }
            }
            if (!layoutConstraintMap.isEmpty()) {
            	Set<Map.Entry<String,HTMLComponentLayout>> entries = layoutConstraintMap.entrySet();
            	for (Map.Entry<String,HTMLComponentLayout> entry: entries) {
            		logger.warn("UI Widget " + entry.getValue().getUIID() 
            				+ "[at column:"+ entry.getKey() + " row]" 
            				+ " loses the layout constraint, please check the TableLayoutType definition!");
            	}
            }
            layoutConstraintMap.clear();
            
            for (Iterator it = componentMap.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry)it.next();
                String mapKey = (String)entry.getKey();
                UIComponentType uIComponent = (UIComponentType)entry.getValue();
                if (uIComponent instanceof UIReferenceEntityType)
                {
                    if (unUsedReferenceEntity == null)
                    {
                        unUsedReferenceEntity = new ArrayList();
                    }
                    unUsedReferenceEntity.add(((UIReferenceEntityType)uIComponent)
                            .getReferenceEntity().getEntityName());
                }
            }

            if (layoutComstraintList.size() != 0)
            {
                AbstractHTMLLayout firstLayout = (AbstractHTMLLayout)layoutComstraintList.get(0);
                firstLayout.setFirstLine();
            }
        }
    }

    private static double getSum(double a, double b)
    {
        if (a >= 0 && b >= 0 && a + b < 1)
        {
            return a + b;
        }
        else if ((a >= 1 || a == 0) && (b >= 1 || b == 0))
        {
            return (int)a + (int)b;
        }
        else
        {
            return -1;
        }
    }

    private HTMLComponentLayout generateHTMLLayout(UIComponentType component,
            TableLayoutConstraintType layoutConstraint, String cellUIStyle)
    {
        if (component instanceof UIPanelType)
        {
            HTMLPanelLayout panelLayout = new HTMLPanelLayout(component.getUIID(), entity);
            panelLayout.setConstraints(layoutConstraint, parsingContext);
            panelLayout.setBody((UIPanelType)component, parsingContext);
            if (cellUIStyle != null)
            {
                panelLayout.setTDAttribute("cellUIStyle", cellUIStyle);
            }

            return panelLayout;
        }
        else
        {
            HTMLComponentLayout compLayout = new HTMLComponentLayout(component.getUIID(), entity);
            compLayout.setConstraints(layoutConstraint, parsingContext);
            if (cellUIStyle != null)
            {
                compLayout.setTDAttribute("cellUIStyle", cellUIStyle);
            }

            return compLayout;
        }
    }

    private static String getTableWidthString(double d)
    {
        if (d > 0.0 && d <= 1.0)
        {
            return (int)(d * 100) + "%";
        }
        else
        {
            return String.valueOf((int)d) + "px";
        }
    }

    public void generateHidden(HTMLSnapshotContext context, int depth, Map appendMap,
    		VariableEvaluator ee, HTMLLayoutType htmlLayout) throws EvaluationException
    {
        if (hiddenList == null)
        {
            return;
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("<---HTMLLayout.generateHidden--->generate hidden components for the uipage: {}",
                          entity.getName());
        }
        if (hiddenList.size() > 0)
        {
            HTMLUtil.generateTab(context, depth - 1);
            context.generateHTML("<div id=\"div-" + context.getDIVPrefix() + "hidden\" >" );
            for (int i = 0, n = hiddenList.size(); i < n; i++)
            {
                String hiddenID = (String)hiddenList.get(i);
                Map propMap = entity.getComponentProperty(hiddenID);
                Map eventMap = entity.getComponentEvent(hiddenID);
                Map i18nMap = entity.getComponentI18N(hiddenID);
                Map expMap = entity.getComponentExpression(hiddenID);
                Map tempMap = null;
                tempMap = HTMLUtil.evaluateExpression(propMap, expMap, tempMap, ee);
                tempMap = HTMLUtil.internationalization(propMap, i18nMap, tempMap, context);
                tempMap = HTMLUtil.merge(tempMap, (Map)appendMap.get(hiddenID));
                HTMLWidgetType htmlComponent = HTMLUtil.getHTMLUIComponent(hiddenID, context,
                        propMap, eventMap, null, tempMap, htmlLayout, false);
                HTMLUtil.generateTab(context, depth);
                IUISkin uiskinObj = entity.getUISkinObj(hiddenID, ee, htmlComponent);
                if (uiskinObj != null)
                {
                    htmlComponent.addAttribute(uiskinObj.getAttributeMap(htmlComponent));
                    htmlComponent.preIncludePage(context);
                    try
                    {
                        uiskinObj.generatePreCode(htmlComponent);
                    }
                    catch (Exception e)
                    {
                        logger.error("uiskin error: ", e);
                    }
                }
                else
                {
                    htmlComponent.preIncludePage(context);
                }

                Widget newWidget = htmlComponent.createAjaxWidget(ee);
                if (newWidget != null)
                {
                    context.addAjaxWidget(newWidget.getId(), newWidget);
                }
                if (uiskinObj == null || !uiskinObj.isOverwrite())
                {
                    htmlComponent.generateBeginHTML(context, this.entity, depth);
                    htmlComponent.generateEndHTML(context, this.entity, depth);
                }
                if (uiskinObj != null)
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
            }
            HTMLUtil.generateTab(context, depth - 1);
            context.generateHTML("</div>");
        }
    }
}
