package org.shaolin.uimaster.page.flow.error;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


/**
 * <p>A class that encapsulates the error messages being reported by
 * webflow engine. the object of this class will be set into request attribute
 * scope, you can reference the stored key with
 * <code> bmiasia.ebos.webflow.WebflowConstants.ERROR_KEY</code> .
 *
 * Data Structure:
 *
 * map:
 * key: String Object, named as property
 * value: list of WebflowError objects
 *
 * <p>Each individual error is described by an <code>WebflowError</code>
 * object, which contains a message key (to be looked up in an appropriate
 * message resources database), and up to four placeholder arguments used for
 * parametric substitution in the resulting message.</p>
 *
 */

public class WebflowErrors implements Serializable {

    /**
     * The accumulated set of <code>WebflowError</code> objects (represented
     * as an ArrayList) for each property, keyed by property name.
     */
    protected HashMap<String, ArrayList<WebflowError>> errors = 
    		new HashMap<String, ArrayList<WebflowError>>();

    /**
     * Add an error message to the set of errors for the specified property.
     *
     * @param property Property name (or WebflowError.GLOBAL_ERROR)
     * @param error The error message to be added
     */
    public void add(String property, WebflowError error) {
        ArrayList<WebflowError> list = errors.get(property);
        if (list == null) {
            list = new ArrayList<WebflowError>();
            errors.put(property, list);
        }
        list.add(error);
    }


    /**
     * Clear all error messages recorded by this object.
     */
    public void clear() {
        errors.clear();
    }


    /**
     * Return <code>true</code> if there are no error messages recorded
     * in this collection, or <code>false</code> otherwise.
     */
    public boolean empty() {
        return (errors.size() == 0);
    }


    /**
     * Return the set of all recorded error messages, without distinction
     * by which property the messages are associated with.  If there are
     * no error messages recorded, an empty enumeration is returned.
     */
    public Iterator<WebflowError> get() {
        if (errors.size() == 0)
            return (Collections.EMPTY_LIST.iterator());
        ArrayList<WebflowError> results = new ArrayList<WebflowError>();
        Iterator<String> props = errors.keySet().iterator();
        while (props.hasNext()) {
            String prop = props.next();
            Iterator<WebflowError> errors = this.errors.get(prop).iterator();
            while (errors.hasNext())
                results.add(errors.next());
        }
        return (results.iterator());
    }


    /**
     * Return the set of error messages related to a specific property.
     * If there are no such errors, an empty enumeration is returned.
     *
     * @param property Property name (or WebflowErrors.GLOBAL_ERROR)
     */
    public Iterator<WebflowError> get(String property) {
        ArrayList<WebflowError> list = errors.get(property);
        if (list == null)
            return (Collections.EMPTY_LIST.iterator());
        else
            return (list.iterator());
    }


    /**
     * Return the set of property names for which at least one error has
     * been recorded.  If there are no errors, an empty Iterator is returned.
     * If you have recorded global errors, the String value of
     * <code>WebflowErrors.GLOBAL_ERROR</code> will be one of the returned
     * property names.
     */
    public Iterator<String> properties() {
        return (errors.keySet().iterator());
    }


    /**
     * Return the number of errors recorded for all properties (including
     * global errors).  <strong>NOTE</strong> - it is more efficient to call
     * <code>empty()</code> if all you care about is whether or not there are
     * any error messages at all.
     */
    public int size() {
        int total = 0;
        Iterator keys = errors.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            ArrayList<WebflowError> list = errors.get(key);
            total += list.size();
        }
        return (total);
    }


    /**
     * Return the number of errors associated with the specified property.
     *
     * @param property Property name (or WebflowErrors.GLOBAL_ERROR)
     */
    public int size(String property) {
        ArrayList<WebflowError> list = errors.get(property);
        if (list == null)
            return (0);
        else
            return (list.size());
    }

    private static final long serialVersionUID = 1407591672624774678L;
}
