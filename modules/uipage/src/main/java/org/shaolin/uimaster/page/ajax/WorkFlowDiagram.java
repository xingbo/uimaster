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
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.flowdiagram.CircleNodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.Connection;
import org.shaolin.bmdp.datamodel.flowdiagram.DiamondNodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.FlowChunk;
import org.shaolin.bmdp.datamodel.flowdiagram.JoinTriangleNodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.NodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.RectangleNodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.RefFlowNodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.SplitTriangleNodeType;
import org.shaolin.bmdp.datamodel.flowdiagram.TriangleNodeType;
import org.shaolin.bmdp.datamodel.workflow.ChildFlowNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConditionNodeType;
import org.shaolin.bmdp.datamodel.workflow.DestType;
import org.shaolin.bmdp.datamodel.workflow.DestWithFilterType;
import org.shaolin.bmdp.datamodel.workflow.EndNodeType;
import org.shaolin.bmdp.datamodel.workflow.FlowType;
import org.shaolin.bmdp.datamodel.workflow.GeneralNodeType;
import org.shaolin.bmdp.datamodel.workflow.JoinNodeType;
import org.shaolin.bmdp.datamodel.workflow.MissionNodeType;
import org.shaolin.bmdp.datamodel.workflow.SplitNodeType;
import org.shaolin.bmdp.datamodel.workflow.StartNodeType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Shaolin Wu
 */
public class WorkFlowDiagram extends Widget implements Serializable {
	private static final long serialVersionUID = -1744731434666233557L;

	private static final Logger logger = LoggerFactory.getLogger(WorkFlowDiagram.class);

	private Workflow wflowModel;
	
	private FlowChunk flowView;
	
	private String currentFlowName;
	
	private String selectedNode;

	private ReentrantLock lock = new ReentrantLock();
	
	private static final List<String> nodeTypes = new ArrayList<String>();
	
	static {
		nodeTypes.add(StartNodeType.class.getName());
		nodeTypes.add(EndNodeType.class.getName());
		nodeTypes.add(MissionNodeType.class.getName());
		nodeTypes.add(ConditionNodeType.class.getName());
		nodeTypes.add(JoinNodeType.class.getName());
		nodeTypes.add(SplitNodeType.class.getName());
		nodeTypes.add(ChildFlowNodeType.class.getName());
		nodeTypes.add(GeneralNodeType.class.getName());
	}

	public WorkFlowDiagram(String id, Layout layout, FlowChunk flow, Workflow wflow) {
		super(id, layout);
		this._setWidgetLabel(id);
		this.flowView = flow;
		this.wflowModel = wflow;
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
					List<NodeType> nodes = flowView.getNodes();
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
				flowView.getConnections().clear();
				JSONArray array = new JSONArray(value.toString());
				int length = array.length();
				for (int i=0; i<length; i++) {
					JSONObject item = array.getJSONObject(i);
					Connection newConnection = new Connection();
					newConnection.setName(item.getString("connectId"));
					newConnection.setSourceAnchor(item.getString("source") + item.getString("srcAnchor"));
					newConnection.setTargetAnchor(item.getString("target") + item.getString("tarAnchor"));
					flowView.getConnections().add(newConnection);
				}
			} catch (JSONException e) {
				logger.error("error occurrs while synchronizing the value from the page.", e);
			}
		} else {
			super.addAttribute(name, value, update);
		}
    }
	
	public String getSelectedNode() {
		List<NodeType> nodes = flowView.getNodes();
		for (NodeType node : nodes) {
			if (node.getId().equals(selectedNode)) {
				return node.getName();
			}
		}
		return null;
	}
	
	public List<String> getNodeList() {
		List<String> nodeList = new ArrayList<String>();
		List<NodeType> nodes = flowView.getNodes();
		for (NodeType node : nodes) {
			nodeList.add(node.getName());
		}
		return nodeList;
	}
	
	public Workflow getWorflowModel() {
		return wflowModel;
	}
	
	public FlowType getSelectedFlow() {
		if (currentFlowName == null) {
			throw new IllegalStateException("No flow selected currently!");
		}
		for (FlowType flow : wflowModel.getFlows()) {
			if (flow.getName().equals(currentFlowName)) {
				return flow;
			}
		}
		return null;
	}
	
	public FlowChunk getWorflowView() {
		return flowView;
	}
	
	public String getFlowModelXML() {
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(wflowModel, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new IllegalStateException("error occurrs while marshalling flow object to XML." + e.getMessage(), e);
		}
	}
	
	public String getFlowViewXML() {
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(flowView, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new IllegalStateException("error occurrs while marshalling flow object to XML." + e.getMessage(), e);
		}
	}
	
	public Workflow createWorkflow() {
		try {
			lock.lock();
			this.selectedNode = null;
			
			this.wflowModel = new Workflow();
			this.wflowModel.setEntityName("NewWorkflow_" + (int)(Math.random() * 100000));
			
			this.flowView = new FlowChunk();
			return this.wflowModel;
		} finally {
			lock.unlock();
		}
	}
	
	public void setWorkflow(String currentFlowName, Workflow wflowMode, FlowChunk flowView) {
		try {
			lock.lock();
			this.selectedNode = null;
			this.currentFlowName = currentFlowName;
			int hasDot = this.currentFlowName.lastIndexOf(".");
			if (hasDot != -1) {
				this.currentFlowName = this.currentFlowName.substring(hasDot + 1);
			}
			this.wflowModel = wflowMode;
			this.flowView = flowView;
		} finally {
			lock.unlock();
		}
	}
	
	public static List<String> getNodeTypes() {
		return nodeTypes;
	}
	
	public static void syncFlow(String currentFlowName, Workflow wflowMode, FlowChunk flowView) {
		for (FlowType flow : wflowMode.getFlows() ) {
			if (currentFlowName.endsWith("." + flow.getName())) {
				List<org.shaolin.bmdp.datamodel.workflow.NodeType> nodes = flow.getNodesAndConditionsAndSplits();
				for (org.shaolin.bmdp.datamodel.workflow.NodeType dateNode : nodes) {
					org.shaolin.bmdp.datamodel.flowdiagram.NodeType viewNode = null;
					if (dateNode.getClass() == StartNodeType.class) {
						viewNode = new CircleNodeType();
						viewNode.setX(20);
						viewNode.setY(10);
					} else if (dateNode.getClass() == EndNodeType.class) {
						viewNode = new CircleNodeType();
						viewNode.setX(20);
						viewNode.setY(80);
					} else if (dateNode.getClass() == GeneralNodeType.class) {
						viewNode = new RectangleNodeType();
						viewNode.setX(20);
						viewNode.setY(20);
				    } else if (dateNode.getClass() == MissionNodeType.class) {
						viewNode = new RectangleNodeType();
						viewNode.setX(20);
						viewNode.setY(40);
					} else if (dateNode.getClass() == ConditionNodeType.class) {
						viewNode = new DiamondNodeType();
						viewNode.setX(20);
						viewNode.setY(60);
					} else if (dateNode.getClass() == JoinNodeType.class) {
						viewNode = new TriangleNodeType();
						viewNode.setX(20);
						viewNode.setY(70);
					} else if (dateNode.getClass() == SplitNodeType.class) {
						viewNode = new TriangleNodeType();
						viewNode.setX(20);
						viewNode.setY(80);
					} else if (dateNode.getClass() == ChildFlowNodeType.class) {
						viewNode = new RefFlowNodeType();
						viewNode.setX(20);
						viewNode.setY(90);
					} else {
						throw new UnsupportedOperationException("Node type " + dateNode.getClass() + " is unidentified!");
					}
					viewNode.setId(dateNode.getName());
					viewNode.setName(dateNode.getName());
					viewNode.setDisplayName(dateNode.getName());
					viewNode.setDescription(dateNode.getDescription());
					flowView.getNodes().add(viewNode);
					
					if (dateNode instanceof GeneralNodeType) {
						if (((GeneralNodeType)dateNode).getDest() != null) {
							String tarName = ((GeneralNodeType)dateNode).getDest().getName();
							Connection connection = new Connection();
							connection.setName(dateNode.getName() + "_" + tarName);
							connection.setSourceAnchor(dateNode.getName());
							connection.setTargetAnchor(tarName);
							flowView.getConnections().add(connection);
						}
					} else if (dateNode instanceof ConditionNodeType) {
						ConditionNodeType condition = ((ConditionNodeType)dateNode);
						if (condition.getDests() != null) {
							for (DestWithFilterType filter : condition.getDests()) {
								String tarName = filter.getName();
								Connection connection = new Connection();
								connection.setName(dateNode.getName() + "_" + tarName);
								connection.setSourceAnchor(dateNode.getName());
								connection.setTargetAnchor(tarName);
								flowView.getConnections().add(connection);
							}
						}
					} else if (dateNode instanceof SplitNodeType) {
						SplitNodeType split = ((SplitNodeType)dateNode);
						if (split.getDests() != null) {
							for (DestWithFilterType filter : split.getDests()) {
								String tarName = filter.getName();
								Connection connection = new Connection();
								connection.setName(dateNode.getName() + "_" + tarName);
								connection.setSourceAnchor(dateNode.getName());
								connection.setTargetAnchor(tarName);
								flowView.getConnections().add(connection);
							}
						}
					} 
				}
				break;
			}
		}
	}
	
	public void addFlow(FlowType newFlow) {
		for (FlowType f : this.wflowModel.getFlows()) {
			if (f.getName().equals(newFlow.getName())) {
				this.wflowModel.getFlows().remove(f);
				break;
			}
		}
		this.currentFlowName = newFlow.getName();
		this.wflowModel.getFlows().add(newFlow);
		this.flowView = new FlowChunk();
		
		this.refreshModel();
	}
	
	public void removeFlow(FlowType newFlow) {
		//TODO: check the node if exists.
		for (FlowType f : this.wflowModel.getFlows()) {
			if (f.getName().equals(newFlow.getName())) {
				this.wflowModel.getFlows().remove(f);
				break;
			}
		}
		this.currentFlowName = null;
		this.flowView = new FlowChunk();
		
		this.refreshModel();
	}
	
	public void addNode(final org.shaolin.bmdp.datamodel.workflow.NodeType node) {
		if (this.currentFlowName == null || node == null) {
			logger.warn("Please selected a sub flow before adding the node!");
			return;
		}
		
		org.shaolin.bmdp.datamodel.flowdiagram.NodeType viewNode = null;
		if (node.getClass() == StartNodeType.class) {
			viewNode = new CircleNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == EndNodeType.class) {
			viewNode = new CircleNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == MissionNodeType.class) {
			viewNode = new RectangleNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == ConditionNodeType.class) {
			viewNode = new DiamondNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == JoinNodeType.class) {
			viewNode = new JoinTriangleNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == SplitNodeType.class) {
			viewNode = new SplitTriangleNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == ChildFlowNodeType.class) {
			viewNode = new RefFlowNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else if (node.getClass() == GeneralNodeType.class) {
			viewNode = new RectangleNodeType();
			viewNode.setX(0);
			viewNode.setY(0);
		} else {
			throw new UnsupportedOperationException("Node type " + node.getClass() + " is unidentified!");
		}
		for (FlowType f : wflowModel.getFlows()) {
			if(this.currentFlowName.equals(f.getName())) {
				for (org.shaolin.bmdp.datamodel.workflow.NodeType n : f.getNodesAndConditionsAndSplits()) {
					if (n.getName().equals(node.getName())) {
						f.getNodesAndConditionsAndSplits().remove(n);
						logger.warn("the node {} is already existed, replace it.", n.getName());
						break;
					}
				}
				f.getNodesAndConditionsAndSplits().add(node);
				break;
			}
		}
		viewNode.setId(node.getName());
		viewNode.setName(node.getName());
		viewNode.setDescription(node.getDescription());
		flowView.getNodes().add(viewNode);
		
		AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setUiid(this.getId());
        dataItem.setJsHandler("uiflowhandler");
        
        Map data = new HashMap();
        data.put("cmd","addNode");
        data.put("data", (new JSONObject(viewNode)).toString());
        dataItem.setData((new JSONObject(data)).toString());
        dataItem.setFrameInfo(getFrameInfo());
        ajaxContext.addDataItem(dataItem);
	}

	public void deleteNode(final String nodeName) {
		if (nodeName == null || nodeName.isEmpty()) {
			return ;
		}
		
		boolean checkflowNameCorrect = false; 
		boolean checknodeNameCorrect = false; 
		for (FlowType f : wflowModel.getFlows()) {
			if(this.currentFlowName.equals(f.getName())) {
				checkflowNameCorrect = true;
				for (org.shaolin.bmdp.datamodel.workflow.NodeType node : f
						.getNodesAndConditionsAndSplits()) {
					if (nodeName.equals(node.getName())) {
						checknodeNameCorrect = true;
						f.getNodesAndConditionsAndSplits().remove(node);
						break;
					}
				}
				break;
			}
		}
		if (!checkflowNameCorrect || !checknodeNameCorrect) {
			throw new IllegalArgumentException("Flow name " + currentFlowName + "." + nodeName + " is unidentified!");
		}
		
		String nodeId = nodeName;
		List<NodeType> nodes = flowView.getNodes();
		for (NodeType node: nodes) {
			if (node.getId().equals(nodeId)) {
				nodes.remove(node);
				break;
			}
		}
		List<Connection> connections = flowView.getConnections();
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
	
	public void addNodeLink(String srcNodeName, String targetNodeName) {
		for (FlowType f : wflowModel.getFlows()) {
			if(this.currentFlowName.equals(f.getName())) {
				
				org.shaolin.bmdp.datamodel.workflow.NodeType srcNode = null;
				org.shaolin.bmdp.datamodel.workflow.NodeType tarNode = null;
				
				for (org.shaolin.bmdp.datamodel.workflow.NodeType node : f
						.getNodesAndConditionsAndSplits()) {
					if (srcNodeName.equals(node.getName())) {
						srcNode = node;
					}
					if (targetNodeName.equals(node.getName())) {
						tarNode = node;
					}
					
					if (srcNode != null && tarNode != null) {
						break;
					}
				}
				
				if (srcNode instanceof GeneralNodeType) {
					DestType destType = new DestType();
					destType.setName(targetNodeName);
					((GeneralNodeType)srcNode).setDest(destType);
				} else if (srcNode instanceof ConditionNodeType) {
					ConditionNodeType condition = ((ConditionNodeType)srcNode);
					
					DestWithFilterType filter = new DestWithFilterType();
					filter.setName(targetNodeName);
					ExpressionType expr = new ExpressionType();
					expr.setExpressionString("true");
					filter.setExpression(expr);
					condition.getDests().add(filter);
				} else if (srcNode instanceof SplitNodeType) {
					SplitNodeType split = ((SplitNodeType)srcNode);
					
					DestWithFilterType filter = new DestWithFilterType();
					filter.setName(targetNodeName);
					ExpressionType expr = new ExpressionType();
					expr.setExpressionString("true");
					filter.setExpression(expr);
					split.getDests().add(filter);
				} else {
					throw new UnsupportedOperationException("Unsupport node to add connection!");
				}
				
				break;
			}
		}
		
		List<Connection> connections = flowView.getConnections();
		for (Connection conn: connections) {
			if (conn.getSourceAnchor().startsWith(srcNodeName)) {
				conn.setName(srcNodeName + "_" + targetNodeName);
				conn.setSourceAnchor(srcNodeName);
				conn.setTargetAnchor(targetNodeName);
				
				return;
			}
		}
		
		Connection connection = new Connection();
		connection.setName(srcNodeName + "_" + targetNodeName);
		connection.setSourceAnchor(srcNodeName);
		connection.setTargetAnchor(targetNodeName);
		flowView.getConnections().add(connection);
	}
	
	public void removeNodeLink(String srcNodeName, String targetNodeName) {
		for (FlowType f : wflowModel.getFlows()) {
			if(this.currentFlowName.equals(f.getName())) {
				org.shaolin.bmdp.datamodel.workflow.NodeType srcNode = null;
				org.shaolin.bmdp.datamodel.workflow.NodeType tarNode = null;
				
				for (org.shaolin.bmdp.datamodel.workflow.NodeType node : f
						.getNodesAndConditionsAndSplits()) {
					if (srcNodeName.equals(node.getName())) {
						srcNode = node;
					}
					if (targetNodeName.equals(node.getName())) {
						tarNode = node;
					}
					
					if (srcNode != null && tarNode != null) {
						break;
					}
				}
				
				if (srcNode instanceof GeneralNodeType) {
					((GeneralNodeType)srcNode).setDest(null);
				} else if (srcNode instanceof GeneralNodeType) {
					ConditionNodeType condition = ((ConditionNodeType)srcNode);
					for (DestWithFilterType filter : condition.getDests()) {
						if (filter.getName().equals(targetNodeName)) {
							condition.getDests().remove(filter);
							break;
						}
					}
				} else if (srcNode instanceof SplitNodeType) {
					SplitNodeType split = ((SplitNodeType)srcNode);
					for (DestWithFilterType filter : split.getDests()) {
						if (filter.getName().equals(targetNodeName)) {
							split.getDests().remove(filter);
							break;
						}
					}
				} else {
					throw new UnsupportedOperationException("Unsupport node to add connection!");
				}
				
				break;
			}
		}
		
		List<Connection> connections = flowView.getConnections();
		for (Connection conn: connections) {
			if (conn.getSourceAnchor().startsWith(srcNodeName) 
					&& conn.getTargetAnchor().startsWith(targetNodeName)) {
				connections.remove(conn);
				return;
			}
		}
	}
	
	public void refreshModel() {
		try {
			JSONArray jsonArray = new JSONArray();
			List<NodeType> nodes =  this.flowView.getNodes();
	    	for (NodeType node : nodes) {
	    		jsonArray.put(new JSONObject(node));
	    	}
	    	List<Connection> connections = this.flowView.getConnections();
	    	for (Connection conn : connections) {
	    		jsonArray.put(new JSONObject(conn));
	    	}
			
	        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
	        IDataItem dataItem = AjaxActionHelper.createDataItem();
	        dataItem.setUiid(this.getId());
	        dataItem.setJsHandler("uiflowhandler");
	        
	        Map<String, String> data = new HashMap<String, String>();
	        data.put("cmd","refreshModel");
	        data.put("data",jsonArray.toString());
	        dataItem.setData((new JSONObject(data)).toString());
	        dataItem.setFrameInfo(getFrameInfo());
	        ajaxContext.addDataItem(dataItem);
	        
		} catch (Exception e) {
			logger.error("error occurrs while refreshing table.", e);
		}
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
