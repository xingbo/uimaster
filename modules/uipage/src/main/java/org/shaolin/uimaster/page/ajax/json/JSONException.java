
package org.shaolin.uimaster.page.ajax.json;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 * 
 * @author JSON.org
 * @version 2008-09-18
 */
public class JSONException extends Exception
{
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     * 
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message)
    {
        super(message);
    }

    public JSONException(Throwable t)
    {
        super(t.getMessage());
        this.cause = t;
    }

    public Throwable getCause()
    {
        return this.cause;
    }
    
    public String toString() 
    {
    	StringBuffer sb = new StringBuffer();
    	if(this.cause != null)
    	{
    		java.io.StringWriter sw = null;
    		java.io.PrintWriter pw = null;
    		try
    		{
    			sw = new java.io.StringWriter();
    			pw = new java.io.PrintWriter(sw);
    			this.cause.printStackTrace(pw);
    			pw.flush();
    			sb.append( sw.toString() );
    		}
    		finally
    		{
    			try{
    				if (pw != null)
    					pw.close();
    			}catch (Exception e){}
    			try{
    				if (sw != null)
    					sw.close();
    			}catch (Exception e){}
    		}
    	}
    	else
    	{
    		sb.append( super.toString() );
    	}
        return sb.toString();
    }
    
}
