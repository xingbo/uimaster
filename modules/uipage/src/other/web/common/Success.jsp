<% //$Revision: 1.5 $%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<title>Add/Update Success</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body onLoad="javascript:parent.searchFrame.location.reload();">
<form>
<input name="_pagename" type="hidden" value="SuccessPage">
<input name="_outname" type="hidden" value="">
<%= bmiasia.ebos.appbase.util.ResourceUtil.getResourceByRemote(null,"bmiasia.ebos.appbase.appbase.Bundle","ACTION_SUCCESS")%><br>
</form>

</body>
</html>
