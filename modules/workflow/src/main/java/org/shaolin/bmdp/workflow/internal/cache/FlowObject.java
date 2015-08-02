package org.shaolin.bmdp.workflow.internal.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.workflow.ChildFlowNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConditionNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConfType;
import org.shaolin.bmdp.datamodel.workflow.DestType;
import org.shaolin.bmdp.datamodel.workflow.DestWithFilterType;
import org.shaolin.bmdp.datamodel.workflow.EventDestType;
import org.shaolin.bmdp.datamodel.workflow.ExceptionHandlerType;
import org.shaolin.bmdp.datamodel.workflow.FlowImportType;
import org.shaolin.bmdp.datamodel.workflow.GeneralNodeType;
import org.shaolin.bmdp.datamodel.workflow.HandlerType;
import org.shaolin.bmdp.datamodel.workflow.JoinNodeType;
import org.shaolin.bmdp.datamodel.workflow.MissionNodeType;
import org.shaolin.bmdp.datamodel.workflow.SessionServiceType;
import org.shaolin.bmdp.datamodel.workflow.SplitNodeType;
import org.shaolin.bmdp.datamodel.workflow.StartNodeType;
import org.shaolin.bmdp.datamodel.workflow.TimeoutNodeType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.exception.ConfigException;
import org.shaolin.bmdp.workflow.internal.FlowEngine;
import org.shaolin.bmdp.workflow.internal.type.AppInfo;
import org.shaolin.bmdp.workflow.internal.type.FlowInfo;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.IWorkflowService;
import org.shaolin.bmdp.workflow.spi.SessionService;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse flow and build a memory structure for flow engine.
 */
public class FlowObject implements java.io.Serializable {
	
    private static final long serialVersionUID = 43243223432L;

    /**
	 * @param variables
	 *            the variables of the LogicNode, or the Output Data of
	 *            DisplayNode. request context
	 * @param globalVariables
	 *            the Global variables of the WebChunk. session context
	 * @return
	 */
	public static OpExecuteContext getOpParsingContext(
			List<ParamType> variables, DefaultParsingContext globalContext) {

		OpExecuteContext opContext = new OpExecuteContext();
		OOEEContext external = OOEEContextFactory.createOOEEContext();

		// set CurrentUser in session parsing context
		DefaultParsingContext local = new DefaultParsingContext();
		for (VariableType var : variables) {
			Class<?> clazz = VariableUtil.getVariableClass(var);
			local.setVariableClass(var.getName(), clazz);
		}
		local.setVariableClass(FlowEngine.EVENT_VAR_NAME, Event.class);

		external.setParsingContextObject("$", local);
		external.setParsingContextObject("@", globalContext);

		opContext.setExternalParseContext(external);
		opContext.setParsingContextObject("$", local);
		opContext.setParsingContextObject("@", globalContext);

		return opContext;
	}
	
    private static final class StartNodeRankingComparator 
        implements Comparator<NodeInfo>, Serializable {
        private static final long serialVersionUID = 6359354182039732268L;

        @Override
        public int compare(NodeInfo arg0, NodeInfo arg1) {
            return ((StartNodeType)arg1.getNode()).getRanking() - ((StartNodeType)arg0.getNode()).getRanking();
        }
    }
    
    public final class FlowCompiler {
    	
    	final DefaultParsingContext appContext = new DefaultParsingContext();
    	
    	public FlowCompiler() {
    	}

    	void init(final AppInfo appInfo) throws ConfigException {
    		ConfType conf = appInfo.getConf();
    		if (conf != null) {
    			if (logger.isTraceEnabled()) {
    				logger.trace("Init app {}", appInfo.getName());
    			}
    			Map<String, Class<?>> globalVariables = new HashMap<String, Class<?>>();
    			initServices(conf, globalVariables);
    			initSession(conf, globalVariables);

    			Set<Map.Entry<String, Class<?>>> entries = globalVariables.entrySet();
    			for (Map.Entry<String, Class<?>> e : entries) {
    				appContext.setVariableClass(e.getKey(), e.getValue());
    			}
    			appContext.setVariableClass(FlowEngine.SESSION_VAR_NAME, WorkflowSession.class);
    			appContext.setVariableClass(FlowEngine.EVENT_VAR_NAME, Event.class);
    			
    			try {
					initExceptionHandlers(appContext, conf.getExceptionHandlers(), appInfo.getName());
				} catch (ParsingException e1) {
					throw new ConfigException(e1.getMessage(), e1);
				}
    		}

    	}

    	private void initServices(ConfType conf, Map<String, Class<?>> globalVariables) throws ConfigException {
    		List<VariableType> services = conf.getServices();
    		if (services == null) {
    			return;
    		}

    		for (VariableType service : services) {
    			String serviceName = service.getName();
    			if (logger.isTraceEnabled()) {
    				logger.trace("Init service {} : {}", serviceName, service);
    			}
    			try {
					globalVariables.put(serviceName, Class.forName(service.getType().getEntityName()));
				} catch (ClassNotFoundException e) {
					throw new ConfigException(e.getMessage(), e);
				}
    		}
    	}

    	private void initSession(ConfType conf, Map<String, Class<?>> globalVariables) throws ConfigException {
    		SessionServiceType sessionConfig = conf.getSessionService();
    		if (sessionConfig != null) {
    			try {
    				globalVariables.put(sessionConfig.getName(),
    						Class.forName(sessionConfig.getSessionClass()));
    			} catch (ClassNotFoundException e) {
    				throw new ConfigException(e.getMessage());
    			}
    		}
    	}

    	public void initExceptionHandlers(DefaultParsingContext globalContext, 
    			List<ExceptionHandlerType> exceptionHandlers, String classPrefix) throws ParsingException {
    		if (exceptionHandlers == null) {
    			return;
    		}
    		for (ExceptionHandlerType handler : exceptionHandlers) {
    			if (handler.getExpression() != null) {
					OpExecuteContext opContext = getOpParsingContext(handler
							.getVars(), globalContext);
					handler.getExpression().parse(opContext);
    			}
    		}
    	}


    	public void initStartNode(DefaultParsingContext globalContext, 
    			StartNodeType start, String classPrefix) throws ParsingException {
    		if (start.getProcess() == null
    				|| start.getProcess().getExpression() == null) {
    			return;
    		}
    		OpExecuteContext opContext = getOpParsingContext(start.getProcess()
    				.getVars(), globalContext);
    		initConditionFilters(opContext, start.getFilter(), classPrefix);
    	}

    	public void initMissionNodeInfo(DefaultParsingContext globalContext, 
    			MissionNodeType interNode, String classPrefix) throws ParsingException {
    		if (interNode.getProcess() == null
    				|| interNode.getProcess().getExpression() == null) {
    			return;
    		}

    		OpExecuteContext opContext = getOpParsingContext(interNode.getProcess()
    				.getVars(), globalContext);
    		initConditionFilters(opContext, interNode.getFilter(), classPrefix);
    	}

    	public void initConditionFilters(OpExecuteContext opContext,
    			DestWithFilterType filter, String classPrefix) throws ParsingException {
    		if (filter != null && filter.getExpression() != null) {
				filter.getExpression().parse(opContext);
			}
    	}
    	
    	public void initConditionFilters(OpExecuteContext opContext,
    			List<DestWithFilterType> set, String classPrefix) throws ParsingException {
    		for (DestWithFilterType filter : set) {
    			if (filter.getExpression() != null) {
					filter.getExpression().parse(opContext);
    			}
    		}
    	}

    	public void initHandlerInfo(DefaultParsingContext globalContext, HandlerType handlerInfo, 
    			String className) throws ParsingException {
    		if (handlerInfo == null || handlerInfo.getExpression() == null) {
    			return;
    		}
    		
			handlerInfo.getExpression().parse(
					getOpParsingContext(handlerInfo.getVars(), globalContext));
    	}

    	public void initConditionHandlerInfo(DefaultParsingContext globalContext, 
    			ConditionNodeType conditionNode, String classPrefix) throws ParsingException {
    		OpExecuteContext opContext = getOpParsingContext(
    				conditionNode.getVars(), globalContext);
    		if (conditionNode.getExpression() != null) {
    			conditionNode.getExpression().parse(opContext);
    		}

			initConditionFilters(opContext, conditionNode.getDests(), classPrefix);
    	}

    	public void initSplitHandlerInfo(DefaultParsingContext globalContext, 
    			SplitNodeType split, String className) throws ParsingException {
    		OpExecuteContext opContext = getOpParsingContext(
    				split.getVars(), globalContext);
    		if (split.getExpression() != null) {
    			split.getExpression().parse(opContext);
    		}
			initConditionFilters(opContext, split.getDests(), className);
    	}

    	public void initJoinHandlerInfo(DefaultParsingContext globalContext, 
    			JoinNodeType join, String className) throws ParsingException {
    		if (join.getExpression() != null) {
    			return;
    		}
			join.getExpression().parse(
					getOpParsingContext(join.getVars(), globalContext));
    	}

    }

    private static final Logger logger = LoggerFactory.getLogger(FlowObject.class);

    private final AppInfo appInfo;
    private FlowCompiler flowCompiler;
	
    /**
     * global vars structures: <workflow/app name, Map<flow name, flow config var>>
     */
    private final Map<String, DefaultParsingContext> globalContext = new HashMap<String, DefaultParsingContext>();
    private final Map<String, Object> globalDefaultValues = new HashMap<String, Object>();
    private final Map<String, Map<String, List<String>>> globalVarNames = 
            new HashMap<String, Map<String, List<String>>>();
    private final Map<String, Map<String, Set<String>>> globalVarNamesSet = 
            new HashMap<String, Map<String, Set<String>>>();
    private final Map<String, Map<String, Map<String, NodeInfo>>> nodeMap = 
            new HashMap<String, Map<String, Map<String, NodeInfo>>>();
    /**
     * key. appname.flowname.nodename
     */
    private final Map<String, HashMap<String, Object>> localDefaultValues = new HashMap<String, HashMap<String, Object>>();
    
    private final Map<String, List<NodeInfo>> missionNodes = 
            new HashMap<String, List<NodeInfo>>(); // IntermediateNodeType
    private final Map<String, List<NodeInfo>> requestNodes = 
            new HashMap<String, List<NodeInfo>>(); // IntermediateNodeType
    private final Map<String, List<NodeInfo>> startNodes = 
            new HashMap<String, List<NodeInfo>>(); //StartNodeType
    private final List<NodeInfo> exceptionNodes = 
            new ArrayList<NodeInfo>(); //IntermediateNodeType
    
    //linkedUIPage.linkedUIAction, flowname.nodename
    private final Map<String, String> entrances = new HashMap<String, String>();
    
    private boolean isSessionBased = false;

    private Class<SessionService> sessionService;
    
    private HashMap<String, Class<IServiceProvider>> services;
    
    private boolean initialized = false;
    
	protected final transient OpExecuteContext opContext;

    public FlowObject(AppInfo appInfo) {
        this.appInfo = appInfo;
        this.opContext = new OpExecuteContext();
    }

    public void parse() throws ConfigException {
    	if (initialized) {
    		return;
    	}
    	initialized = true;
    	
    	appInfo.init();
    	
        initConf(appInfo.getName(), appInfo.getConf());

        flowCompiler = new FlowCompiler();
        flowCompiler.init(appInfo);
        
        parseApp(appInfo);
    }
    
    public AppInfo getAppInfo() {
    	return this.appInfo;
    }
    
    public Map<String, Class<IServiceProvider>> getServices() {
    	return this.services;
    }
    
    public Class<?> getSessionService() {
    	if (isSessionBased) {
    		return sessionService;
    	} 
    	return null;
    }
    
    private void initConf(String appName, ConfType conf) throws ConfigException {
        if (conf != null) {
            SessionServiceType sessionConfig = conf.getSessionService();
            if (sessionConfig != null) {
                isSessionBased = true;
                try {
					sessionService = (Class<SessionService>) Class.forName(sessionConfig.getType());
				} catch (ClassNotFoundException e) {
					throw new ConfigException(e.getMessage(), e);
				}
            }
            
            if (conf.getServices() != null && conf.getServices().size() > 0) {
	            services = new HashMap<String, Class<IServiceProvider>>();
	            for (VariableType var : conf.getServices()) {
	            	try {
	            		services.put(var.getName(), (Class<IServiceProvider>) Class.forName(var.getType().getEntityName()));
	            	} catch (ClassNotFoundException e) {
						throw new ConfigException(e.getMessage(), e);
					}
	            }
            }
            
        } 
        if (logger.isTraceEnabled()) {
            if (isSessionBased) {
                logger.trace("APP: {} is session based", appName);
            } else {
                logger.trace("APP: {} is not session based: {}", appName);
            }
        }
    }

    private void parseApp(AppInfo appInfo)
            throws ConfigException {
        Map<String, Set<String>> initialEventNodes = new HashMap<String, Set<String>>();
        handleImportedFlows(appInfo, initialEventNodes);
        parseFlows(appInfo, initialEventNodes, false);

//        for (List<NodeInfo> nList : missionNodes.values()) {
//            for (NodeInfo n : nList) {
//                if (BuiltInEventProducer.EXCEPTION_PRODUCER_NAME.equals(((MissionNodeType)n.getNode()).getEventConsumer())) {
//                    exceptionNodes.add(n);
//                }
//            }
//        }
        
        for (List<NodeInfo> nList : startNodes.values()) {
            Collections.sort(nList, new StartNodeRankingComparator());
        }

        for (Map.Entry<String, List<NodeInfo>> e : missionNodes.entrySet()) {
            List<NodeInfo> l = new ArrayList<NodeInfo>();
            for (NodeInfo n : e.getValue()) {
            	MissionNodeType m = (MissionNodeType)n.getNode();
            	if (m.getActionPage() == null) {
            		continue;
            	}
            	try {
            		//TODO: add the dynamic ui.
        			UIFormObject uiCache = PageCacheManager.getUIFormObject(m.getActionPage());
        			m.getActionPosition();
        			m.getActionProperty();
//        			uiCache.addDynamicItem(object);
        		} catch (EntityNotFoundException e0) {
        			try {
        				UIPageObject uiCache = PageCacheManager.getUIPageObject(m.getActionPage());
        				m.getActionPosition();
            			m.getActionProperty();
        				//uiCache.getUIForm().addDynamicItem(object);
        			} catch (Exception e1) {
        				logger.error("Error to load the dynamic UI items: " + e0.getMessage(), e0);
        			} 
        		} 
//            	catch (ParsingException | ClassNotFoundException e1) {
//        			logger.error("Error to load the dynamic UI items: " + e1.getMessage(), e1);
//        		}
//            	
                Set<String> set = initialEventNodes.get(n.getAppName() + "-" + n.getFlowName());
                if (set != null && set.contains(n.getName())) {
                    l.add(n);
                }
            }
            if (!l.isEmpty()) {
                requestNodes.put(e.getKey(), l);
            }
        }
    }

    private void handleImportedFlows(AppInfo appInfo, Map<String, Set<String>> initialEventNodes) throws ConfigException {
        Set<FlowImportType> importInfos = appInfo.getImportedApps();
        if (importInfos != null) {
            for (FlowImportType importInfo : importInfos) {
                String appName = importInfo.getApp();
                if (appInfo.getName().equals(appName)) {
                    continue;
                }
                
                //import the referred workflow if it's not imported.
                FlowObject importFlowObject = AppContext.get().getService(IWorkflowService.class)
                			.getFlowObject(importInfo.getApp());
                importFlowObject.parse();
            }
        }
    }

    private void parseFlows(AppInfo appInfo, Map<String, Set<String>> initialEventNodes, boolean parseOnly) throws ConfigException {
        Set<FlowInfo> flows = appInfo.getFlows();
        Map<String, Map<String, NodeInfo>> appNodeMap = new HashMap<String, Map<String, NodeInfo>>();
        nodeMap.put(appInfo.getName(), appNodeMap);

        if (flows != null) {
            for (FlowInfo flow : flows) {
            	if (logger.isTraceEnabled()) {
                	logger.trace("parse flow node: {}.", flow.getName());
                }
            	
            	if (flow.getEventConsumer() != null) {
            		entrances.put(flow.getEventConsumer(), flow.getName());
            	}
            	
            	DefaultParsingContext flowContext = initGlobalVars(flowCompiler.appContext, flow);
                if (flow.getConf() != null) {
                    try {
						flowCompiler.initExceptionHandlers(flowContext, flow.getConf().getExceptionHandlers(),
						        appInfo.getName() + "_" + flow.getName());
					} catch (ParsingException e) {
						throw new ConfigException(e.getMessage(), e);
					}
                }
                Map<String, NodeInfo> nodeInfoMap = new HashMap<String, NodeInfo>();
                appNodeMap.put(flow.getName(), nodeInfoMap);
                Set<String> intermediateEventNodes = new HashSet<String>();
                for (NodeInfo node : flow.getNodes()) {
                    nodeInfoMap.put(node.getName(), node);
                    String classPrefix = appInfo.getName() + "_" + flow.getName() + "_"
                            + node.getName();
                    if (!parseOnly) {
                        try {
                        	flowCompiler.initExceptionHandlers(flowContext, node.getExceptionHandlers(), classPrefix);
							initNode(flowContext, intermediateEventNodes, node, classPrefix);
						} catch (ClassNotFoundException e) {
							throw new ConfigException(e.getMessage(), e);
						} catch (ParsingException e) {
							throw new ConfigException(e.getMessage(), e);
						}
                    }
                }

                if (!parseOnly) {
                    injectDestNode(flow, intermediateEventNodes);
                    initialEventNodes.put(appInfo.getName() + "-" + flow.getName(),
                            intermediateEventNodes);
                }
            }
        }
        
		if (logger.isTraceEnabled()) {
			logger.trace("Global vars map: {}", globalVarNames);
			logger.trace("Initial event nodes: {}", initialEventNodes);
			logger.trace("Mission event nodes: {}", missionNodes);
			logger.trace("Start event nodes: {}", startNodes);
		}
    }

    private DefaultParsingContext initGlobalVars(DefaultParsingContext appContext, FlowInfo flow) {
        List<String> globalVars = new ArrayList<String>();
        Set<String> globalVarsSet = new HashSet<String>();
        Map<String, List<String>> _global = globalVarNames.get(flow.getApp().getName());
        if (_global == null) {
            _global = new HashMap<String, List<String>>();
            globalVarNames.put(flow.getApp().getName(), _global);
            globalVarNamesSet.put(flow.getApp().getName(), new HashMap<String, Set<String>>());
        }
        _global.put(flow.getName(), globalVars);
        globalVarNamesSet.get(flow.getApp().getName()).put(flow.getName(), globalVarsSet);
        
        DefaultParsingContext context = new DefaultParsingContext(appContext.getVariableTypes());
        globalContext.put(flow.getName(), context);
        
        if (flow.getConf() == null || flow.getConf().getParams() == null) {
            return context;
        }
        for (VariableType param : flow.getConf().getParams()) {
            if (!globalVarsSet.contains(param.getName())) {
                globalVarsSet.add(param.getName());
            }
            context.setVariableClass(param.getName(), VariableUtil.getVariableClass(param));
            globalDefaultValues.put(param.getName(), VariableUtil.createVariableObject(param));
        }
        globalVars.addAll(globalVarsSet);
        return context;
    }

    private void initNode(DefaultParsingContext flowContext, Set<String> intermediateEventNodes, NodeInfo node,
            String classPrefix) throws ConfigException, ClassNotFoundException, ParsingException {
    	
    	if (node.getProcessHandler() != null && node.getProcessHandler().getVars().size() > 0) {
			HashMap<String, Object> values = new HashMap<String, Object>();
			for (ParamType param : node.getProcessHandler().getVars()) {
				values.put(param.getName(), VariableUtil.createVariableObject(param));
			}
    		localDefaultValues.put(node.toString(), values);
    	}
    	
		switch (node.getNodeType()) {
            case START: {
                StartNodeType start = (StartNodeType) node.getNode();
                if (logger.isTraceEnabled()) {
                	logger.trace("parse start node: {}", start.getName());
                }
                flowCompiler.initStartNode(flowContext, start, classPrefix);
                flowCompiler.initHandlerInfo(flowContext, node.getProcessHandler(), classPrefix);
                appendEventNode(node.getFlow().getEventConsumer(), node, startNodes);
                break;
            }
            case MISSION: {
            	MissionNodeType inode = (MissionNodeType) node.getNode();
            	if (logger.isTraceEnabled()) {
            		logger.trace("parse mission node: {}", inode.getName());
            	}
            	flowCompiler.initMissionNodeInfo(flowContext, inode, classPrefix);
                flowCompiler.initHandlerInfo(flowContext, node.getProcessHandler(), classPrefix);
                appendEventNode(node.getFlow().getEventConsumer(), node, missionNodes);
                break;
            }
            case CONDITION:
            	ConditionNodeType conditionNode = (ConditionNodeType) node.getNode();
            	if (logger.isTraceEnabled()) {
            		logger.trace("parse condition node: {}", conditionNode.getName());
            	}
            	flowCompiler.initConditionHandlerInfo(flowContext, conditionNode, classPrefix);
                break;
            case CHILD:
                ChildFlowNodeType childNode = (ChildFlowNodeType) node.getNode();
                if (logger.isTraceEnabled()) {
            		logger.trace("parse child node: {} -> {}.{}.{}", 
            				new Object[]{childNode.getName(), childNode.getApp(), childNode.getFlow(), childNode.getStart()});
            	}
                flowCompiler.initHandlerInfo(flowContext, childNode.getProcess(), classPrefix + "PreProcess");
                flowCompiler.initHandlerInfo(flowContext, childNode.getPostProcess(), classPrefix + "PostProcess");
                break;
            case SPLIT:
                SplitNodeType split = (SplitNodeType) node.getNode();
                if (logger.isTraceEnabled()) {
            		logger.trace("parse split node: {}", split.getName());
            	}
                flowCompiler.initSplitHandlerInfo(flowContext, split, classPrefix);
                break;
            case JOIN:
            	JoinNodeType join = (JoinNodeType) node.getNode();
            	if (logger.isTraceEnabled()) {
            		logger.trace("parse join node: {}", join.getName());
            	}
            	flowCompiler.initJoinHandlerInfo(flowContext, join, classPrefix);
                break;
            case LOGICAL:
            	if (logger.isTraceEnabled()) {
            		logger.trace("parse logic node: {}", node.getName());
            	}
            	flowCompiler.initHandlerInfo(flowContext, node.getProcessHandler(), classPrefix);
                break;
		default:
			break;
        }
    }

    private <T> void appendEventNode(String key, T o, Map<String, List<T>> nodeMap) {
        List<T> list = nodeMap.get(key);
        if (list == null) {
            list = new ArrayList<T>();
            nodeMap.put(key, list);
        }
        list.add(o);
    }
    
    private void injectDestNode(FlowInfo flow, Set<String> eventNodes) throws ConfigException {
        for (NodeInfo node : flow.getNodes()) {
            if (node.getNode() instanceof GeneralNodeType) {
                DestType dest2 = ((GeneralNodeType) node.getNode()).getDest();
                if (dest2 != null) {
                    dest2.setNode(_getNode(flow.getApp().getName(), flow.getName(), dest2.getName()));
                }
                EventDestType eventDest = ((GeneralNodeType) node.getNode()).getEventDest();
                if (eventDest == null) {
                    continue;
                }
                List<DestType> dests = eventDest.getDests();
                for (DestType dest : dests) {
                    dest.setNode(_getNode(flow.getApp().getName(), flow.getName(), dest.getName()));
                    eventNodes.remove(dest.getName());
                }
            } else if (node.getNode() instanceof ConditionNodeType) {
            	ConditionNodeType cNode = (ConditionNodeType) node.getNode();
                List<DestWithFilterType> dests = cNode.getDests();
                for (DestWithFilterType dest : dests) {
                    dest.setNode(_getNode(flow.getApp().getName(), flow.getName(), dest.getName()));
                }
            } else if (node.getNode() instanceof TimeoutNodeType) {
                DestType dest2 = ((TimeoutNodeType) node.getNode()).getTimeoutDest();
                if (dest2 != null) {
                    dest2.setNode(_getNode(flow.getApp().getName(), flow.getName(), dest2.getName()));
                }
            }
        }
    }

    public NodeInfo getNode(String appName, String flowName, String nodeName) {
        try {
            return _getNode(appName, flowName, nodeName);
        } catch (ConfigException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private NodeInfo _getNode(String appName, String flowName, String nodeName)
            throws ConfigException {
        Map<String, Map<String, NodeInfo>> map = nodeMap.get(appName);
        if (map == null) {
            throw new ConfigException("App " + appName + " not exist.");
        }
        Map<String, NodeInfo> map2 = map.get(flowName);
        if (map2 == null) {
            throw new IllegalArgumentException("The flow " + flowName + " in " + appName
                    + " not exist");
        }
        NodeInfo nodeInfo = map2.get(nodeName);
        if (nodeInfo == null) {
            throw new IllegalArgumentException("The node " + nodeName + " in flow " + flowName
                    + " of " + appName + " not exist");
        }
        return nodeInfo;
    }

    public List<NodeInfo> getStartNodes(String producerName) {
        return this.startNodes.get(producerName);
    }

    public List<NodeInfo> getMissionRequestNodes(String producerName) {
        return this.requestNodes.get(producerName);
    }

    public List<NodeInfo> getExceptionNodes() {
        return exceptionNodes;
    }

    public HashMap<String, Object> getGlobalDefaultValues() {
    	return new HashMap<String, Object>(globalDefaultValues);
    }
    
    public HashMap<String, Object> getLocalDefaultValues(NodeInfo currentNode) {
    	return localDefaultValues.get(currentNode.toString());
    }
    
    public List<String> getGlobalVarNames(NodeInfo currentNode) {
        return globalVarNames.get(currentNode.getAppName()).get(currentNode.getFlowName());
    }
    
    public Set<String> getGlobalVarNamesSet(NodeInfo currentNode) {
        return globalVarNamesSet.get(currentNode.getAppName()).get(currentNode.getFlowName());
    }

    public List<String> getGlobalVarNames(String appName, String childFlowName) {
        return globalVarNames.get(appName).get(childFlowName);
    }

    public Set<String> getGlobalVarNamesSet(String appName, String childFlowName) {
        return globalVarNamesSet.get(appName).get(childFlowName);
    }
    
    public boolean isSessionBased() {
        return isSessionBased;
    }

    public Map<String, String> getEventConsumers() {
        return this.entrances;
    }
    
    @Override
    public String toString() {
        return appInfo.getName();
    }
}
