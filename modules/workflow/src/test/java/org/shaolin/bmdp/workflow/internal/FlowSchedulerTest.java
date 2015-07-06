package org.shaolin.bmdp.workflow.internal;

import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

public class FlowSchedulerTest {
	private final static class Task implements Runnable {
		private volatile boolean isTriggered = false;

		@Override
		public void run() {
			isTriggered = true;
		}

		public boolean isTriggered() {
			return isTriggered;
		}
	}

	@Test
	public void test() {
		FlowScheduler scheduler = new FlowScheduler();
		scheduler.start();
		{
			Task t = new Task();
			scheduler.schedule(t, 1000);

			sleep(2000);

			Assert.assertTrue(t.isTriggered());
		}

		{
			Task t = new Task();
			Future<?> future = scheduler.schedule(t, 3000);

			sleep(2000);

			future.cancel(true);

			Assert.assertFalse(t.isTriggered());

			sleep(3000);

			Assert.assertFalse(t.isTriggered());
		}

		{
			Task t = new Task();
			Future<?> future = scheduler.schedule(t, 3000);

			sleep(4000);

			future.cancel(true);

			Assert.assertTrue(t.isTriggered());

		}

		scheduler.stop();
	}

	private void sleep(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
