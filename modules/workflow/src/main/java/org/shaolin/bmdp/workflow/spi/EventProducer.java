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
package org.shaolin.bmdp.workflow.spi;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;

/**
 * assemble a event after it is created, then fire it to the event processor
 * 
 */
public interface EventProducer {
    /**
     * Type to identify the why the event can not be handled.
     */
    public enum ErrorType {
        NO_MATCHED_PROCESSOR, SESSION_CLOSED, CANNOT_INIT_SESSION, NO_MATCHED_NODE, SESSION_NOT_EXIST, SEM_TRANSAC_TIMEOUT
    }

    /**
     * Register event processor. After invoked, the producer should 
     * use the registered event to process event.
     * 
     * The event will be processed in the event producer thread.
     * 
     * @param processor
     */
    public void setEventProcessor(EventProcessor processor);

    /**
     * Return registered event processor.
     * 
     * @return
     */
    public EventProcessor getEventProcessor();
    
    /**
     * Process error event.
     * 
     * @param evt
     * @param type
     */
    public void processErrorEvent(Event evt, ErrorType type);
    
    /**
     * Notify the producer after event processed.
     * 
     * Exception info only available when the event process failed.
     * 
     * @param evt
     * @param isSuccess
     * @param exception
     * @param currentNode
     */
    public void handleProcessorResult(Event evt, boolean isSuccess, Throwable exception, NodeInfo currentNode);
}
