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
import java.util.Map;

public interface ICategoryDescriptor
{
    public String getCategoryName();
    
    public IClassDescriptor getNodeClassDescriptor();
    
    public IClassDescriptor getItemClassDescriptor();
    
    public List getNodePKDescriptorList();
    
    public List getItemPKDescriptorList();

    public List getNodeParentPKDescriptorList();

    public List getItemParentPKDescriptorList();

    public Class getNodeClass();
    
    public Class getItemClass();
    
    public String getNodeTableName();
    
    public String getItemTableName();
    
    public String getNodeItemRelationTableName();
    
    public String getSelectParentFromNodeItemRelationTableSql();
    
    public String getNodeItemRelationIdOIDColumnName();
    public List getNodeItemRelationIdPKDescriptor();
    
    public String getNodeItemRelationParentIdOIDColumnName();
    public List getNodeItemRelationParentIdPKDescriptor();
    
    public String getItemParentOIDFieldName();
    public String[] getItemParentPKFieldNames();
    
    public String getNodeRelationTableName();
    
    public String getSelectParentFromNodeRelationTableSql();

    public String getNodeRelationIdOIDColumnName();
    public List getNodeRelationIdPKDescriptor();
    
    public String getNodeRelationParentIdOIDColumnName();
    public List getNodeRelationParentIdPKDescriptor();
    
    public String getNodeParentOIDFieldName();
    public String[] getNodeParentPKFieldNames();
    
    public Object[] getNodeItemRelationParentIdJavaNullValue();
    public Object[] getItemParentIdJavaNullValue();
    public Object[] getNodeRelationParentIdJavaNullValue();
    public Object[] getNodeParentIdJavaNullValue();
    
    public Map getNodeParentIdNullValue();
    public Map getItemParentIdNullValue();
    
    public String getSelectNodeAncestorSql();
    public String getSelectItemAncestorSql();
    
    public String getSelectRootChildNodesSql();
    public String getSelectRootChildItemsSql();

    public String getSelectRootDescendantNodesSql();
    public String getSelectRootDescendantItemsSql();

    public String getSelectChildNodesSql();
    public String getSelectChildItemsSql();

    public String getSelectDescendantNodesSql();
    public String getSelectDescendantItemsSql();
    
    public String getHasChildrenSql();
    public String getRootHasChildrenSql();
    
    public String getNNHasADRelationSql();
    public String getNIHasADRelationSql();
    public String getRootNHasADRelationSql();
    public String getRootIHasADRelationSql();

    public static final String ___REVISION___ = "$Revision: 1.2 $";
}
