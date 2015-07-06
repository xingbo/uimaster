package org.shaolin.bmdp.runtime.spi;


/**
 * The interface to control service life cycle.
 * 
 * The run level control the start order and stop order.
 * 
 * Any implementation should support force stop. For gracefully shutdown,
 * the container will first use REQUEST_LEVEL_GRACEFUL or SESSION_LEVEL_GRACEFUL to
 * call the stop service on all services. Then it will call readyToStop to determine
 * the service is ready to stop or not, it will wait all services return true to call
 * the stopService with FORCE option. If an service return false in readyToStop method,
 * the container will retry after an interval.
 * 
 * In the graceful shutdown or force shutdown, the container will use the runlevel to 
 * control the order. 
 * 
 */
public interface ILifeCycleProvider {

    /**
     * Start service.
     * 
     * The order controlled by the startOrder.
     */
    void startService();
    
    /**
     * Return whether the service stopped gracefully.
     * 
     * @return true if the service ready to stop.
     */
    boolean readyToStop();
    
    /**
     * Stop service.
     * 
     * The stopService should stop the service immediately if the option is STOP.
     * 
     * @param option stop option
     */
    void stopService();
    
    /**
     * Notify the service to reload or refresh the data it cached.
     * 
     */
    void reload();
    
    /**
     * Indicate the service will be run on which level. 
     * By default is 0, which means the highest priority.
     */
    int getRunLevel();
}
