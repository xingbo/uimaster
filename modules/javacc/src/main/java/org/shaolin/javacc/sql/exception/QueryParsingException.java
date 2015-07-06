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

import org.shaolin.bmdp.exceptions.I18NRuntimeException;

/**
 * search query parse exception
 * 
 */
public class QueryParsingException extends I18NRuntimeException {
	public QueryParsingException(String msg) {
		super(msg);
	}

	public QueryParsingException(String msg, Object... args) {
		super(msg, args);
	}
	
	public QueryParsingException(String msg, Throwable aThrowable, Object... args) {
		super(msg, aThrowable, args);
	}

}
