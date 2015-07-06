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
