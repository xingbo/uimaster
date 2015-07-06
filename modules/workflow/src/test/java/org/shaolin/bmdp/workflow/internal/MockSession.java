package org.shaolin.bmdp.workflow.internal;

import org.shaolin.bmdp.workflow.spi.WorkflowSession;

public final class MockSession implements WorkflowSession {
    private String id;
    
    public MockSession(String idl) {
        this.id = idl;
    }
    @Override
    public String getID() {
        return id;
    }

    @Override
    public int getTXFlag() {
        return WorkflowSession.COMMIT;
    }
}