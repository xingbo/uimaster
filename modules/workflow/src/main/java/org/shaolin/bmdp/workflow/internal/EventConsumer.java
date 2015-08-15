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
     * mission event, it will ignore and not pass to next consumer.
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
        	
            FlowContextImpl payload = (FlowContextImpl) evt.getFlowContext();
            if (payload != null) {
                // The pay-load is a pair of request and response events.
                FlowRuntimeContext flowContext = payload.getFlowRuntime();
                if (!flowContext.match(engine)) { // not for this flow.
                    return false;
                } else if (evt instanceof TimeoutEvent && ((TimeoutEvent) evt).fromTimerNode()) {
                    handleTimeoutEvent(evt);
                } else {
                    evt.setAttribute(BuiltInAttributeConstant.KEY_PRODUCER_NAME,
                            eventConsumerName);
                    // the response event carries the flow info from the request event.
                    // which is why we can match it by this payload mechanism.
                    handleResponseEvent(evt, payload);
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
                    matchEvent(evt, flowContext, sessionId, startNode, eventNode);
                }
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
                if(flowContext.getEvent() == null) {
                    flowContext.setEvent(evt);
                }
                nextEvent = handle(startNode, flowContext, session);
            }
        } else if (session != null && eventNode != null) {
            // mission node matched
            if(flowContext.getEvent() == null) {
                // mission node can be pending and handled by
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

        if (event instanceof TimeoutEvent) {
            TimeoutEvent tEvent = (TimeoutEvent) event;
            if (tEvent.fromTimerNode()) {
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

        if (flowContext.isWaitResponse()) {
            // Should wait response and keep the lock
            // Check Pending event on flowContext
            // If nextEvent is not null, it means the response event has arrived
        	flowContext.recordState();
            return null;
        } else {
        	flowContext.recordState();
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

    private Event handleResponseEvent(Event evt, FlowContextImpl flowContext) {
        FlowRuntimeContext flowRuntime = flowContext.getFlowRuntime();
        evt.setFlowContext(null); // Clear flow context.
        if (logger.isTraceEnabled()) {
            logger.trace("The event {} is a response event, sesson id is {}", evt.getId(),
                    flowRuntime.getSession().getID());
        }

        if (!flowRuntime.flowContextMatched(flowContext)) {
            engine.discardResponse(evt, true);
            return null;
        }
        WorkflowSession session = flowRuntime.getSession();
        flowRuntime.setEvent(evt);
        if (evt instanceof TimeoutEvent) {
            if (checkTimeoutExceptionNode(flowRuntime, evt) == null) {
                engine.discardResponse(evt, false);
                if (flowRuntime.isRecoverable()) {
                    engine.recoverSession(session, flowRuntime);
                }
                // We must release session here if timeout was not handled, otherwise session will leak.
                return releaseSession(flowRuntime, session);
            }
            flowRuntime.startNewFlow(true);
        } else {
        	// response comes back.
            flowRuntime.startNewFlow(false);
        }
        return handle(flowRuntime.getCurrentNode(), flowRuntime, session);
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

        return handle(destNode, newFlowContext, session);
    }

    private void printException(FlowRuntimeContext flowContext, Throwable ex) {
        engine.analyzeException(flowContext, ex);
        logger.warn(flowContext.toString(), ex);
    }

    private Event releaseSession(FlowRuntimeContext flowContext, WorkflowSession session) {
        engine.rollbackSession(flowContext, session);
        return engine.unlockSession(session.getID(), false);
    }
}
