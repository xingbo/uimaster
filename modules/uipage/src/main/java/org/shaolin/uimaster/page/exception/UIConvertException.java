package org.shaolin.uimaster.page.exception;

import org.shaolin.bmdp.i18n.Localizer;

public class UIConvertException extends ODException {
	private static final long serialVersionUID = 7439664688371859036L;

	public UIConvertException(String aReason) {
		super(aReason);
	}

	public UIConvertException(String aReason, Object[] args) {
		super(aReason, args);
	}

	public UIConvertException(String aReason, Throwable aThrowable) {
		super(aReason, aThrowable);
	}

	public UIConvertException(String aReason, Throwable aThrowable,
			Object[] args) {
		super(aReason, aThrowable, args);
	}

	public UIConvertException(String aReason, Throwable aThrowable,
			Object[] args, Localizer aLocalizer) {
		super(aReason, aThrowable, args, aLocalizer);
	}
}