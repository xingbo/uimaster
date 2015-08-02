package org.shaolin.bmdp.workflow.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.workflow.internal.type.NodeInfo;
import org.shaolin.bmdp.workflow.spi.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockEventProducer implements EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(MockEventProducer.class);
    
    private AtomicLong counter = new AtomicLong(0);
    
    private EventProcessor processor;
    
    private List<Event> events = Collections.synchronizedList(new ArrayList<Event>());
    
    private List<Event> requests = Collections.synchronizedList(new ArrayList<Event>());
    
    @Override
    public EventProcessor getEventProcessor() {
        return processor;
    }

    @Override
    public void setEventProcessor(EventProcessor processor) {
        this.processor = processor;
    }
    
    public void sendSyncEvent(final Event evt) {
        counter.incrementAndGet();
        processor.process(evt);
    }
    
    public void sendRequest(final Event evt) {
        requests.add(evt);
        sendEvent(evt, 0);
    }
    
    public void sendResponse(final Event evt, final int delay) {
        sendEvent(evt, delay);
    }
    
    public void sendEvent(final Event evt) {
        sendEvent(evt, 0);
    }
    
    public void sendEvent(final Event evt, final int delay) {
        events.add(evt);
        counter.incrementAndGet();
                    
        processor.process(evt);
    }

    @Override
    public void processErrorEvent(Event event, ErrorType type) {
        events.remove(event);
        counter.decrementAndGet();
        logger.warn("Invalid event {}, error is {}", event, type);
    }

    @Override
    public void handleProcessorResult(Event evt, boolean isSuccess, Throwable exception, NodeInfo currentNode) {
        events.remove(evt);
        counter.decrementAndGet();
    }
    
    public long getPendingSize() {
        return counter.get();
    }
    
    public void assertResult() {
        for (Event event : events) {
            logger.error("Lose event {} -> {}", event.getId(), event);
        }
        Assert.assertEquals(counter.get(), 0);
    }

}
