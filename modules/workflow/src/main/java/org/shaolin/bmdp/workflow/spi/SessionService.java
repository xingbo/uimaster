package org.shaolin.bmdp.workflow.spi;

import org.shaolin.bmdp.runtime.spi.Event;


/**
 * Workflow session service.
 * 
 * Typical use case:
 * 1. Define a session manager
 * 2. Return session from event.
 * 3. Use session manager to get Session data
 * 4. When commit session, use session manager insert/update method
 * 5. When rollback session, use session manager clearCachedSession method
 *    to clean cache.
 * 6. Delete session in destroy method.
 * 
 */
public interface SessionService {
    /**
     * Return related sessionid of the event.
     * 
     * @param evt
     * @return
     */
    public String getSessionId(Event evt);
    
    /**
     * When flow goes to start-node, it will create a WorkflowSession.
     * 
     * @param evt
     * @param id
     * @return WorkflowSession
     */
    public WorkflowSession createSession(Event evt, String id);

    /**
     * When flow goes to intermediate-node, it will get WorkflowSession which was created in start-node.
     * 
     * @param evt
     * @param id
     * @return WorkflowSession
     */
    public WorkflowSession getSession(Event evt, String id);

    /**
     * When flow goes to node without destinations and the transaction flag is bigger than 0,
     * it will commit.
     * 
     * @param session
     */
    public void commitSession(WorkflowSession session);

    /**
     * When flow goes to node without destinations and the transaction flag is 0,
     * it will rollback.
     * 
     * @param session
     */
    public void rollbackSession(WorkflowSession session);

    /**
     * When flow goes to end-node, it will destroy session.
     * 
     * @param session
     */
    public void destroySession(WorkflowSession session);
    
    /**
     * Pause session.
     * 
     * @param session
     */
    public void pasueSession(WorkflowSession session);

    /**
     * Resume session.
     * 
     * @param session
     */
    public void resumeSession(WorkflowSession session);
}
