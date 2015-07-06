package org.shaolin.bmdp.workflow.internal;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.EventProducer;

public class BuiltInEventProducer implements EventProducer {
	
	public static final String EXCEPTION_PRODUCER_NAME = "exceptionEventProducer";

	public static final String TIMEOUT_PRODUCER_NAME = "timeoutEventProducer";

	private EventProcessor processor;

	@Override
	public EventProcessor getEventProcessor() {
		return processor;
	}

	@Override
	public void setEventProcessor(EventProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void processErrorEvent(Event event, ErrorType type) {
		System.out.println("Error event: " + event + ",Type: " + type);
	}

	@Override
	public void handleProcessorResult(Event evt, boolean isSuccess,
			Throwable exception, NodeInfo currentNode) {
		if (!isSuccess) {
			System.out.println("Error event: " + evt + ",currentNode: " + currentNode.toString());
		}
	}
	
}
