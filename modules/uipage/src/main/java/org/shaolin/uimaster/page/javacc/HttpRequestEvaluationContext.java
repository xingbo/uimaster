package org.shaolin.uimaster.page.javacc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.flow.nodes.WebNode;

public class HttpRequestEvaluationContext implements EvaluationContext,
		Cloneable {
	private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

	private HttpServletRequest request = null;

	private WebNode node = null; // the node

	public HttpRequestEvaluationContext(HttpServletRequest req, WebNode n) {
		request = req;
		node = n;
	}

	/**
	 * Retruns the http request
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Retruns the node
	 * 
	 * @return
	 */
	public WebNode getNode() {
		return node;
	}

	/**
	 * Get the value of a variable with the specified name
	 * 
	 * @param name
	 *            the variable name
	 * @return the variable value object
	 * @throws EvaluationException
	 *             if can't find the variable with this name
	 */
	public Object getVariableValue(String name) throws EvaluationException {
		Object result = request.getAttribute(name);
		return result;
	}

	/**
	 * Set the value of a variable with the specified name
	 * 
	 * @param name
	 *            the variable name
	 * @param value
	 *            the variable value object
	 * @throws EvaluationException
	 *             if can't find the variable with this name
	 */
	public void setVariableValue(String name, Object value)
			throws EvaluationException {
		request.setAttribute(name, value);
	}

	/**
	 * Invoke a method with the the specified name and specified argument type
	 * classes and objects
	 * 
	 * @param name
	 *            the method name
	 * @param argClasses
	 *            list of argument type classes
	 * @param argObjects
	 *            list of argument objects
	 * @return the invocation result object
	 * @throws EvaluationException
	 *             if can't find the variable with this name
	 */
	public Object invokeMethod(String name, List argClasses, List argObjects)
			throws EvaluationException {
		if ("getRequest".equals(name)) {
			return this.getRequest();
		}
		
		throw new I18NRuntimeException(
				"Plugin method {0} not support in HttpRequestEvaluationContext",
				new Object[] { name });
	}

	/**
	 * Clone a new DefaultEvaluationContext with all existing variable values
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new I18NRuntimeException("Internal Error", e);
		}
	}

	/**
	 * Register the HttpServletRequest object in this class. The object will be
	 * hold in the current thread.
	 * 
	 * @param request
	 *            The HttpServletRequest object to be hold
	 */
	public static void registerCurrentRequest(HttpServletRequest request) {
		requestHolder.set(request);
	}

	/**
	 * Get the registered HttpServletRequest object of the current thread.
	 * 
	 * @return The HttpServletRequest object registered in the current thread
	 */
	public static HttpServletRequest getCurrentRequest() {
		return requestHolder.get();
	}

	/**
	 * Unregister the HttpServletRequest object which is hold by this class in
	 * the current thread.
	 */
	public static void unregisterCurrentRequest() {
		requestHolder.set(null);
	}
}
