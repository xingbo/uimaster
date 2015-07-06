package org.shaolin.uimaster.page.exception;

//imports
import org.shaolin.bmdp.i18n.Localizer;

/**
 * Exception for OD actions.
 *
 * @author Shaolin Wu
 */
public class ODProcessException extends ODException
{
    /**
     * Constructs a ODException with a given exception reason
     *
     * @param aReason
     */
    public ODProcessException(String aReason)
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
    public ODProcessException(String aReason, Object[] args)
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
    public ODProcessException(String aReason, Throwable aThrowable)
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
    public ODProcessException(String aReason, Throwable aThrowable,
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
    public ODProcessException(String aReason, Throwable aThrowable,
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }


    public static final String ___REVISION___ = "$Revision: 1.2 $";

    private static final long serialVersionUID = 6637940630141909726L;
}

