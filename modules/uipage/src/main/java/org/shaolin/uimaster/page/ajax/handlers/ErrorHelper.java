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