package org.shaolin.bmdp.workflow.exception;

public class ExpirationException extends WorkflowException {
	private static final long serialVersionUID = 1L;

	public ExpirationException() {
		super("Node time out.");
	}

	public ExpirationException(String message) {
		super(message);
	}

}
