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
package org.shaolin.bmdp.persistence.query.mapping;

//imports
import java.util.List;

public interface IFieldDescriptor
{
    public void setDeclaringClassDescriptor(IClassDescriptor classDesc);
    
    public IClassDescriptor getDeclaringClassDescriptor();

    public void setFieldOwnerDescriptor(IFieldOwnerDescriptor foDesc);
    
    public IFieldOwnerDescriptor getFieldOwnerDescriptor();
    
    public String getPFFieldName();
    
    public String getConfigPath();
    
    public String[] getFieldNames();
    
    public String[] getColumnNames();
    
    public int[] getColumnTypes();
    
    public boolean isRedundant();

    public boolean isUseClobBuffer();

    public void setUseClobBuffer(boolean useClobBuffer);

    public List diffValue(Object newValue, Object oldValue, boolean markedDirty);
    

}
