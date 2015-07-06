package org.shaolin.bmdp.persistence;

import org.shaolin.bmdp.exceptions.BaseRuntimeException;
import org.shaolin.bmdp.i18n.Localizer;

/**
 * Base class for persistence runtime exceptions
 */
public class PersistenceRuntimeException extends BaseRuntimeException {

	public PersistenceRuntimeException(PersistenceException persistenceException) {
		super("persistence runtime Excetpion", persistenceException);
	}

	/**
	 * Constructs a PersistenceRuntimeException with a given exception reason
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 */
	public PersistenceRuntimeException(String aReason) {
		super(aReason);
	}

	/**
	 * Constructs a PersistenceRuntimeException with a given exception reason,
	 * an argument array.
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 * @param args
	 *            the argument list
	 */
	public PersistenceRuntimeException(String aReason, Object[] args) {
		super(aReason, args);
	}

	/**
	 * Constructs a PersistenceRuntimeException given a exception reason, a
	 * nested Throwable object.
	 * 
	 * @param aReason
	 *            the key to the localized reason stringdd
	 * @param aThrowable
	 *            nested throwable
	 */
	public PersistenceRuntimeException(String aReason, Throwable aThrowable) {
		super(aReason, aThrowable);
	}

	/**
	 * Constructs a PersistenceRuntimeException with the given reason, a nested
	 * Throwable object, and an argument array.
	 * 
	 * @param aReason
	 *            the key to the localized reason string
	 * @param aThrowable
	 *            nested throwable
	 * @param args
	 *            the argument list
	 */
	public PersistenceRuntimeException(String aReason, Throwable aThrowable,
			Object[] args) {
		super(aReason, aThrowable, args);
	}

	/**
	 * Constructs a PersistenceRuntimeException with the given reason, a nested
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
	public PersistenceRuntimeException(String aReason, Throwable aThrowable,
			Object[] args, Localizer aLocalizer) {
		super(aReason, aThrowable, args, aLocalizer);
	}

}
