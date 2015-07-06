package org.shaolin.bmdp.exceptions;

//imports
import java.io.Serializable;

import org.shaolin.bmdp.i18n.Localizer;

public final class ExceptionData implements Serializable {
	/**
     * The exception reason for this ExceptionData object
     */
    private String reason;

    /**
     * An array of Objects as exception arguments.
     */
    private Object[] arguments;
    
    private Localizer localizer;
    
    private static final long serialVersionUID = 0x2639D7C3CEFA54B9L;
	
    /**
     * Constructs an ExceptionData object from an exception reason, 
     * an argument array, and a localizer.
     * 
     * @param   aReason             the key to the localized reason string
     * @param   args                the argument list
     * @param   aLocalizer          the localizer used for the error messages
     */
    public ExceptionData(String aReason, Object[] args, Localizer aLocalizer)
    {
        reason = aReason;
        arguments = args;
        localizer = aLocalizer;
    }

    public String getReason()
    {
        return reason;
    }

    public String getMessage()
    {
        return getMessage(null, null);
    }

    public String getMessage(String localeConfig)
    {
        return getMessage(null, localeConfig);
    }

    public String getMessage(Localizer localizer)
    {
        return getMessage(localizer, null);
    }
    
    public String getMessage(Localizer localizer, String localeConfig)
    {
        Localizer l = getLocalizer(localizer, this.localizer);
        return l.getString(reason, arguments, localeConfig);
    }

    public Object getArgument(int argPosition)
    {
        return arguments[argPosition];
    }

    public int getArgumentNumber()
    {
        if (arguments == null)
        {
            return 0;
        }
        return arguments.length;
    }
    
    private Localizer getLocalizer(Localizer l1, Localizer l2)
    {
        if (l1 != null)
        {
            return l1;
        }
        if (l2 != null)
        {
            return l2;
        }
        return Localizer.EBOS_ERRORS;
    }

    public static Throwable getRootCause(Throwable t)
    {
        Throwable rootCause = null;
        while ((rootCause = t.getCause()) != null)
        {
            t = rootCause;
        }
        return t;
    }


}//ExceptionData
