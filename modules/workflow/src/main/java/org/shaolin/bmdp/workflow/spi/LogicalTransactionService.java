package org.shaolin.bmdp.workflow.spi;

/**
 * Provide a abstract logical transaction layer.
 * 
 * The logical transaction service support save state and recovery.
 */
public interface LogicalTransactionService {
    /**
     * Begin a transaction.
     */
    void begin();
    
    /**
     * Commit the transaction.
     */
    void commit();
    
    /**
     * Rollback the transaction.
     */
    void rollback();
    
    /**
     * Pause the transaction and return the state.
     * 
     * @return
     */
    Object pause();
    
    /**
     * Resume the transaction and recover from the state.
     * 
     * @param state
     */
    void resume(Object state);
}
