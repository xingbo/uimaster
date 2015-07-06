<%@ page contentType="text/xml;charset=UTF-8" %>
<%@ page import="org.apache.log4j.Logger, bmiasia.ebos.appbase.util.DataFormatUtil, bmiasia.ebos.common.util.LocaleContext" %>
<%
    String dataType = request.getParameter("datatype");
    String localeConfig = request.getParameter("localeconfig");
    String formatName = request.getParameter("formatname");
    
    if (localeConfig == null || "".equals(localeConfig))
    {
        localeConfig = LocaleContext.getUserLocale();
    }
    
    try
    {
        String formatPattern = 
            DataFormatUtil.getFormatPattern(dataType, localeConfig, formatName);
        out.print(formatPattern);        
    }
    catch (Exception e) 
    {
        Logger.getLogger("/jsp/common/DataFormatService.jsp").error(e.getMessage(), e);
    }
%>