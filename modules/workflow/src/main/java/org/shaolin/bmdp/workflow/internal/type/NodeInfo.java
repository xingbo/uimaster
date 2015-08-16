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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.workflow.ChildFlowNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConditionNodeType;
import org.shaolin.bmdp.datamodel.workflow.DestWithFilterType;
import org.shaolin.bmdp.datamodel.workflow.EndNodeType;
import org.shaolin.bmdp.datamodel.workflow.ExceptionHandlerType;
import org.shaolin.bmdp.datamodel.workflow.GeneralNodeType;
import org.shaolin.bmdp.datamodel.workflow.HandlerType;
import org.shaolin.bmdp.datamodel.workflow.JoinNodeType;
import org.shaolin.bmdp.datamodel.workflow.MissionNodeType;
import org.shaolin.bmdp.datamodel.workflow.NodeType;
import org.shaolin.bmdp.datamodel.workflow.SplitNodeType;
import org.shaolin.bmdp.datamodel.workflow.StartNodeType;
import org.shaolin.bmdp.workflow.internal.FlowValidationResult;

/**
 * basic node config info
 */
public class NodeInfo implements Serializable {

	public static enum Type {
        START, MISSION, CONDITION, CHILD, JOIN, SPLIT, END, LOGICAL, UNKNOWN
    }
	
	private String toString;
	
    private final String name;

    /**
     * if timeout > 0, when executetime > timeout will throw TimeoutException
     */
    private final long timeout;

    /**
     * if true, AC will stop in this node till signal()
     */
    private final boolean asyn;

    private final HandlerType processHandler;

    private final FlowInfo flow;
    
    private final NodeType node;

    private List<ExceptionHandlerType> exceptionHandlers;

    private Map<String, ExceptionHandlerType> handlerMap;
    
    private final Map<String, DestWithFilterType> filterMap = new HashMap<String, DestWithFilterType>();
    
    private final Map<String, DestWithFilterType> destMap = new HashMap<String, DestWithFilterType>();
    
    private List<DestWithFilterType> filtersInList;
    
    private String description;
    
    public NodeInfo(NodeType node, FlowInfo flow) {
    	this.node = node;
    	this.flow = flow;
    	this.name = node.getName();
		this.timeout = node.getTimeout() == null ? -1 : node.getTimeout();
    	this.asyn = node.isAsyn();
    	this.processHandler = node.getProcess();
    }
    
    public String getDescription() {
        return description;
    }

    public ExceptionHandlerType getExceptionFromName(String name) {
    	if (handlerMap == null) {
    		throw new IllegalStateException(this.getName() + " node does not define any exception handler.");
    	}
        return handlerMap.get(name);
    }

    public List<ExceptionHandlerType> getExceptionHandlers() {
    	return exceptionHandlers;
    }
    
    public FlowInfo getFlow() {
        return flow;
    }

    public String getName() {
        return name;
    }

    public String getAppName() {
        return flow.getApp().getName();
    }

    public String getFlowName() {
        return flow.getName();
    }

    public HandlerType getProcessHandler() {
        return processHandler;
    }

    public long getTimeout() {
        return timeout;
    }
    
    /**
     * initiate nodes
     */
    protected void init(Set<String> beans, List<FlowValidationResult> errorMessages, String appName,
            String flowName) {
    	
        if (this.node.getExceptionHandlers() != null) {
        	this.exceptionHandlers = new ArrayList<ExceptionHandlerType>(this.node.getExceptionHandlers());
        	this.handlerMap = new HashMap<String, ExceptionHandlerType>();
            for (ExceptionHandlerType handler : this.node.getExceptionHandlers()) {
                if (handlerMap.containsKey(handler.getExceptionType())) {
                    errorMessages.add(new FlowValidationResult(appName, flowName, null,
                            "The exception type " + handler.getExceptionType() + " in node " + name
                                    + " has already been defined."));
                } else {
                    handlerMap.put(handler.getExceptionType(), handler);
                }
            }
        }
        
        if (node instanceof StartNodeType) {
        	DestWithFilterType filter = ((StartNodeType)node).getFilter();
        	if (filter != null && filter.getName() != null) {
	            filterMap.put(filter.getName(), filter);
	            if (filter.getBean() != null) {
	                beans.add(filter.getBean()); // add start filter bean
	            }
        	}
        } else if (node instanceof ConditionNodeType) {
        	if (((ConditionNodeType)node).getBean() != null) {
                beans.add(((ConditionNodeType)node).getBean()); // add condition bean
            }
        	List<DestWithFilterType> filters = ((ConditionNodeType)node).getDests();
	        for (DestWithFilterType dest : filters) {
	        	destMap.put(dest.getName(), dest);
	            if (dest.getBean() != null) {
	                beans.add(dest.getBean());
	            }
	        }
        } else if (node instanceof MissionNodeType) {
        	DestWithFilterType filter = ((MissionNodeType)node).getFilter();
        	if (filter != null) {
	            filterMap.put(filter.getName(), filter);
	            if (filter.getBean() != null) {
	                beans.add(filter.getBean());
	            }
        	}
        } else if (node instanceof JoinNodeType) {
        	if (((JoinNodeType)node).getBean() != null) {
                beans.add(((JoinNodeType)node).getBean());
            } else if (((JoinNodeType)node).getExpression() == null && ((JoinNodeType)node).getVar() == null) {
                errorMessages.add(new FlowValidationResult(appName, flowName, getName(),
                        "The join node must have either a join bean or an join expreesion."));
            }
        } else if (node instanceof SplitNodeType) {
        	if (((SplitNodeType)node).getBean() != null) {
                beans.add(((SplitNodeType)node).getBean());
            }
        	List<DestWithFilterType> filters = ((SplitNodeType)node).getDests();
	        for (DestWithFilterType dest : filters) {
	        	destMap.put(dest.getName(), dest);
	            if (dest.getBean() != null) {
	                beans.add(dest.getBean()); // add start filter bean
	            }
	        }
        } 
    }

    public boolean containsFilter(String name) {
        return filterMap.containsKey(name);
    }
    
    public DestWithFilterType getFilterFromName(String name) {
        return filterMap.get(name);
    }
    
    public List<DestWithFilterType> getFiltersInList() {
    	if (filtersInList == null) {
    		filtersInList = new ArrayList<DestWithFilterType>();
    		Set<Map.Entry<String, DestWithFilterType>> entries = filterMap.entrySet();
    		for (Map.Entry<String, DestWithFilterType> e : entries) {
    			filtersInList.add(e.getValue());
    		}
    	}
        return filtersInList;
    }
    
    /**
     * For condition and slipt nodes
     * 
     * @param name
     * @return
     */
    public boolean containsDest(String name) {
        return destMap.containsKey(name);
    }
    
    public DestWithFilterType getDestFromName(String name) {
        return destMap.get(name);
    }
    
    public boolean isAsyn() {
        return asyn;
    }
    
    private NodeInfo srcNode;
    
    public void addSource(NodeInfo srcNode) {
    	if(getNodeType() != Type.JOIN) {
    		throw new IllegalArgumentException("Add source node must be Join type.");
    	}
    	this.srcNode = srcNode;
    }
    
    public NodeInfo getSource() {
    	return this.srcNode;
    }
    
    @Override
    public String toString() {
        if (toString == null) {
        	toString = getFlow().getApp().getName() + "." + getFlow().getName() + "." + getName();
        }
        return toString;
    }

    public Type getNodeType() {
    	if (this.node instanceof StartNodeType) {
    		return Type.START;
    	}
    	if (this.node instanceof EndNodeType) {
    		return Type.END;
    	}
    	if (this.node instanceof ChildFlowNodeType) {
    		return Type.CHILD;
    	}
    	if (this.node instanceof ConditionNodeType) {
    		return Type.CONDITION;
    	}
    	if (this.node instanceof MissionNodeType) {
    		return Type.MISSION;
    	}
    	if (this.node instanceof JoinNodeType) {
    		return Type.JOIN;
    	}
    	if (this.node instanceof SplitNodeType) {
    		return Type.SPLIT;
    	}
    	if (this.node instanceof GeneralNodeType) {
    		return Type.LOGICAL;
    	}
    	return Type.UNKNOWN;
    }

    public NodeType getNode() {
        return node;
    }

    public void reset() {
        handlerMap.clear();
        exceptionHandlers.clear();
        filterMap.clear();
    }
}
