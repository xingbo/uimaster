package org.shaolin.bmdp.designtime.orm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.bediagram.BEListType;
import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.CEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.DateTimeType;
import org.shaolin.bmdp.datamodel.bediagram.MemberType;
import org.shaolin.bmdp.datamodel.bediagram.TimeType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ClassMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.JoinTableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ListFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.PKType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBDiagram;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ViewType;
import org.shaolin.bmdp.designtime.bediagram.BESourceGenerator;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateMappingGenerator implements IEntityEventListener<TableType, RDBDiagram> {
	
	private static final Logger logger = LoggerFactory.getLogger(BESourceGenerator.class.getName());
	
	private final PumpWriter out = new PumpWriter();
	private Map<String, RDBType> compMap = new HashMap<String, RDBType>();
	private List<TableType> tables = new ArrayList<TableType>();

	private EntityManager entityManager;

	private GeneratorOptions options;
	
	public HibernateMappingGenerator(GeneratorOptions options) {
		this.options = options;
		
		out.setOutputDir(options.getHBMDirectory());
		out.setEncoding(Registry.getInstance().getEncoding());
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void notify(EntityAddedEvent<TableType, RDBDiagram> event) {
		if (event.getDiagram().getDaoPackage().indexOf(options.getBundleName()) == -1) {
			return;
		}
		
		RDBDiagram diagram = event.getDiagram();
		TableType table = event.getEntity();
		ClassMappingType mapping = event.getEntity().getMapping();
		if (mapping == null) {
			return;
		}
		if (mapping.getBusinessEntity() == null) {
			return;
		}
		String beName = mapping.getBusinessEntity().getEntityName();
		BusinessEntityType be = entityManager.getEntity(beName, BusinessEntityType.class);
		if (!be.isNeedPersist()) {
			return;
		}
		
		try {
			File path = new File(options.getHBMDirectory(), table.getEntityName()+ ".hbm.xml");
			out.write("\n!!!!file ");
			out.print(path.getAbsolutePath());
			out.write("\n");
			if (logger.isInfoEnabled()) {
				logger.info("Table's hbm is {}, whose generated file is {}", 
						table.getEntityName(), path.getAbsolutePath());
			}
			
			out.write("<?xml version=\"1.0\"?>\n");
			out.write("<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \n");
			out.write("\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n");
			out.write("<hibernate-mapping>\n");
			
			out.write("  <class name=\"");
			out.print(mapping.getBusinessEntity().getEntityName() + "Impl");
			out.write("\" table=\"");
			String tableName = table.getEntityName();
			tableName = tableName.substring(tableName.lastIndexOf('.') + 1);
			out.print(tableName);
			out.write("\">\n");
			
			List<FieldMappingType> fieldMappings = mapping.getFieldMappings();
			if (table.getPrimaryKeies() != null) {
				List<PKType> pks = table.getPrimaryKeies();
				for (PKType pk: pks) {
					out.write("    <id name=\"");
					out.print(getBEField(pk.getColumnName(), mapping));
					out.write("\" column=\"");
					out.print(pk.getColumnName());
					out.write("\">\n");
					out.write("      <generator class=\"native\" />\n");
	//				out.write("<param name=\"sequence\">");
	//				out.print(table.getSequence().getName());
	//				out.write("</param>\n");
	//				out.write("      </generator>\n");
					out.write("    </id>\n");
					
					// remove PK mapping.
					FieldMappingType matched = null;
					for (FieldMappingType fm: fieldMappings) {
						if (pk.getColumnName().equals(fm.getColumnName())) {
							matched = fm;
						}
					}
					if (matched != null) {
						fieldMappings.remove(matched);
					}
				}
			}
			for (FieldMappingType fm: fieldMappings) {
				if (fm instanceof ListFieldMappingType) {
					ListFieldMappingType listMapping = (ListFieldMappingType)fm;
					/**
					 * &lt;enumeration value="One-to-One"/>
					 * &lt;enumeration value="One-to-Many"/>
					 * &lt;enumeration value="Many-to-Many"/>
					 */
					if ("One-to-One".equals(listMapping.getMappingType())) {
						// A bidirectional one-to-one association on a foreign key is common:
						out.write("    <many-to-one name=\"");
						out.print(listMapping.getBeFieldName());
						out.write("\" column=\"");
						out.print(listMapping.getColumnName());
						out.write("\" unique=\"true\" not-null=\"true\" lazy=\"false\" insert=\"false\" update=\"false\"/>\n");
					} else if ("One-to-Many".equals(listMapping.getMappingType())) {
						// A unidirectional one-to-many association on a foreign key is an 
						// unusual case, and is not recommended.
						// You should instead use a join table for this kind of association.
						// A unidirectional one-to-many association on a join table is the preferred option. 
						// Specifying unique="true", changes the multiplicity from many-to-many to one-to-many.
						String targetBEClass =  BEUtil.getBEImplementClassName(listMapping.getCollectionElement());
						JoinTableType joinTable = getJoinTable(diagram, listMapping.getAssociationName());
						out.write("    <list name=\"");
						out.print(listMapping.getBeFieldName());
						out.write("\" table=\"");
						out.print(joinTable.getName());
						//cascade="all|none|save-update|delete|all-delete-orphan|(6)delete-orphan"
					    //sort="unsorted|natural|comparatorClass"     
						//TODO: cascading decision is difficult here.
						out.write("\" cascade=\"all\" lazy=\"false\">\n");
						out.write("        <key column=\"");
						out.print(joinTable.getTarPKColumn());
						out.write("\"/>\n");
						out.write("        <index column=\"_index\"/>\n");
						out.write("        <many-to-many unique=\"true\" class=\"");
						out.print(targetBEClass);
						out.write("\"/>\n");
						out.write("    </list>\n");
					} else if ("Many-to-Many".equals(listMapping.getMappingType())) {
						String targetBEClass =  BEUtil.getBEImplementClassName(listMapping.getCollectionElement());
						JoinTableType joinTable = getJoinTable(diagram, listMapping.getAssociationName());
						out.write("    <list name=\"");
						out.print(listMapping.getBeFieldName());
						out.write("\" table=\"");
						out.print(joinTable.getName());
						out.write("\" cascade=\"all\" lazy=\"false\">\n");
						out.write("        <key column=\"");
						out.print(joinTable.getTarPKColumn());
						out.write("\"/>\n");
						out.write("        <index column=\"_index\"/>\n");
						out.write("        <many-to-many class=\"");
						out.print(targetBEClass);
						out.write("\"/>\n");
						out.write("    </list>\n");
					}
				} else {
					// 8.1.1. Basic types hibernate-release-4.2.3.Final/
					// documentation/devguide/en-US/html_single/index.html#d5e2292
					out.write("    <property name=\"");
					if (isCEType(be, fm.getBeFieldName())) {
						out.print(fm.getBeFieldName());
						out.write("Int");
					} else if (isCEListType(be, fm.getBeFieldName())) {
						out.print(fm.getBeFieldName());
						out.write("IntValues");
					} else {
						out.print(fm.getBeFieldName());
					}
					getPropertyType(be, fm.getBeFieldName());
					out.write("\" column=\"");
					out.print(fm.getColumnName());
					out.write("\"/>\n");
				}
			}
			
			out.write("  </class>\n");
			
			out.write("</hibernate-mapping>\n");
			out.finish();
		} finally {
			tables.remove(event.getEntity());
		}
	}

	public void notify(EntityUpdatedEvent<TableType, RDBDiagram> event) {
		
	}

	public Class<TableType> getEventType() {
		return TableType.class;
	}
	
	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}
	
	public void notifyAllLoadFinish() {
	}
	
	public boolean isCEType(BusinessEntityType be, String beField) {
		for (MemberType m: be.getMembers()) {
			if (beField.equals(m.getName())) {
				return m.getType() instanceof CEObjRefType;
			}
		}
		
		if (be.getParentObject() != null) {
			if (be.getParentObject() instanceof BEObjRefType) {
				String entityName = ((BEObjRefType)be.getParentObject()).getTargetEntity().getEntityName();
				if (entityName.indexOf('.') == -1) {
					entityName = be.getEntityName().substring(0, be.getEntityName().lastIndexOf('.') + 1) + entityName;
				}
				BusinessEntityType parent = entityManager.getEntity(entityName, BusinessEntityType.class);
				return isCEType(parent, beField);
			}
		}
		return false;
	}
	
	public boolean isCEListType(BusinessEntityType be, String beField) {
		for (MemberType m: be.getMembers()) {
			if (beField.equals(m.getName())) {
				if (m.getType().getClass() == BEListType.class 
						&& ((BEListType)m.getType()).getElementType().getClass() == CEObjRefType.class) {
					return true;
				}
			}
		}
		
		if (be.getParentObject() != null) {
			if (be.getParentObject() instanceof BEObjRefType) {
				String entityName = ((BEObjRefType)be.getParentObject()).getTargetEntity().getEntityName();
				if (entityName.indexOf('.') == -1) {
					entityName = be.getEntityName().substring(0, be.getEntityName().lastIndexOf('.') + 1) + entityName;
				}
				BusinessEntityType parent = entityManager.getEntity(entityName, BusinessEntityType.class);
				return isCEListType(parent, beField);
			}
		}
		
		return false;
	}
	
	
	/**
	 * The name of a Hibernate basic type: 
	 * integer, string, character, date, 
	 * timestamp, float, binary, serializable,
	 * object, blob
	 * 
	 * @param beName
	 * @param beField
	 * @return
	 */
	public void getPropertyType(BusinessEntityType be, String beField) {
		List<MemberType> fields = be.getMembers();
		for (MemberType field: fields) {
			if (field.getName().equals(beField)) {
				if (field.getType() instanceof DateTimeType) {
					out.write("\" type=\"timestamp");;
				} else if (field.getType() instanceof TimeType) {
					out.write("\" type=\"timestamp");
				} else if (field.getType() instanceof CEObjRefType) {
					out.write("\" type=\"integer");
				} 
				return;
			}
		}
		// history attributes
		if ("_version".equals(beField)) {
			out.write("\" type=\"int");
			return;
		} else if ("_starttime".equals(beField)) {
			out.write("\" type=\"timestamp");
			return;
		} else if ("_endtime".equals(beField)) {
			out.write("\" type=\"timestamp");
			return;
		} else if ("_optuserid".equals(beField)) {
			out.write("\" type=\"long");
			return;
		} else if ("_enable".equals(beField)) {
			out.write("\" type=\"boolean");
			return;
		} else if ("x".equals(beField)) {
			out.write("\" type=\"int");
			return;
		} else if ("y".equals(beField)) {
			out.write("\" type=\"int");
			return;
		}
		
		if (be.getParentObject() != null) {
			if (be.getParentObject() instanceof BEObjRefType) {
				String entityName = ((BEObjRefType)be.getParentObject()).getTargetEntity().getEntityName();
				if (entityName.indexOf('.') == -1) {
					entityName = be.getEntityName().substring(0, be.getEntityName().lastIndexOf('.') + 1) + entityName;
				}
				BusinessEntityType parent = entityManager.getEntity(entityName, BusinessEntityType.class);
				getPropertyType(parent, beField);
				return;
			}
		}
		
		throw new IllegalArgumentException("The field '" + beField
				+ "' can't be found in BE entity " + be.getEntityName());
	}
	
	private String getBEField(String columnName, ClassMappingType mapping) {
		List<FieldMappingType> mappings = mapping.getFieldMappings();
		for (FieldMappingType m : mappings) {
			if (columnName.equals(m.getColumnName())) {
				return m.getBeFieldName();
			}
		}
		
		throw new IllegalArgumentException("The be field[" + columnName 
				+ "] can't be found in ClassMapping of BE "
				+ mapping.getBusinessEntity().getEntityName() + ".");
	}
	
	private JoinTableType getJoinTable(RDBDiagram diagram, String associateName) {
		for (JoinTableType table: diagram.getJoinTables()) {
			if (associateName.equals(table.getName())) {
				return table;
			}
		}
		return null;
	}
	
	private TableType getTableByName(String entityName) {
		return (TableType) getRDBByName(entityName);
	}

	private RDBType getRDBByName(String entityName) {
		RDBType rdb = (RDBType) compMap.get(entityName);
		if (rdb != null) {
			return rdb;
		}
		return getRDBByComponent(entityName);
	}

	private RDBType getRDBByComponent(String entityName) {
		try {
			TableType table = entityManager.getEntity(entityName,
					TableType.class);
			compMap.put(table.getEntityName(), table);
			return table;
		} catch (EntityNotFoundException e) {
			ViewType view = entityManager.getEntity(entityName, ViewType.class);
			compMap.put(view.getEntityName(), view);
			return view;
		}
	}

}
