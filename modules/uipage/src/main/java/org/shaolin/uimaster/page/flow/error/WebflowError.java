package org.shaolin.uimaster.page.flow.error;

public class WebflowError {

    /**
     * Construct an action error with no replacement values.
     *
     * @param key Message key for this error message
     */
    public WebflowError(String key) {

        this(key, null, null, null, null);

    }

    /**
     * Construct an action error with no replacement values.
     *
     * @param key Message key for this error message
     * @param t the throwable which causes this error
     */
    public WebflowError(String key, Throwable t) {

        this(key, null, null, null, null, t);

    }

    /**
     * Construct an action error with the specified replacement values.
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     */
    public WebflowError(String key, Object value0, Object value1) {

        this(key, value0, value1, null, null);

    }


    /**
     * Construct an action error with the specified replacement values.
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     * @param value2 Third replacement value
     */
    public WebflowError(String key, Object value0, Object value1,
                       Object value2) {

        this(key, value0, value1, value2, null);

    }


    /**
     * Construct an action error with the specified replacement values.
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     * @param value2 Third replacement value
     * @param value3 Fourth replacement value
     */
    public WebflowError(String key, Object value0, Object value1,
                       Object value2, Object value3) {

        super();
        this.key = key;
        values[0] = value0;
        values[1] = value1;
        values[2] = value2;
        values[3] = value3;

    }

    /**
     * Construct an action error with the specified replacement values.
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     * @param value2 Third replacement value
     * @param value3 Fourth replacement value
     * @param t the throwable which causes this error
     */
    public WebflowError(String key, Object value0, Object value1,
                       Object value2, Object value3, Throwable t) {

        super();
        this.key = key;
        values[0] = value0;
        values[1] = value1;
        values[2] = value2;
        values[3] = value3;
        throwable = t;

    }



    // ----------------------------------------------------- Instance Variables


    /**
     * The message key for this error message.
     */
    private String key = null;


    /**
     * The replacement values for this error mesasge.
     */
    private Object values[] = { null, null, null, null };

    /**
     *
     * the Throwable which causes this error
     */
    private Throwable throwable;


    // --------------------------------------------------------- Public Methods


    /**
     * Get the message key for this error message.
     */
    public String getKey() {

        return (this.key);

    }


    /**
     * Get the replacement values for this error message.
     */
    public Object[] getValues() {

        return (this.values);

    }

    /**
     *  Returns the throwable which causes this error
     * @return Throwable
     */
    public Throwable getThrowable()
    {
        return throwable;
    }

}
