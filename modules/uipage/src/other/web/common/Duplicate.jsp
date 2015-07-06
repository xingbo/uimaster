<% //$Revision: 1.5 $%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<title><%=bmiasia.ebos.appbase.util.ResourceUtil.getResourceByRemote(null,"bmiasia.ebos.appbase.appbase.Bundle","DUPLICATE_ERROR") %></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>
<%=bmiasia.ebos.appbase.util.ResourceUtil.getResourceByRemote(null,"bmiasia.ebos.appbase.appbase.Bundle","DUPLICATE_ERROR_BODY") %>
<br>
<form>
<%@ include file="/jsp/common/Errors.jsp" %>
<input name="_pagename" type="hidden" value="DuplicatePage">
<input name="_outname" type="hidden" value="">
<input type="button" name="Back" value="Back" onClick="javascipt: history.go(-1);">
</form>

</body>
</html>
