package org.shaolin.uimaster.page.ajax.handlers;

import org.shaolin.uimaster.page.exception.AjaxException;

//imports

/**
 * Exception for Ajax
 *
 */
public class AjaxHandlerException extends AjaxException
{
    /**
     * Constructs a AjaxHandlerException with a given exception reason
     *
     * @param aReason
     */
    public AjaxHandlerException(String aReason)
    {
        super(aReason);
    }
    
    public AjaxHandlerException(String aReason,Throwable ex)
    {
        super(aReason,ex);
    }

    /**
     * Constructs a AjaxHandlerException with a given exception reason, an argument
     * array.
     *
     * @param aReason
     * @param args
     */
    public AjaxHandlerException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    private static final long serialVersionUID = 6637940630141909726L;
}

