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
 * Shaolin Wu
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
