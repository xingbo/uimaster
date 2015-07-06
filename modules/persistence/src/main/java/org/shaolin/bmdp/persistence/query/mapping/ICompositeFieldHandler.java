/*
 * $Id: ICompositeFieldHandler.java,v 1.2 2007/11/14 02:09:27 phileas Exp $
 * Copyright 2000-2003 by BraveMinds, Inc.,
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of BraveMinds, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BraveMinds.
 */

// package
package org.shaolin.bmdp.persistence.query.mapping;


// imports

/**
 * Specifies how a particular object type will 
 * handle its persistence.
 * 
 * @author Tom Joseph
 * @see bmiasia.ebos.ormapper.handlers.FieldHandlers
 */
public  interface   ICompositeFieldHandler   
{
    /**
     * Read the object value from the result set.
     * 
     * @param aSet          the result set containing the value.
     * @param aColumns      the columns holding the value interested.
     * @param aField        composite field to read
     * @return the value extracted.
     */
    public Object fromCache(Object[] values);
    
    public Object[] toCache(Object value);


    public static final String ___REVISION___ = "$Revision: 1.2 $";
} //ICompositeFieldHandler
