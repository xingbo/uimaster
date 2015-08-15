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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.workflow.ChildFlowNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConfType;
import org.shaolin.bmdp.datamodel.workflow.ExceptionHandlerType;
import org.shaolin.bmdp.datamodel.workflow.ExceptionType;
import org.shaolin.bmdp.datamodel.workflow.FlowImportType;
import org.shaolin.bmdp.datamodel.workflow.FlowType;
import org.shaolin.bmdp.datamodel.workflow.SessionServiceType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.bmdp.workflow.exception.FlowValidationException;
import org.shaolin.bmdp.workflow.internal.FlowValidationResult;

public class AppInfo implements Serializable {
	
    private Set<FlowInfo> flows;

    private Set<FlowImportType> importedApps;

    private final Map<String, FlowInfo> flowMap = new HashMap<String, FlowInfo>();

    private final Map<String, ExceptionHandlerType> exceptionHandlerMap = new HashMap<String, ExceptionHandlerType>();
    
    private final Map<String, String> exceptionNameMap = new HashMap<String, String>();

    private final Set<String> serviceNames = new HashSet<String>();

    public static final String ENTITY_TYPE = "workflow";

    private List<String> referring;
    
    private final Workflow workflow;
    
    public AppInfo(Workflow type) {
    	this.workflow = type;
    }
    
    public boolean containsFlow(String name) {
        return flowMap.containsKey(name);
    }

    public String getName() {
    	return this.workflow.getEntityName();
    }
    
    public ConfType getConf() {
        return this.workflow.getConf();
    }

    public ExceptionHandlerType getExceptionFromName(String exceptionName) {
        return exceptionHandlerMap.get(exceptionName);
    }

    public FlowInfo getFlowFromName(String name) {
        return flowMap.get(name);
    }

    public Set<FlowInfo> getFlows() {
        return flows;
    }

    public List<String> getReferring() {
        if (this.referring == null) {
            this.referring = new ArrayList<String>();
        }

        return this.referring;
    }

    public void init() {
        List<FlowValidationResult> errorMessages = new ArrayList<FlowValidationResult>();
        init(errorMessages);
        if (!errorMessages.isEmpty()) {
            throw new FlowValidationException(this.getName(), errorMessages);
        }
    }
    /**
     * validate, initiate expressions using Script Engine, get dependencies
     * 
     * @return the dependent bean names or List of WorkflowConfigException.
     */
    public void init(List<FlowValidationResult> errorMessages) {
        this.reset();
        Set<String> beans = new HashSet<String>();

        importedApps = new HashSet<FlowImportType>(this.workflow.getImports());
        ConfType conf = this.workflow.getConf();
		if (conf != null) {
        	SessionServiceType sessionService = conf.getSessionService();
            if (sessionService != null && sessionService.getBean() != null) {
                beans.add(sessionService.getBean());
            }

            List<VariableType> varInfos = conf.getServices();
            if (null != varInfos && varInfos.size() > 0) {
                for (VariableType var : varInfos) {
                    String varName = var.getName();
                    String beanName = VariableUtil.getVariableClassName(var);
                    if (serviceNames.contains(varName)) {
                        errorMessages.add(new FlowValidationResult(getName(), null, null, "In appConf ( appName = "
                                + this.getName() + " ) has multiple variables with the same name " + varName));
                    }
                    if (beanName != null) {
                        beans.add(beanName);
                    }
                    serviceNames.add(varName);
                }
            }
            
            if (conf.getExceptionHandlers() != null) {
                for (ExceptionHandlerType handler : conf.getExceptionHandlers()) {
                    if (handler.getDest() != null) {
                        errorMessages.add(new FlowValidationResult(getName(), null, null, 
                                "The destination of exception in app "
                                + getName() + " is not allowed."));
                    }
                    if (exceptionHandlerMap.containsKey(handler.getExceptionType())) {
                        errorMessages.add(new FlowValidationResult(getName(), null, null, "The exception type "
                                + handler.getExceptionType() + " in app " + getName()
                                + " has already been defined.)"));
                    } else {
                        exceptionHandlerMap.put(handler.getExceptionType(), handler);
                    }
                    beans.add(handler.getBean());
                }
            }
            
            if (conf.getExceptionTypes() != null) {
                for (ExceptionType t : conf.getExceptionTypes()) {
                    if (exceptionNameMap.containsKey(t.getType())) {
                        errorMessages.add(new FlowValidationResult(getName(), null, null, "The exception type "
                                + t.getType() + " in app " + getName()
                                + " has already been declared.)"));
                    } else {
                        exceptionNameMap.put(t.getType(), t.getName());
                    }
                }
            }
        }

		Map<String, Set<ChildFlowNodeType>> childFlows = new HashMap<String, Set<ChildFlowNodeType>>();
        // init flows
		List<FlowType> fs = this.workflow.getFlows();
        if (fs != null) {
        	this.flows = new HashSet<FlowInfo>(fs.size());
            for (FlowType f: fs) {
            	FlowInfo flow = new FlowInfo(f, this);
                this.flows.add(flow);
                
                // flow name must be unique in an Application
                if (flowMap.containsKey(flow.getName())) {
                    errorMessages.add(new FlowValidationResult(getName(), flow.getName(), null, "App " + getName()
                            + " has duplicated flows with the same name " + flow.getName()));
                } else {
                    Set<ChildFlowNodeType> childFlow = flow.init(beans, errorMessages, getName());
                    childFlows.put(flow.getName(), childFlow);
                    flowMap.put(flow.getName(), flow);
                }
            }
        }

        /**
        for (Map.Entry<String, Set<ChildFlowNodeType>> entry : childFlows.entrySet()) {
            String flowName = entry.getKey();
            Set<ChildFlowNodeType> childFlow = entry.getValue();
            Set<String> flowInGraph = new HashSet<String>();
            for (ChildFlowNodeType child : childFlow) {
                String appName = child.getApp();
                String childName = child.getFlow();
                String startNode = child.getStart();
                if (appName != null) {
                    continue; //TODO; need validate against other workflow apps;
                }
                flowInGraph.add(childName);
                // child-flow name must refer an exist flow
                if (flowMap.containsKey(childName)) {
                    FlowInfo childFlowInfo = flowMap.get(childName);
                    child.setChildFlow(childFlowInfo);
                    // child-flow start node must in the flow
                    if (flowMap.get(childName).containsNode(startNode)) {
                        NodeInfo node = flowMap.get(childName).getNodeFromName(startNode);
                        child.setStart(node);
                    } else {
                        errorMessages.add(new FlowValidationResult(getName(), flowName, child.getName(), 
                                "The start ( node = " + startNode
                                        + " ) is not defined in the ( child flow = " + childName + " )."));
                    }
                } else {
                    errorMessages.add(new FlowValidationResult(getName(), flowName, child.getName(),
                            "The ( child-flow = "
                            + childName + " ) doesn't have a referred flow."));
                }

            }
        }
        */
    }

    public Set<FlowImportType> getImportedApps() {
        return importedApps;
    }
    
    public void reset() {
        flowMap.clear();
        exceptionHandlerMap.clear();
        serviceNames.clear();
        exceptionNameMap.clear();
        
        if (flows != null) {
            for (FlowInfo flow : flows) {
                flow.reset();
            }
        }
    }

    public String getExceptionName(String name) {
        return exceptionNameMap.get(name);
    }

}
