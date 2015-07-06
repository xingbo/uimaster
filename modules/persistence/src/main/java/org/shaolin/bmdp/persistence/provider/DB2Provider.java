package org.shaolin.bmdp.persistence.provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.bmdp.utils.DateParser;

public final class DB2Provider extends AbstractDBMSProvider {

	public String getName() {
		return "DB2";
	}

	public String getRDBName(RDBType rdb) {
		String name = rdb.getName();
		if (name.length() <= 128) {
			return name;
		}
		System.out.println("Warning: " + rdb.getEntityName()
				+ "'s name trimmed to 128");
		return name.substring(0, 128);
	}

	public String getColumnName(String columnName) {
		if (columnName.length() <= 30) {
			return columnName;
		}
		System.out.println("Warning: Column:" + columnName + " trimmed to 30");
		return columnName.substring(0, 30);
	}

	public String getColumnType(ColumnType column) {
		String type = column.getType();
		if (type.equals("BIGINT")) {
			return "BIGINT";
		}
		if (type.equals("BIT")) {
			return "SMALLINT";
		}
		if (type.equals("BLOB") || type.equals("BLOB2")) {
			String len = column.getLength();
			StringBuffer sb = new StringBuffer("BLOB");
			if (len != null) {
				sb.append("(");
				sb.append(len);
				sb.append(")");
			}
			return new String(sb);
		}
		if (type.equals("CLOB") || type.equals("CLOB2")) {
			String len = column.getLength();
			StringBuffer sb = new StringBuffer("CLOB");
			if (len != null) {
				sb.append("(");
				sb.append(len);
				sb.append(")");
			}
			return new String(sb);
		}
		if (type.equals("DECIMAL")) {
			StringBuffer sb = new StringBuffer("DECIMAL");
			String len = column.getLength();
			if (len != null) {
				sb.append("(");
				sb.append(len);
				if (column.getPrecision() > 0) {
					sb.append(",");
					sb.append(column.getPrecision());
				}
				sb.append(")");
			}
			return new String(sb);
		}
		if (type.equals("INTEGER")) {
			return "INTEGER";
		}
		if (type.equals("DATETIME")) {
			return "TIMESTAMP";
		}
		if (type.equals("TIMESTAMP2")) {
			return "TIMESTAMP";
		}
		if (type.equals("VARCHAR")) {
			String len = column.getLength();
			return "VARCHAR(" + ((len != null) ? len : "255") + ")";
		}
		throw new IllegalArgumentException("unsupported column type: " + type);
	}

	public String getSequenceName(String name) {
		if (name.length() <= 128) {
			return name;
		}
		System.out.println("Warning: Sequence:" + name + " trimmed to 128");
		return name.substring(0, 128);
	}

	public String getSequenceDataType() {
		return " AS BIGINT";
	}

	public String getSequenceNoCache() {
		return "NO CACHE";
	}

	public String getIndexName(IndexType index) {
		String name = index.getName();
		if (name.length() <= 128) {
			return name;
		}
		System.out.println("Warning: Index:" + name + " trimmed to 128");
		return name.substring(0, 128);
	}

	public String getConstraintName(String name) {
		if (name.length() <= 18) {
			return name;
		}
		System.out.println("Warning: Constraint:" + name + " trimmed to 18");
		return name.substring(0, 18);
	}

	public String getSeqNextValueSql(String seqName) {
		return "NEXTVAL FOR " + getSequenceName(seqName);
	}

	public String getExit() {
		return "quit";
	}

	public boolean canOptimizeRowNum() {
		return false;
	}

	public String getRowNum() {
		return "ROW_NUMBER() OVER()";
	}

	public void afterCreateConnection(Connection con) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.execute("DECLARE GLOBAL TEMPORARY TABLE EBOS_OR_BATCHTEMP (BOID BIGINT NOT NULL)");
		} catch (SQLException e) {
			// ignore
			/*
			 * logger.error("Error while execute:DECLARE GLOBAL TEMPORARY " +
			 * "TABLE EBOS_OR_BATCHTEMP (BOID BIGINT NOT NULL)", e);
			 */
		} finally {
			CloseUtil.close(stmt);
		}
	}

	public String getBatchTempTable() {
		return "session.EBOS_OR_BATCHTEMP";
	}

	public String getToTimestampFunction(Date date) {
		DateParser parser = new DateParser(date);
		return "TIMESTAMP('" + parser.getDB2TimestampString() + "')";
	}

	public String getToDateFunction(Date date) {
		return getToTimestampFunction(date);
	}

	public String getToCharFunction(String columnName) {
		return "TRIM(CHAR(" + columnName + "))";
	}

	public String getRandomFunction() {
		return "RAND";
	}

	public String getParamInFunction() {
		return "CAST(? AS VARCHAR(2000))";
	}
	
}
