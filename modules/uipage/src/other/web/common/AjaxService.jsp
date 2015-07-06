<%@page contentType="text/html; charset=UTF-8" language="java" errorPage=""%><%@page 
import="org.apache.log4j.Logger,
		bmiasia.ebos.security.util.SecurityUtil,
		bmiasia.ebos.webbase.util.UserLocaleUtil,
		bmiasia.ebos.common.util.LocaleContext,
		bmiasia.ebos.webflow.WebflowConstants,
		bmiasia.ebos.security.IUserContext,
		bmiasia.ebos.appbase.ajax.*,
		bmiasia.ebos.uihtml2.exception.*,
		bmiasia.ebos.uihtml2.context.*,
		bmiasia.ebos.uihtml2.engine.*,
		bmiasia.ebos.uihtml2.be.*,
		bmiasia.ebos.uihtml2.util.json.*,
        bmiasia.ebos.uihtml2.ajax.type.Component,
		bmiasia.ebos.webflow.util.WebFlowUtil,
        bmiasia.ebos.webflow.engine.ProcessHelper,
		javax.servlet.http.HttpSession"%><%
	//$Revision: 1.26 $
	if (request.getProtocol().compareTo("HTTP/1.0") == 0) 
	{
		response.setHeader("Pragma", "no-cache");
	} 
	else if (request.getProtocol().compareTo("HTTP/1.1") == 0) 
	{
		response.setHeader("Cache-Control", "no-cache");
	}
	response.setDateHeader("Expires", 0);
	request.setCharacterEncoding("UTF-8");

	HttpSession httpSession = request.getSession(false);
	if ( httpSession == null || httpSession.getAttribute(AjaxContext.AJAX_COMP_MAP) == null ) 
	{
		IDataItem dataItem = AjaxContextHelper.createSessionTimeOut(WebFlowUtil.getTimeoutPage());
        JSONArray array = new JSONArray();
		array.put(new JSONObject(dataItem));
		out.print(array.toString());
		return;
	}
	Object obj = httpSession.getAttribute(WebflowConstants.USER_SESSION_KEY);
	if (obj != null) 
	{
		SecurityUtil.registerCurrentUserContext((IUserContext) obj);
	}
	String locale = UserLocaleUtil.getUserLocale(request);
	LocaleContext.createLocaleContext(locale);
	if (request.getParameter("_ajaxUserEvent") != null) 
	{ // for new UI framework.
		try 
		{
            ProcessHelper.processSyncValues(request);
            
			HTMLGenerationContext htmlContext = new HTMLGenerationContext(request);
			htmlContext.setPageContext(pageContext);
            htmlContext.setAJAXComponentMap(AjaxContextHelper.getFrameMap(request));
			AjaxProcessor ajxProcessor = new AjaxProcessor(htmlContext);
			out.print(ajxProcessor.execute());
		} 
		catch (Exception ex) 
		{
			Logger.getLogger("/jsp/common/AjaxService.jsp").error(ex.getMessage(), ex);
			JSONException json = new JSONException(ex);			
			IDataItem dataItem = AjaxContextHelper.createErrorDataItem(json.toString());
            dataItem.setParent(ex.getMessage());
            JSONArray array = new JSONArray();
			array.put(new JSONObject(dataItem));
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
				out.print(result.toString());
			}
		} 
		catch (Exception ex) 
		{
			Logger.getLogger("/jsp/common/AjaxService.jsp").error(ex.getMessage(), ex);

			StringBuffer sb = new StringBuffer();
			sb.append("[ajax_error]");
			sb.append((new JSONException(ex)).toString());
			out.print(sb.toString());
		}
	}
	SecurityUtil.unregisterCurrentUserContext();
	LocaleContext.clearLocaleContext();
%>
