package org.shaolin.bmdp.coordinator.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.shaolin.bmdp.coordinator.be.INotification;
import org.shaolin.bmdp.coordinator.be.ITask;
import org.shaolin.bmdp.coordinator.be.NotificationImpl;
import org.shaolin.bmdp.coordinator.be.TaskImpl;
import org.shaolin.bmdp.coordinator.ce.TaskStatusType;
import org.shaolin.bmdp.coordinator.dao.CoordinatorModel;
import org.shaolin.bmdp.coordinator.spi.ICoordinatorService;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.ILifeCycleProvider;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;

public class CoordinatorServiceImpl implements ILifeCycleProvider, ICoordinatorService, IServiceProvider {
	
	private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	
	private final Map<ITask, ScheduledFuture<?>> futures = new HashMap<ITask, ScheduledFuture<?>>();
	
	private final ConcurrentHashMap<Long, ITask> allPendingTasks = new ConcurrentHashMap<Long, ITask>();
	
	private final ConcurrentHashMap<Long, List<INotification>> allNotifications 
		= new ConcurrentHashMap<Long, List<INotification>>();
	
	private boolean testCaseFlag = false;
	
	private IAppServiceManager appService;
	
	public void setAppService(IAppServiceManager appService) {
		this.appService = appService;
	}

	void markAsTestCaseFlag() {
		testCaseFlag = true;
	}
	
	@Override
	public void reload() {
		
	}
	
	@Override
	public List<ITask> getAllTasks() {
		List<ITask> allTasks = new ArrayList<ITask>();
		Collection<ITask> tasks= allPendingTasks.values();
		for (ITask t : tasks) {
			allTasks.add(t);
		}
		return allTasks;
	}
	
	@Override
	public int getTaskSize() {
		return allPendingTasks.size();
	}
	
	@Override
	public List<ITask> getAllTasks(TaskStatusType status) {
		List<ITask> partyTasks = new ArrayList<ITask>();
		Collection<ITask> tasks= allPendingTasks.values();
		for (ITask t : tasks) {
			if (t.getStatus() == status) {
				partyTasks.add(t);
			}
		}
		return partyTasks;
	}
	
	@Override
	public List<ITask> getPartyTasks(long partyId) {
		List<ITask> partyTasks = new ArrayList<ITask>();
		Collection<ITask> tasks= allPendingTasks.values();
		for (ITask t : tasks) {
			if (t.getPartyId() == partyId) {
				partyTasks.add(t);
			}
		}
		return partyTasks;
	}
	
	@Override
	public ITask getTask(long taskId) {
		Collection<ITask> tasks= allPendingTasks.values();
		for (ITask t : tasks) {
			if (t.getId() == taskId) {
				return t;
			}
		}
		return null;
	}

	@Override
	public void addTask(final ITask task) {
		if (task.getId() > 0) {
			throw new IllegalArgumentException("A existing task can't be created again!");
		}
		if (task.getStatus() == TaskStatusType.COMPLETED
				|| task.getStatus() == TaskStatusType.CANCELLED) {
			throw new IllegalArgumentException("Task is finished: " + task.getStatus());
		}
		
		task.setStatus(TaskStatusType.NOTSTARTED);
		
		if (!testCaseFlag) {
			CoordinatorModel.INSTANCE.create(task);
		}
		
		if (allPendingTasks.putIfAbsent(task.getId(), task) == null) {
			schedule(task);
		}
	}

	private void schedule(final ITask task) {
		if (task.getStatus() == TaskStatusType.INPROGRESS
				|| task.getStatus() == TaskStatusType.COMPLETED
				|| task.getStatus() == TaskStatusType.CANCELLED) {
			allPendingTasks.remove(task.getId());
			throw new IllegalArgumentException("Task must be not started state: " + task.getStatus());
		}
		
		long delay = task.getTriggerTime().getTime() - System.currentTimeMillis();
		if (delay <= 0) {
			completeTask(task);
			return;
		}
		
		ScheduledFuture<?> future = pool.schedule(new Runnable() {
			@Override
			public void run() {
				completeTask(task);
			}
		}, delay, TimeUnit.MILLISECONDS);
		
		task.setStatus(TaskStatusType.INPROGRESS);
		futures.put(task, future);
		
		taskToNotification(task);
	}
	
	@Override
	public void updateTask(ITask task) {
		if (task.getId() <= 0) {
			throw new IllegalArgumentException("A created task can't be updated!");
		}
		
		if (!testCaseFlag) {
			CoordinatorModel.INSTANCE.update(task);
		}
		
		ScheduledFuture<?> future = futures.remove(task);
		if (!future.isDone()) {
			future.cancel(true);
		}
		
		if (allPendingTasks.containsKey(task.getId())) {
			allPendingTasks.put(task.getId(), task);
		}
		
		if (task.getStatus() == TaskStatusType.NOTSTARTED) {
			schedule(task);
		}
	}
	
	@Override
	public void completeTask(ITask task) {
		AppContext.register(appService);
		
		ScheduledFuture<?> future = futures.remove(task); 
		if (!future.isDone()) {
			future.cancel(true);
		}
		//TODO: trigger the action.
		allPendingTasks.remove(task.getId());
		
		task.setStatus(TaskStatusType.COMPLETED);
		task.setCompleteRate(100);
		
		if (!testCaseFlag) {
			CoordinatorModel.INSTANCE.update(task);
		}
		
		taskToNotification(task);
	}
	
	@Override
	public void cancelTask(ITask task) {
		AppContext.register(appService);
		
		ScheduledFuture<?> future = futures.remove(task);
		if (!future.isDone()) {
			future.cancel(true);
			
			allPendingTasks.remove(task.getId());
			
			task.setStatus(TaskStatusType.CANCELLED);
			
			if (!testCaseFlag) {
				CoordinatorModel.INSTANCE.update(task);
			}
			
			taskToNotification(task);
		}
	}
	
	@Override
	public void startService() {
		this.setAppService(AppContext.get());
		allPendingTasks.clear();
		
		// load all pending tasks when system up.
		TaskImpl condition = new TaskImpl();
		List<TaskImpl> tasks = CoordinatorModel.INSTANCE.searchPendingTasks(condition, null, 0, -1);
		for (TaskImpl t : tasks) {
			allPendingTasks.put(t.getId(), t);
			
			if (t.getStatus() == TaskStatusType.INPROGRESS) {
				if (System.currentTimeMillis() < t.getTriggerTime().getTime()) {
					schedule(t);
				} else {
					//trigger it directly.
					completeTask(t);
				}
			}
		}
	}

	@Override
	public boolean readyToStop() {
		return false;
	}

	@Override
	public void stopService() {
		pool.shutdown();
	}

	@Override
	public int getRunLevel() {
		return 1;
	}
	
	private void taskToNotification(ITask t) {
		NotificationImpl notifier = new NotificationImpl();
		notifier.setSubject("[" + t.getTriggerTime().toString() 
				+ "/" + t.getStatus().getDisplayName() + "] " + t.getSubject());
		notifier.setDescription(t.getDescription());
		notifier.setPartyId(t.getPartyId());
		addNotification(notifier);
	}
	
	@Override
	public void addNotification(INotification message) {
		long partyId = message.getPartyId();
		if (partyId <= 0) {
			throw new IllegalArgumentException("Please specify the party id.");
		}
		
		if (!allNotifications.containsKey(partyId)) {
			allNotifications.put(partyId, new ArrayList<INotification>());
		}
		allNotifications.get(partyId).add(message);
	}
	
	@Override
	public List<INotification> pullNotification(long partyId) {
		return allNotifications.remove(partyId);
	}

	@Override
	public Class getServiceInterface() {
		return ICoordinatorService.class;
	}
	
}
