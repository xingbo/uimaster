package org.shaolin.uimaster.page.exception;

//imports
import org.shaolin.bmdp.exceptions.BaseException;
import org.shaolin.bmdp.i18n.Localizer;

/**
 * Base class for UIPage exception
 */
public class UIPageException extends BaseException
{
    /**
     * Constructs a UIPageException with a given exception reason
     * 
     * @param aReason
     */
    public UIPageException(String aReason)
    {
        super(aReason);
    }

    /**
     * Constructs a UIPageException with a given exception reason, an argument 
     * array.
     * 
     * @param aReason
     * @param args
     */
    public UIPageException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    /**
     * Constructs a UIPageException given a exception reason, a nested Throwable
     * object.
     * 
     * @param aReason
     * @param aThrowable
     */
    public UIPageException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }

    /**
     * Constructs a UIPageException with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public UIPageException(String aReason, Throwable aThrowable, 
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs a UIPageException with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param aReason
     * @param aThrowable
     * @param args
     */
    public UIPageException(String aReason, Throwable aThrowable, 
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }


    public static final String ___REVISION___ = "$Revision: 1.3 $";

    private static final long serialVersionUID = -2201969585960483368L;
}//UIPageException
