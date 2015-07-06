package org.shaolin.uimaster.page.javacc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.runtime.be.IBusinessEntity;
import org.shaolin.bmdp.utils.SerializeUtil;
import org.shaolin.bmdp.utils.StringUtil;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.cache.UIFlowCacheManager;
import org.shaolin.uimaster.page.flow.WebFlowUtil;
import org.shaolin.uimaster.page.flow.nodes.WebChunk;

public class HttpSessionEvaluationContext implements EvaluationContext, Cloneable
{
    private transient HttpSession session = null;
    private transient HttpServletRequest request = null;
    private transient HttpServletResponse response = null;
    private WebChunk chunk = null;

    public HttpSessionEvaluationContext(HttpServletRequest req, WebChunk chunk)
    {
        this(req, null, chunk);
    }
    
    public HttpSessionEvaluationContext(HttpServletRequest req, HttpServletResponse resp, WebChunk chunk)
    {
        request = req;
        response = resp;
        session = request.getSession(true);
        this.chunk = chunk;
    }

    /**
     * Returns the http session
     * @return
     */
    public HttpSession getSession()
    {
        return session;
    }

    /**
     * Returns the http request
     * @return
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    /**
     * Returns the http response
     * @return
     */    
    public HttpServletResponse getResponse()
    {
        return response;   
    }

    /**
     *  Get the value of a variable with the specified name
     *
     *  @param      name    the variable name
     *  @return     the variable value object
         *  @throws     EvaluationException     if can't find the variable with this name
     */
    public Object getVariableValue(String name) throws EvaluationException
    {
        name = fixGlobalVarName(name);
        Object result = session.getAttribute(name);

        return result;
    }

    /**
     *  Set the value of a variable with the specified name
     *
     *  @param      name    the variable name
     *  @param      value   the variable value object
         *  @throws     EvaluationException     if can't find the variable with this name
     */
    public void setVariableValue(String name, Object value) throws EvaluationException
    {
        name = fixGlobalVarName(name);
        checkObjectLimit(value);
        session.setAttribute(name, value);
    }

    /**
     *  Invoke a method with the the specified name and specified argument type classes and objects
     *
     *  @param      name    the method name
     *  @param      argClasses     list of argument type classes
     *  @param      argObjects     list of argument objects
     *  @return     the invocation result object
         *  @throws     EvaluationException     if can't find the variable with this name
     */
    public Object invokeMethod(String name, List argClasses, List argObjects) throws
        EvaluationException
    {
        try
        {
            List argClassList = new ArrayList();
            argClassList.add(HttpSessionEvaluationContext.class);
            argClassList.addAll(argClasses);

            List argObjectList = new ArrayList();
            argObjectList.add(this);
            argObjectList.addAll(argObjects);

            return WebflowContextFunction.class.getMethod(name,
                (Class[])argClassList.toArray(new Class[argClassList.size()])
                ).invoke(
                null, (Object[])argObjectList.toArray(
                new Object[argObjectList.size()]));
        }
        catch (IllegalAccessException ex)
        {
            throw new I18NRuntimeException("Execute plugin method {0} error in HttpSessionEvaluationContext",
            		ex,new Object[]{name});
        }
        catch (IllegalArgumentException ex)
        {
        	throw new I18NRuntimeException("Execute plugin method {0} error in HttpSessionEvaluationContext",
        			ex,new Object[]{name});
        }
        catch (InvocationTargetException ex)
        {
        	throw new I18NRuntimeException("Execute plugin method {0} error in HttpSessionEvaluationContext",
        			ex,new Object[]{name});
        }
        catch (NoSuchMethodException ex)
        {
        	throw new I18NRuntimeException("Error executing WebFlow UserAction {0}, the action instance is null",
        			ex,new Object[]{name});
        }
    }

    /**
     *  Clone a new DefaultEvaluationContext with all existing variable values
     */
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException e)
        {
        	throw new I18NRuntimeException("Internal Error", e);
        }
    }

    /**
     * add prefix: chunk name
     * @param name
     * @return
     */
    private String fixGlobalVarName(String name)
    {
        if (chunk != null &&
            (!WebFlowUtil.isBuildInVariables(name)))
        {
            name = chunk.getEntityName() + "." + name;
        }

        return name;
    }
    
    private void checkObjectLimit(Object value) throws EvaluationException
    {
        long limit = UIFlowCacheManager.getSessionObjectLimit();
        if (limit > 0L && (value instanceof List || value instanceof Map ||
                value instanceof Set || value instanceof IBusinessEntity))
        {
            long objectSize = SerializeUtil.estimateObjectSize(value);
            if (objectSize > limit)
            {
            	throw new I18NRuntimeException("Session object limit exceeded: {0} > {1} . Object to string: {2}",
            			new Object[]{StringUtil.getSizeString(objectSize), StringUtil.getSizeString(limit), value});
            }
        }
    }

}
