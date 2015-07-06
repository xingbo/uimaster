package org.shaolin.javacc.exception;

import java.lang.reflect.InvocationTargetException;

import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.ExceptionConstants;

/**
 * The class for exception during parsing time
 */

public class ParsingException extends BaseException {
	public ParsingException(String description, Throwable e) {
		super(description, getNestedException(e));
	}

	public ParsingException(Throwable e) {
		super(ExceptionConstants.EBOS_000, getNestedException(e));
	}

	public ParsingException(String description) {
		super(description);
	}

	public ParsingException(String msg, Object[] args) {
		super(msg, args);
	}

	public ParsingException(String msg, Throwable t, Object[] args) {
		super(msg, getNestedException(t), args);
	}

	private static Throwable getNestedException(Throwable t) {
		if (t instanceof EvaluationException || t instanceof ParsingException
				|| t instanceof InvocationTargetException) {
			Throwable cause = t.getCause();
			if (cause != null) {
				return getNestedException(cause);
			}
		}
		return t;
	}

	static final long serialVersionUID = 0x91B0129CE83B50FFL;

}
