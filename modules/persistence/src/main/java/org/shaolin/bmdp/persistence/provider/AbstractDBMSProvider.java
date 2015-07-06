package org.shaolin.bmdp.persistence.provider;

import java.sql.Connection;

import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SequenceType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;

public abstract class AbstractDBMSProvider implements IDBMSProvider {
	
	public String getRDBName(RDBType rdb) {
		return rdb.getName();
	}

	public String getColumnName(String columnName) {
		return columnName;
	}

	public String getIndexName(IndexType index) {
		return index.getName();
	}

	public String getSequenceName(String name) {
		return name;
	}

	public String getSequenceDataType() {
		return "";
	}

	public String getSequenceNoCache() {
		return "NOCACHE";
	}

	public String getConstraintName(String name) {
		return name;
	}

	public String getDropTableSuffix(TableType table) {
		return "";
	}

	public String generateSequenceSql(SequenceType seq) {
		return "CREATE SEQUENCE "
				+ getSequenceName(seq.getName())
				+ getSequenceDataType()
				+ " START WITH "
				+ seq.getStartWith()
				+ " INCREMENT BY "
				+ seq.getSpan()
				+ " "
				+ (seq.getDbcache() == 0 ? getSequenceNoCache() : "CACHE "
						+ seq.getDbcache()) + ";";
	}

	public String generateDropSequenceSql(SequenceType seq) {
		return "DROP SEQUENCE " + getSequenceName(seq.getName()) + ";";
	}

	public void afterCreateConnection(Connection con) {
	}

	public String getParamInFunction() {
		return "?";
	}

}
