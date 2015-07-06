/*
 * Copyright 2000-2005 by BMI Asia, Inc.,
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of BMI Asia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMI Asia.
 */

//package
package org.shaolin.bmdp.persistence.query.mapping;

//imports
import java.util.LinkedList;
import java.util.List;

public class JavaToSqlCommand
{
    public JavaToSqlCommand()
    {
    }
    
    public ILobWriter getLobWriter()
    {
    	throw new UnsupportedOperationException("No lob writer specified for this command");
    }

    public void bind(Object value, int columnType)
    {
        values.add(value);
    }
    
    public void bindLong(long l)
    {
        values.add(new Long(l));
    }
    
    public Object[] toArray()
    {
        return values.toArray();
    }
    
    private List values = new LinkedList();

}
