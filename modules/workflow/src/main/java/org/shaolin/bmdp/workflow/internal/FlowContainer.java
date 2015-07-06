package org.shaolin.bmdp.workflow.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;
import org.shaolin.bmdp.workflow.exception.ConfigException;
import org.shaolin.bmdp.workflow.internal.cache.FlowObject;
import org.shaolin.bmdp.workflow.internal.type.AppInfo;
import org.shaolin.bmdp.workflow.spi.LogicalTransactionService;
import org.shaolin.bmdp.workflow.spi.TimeoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlowContainer is the core object that maintains all the flow engines and the event consumers.
 * 
 */
public class FlowContainer {
	
    private static final class TimeoutEventTask implements Runnable {
        private final WorkFlowEventProcessor timeoutEventProcessor;
        private final TimeoutEvent event;

        private TimeoutEventTask(WorkFlowEventProcessor timeoutEventProcessor, TimeoutEvent event) {
            this.timeoutEventProcessor = timeoutEventProcessor;
            this.event = event;
        }

        @Override
        public void run() {
            timeoutEventProcessor.process(event);
        }
    }

    public static final class TimerTask implements Runnable {
        private final FlowEngine engine;
        private FlowContextImpl flowContext;
        private Future<?> future;

        private TimerTask(FlowEngine engine, FlowContextImpl flowContext) {
            this.engine = engine;
            this.flowContext = flowContext;
        }

        @Override
        public void run() {
            FlowContextImpl _flowContext = flowContext;
            if (_flowContext != null) {
                engine.timeout(flowContext);
            }
        }

        public void cancel() {
            future.cancel(false);
            flowContext = null;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(FlowContainer.class);

    private ExecutorService executorService;
    private LogicalTransactionService transactionService;
    
    private long defaultWorkflowTimeout = 5000;

    // local variable
    private final ConcurrentMap<String, FlowEngine> allEngines = new ConcurrentHashMap<String, FlowEngine>();

    private final FlowScheduler scheduler = new FlowScheduler();
    
	private ICache<String, FlowObject> appflowCache;

	public FlowContainer(String appName) {
		appflowCache = CacheManager.getInstance().getCache(appName + "_workflow_cache", String.class,
				FlowObject.class);
	}
	
	public FlowObject getFlowObject(String flowName) {
		return appflowCache.get(flowName);
	}
	
    public List<String> getActiveEngines() {
        return new ArrayList<String>(allEngines.keySet());
    }

    void startService(List<Workflow> appInfos) {
        scheduler.start();
        
        Map<String, FlowEngine> engineMap = new HashMap<String, FlowEngine>();
        List<FlowObject> activeFlows = new ArrayList<FlowObject>();
        for (Workflow flow : appInfos) {
        	FlowObject flowInfo = new FlowObject(new AppInfo(flow));
        	// add to cache, but not initialized.
        	appflowCache.putIfAbsent(flow.getEntityName(), flowInfo);
            
            if (flow.getConf().isBootable()) {
            	activeFlows.add(flowInfo);
            }
        }
        
        for (FlowObject flowInfo: activeFlows) {
        	logger.info("Create workflow engine: {}",  flowInfo.getAppInfo().getName());
        	FlowEngine engine = initFlowEngine(flowInfo, false);
        	engineMap.put(flowInfo.getAppInfo().getName(), engine);
        }

        startFlowEngines(engineMap);
    }
    
    void stopService() {
        scheduler.stop();
    }

    /**
     * All flow engines read for work. This is the most important step for FlowContainer.
     * After this, all flow engines are able to handle the incoming event.
     * 
     * @param engineMap
     */
    private void startFlowEngines(Map<String, FlowEngine> engineMap) {
        Map<String, EventConsumer> tempProcessors = new HashMap<String, EventConsumer>();

        // init all EventConsumers.
        allEngines.clear();
        for (FlowEngine engine : engineMap.values()) {
            logger.info("Start workflow engine: {}", engine.getEngineName());
            engine.start(tempProcessors);
            allEngines.put(engine.getEngineName(), engine);
        }

        WorkFlowEventProcessor eventProcessor = new WorkFlowEventProcessor(tempProcessors);
        AppContext.get().register(eventProcessor);
    }

    public void runTask(final WorkFlowEventProcessor timeoutEventProcessor, final TimeoutEvent event) {
        executorService.submit(new TimeoutEventTask(timeoutEventProcessor, event));
    }

    public void startTransaction() {
    	if (transactionService == null) {
    		return;
    	}
        transactionService.begin();
    }

    public void rollbackTransaction() {
    	if (transactionService == null) {
    		return;
    	}
        transactionService.rollback();
    }

    public void commitTransaction() {
    	if (transactionService == null) {
    		return;
    	}
        transactionService.commit();
    }

    public Object pauseTransaction() {
    	if (transactionService == null) {
    		return null;
    	}
        return transactionService.pause();
    }

    public void resumeTransaction(Object obj) {
    	if (transactionService == null) {
    		return;
    	}
        transactionService.resume(obj);
    }

    public void scheduleTask(long timeout, final FlowContextImpl flowContext,
            FlowRuntimeContext runtimeContext, final FlowEngine engine) {
        if (timeout <= 0) {
            timeout = defaultWorkflowTimeout;
        }

        TimerTask task = new TimerTask(engine, flowContext);
        Future<?> future = scheduler.schedule(task, timeout);
        task.setFuture(future);
        runtimeContext.setTimeoutFuture(task);
    }

    /**
     * Create FlowEngines by all defined templates. Each flow template will have
     * a flow engine created if success.
     * 
     * @param entityFlows
     * @return
     */
    public FlowEngine initFlowEngine(FlowObject flowInfo, boolean isManagedTransaction) throws ConfigException {
        FlowEngine engine = new FlowEngine(flowInfo.getAppInfo().getName(), this, isManagedTransaction);
        if (isManagedTransaction && transactionService == null) {
            throw new IllegalStateException("Transaction service does not exist.");
        }
        engine.init(flowInfo);
        return engine;
    }

}
