package org.shaolin.bmdp.designtime.orm;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ClassMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FKType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
import org.shaolin.bmdp.datamodel.rdbdiagram.JoinTableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.OnDeleteType;
import org.shaolin.bmdp.datamodel.rdbdiagram.PKType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBDiagram;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SequenceType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ViewOpType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ViewType;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.persistence.provider.DBMSProviderFactory;
import org.shaolin.bmdp.persistence.provider.IDBMSProvider;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLSchemaGenerator implements IEntityEventListener<RDBType, RDBDiagram> {
	
	private static final Logger logger = LoggerFactory.getLogger(OracleSchemaGenerator.class);
    
	private final PumpWriter out = new PumpWriter();
	
	private List<RDBType> snapshot = new ArrayList<RDBType>();
    
	private List<JoinTableType> joinTables = new ArrayList<JoinTableType>();
    
    private List<SequenceType> sequenceList = new ArrayList<SequenceType>();
    
    private final GeneratorOptions option;
    
    private final IDBMSProvider provider;
    
    private final String sqlFileName;
    
    private EntityManager entityManager;
    
    private boolean isFirst = true;
    
    public MySQLSchemaGenerator(GeneratorOptions option) {
    	this.option = option;
    	this.provider = DBMSProviderFactory.getProvider(option.getSqlVendorType());
    	this.sqlFileName = BuilderUtil.getSqlFileName(option.getSqlDir()
    						+ "/" + option.getProjectName());
    	
    	out.setOutputDir(option.getSqlDir());
		out.setEncoding(Registry.getInstance().getEncoding());
    }
    
    public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void notify(EntityAddedEvent<RDBType, RDBDiagram> event) {
		if (event.getDiagram().getDaoPackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		snapshot.add(event.getEntity());
	}

	public void notify(EntityUpdatedEvent<RDBType, RDBDiagram> event) {
		if (event.getDiagram().getDaoPackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		if (snapshot.contains(event.getOldEntity())) {
			snapshot.remove(event.getOldEntity());
		}
		snapshot.add(event.getNewEntity());
	}

	public Class<RDBType> getEventType() {
		return RDBType.class;
	}

	@Override
	public void notifyLoadFinish(DiagramType d) {
		RDBDiagram diagram = (RDBDiagram) d;
		if (diagram.getDaoPackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		List<JoinTableType> joins = ((RDBDiagram)diagram).getJoinTables();
		for (JoinTableType j : joins) {
			joinTables.add(j);
		}
	}
	
	public void notifyAllLoadFinish() {
		if (snapshot.isEmpty()) {
			return;
		}
		try {
			validate();
			
			out.write("\n\n");
	        
	        generateTableSqlFile();
	        
	        out.write("\n\n");
	        out.finish();
		} finally {
			snapshot.clear();
		}
	}
	
	private void validate() {
		List<String> checkName = new ArrayList<String>();
		for (RDBType type : snapshot) {
			if (!checkName.contains(type.getEntityName())) {
				checkName.add(type.getEntityName());
			} else {
				logger.warn("Table name " + type.getEntityName()
						+ " is duplicated!!!");
			}
			
			//TODO: check column name, these are special words.
			// GROUP | RESUME | PRIMARY
			
		}
		checkName.clear();
		for (JoinTableType j : joinTables) {
			if (!checkName.contains(j.getName())) {
				checkName.add(j.getName());
			} else {
				logger.warn("Join Table name " + j.getName()
						+ " is duplicated!!!");
			}
		}
		
	}
    
    private void generateTableSqlFile()
    {
    	SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String now = dataFormat.format(new Date());
    	
        File sqlFile = new File(sqlFileName + "." + provider.getName() + ".sql");
        File sqlDropFile = new File(sqlFileName + ".drop." + provider.getName() + ".sql");
        File sqlCreateIdxFile = new File(sqlFileName + ".create_idx." + provider.getName() + ".sql");
        File sqlDropIdxFile = new File(sqlFileName + ".drop_idx." + provider.getName() + ".sql");
        
        out.write("\n!!!!file ");
        out.print(sqlFile.getAbsolutePath());
        /**
         * -- Create Databse
CREATE DATABASE bmdp;
grant all privileges on bmdp.* to root@localhost identified by 'admin';
flush privileges;
         */
        out.write("\n#---------------------------------------\n-- Create SQL Generated               --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n#---------------------------------------\n");
        out.write("\nUSE <database name>;\n");
        
		for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
				generateSqlForTable(provider, table);
			} else {
				ViewType view = (ViewType) type;
				generateSqlForView(provider, view);
			}
		}
		
		for (JoinTableType jt : joinTables) {
			generateSqlForJoinTable(provider, jt);
		}
		
        generateSqlForFK(provider);
        isFirst = false;
    
        out.write("\n\n!!!!file ");
        out.print(sqlDropFile.getAbsolutePath());
        out.write("\n#---------------------------------------\n-- Drop SQL Generated         --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n#---------------------------------------\n");
        out.write("\nUSE <database name>;\n");
        
        for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
				out.write("\nDROP TABLE ");
		        out.print(provider.getRDBName(table));
		        out.print(provider.getDropTableSuffix(table));
		        out.write(";\n");
			} else {
				ViewType view = (ViewType) type;
				out.write("\nDROP VIEW ");
		        out.print(provider.getRDBName(view));
		        out.write(";\n");
			}
		}
        for (JoinTableType jt : joinTables) {
        	out.write("\nDROP TABLE ");
	        out.print(jt.getName());
	        out.print(provider.getDropTableSuffix(null));
	        out.write(";\n");
		}
    
        out.write("\n\n!!!!file ");
        out.print(sqlCreateIdxFile.getAbsolutePath());
        out.write("\n#---------------------------------------\n-- Create Index SQL Generated         --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n#---------------------------------------\n");
        out.write("\nUSE <database name>;\n");
        
        for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
	            List<IndexType> indexs = table.getIndices();
	            for (IndexType index: indexs) {
	                generateCreateIndexSql(provider, table, index);
	            }
			}
        }
        generateSqlForSequence(provider);
    
        out.write("\n\n\n!!!!file ");
        out.print(sqlDropIdxFile.getAbsolutePath());
        out.write("\n#--------------------------------------\n-- Drop Index SQL Generated      --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n#--------------------------------------\n");
        out.write("\nUSE <database name>;\n");
        
        for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
	            List<IndexType> indexs = table.getIndices();
	            for (IndexType index: indexs)
	            {
	                generateDropIndexSql(provider, index, table.getEntityName());
	            }
			}
        }
        generateDropSqlForSequence(provider);
    
        out.write("\n\n\n");
    }
    
    private void generateSqlForJoinTable(IDBMSProvider provider, JoinTableType jt)
    {
    	//TODO:
    	String sequence = jt.getName() + "_SEQ";
    	SequenceType seq = new SequenceType();
		seq.setName(sequence);
		sequenceList.add(seq);
		
    	//create table
        out.write("\n-- Join table ");
        out.print(jt.getName());
        out.write("\nCREATE TABLE ");
        out.print(jt.getName());
        out.write("\n(");
    
        ColumnType pkCol = new ColumnType();
        pkCol.setName("ID");
        pkCol.setType("BIGINT");
        pkCol.setPrecision(38);
        pkCol.setIsNull(false);
        ColumnType elt = new ColumnType();
        elt.setName("ELT");
        elt.setType("BIGINT");
        elt.setPrecision(38);
        elt.setIsNull(false);
        ColumnType index = new ColumnType();
        index.setName("_index");
        index.setType("INT");
        index.setPrecision(11);
        index.setIsNull(false);
        generateColumnSql(provider, pkCol);
        out.write(",\n ");
        generateColumnSql(provider, elt);
        out.write(",\n ");
        generateColumnSql(provider, index);
        out.write(");\n");
        
    }
    
    private void generateSqlForTable(IDBMSProvider provider, TableType tableType)
    {
		if (isFirst) {
			// collection sequences
			List<SequenceType> sequences = tableType.getSequences();
			if (sequences.size() > 0) {
				for (SequenceType sequence : sequences) {
					sequenceList.add(sequence);
				}
			} else {
				// generate a default one.
				List<PKType> pk = tableType.getPrimaryKeies();
				if (pk.size() > 0) {
					SequenceType seq = new SequenceType();
					seq.setName(tableType.getEntityName() + "_SEQ");
					sequenceList.add(seq);
				}
			}
			
		}
    
        //create table
        out.write("\n-- ");
        out.print(tableType.getEntityName());
        out.write("\nCREATE TABLE ");
        out.print(provider.getRDBName(tableType));
        out.write("\n(");
    
		int i = 0;
		List<ColumnType> columns = tableType.getColumns();
		ClassMappingType mappings = tableType.getMapping();
		List<FieldMappingType> fields = null;
		if (mappings != null) {
			fields = mappings.getFieldMappings();
		}
		for (ColumnType column : columns) {
			if (i++ > 0) {
				out.write(",\n ");
			}
			generateColumnSql(provider, column);
		}
    
        List<PKType> pkType = tableType.getPrimaryKeies();
        if (pkType != null && pkType.size() > 0)
        {
	        out.write(",\n PRIMARY KEY(");
	        i = 0;
			for (int n = pkType.size(); i < n; i++) {
				if (i > 0) {
					out.write(",");
				}
				out.print(provider.getColumnName(pkType.get(i).getColumnName()));
			}
	        out.write(")\n");
        }
        out.write(");\n");
    }
    
    private void generateSqlForView(IDBMSProvider provider, ViewType viewType)
    {
        String viewName = provider.getRDBName(viewType);
    
        RDBType baseType = getRDBByName(viewType.getBaseTable().getEntityName());
        String base = provider.getRDBName(baseType);
    
        RDBType extType = getRDBByName(viewType.getExtTable().getEntityName());
        String ext = provider.getRDBName(extType);
    
		if (ViewOpType.UNION.equals(viewType.getOp())) {
			out.write("\n-- ");
			out.print(viewType.getEntityName());
			out.write("\nCREATE VIEW ");
			out.print(viewName);
			out.write(" AS SELECT\n");

			generateSelection(provider, baseType, base, true);

			out.write(" FROM ");
			out.print(base);
			out.write(" UNION ALL SELECT\n");

			generateSelection(provider, extType, ext, true);

			out.write(" FROM ");
			out.print(ext);
			out.write(";\n");
		}
        else //null or JOIN
        {
	        out.write("\n-- ");
	        out.print(viewType.getEntityName());
	        out.write("\nCREATE VIEW ");
	        out.print(viewName);
	        out.write(" AS SELECT\n");
    
            generateSelection(provider, baseType, base, true);
            generateSelection(provider, extType, ext, true);
    
	        out.write(" FROM ");
	        out.print(base);
	        out.write(",");
	        out.print(ext);
	        out.write(" WHERE ");
	        out.print(base);
	        out.write(".BOID=");
	        out.print(ext);
	        out.write(".BOID;\n");
        }
    }
    
    private void generateDropSqlForView(IDBMSProvider provider, ViewType viewType)
    {
        out.write("\nDROP VIEW ");
        out.print(provider.getRDBName(viewType));
        out.write(";    \n");
    }
    
    private void generateColumnSql(IDBMSProvider provider, ColumnType column)
    {
    	boolean autoIncrement = column.isAutoIncrement();
        String notNull = autoIncrement ? " NOT NULL" : "";
        
        out.print(provider.getColumnName(column.getName()));
        out.write(" ");
        out.print(provider.getColumnType(column));
        out.print(notNull);

		if (autoIncrement) {
			out.write(" AUTO_INCREMENT");
		}
		if (column.getDefault() != null && !"false".equals(column.getDefault())) {
			out.write(" DEFAULT ");
			out.write(column.getDefault());
		}
    }
    
    private void generateCreateIndexSql(IDBMSProvider provider, TableType table, IndexType index)
    {
        String unique = index.isIsUnique() ? "UNIQUE " : "";
    
        out.write("\nCREATE ");
        out.print(unique);
        out.write("INDEX ");
        out.print(provider.getIndexName(index));
        out.write(" ON ");
        out.print(provider.getRDBName(table));
        out.write("(");
    
		for (int i = 0, n = index.getColumns().size(); i < n; i++) {
			if (i > 0) {
				out.write(",");
			}
			out.print(provider.getColumnName(index.getColumns().get(i)));
		}
        out.write(");\n");
    }
    
    private void generateDropIndexSql(IDBMSProvider provider, IndexType index, String tableName)
    {
        out.write("\nDROP INDEX ");
        out.print(provider.getIndexName(index));
        out.write(" ON ");
        out.write(tableName);
        out.write(";\n");
    }
    
    private void generateSelection(IDBMSProvider provider, RDBType rdbType,
            String name, boolean ignoreLobColumns)
    {
        if (rdbType instanceof TableType)
        {
            generateSelectionForTable(provider, (TableType)rdbType, name, ignoreLobColumns);
        }
        else //view
        {
            ViewType viewType = (ViewType)rdbType;
            RDBType baseType = getRDBByName(viewType.getBaseTable().getEntityName());
            if (ViewOpType.UNION.equals(viewType.getOp()))
            {
                generateSelection(provider, baseType, name, ignoreLobColumns);
            }
            else //NULL or JOIN
            {
                RDBType extType = getRDBByName(viewType.getExtTable().getEntityName());
                generateSelection(provider, baseType, name, ignoreLobColumns);
                generateSelection(provider, extType, name, ignoreLobColumns);
            }
        }
    }
    
    private void generateSelectionForTable(IDBMSProvider provider, TableType tableType,
            String name, boolean ignoreLobColumns)
    {
        for(int i = 0, n = tableType.getColumns().size(); i < n; i++)
        {
            ColumnType column = tableType.getColumns().get(i);
            String columnType = column.getType();
            if (column.isRedundant())
            {
                continue;
            }
            if (ignoreLobColumns && (
                columnType.equals("BLOB") ||
                columnType.equals("BLOB2") ||
                columnType.equals("CLOB") ||
                columnType.equals("CLOB2")))
            {
                continue;
            }
            
	        out.write(",\n");
	        out.print(name);
	        out.write(".");
	        out.print(provider.getColumnName(column.getName()));
    
        }
    }
    
    /**
     * MYSQL does not support the sequence defintion.
     * instead by using AUTO_INCREMENT with primary key.
     * @param provider
     */
    private void generateSqlForSequence(IDBMSProvider provider) {
    	/**
		for (int i = 0; i < sequenceList.size(); i++) {
			out.write("\n");
			out.print(provider.generateSequenceSql((SequenceType) sequenceList
					.get(i)));
			out.write("\n");
		}
		*/
	}
    
	private void generateDropSqlForSequence(IDBMSProvider provider) {
		/**
		for (int i = 0; i < sequenceList.size(); i++) {
			out.write("\n");
			out.print(provider
					.generateDropSequenceSql((SequenceType) sequenceList.get(i)));
			out.write("\n");
		}
		*/
	}
    
	private void generateSqlForFK(IDBMSProvider provider) {
		for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
				if (table.getForeignKeies().size() == 0) {
					continue;
				}
				
				for (int j = 0, m = table.getForeignKeies().size(); j < m; j++) {
					FKType fk = table.getForeignKeies().get(j);
					// process on delete
					String constraint = "";
					if (fk.getOnDelete() != null && fk.getOnDelete() == OnDeleteType.CASCADE) {
						constraint = "ON DELETE CASCADE";
					} else {
						constraint = "ON DELETE SET NULL";
					}
					
					out.write("\nALTER TABLE ");
					out.print(provider.getRDBName(table));
					out.write(" ADD CONSTRAINT ");
					out.print(provider.getConstraintName("FK_" + table.getEntityName() + "_" + fk.getName()));
					out.write(" FOREIGN KEY(");
					out.print(provider.getColumnName(fk.getName()));
					out.write(") REFERENCES ");
					TableType refType = getTableByName(fk.getRefTable()
							.getEntityName());
					out.print(provider.getRDBName(refType));
					out.write(" (");
					out.print(fk.getColumnName());
					out.write(") ");
					out.print(constraint);
					out.write(";\n");
				}
			}
		}
	}
	
	private RDBType getRDBByName(String name) {
		try {
			return getTableByName(name);
		} catch (EntityNotFoundException e) {
			return getViewByName(name);
		}
	}
	
	private ViewType getViewByName(String viewName) {
		return entityManager.getEntity(viewName, ViewType.class);
	}
    
	private TableType getTableByName(String tableName) {
		return entityManager.getEntity(tableName, TableType.class);
	}
}
