/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.bmdp.workflow.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.shaolin.bmdp.persistence.HibernateUtil;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.ILifeCycleProvider;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.be.INotification;
import org.shaolin.bmdp.workflow.be.ITask;
import org.shaolin.bmdp.workflow.be.NotificationImpl;
import org.shaolin.bmdp.workflow.be.TaskHistoryImpl;
import org.shaolin.bmdp.workflow.be.TaskImpl;
import org.shaolin.bmdp.workflow.ce.TaskStatusType;
import org.shaolin.bmdp.workflow.coordinator.ICoordinatorService;
import org.shaolin.bmdp.workflow.coordinator.IResourceManager;
import org.shaolin.bmdp.workflow.coordinator.ITaskListener;
import org.shaolin.bmdp.workflow.dao.CoordinatorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordinatorServiceImpl implements ILifeCycleProvider, ICoordinatorService, IServiceProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(CoordinatorServiceImpl.class);
	
	// make this for whole system, not only for one application instance.
	private ScheduledExecutorService pool;
	
	private final Map<ITask, ScheduledFuture<?>> futures = new HashMap<ITask, ScheduledFuture<?>>();
	
	private final ConcurrentHashMap<Long, ITask> workingTasks = new ConcurrentHashMap<Long, ITask>();
	
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
		Collection<ITask> tasks= workingTasks.values();
		for (ITask t : tasks) {
			allTasks.add(t);
		}
		return allTasks;
	}
	
	@Override
	public int getTaskSize() {
		return workingTasks.size();
	}
	
	public Set<Long> getAllTaskOnwers() {
		Map<Long, Long> onwers = new HashMap<Long, Long>();
		Collection<ITask> tasks= workingTasks.values();
		for (ITask t : tasks) {
			onwers.put(t.getPartyId(), t.getPartyId());
		}
		return onwers.keySet();
	}
	
	@Override
	public List<ITask> getAllTasks(TaskStatusType status) {
		List<ITask> partyTasks = new ArrayList<ITask>();
		Collection<ITask> tasks= workingTasks.values();
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
		Collection<ITask> tasks= workingTasks.values();
		for (ITask t : tasks) {
			if (t.getPartyId() == partyId) {
				partyTasks.add(t);
			}
		}
		return partyTasks;
	}
	
	@Override
	public ITask getTask(long taskId) {
		Collection<ITask> tasks= workingTasks.values();
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
			throw new IllegalArgumentException("The existing task can't be created again!");
		}
		if (task.getStatus() == TaskStatusType.COMPLETED
				|| task.getStatus() == TaskStatusType.CANCELLED) {
			throw new IllegalArgumentException("Task is finished: " + task.getStatus());
		}
		
		task.setStatus(TaskStatusType.NOTSTARTED);
		
		if (!testCaseFlag) {
			CoordinatorModel.INSTANCE.create(task);
		}
		
		if (workingTasks.putIfAbsent(task.getId(), task) == null) {
			schedule(task);
		}
	}

	private void schedule(final ITask task) {
		if (task.getStatus() == TaskStatusType.INPROGRESS
				|| task.getStatus() == TaskStatusType.COMPLETED
				|| task.getStatus() == TaskStatusType.CANCELLED) {
			workingTasks.remove(task.getId());
			throw new IllegalArgumentException("Task must be not in started state: " + task.getStatus());
		}
		AppContext.get().getService(IResourceManager.class).assignOnwer(task);
		
		long delay = task.getExpiredTime().getTime() - System.currentTimeMillis();
		if (delay <= 0) {
			expireTask(task);
			return;
		}
		ScheduledFuture<?> future = pool.schedule(new Runnable() {
			@Override
			public void run() {
				expireTask(task);
			}
		}, delay, TimeUnit.MILLISECONDS);
		
		task.setStatus(TaskStatusType.INPROGRESS);
		futures.put(task, future);
		
		taskToNotification(task);
	}
	
	public void postponeTask(ITask task, Date date) {
		if (task.getExpiredTime() != null) {
			if (task.getExpiredTime().getTime() >= date.getTime()) {
				throw new IllegalArgumentException("Current task expired time is greater than the given date.");
			}
		}
		task.setExpiredTime(date);
		task.setStatus(TaskStatusType.NOTSTARTED);
				
		updateTask(task);
	}
	
	@Override
	public void updateTask(ITask task) {
		if (task.getId() <= 0) {
			throw new IllegalArgumentException("The created task can't be updated!");
		}
		
		if (!testCaseFlag) {
			CoordinatorModel.INSTANCE.update(task);
		}
		
		ScheduledFuture<?> future = futures.remove(task);
		if (future != null && !future.isDone()) {
			future.cancel(true);
		}
		if (!workingTasks.containsKey(task.getId())) {
			workingTasks.put(task.getId(), task);
		}
		
		if (task.getStatus() == TaskStatusType.NOTSTARTED) {
			schedule(task);
		}
	}
	
	private void expireTask(ITask task) {
		AppContext.register(appService);
		if (logger.isDebugEnabled()) {
			logger.debug("Task is expired!  {}", task.toString());
		}
		
		ScheduledFuture<?> future = futures.remove(task); 
		if (!future.isDone()) {
			future.cancel(true);
		}
		workingTasks.remove(task.getId());
		
		task.setStatus(TaskStatusType.EXPIRED);
		task.setCompleteRate(100);
		
		if (!testCaseFlag) {
			CoordinatorModel.INSTANCE.update(task);
		}
		
		if (task.getListener() != null) {
			ITaskListener listener = (ITaskListener)task.getListener();
			listener.notifyExpired();
		}
		
		taskToNotification(task);
	}
	
	@Override
	public void completeTask(ITask task) {
		AppContext.register(appService);
		if (logger.isTraceEnabled()) {
			logger.trace("Task is completed.  {}", task.toString());
		}
		
		ScheduledFuture<?> future = futures.remove(task); 
		if (!future.isDone()) {
			future.cancel(true);
		}
		workingTasks.remove(task.getId());
		
		task.setStatus(TaskStatusType.COMPLETED);
		task.setCompleteRate(100);
		
		if (!testCaseFlag) {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			moveToHistory(task, session);
			
			session.getTransaction().commit();
		}
		
		if (task.getListener() != null) {
			ITaskListener listener = (ITaskListener)task.getListener();
			listener.notifyCompleted();
		}
		taskToNotification(task);
	}
	
	@Override
	public void cancelTask(ITask task) {
		AppContext.register(appService);
		if (logger.isTraceEnabled()) {
			logger.trace("Task is cancelled.  {}", task.toString());
		}
		
		ScheduledFuture<?> future = futures.remove(task);
		if (!future.isDone()) {
			future.cancel(true);
			
			workingTasks.remove(task.getId());
			
			task.setStatus(TaskStatusType.CANCELLED);
			
			if (!testCaseFlag) {
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				
				moveToHistory(task, session);
				
				session.getTransaction().commit();
			}
			
			if (task.getListener() != null) {
				ITaskListener listener = (ITaskListener)task.getListener();
				listener.notifyCancelled();
			}
			
			taskToNotification(task);
		}
	}
	
	private void moveToHistory(ITask task, Session session) {
		TaskHistoryImpl history = new TaskHistoryImpl();
		history.setCompleteRate(task.getCompleteRate());
		history.setDescription(task.getDescription());
		history.setEnabled(task.isEnabled());
		history.setExpiredTime(task.getExpiredTime());
		history.setPartyId(task.getPartyId());
		history.setPartyType(task.getPartyType());
		history.setPriority(task.getPriority());
		history.setSendEmail(task.getSendEmail());
		history.setSendSMS(task.getSendSMS());
		history.setStatus(task.getStatus());
		history.setSubject(task.getSubject());
		
		session.save(history);
		session.delete(task);
	}
	
	@Override
	public void startService() {
		this.workingTasks.clear();
		this.futures.clear();
		this.setAppService(AppContext.get());
		// make this shared
		this.pool = IServerServiceManager.INSTANCE.getSchedulerService()
				.createScheduler("system", "wf-coordinator", Runtime.getRuntime().availableProcessors() * 2);
		
		if (testCaseFlag) {
			return;
		}
		// load all pending tasks when system up.
		TaskImpl condition = new TaskImpl();
		List<TaskImpl> tasks = CoordinatorModel.INSTANCE.searchPendingTasks(condition, null, 0, -1);
		for (TaskImpl t : tasks) {
			workingTasks.put(t.getId(), t);
			
			if (t.getStatus() == TaskStatusType.INPROGRESS) {
				if (System.currentTimeMillis() < t.getExpiredTime().getTime()) {
					schedule(t);
				} else {
					//trigger it directly.
					expireTask(t);
				}
			}
		}
	}

	@Override
	public boolean readyToStop() {
		return true;
	}

	@Override
	public void stopService() {
		pool.shutdown();
		workingTasks.clear();
		futures.clear();
	}

	@Override
	public int getRunLevel() {
		return 1;
	}
	
	private void taskToNotification(ITask t) {
		NotificationImpl notifier = new NotificationImpl();
		notifier.setSubject("[" + t.getExpiredTime().toString() 
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
