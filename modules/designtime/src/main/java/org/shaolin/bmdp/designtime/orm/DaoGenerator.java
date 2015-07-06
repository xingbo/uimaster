package org.shaolin.bmdp.designtime.orm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ClassMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.JoinTableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBDiagram;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchQueryType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ViewType;
import org.shaolin.bmdp.designtime.orm.query.SearchQeuryContext;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.persistence.PersistentUtil;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoGenerator implements IEntityEventListener<TableType, RDBDiagram> {
	
	private static final Logger logger = LoggerFactory.getLogger(DaoGenerator.class.getName());
	
	private final PumpWriter out = new PumpWriter();
	private Map<String, RDBType> compMap = new HashMap<String, RDBType>();
	
	private EntityManager entityManager;

	private GeneratorOptions options;
	
	public DaoGenerator(GeneratorOptions options) {
		this.options = options;
		
		out.setOutputDir(options.getSqlDir());
		out.setEncoding(Registry.getInstance().getEncoding());
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void notify(EntityAddedEvent<TableType, RDBDiagram> event) {
		if (event.getDiagram().getDaoPackage().indexOf(options.getBundleName()) == -1) {
			return;
		}
		
		TableType entity = event.getEntity();
		ClassMappingType mapping = entity.getMapping();
		if (mapping != null) {
			TargetEntityType businessEntity = mapping.getBusinessEntity();
			if (businessEntity == null) {
				throw new IllegalStateException("BE entity does not "
						+ "define the class mapping relatioship of table " 
						+ entity.getEntityName());
			}
			PersistentUtil.addClassMapping(businessEntity.getEntityName(),
					entity.getEntityName());
		}
	}

	public void notify(EntityUpdatedEvent<TableType, RDBDiagram> event) {
		
	}

	public Class<TableType> getEventType() {
		return TableType.class;
	}
	
	@Override
	public void notifyLoadFinish(DiagramType d) {
		RDBDiagram diagram = (RDBDiagram)d;
		if (diagram.getTables().size() == 0) {
			return;
		}
		if (diagram.getDaoPackage().indexOf(options.getBundleName()) == -1) {
			return;
		}
		
		String realPackage = diagram.getDaoPackage().replace('.', File.separatorChar);
		String javaName = diagram.getName();
		File path = new File(options.getSrcDir() + "\\" + realPackage, 
				javaName + ".java");
		out.write("\n!!!!file ");
		out.print(path.getAbsolutePath());
		out.write("\n");
		if (logger.isInfoEnabled()) {
			logger.info("Dao class is {}, whose generated file is {}", 
					javaName, path.getAbsolutePath());
		}
		
		out.write("package ");
		out.write(diagram.getDaoPackage());
		out.write(";\n\nimport java.util.List;\n");
		out.write("import java.util.ArrayList;\n\n");
		
		out.write("import org.hibernate.Criteria;\n");
		out.write("import org.hibernate.Session;\n");
		out.write("import org.hibernate.criterion.Criterion;\n");
		out.write("import org.hibernate.criterion.MatchMode;\n");
		out.write("import org.hibernate.criterion.Restrictions;\n\n");
		out.write("import org.hibernate.criterion.Order;\n\n");
		
		out.write("import org.shaolin.bmdp.persistence.BEEntityDaoObject;\n");
		out.write("import org.shaolin.bmdp.persistence.HibernateUtil;\n");
		out.write("import org.shaolin.bmdp.persistence.query.operator.Operator;\n");
		
		for (TableType table : diagram.getTables()) {
			ClassMappingType mapping = table.getMapping();
			if (mapping == null) {
				continue;
			}
			if (mapping.getBusinessEntity() == null) {
				continue;
			}
			String beEntityName = mapping.getBusinessEntity().getEntityName();
			BusinessEntityType be = entityManager.getEntity(beEntityName, BusinessEntityType.class);
			if (!be.isNeedHistory()) {
				continue;
			}
			
			out.write("\nimport ");
			String beInterfaceName = BEUtil.getBEInterfaceClassName(beEntityName);
			out.write(beInterfaceName);
			out.write(";\nimport ");
			String beImplName = BEUtil.getBEImplementClassName(beEntityName);
			out.write(beImplName);
			out.write(";");
		}
		out.write("\n");
		out.write("/**\n");
		out.write(" * This code is generated automatically, any change will be replaced after rebuild.\n");
		out.write(" */\n");
		out.write("public class ");
		out.write(javaName);
		out.write(" extends BEEntityDaoObject {\n\n");
		out.write("    public static final ");
		out.print(javaName);
		out.write(" INSTANCE = new ");
		out.print(javaName);
		out.write("();\n\n");
		out.write("    private ");
		out.write(javaName);
		out.write("() {\n");
		/**
		for (TableType table : diagram.getTables()) {
			out.write("        addResource(\"hbm/");
			out.write(table.getEntityName());
			out.write(".hbm.xml\");\n");
		}
		*/
		out.write("    }\n\n");
		
		for (TableType table : diagram.getTables()) {
			ClassMappingType mapping = table.getMapping();
			if (mapping == null) {
				continue;
			}
			if (mapping.getBusinessEntity() == null) {
				continue;
			}
			String beEntityName = mapping.getBusinessEntity().getEntityName();
			BusinessEntityType be = entityManager.getEntity(beEntityName, BusinessEntityType.class);
			if (!be.isNeedHistory()) {
				continue;
			}
			
			String beInterfaceName = BEUtil.getBEInterfaceOnlyName(be.getEntityName());
			String beName = BEUtil.getBEOnlyName(be.getEntityName());
			String beImplName = BEUtil.getBEImplementOnlyName(be.getEntityName());
			/** no need now, saving the generation code lines.
			out.write("    public void createFile("+beInterfaceName+" file) {\n");
			out.write("        create(file);\n");
			out.write("    }\n\n");
			out.write("    public void deleteFile("+beInterfaceName+" file) {\n");
			out.write("        delete(file);\n");
			out.write("    }\n\n");
			out.write("    public void updateFile("+beInterfaceName+" file) {\n");
			out.write("        update(file);\n");
			out.write("    }\n\n");
			 */
			out.write("    public List<"+beInterfaceName+"> list"+beInterfaceName+"s(int offset, int count) {\n");
			out.write("        return list(offset, count, "+beInterfaceName+".class, "+beImplName+".class);\n");
			out.write("    }\n\n");
			out.write("    public long list"+beInterfaceName+"Count() {\n");
			out.write("        return count("+beInterfaceName+".class);\n");
			out.write("    }\n\n");
		}
		
		ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(options.getBEMemoryClassLoader());
			for (SearchQueryType query: diagram.getQueries()) {
				try {
					SearchQeuryContext context = new SearchQeuryContext(query);
					// data result
					out.write("    public List<" + context.getReturnType() + "> ");
					out.write(query.getQueryName());
					out.write("(");
					out.write(context.getDefaultInputParams());
					out.write(",\n           List<Order> orders, int offset, int count) {\n");
				
					// add head.
					out.write("        Session session = HibernateUtil.getSessionFactory().getCurrentSession();\n");
					out.write("        session.beginTransaction();\n");
					out.write("        try {\n");
					
					// head criteria
					out.print(context.getFirstCriteriaData());
					// join criteria
					out.print(context.getJoinCriteriaData());
					// add order by.
					out.print(context.getOrderByData());
					out.write("\n");
					// add condition
					out.print(context.getConditions());
					
					out.write("\n            List result = this._list(offset, count, ");
					out.print(context.getFirstCriteriaName());
					out.write(");\n");
					if (query.getPostSearch() != null && query.getPostSearch().getExpressionString() != null) {
						out.write("            ");
						out.write(query.getPostSearch().getExpressionString());
						out.write("\n            return result;\n");
					} else {
						out.write("            return result;\n");
					}
					
					out.write("        } finally {\n");
					out.write("            session.getTransaction().commit();\n");
					out.write("        }\n");
					out.write("    }\n\n");
					
					// query result with out getting session.
					out.write("    public List<" + context.getReturnType() + "> ");
					out.write(query.getQueryName());
					out.write("(");
					out.write(context.getDefaultInputParams());
					out.write(",\n           Session session, List<Order> orders, int offset, int count) {\n");	
					out.write("        try {\n");
					
					// head criteria
					out.print(context.getFirstCriteriaData());
					// join criteria
					out.print(context.getJoinCriteriaData());
					// add order by.
					out.print(context.getOrderByData());
					out.write("\n");
					// add condition
					out.print(context.getConditions());
					
					out.write("\n            List result = this._list(offset, count, ");
					out.print(context.getFirstCriteriaName());
					out.write(");\n");
					if (query.getPostSearch() != null && query.getPostSearch().getExpressionString() != null) {
						out.write("            ");
						out.write(query.getPostSearch().getExpressionString());
						out.write("\n            return result;\n");
					} else {
						out.write("            return result;\n");
					}
					
					out.write("        } finally {\n");
					out.write("        }\n");
					out.write("    }\n\n");
					
					// count result
					out.write("    public long ");
					out.write(query.getQueryName());
					out.write("Count(");
					out.write(context.getDefaultInputParams());
					out.write(") {\n");
					
					// add head.
					out.write("        Session session = HibernateUtil.getSessionFactory().getCurrentSession();\n");
					out.write("        session.beginTransaction();\n");
					out.write("        try {\n");
					
					// head criteria
					out.print(context.getFirstCriteriaData());
					// join criteria
					out.print(context.getJoinCriteriaData());
					out.write("\n");
					// add condition
					out.print(context.getConditions());
					
					out.write("\n            return this._count(");
					out.print(context.getFirstCriteriaName());
					out.write(");\n");
					out.write("        } finally {\n");
					out.write("            session.getTransaction().commit();\n");
					out.write("        }\n");
					out.write("    }\n\n");
					
					// count result with out getting session.
					out.write("    public long ");
					out.write(query.getQueryName());
					out.write("Count(");
					out.write(context.getDefaultInputParams());
					out.write(", Session session) {\n");
					out.write("        try {\n");
					
					// head criteria
					out.print(context.getFirstCriteriaData());
					// join criteria
					out.print(context.getJoinCriteriaData());
					out.write("\n");
					// add condition
					out.print(context.getConditions());
					
					out.write("\n            return this._count(");
					out.print(context.getFirstCriteriaName());
					out.write(");\n");
					out.write("        } finally {\n");
					out.write("        }\n");
					out.write("    }\n\n");
					
				} catch (Exception e) {
					logger.error("Generate SearchQuery[" + query.getQueryName() + "] Error: " + e.getMessage(), e);
				}
			}
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentCL);
		}
		
		out.write("}\n\n");
		out.finish();
		
		// generate test case.
		generateTestCase(diagram);
	}
	
	/**
	 * Start to generate the DAO methods after all tables 
	 * being loaded from current RDB diagrams.
	 */
	public void notifyAllLoadFinish() {
		
	}

	private void generateTestCase(RDBDiagram diagram) {
		String realPackage = diagram.getDaoPackage().replace('.', File.separatorChar);
		String javaName = diagram.getName();
		File path = new File(options.getTestDir() + "\\" + realPackage, 
				javaName + "Test.java");
		
		if (path.exists()) {
			// if already existed, we just stop generating again.
			return;
		}
		
		out.write("\n!!!!file ");
		out.print(path.getAbsolutePath());
		out.write("\n");
		if (logger.isInfoEnabled()) {
			logger.info("Dao testcase is {}, whose generated file is {}", 
					javaName, path.getAbsolutePath());
		}
		
		out.write("package ");
		out.print(diagram.getDaoPackage());
		out.write(";\n");
		out.write("\nimport org.junit.Test;\n");
		for (TableType table : diagram.getTables()) {
			ClassMappingType mapping = table.getMapping();
			if (mapping == null) {
				continue;
			}
			if (mapping.getBusinessEntity() == null) {
				continue;
			}
			String beEntityName = mapping.getBusinessEntity().getEntityName();
			BusinessEntityType be = entityManager.getEntity(beEntityName, BusinessEntityType.class);
			if (!be.isNeedHistory()) {
				continue;
			}
			
			out.write("\nimport ");
			String beInterfaceName = BEUtil.getBEInterfaceClassName(beEntityName);
			out.print(beInterfaceName);
			out.write(";\nimport ");
			String beImplName = BEUtil.getBEImplementClassName(beEntityName);
			out.print(beImplName);
			out.write(";");
		}
		out.write("\n\n");
		out.write("public class ");
		out.print(javaName);
		out.write("Test {\n\n");
		
		out.write("    private ");
		out.print(javaName);
		out.write(" service = new ");
		out.print(javaName);
		out.write("();\n\n");
		
		for (SearchQueryType query: diagram.getQueries()) {
			out.write("    @Test\n");
			out.write("    public void test");
			out.print(query.getQueryName());
			out.write("() {\n");
			//TODO:
			
			out.write("    }\n\n");
		}
		
		out.write("}\n\n");
		out.finish();

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
