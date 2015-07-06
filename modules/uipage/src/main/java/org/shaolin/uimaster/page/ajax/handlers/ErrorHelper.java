package org.shaolin.uimaster.page.ajax.handlers;

import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.ErrorItem;
import org.shaolin.uimaster.page.ajax.json.IErrorItem;


public class ErrorHelper 
{
    public ErrorHelper()
    {
    }
    
    public static IErrorItem createErrorItem(String html)
    {
        IErrorItem errorItem = new ErrorItem();

        errorItem.setHtml(html);
        errorItem.setJsHandler(IJSHandlerCollections.HTML_APPEND_ERROR);
        
        return errorItem;
    }
    
    
    public static IErrorItem createErrorItem(String uiid, 
    		String errorMsgTitle, String errorMsgBody, String exceptionTrace, 
            String image, String jsSnippet, String html)
    {
        IErrorItem errorItem = new ErrorItem();

        errorItem.setUiid(uiid);
        errorItem.setErrorMsgTitle(errorMsgTitle);
        errorItem.setErrorMsgBody(errorMsgBody);
        errorItem.setExceptionTrace(exceptionTrace);
        errorItem.setImage(image);
        errorItem.setJsSnippet(jsSnippet);
        errorItem.setHtml(html);
        errorItem.setStyle(null);
        errorItem.setFrameInfo(null);
        errorItem.setJsHandler(IJSHandlerCollections.HTML_APPEND_ERROR);
        
        return errorItem;
    }
    
    public static IErrorItem createErrorItemForPortlet(String errorMsgTitle, String errorMsgBody)
    {
    	IErrorItem errorItem = new ErrorItem();
    	
    	errorItem.setErrorMsgTitle(errorMsgTitle);
    	errorItem.setErrorMsgBody(errorMsgBody);
    	
    	return errorItem;
    }
    
    public static IErrorItem createErrorItemForPortlet(String errorMsgTitle)
    {
        IErrorItem errorItem = new ErrorItem();
        errorItem.setErrorMsgTitle(errorMsgTitle);
        return errorItem;
    }
    
    public static String createErrorHTMLCodeForPortlet(IErrorItem errorItem)
    {
        StringBuffer htmlFrag = new StringBuffer();
        
        //create the container
        htmlFrag.append("<div ");
        if ( errorItem.getStyle() != null )
        {
            htmlFrag.append("class=\"" + errorItem.getStyle() + "\">");
        }
        else
        {
            htmlFrag.append("class=\"uimaster-state-error\">");
        }

        //append the text area
        htmlFrag.append("<p>");
        htmlFrag.append(errorItem.getErrorMsgTitle());
        htmlFrag.append("</p>");
            
        htmlFrag.append("</div>");
        
        return htmlFrag.toString();
    }
    
}