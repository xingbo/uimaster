/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.uimaster.page.ajax.Table;
import org.shaolin.uimaster.page.ajax.handlers.ErrorHelper;
import org.shaolin.uimaster.page.ajax.handlers.IAjaxCommand;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.IErrorItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.flow.ProcessHelper;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AjaxServlet extends HttpServlet {
	
	private static final long serialVersionUID = 236538261853041089L;
	private static final Logger logger = LoggerFactory.getLogger(AjaxServlet.class);
	
	private String charset = "UTF-8";
	
	public void init() throws ServletException {
		String value = getServletConfig().getInitParameter("content");
        if (value != null)
        {
            String content = value;
            //parse charset
            String[] s = content.split(";", 0);
            for (int i = 0, n = s.length; i < n; i++)
            {
                if (s[i].startsWith("charset="))
                {
                    charset = s[i].substring(8);
                    break;
                }
            }
        }
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		process(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		process(request, response);
	}
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
		
		if (request.getProtocol().compareTo("HTTP/1.0") == 0) 
		{
			response.setHeader("Pragma", "no-cache");
		} 
		else if (request.getProtocol().compareTo("HTTP/1.1") == 0) 
		{
			response.setHeader("Cache-Control", "no-cache");
		}
		response.setDateHeader("Expires", 0);
		response.setContentType("json");
		response.setCharacterEncoding(charset);
		request.setCharacterEncoding(charset);

		HttpSession httpSession = request.getSession(false);
		if (httpSession == null
				|| httpSession.getAttribute(AjaxContext.AJAX_COMP_MAP) == null) {
			PrintWriter out = response.getWriter();
			IDataItem dataItem = AjaxActionHelper
					.createSessionTimeOut(WebConfig.replaceWebContext(WebConfig.getTimeoutPage()));
			JSONArray array = new JSONArray();
			array.put(new JSONObject(dataItem));
			out.print(array.toString());
			return;
		}
		HttpSession session = request.getSession();
		Object currentUserContext = session.getAttribute(WebflowConstants.USER_SESSION_KEY);
		String userLocale = WebConfig.getUserLocale(request);
		List userRoles = (List)session.getAttribute(WebflowConstants.USER_ROLE_KEY);
		String userAgent = request.getHeader("user-agent");
		boolean isMobile = MobilitySupport.isMobileRequest(userAgent);
		//add user-context thread bind
        UserContext.registerCurrentUserContext(currentUserContext, userLocale, userRoles, isMobile);
		LocaleContext.createLocaleContext(userLocale);
		
		AppContext.register((IAppServiceManager)this.getServletContext().getAttribute(IAppServiceManager.class.getCanonicalName()));
		
		if (request.getParameter("_ajaxUserEvent") != null) 
		{ // for new UI framework.
			try 
			{
				String actionName = request.getParameter("_actionName");
				if (actionName != null && "exportTable".equals(actionName)) {
					response.setContentType("application/x-download");    
				    response.addHeader("Content-Disposition","attachment;filename=TableReport.xls");
				    Map uiMap = AjaxActionHelper.getFrameMap(request);
				    Table comp = (Table)uiMap.get(request.getParameter("_uiid"));
				    comp.exportAsExcel(response.getOutputStream());
				} else {
					PrintWriter out = response.getWriter();
		            ProcessHelper.processSyncValues(request);
		            
					HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request);
					AjaxProcessor ajxProcessor = new AjaxProcessor(htmlContext);
					out.print(ajxProcessor.execute());
				}
			} 
			catch (Throwable ex) 
			{
				logger.error(ex.getMessage(), ex);
//				JSONException json = new JSONException(ex);			
//				IDataItem dataItem = AjaxActionHelper.createErrorDataItem(json.toString());
//	            dataItem.setParent(ex.getMessage());
				JSONException json = new JSONException(ex);
				String uiid = request.getParameter("_uiid");
	    		String errorMsgTitle = "Error Action";
	    		String errorMsgBody = ex.getMessage(); 
	    		String exceptionTrace = json.toString(); 
	            String image = "/images/Error.png";
	            String jsSnippet = "";
	            String html = null;
	            IErrorItem dataItem = ErrorHelper.createErrorItem(uiid, errorMsgTitle, 
	            				errorMsgBody, exceptionTrace, image, jsSnippet, html);
	            JSONArray array = new JSONArray();
				array.put(new JSONObject(dataItem));
				PrintWriter out = response.getWriter();
				out.print(array.toString());
			}
		} 
		else 
		{  // for old ajax calling.
			try 
			{
				String serviceName = request.getParameter("serviceName");
				if (serviceName == null || serviceName.length() == 0) 
				{
					throw new IllegalArgumentException("Invoking service name is null, please make sure service name.");
				}

				IAjaxCommand ajaxCommand = AjaxFactory.getIAjaxCommand(serviceName);
				Object result = ajaxCommand.execute(request, response);
				if (result != null) 
				{
					PrintWriter out = response.getWriter();
					out.print(result.toString());
				}
			} 
			catch (Exception ex) 
			{
				logger.error(ex.getMessage(), ex);

				StringBuffer sb = new StringBuffer();
				sb.append("[ajax_error]");
				sb.append((new JSONException(ex)).toString());
				PrintWriter out = response.getWriter();
				out.print(sb.toString());
			}
		}
		AjaxActionHelper.removeAjaxContext();
		UserContext.unregisterCurrentUserContext();
		LocaleContext.clearLocaleContext();
    }
}
