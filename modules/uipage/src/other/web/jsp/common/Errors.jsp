<% //$Revision: 1.9 $%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="bmiasia.ebos.appbase.util.ResourceUtil" %>
<%@ page import="bmiasia.ebos.webflow.exception.WebflowError" %>
<%@ page import="bmiasia.ebos.webflow.exception.WebflowErrors" %>
<%@ page import="bmiasia.ebos.webflow.WebflowConstants" %>
<%
    // clear timestamp set
    session.setAttribute("_timestamp", new HashSet());

WebflowErrors errors = (WebflowErrors)request.getAttribute(WebflowConstants.ERROR_KEY);
if(errors != null && !errors.empty())
{
        for (Iterator keys = errors.properties(); keys.hasNext();)
        {
            String prop = (String)keys.next();
            for (Iterator it = errors.get(prop); it.hasNext();)
            {
                WebflowError error = (WebflowError)it.next();
                Throwable t = error.getThrowable();
                if (t != null)
                {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    pw.flush();

                    out.println("<pre> Exception stackTrace: "
                            + sw.toString() + "</pre>");
                }
            }
        }
}
%>