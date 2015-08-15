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

import java.io.Serializable;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;

/**
 * Payload for matching request/response.
 * 
 * The connector will be responsible for setting the payload.
 * 
 */
public class FlowContextImpl implements FlowContext, Serializable {

	private static final long serialVersionUID = -132145046230765346L;

	private final FlowRuntimeContext runtime;
	private volatile NodeInfo waitingNode;

	public FlowContextImpl(FlowRuntimeContext runtime) {
		this.runtime = runtime;
	}

	@Override
	public void registerOutboundEvent(Event event) {
		event.setFlowContext(this);
	}

	public FlowRuntimeContext getFlowRuntime() {
		return runtime;
	}

	public void setWaitingNode(NodeInfo waitingNode) {
		if (waitingNode == null) {
			throw new IllegalArgumentException("waitingNode can not be null!");
		}
		this.waitingNode = waitingNode;
	}

	public NodeInfo getWaitingNode() {
		return this.waitingNode;
	}
}
