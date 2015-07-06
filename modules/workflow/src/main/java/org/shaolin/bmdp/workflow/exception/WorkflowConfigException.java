package org.shaolin.bmdp.workflow.exception;

/**
 * the application configuration exception
 */
public class WorkflowConfigException extends RuntimeException {

	private String flowName;
	private String nodeName;

	public String getFlowName() {
		return flowName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public WorkflowConfigException(String flowName, String nodeName,
			String message) {
		this(flowName, nodeName, message, null);
	}

	public WorkflowConfigException(String flowName, String nodeName,
			String message, Throwable cause) {
		super(message, cause);
		this.flowName = flowName;
		this.nodeName = nodeName;
	}

}
