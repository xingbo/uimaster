package org.shaolin.bmdp.workflow.spi;

import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.workflow.internal.cache.FlowObject;

public interface IWorkflowService {

	/**
	 * Get a workflow entity.
	 * 
	 * @param flowName
	 * @return
	 */
	Workflow getWorkflowEntity(String flowName);
	
	/**
	 * Get a runtime workflow object.
	 * 
	 * @param flowName
	 * @return
	 */
	FlowObject getFlowObject(String flowName);
}
