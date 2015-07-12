/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.widgets;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.shaolin.bmdp.datamodel.page.ResourceBundlePropertyType;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.datamodel.page.UITabPaneItemType;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.uimaster.html.layout.HTMLPanelLayout;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.CellLayout;
import org.shaolin.uimaster.page.ajax.TabPane;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLTabPaneType extends HTMLContainerType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLTabPaneType.class);

    private int selectedIndex;

    public HTMLTabPaneType()
    {
        selectedIndex = 0;
    }

    public HTMLTabPaneType(HTMLSnapshotContext context)
    {
        super(context);
        selectedIndex = 0;
    }

    public HTMLTabPaneType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
        selectedIndex = 0;
    }
    
    public boolean isAjaxLoading() {
    	return (boolean)this.removeAttribute("ajaxLoad");
    }

    public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            generateWidget(context);

            context.generateHTML("<div id=\"");
            context.generateHTML(getName());
            context.generateHTML("\" class=\"tab ui-tabs ui-widget ui-corner-all\">");

            super.generateBeginHTML(context, ownerEntity, depth);

            
            List<UITabPaneItemType> tabs = (List<UITabPaneItemType>)this.removeAttribute("tabPaneItems");
            int tabSelected = 0;
            if(this.getAttribute("tabSelected") != null) {
                tabSelected = Integer.parseInt((String)this.getAttribute("tabSelected"));
            }
            boolean ajaxLoad = (boolean)this.removeAttribute("ajaxLoad");
            if (ajaxLoad) {
            	TabPane tabPane = ((TabPane)context.getAjaxWidgetMap().get(this.getId()));
            	tabPane.setPageWidgetContext(context.getPageWidgetContext());
            	tabPane.setOwnerEntity(ownerEntity);
            }
            //Generate the titles of the tabpane
            String cmpName = getName().replace('.', '_');
            context.generateHTML("<div class =\"tab-titles ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all\" id=\"titles-container-");
            context.generateHTML(cmpName);
            context.generateHTML("\" selectedIndex=\"");
            context.generateHTML(tabSelected + "");
            context.generateHTML("\">");
            for(int i = 0, n = tabs.size(); i < n; i++)
            {
                UITabPaneItemType tab = tabs.get(i);
                String title = "";
                if (tab.getTitle() instanceof ResourceBundlePropertyType)
                {
                	ResourceBundlePropertyType resourceBundle = ((ResourceBundlePropertyType)tab.getTitle());
                    String bundle = resourceBundle.getBundle();
                    String key = resourceBundle.getKey();
                    title = ResourceUtil.getResource(bundle, key);
                } 
                else if (tab.getTitle() instanceof StringPropertyType)
                {
                	title = ((StringPropertyType)tab.getTitle()).getValue();
                }
                if(i == tabSelected)
                {
                    context.generateHTML("<div class =\"ui-state-default ui-corner-top ui-tabs-active ui-state-active\" style=\"border-bottom: 1px solid white;\" id = \"tab-" + cmpName + "-titles-" + i + "\" index=\""+ i +"\">");
                    selectedIndex = i;
                }
                else
                {
                    context.generateHTML("<div class =\"ui-state-default ui-corner-top\" id = \"tab-" + cmpName + "-titles-" + i + "\" index=\""+ i +"\" ajaxload=\"" + ajaxLoad + "\">");
                }
                context.generateHTML("" + title);
                context.generateHTML("</div>");
            }
            context.generateHTML("</div>");

            //Generate the bodies of the tabpane
            context.generateHTML("<div class=\"tab-bodies\" id=\"bodies-container-" + cmpName + "\">");
            for(int i = 0, n = tabs.size(); i < n; i++)
            {
            	//Generate one body
            	context.generateHTML("<div id=\"tab-");
            	context.generateHTML(cmpName + "-body-"+ i + "\"");
            	context.generateHTML(" class=\"");
            	if(tabSelected == i)
            	{
            		context.generateHTML("tab-selected-body ");
            	}
            	else
            	{
            		context.generateHTML("tab-unselected-body ");
            	}
            	context.generateHTML("\" index=\"");
            	context.generateHTML(String.valueOf(i));
            	context.generateHTML("\"");
            	context.generateHTML(" uiid=\"");
            	context.generateHTML(tabs.get(i).getUiid());
            	context.generateHTML("\">");
            	
            	if (ajaxLoad && i > 0) {
            		// save context if it's ajax loading.
            		String uiid = tabs.get(i).getUiid();
                	TabPane tabPane = (TabPane)context.getAjaxWidget(getName());
                	tabPane.addODMapperContext(uiid, context.getODMapperContext(uiid));
            	}
            	if (!ajaxLoad || i==0) {
	                UITabPaneItemType tab = tabs.get(i);
	                if (tab.getPanel() != null) {
	                	//ui panel support
	                	String UIID = this.getPrefix() + tab.getPanel().getUIID();
	                	HTMLPanelLayout panelLayout = new HTMLPanelLayout(UIID, ownerEntity);
	                	TableLayoutConstraintType layoutConstraint = new TableLayoutConstraintType();
	                	OOEEContext ooee = OOEEContextFactory.createOOEEContext();
	                    panelLayout.setConstraints(layoutConstraint, ooee);
	                    panelLayout.setBody(tab.getPanel(), ooee);
	                	
	                    panelLayout.generateComponentHTML(context, depth, true, Collections.emptyMap(), ee, this.getHTMLLayout());
	                } else if (tab.getRefEntity() != null) {
	                	//form support
	                	String prefix = context.getHTMLPrefix();
	                	try {
		                	UIReferenceEntityType itemRef = (UIReferenceEntityType)tab.getRefEntity();
		                	String UIID = this.getPrefix() + itemRef.getUIID();
			                String type = itemRef.getReferenceEntity().getEntityName();
			                HTMLReferenceEntityType refEntity = new HTMLReferenceEntityType(context, UIID, type);
			                //Generate the uiform of the body
			                refEntity.generateBeginHTML(context, ownerEntity, depth+1);
			                refEntity.generateEndHTML(context, ownerEntity, depth+1);
	                	} finally {
	                		context.setHTMLPrefix(prefix);
	                	}
	                } else if (tab.getFrame() != null) {
	                	//page support
	                	this.context.getRequest().setAttribute("_chunkname", tab.getFrame().getChunkName());
	                	this.context.getRequest().setAttribute("_nodename", tab.getFrame().getNodeName());
	                	this.context.getRequest().setAttribute("_framePagePrefix", this.context.getFrameInfo());
	                	this.context.getRequest().setAttribute("_tabcontent", "true");
	                	HTMLFrameType frame = new HTMLFrameType(this.context, tab.getUiid());
	                	frame.setHTMLAttribute(HTMLFrameType.NEED_SRC, "true");
	                	frame.generateBeginHTML(context, ownerEntity, depth + 1);
	                	frame.generateEndHTML(context, ownerEntity, depth + 1);
	                } 
            	}
                context.generateHTML("</div>");
            }
            context.generateHTML("</div>");
            
            generateEndWidget(context);
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            super.generateEndHTML(context, ownerEntity, depth);
            context.generateHTML("</div>");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if( !(attributeValue instanceof String) )
            return;
        
        String attrValue = (String)attributeValue;

        if ("align".equals(attributeName))
        {
            if(!"full".equals(attrValue.toLowerCase()))
            {
                addStyle("text-align", attrValue);
            }
        }
        else if ( "valign".equals(attributeName) || "rowUIStyle".equals(attributeName)
                || "x".equals(attributeName) || "y".equals(attributeName) )
        {
        }
        else if ("width".equals(attributeName))
        {
            addStyle("min-width", attrValue);
            addStyle("_width", attrValue);
        }
        else if ("height".equals(attributeName))
        {
            addStyle("min-height", attrValue);
            addStyle("_height", attrValue);
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

    /**
     * Whether this component can have editPermission.
     */
    @Override
    public boolean isEditPermissionEnabled()
    {
        return false;
    }
    
    private VariableEvaluator ee;
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
    	this.ee = ee;
//        HashMap tempVars = null;
//        boolean ajaxLoad = (boolean)this.getAttribute("ajaxLoad");
//        if (ajaxLoad) {
//        	// save the input variables to this tab panel.
//        	EvaluationContext tempContext = ee.getExpressionContext(ODContext.LOCAL_TAG);
//        	if (tempContext == null) {
//        		tempContext = ee.getExpressionContext();
//        	}
//        	if (tempContext instanceof DefaultEvaluationContext) {
//        		Map vars = ((DefaultEvaluationContext)tempContext).getVariableObjects();
//        		tempVars = new HashMap(vars);
//        	}
//        }
    	List<UITabPaneItemType> tabs = (List<UITabPaneItemType>)this.getAttribute("tabPaneItems");
    	TabPane panel = new TabPane(getName(), tabs, selectedIndex, new CellLayout());
        panel.setReadOnly(getReadOnly());
        panel.setUIEntityName(getUIEntityName());
        
        panel.setListened(true);
        panel.setFrameInfo(getFrameInfo());
        
        return panel;

    }

}
