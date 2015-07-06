<% //$Revision: 1.5 $%>
<%@ page import="bmiasia.ebos.webbase.constant.IWebConstantManager"%>
<%@ page import="bmiasia.ebos.businessmanager.factory.ManagerFactory"%>
<%@ include file="/jsp/common/taglibs.jsp"%>
<% 
    IWebConstantManager	constantManager	= (IWebConstantManager)ManagerFactory.getManager(
            ManagerFactory.MANAGER_TYPE_LOCALIMPL, IWebConstantManager.MANAGER_NAME);
	String webRoot = constantManager.get(IWebConstantManager.KEY_WEB_ROOT);
	String servletRoot = constantManager.get(IWebConstantManager.KEY_SERVLET_ROOT);
%>
<script type="text/javascript" src="<%=webRoot%>/js/common.js"></script>
<script type="text/javascript" src="<%=webRoot%>/js/ajax.js"></script>
<script type="text/javascript" src="<%=webRoot%>/js/ebos.js"></script>
