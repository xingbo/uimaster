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

import org.shaolin.uimaster.page.exception.AjaxException;

//imports

/**
 * Exception for Ajax
 *
 */
public class AjaxHandlerException extends AjaxException
{
    /**
     * Constructs a AjaxHandlerException with a given exception reason
     *
     * @param aReason
     */
    public AjaxHandlerException(String aReason)
    {
        super(aReason);
    }
    
    public AjaxHandlerException(String aReason,Throwable ex)
    {
        super(aReason,ex);
    }

    /**
     * Constructs a AjaxHandlerException with a given exception reason, an argument
     * array.
     *
     * @param aReason
     * @param args
     */
    public AjaxHandlerException(String aReason, Object[] args)
    {
        super(aReason, args);
    }

    private static final long serialVersionUID = 6637940630141909726L;
}

