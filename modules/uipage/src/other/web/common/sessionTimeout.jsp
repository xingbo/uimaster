<% //$Revision: 1.10 $%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="bmiasia.ebos.webflow.exception.*" %>
<%@ page import="bmiasia.ebos.webflow.WebflowConstants" %>
<%@ page import="bmiasia.ebos.appbase.util.*,bmiasia.ebos.webbase.util.UserLocaleUtil" %>

<html>
<head>
<title>SessionTimeOut</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/jsp/common/common.jsp" %>

</head>

<body>

<font color="red"><%=ResourceUtil.getResourceByRemote(null,"bmiasia.ebos.appbase.appbase.Bundle","SESSION_TIMEOUT") %></font><br>

</body>
</html>
<script language="javascript">
    
        alert('<%= ResourceUtil.getResourceByRemote(null,"bmiasia.ebos.appbase.appbase.Bundle","SESSION_TIMEOUT") %>');
        top.location.href="<%=webRoot%>/console/jsp/index.jsp";
        //return true;

    
</script>
