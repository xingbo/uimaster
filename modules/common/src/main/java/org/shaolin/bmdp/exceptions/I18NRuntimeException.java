package org.shaolin.bmdp.exceptions;

import org.shaolin.bmdp.i18n.Localizer;

public class I18NRuntimeException extends BaseRuntimeException {
	private static final long serialVersionUID = 1L;

	public I18NRuntimeException(String message) {
		super(message);
	}
	
	public I18NRuntimeException(String msg, Localizer aLocalizer) {
		super(msg, null, null, aLocalizer);
	}

	public I18NRuntimeException(String message, Object... args) {
		super(message, args);
	}

	public I18NRuntimeException(String msg, Object[] args, Localizer aLocalizer) {
		super(msg, null, args, aLocalizer);
	}
	
	public I18NRuntimeException(String message, Throwable t) {
		super(message, t);
	}

	public I18NRuntimeException(String msg, Throwable t, Object... args) {
		super(msg, t, args);
	}
	
	public I18NRuntimeException(String msg, Throwable t, Object[] args, Localizer aLocalizer) {
		super(msg, t, args, aLocalizer);
	}
}
