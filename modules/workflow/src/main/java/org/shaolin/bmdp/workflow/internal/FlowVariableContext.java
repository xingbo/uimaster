package org.shaolin.bmdp.workflow.internal;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;

/**
 * Interface for accessing workflow global and local variables.
 */
public interface FlowVariableContext {
    
    /**
     * Get current global variable. 
     * 
     * @param <T>
     * @param name
     * @param clazz
     * @return
     */
    public <T> T getGlobalVarible(String name, Class<T> clazz);
    public <T> T getGlobalVarible(String name);
    
    /**
     * Return event object.
     * 
     * @param <T>
     * @param eventClass
     * @return
     */
    public <T> T getEvent(Class<T> eventClass);
    
    /**
     * Return session object.
     * 
     * @param <T>
     * @param sessionClass
     * @return
     */
    public <T> T getSession(Class<T> sessionClass);
    
    /**
     * Return session object.
     * 
     * @return
     */
    public WorkflowSession getSession();
    
    /**
     * Return event object.
     * 
     * @return
     */
    public Event getEvent();
    
    /**
     * Return the request event which trigger the flow.
     * 
     * @return
     */
    public Event getRequestEvent();
    
    /**
     * Set output variable.
     * 
     * @param name
     * @param value
     */
    public void setOutput(String name, Object value);
}
