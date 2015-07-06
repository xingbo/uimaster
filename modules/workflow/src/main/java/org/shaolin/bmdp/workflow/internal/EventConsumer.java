package org.shaolin.bmdp.workflow.internal;

import java.util.Map;

import org.shaolin.bmdp.datamodel.workflow.FlowType;
import org.shaolin.bmdp.datamodel.workflow.NodeType;
import org.shaolin.bmdp.datamodel.workflow.TimeoutNodeType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.exception.ExpirationException;
import org.shaolin.bmdp.workflow.internal.type.AppInfo;
import org.shaolin.bmdp.workflow.internal.type.FlowInfo;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.EventProducer.ErrorType;
import org.shaolin.bmdp.workflow.spi.ExceptionEvent;
import org.shaolin.bmdp.workflow.spi.TimeoutEvent;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The event consumer will try to match the event with the app and handle it if matched, otherwise
 * leave and let it handle by other apps.
 * 
 */
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private final FlowEngine engine;
    private final String eventConsumerName;
    private static final NodeInfo NODE_PLACEHOLDER = new NodeInfo(new NodeType(), 
    		new FlowInfo(new FlowType(), new AppInfo(new Workflow())));
    private final ThreadLocal<String> lockedSessionId = new ThreadLocal<String>();
    
    public EventConsumer(FlowEngine engine, String eventConsumerName) {
        this.engine = engine;
        this.eventConsumerName = eventConsumerName;
    }

    /**
     * Return true if the event handled by this app.
     * 
     * If the evt is a response event, check it with the payload from the event. Otherwise only when
     * the event node matched, it will return true. If there is an invalid start event or
     * intermediate event, it will ignore and not pass to next consumer.
     * 
     * @param evt
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean accept(Event evt, Map oldMdc) {
        try {
        	if (!evt.getEventConsumer().equals(eventConsumerName)) {
        		return false;
        	}
        	
            Event nextEvent = null;
            FlowContextImpl payload = (FlowContextImpl) evt.getFlowContext();
            if (payload != null) {
                // The pay-load is a pair of request and response events.
                // When we send a request in a flow node. Connector will 
                // put this request in a pending queue and wait response
                // coming back.
                // When the response comes back, Connector will find out
                // the correspondent request and get the flow context info
                // from this request and put into response event.
                // Or the request is timeout with an timeout event in play
                // load here that triggered by Connector.
                // Then pay-load is able to handle the response for spec
                // ific intermediate-node that defines the handling resp
                // onse logic.
                // In such case that the session lock already assigned for
                // the response in the flow context.
                FlowRuntimeContext flowContext = payload.getFlowRuntime();
                if (!flowContext.match(engine)) { // not for this flow.
                    return false;
                } else if (evt instanceof TimeoutEvent && ((TimeoutEvent) evt).fromTimerNode()) {
                    nextEvent = handleTimeoutEvent(evt);
                } else {
                    evt.setAttribute(BuiltInAttributeConstant.KEY_PRODUCER_NAME,
                            eventConsumerName);
                    // If cannot lock, it means the original flow is still being processed.
                    // Add the response event to pending list.
                    if (flowContext.lock(evt)) {
                        // the response event carries the flow info from the request event.
                        // which is why we can match it by this payload mechanism.
                        nextEvent = handleResponseEvent(evt, payload);
                    }
                }
            } else {
                // even though the incoming event might be rejected, 
                // we still use FlowRuntimeContext in matchStartNode or matchRequestNode.
                FlowRuntimeContext flowContext = new FlowRuntimeContext(evt, engine);
                String sessionId = engine.getSessionId(evt);
                if (sessionId == null) {
                    flowContext.clearEventAttributes();
                    return false;
                }
                NodeInfo startNode = engine.matchStartNode(eventConsumerName, evt);
                NodeInfo eventNode = engine.matchRequestNode(eventConsumerName, evt);
                if (startNode == null && eventNode == null) {
                    return false;
                }
                
                // normal process.
                flowContext.setEventNode(eventNode);
                flowContext.setStartNode(startNode);
                flowContext.setSessionId(sessionId);
                if (engine.lockSession(sessionId, evt, flowContext, NODE_PLACEHOLDER)) {
                    lockedSessionId.set(sessionId);
                    nextEvent = matchEvent(evt, flowContext, sessionId, startNode, eventNode);
                }
            }

            while (nextEvent != null) {
                nextEvent = handlePendingEvent(nextEvent);
            }
            lockedSessionId.remove();
            return true;
        } finally {
            // avoid lock leak.
            String sessionId = lockedSessionId.get();
            if (sessionId != null) {
                lockedSessionId.remove();
                try {
                    engine.unlockSession(sessionId, true);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage() + ". Session Id: " + sessionId, ex);
                    // ignore
                }
            }

            try {
                engine.checkSessionState();
            } catch (Throwable ex) {
                logger.warn("Fail to rollback transaction for {0}", ex, evt.getId());
            }
            
        }
    }

    private Event matchEvent(Event evt, FlowRuntimeContext flowContext, 
    		String sessionId, NodeInfo startNode, NodeInfo eventNode) {
        Event nextEvent;
        WorkflowSession session = engine.getSession(evt, sessionId);
        if (session == null && startNode != null) {
            // start node matched
            session = engine.createSession(evt, sessionId);
            if (session == null) {
                logger.warn("Error occurred: {}.{}", evt, ErrorType.CANNOT_INIT_SESSION);
                nextEvent = engine.unlockSession(sessionId, false);
            } else {
                flowContext.lock(evt);
                if(flowContext.getEvent() == null) {
                    flowContext.setEvent(evt);
                }
                nextEvent = handle(startNode, flowContext, session);
            }
        } else if (session != null && eventNode != null) {
            // intermediate node matched
            flowContext.lock(evt);
            if(flowContext.getEvent() == null) {
                // intermediate node can be pending and handled by
                // another thread, the event requires to reset again.
                flowContext.setEvent(evt);
            }
            // start event statistic
            nextEvent = handle(eventNode, flowContext, session);
        } else if (session != null && eventNode == null) {
        	logger.warn("Error occurred: {}.{}", evt, ErrorType.NO_MATCHED_NODE);
            nextEvent = engine.unlockSession(sessionId, false);
        } else {
        	logger.warn("Error occurred: {}.{}", evt, ErrorType.SESSION_NOT_EXIST);
            nextEvent = engine.unlockSession(sessionId, false);
        }
        return nextEvent;
    }

    @Override
    public String toString() {
        return "EventConsumer [engine=" + engine.getEngineName() + ", eventProducerName="
                + eventConsumerName + "]";
    }

    private NodeInfo checkTimeoutExceptionNode(FlowRuntimeContext flowRuntime, Event evt) {
    	NodeInfo matchResponseNode;
        ExceptionEvent newEvent = new ExceptionEvent(new ExpirationException(),
                flowRuntime.getCurrentNode(), flowRuntime);
        flowRuntime.setEvent(newEvent);
        newEvent.setId(BuiltInEventProducer.EXCEPTION_PRODUCER_NAME
                + flowRuntime.getEvent().getId());
        matchResponseNode = engine.matchExceptionNode(newEvent);
        return matchResponseNode;
    }

    private Event handle(NodeInfo eventNode, FlowRuntimeContext flowContext, WorkflowSession session) {
        Event event = flowContext.getEvent();
        String sessionId = session.getID();

        // trigger the event listener configured in the corresponding workflow app
        engine.triggerEventListener(event, eventNode, sessionId);

        if (event instanceof TimeoutEvent) {
            TimeoutEvent tEvent = (TimeoutEvent) event;
            if (tEvent.fromTimerNode()) {
                // timer event
                boolean removed = engine.removeTimer(sessionId,
                        (FlowRuntimeContext) tEvent.getContext());
                if (!removed) {
                    // The timer was not active.
                    flowContext.unlock(true, tEvent);
                    return engine.unlockSession(sessionId, false);
                }
                flowContext.initInputVariables((FlowRuntimeContext) tEvent.getContext());
            }
        }
        if (flowContext.isRecoverable()) {
            engine.recoverSession(session, flowContext);
        }
        try {
            flowContext.setSession(session);
            engine.signal(eventNode, flowContext);
        } catch (Throwable ex) {
            printException(flowContext, ex);
            // Exception was not handled by application.
            // Log it and Rollback session and unlock session.
            Event nextEvent = releaseSession(flowContext, session);
            NodeInfo currentNode = flowContext.getCurrentNode();
            processEventResult(event, false, ex, currentNode);
            if (event != flowContext.getRequestEvent()) {
                Event requestEvent = flowContext.getRequestEvent();
                processEventResult(requestEvent, false, ex, currentNode);
            }
            return nextEvent;
        }

        if (flowContext.getEventDestInfo() != null) {
            // Should wait response and keep the lock
            // Check Pending event on flowContext
            // If nextEvent is not null, it means the response event has arrived
        	flowContext.recordState();
            Event nextEvent = flowContext.unlock(false);
            if (event != flowContext.getRequestEvent()) {
                processEventResult(event, true, null, null);
            }
            return nextEvent;
        } else {
        	flowContext.recordState();
            flowContext.unlock(true);
            if (event != flowContext.getRequestEvent()) {
                processEventResult(event, true, null, null);
            }
            Event requestEvent = flowContext.getRequestEvent();
            processEventResult(requestEvent, true, null, null);
            return engine.unlockSession(sessionId, false);
        }
    }

    private void processEventResult(Event event, boolean isSuccess, Throwable ex,
            NodeInfo errorNode) {
        //producer.handleProcessorResult(event, isSuccess, ex, errorNode);
    	if (logger.isTraceEnabled()) {
        	logger.trace("Processed event: {}, isSuccess: {}", event.getId(), isSuccess);
        }
    }

    private Event handlePendingEvent(Event nextEvent) {
        if (nextEvent.getAttribute(BuiltInAttributeConstant.KEY_NODE) != null) {
            NodeInfo eventNode = (NodeInfo) nextEvent
                    .removeAttribute(BuiltInAttributeConstant.KEY_NODE);
            FlowRuntimeContext flowContext = (FlowRuntimeContext) nextEvent
                    .removeAttribute(BuiltInAttributeConstant.KEY_RUNTIME);
            if (NODE_PLACEHOLDER == eventNode) {
                return matchEvent(nextEvent, flowContext, flowContext.getSessionId(),
                        flowContext.getStartNode(), flowContext.getEventNode());
            } else {
                return processTimeoutEvent((TimeoutEvent) nextEvent, eventNode, flowContext);
            }
        } else if (nextEvent.getFlowContext() != null) {
            return handleResponseEvent(nextEvent, (FlowContextImpl) nextEvent.getFlowContext());
        }
        return null;
    }

    private Event handleResponseEvent(Event evt, FlowContextImpl flowContext) {
        FlowRuntimeContext flowRuntime = flowContext.getFlowRuntime();
        evt.setFlowContext(null); // Clear flow context.

        if (logger.isTraceEnabled()) {
            logger.trace("The event {} is a response event, sesson id is {}", evt.getId(),
                    flowRuntime.getSession().getID());
        }

        if (!flowRuntime.flowContextMatched(flowContext)) {
            engine.discardResponse(evt, true);
            return flowRuntime.unlock(false, evt);
        }
        WorkflowSession session = flowRuntime.getSession();
        flowRuntime.setEvent(evt);
        NodeInfo matchResponseNode;
        if (evt instanceof TimeoutEvent) {
            matchResponseNode = engine.matchTimeoutNode(flowRuntime.getEventDestInfo(), evt);
            if (matchResponseNode == null) {
                matchResponseNode = checkTimeoutExceptionNode(flowRuntime, evt);

                if (matchResponseNode == null) {
                    engine.discardResponse(evt, false);
                    if (flowRuntime.isRecoverable()) {
                        engine.recoverSession(session, flowRuntime);
                    }
                    // We must release session here if timeout was not handled, otherwise session will leak.
                    return releaseSession(flowRuntime, session);
                }
            }
            flowRuntime.startNewFlow(true);
        } else {
            String eventProducerName = (String) evt
                    .removeAttribute(BuiltInAttributeConstant.KEY_PRODUCER_NAME);
            if (eventProducerName == null) {
                eventProducerName = eventProducerName;
            }
            // response node are all intermediate nodes.
            matchResponseNode = engine.matchResponseNode(eventProducerName,
                    flowRuntime.getEventDestInfo(), evt);
            if (matchResponseNode == null) {
                engine.discardResponse(evt, false);
                return flowRuntime.unlock(false, evt);
            }
            flowRuntime.startNewFlow(false);
        }

        return handle(matchResponseNode, flowRuntime, session);
    }

    private Event handleTimeoutEvent(Event evt) {
        TimeoutEvent tEvent = (TimeoutEvent) evt;

        FlowRuntimeContext oldContext = (FlowRuntimeContext) tEvent.getContext();
        NodeInfo timeoutNode = oldContext.getCurrentNode();
        NodeInfo destNode = (NodeInfo)((TimeoutNodeType)timeoutNode.getNode()).getTimeoutDest().getNode();

        FlowRuntimeContext newFlowContext = new FlowRuntimeContext(evt, engine);
        newFlowContext.setSessionId(oldContext.getSessionId());
        
        if (engine.lockSession(tEvent.getSessionId(), evt, newFlowContext, destNode)) {
            lockedSessionId.set(tEvent.getSessionId());
            return processTimeoutEvent(tEvent, destNode, newFlowContext);
        }
        return null;
    }

    private Event processTimeoutEvent(TimeoutEvent tEvent, NodeInfo destNode,
            FlowRuntimeContext newFlowContext) {
        WorkflowSession session = engine.getSession(tEvent, tEvent.getSessionId());

        if (session == null) {
            logger.error("Invalid timer event {0}, the session does not exist.", tEvent);
            return engine.unlockSession(tEvent.getSessionId(), false);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("The event {} is a timer request event for app {}:{}", new Object[] {
                    tEvent.getId(), engine.getEngineName(), destNode.getName() });
        }

        newFlowContext.lock(tEvent);
        return handle(destNode, newFlowContext, session);
    }

    private void printException(FlowRuntimeContext flowContext, Throwable ex) {
        engine.analyzeException(flowContext, ex);
        logger.warn(flowContext.toString(), ex);
    }

    private Event releaseSession(FlowRuntimeContext flowContext, WorkflowSession session) {
        flowContext.unlock(true);
        engine.rollbackSession(flowContext, session);
        return engine.unlockSession(session.getID(), false);
    }
}
