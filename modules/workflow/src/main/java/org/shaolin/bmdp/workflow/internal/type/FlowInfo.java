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
package org.shaolin.bmdp.workflow.internal.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.workflow.ChildFlowNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConditionNodeType;
import org.shaolin.bmdp.datamodel.workflow.DestType;
import org.shaolin.bmdp.datamodel.workflow.DestWithFilterType;
import org.shaolin.bmdp.datamodel.workflow.EventDestType;
import org.shaolin.bmdp.datamodel.workflow.ExceptionHandlerType;
import org.shaolin.bmdp.datamodel.workflow.FlowConfType;
import org.shaolin.bmdp.datamodel.workflow.FlowType;
import org.shaolin.bmdp.datamodel.workflow.GeneralNodeType;
import org.shaolin.bmdp.datamodel.workflow.MissionNodeType;
import org.shaolin.bmdp.datamodel.workflow.NodeType;
import org.shaolin.bmdp.datamodel.workflow.SplitNodeType;
import org.shaolin.bmdp.datamodel.workflow.StartNodeType;
import org.shaolin.bmdp.workflow.exception.WorkflowConfigException;
import org.shaolin.bmdp.workflow.internal.FlowValidationResult;

public class FlowInfo implements Serializable {
	
	private static final long serialVersionUID = -5902988192420674316L;

	private final String name;

    private final Set<NodeInfo> nodes = new HashSet<NodeInfo>();;

    private final Map<String, NodeInfo> nodeMap = new HashMap<String, NodeInfo>();

    private final AppInfo app;

    private final FlowType flow;

    private final String description;
    
    private Map<String, ExceptionHandlerType> exceptionsMap;
    
    private FlowConfType conf;
    
    public FlowInfo(FlowType flow, AppInfo app) {
    	this.app = app;
    	this.flow = flow;
    	this.name = flow.getName();
    	this.description = flow.getDescription();
    }
    
    private NodeInfo checkDest(NodeInfo node, String dest, Map<String, Set<String>> graph,
            Map<String, Set<String>> synGraph, boolean exception, 
            List<FlowValidationResult> errorMessages, String appName) {
        String nodeName = node.getName();
        if (nodeMap.containsKey(dest)) {
            NodeInfo destNode = nodeMap.get(dest);
            // add line to undirected graph G(V,E)
            graph.get(nodeName).add(dest);
            graph.get(dest).add(nodeName);
            // add node to synchronized node graph
            if (synGraph != null && !node.isAsyn() && !destNode.isAsyn()) {
                synGraph.get(nodeName).add(dest);
            }
            // manual set join node's source, IEvent the source is defined in
            // handler
            if (!exception && destNode.getNodeType() == NodeInfo.Type.JOIN) {
                destNode.addSource(node);
            }
            return destNode;
        } else {
            errorMessages.add(new FlowValidationResult(appName, getName(), nodeName, 
                    "The dest ( name = " + dest + " ) defined in ( node = " + nodeName
                    + " ) is not in the ( flow = " + name + " )."));
            return null;
        }
    }

    public boolean containsNode(String name) {
        return nodeMap.containsKey(name);
    }

    public AppInfo getApp() {
        return app;
    }

    public FlowConfType getConf() {
        return conf;
    }

    public String getDescription() {
        return description;
    }

    public ExceptionHandlerType getExceptionFromName(String name) {
    	if (exceptionsMap == null) {
    		throw new IllegalStateException(this.getName() + " flow does not define any exception handler.");
    	}
        return exceptionsMap.get(name);
    }

    public String getName() {
        return name;
    }

    public NodeInfo getNodeFromName(String name) {
        return nodeMap.get(name);
    }

    public Set<NodeInfo> getNodes() {
        return nodes;
    }
    
    public String getEventConsumer() {
    	return flow.getEventConsumer();
    }

    /**
     * validate the flow: the node name in flow must be unique, the child-flow should not lead to infinite recursion,
     * the dest name must be a node name in the process
     * 
     * @param beans
     *            referred beans
     * @param configExceptions 
     * @return child flow node
     * @throws WorkflowConfigException
     */
    public Set<ChildFlowNodeType> init(Set<String> beanNames, 
            List<FlowValidationResult> errorMessages, String appName) {
        Set<ChildFlowNodeType> childFlow = new HashSet<ChildFlowNodeType>();
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        Map<String, Set<String>> synGraph = new HashMap<String, Set<String>>();
        // validate node name
        if (this.flow.getNodesAndConditionsAndSplits() != null) {
            for (NodeType n : this.flow.getNodesAndConditionsAndSplits()) {
            	NodeInfo node = new NodeInfo(n, this);
            	this.nodes.add(node);
            	
                // node name must be unique
                String nodeName = node.getName();
                if (nodeMap.keySet().contains(nodeName)) {
                    errorMessages.add(new FlowValidationResult(appName, name, nodeName, 
                            "Flow " + name + " has multiple nodes with the same name "
                            + nodeName));
                } else {
                    nodeMap.put(node.getName(), node);
                }
    
                graph.put(nodeName, new HashSet<String>());
                if (!node.isAsyn()) {
                    synGraph.put(nodeName, new HashSet<String>());
                }
            }
        }
        
        this.conf = this.flow.getConf();
        if (this.conf != null && this.conf.getExceptionHandlers() != null
        		&& this.conf.getExceptionHandlers().size() > 0) {
        	this.exceptionsMap = new HashMap<String, ExceptionHandlerType>();
        	List<ExceptionHandlerType> exceptions = this.conf.getExceptionHandlers();
            for (ExceptionHandlerType exception : exceptions) {
                if (exceptionsMap.containsKey(exception.getExceptionType())) {
                    errorMessages.add(new FlowValidationResult(appName, name, null, 
                            "In flowConf, The exception type " + exception.getExceptionType()
                            + " in flow " + name + " has already been defined."));
                } else {
                    exceptionsMap.put(exception.getExceptionType(), exception);
                }
            }
        }

        // validate dest name
        if (nodes != null) {
            validateDestNode(beanNames, errorMessages, appName, childFlow, graph, synGraph);
        }
        return childFlow;
    }

    private void validateDestNode(Set<String> beanNames, 
            List<FlowValidationResult> errorMessages, String appName,
            Set<ChildFlowNodeType> childFlow, Map<String, 
            Set<String>> graph, Map<String, Set<String>> synGraph) {
        for (NodeInfo node : nodes) {
            // validate exception handler destination
            if (node.getExceptionHandlers() != null) {
                for (ExceptionHandlerType exception : node.getExceptionHandlers()) {
                    String dest = exception.getDest();
                    if (dest != null) {
                        checkDest(node, dest, graph, synGraph, true, errorMessages, appName);
                    }
                }
            }
            if (conf != null && conf.getExceptionHandlers() != null) {
                for (ExceptionHandlerType exception : conf.getExceptionHandlers()) {
                    String dest = exception.getDest();
                    if (dest != null) {
                        checkDest(node, dest, graph, synGraph, true, errorMessages, appName);
                    }
                }
            }

            // check normal dest to identify start and end node
            if (node.getNodeType() == NodeInfo.Type.START) {
                // 1. set dest
                DestType dest = ((StartNodeType)node.getNode()).getDest();
                if (dest != null) {
                    String destName = dest.getName();
                    NodeInfo destNode = checkDest(node, destName, graph, synGraph, false, errorMessages, appName);
                    dest.setNode(destNode);
                }
                // 2.parse event producer

                // 3.event not match senario

                // 4.
            } else if (node.getNodeType() == NodeInfo.Type.END) {
            	
            	
            } else if (node.getNodeType() == NodeInfo.Type.MISSION) {
            	MissionNodeType gNode = (MissionNodeType) node.getNode();
                // 1. set dest
                DestType dest = gNode.getDest();
                if (dest != null) {
                    String destName = dest.getName();
                    NodeInfo destNode = checkDest(node, destName, graph, synGraph, false, errorMessages, appName);
                    dest.setNode(destNode);
                }
                // 2.init sub-flow ?
                // 3.What to do?

            } else if (node.getNode() instanceof GeneralNodeType) {
            	// logic node
            	GeneralNodeType gNode = (GeneralNodeType) node.getNode();
                DestType dest = gNode.getDest();
                if (dest != null) {
                    String destName = dest.getName();
                    NodeInfo destNode = checkDest(node, destName, graph, 
                            synGraph, false, errorMessages, appName);
                    dest.setNode(destNode);
                }
                EventDestType eventDest = gNode.getEventDest();
                if (eventDest != null) {
                    for (DestType entry : eventDest.getDests()) {
                        String destNameTmp = entry.getName();
                        NodeInfo destNode = checkDest(node, destNameTmp, graph, 
                                synGraph, false, errorMessages, appName);
                        entry.setNode(destNode);
                    }
                }
                
                if (node.getNodeType() == NodeInfo.Type.CHILD) {
	            	ChildFlowNodeType childNode = (ChildFlowNodeType) node.getNode();
	                childFlow.add(childNode);
                }
            } else if (node.getNodeType() == NodeInfo.Type.CONDITION) {
            	ConditionNodeType condition = (ConditionNodeType) node.getNode();
                List<DestWithFilterType> dests = condition.getDests();
                if (dests != null) {
                    for (DestType d : dests) {
                        String destName = d.getName();
                        // condition doesn't care about synchronous circle
                        NodeInfo destNode = checkDest(node, destName, graph, 
                                null, false, errorMessages, appName);
                        d.setNode(destNode);
                    }
                }
            } else if (node.getNodeType() == NodeInfo.Type.SPLIT) {
                // init split node
            	SplitNodeType split = (SplitNodeType) node.getNode();
                List<DestWithFilterType> dests = split.getDests();
                if (null != dests && dests.size() > 0) {
                    for (DestType d : dests) {
                        String destName = d.getName();
                        NodeInfo destNode = checkDest(node, destName, graph, 
                                synGraph, false, errorMessages, appName);
                        d.setNode(destNode);
                    }
                }
            }

            // validate node
            node.init(beanNames, errorMessages, appName, getName());
        }
    }

    public void reset() {
        nodeMap.clear();
        exceptionsMap.clear();
        if (nodes != null) {
            for (NodeInfo node : nodes) {
                node.reset();
            }
        }
    }
}
