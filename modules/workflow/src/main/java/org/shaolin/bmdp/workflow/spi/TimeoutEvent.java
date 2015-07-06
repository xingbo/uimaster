package org.shaolin.bmdp.workflow.spi;

import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.bmdp.workflow.internal.FlowVariableContext;

public class TimeoutEvent extends FlowEvent {

	private final FlowVariableContext context;
	private final String sessionId;

	public TimeoutEvent(FlowVariableContext context, String sessionId) {
		super("");//TODO:
		this.context = context;
		this.sessionId = sessionId;
	}

	public TimeoutEvent(String sessionId) {
		super("");//TODO:
		this.context = null;
		this.sessionId = sessionId;
	}

	public boolean fromTimerNode() {
		return context != null;
	}

	public FlowVariableContext getContext() {
		return context;
	}

	public String getSessionId() {
		return sessionId;
	}

	public WorkflowSession getPreviousSession() {
		if (context != null) {
			return context.getSession();
		}
		return null;
	}
}
