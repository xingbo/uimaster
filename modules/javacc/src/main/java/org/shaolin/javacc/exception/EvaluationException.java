package org.shaolin.javacc.exception;

import java.lang.reflect.InvocationTargetException;

import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.ExceptionConstants;

/**
 * The class for exception during evaluation time
 */
public class EvaluationException extends BaseException {
	public EvaluationException(String description, Throwable t) {
		super(description, getNestedException(t));
	}

	public EvaluationException(Throwable t) {
		super(ExceptionConstants.EBOS_000, getNestedException(t));
	}

	public EvaluationException(String msg, Object[] args) {
		super(msg, args);
	}

	public EvaluationException(String msg, Throwable t, Object[] args) {
		super(msg, getNestedException(t), args);
	}

	public EvaluationException(String description) {
		super(description);
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
