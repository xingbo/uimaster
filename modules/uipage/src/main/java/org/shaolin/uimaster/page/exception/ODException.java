package org.shaolin.uimaster.page.exception;

//imports
import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.Localizer;

public class ODException extends BaseException
{
    /**
     * Constructs a ODException with a given exception reason
     *
     * @param aReason
     */
    public ODException(String aReason)
    {
        super(aReason);
    }

    /**
     * Constructs a ODException with a given exception reason, an argument
     * array.
     *
     * @param aReason
     * @param args
     */
    public ODException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    /**
     * Constructs a ODException given a exception reason, a nested Throwable
     * object.
     *
     * @param aReason
     * @param aThrowable
     */
    public ODException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }

    /**
     * Constructs a ODException with the given reason, a nested
     * Throwable object, and an argument array.
     *
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public ODException(String aReason, Throwable aThrowable,
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs a ODException with the given reason, a nested
     * Throwable object, and an argument array.
     *
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public ODException(String aReason, Throwable aThrowable,
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

}

