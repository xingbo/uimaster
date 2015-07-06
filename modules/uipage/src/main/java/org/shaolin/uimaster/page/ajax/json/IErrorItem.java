package org.shaolin.uimaster.page.ajax.json;

public interface IErrorItem extends IDataItem
{
    public final static String ENTITY_NAME = "org.shaolin.uimaster.page.ajax.json.ErrorItem";

    public String getErrorMsgTitle();

    public void setErrorMsgTitle(String errorMsgTitle);

    public String getErrorMsgBody();

    public void setErrorMsgBody(String errorMsgBody);

    public java.lang.String getImage();

    public void setImage(java.lang.String image);

    public java.lang.String getJsSnippet();

    public void setJsSnippet(java.lang.String jsSnippet);

    public java.lang.String getHtml();

    public void setHtml(java.lang.String html);

    public java.lang.String getStyle();

    public void setStyle(java.lang.String style);

    public java.lang.String getExceptionTrace();

    public void setExceptionTrace(java.lang.String exceptionTrace);

}
