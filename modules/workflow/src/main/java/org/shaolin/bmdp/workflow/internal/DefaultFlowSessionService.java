package org.shaolin.bmdp.workflow.internal;

import java.util.concurrent.atomic.AtomicLong;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.spi.SessionService;
import org.shaolin.bmdp.workflow.spi.TimeoutEvent;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;

public class DefaultFlowSessionService implements SessionService {
	
	private final AtomicLong sessionIdSeq = new AtomicLong(0);
	
	private static final ThreadLocal<StringBuilder> idBuilder = new ThreadLocal<StringBuilder>() {
		@Override
		public StringBuilder initialValue() {
			return new StringBuilder();
		}
	};

	private static final class DefaultWorkflowSession implements WorkflowSession {
		private final String sessionId;

		public DefaultWorkflowSession(String id) {
			this.sessionId = id;
		}

		@Override
		public String getID() {
			return sessionId;
		}

		@Override
		public int getTXFlag() {
			return WorkflowSession.COMMIT;
		}
	}

	@Override
	public WorkflowSession createSession(Event evt, String id) {
		return new DefaultWorkflowSession(id);
	}

	@Override
	public WorkflowSession getSession(Event evt, String id) {
		if (evt instanceof TimeoutEvent && ((TimeoutEvent) evt).fromTimerNode()) {
			return ((TimeoutEvent) evt).getContext().getSession();
		}
		return null;
	}

	@Override
	public void commitSession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public void rollbackSession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public void destroySession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public String getSessionId(Event evt) {
		StringBuilder stringBuilder = idBuilder.get();
		stringBuilder.setLength(0);
		stringBuilder.append(sessionIdSeq.getAndIncrement());
		return stringBuilder.toString();
	}

	@Override
	public void pasueSession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public void resumeSession(WorkflowSession session) {
		// do nothing
	}

}
