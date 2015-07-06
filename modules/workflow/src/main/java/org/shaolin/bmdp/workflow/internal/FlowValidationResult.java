package org.shaolin.bmdp.workflow.internal;

public class FlowValidationResult {
	
	private final String appName;
	private final String flowName;
	private final String nodeName;
	private final String message;

	public String getAppName() {
		return appName;
	}

	public String getFlowName() {
		return flowName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getMessage() {
		return message;
	}

	public FlowValidationResult(String appName, String flowName,
			String nodeName, String message) {
		this.appName = appName;
		this.flowName = flowName;
		this.nodeName = nodeName;
		this.message = message;
	}

	@Override
	public String toString() {
		return "FlowValidationResult [appName=" + appName + ", flowName="
				+ flowName + ", nodeName=" + nodeName + ", message=" + message
				+ "]";
	}
}
