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
