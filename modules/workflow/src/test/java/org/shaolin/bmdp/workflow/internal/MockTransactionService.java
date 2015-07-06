package org.shaolin.bmdp.workflow.internal;

import org.shaolin.bmdp.workflow.spi.LogicalTransactionService;

public class MockTransactionService implements LogicalTransactionService {

    @Override
    public void begin() {
        System.out.println("begin transaction");
    }

    @Override
    public void commit() {
    	System.out.println("commit transaction");
    }

    @Override
    public Object pause() {
    	System.out.println("pause transaction");
        return null;
    }

    @Override
    public void resume(Object arg0) {
    	System.out.println("resume transaction");
    }

    @Override
    public void rollback() {
    	System.out.println("rollback transaction");
    }

}
