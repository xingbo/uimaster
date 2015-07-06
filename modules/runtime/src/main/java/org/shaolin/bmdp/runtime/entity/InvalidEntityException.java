package org.shaolin.bmdp.runtime.entity;

import org.shaolin.bmdp.exceptions.BaseRuntimeException;
import org.shaolin.bmdp.i18n.Localizer;

/**
 * Signals that an entity was not found
 */
public class InvalidEntityException extends BaseRuntimeException {
	
	/**
     * Constructs an EntityNotFoundException with a given exception reason
     * 
     * @param   aReason     the key to the localized reason string.
     */
    public InvalidEntityException(String aReason)
    {
        super(aReason, null, null);
    }
    
	/**
     * Constructs an EntityNotFoundException with a nested throwable
     * 
     * @param   aThrowable      the nested throwable
     */
    public InvalidEntityException(Throwable aThrowable)
    {
    	super(null, aThrowable, null);
    }
    
	/**
     * Constructs an EntityNotFoundException with a given exception reason,
     * an argument array
     * 
     * @param   aReason     the key to the localized reason string.
     * @param   args        the argument list
     */
    public InvalidEntityException(String aReason, Object[] args)
    {
        super(aReason, null, args);
    }


	/**
     * Constructs an EntityNotFoundException with a given exception reason,
     * a nested throwable object
     * 
     * @param   aReason         the key to the localized reason string.
     * @param   aThrowable      the nested throwable
     */
    public InvalidEntityException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable, null);
    }


	/**
     * Constructs an EntityNotFoundException with a given exception reason,
     * a nested throwable object, an argument array
     * 
     * @param   aReason         the key to the localized reason string.
     * @param   aThrowable      the nested throwable
     * @param   args            the argument list
     */
    public InvalidEntityException(String aReason, 
                                   Throwable aThrowable, 
                                   Object[] args)
    {
        super(aReason, aThrowable, args, null);
    }


	/**
     * Constructs an EntityNotFoundException with a given exception reason,
     * a nested throwable object, an argument array and a localizer
     * 
     * @param   aReason         the key to the localized reason string.
     * @param   aThrowable      the nested throwable
     * @param   args            the argument list
     * @param   aLocalizer      the localizer used for the error messages
     */
    public InvalidEntityException(String aReason, 
                                   Throwable aThrowable, 
                                   Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

	private static final long serialVersionUID = 6608533739900225244L;
}
