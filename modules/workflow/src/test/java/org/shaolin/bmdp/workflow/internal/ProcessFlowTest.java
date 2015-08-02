package org.shaolin.bmdp.workflow.internal;

import org.junit.Assert;
import org.junit.Test;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.bmdp.runtime.test.TestContext;
import org.shaolin.bmdp.workflow.be.ITask;
import org.shaolin.bmdp.workflow.coordinator.IResourceManager;

public class ProcessFlowTest extends TestContext {

	private WorkflowLifecycleServiceImpl wfservice = new WorkflowLifecycleServiceImpl();
	
	private IResourceManager resourceManager = new ResourceManagerImpl();
	
	private CoordinatorServiceImpl coordinator = new CoordinatorServiceImpl();
	
	private MockEventProducer producer = new MockEventProducer();
	
	private MockSessionService sessionService = new MockSessionService();
	
	public ProcessFlowTest() {
	}
	
	@Test
	public void testExecuteFlow() throws Exception {
		AppContext.get().register(sessionService);
		AppContext.get().register(coordinator);
		AppContext.get().register(resourceManager);
		wfservice.startService();
		coordinator.markAsTestCaseFlag();
		coordinator.startService();
		producer.setEventProcessor(AppContext.get().getService(WorkFlowEventProcessor.class));
		
		normalTest("NodeTest", "producer", 1000);
		
		missionTest("mission-flow", "producer1", 1000);
		
		Thread.sleep(1000);
		wfservice.stopService();
	}
	
	private void normalTest(String nodeName, String eventConsumer, int waitSeconds) throws InterruptedException {
        FlowEvent evt;
        evt = new FlowEvent(eventConsumer);
        evt.setAttribute("Request", nodeName);
        evt.setAttribute("NodeName", nodeName);
        producer.sendEvent(evt);
        Thread.sleep(waitSeconds);
        Assert.assertEquals(evt.getAttribute("Response"), nodeName);
    }
	
	private void missionTest(String nodeName, String eventConsumer, int waitSeconds) throws InterruptedException {
        FlowEvent evt;
        evt = new FlowEvent(eventConsumer);
        evt.setAttribute("Request", nodeName);
        evt.setAttribute("NodeName", nodeName);
        producer.sendEvent(evt);//place and order
        Thread.sleep(waitSeconds);
        
        coordinator.completeTask(coordinator.getAllTasks().get(0));//approved order
        Thread.sleep(waitSeconds);
        
        coordinator.completeTask(coordinator.getAllTasks().get(0));// on production
        Thread.sleep(waitSeconds);
        
        coordinator.completeTask(coordinator.getAllTasks().get(0));// on delivery
        Thread.sleep(waitSeconds);
        
        Assert.assertEquals(0, coordinator.getAllTasks().size());
    }
	
	private class ResourceManagerImpl implements IResourceManager {

		@Override
		public Class getServiceInterface() {
			return IResourceManager.class;
		}

		@Override
		public void assignOnwer(ITask task) {
			task.setPartyId(1);
		}
		
	}

}
