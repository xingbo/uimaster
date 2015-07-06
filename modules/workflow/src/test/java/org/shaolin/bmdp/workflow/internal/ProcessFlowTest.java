package org.shaolin.bmdp.workflow.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.bmdp.runtime.test.TestContext;
import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.bmdp.workflow.spi.EventProducerService;

public class ProcessFlowTest extends TestContext {

	private TestEventProducer producer = new TestEventProducer();
	
	private TestSessionService sessionService = new TestSessionService();
	
	@Test
	public void testExecuteFlow() throws Exception {
		EventProducerService service = new EventProducerService();
		service.addProducer("producer", producer);
		AppContext.get().register(service);
		AppContext.get().register(sessionService);
		
		List<Workflow> appInfos = new ArrayList<Workflow>();
		URL url = ProcessFlowTest.class.getClassLoader().getResource(
				"general-flow.workflow");
		InputStream in = null;
		try {
			in = new FileInputStream(new File(url.getFile()));
			appInfos.add(EntityUtil.unmarshaller(Workflow.class, in));
		} catch (JAXBException | FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			CloseUtil.close(in);
		}
		
		FlowContainer container = new FlowContainer("test");
		container.startService(appInfos);
		
		loopBackTest("NodeTest", 1000);
		
		Thread.sleep(1000);
	}
	
	private void loopBackTest(String nodeName, int waitSeconds) throws InterruptedException {
        FlowEvent evt;
        evt = new FlowEvent();
        evt.setAttribute("Request", nodeName);
        evt.setAttribute("NodeName", nodeName);
        producer.sendEvent(evt);
        Thread.sleep(waitSeconds);
        Assert.assertEquals(evt.getAttribute("Response"), nodeName);
    }
	
	

}
