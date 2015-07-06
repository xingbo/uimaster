package org.shaolin.bmdp.exceptions;

import org.shaolin.bmdp.i18n.Localizer;

public class BusinessOperationException extends ApplicationException
{
     /**
     * Constructs a ApplicationException with a given exception reason
     * 
     * @param aReason
     */
    public BusinessOperationException(String aReason)
    {
        super(aReason);
    }


    /**
     * Constructs a ApplicationException with a given exception reason, an argument 
     * array.
     * 
     * @param aReason
     * @param args
     */
    public BusinessOperationException(String aReason, Object[] args)
    {
        super(aReason, args);
    }


    /**
     * Constructs a ApplicationException given a exception reason, a nested Throwable
     * object.
     * 
     * @param aReason
     * @param aThrowable
     */
    public BusinessOperationException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }


    /**
     * Constructs an exception with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public BusinessOperationException(String aReason, Throwable aThrowable, 
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }
    
    
    /**
     * Constructs an exception with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public BusinessOperationException(String aReason, Throwable aThrowable, 
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

}
