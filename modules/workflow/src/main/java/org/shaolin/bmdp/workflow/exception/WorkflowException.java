package org.shaolin.bmdp.workflow.exception;


public class WorkflowException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WorkflowException(String message) {
		super(message);
	}
	
	public WorkflowException(String message, Throwable t) {
		super(message, t);
	}
	
}
