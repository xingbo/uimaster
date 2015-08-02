package org.shaolin.bmdp.workflow.coordinator;

import java.util.List;
import java.util.Set;

import org.shaolin.bmdp.workflow.be.INotification;
import org.shaolin.bmdp.workflow.be.ITask;
import org.shaolin.bmdp.workflow.ce.TaskStatusType;

public interface ICoordinatorService {

	/**
	 * monitor the task queue size.
	 * 
	 * @return
	 */
	int getTaskSize();
	
	/**
	 * monitor all onwers of current tasks.
	 * 
	 * @return
	 */
	Set<Long> getAllTaskOnwers();
	
	/**
	 * 
	 * @return
	 */
	List<ITask> getAllTasks();
	
	/**
	 * How many tasks that the organization/company is running.
	 * 
	 * @param status
	 * @return
	 */
	List<ITask> getAllTasks(TaskStatusType status);
	
	/**
	 * How many tasks that the employee is working.
	 * 
	 * @param partyId
	 * @return
	 */
	List<ITask> getPartyTasks(long partyId);
	
	ITask getTask(long taskId);
	
	void addTask(ITask task);
	
	void updateTask(ITask task);
	
	void completeTask(ITask task);
	
	void cancelTask(ITask task);
	
	/**
	 * The party id is required!
	 * 
	 * @param message
	 */
	void addNotification(INotification message);
	
	/**
	 * Once invoke this method, the user notifications will be cleared from the cache.
	 * 
	 * @param partyId
	 * @return
	 */
	List<INotification> pullNotification(long partyId);
	
}
