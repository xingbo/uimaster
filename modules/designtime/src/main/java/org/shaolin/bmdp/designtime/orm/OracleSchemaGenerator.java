package org.shaolin.bmdp.designtime.orm;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.FKType;
import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
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
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleSchemaGenerator implements IEntityEventListener<RDBType, RDBDiagram> {
	
	private static final Logger logger = LoggerFactory.getLogger(OracleSchemaGenerator.class);
    
	private final PumpWriter out = new PumpWriter();
	
	private List<RDBType> snapshot = new ArrayList<RDBType>();
    
    private boolean isFirst = true;
    
    private List<SequenceType> sequenceList = new ArrayList<SequenceType>();
    
    private final GeneratorOptions option;
    
    private final IDBMSProvider provider;
    
    private final String sqlFileName;
    
    private EntityManager entityManager;
    
    public OracleSchemaGenerator(GeneratorOptions option) {
    	this.option = option;
    	this.provider = DBMSProviderFactory.getProvider(option.getSqlVendorType());
    	this.sqlFileName = BuilderUtil.getSqlFileName(option.getSqlDir()
    						+ "/" + option.getProjectName());
    }
    
    public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void notify(EntityAddedEvent<RDBType, RDBDiagram> event) {
		snapshot.add(event.getEntity());
	}

	public void notify(EntityUpdatedEvent<RDBType, RDBDiagram> event) {
		if (snapshot.contains(event.getOldEntity())) {
			snapshot.remove(event.getOldEntity());
		}
		snapshot.add(event.getNewEntity());
	}

	public Class<RDBType> getEventType() {
		return RDBType.class;
	}

	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}
	
	public void notifyAllLoadFinish() {
		try {
			out.write("\n\n");
	        
	        generateTableSqlFile();
	        
	        out.write("\n\n");
	        out.finish();
		} finally {
			snapshot.clear();
			out.close();
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
        out.write("\n----------------------------------------\n-- Create SQL Generated       --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n----------------------------------------\n");
    
		for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
				generateSqlForTable(provider, table);
			} else {
				ViewType view = (ViewType) type;
				generateSqlForView(provider, view);
			}
		}
        generateSqlForFK(provider);
        generateSqlForSequence(provider);
        isFirst = false;
    
        out.write("\n\n!!!!file ");
        out.print(sqlDropFile.getAbsolutePath());
        out.write("\n----------------------------------------\n-- Drop SQL Generated         --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n----------------------------------------\n");
    
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
        generateDropSqlForSequence(provider);
    
        out.write("\n\n!!!!file ");
        out.print(sqlCreateIdxFile.getAbsolutePath());
        out.write("\n----------------------------------------\n-- Create Index SQL Generated --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n----------------------------------------\n");
    
        for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
	            List<IndexType> indexs = table.getIndices();
	            for (IndexType index: indexs) {
	                generateCreateIndexSql(provider, table, index);
	            }
			}
        }
    
        out.write("\n\n\n!!!!file ");
        out.print(sqlDropIdxFile.getAbsolutePath());
        out.write("\n----------------------------------------\n-- Drop Index SQL Generated   --\n--                                    --\n-- Please DO NOT modify !!            --\n-- Generated on ");
        out.print(now);
        out.write("   --\n----------------------------------------\n");
    
        for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
	            List<IndexType> indexs = table.getIndices();
	            for (IndexType index: indexs)
	            {
	                generateDropIndexSql(provider, index);
	            }
			}
        }
    
        out.write("\n\n\n");
    }
    
    private void generateSqlForTable(IDBMSProvider provider, TableType tableType)
    {
		if (isFirst) {
			// collection sequences
			List<SequenceType> sequences = tableType.getSequences();
			for (SequenceType sequence : sequences) {
				sequenceList.add(sequence);
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
		for (ColumnType column : columns) {
			if (i++ > 0) {
				out.write(",\n ");
			}
			generateColumnSql(provider, column);
		}
        
        out.write(");\n");
    
        List<PKType> pkType = tableType.getPrimaryKeies();
        if (pkType != null && pkType.size() > 0)
        {
            String pkName = "PK_" + tableType.getName();
            
	        out.write("\nALTER TABLE ");
	        out.print(provider.getRDBName(tableType));
	        out.write(" ADD CONSTRAINT ");
	        out.print(provider.getConstraintName(pkName));
	        out.write(" PRIMARY KEY(");
	    
	        i = 0;
			for (int n = pkType.size(); i < n; i++) {
				if (i > 0) {
					out.write(",");
				}
				out.print(provider.getColumnName(pkType.get(i).getColumnName()));
			}
	        out.write(");\n");
        }
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
        String notNull = column.isIsNull() ? "" : " NOT NULL";
        
        out.print(provider.getColumnName(column.getName()));
        out.write(" ");
        out.print(provider.getColumnType(column));
        out.print(notNull);
    
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
    
    private void generateDropIndexSql(IDBMSProvider provider, IndexType index)
    {
        out.write("\nDROP INDEX ");
        out.print(provider.getIndexName(index));
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
    
	private void generateSqlForSequence(IDBMSProvider provider) {
		for (int i = 0; i < sequenceList.size(); i++) {
			out.write("\n");
			out.print(provider.generateSequenceSql((SequenceType) sequenceList
					.get(i)));
			out.write("\n");
		}
	}
    
	private void generateDropSqlForSequence(IDBMSProvider provider) {
		for (int i = 0; i < sequenceList.size(); i++) {
			out.write("\n");
			out.print(provider
					.generateDropSequenceSql((SequenceType) sequenceList.get(i)));
			out.write("\n");
		}
	}
    
	private void generateSqlForFK(IDBMSProvider provider) {
		for (RDBType type : snapshot) {
			if (type instanceof TableType) {
				TableType table = (TableType) type;
				if (table.getForeignKeies().size() == 0) {
					return;
				}
				
				for (int j = 0, m = table.getForeignKeies().size(); j < m; j++) {
					FKType fk = table.getForeignKeies().get(j);
					// process on delete
					String onDelete = "";
					if (fk.getOnDelete() != null) {
						if (fk.getOnDelete() == OnDeleteType.CASCADE) {
							onDelete = " ON DELETE CASCADE";
						} else {
							onDelete = " ON DELETE SET NULL";
						}
					}
					
					out.write("ALTER TABLE ");
					out.print(provider.getRDBName(table));
					out.write(" ADD CONSTRAINT ");
					out.print(provider.getConstraintName("FK_" + table.getEntityName() + "_" + fk.getName()));
					out.write(" FOREIGN KEY(");
					out.print(provider.getColumnName(fk.getColumnName()));
	
					TableType refType = getTableByName(fk.getRefTable()
							.getEntityName());
					out.write(") REFERENCES ");
					out.print(provider.getRDBName(refType));
					out.print(onDelete);
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
