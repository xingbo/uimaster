package org.shaolin.bmdp.persistence;


import org.shaolin.bmdp.i18n.Localizer;
import org.shaolin.bmdp.persistence.PersistenceException;

/**
 * The InvalidSearchQueryException exception is thrown by
 * a remove relation method to indicate that the
 * specified relation does not exist
 * 
 */
public class InvalidSearchQueryException extends PersistenceException
{
    /**
     * Constructs a InvalidSearchQueryException with a given exception reason
     * 
     * @param   aReason     the key to the localized reason string
     */
    public InvalidSearchQueryException(String aReason)
    {
        super(aReason);
    }

    /**
     * Constructs a InvalidSearchQueryException with a given exception reason, an argument 
     * array.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   args        the argument list
     */
    public InvalidSearchQueryException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    /**
     * Constructs a InvalidSearchQueryException given a exception reason, a nested Throwable
     * object.
     * 
     * @param   aReason     the key to the localized reason stringdd
     * @param   aThrowable  nested throwable
     */
    public InvalidSearchQueryException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }

    /**
     * Constructs a InvalidSearchQueryException with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     * @param   args        the argument list
     */
    public InvalidSearchQueryException(String aReason, Throwable aThrowable, 
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs a InvalidSearchQueryException with the given reason, a nested 
     * Throwable object, an argument array, and a localizer.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     * @param   args        the argument list
     * @param   aLocalizer  the localizer used for the error messages
     */
    public InvalidSearchQueryException(String aReason, Throwable aThrowable, 
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

}

