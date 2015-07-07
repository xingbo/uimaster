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
