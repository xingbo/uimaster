/*
 * Copyright 2000-2010 by BMI Asia, Inc.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of BMI Asia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMI Asia.
 */
package org.shaolin.uimaster.page.ajax.handlers;

import org.shaolin.uimaster.page.AjaxContext;

public interface IAjaxHandler {

	/**
	 * Trigger an ajax handler.
	 * 
	 * @param context
	 * @return must be a JSON string
	 * @throws AjaxHandlerException
	 */
	public String trigger(AjaxContext context) throws AjaxHandlerException;
	
}
