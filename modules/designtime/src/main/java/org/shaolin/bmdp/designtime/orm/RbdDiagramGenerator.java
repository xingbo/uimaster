package org.shaolin.bmdp.designtime.orm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.bediagram.BECollectionType;
import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BESetType;
import org.shaolin.bmdp.datamodel.bediagram.BinaryType;
import org.shaolin.bmdp.datamodel.bediagram.BooleanType;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.CEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.DateTimeType;
import org.shaolin.bmdp.datamodel.bediagram.DoubleType;
import org.shaolin.bmdp.datamodel.bediagram.FileType;
import org.shaolin.bmdp.datamodel.bediagram.IntType;
import org.shaolin.bmdp.datamodel.bediagram.JavaObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.LongType;
import org.shaolin.bmdp.datamodel.bediagram.MemberType;
import org.shaolin.bmdp.datamodel.bediagram.StringType;
import org.shaolin.bmdp.datamodel.bediagram.TimeType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ClassMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.JoinTableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ListFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ObjectFactory;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBDiagram;
import org.shaolin.bmdp.datamodel.rdbdiagram.SQLFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchQueryType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchResultMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SimpleFieldValueType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RbdDiagramGenerator implements
		IEntityEventListener<BusinessEntityType, BEDiagram> {

	private static final Logger logger = LoggerFactory
			.getLogger(RbdDiagramGenerator.class.getName());

	private EntityManager entityManager;

	private GeneratorOptions options;

	public RbdDiagramGenerator(GeneratorOptions options) {
		this.options = options;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void notify(EntityAddedEvent<BusinessEntityType, BEDiagram> event) {

	}

	public void notify(
			EntityUpdatedEvent<BusinessEntityType, BEDiagram> event) {

	}

	public Class<BusinessEntityType> getEventType() {
		return BusinessEntityType.class;
	}

	@Override
	public void notifyLoadFinish(DiagramType d) {
		BEDiagram bediagram = (BEDiagram) d;
		if (bediagram.getBePackage().indexOf(options.getBundleName()) == -1) {
			return;
		}
		
		List<BusinessEntityType> bes = bediagram.getBeEntities();

		String bePackage = bediagram.getBePackage();
		RDBDiagram rdbdiagram = new RDBDiagram();
		rdbdiagram.setDaoPackage(bePackage.replace("be", "dao"));
		rdbdiagram.setDiagramPackage(bediagram.getDiagramPackage());
		rdbdiagram.setName(bediagram.getName());

		File entityDir = options.getEntitiesDirectory();
		File formDir = new File(entityDir, rdbdiagram.getDiagramPackage().replace('.', '/'));
		if (!formDir.exists()) {
			formDir.mkdirs();
		} 
		File formFile = new File(formDir, bediagram.getName() + ".rdbdiagram");
		if (formFile.exists()) {
			// check the merger.
			if (true) {
				return;
			}
			try {
				ObjectFactory rdbObjFactory = new ObjectFactory();
				RDBDiagram rdbObject = EntityUtil.unmarshaller(RDBDiagram.class, new FileReader(formFile));
				List<TableType> tables = rdbObject.getTables();
				for (BusinessEntityType be : bes) {
					if (!be.isNeedPersist()) {
						continue;
					}
					
					boolean nomatched = true;
					for (TableType t: tables) {
						if (t.getMapping().getBusinessEntity().getEntityName().endsWith(be.getEntityName())) {
							nomatched = false;
						}
					}
					if (nomatched) {
						createTable(bediagram, be, rdbObject, rdbObjFactory);
						try {
							// FIXME: marshalling twice entity occurrs some issue for all operator expressions.
							EntityUtil.marshaller(rdbObject, new FileWriter(formFile));
							if (logger.isInfoEnabled()) {
								logger.info("updated RDB diagram {}, whose generated file is {}", 
										rdbdiagram.getName(), formFile.getAbsolutePath());
							}
							entityManager.appendEntity("rdbdiagram", formFile);
						} catch (JAXBException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		} else {
			ObjectFactory rdbObjFactory = new ObjectFactory();
			for (BusinessEntityType be : bes) {
				if (!be.isNeedPersist()) {
					continue;
				}
				createTable(bediagram, be, rdbdiagram, rdbObjFactory);
			}
			
			try {
				EntityUtil.marshaller(rdbdiagram, new FileWriter(formFile));
				if (logger.isInfoEnabled()) {
					logger.info("RDB diagram {}, whose generated file is {}", 
							rdbdiagram.getName(), formFile.getAbsolutePath());
				}
				entityManager.appendEntity("rdbdiagram", formFile);
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createTable(BEDiagram bediagram, BusinessEntityType be, RDBDiagram rdbdiagram, ObjectFactory rdbObjFactory) {
		TableType table = rdbObjFactory.createTableType();
		if (be.getEntityName().lastIndexOf('.') != -1) {
			table.setEntityName(be.getEntityName().substring(
					be.getEntityName().lastIndexOf('.') + 1).toUpperCase());
		} else {
			table.setEntityName(be.getEntityName().toUpperCase());
		}
		
		ClassMappingType mapping = rdbObjFactory.createClassMappingType();
		mapping.setBusinessEntity(new TargetEntityType());
		if (be.getEntityName().lastIndexOf('.') != -1) {
			mapping.getBusinessEntity().setEntityName(be.getEntityName());
		} else {
			mapping.getBusinessEntity().setEntityName(
				bediagram.getBePackage() + "." + be.getEntityName());
		}
		
		table.setMapping(mapping);
		rdbdiagram.getTables().add(table);
		
		// Gen associations
		List<MemberType> members = be.getMembers();
		if (be.getParentObject() != null) {
			BEObjRefType ref = (BEObjRefType)be.getParentObject();
			String parentBe = ref.getTargetEntity().getEntityName();
			if (parentBe == null || parentBe.isEmpty()) {
				logger.warn("the parent BE value is null from BE entity: " + be.getEntityName());
				return;
			}
			if (parentBe.indexOf(".") == -1) {
				parentBe = bediagram.getBePackage() + "." + parentBe;
			}
			BusinessEntityType prentObject = this.entityManager.getEntity(parentBe, BusinessEntityType.class);
			members = prentObject.getMembers();
			members.addAll(be.getMembers());
		}

		for (MemberType beField : members) {
			if (beField.getType() instanceof BECollectionType) {
				if (beField.getType() instanceof BESetType) {
					BEObjRefType refBE = (BEObjRefType) ((BESetType) beField
							.getType()).getElementType();
					String entityName = "";
					if (refBE != null && refBE.getTargetEntity() != null) {
						entityName = refBE.getTargetEntity()
								.getEntityName();
					}

					TargetEntityType tarEntity = new TargetEntityType();
					tarEntity.setEntityName(entityName);
					
					BusinessEntityType tarBe = getBE(bediagram.getBeEntities(), refBE
							.getTargetEntity().getEntityName());
					// suppose the first element is the primary column in BE entity.
					// it can be changed manually.
					String fkName = tarBe.getMembers().get(0).getName();
					String tarTable = tarBe.getEntityName().toUpperCase();
					if (tarTable.lastIndexOf('.') != -1) {
						tarTable = tarTable.substring(tarTable.lastIndexOf('.') + 1);
					}
					JoinTableType joinTable = rdbObjFactory.createJoinTableType();
					joinTable.setName(table.getEntityName() + tarTable + "_JT");
					joinTable.setSrcPKColumn(beField.getName().toUpperCase());
					joinTable.setSrcTable(table.getEntityName());
					joinTable.setTarPKColumn(fkName);
					joinTable.setTarTable(tarTable);
					
					rdbdiagram.getJoinTables().add(joinTable);
					
					ListFieldMappingType mt = rdbObjFactory
							.createListFieldMappingType();
					mt.setBeFieldName(beField.getName());
					mt.setCollectionElement(be.getEntityName());
					mt.setAssociationName(joinTable.getName());
					mt.setMappingType("One-to-Many");
					mapping.getFieldMappings().add(mt);
				} else {
					// skip ListType and MapType.
					continue; //unsupported type.
				}
			} else if (beField.getType() instanceof JavaObjRefType) {
				continue; //unsupported type.
			} else { 
				ColumnType column = rdbObjFactory.createColumnType();
				table.getColumns().add(column);

				if (beField.getType() instanceof BEObjRefType) {
					column.setName(beField.getName().toUpperCase() + "ID");
					column.setType("BIGINT");
				} else {
					column.setName(beField.getName().toUpperCase());
					column.setType(getColumnType(beField.getType().getClass()));
				}
				column.setIsNull(true);
				if (beField.isPk()) {
					column.setAutoIncrement(true);
					column.setIsNull(false);
				}
				if (beField.getType() instanceof BEObjRefType) {
					// one to one mapping
					BEObjRefType refBE = ((BEObjRefType) beField.getType());
					if (refBE.getTargetEntity() == null) {
						logger.warn("BE reference is not specified!"
								+ be.getEntityName() + ":" + beField.getName());
						break;
					}
					BusinessEntityType tarBe = getBE(bediagram.getBeEntities(), refBE
							.getTargetEntity().getEntityName());
					String tarTable = tarBe.getEntityName().toUpperCase();
					if (tarTable.lastIndexOf('.') != -1) {
						tarTable = tarTable.substring(tarTable.lastIndexOf('.') + 1);
					}
					// suppose the first element is the primary column in BE entity.
					// it can be changed manually.
					String fkName = tarBe.getMembers().get(0).getName();
					// update column info from the referred entity.
					column.setName(fkName);
					column.setType("BIGINT");
					column.setLength("38");// default length of BE primary key
					column.setIsNull(true);
					
					/** no need.
					JoinTableType joinTable = rdbObjFactory.createJoinTableType();
					joinTable.setName(table.getEntityName() + tarTable + "_JT");
					joinTable.setSrcPKColumn(column.getName().toUpperCase());
					joinTable.setSrcTable(table.getEntityName());
					joinTable.setTarPKColumn(fkName);
					joinTable.setTarTable(tarTable);
					rdbdiagram.getJoinTables().add(joinTable);
					*/

					ListFieldMappingType mt = rdbObjFactory
							.createListFieldMappingType();
					mt.setBeFieldName(beField.getName());
					mt.setMappingType("One-to-One");
					mt.setAssociationName(table.getEntityName() + tarTable);
					mt.setColumnName(column.getName());
					
					mapping.getFieldMappings().add(mt);
				}else if (beField.getType() instanceof CEObjRefType) {
					FieldMappingType mt = rdbObjFactory
							.createFieldMappingType();
					mt.setBeFieldName(beField.getName());
					mt.setColumnName(column.getName());

					// the max length of CE value limited in 2.
					column.setLength("2");
					mapping.getFieldMappings().add(mt);
				} else {
					FieldMappingType mt = rdbObjFactory
							.createFieldMappingType();
					mt.setBeFieldName(beField.getName());
					mt.setColumnName(column.getName());

					mapping.getFieldMappings().add(mt);
				}
			}
		}

		if (be.isNeedPersist()) {
			ColumnType version = rdbObjFactory.createColumnType();
			version.setName("_enable");
			version.setType("INTEGER");
			version.setLength("2");
			version.setDefault("1");
			table.getColumns().add(version);
			FieldMappingType versionMP = rdbObjFactory
					.createFieldMappingType();
			versionMP.setBeFieldName("_enable");
			versionMP.setColumnName("_enable");
			mapping.getFieldMappings().add(versionMP);
		}
		if (be.isNeedHistory()) {
			ColumnType version = rdbObjFactory.createColumnType();
			version.setName("_version");
			version.setType("INTEGER");
			version.setLength("2");
			version.setDefault("0");
			table.getColumns().add(version);

			ColumnType starttime = rdbObjFactory.createColumnType();
			starttime.setName("_starttime");
			starttime.setType("TIMESTAMP");
			table.getColumns().add(starttime);

			ColumnType endtime = rdbObjFactory.createColumnType();
			endtime.setName("_endtime");
			endtime.setType("TIMESTAMP");
			table.getColumns().add(endtime);

			ColumnType optUserId = rdbObjFactory.createColumnType();
			optUserId.setName("_optuserid");
			optUserId.setType("BIGINT");
			optUserId.setLength("38");
			optUserId.setDefault("0");
			table.getColumns().add(optUserId);

			FieldMappingType versionMP = rdbObjFactory
					.createFieldMappingType();
			versionMP.setBeFieldName("_version");
			versionMP.setColumnName("_version");
			mapping.getFieldMappings().add(versionMP);

			FieldMappingType starttimeMP = rdbObjFactory
					.createFieldMappingType();
			starttimeMP.setBeFieldName("_starttime");
			starttimeMP.setColumnName("_starttime");
			mapping.getFieldMappings().add(starttimeMP);

			FieldMappingType endtimeMP = rdbObjFactory
					.createFieldMappingType();
			endtimeMP.setBeFieldName("_endtime");
			endtimeMP.setColumnName("_endtime");
			mapping.getFieldMappings().add(endtimeMP);

			FieldMappingType userIdMP = rdbObjFactory
					.createFieldMappingType();
			userIdMP.setBeFieldName("_optuserid");
			userIdMP.setColumnName("_optuserid");
			mapping.getFieldMappings().add(userIdMP);
		}
		
		SearchQueryType searchQuery = new SearchQueryType();
		String suffix = be.getEntityName();
		if (suffix.lastIndexOf(".") != -1) {
			suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
		}
		searchQuery.setQueryName("search" + suffix);
		
		VariableType outVar = new VariableType();
		outVar.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		outVar.setName("outObject");
		outVar.setType(new TargetEntityType());
		outVar.getType().setEntityName(be.getEntityName());
		searchQuery.setSearchResult(outVar);
		
		SearchQueryType.FromData from = new SearchQueryType.FromData();
		from.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		from.setName("inObject");
		from.setType(new TargetEntityType());
		from.getType().setEntityName(be.getEntityName());
		searchQuery.getFromDatas().add(from);
		
		searchQuery.setSearchResultMapping(new SearchResultMappingType());
		SQLFieldMappingType sqlFieldMapping = new SQLFieldMappingType();
		sqlFieldMapping.setToDataFieldPath("outObject");
		SimpleFieldValueType sfvalue = new SimpleFieldValueType();
		sfvalue.setValueFieldPath("inObject");
		sqlFieldMapping.setValue(sfvalue);
		searchQuery.getSearchResultMapping().getFieldMappings().add(sqlFieldMapping);
		
		SearchConditionMappingType conditionMappings = new SearchConditionMappingType();
		conditionMappings.setName("Search");
		VariableType outVar1 = new VariableType();
		outVar1.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		outVar1.setName("scObject");
		outVar1.setType(new TargetEntityType());
		outVar1.getType().setEntityName(be.getEntityName());
		conditionMappings.getSearchConditionDatas().add(outVar1);
		//TODO: add mapping fields.
		searchQuery.setSearchConditionMapping(conditionMappings);
		
		rdbdiagram.getQueries().add(searchQuery);
		
	}
	
	public void notifyAllLoadFinish() {

	}

	private BusinessEntityType getBE(List<BusinessEntityType> bes,
			String entityName) {
		for (BusinessEntityType be : bes) {
			if (entityName.equals(be.getEntityName())) {
				return be;
			}
		}
		return entityManager.getEntity(entityName, BusinessEntityType.class);
	}

	/**
	 * default mapping
	 */
	public final static Map<Class<?>, String> TYPES = new HashMap<Class<?>, String>();

	static {
		TYPES.put(IntType.class, "INTEGER");
		TYPES.put(LongType.class, "BIGINT");
		TYPES.put(StringType.class, "VARCHAR");
		TYPES.put(BinaryType.class, "BLOB");
		TYPES.put(BooleanType.class, "BIT");
		TYPES.put(DoubleType.class, "DECIMAL");
		TYPES.put(DateTimeType.class, "DATETIME");
		TYPES.put(TimeType.class, "TIMESTAMP");
		TYPES.put(FileType.class, "BLOB");
		TYPES.put(CEObjRefType.class, "INTEGER");
		TYPES.put(BEObjRefType.class, "BIGINT");
	}

	public static String getColumnType(Class<?> beFieldType) {
		if (TYPES.containsKey(beFieldType)) {
			return TYPES.get(beFieldType);
		}
		// by default.
		return "VARCHAR";
	}

}
