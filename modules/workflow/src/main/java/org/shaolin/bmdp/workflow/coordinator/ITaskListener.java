package org.shaolin.bmdp.workflow.coordinator;

public interface ITaskListener {

	public void notifyCompleted();
	
	public void notifyExpired();
	
	public void notifyCancelled();
	
}
