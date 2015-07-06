package org.shaolin.bmdp.coordinator.internal;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.shaolin.bmdp.coordinator.be.INotification;
import org.shaolin.bmdp.coordinator.be.TaskImpl;
import org.shaolin.bmdp.coordinator.ce.TaskStatusType;
import org.shaolin.bmdp.runtime.test.TestContext;

public class CoordinatorServiceImplTest extends TestContext {

	@Test
	public void testProcessTask() throws InterruptedException {

		CoordinatorServiceImpl service = new CoordinatorServiceImpl();
		service.setAppService(appContext);
		service.markAsTestCaseFlag();

		TaskImpl task = new TaskImpl();
		task.setPartyId(1);
		task.setSubject("task 1");
		task.setDescription("let's do it.");
		task.setTriggerTime(new Date());

		// System.out.println(task.getTriggerTime());

		task.getTriggerTime().setTime(task.getTriggerTime().getTime() + 2000);// 2 seconds

		// System.out.println(task.getTriggerTime());

		service.addTask(task);
		Assert.assertEquals(service.getTaskSize(), 1);
		Assert.assertEquals(task.getStatus(), TaskStatusType.INPROGRESS);
		Thread.sleep(3000);
		Assert.assertEquals(task.getStatus(), TaskStatusType.COMPLETED);
		
		try {
			service.addTask(task);
			Assert.fail();
		} catch (Exception e) {
		}

		try {
			service.updateTask(task);
			Assert.fail();
		} catch (Exception e) {
		}
		
		task.setStatus(TaskStatusType.INPROGRESS);
		task.setTriggerTime(new Date());
		task.getTriggerTime().setTime(task.getTriggerTime().getTime() + 2000);// 2 seconds
		
		service.addTask(task);
		Assert.assertEquals(service.getTaskSize(), 1);
		Assert.assertEquals(task.getStatus(), TaskStatusType.INPROGRESS);
		Thread.sleep(3000);
		Assert.assertEquals(task.getStatus(), TaskStatusType.COMPLETED);
		
		Assert.assertEquals(service.getTaskSize(), 0);
		
		List<INotification> notifications = service.pullNotification(task.getPartyId());
		Assert.assertEquals(notifications.size(), 4);
		
		for (INotification n : notifications) {
			System.out.println(n.getSubject() + " " + n.getDescription());
		}
		
		Assert.assertEquals(service.pullNotification(task.getPartyId()), null);
	}

}
