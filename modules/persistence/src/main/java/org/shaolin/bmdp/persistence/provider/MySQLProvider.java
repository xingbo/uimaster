package org.shaolin.bmdp.persistence.provider;

import java.util.Date;

import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.utils.DateParser;

public final class MySQLProvider extends AbstractDBMSProvider {
	
	public static final String TO_TIMESTAMP = "str_to_date(";
	public static final String TO_TIMESTAMP_FORMAT = ", '%Y-%m-%d %H %i %s %f')";

	public static final String TO_DATE = "str_to_date(";
	public static final String TO_DATE_FORMAT = ", '%Y-%m-%d %H %i %s')";
	
	public String getName() {
		return "MYSQL";
	}

	public String getRDBName(RDBType rdb) {
		String name = rdb.getEntityName();
		if (name.length() <= 64) {
			return name;
		}
		System.out.println("Warning: " + rdb.getEntityName()
				+ "'s name trimmed to 64");
		return name.substring(0, 64);
	}

	public String getColumnName(String columnName) {
		if (columnName.length() <= 64) {
			return columnName;
		}
		System.out.println("Warning: Column:" + columnName + " trimmed to 64");
		return columnName.substring(0, 64);
	}

	public String getColumnType(ColumnType column) {
		String type = column.getType();
		if (type.equals("BIGINT")) {
			// BIGINT[(M)] [UNSIGNED] [ZEROFILL]
			String len = column.getLength();
			return "BIGINT(" + ((len != null) ? len : "38") + ")";
		}
		if (type.equals("INT")) {
			String len = column.getLength();
			return "INT(" + ((len != null) ? len : "5") + ")";
		}
		if (type.equals("BIT")) {
			return "TINYINT(1)";
		}
		if (type.equals("BLOB")) {
			return "TINYBLOB";
		}
		if (type.equals("BLOB2")) {
			return "BLOB";
		}
		if (type.equals("CLOB")) {
			return "LONGTEXT";
		}
		if (type.equals("CLOB2")) {
			return "LONGTEXT";
		}
		if (type.equals("TEXT")) {
			return "TEXT";
		}
		if (type.equals("MEDIUMTEXT")) {
			return "MEDIUMTEXT";
		}
		if (type.equals("LONGTEXT")) {
			return "LONGTEXT";
		}
		if (type.equals("DECIMAL")) {
			// DECIMAL[(M[,D])] [UNSIGNED] [ZEROFILL]
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
			String len = column.getLength();
			return "INT(" + ((len != null) ? len : "10") + ")";
		}
		if (type.equals("DATE")) {
			return "DATE";
		}
		if (type.equals("DATETIME")) {
			return "DATETIME";
		}
		if (type.equals("TIMESTAMP")) {
			// TIMESTAMP列用于INSERT或UPDATE操作时记录日期和时间。
			// 如果你不分配一个值，表中的第一个TIMESTAMP列自动设置
			// 为最近操作的日期和时间。也可以通过分配一个NULL值，
			// 将TIMESTAMP列设置为当前的日期和时间。
			// TIMESTAMP[(M)]
			if (column.getLength() != null) {
				return "TIMESTAMP("+column.getLength()+")";
			}
			return "TIMESTAMP";
		}
		if (type.equals("VARCHAR")) {
			String len = column.getLength();
			return "VARCHAR(" + ((len != null) ? len : "255") + ")";
		}
		throw new IllegalArgumentException("unsupported column type: " + type);
	}

	public String getSequenceName(String name) {
		if (name.length() <= 64) {
			return name;
		}
		System.out.println("Warning: Sequence:" + name + " trimmed to 64");
		return name.substring(0, 64);
	}

	public String getIndexName(IndexType index) {
		String name = index.getName();
		if (name.length() <= 64) {
			return name;
		}
		System.out.println("Warning: Index:" + name + " trimmed to 64");
		return name.substring(0, 64);
	}

	public String getConstraintName(String name) {
		if (name.length() <= 64) {
			return name;
		}
		System.out.println("Warning: Constraint:" + name + " trimmed to 64");
		return name.substring(0, 64);
	}

	public String getDropTableSuffix(TableType table) {
		return " CASCADE";
	}

	public String getSeqNextValueSql(String seqName) {
		return getSequenceName(seqName) + ".NEXTVAL";
	}

	public String getExit() {
		return "exit";
	}

	public boolean canOptimizeRowNum() {
		return false;
	}

	public String getRowNum() {
		return "(@row:=@row+1) as RowNum";
	}

	public String getBatchTempTable() {
		// ORMapperController.schema +
		return "EBOS_OR_BATCHTEMP";
	}

	public String getToTimestampFunction(Date date) {
		DateParser parser = new DateParser(date);
		return TO_TIMESTAMP + "'" + parser.getTimestampString() + "'"
				+ TO_TIMESTAMP_FORMAT;
	}

	public String getToDateFunction(Date date) {
		DateParser parser = new DateParser(date);
		return TO_DATE + "'" + parser.getTimeString() + "'" + TO_DATE_FORMAT;
	}

	public String getToCharFunction(String columnName) {
		return "TO_CHAR(" + columnName + ")";
	}

	public String getRandomFunction() {
		return "DBMS_RANDOM.RANDOM";
	}

}
