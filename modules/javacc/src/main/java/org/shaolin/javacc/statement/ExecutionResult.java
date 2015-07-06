package org.shaolin.javacc.statement;

import java.io.Serializable;

/**
 * The execution result type of the statement script
 * 
 */
public class ExecutionResult implements Serializable
{

    public ExecutionResult()
    {
        
    }
    
    public int getResultCode()
    {
        return resultCode;
    }
    
    public void setResultCode(int code)
    {
        this.resultCode = code;
    }
    
    public Throwable getCauseException()
    {
        return causeException;
    }
    
    public void setCauseException(Throwable cause)
    {
        this.causeException = cause;
    }
    
    public Object getReturnResult()
    {
        return returnResult;
    }
    
    public void setReturnResult(Object result)
    {
        this.returnResult = result;
    }
    
    /*The result of the execution of the statement*/
    private int resultCode = StatementConstants.normalEnding; 
    
    /*The cause of the exception*/
    private Throwable causeException = null;
    
    /*The return value of the script execution*/
    private Object returnResult = null;
    
    /*The serial ID*/
    private static final long serialVersionUID = 1357924680L;

}
