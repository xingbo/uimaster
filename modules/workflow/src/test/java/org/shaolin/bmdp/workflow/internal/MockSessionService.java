package org.shaolin.bmdp.workflow.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.spi.SessionService;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;

public class MockSessionService implements SessionService, IServiceProvider {
	
    public static final String SESSION_ID = "SessionId";
    private final AtomicLong seq = new AtomicLong(0);
    private final ConcurrentMap<String, MockSession> sessionMap =
        new ConcurrentHashMap<String, MockSession>();
    
    @Override
    public WorkflowSession createSession(Event evt, String id) {
        MockSession s = new MockSession(id);
        evt.setAttribute(SESSION_ID, s.getID());
        return s;
    }

    @Override
    public WorkflowSession getSession(Event evt, String id) {
        String sId = (String) evt.getAttribute(SESSION_ID);
        if (sId != null) {
            return sessionMap.get(sId);
        }
        return null;
    }

    @Override
    public void commitSession(WorkflowSession session) {
        sessionMap.put(session.getID(), (MockSession) session);
    }

    @Override
    public void rollbackSession(WorkflowSession session) {
        System.out.println("Rollback " + session.getID());
    }

    @Override
    public void destroySession(WorkflowSession session) {
        sessionMap.remove(session.getID());
    }

    @Override
    public String getSessionId(Event evt) {
        String sId = (String) evt.getAttribute(SESSION_ID);
        if (sId != null) {
            return sId;
        }
        return "" + seq.incrementAndGet();
    }

    @Override
    public void pasueSession(WorkflowSession session) {
    }

    @Override
    public void resumeSession(WorkflowSession session) {
    }

	@Override
	public Class getServiceInterface() {
		return MockSessionService.class;
	}
}
