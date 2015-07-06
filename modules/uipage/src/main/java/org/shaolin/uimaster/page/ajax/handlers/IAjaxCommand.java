package org.shaolin.uimaster.page.ajax.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This defines the behavior of a command.
 */
public interface IAjaxCommand {

	public static final String EXECUTE_SUCCESS = "1";

	public static final String EXECUTE_FAILURE = "-1";

	/**
	 * Execute this command.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Object execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

}
