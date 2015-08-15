/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
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
