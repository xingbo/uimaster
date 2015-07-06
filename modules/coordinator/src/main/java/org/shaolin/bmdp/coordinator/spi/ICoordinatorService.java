package org.shaolin.bmdp.coordinator.spi;

import java.util.List;

import org.shaolin.bmdp.coordinator.be.INotification;
import org.shaolin.bmdp.coordinator.be.ITask;
import org.shaolin.bmdp.coordinator.ce.TaskStatusType;

public interface ICoordinatorService {

	/**
	 * monitor the task queue size.
	 * 
	 * @return
	 */
	int getTaskSize();
	
	List<ITask> getAllTasks();
	
	List<ITask> getAllTasks(TaskStatusType status);
	
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
