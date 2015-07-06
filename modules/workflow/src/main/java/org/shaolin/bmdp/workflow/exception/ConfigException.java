package org.shaolin.bmdp.workflow.exception;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;

public class ConfigException extends I18NRuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(String message, Throwable t) {
		super(message, t);
	}
	
}
