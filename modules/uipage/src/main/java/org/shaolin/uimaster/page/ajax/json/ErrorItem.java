
package org.shaolin.uimaster.page.ajax.json;

public class ErrorItem implements IErrorItem
{
    private static final long serialVersionUID = 0x680C426CE87CD34FL;

    protected java.lang.String uiid;
    
    protected String errorMsgTitle;

    protected String errorMsgBody;

    protected String image;

    protected String jsSnippet;

    protected String html;

    protected String style;

    protected String exceptionTrace;
    
    protected java.lang.String jsHandler;
    
    protected java.lang.String frameInfo;

    public ErrorItem()
    {
    }
    
    public void setUiid(java.lang.String uiid) {
		this.uiid = uiid;
	}
	
	public void setJsHandler(java.lang.String jsHandler) {
		this.jsHandler = jsHandler;
	}
	
	public void setFrameInfo(java.lang.String frameInfo) {
		this.frameInfo = frameInfo;
	}
	
    public String getErrorMsgTitle()
    {
        return errorMsgTitle;
    }

    public void setErrorMsgTitle(String errorMsgTitle)
    {
        this.errorMsgTitle = errorMsgTitle;
    }

    public String getErrorMsgBody()
    {
        return errorMsgBody;
    }

    public void setErrorMsgBody(String errorMsgBody)
    {
        this.errorMsgBody = errorMsgBody;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getJsSnippet()
    {
        return jsSnippet;
    }

    public void setJsSnippet(String jsSnippet)
    {
        this.jsSnippet = jsSnippet;
    }

    public String getHtml()
    {
        return html;
    }

    public void setHtml(String html)
    {
        this.html = html;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getExceptionTrace()
    {
        return exceptionTrace;
    }

    public void setExceptionTrace(String exceptionTrace)
    {
        this.exceptionTrace = exceptionTrace;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(1024);

        sb.append("ErrorDataItem");
        sb.append(" : ");

        sb.append("uiid");
        sb.append("=");
        sb.append(uiid);
        sb.append(", ");

        sb.append("errorMsgTitle");
        sb.append("=");
        sb.append(errorMsgTitle);
        sb.append(", ");

        sb.append("errorMsgBody");
        sb.append("=");
        sb.append(errorMsgBody);
        sb.append(", ");

        sb.append("html");
        sb.append("=");
        sb.append(html);

        sb.append("image");
        sb.append("=");
        sb.append(image);
        sb.append(", ");

        sb.append("jsHandler");
        sb.append("=");
        sb.append(jsHandler);

        sb.append("style");
        sb.append("=");
        sb.append(style);
        sb.append(", ");

        sb.append("frameInfo");
        sb.append("=");
        sb.append(frameInfo);
        sb.append(", ");

        sb.append("exceptionTrace");
        sb.append("=");
        sb.append(exceptionTrace);
        sb.append(", ");

        return sb.toString();
    }

	@Override
	public String getUiid() {
		return this.uiid;
	}

	@Override
	public String getParent() {
		return null;
	}

	@Override
	public String getSibling() {
		return null;
	}

	@Override
	public String getJsHandler() {
		return this.jsHandler;
	}

	@Override
	public String getData() {
		return null;
	}

	@Override
	public String getJs() {
		return null;
	}

	@Override
	public String getFrameInfo() {
		return this.frameInfo;
	}

	@Override
	public void setParent(String parent) {
		
	}

	@Override
	public void setSibling(String sibling) {
		
	}

	@Override
	public void setData(String data) {
		
	}

	@Override
	public void setJs(String js) {
		
	}

	@Override
	public void addItem(String name, String value) {
		
	}

	@Override
	public String getItem(String name) {
		return null;
	}

	@Override
	public String getItems() {
		return null;
	}

	@Override
	public void setLayout(boolean isLayout) {
		
	}

	@Override
	public boolean isLayout() {
		return false;
	}

}
