package org.shaolin.bmdp.exceptions;

import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.Localizer;

public abstract class ApplicationException extends BaseException
{
    /**
     * Constructs a ApplicationException with a given exception reason
     * 
     * @param   aReason     the key to the localized reason string
     */
    protected ApplicationException(String aReason)
    {
        super(aReason);
    }


    /**
     * Constructs a ApplicationException with a given exception reason, an argument 
     * array.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   args        the argument list
     */
    protected ApplicationException(String aReason, Object[] args)
    {
        super(aReason, args);
    }


    /**
     * Constructs a ApplicationException given a exception reason, a nested Throwable
     * object.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     */
    protected ApplicationException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }


    /**
     * Constructs an exception with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     * @param   args        the argument list
     */
    protected ApplicationException(String aReason, Throwable aThrowable, 
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs an exception with the given reason, a nested 
     * Throwable object, an argument array, and a localizer.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     * @param   args        the argument list
     * @param   aLocalizer  the localizer used for the error messages
     */
    protected ApplicationException(String aReason, Throwable aThrowable, 
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }
    
    /**
     * @deprecated
     */
    protected ApplicationException(String aReason, Throwable aThrowable, 
                            Object[] args, Localizer aLocalizer, boolean aNeedI18N)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

    private static final long serialVersionUID = 3805250691110616185L;
    
}
