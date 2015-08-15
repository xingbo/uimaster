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
