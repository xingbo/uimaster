package org.shaolin.bmdp.workflow.internal;

import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.workflow.EventDestType;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.internal.FlowContainer.TimerTask;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.TimeoutEvent;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow runtime context, store everything related to flow state.
 * 
 */
public final class FlowRuntimeContext extends OpExecuteContext implements FlowVariableContext, Serializable {

	private static final long serialVersionUID = -6150417408203161229L;

	private static final class FlowState {
        private final NodeInfo currentNode;
        private final DefaultEvaluationContext globalVariables;
        private final List<String> globalVarNames;
        private final Set<String> globalVarNamesSet;

        public FlowState(NodeInfo childNode, List<String> globalVarNames, 
                Set<String> globalVarNamesSet, DefaultEvaluationContext globalVariables) {
            this.currentNode = childNode;
            this.globalVarNames = globalVarNames;
            this.globalVarNamesSet = globalVarNamesSet;
            this.globalVariables = globalVariables;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(FlowRuntimeContext.class);
    
    private Event event;
    
    private final FlowEngine engine;
    private final Event requestEvent;
    private FlowContextImpl flowContextInfo;

    private List<String> globalVarNames;
    private Set<String> globalVarNamesSet;
    private final DefaultEvaluationContext globalVariables = new DefaultEvaluationContext();
    private final DefaultEvaluationContext localVariables = new DefaultEvaluationContext();

    // event process lock
    private final ReentrantLock mainLock = new ReentrantLock();
    // event queue lock
    private final ReentrantLock queueLock = new ReentrantLock();
    private Deque<Event> pendingResponseEvents;
    
    private WorkflowSession session;
    private boolean sessionDestroyed;

    private Deque<FlowState> stacks;
    
    private Object transactionState;
    private boolean recoverable;
    private TimerTask timeoutFuture;

    private volatile boolean isClosed = false;

    private NodeInfo currentNode;
    private EventDestType destInfo;
    private String sessionId;
    private NodeInfo startNode;
    private NodeInfo eventNode; // IntermediateNodeType
    private Throwable exception;

    @SuppressWarnings("rawtypes")
    private static final Map<Class, Object> primitiveDefaultValues = new HashMap<Class, Object>();
    private static final Map<String, Object> primitiveDefaultValues2 = new HashMap<String, Object>();
    static {
        primitiveDefaultValues.put(int.class, 0);
        primitiveDefaultValues.put(byte.class, 0);
        primitiveDefaultValues.put(char.class, 0);
        primitiveDefaultValues.put(short.class, 0);
        primitiveDefaultValues.put(long.class, 0);
        primitiveDefaultValues.put(float.class, 0);
        primitiveDefaultValues.put(double.class, 0);
        primitiveDefaultValues.put(boolean.class, false);

        primitiveDefaultValues2.put(int.class.getName(), 0);
        primitiveDefaultValues2.put(byte.class.getName(), 0);
        primitiveDefaultValues2.put(char.class.getName(), 0);
        primitiveDefaultValues2.put(short.class.getName(), 0);
        primitiveDefaultValues2.put(long.class.getName(), 0);
        primitiveDefaultValues2.put(float.class.getName(), 0);
        primitiveDefaultValues2.put(double.class.getName(), 0);
        primitiveDefaultValues2.put(boolean.class.getName(), false);
    }

    public FlowRuntimeContext(Event event, FlowEngine engine) {
        this.engine = engine;
        this.event = event;
        
        this.requestEvent = event;
        event.setAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT, this);
        flowContextInfo = new FlowContextImpl(this);
        event.setAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT, flowContextInfo);
        
        try {
			if (this.engine.getServices() != null) {
				Set<Map.Entry<String, IServiceProvider>> set = this.engine.getServices().entrySet();
		        for (Map.Entry<String, IServiceProvider> spi: set) {
		        	globalVariables.setVariableValue(spi.getKey(), spi.getValue());
		        }
			}
			globalVariables.setVariableValue(FlowEngine.EVENT_VAR_NAME, event);
			
			Set<Map.Entry<String, Object>> set = this.engine.getDefaultGlobalVariables().entrySet();
			for (Map.Entry<String, Object> spi: set) {
	        	globalVariables.setVariableValue(spi.getKey(), spi.getValue());
	        }
		} catch (EvaluationException e) {
		}
        this.setEvaluationContextObject("@", globalVariables);
        this.setEvaluationContextObject("$", localVariables);
    }
    
    public boolean match(FlowEngine engine) {
        return this.engine == engine;
    }

    public boolean flowContextMatched(FlowContextImpl inputContext) {
        if (isClosed) {
            if (logger.isTraceEnabled()) {
                logger.trace("Context for {} is closed", this.session.getID());
            }
            return false;
        }
        if (inputContext.getWaitingNode() != currentNode) {
        	logger.info("Current waiting node is " + inputContext.getWaitingNode() 
        			+ ", input node is " + currentNode);
        }
    	return inputContext.getWaitingNode() == currentNode;
    }

    public void startNewFlow(boolean isTimingOut) {
        this.flowContextInfo = new FlowContextImpl(this);
        this.event.setAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT, flowContextInfo);
        this.destInfo = null;
        if (isTimingOut) {
            this.timeoutFuture = null;
        } else if (timeoutFuture != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Cancel timeout task on {}", currentNode.getName());
            }
            this.timeoutFuture.cancel();
            this.timeoutFuture = null;
        }
    }

    /**
     * Try to acquire a lock for this event. Several situations below:
     * <br>1. if mainLock is acquired, this event can be processed immediately.
     * <br>2. if mainLock is acquired and flow processed to end(isClosed=true),
     *    discard this event.
     * <br>3. If mainLock is not acquired, it means this lock occupied by 
     *    another event or other words that the original flow is still 
     *    being processed. Add this response event into the pending list.
     * @param event
     * @return
     */
    public boolean lock(Event event) {
        if (mainLock.isHeldByCurrentThread()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Event {} got the lock on same thread.", event.getId());
            }
            queueLock.lock();

            if (pendingResponseEvents == null) {
                pendingResponseEvents = new LinkedList<Event>();
            }
            pendingResponseEvents.offer(event);
            queueLock.unlock();
            if (logger.isTraceEnabled()) {
                logger.trace("Add event {} into queue", event.getId());
            }
            return false;
        }
        if (mainLock.tryLock()) {
            if (isClosed) {
                mainLock.unlock();
                engine.discardResponse(event, true);
                return false;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Event {} got the lock", event.getId());
            }
            return true;
        } else {
            queueLock.lock();
            if (isClosed) {
                queueLock.unlock();
                engine.discardResponse(event, true);
                return false;
            }
            if (mainLock.tryLock()) {
                queueLock.unlock();
                if (logger.isTraceEnabled()) {
                    logger.trace("Event {} got the lock", event.getId());
                }
                return true;
            } else {
                if (pendingResponseEvents == null) {
                    pendingResponseEvents = new LinkedList<Event>();
                }
                pendingResponseEvents.offer(event);
                queueLock.unlock();
                if (logger.isTraceEnabled()) {
                    logger.trace("Add event {} into queue", event.getId());
                }
                return false;
            }
        }
    }

    public Event unlock(boolean isFlowEnded) {
        return unlock(isFlowEnded, event);
    }

    /**
     * This method will be invoked in below situations:
     * 1. when a flow processed to the terminal/exit node.
     * 2. when a flow processed to an event waiting node.
     * 
     * *: This method MUST invoked before unlock method!
     */
    public void recordState() {
        this.event = null;
        flowContextInfo.setWaitingNode(currentNode);
    }
    
    /**
     * Try to release an event from the message queue.
     * <br>1. If flow processed to end(isFlowEnded=true), discard all events.
     * <br>2. If message queue has events, poll the first one.
     * <br>3. If message queue is empty, release mainLock in standby mode.
     * 
     * @param isFlowEnded
     * @param currentEvent
     * @return
     */
    public Event unlock(boolean isFlowEnded, Event currentEvent) {
        if (logger.isTraceEnabled() && currentEvent != null) {
            logger.trace("Finish event {}, unlock context, flow ended: {}", currentEvent.getId(),
                    isFlowEnded);
        }
        
        queueLock.lock();
        Event pending = null;
        if (isFlowEnded) {
            isClosed = true;
            mainLock.unlock();
            queueLock.unlock();
            if (timeoutFuture != null && !(NodeInfo.Type.TIMER.equals(currentNode.getNodeType()))) {
                timeoutFuture.cancel();
                timeoutFuture = null;
            }
            if (pendingResponseEvents != null && !pendingResponseEvents.isEmpty()) {
                for (Event pendingEvent : pendingResponseEvents) {
                    engine.discardResponse(pendingEvent, true);
                }
            }
            return null;
        }

        if (pendingResponseEvents != null) {
            pending = pendingResponseEvents.poll();
        }
        if (pending != null) {
            queueLock.unlock();
            if (logger.isTraceEnabled()) {
                logger.trace("Poll event {} from queue", pending.getId());
            }
            return pending;
        } else {
        	mainLock.unlock();
            queueLock.unlock();
            return null;
        }
    }

    public void removePendingTimeout() {
        // when the response event is received before timeout event but fails to cancel the timeout event in time,
        // and the flow has to wait for other response event(s), the pending timeout event needs to be removed and
        // a new timeout event will be scheduled
        if (logger.isTraceEnabled()) {
            logger.trace("Remove expired timeout event");
        }
        try {
            queueLock.lock();
            if (pendingResponseEvents != null && !pendingResponseEvents.isEmpty()) {
                for (Iterator<Event> iterator = pendingResponseEvents.iterator(); iterator
                        .hasNext();) {
                    Event pendingEvent = iterator.next();
                    if (pendingEvent instanceof TimeoutEvent) {
                        iterator.remove();
                        if (logger.isTraceEnabled()) {
                            logger.trace("Expired timeout event: {} was removed from the context",
                                    pendingEvent.getId());
                        }
                        break;
                    }
                }
            }
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * Prepare for invoking child flow. the variable context of invoker 
     * will be pushed into a stack. When child flow finished, it will be
     * popped again.
     * 
     * @param nodeInfo
     * 			current node
     * @param childInfo
     * 			child node
     * @param childGlobalVarNames
     * 			child global var names
     * @param childGlobalVarNamesSet
     * 			child global var name set
     */
    public void push(NodeInfo nodeInfo, NodeInfo childInfo, List<String> childGlobalVarNames,
            Set<String> childGlobalVarNamesSet) {
        if (stacks == null) {
            stacks = new LinkedList<FlowState>();
        }
        // Child flow input rule : Child flow first node can only access the previous node output
        // of the child flow node. 
        stacks.push(new FlowState(nodeInfo, globalVarNames, globalVarNamesSet, globalVariables));
        for (int i = 0, t = childGlobalVarNames.size(); i < t; i++) {
            String childVarName = childGlobalVarNames.get(i);
            //TODO:
			/**if (!outputVariables.copyValue(childVarName, inputVariables)) {
				
				if (globalVariables.getValue(childVarName) != null) {
					outputVariables.copyValue(childVarName, globalVariables);
				} else {
					// childVarName doesn't exist from invoker flow, try to initial it.
					List<ScriptParamInfo> params = childInfo.getFlow().getConf().getParams();
					for (ScriptParamInfo param : params) {
						if (param.getName().equals(childVarName)) {
							if (primitiveDefaultValues2.containsKey(param.getType())) {
								outputVariables.putValue(childVarName,
										primitiveDefaultValues2.get(param.getType()));
							} else {
								outputVariables.putValue(childVarName, null);
							}
							break;
						}
					}
				}	
			}*/
        }
        globalVarNames = childGlobalVarNames;
        globalVarNamesSet = childGlobalVarNamesSet;
        localVariables.getVariableObjects().clear();
        //TODO: change the local var context as the global var context.
        if (logger.isTraceEnabled()) {
            logger.trace("Sub flow global variables is {}", globalVariables);
        }
    }

    public NodeInfo pop() {
        if (stacks == null) {
            return null;
        } else {
            FlowState state = stacks.poll();
            if (state != null) {
                localVariables.getVariableObjects().clear();
                for (int i = 0, t = globalVarNames.size(); i < t; i++) {
                    String gVarName = globalVarNames.get(i);
                    
                    //if (!localVariables.copyValue(gVarName, outputVariables)) {
                      //  localVariables.copyValue(gVarName, globalVariables);
                    //}
                }
                globalVarNames = state.globalVarNames;
                globalVarNamesSet = state.globalVarNamesSet;
                globalVariables.getVariableObjects().clear();
                // globalVariables.copyAllValue(state.globalVariables);
                if (logger.isTraceEnabled()) {
                    logger.trace("Subflow output variables is {}", localVariables);
                }
                return state.currentNode;
            } else {
                return null;
            }
        }
    }

    public void clearSubFlowInfo() {
        if (stacks != null) {
            stacks.clear();
        }
    }

    @Override
    public Event getEvent() {
        return this.event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getEvent(Class<T> eventClass) {
        return (T) this.event;
    }

    public void setEvent(Event evt) {
        if (this.event != null) {
            if(logger.isDebugEnabled()){
                logger.debug("This event {} is under processing, it must not be interrupted by new event {}!!!",
                		this.event.getId(), evt.getId());
            }
        }
        
        this.event = evt;
        evt.setAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT, this);
        evt.setAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT, flowContextInfo);
    }

    @Override
    public Event getRequestEvent() {
        return this.requestEvent;
    }

    public FlowContextImpl getFlowContextInfo() {
        return flowContextInfo;
    }

    public void changeEvent(Event event) {
        // legacy support, return new event info from handler.
        flowContextInfo.registerOutboundEvent(event);
        setEvent(event);
    }

    public void clearEventAttributes() {
    	this.event.removeAttribute(BuiltInAttributeConstant.KEY_VARIABLECONTEXT);
    	this.event.removeAttribute(BuiltInAttributeConstant.KEY_FLOWCONTEXT);
    }

    public <T> T getInput(String name, Class<T> clazz) {
		try {
			Object v = localVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
	        v = globalVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
	        if (clazz.isPrimitive()) {
	            return (T) primitiveDefaultValues.get(clazz);
	        }
		} catch (EvaluationException e) {
		}
        if (clazz.isPrimitive()) {
            return (T) primitiveDefaultValues.get(clazz);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public void initInputVariables(FlowRuntimeContext context) {
        this.localVariables.getVariableObjects().clear();
        this.localVariables.getVariableObjects().putAll(
        		context.localVariables.getVariableObjects());
    }

    @Override
    public void setOutput(String name, Object value) {
        if (globalVarNamesSet.contains(name)) {
            try {
				globalVariables.setVariableValue(name, value);
			} catch (EvaluationException e) {
				logger.info(e.getMessage(), e);
			}
        }
    }

    public void setGlobalVarNames(List<String> globalVarNames, Set<String> globalVarNamesSet) {
        this.globalVarNames = globalVarNames;
        this.globalVarNamesSet = globalVarNamesSet;
    }

    public void mapGlobalVariables() {
        for (int i = 0, t = globalVarNames.size(); i < t; i++) {
            String globalVarName = globalVarNames.get(i);
            try {
				globalVariables.setVariableValue(globalVarName, localVariables.getVariableValue(globalVarName));
			} catch (EvaluationException e) {
				logger.info(e.getMessage(), e);
			}
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getGlobalVarible(String name, Class<T> clazz) {
    	try {
			Object v = globalVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
		} catch (EvaluationException e) {
		}
        if (clazz.isPrimitive()) {
            return (T) primitiveDefaultValues.get(clazz);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getGlobalVarible(String name) {
    	try {
			Object v = globalVariables.getVariableValue(name);
	        if (v != null) {
	            return (T) v;
	        }
		} catch (EvaluationException e) {
		}
        return null;
    }

    @SuppressWarnings("unchecked")
	public void setLocallVariables(Map<String, Object> initVariables) {
        localVariables.getVariableObjects().clear();
        if (initVariables != null) {
        	localVariables.getVariableObjects().putAll(initVariables);
        }
    }

    public void mapVariables(List<NameExpressionType> mappings) {
        if (mappings != null) {
        	localVariables.getVariableObjects().clear();
            for (int i = 0, t = mappings.size(); i < t; i++) {
            	NameExpressionType m = mappings.get(i);
                String targetName = m.getExpression().getExpressionString();
                String srcName = m.getName();
                try {
					localVariables.setVariableValue(srcName, globalVariables.getVariableValue(targetName));
				} catch (EvaluationException e) {
					logger.info(e.getMessage(), e);
				}
            }
            resetLocalVariables();
        }
    }

    public void resetLocalVariables() {
        if (logger.isTraceEnabled()) {
            logger.trace("Current local variables is {}, global variables is {}", 
            		localVariables, globalVariables);
        }
    }

    @SuppressWarnings("unchecked")
	public Map<String, Object> getSnapshot() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.putAll(globalVariables.getVariableObjects());
        m.putAll(localVariables.getVariableObjects());
        return m;
    }

    @Override
    public WorkflowSession getSession() {
        return session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSession(Class<T> sessionClass) {
        return (T) session;
    }

    public void setSession(WorkflowSession session) {
        this.session = session;
        try {
			globalVariables.setVariableValue(FlowEngine.SESSION_VAR_NAME, session);
		} catch (EvaluationException e) {
		}
    }

    public boolean isSessionDestroyed() {
        return sessionDestroyed;
    }

    public void setSessionDestroyed(boolean sessionDestroyed) {
        this.sessionDestroyed = sessionDestroyed;
    }

    public void saveState(Object transactionState) {
        this.transactionState = transactionState;
        this.recoverable = true;
    }

    public Object getTransactionState() {
        return transactionState;
    }

    public void clearState() {
        transactionState = null;
        recoverable = false;
    }

    public boolean isRecoverable() {
        return recoverable;
    }

    public TimerTask getTimeoutFuture() {
        return timeoutFuture;
    }

    public void setTimeoutFuture(TimerTask timeoutFuture) {
        this.timeoutFuture = timeoutFuture;
    }

    public NodeInfo getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(NodeInfo currentNode) {
        this.currentNode = currentNode;
    }

    public EventDestType getEventDestInfo() {
        return destInfo;
    }

    public void setEventDestInfo(EventDestType destInfo) {
        this.destInfo = destInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public NodeInfo getStartNode() {
        return startNode;
    }

    public void setStartNode(NodeInfo startNode) {
        this.startNode = startNode;
    }

    //IntermediateNodeInfo
    public NodeInfo getEventNode() {
        return eventNode;
    }

    public void setEventNode(NodeInfo eventNode) {
        this.eventNode = eventNode;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    @Override
    public String toString() {
        return "FlowRuntimeContext [Node=" + currentNode + "]" + super.toString();
    }

}
