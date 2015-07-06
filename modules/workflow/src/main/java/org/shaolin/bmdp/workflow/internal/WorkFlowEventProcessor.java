package org.shaolin.bmdp.workflow.internal;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unified event processor which registered to one event producer.
 * 
 * THe event processor will pass event to real app, an event producer 
 * can be handled by multiple APPs. Each app will be one event consumer
 * instance.
 */
public final class WorkFlowEventProcessor implements EventProcessor, IServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(WorkFlowEventProcessor.class);
    private final Map<String, EventConsumer> allConsumers;
    private static final ThreadLocal<Event> currentEvent = new ThreadLocal<Event>();
    private static final ThreadLocal<Queue<ProcessEventTask>> pendingTasks = new ThreadLocal<Queue<ProcessEventTask>>();
    private static final ThreadLocal<StringBuilder> idBuilder = new ThreadLocal<StringBuilder>() {
        @Override
        public StringBuilder initialValue() {
            return new StringBuilder();
        }
    };
    
    private final AtomicLong seq = new AtomicLong(0);
    public WorkFlowEventProcessor(Map<String, EventConsumer> allConsumers) {
        this.allConsumers = allConsumers;
    }
    
    @Override
    public void process(Event event) {
        if (currentEvent.get() != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("The event {} will be queued in current thread", event.getId());
            }
            Queue<ProcessEventTask> queue = pendingTasks.get();
            if (queue == null) {
                queue = new LinkedList<ProcessEventTask>();
                pendingTasks.set(queue);
            }
            queue.offer(new ProcessEventTask(this, event));
        } else {
            safeHandleEvent(event);
            
            Queue<ProcessEventTask> queue = pendingTasks.get();
            if (queue != null) {
                ProcessEventTask task = queue.poll();
                while (task != null) {
                    task.getProcessor().safeHandleEvent(task.getEvent());
                    task = queue.poll();
                }
            }
            pendingTasks.set(null);
        }
    }

    private void safeHandleEvent(Event event) {
        currentEvent.set(event);
        try {
            handleEvent(event);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            currentEvent.set(null);
        }
    }
    
    private void handleEvent(Event event) {
        //Generate a unique id for the event.
        if (event.getId() == null) {
            StringBuilder stringBuilder = idBuilder.get();
            stringBuilder.setLength(0);
            event.setId(stringBuilder.append(event.getEventConsumer()).append('[').append(
                    seq.getAndIncrement()).append(']').toString());
        }
        event.setAttribute(BuiltInAttributeConstant.KEY_ORIGINAL_EVENT, event);

        if (logger.isTraceEnabled()) {
            logger.trace("Assign Id {} to event {}", event.getId(), event);
            logger.trace("Receive a event {}", event);
        }
        
        EventConsumer consumer = allConsumers.get(event.getEventConsumer());
        if (logger.isTraceEnabled()) {
        	logger.trace("Trigger event {} on {}", event.getId(), consumer);
        }
        if (!consumer.accept(event, null)) {
        	if (logger.isTraceEnabled()) {
        		logger.trace("No matched node for event {} from {}", event.getId(), event.getEventConsumer());
        	}
        	//producer.processErrorEvent(event, ErrorType.NO_MATCHED_PROCESSOR);
        }
        
    }
    
    public class ProcessEventTask {
    	private final WorkFlowEventProcessor processor;
    	private final Event event;

    	public ProcessEventTask(WorkFlowEventProcessor processor, Event event) {
    		this.processor = processor;
    		this.event = event;
    	}

    	public WorkFlowEventProcessor getProcessor() {
    		return processor;
    	}

    	public Event getEvent() {
    		return event;
    	}

    }

	@Override
	public Class getServiceInterface() {
		return EventProcessor.class;
	}

}
