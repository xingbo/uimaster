package org.shaolin.bmdp.workflow.internal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FlowScheduler {
    private static final int SLOT_COUNT = 64;
    @SuppressWarnings("unchecked")
    private final Queue<MyFuture>[] queues = new ConcurrentLinkedQueue[SLOT_COUNT];
    private ScheduledExecutorService scheduler;

    public FlowScheduler() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            queues[i] = new ConcurrentLinkedQueue<MyFuture>();
        }
    }

    public void start() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new BatchTask(queues), 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private static final class BatchTask implements Runnable {
        private final Queue<MyFuture>[] queues;

        public BatchTask(Queue<MyFuture>[] queues) {
            this.queues = queues;
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            for (int i = 0, t = queues.length; i < t; i++) {
                MyFuture o = queues[i].peek();
                while (o != null && o.expiredTime <= currentTime) {
                    queues[i].poll(); //Just remove item from the queue.
                    o.trigger();
                    o = queues[i].peek();
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static class MyFuture implements Future {
        private final long expiredTime;
        private Runnable task;
        private volatile boolean isCancelled = false;

        public MyFuture(long timeout, Runnable task) {
            this.expiredTime = System.currentTimeMillis() + timeout;
            this.task = task;
        }

        @Override
        public boolean cancel(boolean arg0) {
            task = null;
            isCancelled = true;
            return true;
        }

        public void trigger() {
            if (!isCancelled) {
                Runnable t = task;
                if (t != null) {
                    t.run();
                }
            }
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(long arg0, TimeUnit arg1) throws InterruptedException,
                ExecutionException, TimeoutException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException();
        }

    }

    public Future<?> schedule(Runnable task, long timeout) {
        if (timeout < SLOT_COUNT * 1000) {
            int slot = (int) (timeout / 1000);
            MyFuture future = new MyFuture(timeout, task);
            queues[slot].offer(future);
            return future;
        }
        return scheduler.schedule(task, timeout, TimeUnit.MILLISECONDS);
    }
}
