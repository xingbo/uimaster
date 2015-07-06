package org.shaolin.bmdp.workflow.exception;

public class EventException extends WorkflowException {
	private static final long serialVersionUID = 1L;

	public EventException(String message) {
		super(message);
	}

	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

}
