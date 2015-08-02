package org.shaolin.bmdp.workflow.coordinator;

import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.bmdp.workflow.be.ITask;

public interface IResourceManager extends IServiceProvider {

	public void assignOnwer(ITask task);
	
}
