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
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.flowdiagram.Connection;
import org.shaolin.bmdp.datamodel.flowdiagram.FlowChunk;
import org.shaolin.bmdp.datamodel.flowdiagram.NodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.RectangleNodeType;
import org.shaolin.bmdp.runtime.be.IBusinessEntity;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.od.ODContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Shaolin Wu
 */
public class FlowDiagram extends Widget implements Serializable {
	private static final long serialVersionUID = -1744731434666233557L;

	private static final Logger logger = LoggerFactory.getLogger(FlowDiagram.class);

	private transient final List<IBusinessEntity> updateEntities = new ArrayList<IBusinessEntity>();

	private transient final List<IBusinessEntity> createEntities = new ArrayList<IBusinessEntity>();
	
	private transient final List<IBusinessEntity> deleteEntities = new ArrayList<IBusinessEntity>();
	
	private transient List<IBusinessEntity> allEntities;
	
	private FlowChunk flow;
	
	private String selectedNode;

	private ExpressionType loadFlowExpr;
	
	private ExpressionType loadDateModelExpr;
	
	public FlowDiagram(String id, Layout layout, FlowChunk flow, List<IBusinessEntity> allEntities) {
		super(id, layout);
		this._setWidgetLabel(id);
		this.flow = flow;
		this.allEntities = allEntities;
	}
	
	public void addAttribute(String name, Object value, boolean update)
    {
		if ("selectedNode".equals(name)) {
			selectedNode = value.toString();
		} if ("nodelocations".equals(name)) {
			try {
				JSONArray array = new JSONArray(value.toString());
				int length = array.length();
				for (int i=0; i<length; i++) {
					JSONObject item = array.getJSONObject(i);
					String nodeName = item.getString("id");
					List<NodeType> nodes = flow.getNodes();
					for (NodeType node: nodes) {
						if (node.getId().equals(nodeName)) {
							node.setY(item.getInt("top"));
							node.setX(item.getInt("left"));
							break;
						}
					}
				}
			} catch (JSONException e) {
				logger.error("error occurrs while synchronizing the value from the page.", e);
			}
		} if ("connections".equals(name)) {
			try {
				flow.getConnections().clear();
				JSONArray array = new JSONArray(value.toString());
				int length = array.length();
				for (int i=0; i<length; i++) {
					JSONObject item = array.getJSONObject(i);
					Connection newConnection = new Connection();
					newConnection.setName(item.getString("connectId"));
					newConnection.setSourceAnchor(item.getString("source") + item.getString("srcAnchor"));
					newConnection.setTargetAnchor(item.getString("target") + item.getString("tarAnchor"));
					flow.getConnections().add(newConnection);
				}
			} catch (JSONException e) {
				logger.error("error occurrs while synchronizing the value from the page.", e);
			}
		} else {
			super.addAttribute(name, value, update);
		}
    }
	
	public String getSelectedNode() {
		List<NodeType> nodes = flow.getNodes();
		for (NodeType node : nodes) {
			if (node.getId().equals(selectedNode)) {
				return node.getName();
			}
		}
		return null;
	}
	
	public List<String> getNodeList() {
		List<String> nodeList = new ArrayList<String>();
		List<NodeType> nodes = flow.getNodes();
		for (NodeType node : nodes) {
			nodeList.add(node.getName());
		}
		return nodeList;
	}
	
	public List<IBusinessEntity> getAllEntities() {
		return allEntities;
	}
	
	public List<IBusinessEntity> getDeleteEntities() {
		return deleteEntities;
	}
	
	public List<IBusinessEntity> getUpdateEntities() {
		return updateEntities;
	}

	public List<IBusinessEntity> getCreateEntities() {
		return createEntities;
	}
	
	public void setLoadFlowExpr(ExpressionType queryExpr) {
		this.loadFlowExpr = queryExpr;
	}
	
	public void setLoadDataModelExpr(ExpressionType queryExpr) {
		this.loadDateModelExpr = queryExpr;
	}
	
	public void refreshModel() {
		try {
			this.updateEntities.clear();
			this.createEntities.clear();
			this.deleteEntities.clear();
			
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
			ooeeContext.setDefaultEvaluationContext(evaContext);
			ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);

			this.allEntities = (List)loadDateModelExpr.evaluate(ooeeContext);
		} catch (Exception e) {
			logger.error("error occurrs while refreshing table.", e);
		}
	}
	
	public void updateNode(String nodeName) {
		//TODO:
	}

	public void addNode(String nodeId, String nodeName, String description) {
		if (nodeName == null || nodeName.trim().isEmpty()) {
			return;
		}
		//TODO: support multiple types.
		RectangleNodeType node = new RectangleNodeType();
		node.setId(nodeId);
		node.setName(nodeName);
		node.setDescription(description);
		node.setX(0);
		node.setY(0);
		flow.getNodes().add(node);
		
		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setUiid(this.getId());
        dataItem.setJsHandler("uiflowhandler");
        
        Map data = new HashMap();
        data.put("cmd","addNode");
        data.put("data", (new JSONObject(node)).toString());
        dataItem.setData((new JSONObject(data)).toString());
        dataItem.setFrameInfo(getFrameInfo());
        ajaxContext.addDataItem(dataItem);
	}

	public void deleteNode(String nodeName) {
		if (nodeName == null || nodeName.isEmpty()) {
			return ;
		}
		String nodeId = nodeName;
		List<NodeType> nodes = flow.getNodes();
		for (NodeType node: nodes) {
			if (node.getId().equals(nodeId)) {
				nodes.remove(node);
				break;
			}
		}
		List<Connection> connections = flow.getConnections();
		for (Connection conn: connections) {
			if (conn.getSourceAnchor().startsWith(nodeId)) {
				connections.remove(conn);
				break;
			}
		}
		
		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
		IDataItem dataItem = AjaxActionHelper.createDataItem();
		dataItem.setUiid(this.getId());
		dataItem.setJsHandler("uiflowhandler");
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("cmd", "removeNode");
		data.put("nodeId", nodeId);
		dataItem.setData((new JSONObject(data)).toString());
		dataItem.setFrameInfo(getFrameInfo());
		ajaxContext.addDataItem(dataItem);
	}
	
	public String getFlowXML() {
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(flow, writer);
			return writer.toString();
		} catch (JAXBException e) {
			logger.error("error occurrs while marshalling flow object to XML.", e);
		}
		return "";
	}
	
	public FlowChunk getFlowView() {
		return flow;
	}
	
	public String refresh() {
		try {
			//TODO
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
			ooeeContext.setDefaultEvaluationContext(evaContext);
			ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);

			flow = (FlowChunk)loadFlowExpr.evaluate(ooeeContext);
			
			StringBuffer sb = new  StringBuffer();
	        
	        return sb.toString();
		} catch (Exception e) {
			logger.error("error occurrs while refreshing table.", e);
		}
		return "";
	}
	
	public void saveSuccess() {
		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setUiid(this.getId());
        dataItem.setJsHandler("uiflowhandler");
        
        Map<String, String> data = new HashMap<String, String>();
        data.put("cmd","saveSuccess");
        dataItem.setData((new JSONObject(data)).toString());
        dataItem.setFrameInfo(getFrameInfo());
        ajaxContext.addDataItem(dataItem);
	}
	
	public void saveFailure() {
		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setUiid(this.getId());
        dataItem.setJsHandler("uiflowhandler");
        
        Map<String, String> data = new HashMap<String, String>();
        data.put("cmd","saveFailure");
        dataItem.setData((new JSONObject(data)).toString());
        dataItem.setFrameInfo(getFrameInfo());
        ajaxContext.addDataItem(dataItem);
	}
}
