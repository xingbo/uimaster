/*
 * Copyright 2000-2003 by BMI Asia, Inc.,
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of BMI Asia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMI Asia.
 */
 
//package
package org.shaolin.javacc.sql.exception;

import org.shaolin.bmdp.exceptions.BaseException;

//imports

/**
 * The OQLException exception signals an oql error is detected.
 */
public class OQLException extends BaseException
{
    /**
     * Constructs an OQLException with an oql string and
     * a detailed message
     * 
     * @param   oql         oql expression string
     * @param   detailMsg   detail error message
     */
    public OQLException(String oql, String detailMsg)
    {
        super(ERR_MSG, new Object[]{oql, detailMsg});
    }
    
    /**
     * Constructs an OQLException with an oql statement, 
     * a detailed message and a nested throwable object
     * 
     * @param   oql         oql expression string
     * @param   detailMsg   detail error message
     * @param   nested      nested throwable object
     */
    public OQLException(String oql, String detailMsg, Throwable nested)
    {
        super(ERR_MSG, nested, new Object[]{oql, detailMsg});
    }
    
    public OQLException(String message,Object[]args)
    {
    	super(message,args);
    }
    
    public OQLException(String aReason, Throwable aThrowable, Object[] args)
    {
        super(aReason, aThrowable, args);
    }
    
    private static final String ERR_MSG = "Invalid oql expression:{0}. {1}";

    private static final long serialVersionUID = -5794169237364852188L;
}
