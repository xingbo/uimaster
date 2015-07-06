package org.shaolin.uimaster.page.exception;

import org.shaolin.bmdp.exceptions.BaseException;

public class FormatException extends BaseException
{
    public FormatException(String msg)
    {
        super(msg);   
    }
    
    public FormatException(String msg,Object[]args)
    {
    	super(msg,args);
    }
    
    public FormatException(String msg, Throwable e)
    {
        super(msg, e);   
    }
    
    public FormatException(String msg, Throwable t, Object[] args)
    {
        super(msg, t, args);
    }
}