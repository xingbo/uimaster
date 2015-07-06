
package org.shaolin.uimaster.page.flow.error;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.shaolin.uimaster.page.flow.WebflowConstants;

/**
 * utility class for WebflowError
 */
public class WebflowErrorUtil
{
    /**
     * create a WebflowError object.
     * @param msg  message or the key of message in resources
     * @param objs objs.length <= 4
     * @param t
     */
    public static WebflowError createWebflowError(
        String msg, Object[] objs, Throwable t)
    {
        Object[] values = new Object[4];
        int length = 0;
        if(objs != null)
        {
            length = Math.min(4, objs.length);
        }

        for(int i = 0; i < length; i ++)
        {
            values[i] = objs[i];
        }

        return new WebflowError(msg, values[0], values[1],
                                values[2], values[3], t);

    }
    /**
     * add the error message to the WebflowErrors and store it into request.
     * @param request the servlet request we are processing
     * @param prop the property name of the WebflowError
     * @param error the added WebflowError object
     */
    public static void addError(HttpServletRequest request,
                                String prop, WebflowError error)
    {
        WebflowErrors errors = (WebflowErrors)
            request.getAttribute(WebflowConstants.ERROR_KEY);
        if(errors == null)
            errors = new WebflowErrors();

        errors.add(prop, error);
        request.setAttribute(WebflowConstants.ERROR_KEY, errors);
    }

    /**
     *
     * Not Used!!!
     * add the errors to the WebflowErrors and store it into request, merge the
     *  errors
     * @param request the servlet request we are processing
     * @param newErrors the added WebflowErrors object
     */
    public static void addError(HttpServletRequest request,
                                WebflowErrors newErrors)
    {
        if(newErrors.empty()) return;

        WebflowErrors errors = (WebflowErrors)
            request.getAttribute(WebflowConstants.ERROR_KEY);
        if(errors == null || errors.empty())
        {
            request.setAttribute(WebflowConstants.ERROR_KEY, newErrors);
            return;
        }

        Iterator<String> keys = newErrors.properties();
        while (keys.hasNext())
        {
            String prop = keys.next();
            Iterator<WebflowError>  it =  newErrors.get(prop);
            while(it.hasNext())
            {
                errors.add(prop, (WebflowError)it.next());
            }
        }

        request.setAttribute(WebflowConstants.ERROR_KEY, errors);
    }
}
