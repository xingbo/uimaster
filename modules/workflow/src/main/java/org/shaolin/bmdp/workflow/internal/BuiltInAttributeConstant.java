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

import org.shaolin.bmdp.datamodel.workflow.NodeType;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.spi.EventProducer;

public final class BuiltInAttributeConstant {
    public static final String KEY_PRODUCER_NAME = EventProducer.class.getName() + "_NAME";
    public static final String KEY_ORIGINAL_EVENT = Event.class.getName() + "_original";
    public static final String KEY_NODE = NodeType.class.getName();
    public static final String KEY_RUNTIME = FlowRuntimeContext.class.getName();
    public static final String KEY_FLOWCONTEXT = FlowContext.class.getName();
    public static final String KEY_VARIABLECONTEXT = FlowVariableContext.class.getName();

    private BuiltInAttributeConstant() {
    }
}
