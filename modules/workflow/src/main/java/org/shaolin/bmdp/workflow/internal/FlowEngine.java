package org.shaolin.bmdp.workflow.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.shaolin.bmdp.datamodel.workflow.CancelTimeoutNodeType;
import org.shaolin.bmdp.datamodel.workflow.ChildFlowNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConditionNodeType;
import org.shaolin.bmdp.datamodel.workflow.DestType;
import org.shaolin.bmdp.datamodel.workflow.DestWithFilterType;
import org.shaolin.bmdp.datamodel.workflow.EventDestType;
import org.shaolin.bmdp.datamodel.workflow.ExceptionHandlerType;
import org.shaolin.bmdp.datamodel.workflow.GeneralNodeType;
import org.shaolin.bmdp.datamodel.workflow.SplitNodeType;
import org.shaolin.bmdp.datamodel.workflow.TimeoutNodeType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.exception.ConfigException;
import org.shaolin.bmdp.workflow.exception.EventException;
import org.shaolin.bmdp.workflow.internal.cache.FlowObject;
import org.shaolin.bmdp.workflow.internal.type.AppInfo;
import org.shaolin.bmdp.workflow.internal.type.FlowInfo;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.EventProducer.ErrorType;
import org.shaolin.bmdp.workflow.spi.ExceptionEvent;
import org.shaolin.bmdp.workflow.spi.Node;
import org.shaolin.bmdp.workflow.spi.SessionService;
import org.shaolin.bmdp.workflow.spi.TimeoutEvent;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow engine is the container of a flow template. A flow template has 
 * its owner flow engine accordingly. When engine starts that all defined 
 * producers will be generated as EventConsumers for event processing.
 * <br>
 * The EventProducer can be many or one such CCR event producer, flow 
 * timeout event producer or etc.
 * <br>
 * The engine is created by AppInfo.name which represents the "engineName" here.
 * <br>
 * For example:
 * <br>1. template AppInfo.name = OCSEventProducer
 * <br>2. template AppInfo.name = OCSEventProducer1
 * <br>Thus, each flow template has a FlowEngine created accordingly. 
 * <br>
 * <br> An event driven process:
 * An event received --> EventProcessor --> EventConsumers --> EventProducer
 * <br>
 * For example: wired Connector with Workflow instance while engine initialing.
 * <br>1. Connector ProtocolNode will be exposed as EventProcessor service.
 * <br>2. Define the EventProducer in worklfow template which wires to connector identifier.
 * <br>3. the all real EventProcessor services will be available in com.hp.atom.commons.container.Container
 * <br>4. Flow engine created by template and generated EventConsumers according to the defined EventProcessor.
 * <br>5. create WorkFlowEventProcessor with EventConsumers together.
 * <br>6. FlowContainer registers WorkFlowEventProcessor to all EventProcessor service after flow engine ready.
 * 
 *  Shaolin Wu(July 20th, 2013)
 */
public class FlowEngine {
    // Built-in varaible name.
    public static final String EVENT_VAR_NAME = "event";
    public static final String SESSION_VAR_NAME = "session";

    private static final Logger logger = LoggerFactory.getLogger(FlowEngine.class);

    private final String engineName;
    private final FlowContainer flowContainer;

    private final LockManager<Object> masterLock = new LockManager<Object>();
    private final ConcurrentMap<String, Queue<Event>> pendingEvents = 
            new ConcurrentHashMap<String, Queue<Event>>();
    private final LockManager<Object> slaveLock = new LockManager<Object>();

    private final WorkFlowEventProcessor timeoutEventProcessor;
    private final ConcurrentMap<Object, FlowRuntimeContext> timerMap = 
            new ConcurrentHashMap<Object, FlowRuntimeContext>();

    private FlowObject flowInfo;
    private SessionService sessionService;
    private Map<String, IServiceProvider> services;
    private final boolean isManagedTransaction;
    private final ThreadLocal<Boolean> activeTranstactionFlag = new ThreadLocal<Boolean>();

    public FlowEngine(String engineName, FlowContainer flowContainer, boolean isManagedTransaction) {
        this.engineName = engineName;
        this.flowContainer = flowContainer;
        this.isManagedTransaction = isManagedTransaction;
        
		Map<String, EventConsumer> tempProcessors = new HashMap<String, EventConsumer>();
		tempProcessors.put(BuiltInEventProducer.TIMEOUT_PRODUCER_NAME, 
				new EventConsumer(this,	BuiltInEventProducer.TIMEOUT_PRODUCER_NAME));
		timeoutEventProcessor = new WorkFlowEventProcessor(tempProcessors);
    }

    /**
     * Parse all flows.
     * 
     * @param appInfo
     * @param javaCompiler
     * @param varContext
     *                  complied object parameters
     * @throws ConfigException
     */
    public void init(FlowObject flowInfo) throws ConfigException {
    	this.flowInfo = flowInfo;
    	this.flowInfo.parse();
        this.sessionService = (SessionService) AppContext.get().getService(this.flowInfo.getSessionService());
    }

    /**
     * Create EventConsumers with all defined producers in current flow accordingly.
     * Each EventConsumer has a EventProducer registered, when an event
     * received that all EventConsumers will be asked for processing till 
     * the first EventConsumer met or throws an exception NO_MATCHED_PROCESSOR.
     * 
     * One workflow may has multiple event producers or zero as the sub flow.
     * 
     * @param processors
     */
    public void start(Map<String, EventConsumer> processors) {
        this.pendingEvents.clear();
        this.timerMap.clear();
        Map<String, String> consumerNames = flowInfo.getEventConsumers();
        for (Map.Entry<String, String> e : consumerNames.entrySet()) {
        	if (processors.containsKey(e.getKey())) {
        		throw new IllegalStateException("The even consumer name is duplicated! " + e.getKey());
        	}
        	
            EventConsumer processor = new EventConsumer(this, e.getKey());
            processors.put(e.getKey(), processor);
        }
        
        Map<String, Class<IServiceProvider>> spis = this.flowInfo.getServices();
        if (spis != null && spis.size() > 0) {
	        Set<Map.Entry<String, Class<IServiceProvider>>> set = spis.entrySet();
	        this.services = new HashMap<String, IServiceProvider>();
	        for (Map.Entry<String, Class<IServiceProvider>> spi: set) {
	        	this.services.put(spi.getKey(), AppContext.get().getService(spi.getValue()));
	        }
        }
    }

    public void stop() {
        this.pendingEvents.clear();
        this.timerMap.clear();
    }

    public String getEngineName() {
        return this.engineName;
    }

    public String getSessionId(Event event) {
        return this.sessionService.getSessionId(event);
    }
    
    public Map<String, IServiceProvider> getServices() {
    	return this.services;
    }
    
    public Map<String, Object> getDefaultGlobalVariables() {
    	return flowInfo.getGlobalDefaultValues();
    }

    public void signal(NodeInfo node, FlowRuntimeContext flowContext) throws Throwable {
        NodeInfo nextNode = node;
        NodeInfo lastExpNode = null;
        List<NodeInfo> visitedExceptionNodes = null;
        while (true) {
            try {
                executeNode(nextNode, flowContext);
                break;
            } catch (Throwable e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Exception when execute {} on {}",
                            new Object[] { flowContext.getEvent(), nextNode });
                    logger.trace("Detail trace: " + e.getMessage(), e);
                }
                flowContext.setException(e);
                try {
                    nextNode = handleException(flowContext, e);
                } catch (Throwable ex) {
                    logger.warn("Fail to handle exception for " + flowContext + ", " + e.getMessage(), ex);
                    flowContext.setException(e);
                    break;
                }
                if (nextNode != null) {
                    if (visitedExceptionNodes != null && visitedExceptionNodes.contains(nextNode)) {
                        flowContext.setException(e);
                        logger.warn("Inifnit loop detected when handle exception, the loop node is {}.", nextNode);
                        break;
                    } else {
                        lastExpNode = nextNode;
                        if (visitedExceptionNodes == null) {
                            visitedExceptionNodes = new ArrayList<NodeInfo>();
                        }
                        visitedExceptionNodes.add(lastExpNode);
                    }
                } else {
                    break;
                }
            }
        }
                
        if (flowContext.getException() == null) {
            NodeInfo previousNode = flowContext.getCurrentNode();
            if (NodeInfo.Type.END.equals(previousNode.getNodeType())) {
                destroySession(flowContext);
            } else if (flowContext.getEventDestInfo() == null) {
                // No next, flush session here.
                flushSession(flowContext.getSession(), flowContext);
            } else {
                saveSession(flowContext.getSession(), flowContext);
            }
        } else {
            throw flowContext.getException();
        }
    }

    private void executeNode(NodeInfo _node, FlowRuntimeContext flowContext) throws Exception {
        NodeInfo currentNode = _node;
        do {
            flowContext.setCurrentNode(currentNode);
            if (logger.isTraceEnabled()) {
                logger.trace("{}:Enter node {}:{}:{}:{}", new Object[] {
                        flowContext.getEvent().getId(), engineName, currentNode.getAppName(),
                        currentNode.getFlowName(), currentNode.getName() });
            }
            
            flowContext.setLocallVariables(flowInfo.getLocalDefaultValues(currentNode));
            flowContext.setGlobalVarNames(flowInfo.getGlobalVarNames(currentNode), 
                    flowInfo.getGlobalVarNamesSet(currentNode));
            flowContext.getEvent().setAttribute(Node.class.getName(), currentNode.getNode());

            switch (currentNode.getNodeType()) {
                case LOGICAL:
                case START:
                case INTERMEDIATE:
                    currentNode = processGeneralNode(flowContext, currentNode);
                    break;
                case CONDITION:
                    currentNode = processConditionNode(flowContext, currentNode, flowContext.getEvent());
                    flowContext.resetLocalVariables();
                    break;
                case CHILD:
                    currentNode = processChildFlow(flowContext, currentNode);
                    break;
                case JOIN:
                    break;
                case SPLIT:
                     SplitNodeType split = (SplitNodeType) currentNode.getNode();
                     String destNodeName = null;
                	 if (split.getExpression() != null) {
                         destNodeName = (String)split.getExpression().evaluate(flowContext);
                     }
                     if (destNodeName != null) {
                         if (!currentNode.containsDest(destNodeName)) {
                             throw new EventException("The split() destination " + destNodeName + 
                            		 " in node " + currentNode.toString() + " can not be resolved");
                         }
                         currentNode = (NodeInfo)currentNode.getDestFromName(destNodeName).getNode();
                     }
                    break;
                case TIMER:
                    processTimerNode(flowContext, currentNode);
                    currentNode = null;
                    break;
                case CANCELTIMER:
                    cancelTimer(flowContext, currentNode);
                    currentNode = processGeneralNode(flowContext, currentNode);
                    break;
                case END:
                    currentNode = null;
			default:
				break;
            }
            if (logger.isTraceEnabled()) {
                NodeInfo previousNode = flowContext.getCurrentNode();
                logger.trace("{}:Leave node {}:{}:{}:{}", new Object[] {
                        flowContext.getEvent().getId(), engineName, previousNode.getAppName(),
                        previousNode.getFlowName(), previousNode.getName() });
            }
        } while (currentNode != null);
    }

    private NodeInfo processGeneralNode(FlowRuntimeContext flowContext, NodeInfo node)
            throws Exception {
    	GeneralNodeType n = (GeneralNodeType)node.getNode();
        executeHandler(flowContext, flowContext.getEvent(), node);
        flowContext.resetLocalVariables();

        if (n.getEventDest() != null) {
            // Should waiting response.
            // Removing existing pending timeout event first
            flowContext.removePendingTimeout();
            registerTimer(flowContext, node);
            flowContext.setEventDestInfo(n.getEventDest());
            return null;
        }
        if (n.getDest() == null) {
            NodeInfo parentNode = flowContext.pop();
            NodeInfo destNode = null;
            while (parentNode != null) {
            	ChildFlowNodeType childNode = (ChildFlowNodeType)parentNode.getNode();
                flowContext.mapVariables(childNode.getOuputMappings());
                if (childNode.getPostProcess() != null) {
                    Event newEvt = (Event)childNode.getPostProcess().getExpression().evaluate(flowContext);
                    if (newEvt != null && newEvt != flowContext.getEvent()) {
                        flowContext.changeEvent(newEvt);
                    }
                    flowContext.resetLocalVariables();
                } else {
                    flowContext.mapGlobalVariables();
                }
                if (childNode.getEventDest() != null) {
                    // Should waiting response.
                    // Removing existing pending timeout event first
                    flowContext.removePendingTimeout();
                    registerTimer(flowContext, parentNode);
                    flowContext.setEventDestInfo(childNode.getEventDest());
                    break;
                }
                if (childNode.getDest() != null) {
                    // back from sub flow
                    destNode = (NodeInfo)childNode.getDest().getNode();
                    break;
                }
                parentNode = flowContext.pop();
            }
            return destNode;
        } else {
            return (NodeInfo)n.getDest().getNode();
        }
    }

    private NodeInfo processConditionNode(FlowRuntimeContext flowContext, NodeInfo cNode, Event evt) throws Exception {
    	ConditionNodeType node = (ConditionNodeType)cNode.getNode();
        String destNodeName = null;
        NodeInfo destNode = null;
        if (node.getExpression() != null) {
            destNodeName = (String)node.getExpression().evaluate(flowContext);
        }
        if (destNodeName != null) {
            if (!cNode.containsDest(destNodeName)) {
                throw new EventException("The condition() destination "+destNodeName
                		+" in node "+cNode.toString()+" can not be resolved");
            }
            destNode = (NodeInfo)cNode.getDestFromName(destNodeName).getNode();
        } else {
            for (DestWithFilterType dest: node.getDests()) {
            	boolean flag = (boolean)dest.getExpression().evaluate(flowContext);
                if (flag) {
                    destNode = (NodeInfo)dest.getNode();
                    break;
                }
            }
        }
        if (destNode != null) {
            return destNode;
        } else {
            throw new EventException("No matched dest node in node " + cNode.toString());
        }
    }

    private NodeInfo processChildFlow(FlowRuntimeContext flowContext, NodeInfo node)
            throws Exception {
    	ChildFlowNodeType child = (ChildFlowNodeType)node.getNode();
        NodeInfo currentNode;
        if (child.getProcess() != null && child.getProcess().getBean() != null) {
            executeHandler(flowContext, flowContext.getEvent(), node);
            flowContext.resetLocalVariables();
        }
        flowContext.mapVariables(child.getInputMappings());
        
        currentNode = flowInfo.getNode(child.getApp(), child.getFlow(),
        		child.getStart());
        flowContext.push(node, currentNode, 
                flowInfo.getGlobalVarNames(child.getApp(), child.getFlow()),
                flowInfo.getGlobalVarNamesSet(child.getApp(), child.getFlow()));
        return currentNode;
    }

    private void processTimerNode(FlowRuntimeContext flowContext, NodeInfo currentNode)
            throws Exception {
        // Timeout node should not in subflow
    	TimeoutNodeType tNode = (TimeoutNodeType) currentNode.getNode();
        executeHandler(flowContext, flowContext.getEvent(), currentNode);

        registerTimer(flowContext, currentNode);
        // One session can have only one timer at same time
        FlowRuntimeContext oldOne = timerMap.put(flowContext.getSession().getID(), flowContext);
        if (oldOne != null && oldOne.getTimeoutFuture() != null) {
            oldOne.getTimeoutFuture().cancel();
            oldOne.setTimeoutFuture(null);
        }
    }

    private void cancelTimer(FlowRuntimeContext flowContext, NodeInfo currentNode) {
    	CancelTimeoutNodeType n = (CancelTimeoutNodeType) currentNode.getNode();
        String timeoutNodeName = n.getTimeoutNode();
        String flowName = n.getFlow();
        if (flowName == null) {
            flowName = n.getFlow();
        }
        NodeInfo tNode = flowInfo.getNode(currentNode.getAppName(), flowName, timeoutNodeName);
        FlowRuntimeContext oldContext = timerMap.get(flowContext.getSession().getID());
        if (oldContext != null && oldContext.getCurrentNode() == tNode) {
            timerMap.remove(flowContext.getSession().getID());
            if (oldContext.getTimeoutFuture() != null) {
                oldContext.getTimeoutFuture().cancel();
                oldContext.setTimeoutFuture(null);
            }
            if (logger.isTraceEnabled()) {
                logger.trace("{}:Unregister timer on {}:{}:{}", new Object[] {
                        flowContext.getEvent().getId(), currentNode.getAppName(),
                        currentNode.getFlow().getName(), timeoutNodeName });
            }
        }
    }

    private void executeHandler(FlowRuntimeContext flowContext, Event evt, NodeInfo n)
            throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("Process node {}", n.getName());
        }
        if (n.getProcessHandler() == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("No process handler in node {}", n.getName());
            }
            return;
        }

        Event newEvt = (Event)n.getProcessHandler().getExpression().evaluate(flowContext);
        if (newEvt != null && newEvt != evt) {
            flowContext.changeEvent(newEvt);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Process node {} successfully.", n.getName());
        }
    }

    private void registerTimer(FlowRuntimeContext flowContext, NodeInfo n) {
        if (logger.isTraceEnabled()) {
            logger.trace("Schedule timer on {}, dealy time is {}", n.getName(), n.getTimeout());
        }
        flowContainer.scheduleTask(n.getTimeout(), flowContext.getFlowContextInfo(), 
        		flowContext, this);
    }

    private NodeInfo handleException(FlowRuntimeContext flowContext, Throwable e) {
        NodeInfo nextNode = null;

        NodeInfo currentNode = flowContext.getCurrentNode();
        ExceptionHandlerType handler = matchException(e, currentNode);
        flowContext.resetLocalVariables();
        ExceptionEvent newEvent = new ExceptionEvent(e, currentNode, flowContext);
        
        newEvent.setId(BuiltInEventProducer.EXCEPTION_PRODUCER_NAME
                + flowContext.getEvent().getId());
                
        if (handler == null || handler.getDest() == null) {
            NodeInfo subFlowNode = flowContext.pop();
            while (subFlowNode != null) {
                ExceptionHandlerType subFlowExpHandler = matchException(e, subFlowNode);
                if (subFlowExpHandler != null && (
                    subFlowExpHandler.getBean() != null || subFlowExpHandler.getDest() != null)) {
                    handler = subFlowExpHandler;
                    currentNode = subFlowNode;
                    newEvent = new ExceptionEvent(e, subFlowNode, flowContext);
                    newEvent.setId(BuiltInEventProducer.EXCEPTION_PRODUCER_NAME
                            + flowContext.getEvent().getId());
                    break;
                }
                subFlowNode = flowContext.pop();
            }
        }
        
        if (handler != null && handler.getDest() != null) {
            flowContext.setEvent(newEvent);
            if (logger.isTraceEnabled()) {
                logger.trace("Goto exception dest node {}", handler.getDest());
            }
            nextNode = flowInfo.getNode(currentNode.getAppName(), currentNode.getFlow().getName(),
                    handler.getDest());
        } else {
            Event oldEvent = flowContext.getEvent();
            flowContext.setEvent(newEvent);
            newEvent.setId(BuiltInEventProducer.EXCEPTION_PRODUCER_NAME + oldEvent.getId());
            NodeInfo nodeInfo = matchExceptionNode(newEvent);
            if (nodeInfo != null) {
                nextNode = nodeInfo;
                flowContext.clearSubFlowInfo();
            } else {
                flowContext.setEvent(oldEvent);
            }
        }
        if (nextNode != null) {
            flowContext.setException(null);
        } else if (logger.isTraceEnabled()) {
            logger.trace("No exception dest node for {}", e.getClass());
        }
        return nextNode;
    }

    private String getExceptionName(Class<?> clazz, AppInfo app) {
        Class<?> c = clazz;
        String expName;
        while (c != null) {
            expName = app.getExceptionName(c.getName());
            if (expName != null) {
                return expName;
            } else {
                c = c.getSuperclass();
            }
        }
        return null;
    }
    
    private ExceptionHandlerType matchException(Throwable e, NodeInfo currentNode) {
        AppInfo app = currentNode.getFlow().getApp();
        Throwable exception = e;
        while (exception != null) {
            String expName = getExceptionName(exception.getClass(), app);
            if (expName != null) {
            	ExceptionHandlerType handler = currentNode.getExceptionFromName(expName);
                if (handler != null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Match local exception handler {} for {}", handler.getName(),
                                e.getClass());
                    }
                    return handler;
                }
            }
            exception = exception.getCause();
        }
        exception = e;
        while (exception != null) {
            FlowInfo flow = currentNode.getFlow();
            String expName = getExceptionName(exception.getClass(), app);
            if (expName != null) {
                ExceptionHandlerType handler = flow.getExceptionFromName(expName);
                if (handler != null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Match local exception handler {} for {}", handler.getName(),
                                e.getClass());
                    }
                    return handler;
                }
            }
            exception = exception.getCause();
        }

        if (logger.isTraceEnabled()) {
            logger.trace("No matched exception handler for {}", e.getClass());
        }
        return null;
    }

    private void destroySession(FlowRuntimeContext flowContext) {
        String sessionId = flowContext.getSession().getID();
        if (logger.isTraceEnabled()) {
            logger.trace("{}:Destroy session {}", engineName, sessionId);
        }
        
        sessionService.destroySession(flowContext.getSession());
        flowContext.setSessionDestroyed(true);
        stopTransaction(true, flowContext.getSession());
    }

    private void flushSession(WorkflowSession session, FlowRuntimeContext flowContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}:Flush session {}", engineName, session.getID());
        }
        switch (session.getTXFlag()) {
            case WorkflowSession.COMMIT:
                sessionService.commitSession(session);
                stopTransaction(true, session);
                break;
            case WorkflowSession.ROLLBACK:
                sessionService.rollbackSession(session);
                stopTransaction(false, session);
                break;
        }
    }

    public void saveSession(WorkflowSession session, FlowRuntimeContext flowContext) {
        sessionService.pasueSession(session);
        if (isManagedTransaction) {
            if (activeTranstactionFlag.get() == null) {
                throw new IllegalStateException("Transaction has not start");
            }
            Object state = flowContainer.pauseTransaction();
            flowContext.saveState(state);
            activeTranstactionFlag.set(null);
            if (logger.isTraceEnabled()) {
                logger.trace("{}:Save transaction on {}",
                        new Object[] { engineName, session.getID() });
            }
        } else {
            flowContext.saveState(null);
        }
    }

    public WorkflowSession createSession(Event event, String id) {
        startTransaction(event);
        WorkflowSession s = sessionService.createSession(event, id);
        if (s != null && logger.isTraceEnabled()) {
            logger.trace("{}:Create session {}", engineName, s.getID());
        }
        return s;
    }

    public WorkflowSession getSession(Event event, String id) {
        startTransaction(event);
        return sessionService.getSession(event, id);
    }

    public void recoverSession(WorkflowSession session, FlowRuntimeContext flowContext) {
        if (isManagedTransaction) {
            Object transactionState = flowContext.getTransactionState();
            flowContainer.resumeTransaction(transactionState);

            flowContext.clearState();
            activeTranstactionFlag.set(Boolean.TRUE);
            if (logger.isTraceEnabled()) {
                logger.trace("{}:Recover transaction on {}",
                        new Object[] { engineName, session.getID() });
            }
        } else {
            flowContext.clearState();
        }
        sessionService.resumeSession(session);
    }

    public void rollbackSession(FlowRuntimeContext flowContext, WorkflowSession session) {
        if (logger.isTraceEnabled()) {
            logger.trace("{}:Rollback session {}", engineName, session.getID());
        }
        // should avoid rollback session throw exception.
        try {
            if (session.getTXFlag() != WorkflowSession.NOTHING) {
                sessionService.rollbackSession(session);
            }
        } catch (Throwable ex) {
            logger.warn("Exception when rollback session {}", ex, session.getID());
        }
        stopTransaction(false, session);
    }

    private void startTransaction(Event e) {
        if (!isManagedTransaction) {
            return;
        }
        if (activeTranstactionFlag.get() == null) {
            flowContainer.startTransaction();
            activeTranstactionFlag.set(Boolean.TRUE);
            if (logger.isTraceEnabled()) {
                logger.trace("{}:Start transaction on {}", engineName, e.getId());
            }
        }
    }

    private void stopTransaction(boolean isCommit, WorkflowSession s) {
        if (!isManagedTransaction) {
            return;
        }
        if (activeTranstactionFlag.get() == null) {
            throw new IllegalStateException("Transaction has not start");
        }
        if (isCommit) {
            flowContainer.commitTransaction();
        } else {
            flowContainer.rollbackTransaction();
        }
        if (logger.isTraceEnabled()) {
            logger.trace("{}:Stop transaction on {}, commit: {}",
                    new Object[] { engineName, s.getID(), isCommit });
        }
        activeTranstactionFlag.set(null);
    }

    public boolean lockSession(String sessionId, Event event, FlowRuntimeContext newFlowContext,
            NodeInfo destNode) {
        if (masterLock.tryLock(sessionId)) {
            masterLock.detachLock(sessionId);
            if (logger.isTraceEnabled()) {
                logger.trace("{}:lock {}", engineName, sessionId);
            }
            return true;
        } else {
            slaveLock.acquireLock(sessionId);
            if (masterLock.tryLock(sessionId)) {
                slaveLock.releaseLock(sessionId);
                masterLock.detachLock(sessionId);
                if (logger.isTraceEnabled()) {
                    logger.trace("{}:lock {}", engineName, sessionId);
                }
                return true;
            } else {
                Queue<Event> events = pendingEvents.get(sessionId);
                if (events != null) {
                    events.offer(event);
                } else {
                    events = new ArrayDeque<Event>();
                    events.add(event);
                    pendingEvents.put(sessionId, events);
                }
                event.setAttribute(BuiltInAttributeConstant.KEY_NODE, destNode);
                event.setAttribute(BuiltInAttributeConstant.KEY_RUNTIME, newFlowContext);
                slaveLock.releaseLock(sessionId);
                if (logger.isTraceEnabled()) {
                    logger.trace("{}: Queue a event on session {}", engineName, sessionId);
                }
                return false;
            }
        }
    }

    public Event unlockSession(String sessionId, boolean isSessionDestroyed) {
        Event pendingEvent = null;

        slaveLock.acquireLock(sessionId);

        Queue<Event> discardEvents = null;
        if (!isSessionDestroyed) {
            Queue<Event> events = pendingEvents.get(sessionId);
            if (events != null) {
                pendingEvent = events.poll();
                if (pendingEvent == null || events.isEmpty()) {
                    pendingEvents.remove(sessionId);
                }
            }
        } else {
            Queue<Event> events = pendingEvents.remove(sessionId);
            if (events != null) {
                discardEvents = events;
            }
        }

        if (pendingEvent != null) {
            slaveLock.releaseLock(sessionId);
            if (logger.isTraceEnabled()) {
                logger.trace("Keep locking session {}, next event is {}", sessionId, pendingEvent);
            }
            return pendingEvent;
        } else {
            masterLock.attachLock(sessionId);
            masterLock.releaseLock(sessionId);
            slaveLock.releaseLock(sessionId);
            if (logger.isTraceEnabled()) {
                logger.trace("Unlock {}", sessionId);
            }
            if (discardEvents != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "Discard pending events {} on {}, since the session was destroyed",
                            discardEvents, sessionId);
                }
                for (Event request : discardEvents) {
                    discardRequest(request, ErrorType.SESSION_CLOSED);
                }
            }
        }
        return null;
    }

    public void checkSessionState() {
        if (!isManagedTransaction) {
            return;
        }
        if (activeTranstactionFlag.get() != null) {
            flowContainer.rollbackTransaction();
            activeTranstactionFlag.set(null);
        }
    }

    private void discardRequest(Event request, ErrorType type) {
        logger.info("Discard event {}, since the session was closed", request);
    }

    //IntermediateNodeInfo
    public NodeInfo matchExceptionNode(Event event) {
        List<NodeInfo> list = flowInfo.getExceptionNodes();
        for (int i = 0, t = list.size(); i < t; i++) {
        	NodeInfo node = list.get(i);
            List<DestWithFilterType> filters = node.getFiltersInList();
            if (matchEventFilter(event, filters)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Event {} matched with {}:{}", new Object[] { event.getId(),
                            node.getFlow().getName(), node.getName() });
                }
                return node;
            }
        }
        return null;
    }

    public NodeInfo matchRequestNode(String producerName, Event event) {
        if (!flowInfo.isSessionBased()
                && !BuiltInEventProducer.EXCEPTION_PRODUCER_NAME.equals(producerName)) {
            return null;
        }
        List<NodeInfo> list = flowInfo.getIntermediateRequestNodes(producerName);
        if (list == null) {
            return null;
        }
        for (int i = 0, t = list.size(); i < t; i++) {
        	NodeInfo node = list.get(i);
            List<DestWithFilterType> filters = node.getFiltersInList();
            if (matchEventFilter(event, filters)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Event {} matched with {}:{}", new Object[] { event.getId(),
                            node.getFlow().getName(), node.getName() });
                }
                return node;
            }
        }
        return null;
    }

    public NodeInfo matchResponseNode(String producerName, EventDestType nodeInfo,
            Event event) {
        for (DestType dest: nodeInfo.getDests()) {
        	NodeInfo node = (NodeInfo) dest.getNode();
            if (producerName.equals(node.getEventProducer())) {
                List<DestWithFilterType> filters = node.getFiltersInList();
                if (matchEventFilter(event, filters)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Event {} matched with {}:{}", new Object[] { event.getId(),
                                node.getFlow().getName(), node.getName() });
                    }
                    return node;
                }
            }
        }

        return null;
    }

    public NodeInfo matchStartNode(String producerName, Event event) {
        List<NodeInfo> list = flowInfo.getStartNodes(producerName);
        if (list == null || list.size() == 0) {
            return null;
        }
        for (NodeInfo start: list) {
            List<DestWithFilterType> filters = start.getFiltersInList();
            if (matchEventFilter(event, filters)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Event {} matched with {}:{}", new Object[] { event.getId(),
                            start.getFlow().getName(), start.getName() });
                }
                return start;
            }
        }
        return null;
    }

    public NodeInfo matchTimeoutNode(EventDestType nodeInfo, Event event) {
        for (DestType dest : nodeInfo.getDests()) {
        	NodeInfo node = (NodeInfo) dest.getNode();
            if (BuiltInEventProducer.TIMEOUT_PRODUCER_NAME.equals(event.getEventConsumer())) {
                List<DestWithFilterType> filters = node.getFiltersInList();
                if (matchEventFilter(event, filters)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Event {} matched with {}:{}", new Object[] { event.getId(),
                                node.getFlow().getName(), node.getName() });
                    }
                    return node;
                }
            }
        }

        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean matchEventFilter(Event event, List<DestWithFilterType> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        FlowRuntimeContext flowContext = (FlowRuntimeContext) event.getAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT);
        for (int i = 0, t = filters.size(); i < t; i++) {
            DestWithFilterType filter = filters.get(i);
            try {
            	boolean flag = (boolean)filter.getExpression().evaluate(flowContext);
                if (flag) {
                    return true;
                }
            } catch (Throwable e) {
                analyzeException(flowContext,e);
                logger.warn("Exception when run filter on node", e);
                continue;
            }
        }
        return false;
    }

    public void timeout(FlowContextImpl context) {
        FlowRuntimeContext flowRuntime = context.getFlowRuntime();
        NodeInfo waitingNode = context.getWaitingNode();
        
        if (NodeInfo.Type.TIMER == waitingNode.getNodeType()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Receive timer time out event on {} ", context.getFlowRuntime()
                        .getCurrentNode().getName());
            }
            if (timerMap.get(flowRuntime.getSession().getID()) == flowRuntime) {
                TimeoutEvent event = new TimeoutEvent(flowRuntime, flowRuntime.getSession().getID());
                event.setFlowContext(context);
                flowContainer.runTask(timeoutEventProcessor, event);
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Receive normal time out event on {} ", context.getFlowRuntime()
                        .getCurrentNode().getName());
            }
            if (flowRuntime.flowContextMatched(context)) {
                TimeoutEvent event = new TimeoutEvent(flowRuntime.getSession().getID());
                event.setFlowContext(context);
                flowContainer.runTask(timeoutEventProcessor, event);
            }
        }
    }

    public boolean removeTimer(String id, FlowRuntimeContext context) {
        return timerMap.remove(id, context);
    }

    public void discardResponse(Event response, boolean isContextClosed) {
        if (isContextClosed) {
            logger.info("Discard event {}, since the context is closed", response);
        } else {
            logger.info("Discard event {}, can not found matched node", response);
        }
    }

    public void analyzeException(FlowRuntimeContext flowContext, Throwable ex) {
        NodeInfo currentNode = flowContext.getCurrentNode();
        Event event = flowContext.getEvent();
        if (event != null) {
        	//TODO:
        }
    }

    public void triggerEventListener(Event event, NodeInfo node, String sessionId) {
//        List<WorkflowEventListener> list = flowInfo.getEventListener();
//        for (int i = 0, t = list.size(); i < t; i++) {
//            WorkflowEventListener eventListener = list.get(i);
//            if (eventListener.accept(event, node)) {
//                eventListener.notify(event, node, sessionId);
//            }
//        }
    }
}
