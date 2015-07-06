package org.shaolin.bmdp.persistence.query.mapping;

import org.shaolin.bmdp.i18n.Localizer;
import org.shaolin.bmdp.persistence.PersistenceRuntimeException;

/**
 * An exception thrown because of mapping error.
 */
public class MappingException extends PersistenceRuntimeException
{
    /**
     * Constructs a MappingException with a given exception reason
     * 
     * @param   aReason     the key to the localized reason string
     */
    public MappingException(String aReason)
    {
        super(aReason);
    }

    /**
     * Constructs a MappingException with a given exception reason, an argument 
     * array.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   args        the argument list
     */
    public MappingException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    /**
     * Constructs a MappingException given a exception reason, a nested Throwable
     * object.
     * 
     * @param   aReason     the key to the localized reason stringdd
     * @param   aThrowable  nested throwable
     */
    public MappingException(String aReason, Throwable aThrowable)
    {
        super(aReason, aThrowable);
    }

    /**
     * Constructs a MappingException with the given reason, a nested 
     * Throwable object, and an argument array.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     * @param   args        the argument list
     */
    public MappingException(String aReason, Throwable aThrowable, 
                            Object[] args)
    {
        super(aReason, aThrowable, args);
    }

    /**
     * Constructs a MappingException with the given reason, a nested 
     * Throwable object, an argument array, and a localizer.
     * 
     * @param   aReason     the key to the localized reason string
     * @param   aThrowable  nested throwable
     * @param   args        the argument list
     * @param   aLocalizer  the localizer used for the error messages
     */
    public MappingException(String aReason, Throwable aThrowable, 
                            Object[] args, Localizer aLocalizer)
    {
        super(aReason, aThrowable, args, aLocalizer);
    }

    private static final long serialVersionUID = -8991692182425106842L;
} 
