package org.shaolin.bmdp.runtime.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustThreadFactory implements ThreadFactory {

	private final AtomicInteger threadNumber_ = new AtomicInteger(1);
	private final String namePrefix_;

	public CustThreadFactory(String name) {
		namePrefix_ = name + "-thread-";
	}

	public Thread newThread(Runnable r) {
		return new DownAwareThread(r, namePrefix_
				+ threadNumber_.getAndIncrement());
	}

	public class DownAwareThread extends Thread {
		private final List<Runnable> callbacks = new ArrayList<Runnable>();

		public DownAwareThread(Runnable target) {
			super(target);
		}

		public DownAwareThread(Runnable target, String name) {
			super(target, name);
		}

		public DownAwareThread(ThreadGroup group, Runnable target) {
			super(group, target);
		}

		public DownAwareThread(ThreadGroup group, Runnable target, String name) {
			super(group, target, name);
		}

		public DownAwareThread(ThreadGroup group, Runnable target, String name,
				long stackSize) {
			super(group, target, name, stackSize);
		}

		/**
		 * register call back to the thread object<BR>
		 * The call back will be invoked with this Thread Object, when the
		 * Thread is going to down
		 * 
		 * @param runnable
		 */
		public void addThreadDownCallBack(Runnable runnable) {
			callbacks.add(runnable);
		}

		/**
		 * remove the call back of this thread
		 * 
		 * @param runnable
		 */
		public void removeThreadDownCallBack(Runnable runnable) {
			callbacks.remove(runnable);
		}

		@Override
		public void run() {
			try {
				super.run();
			} finally {
				for (Runnable runnable : callbacks) {
					if (runnable != null) {
						runnable.run();
					}
				}
			}
		}
	}
}