<% //$Revision: 1.11 $%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="bmiasia.ebos.webflow.exception.WebflowError" %>
<%@ page import="bmiasia.ebos.webflow.exception.WebflowErrors" %>
<%@ page import="bmiasia.ebos.common.exception.EBOSRuntimeException" %>
<%@ page import="bmiasia.ebos.webflow.WebflowConstants" %>
<%@ page import="bmiasia.ebos.appbase.util.ResourceUtil" %>
<%@ include file="/jsp/common/common.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script language="javascript" src="<%=webRoot%>/js/common.js"></script>
<script language="javascript" src="<%=webRoot%>/common_js/common.js"></script>
<link rel="stylesheet" href="<%=webRoot%>/css/main.css" type="text/css">
<link rel="stylesheet" href="<%=webRoot%>/css/controls.css" type="text/css">
<script language="javascript">
function initPage()
{
}
</script>
</head>

<body onload="initPage();">
<% long suffix = System.currentTimeMillis();%>
<DIV id="uIPanel1Root.titlePanel" class=table-tp name="uIPanel1Root">
<DIV class=title-tp>
<DIV style="WIDTH: 20px; Height: 23px; FLOAT: left">&nbsp;</DIV>
<DIV style="FLOAT: left; CLEAR: right"><%=ResourceUtil.getResourceByHttp(null,"bmiasia.ebos.appbase.appbase.Bundle","ERROR_MSG_TITLE") %></DIV>
</DIV>
<DIV class=content-tp>
<DIV id="uIPanel1Root.wrapperPanel">
	<DIV style="width:100%;">
	<DIV style="width:50px;height:100%;FLOAT: left">
	<img src="<%=webRoot%>/images/Error.png" ></img>
	</DIV>
	<DIV style="width:100%;height:100%;vertical-align:bottom;padding-top:10px;padding-bottom:10px;">
	<%
	WebflowErrors errorArray = (WebflowErrors)request.getAttribute(WebflowConstants.ERROR_KEY);
	if(errorArray != null && !errorArray.empty())
	{
        for (Iterator keys = errorArray.properties(); keys.hasNext();)
        {
            String prop = (String)keys.next();
            for (Iterator it = errorArray.get(prop); it.hasNext();)
            {
                WebflowError error = (WebflowError)it.next();
                String errorKey = error.getKey();
                String msg = (errorKey == null) ? null : MessageFormat.format(errorKey, error.getValues());
				out.println(ResourceUtil.getResourceByHttp(null,"bmiasia.ebos.appbase.appbase.Bundle","ERROR_REASON")+prop+": "+msg);
                Throwable currThrowable = error.getThrowable();
                if(currThrowable instanceof EBOSRuntimeException)
                {
                	String errorCode = ((EBOSRuntimeException)currThrowable).getReason();
                	out.println("<br>"+ResourceUtil.getResourceByHttp(null,"bmiasia.ebos.appbase.appbase.Bundle","ERROR_CODE")+errorCode);
                }
            }
        }
	}
	%>
	</DIV> 
	</DIV>
	<br>
	<DIV id=uIPanel1.titlePanel class=table-tp name="titlePanel">
	<DIV class=title-tp onclick="bmiasia_slide('uIPanel1.wrapperPanel<%=suffix %>','<%=webRoot%>/images/table-close.gif','<%=webRoot%>/images/table-open.gif','uIPanel1.arrowIcon',event);">
	<DIV style="WIDTH: 20px; FLOAT: left"><IMG id=uIPanel1.arrowIcon onclick="bmiasia_slide('uIPanel1.wrapperPanel<%=suffix %>','<%=webRoot%>/images/table-close.gif','<%=webRoot%>/images/table-open.gif','uIPanel1.arrowIcon',event);" border=0 src="<%=webRoot%>/images/table-close.gif"></DIV>
	<DIV style="FLOAT: left; CLEAR: right"><%=bmiasia.ebos.appbase.util.ResourceUtil.getResourceByHttp(null,"bmiasia.ebos.appbase.appbase.Bundle","ERROR_MESSAGE") %></DIV>
	</DIV>
	<DIV class=content-tp>
	<DIV id="uIPanel1.wrapperPanel<%=suffix %>" style="display:none;">
	<%@ include file="/jsp/common/Errors.jsp" %>
	</DIV>
	</DIV>
	</DIV>

</DIV>
</DIV>
</DIV>

</body>
</html>
