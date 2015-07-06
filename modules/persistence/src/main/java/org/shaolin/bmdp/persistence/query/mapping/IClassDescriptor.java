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
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface IClassDescriptor extends IFieldOwnerDescriptor
{
    public String getEntityName();
    
    public Class getJavaClass();
    
    public boolean isHistoryEntity();
    
    public String getTableName();
    
    public String getHistoryTableName();
    
    public String getViewName();
    
    public Map getCustomerRDB();
    
    public boolean isCacheEnabled();
    
    public IClassDescriptor getSuperClassDescriptor();
    
    public Iterator listFieldDescriptor();
    
    public IFieldDescriptor getFieldDescriptor(String fieldName);

    public Iterator listDeclaredFieldDescriptor();
    
    public IFieldDescriptor getDeclaredFieldDescriptor(String fieldName);
    
    //include redundant field descriptors
    public Iterator listAllFieldDescriptor();
    
    public Iterator listAllDeclaredFieldDescriptor();
    
    public Map getRedundantFieldConfig();
    
    public List getRedundantFieldConfigForField(String fieldName);
    
    public Field getDeclaredField(String fieldName);
    
    public Field getField(String fieldName);
    
    public String[] getPKFieldNames();
    
    public int[] getPrimaryKeyColumnTypes();
    
    public IFieldDescriptor[] getPrimaryKeyFieldDescriptors();
    
    public String getInsertSql();
    
    public String getDeleteSql();
    
    public String getSelectSql();
    
    public List getSelectList(String alias);

    public String getLockSql();
    
    public String getSelectReferenceSql();
    
    public String getSelectMaxVersionSql();    
    
    public String getSelectTimestampsSql();
    
    public String getSelectPrimaryKeySql();
    
    public String getSelectHistoryReferenceSql();
    
    public String getSelectHistorySql();
    
    public String getInsertHistorySql();
    
    public String getSelectMaxVersionSql2();
    
    public String getSelectHistoryReferenceWithTimestampSql();

    public String getSelectHistoryReferenceWithTimestampSql2();
    
    public boolean isBeExtension();

    public static final String ___REVISION___ = "$Revision: 1.19 $";
}
