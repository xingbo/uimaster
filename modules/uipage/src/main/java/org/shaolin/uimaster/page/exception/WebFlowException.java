package org.shaolin.uimaster.page.exception;

//imports
import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.Localizer;

public class WebFlowException extends BaseException
{
    /**
     * Constructs a WebFlowException with a given exception reason
     *
     * @param aReason
     */
    public WebFlowException(String aReason)
    {
        super(aReason);
    }

    /**
     * Constructs a WebFlowException with a given exception reason, an argument
     * array.
     *
     * @param aReason
     * @param args
     */
    public WebFlowException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    /**
     * Constructs a WebFlowException given a exception reason, a nested Throwable
     * object.
     *
     * @param aReason
     * @param aThrowable
     */
    public WebFlowException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }

    /**
     * Constructs a WebFlowException with the given reason, a nested
     * Throwable object, and an argument array.
     *
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public WebFlowException(String aReason, Throwable aThrowable,
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs a WebFlowException with the given reason, a nested
     * Throwable object, and an argument array.
     *
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public WebFlowException(String aReason, Throwable aThrowable,
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

    private static final long serialVersionUID = 6637940630141909726L;
}

