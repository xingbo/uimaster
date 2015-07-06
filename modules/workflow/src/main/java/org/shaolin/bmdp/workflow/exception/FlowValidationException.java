package org.shaolin.bmdp.workflow.exception;

import java.util.List;

import org.shaolin.bmdp.workflow.internal.FlowValidationResult;

public class FlowValidationException extends WorkflowException {
	private static final long serialVersionUID = 1L;

	private final List<FlowValidationResult> validationResult;

	public FlowValidationException(String appName,
			List<FlowValidationResult> validationResult) {
		super("FLOW_VALIDATION_EXCEPTION" + appName
				+ validationResult.toString());
		this.validationResult = validationResult;
	}

	public List<FlowValidationResult> getValidationResult() {
		return validationResult;
	}

}
