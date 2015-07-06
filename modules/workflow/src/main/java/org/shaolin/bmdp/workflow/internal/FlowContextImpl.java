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
