package org.shaolin.bmdp.workflow.internal;

import org.junit.Assert;
import org.junit.Test;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.bmdp.runtime.test.TestContext;

public class ProcessFlowTest extends TestContext {

	private WorkflowLifecycleServiceImpl service = new WorkflowLifecycleServiceImpl();
	
	private TestEventProducer producer = new TestEventProducer();
	
	private TestSessionService sessionService = new TestSessionService();
	
	public ProcessFlowTest() {
	}
	
	@Test
	public void testExecuteFlow() throws Exception {
		AppContext.get().register(sessionService);
		service.startService();
		producer.setEventProcessor(AppContext.get().getService(WorkFlowEventProcessor.class));
		
		loopBackTest("NodeTest", "producer", 1000);
		loopBackTest("mission-flow", "producer1", 1000);
		
		Thread.sleep(1000);
		service.stopService();
	}
	
	private void loopBackTest(String nodeName, String eventConsumer, int waitSeconds) throws InterruptedException {
        FlowEvent evt;
        evt = new FlowEvent(eventConsumer);
        evt.setAttribute("Request", nodeName);
        evt.setAttribute("NodeName", nodeName);
        producer.sendEvent(evt);
        Thread.sleep(waitSeconds);
        Assert.assertEquals(evt.getAttribute("Response"), nodeName);
    }
	
	

}
