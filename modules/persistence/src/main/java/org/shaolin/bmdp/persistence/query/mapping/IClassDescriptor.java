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
