package org.shaolin.bmdp.persistence.provider;

import java.sql.Connection;
import java.util.Date;

import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SequenceType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;

public interface IDBMSProvider {

	String getName();

	// sql gen
	String getRDBName(RDBType rdb);

	String getColumnName(String columnName);

	String getColumnType(ColumnType column);

	String getSequenceName(String name);

	String getSequenceDataType();

	String getSequenceNoCache();

	String getIndexName(IndexType index);

	String getConstraintName(String name);

	String getDropTableSuffix(TableType table);

	String getExit();

	String getSeqNextValueSql(String seqName);

	String generateSequenceSql(SequenceType seq);

	String generateDropSequenceSql(SequenceType seq);

	// row num
	boolean canOptimizeRowNum();

	String getRowNum();

	// batch temp
	void afterCreateConnection(Connection con);

	String getBatchTempTable();

	// timestamp
	String getToTimestampFunction(Date date);

	String getToDateFunction(Date date);

	String getToCharFunction(String columnName);

	// random
	String getRandomFunction();

	String getParamInFunction();

}
