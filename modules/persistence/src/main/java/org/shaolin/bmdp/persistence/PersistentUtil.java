package org.shaolin.bmdp.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;

public class PersistentUtil {

	private final static Map<String, String> classMappings = new HashMap<String, String>();

	private final static IEntityManager entityManager = 
			IServerServiceManager.INSTANCE.getEntityManager();
	
	public static void addClassMapping(String beName, String tableName) {
		classMappings.put(beName, tableName);
	}
	
	public static String getTableName(String beName) {
		if (!classMappings.containsKey(beName)) {
			throw new IllegalStateException("BE entity " 
					+ beName + " does not define the class mapping relatioship.");
		}
		TableType table = entityManager.getEntity(classMappings.get(beName), TableType.class);
		return table.getEntityName();
	}
	
	public static List<String> getColumns(String tableName) {
		TableType table = entityManager.getEntity(tableName, TableType.class);
		List<String> columnNames = new ArrayList<String>();
		List<ColumnType> columns = table.getColumns();
		for (ColumnType col: columns) {
			columnNames.add(col.getName());
		}
		return columnNames;
	}

}
