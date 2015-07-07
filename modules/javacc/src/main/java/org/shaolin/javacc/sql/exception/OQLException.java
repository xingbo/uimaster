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
