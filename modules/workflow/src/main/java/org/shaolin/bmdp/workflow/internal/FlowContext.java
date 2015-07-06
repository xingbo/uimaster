package org.shaolin.bmdp.workflow.internal;

import org.shaolin.bmdp.runtime.spi.Event;

public interface FlowContext {
	void registerOutboundEvent(Event event);
}
