package org.shaolin.bmdp.persistence;

import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.Localizer;

/**
 * Base class for persistence exceptions
 */
public class PersistenceException extends BaseException {
	
	/**
	 * Constructs a PersistenceException with a given exception reason
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 */
	public PersistenceException(String aReason) {
		super(aReason);
	}

	/**
	 * Constructs a PersistenceException with a given exception reason, an
	 * argument array.
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 * @param args
	 *            the argument list
	 */
	public PersistenceException(String aReason, Object[] args) {
		super(aReason, args);
	}

	/**
	 * Constructs a PersistenceException given a exception reason, a nested
	 * Throwable object.
	 * 
	 * @param aReason
	 *            the key to the localized reason stringdd
	 * @param aThrowable
	 *            nested throwable
	 */
	public PersistenceException(String aReason, Throwable aThrowable) {
		super(aReason, aThrowable);
	}

	/**
	 * Constructs a PersistenceException with the given reason, a nested
	 * Throwable object, and an argument array.
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 * @param aThrowable
	 *            nested throwable
	 * @param args
	 *            the argument list
	 */
	public PersistenceException(String aReason, Throwable aThrowable,
			Object[] args) {
		super(aReason, aThrowable, args);
	}

	/**
	 * Constructs a PersistenceException with the given reason, a nested
	 * Throwable object, an argument array, and a localizer.
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 * @param aThrowable
	 *            nested throwable
	 * @param args
	 *            the argument list
	 * @param aLocalizer
	 *            the localizer used for the error messages
	 */
	public PersistenceException(String aReason, Throwable aThrowable,
			Object[] args, Localizer aLocalizer) {
		super(aReason, aThrowable, args, aLocalizer);
	}

}
