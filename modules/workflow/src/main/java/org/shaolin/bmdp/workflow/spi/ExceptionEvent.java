package org.shaolin.bmdp.workflow.spi;

import java.util.Map;

import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.internal.FlowRuntimeContext;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;

public class ExceptionEvent extends FlowEvent {
	private final Throwable exception;
	private final String nodeName;
	private final String flowName;
	private final String appName;
	private final String sessionId;
	private final Event event;
	private final Map<String, Object> variables;
	private final Event requestedEvent;

	public ExceptionEvent(Throwable exception, NodeInfo currentNode,
			FlowRuntimeContext flowContext) {
		super("");//TODO:
		this.exception = exception;
		this.nodeName = currentNode.getName();
		this.flowName = currentNode.getFlowName();
		this.appName = currentNode.getAppName();
		this.event = flowContext.getEvent();
		this.sessionId = flowContext.getSessionId();
		this.requestedEvent = flowContext.getRequestEvent();
		this.variables = flowContext.getSnapshot();
	}

	public Throwable getException() {
		return exception;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getFlowName() {
		return flowName;
	}

	public Event getEvent() {
		return event;
	}

	public String getSessionId() {
		return sessionId;
	}

	public boolean match(String name) {
		Throwable t = exception;
		while (t != null) {
			if (t.getClass().getName().equals(name)) {
				return true;
			}
			t = t.getCause();
		}
		return false;
	}

	public boolean match(Class<?> clazz) {
		return match(clazz.getName());
	}

	public String getAppName() {
		return appName;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public Event getRequestedEvent() {
		return requestedEvent;
	}

}
