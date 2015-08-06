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

import java.util.List;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.flowdiagram.Connection;
import org.shaolin.bmdp.datamodel.flowdiagram.FlowChunk;
import org.shaolin.bmdp.datamodel.flowdiagram.NodeType;
import org.shaolin.bmdp.datamodel.page.UITableActionGroupType;
import org.shaolin.bmdp.datamodel.page.UITableActionType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.FlowDiagram;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.WorkFlowDiagram;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.UIVariableUtil;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLFlowDiagramType extends HTMLWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLFlowDiagramType.class);

    public HTMLFlowDiagramType()
    {
    }

    public HTMLFlowDiagramType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLFlowDiagramType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
    	
    	HTMLUtil.generateTab(context, depth);
    	String root = WebConfig.getResourceContextRoot();
    	context.generateHTML("<link rel=\"stylesheet\" href=\""+root+"/css/jsplumb/jsplumb.css\" type=\"text/css\">\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/jquery.ui.touch-punch-0.2.2.min.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/dom-adapter.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/biltong-0.2.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/jsBezier-0.6.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/util.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/jsPlumb.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/jquery.jsPlumb.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/defaults.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/renderers.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/connection.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/endpoint.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/anchors.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jsplumb/overlays-guidelines.js\"></script>\n");
    	context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/uimaster-flow.js\"></script>\n");
    	
    	List<UITableActionType> defaultActions = (List<UITableActionType>)this.removeAttribute("defaultActionGroup");
    	HTMLUtil.generateTab(context, depth);
		context.generateHTML("<div id='" + this.getUIID() + "ActionBar' class=\"ui-widget-header ui-corner-all\">");
		HTMLUtil.generateTab(context, depth + 1);
		String defaultBtnSet = "defaultBtnSet_" + this.getUIID();
		context.generateHTML("<span id=\""+defaultBtnSet+"\">");
		for (UITableActionType action: defaultActions){
			HTMLUtil.generateTab(context, depth + 2);
			context.generateHTML("<input type=\"radio\" name=\""+defaultBtnSet+"\" id=\""+action.getUiid());
			context.generateHTML("\" onclick=\"javascript:defaultname.");
			context.generateHTML(this.getPrefix() + action.getFunction());
			context.generateHTML("('" + this.getUIID() + "');\" title='");
			context.generateHTML(UIVariableUtil.getI18NProperty(action.getTitle()));
			context.generateHTML("' icon=\""+action.getIcon()+"\"><label for=\""+action.getUiid()+"\">");
			context.generateHTML(UIVariableUtil.getI18NProperty(action.getTitle()));
			context.generateHTML("</label></input>");
		}
		String htmlPrefix = this.getPrefix().replace('.', '_');
		String htmlId = this.getPrefix().replace('.', '_') + this.getUIID();
		List<UITableActionGroupType> actionGroups = (List<UITableActionGroupType>)this.removeAttribute("actionGroups");
		if (actionGroups !=null && actionGroups.size() > 0) {
			for (UITableActionGroupType a : actionGroups) {
				HTMLUtil.generateTab(context, depth + 2);
				int count = 0;
				String btnSetName = "btnSet_" + htmlId + (count++);
				context.generateHTML("<span id=\""+btnSetName+"\">");
				for (UITableActionType action: a.getActions()){
					HTMLUtil.generateTab(context, depth + 3);
					if("button".equals(a.getType())) {
						context.generateHTML("<button");
					} else if("radio".equals(a.getType())) {
						context.generateHTML("<input type='radio' name='"+btnSetName+"'");
					} else if("checkbox".equals(a.getType())) {
						context.generateHTML("<input type='checkbox'");
					}
					context.generateHTML(" id=\""+htmlPrefix+action.getUiid()+"\" onclick=\"javascript:defaultname.");
					context.generateHTML(this.getPrefix() + action.getFunction());
					context.generateHTML("('" + this.getPrefix() + this.getUIID() + "');\" title='");
					context.generateHTML(UIVariableUtil.getI18NProperty(action.getTitle()));
					context.generateHTML("' icon=\""+action.getIcon()+"\">");
					
					if("button".equals(a.getType())) {
						context.generateHTML(UIVariableUtil.getI18NProperty(action.getTitle()));
						context.generateHTML("</button>");
					} else if("radio".equals(a.getType())) {
						context.generateHTML("<label for=\""+htmlPrefix+action.getUiid()+"\">");
						context.generateHTML(UIVariableUtil.getI18NProperty(action.getTitle()));
						context.generateHTML("</label></input>");
					} else if("checkbox".equals(a.getType())) {
						context.generateHTML("<label for=\""+action.getUiid()+"\">");
						context.generateHTML(UIVariableUtil.getI18NProperty(action.getTitle()));
						context.generateHTML("</label></input>");
					}
				}
				HTMLUtil.generateTab(context, depth + 2);
				context.generateHTML("</span>");
			}
		}
		HTMLUtil.generateTab(context, depth + 1);
		context.generateHTML("</span>");
		HTMLUtil.generateTab(context, depth);
		context.generateHTML("</div>");
    	
    	HTMLUtil.generateTab(context, depth);
		context.generateHTML("<div id='" + this.getUIID() + "' class=\"demo flowchart\">");
		
		FlowChunk flow = null;
    	String flowName = (String)this.removeAttribute("flowName");
    	if (flowName != null) {
	    	flow = IServerServiceManager.INSTANCE.getEntityManager().getEntity(flowName, FlowChunk.class);
    	}
    	Object data = this.removeAttribute("loadFlow");
    	if (data != null) {
    		flow = (FlowChunk)data;
    	}
		List<NodeType> nodes =  flow.getNodes();
    	for (NodeType node : nodes) {
    		HTMLUtil.generateTab(context, depth + 1);
    		context.generateHTML("<div id='");
    		context.generateHTML(node.getId());
    		context.generateHTML("' style='top:");
    		context.generateHTML((node.getX() == null?"1":node.getY()) + "");
    		context.generateHTML("px;left:");
    		context.generateHTML((node.getY() == null?"1":node.getX()) + "");
    		context.generateHTML("px' rotation='");
    		context.generateHTML(node.getRotation() + "");
    		context.generateHTML("' title='");
    		context.generateHTML(node.getName());
    		context.generateHTML("' class=\"window\">");
    		context.generateHTML(node.getName());
    		context.generateHTML("</div>");
    	}
    	HTMLUtil.generateTab(context, depth + 1);
		context.generateHTML("<div id=\"connectionInfo\" style=\"display:none;\">");
    	List<Connection> connections = flow.getConnections();
    	for (Connection conn : connections) {
    		HTMLUtil.generateTab(context, depth + 2);
    		context.generateHTML("<div id='");
    		context.generateHTML(conn.getName());
    		context.generateHTML("' srcAnchor='");
    		context.generateHTML(conn.getSourceAnchor());
    		context.generateHTML("' tarAnchor='");
    		context.generateHTML(conn.getTargetAnchor());
    		context.generateHTML("' routes='");
    		context.generateHTML(conn.getRoutes().toString());
    		context.generateHTML("'></div>");
    	}
    	HTMLUtil.generateTab(context, depth + 1);
		context.generateHTML("</div>");
    	HTMLUtil.generateTab(context, depth);
		context.generateHTML("</div>");
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
    	FlowChunk flow = null;
    	String flowName = (String)this.getAttribute("flowName");
    	if (flowName != null) {
	    	flow = IServerServiceManager.INSTANCE.getEntityManager().getEntity(flowName, FlowChunk.class);
    	}
    	Object data = this.getAttribute("loadFlow");
    	if (data != null) {
    		flow = (FlowChunk)data;
    	}
    	Object dataModel = this.removeAttribute("loadDateModel");
    	if (dataModel instanceof List) {
	    	FlowDiagram t = new FlowDiagram(getName(), Layout.NULL, flow, (List)dataModel);
	    	t.setLoadFlowExpr((ExpressionType)this.removeAttribute("loadFlowExpr"));
	    	t.setLoadDataModelExpr((ExpressionType)this.removeAttribute("loadDateModelExpr"));
	        t.setReadOnly(getReadOnly());
	        t.setUIEntityName(getUIEntityName());
	        t.setListened(true);
	        t.setFrameInfo(getFrameInfo());
	        return t;
    	} else if (dataModel instanceof Workflow) {
    		this.removeAttribute("loadDataModelExpr");
    		this.removeAttribute("loadFlowExpr");
    		WorkFlowDiagram t = new WorkFlowDiagram(getName(), Layout.NULL, flow, (Workflow)dataModel);
	        t.setReadOnly(getReadOnly());
	        t.setUIEntityName(getUIEntityName());
	        t.setListened(true);
	        t.setFrameInfo(getFrameInfo());
	        return t;
    	} else {
    		throw new IllegalStateException("Unsupported object for flow diagram: " + dataModel);
    	}
    }
    
}
