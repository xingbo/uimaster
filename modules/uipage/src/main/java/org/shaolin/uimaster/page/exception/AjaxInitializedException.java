package org.shaolin.uimaster.page.exception;

//imports

/**
 * Exception for Ajax
 *
 */
public class AjaxInitializedException extends AjaxException
{
    /**
     * Constructs a AjaxInitializedException with a given exception reason
     *
     * @param aReason
     */
    public AjaxInitializedException(String aReason)
    {
        super(aReason);
    }
    
    public AjaxInitializedException(String aReason,Throwable ex)
    {
        super(aReason,ex);
    }

    /**
     * Constructs a AjaxInitializedException with a given exception reason, an argument
     * array.
     *
     * @param aReason
     * @param args
     */
    public AjaxInitializedException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    private static final long serialVersionUID = 6637940630141909726L;
}

