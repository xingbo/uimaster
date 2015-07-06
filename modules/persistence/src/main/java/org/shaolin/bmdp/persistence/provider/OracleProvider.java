package org.shaolin.bmdp.persistence.provider;

import java.util.Date;

import org.shaolin.bmdp.datamodel.rdbdiagram.ColumnType;
import org.shaolin.bmdp.datamodel.rdbdiagram.IndexType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBType;
import org.shaolin.bmdp.datamodel.rdbdiagram.TableType;
import org.shaolin.bmdp.utils.DateParser;

public final class OracleProvider extends AbstractDBMSProvider {
	
	public static final String TO_TIMESTAMP = "TO_TIMESTAMP(";
	public static final String TO_TIMESTAMP_FORMAT = ", 'YYYY-MM-DD HH24:MI:SS.FF3')";

	public static final String TO_DATE = "TO_DATE(";
	public static final String TO_DATE_FORMAT = ", 'YYYY-MM-DD HH24:MI:SS')";
	
	public String getName() {
		return "ORACLE";
	}

	public String getRDBName(RDBType rdb) {
		String name = rdb.getName();
		if (name.length() <= 30) {
			return name;
		}
		System.out.println("Warning: " + rdb.getEntityName()
				+ "'s name trimmed to 30");
		return name.substring(0, 30);
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
			String len = column.getLength();
			return "NUMBER(" + ((len != null) ? len : "38") + ")";
		}
		if (type.equals("BIT")) {
			return "NUMBER(1)";
		}
		if (type.equals("BLOB")) {
			return "LONG RAW";
		}
		if (type.equals("BLOB2")) {
			return "BLOB";
		}
		if (type.equals("CLOB")) {
			return "LONG";
		}
		if (type.equals("CLOB2")) {
			return "CLOB";
		}
		if (type.equals("DECIMAL")) {
			StringBuffer sb = new StringBuffer("NUMBER");
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
			return "NUMBER(" + ((len != null) ? len : "10") + ")";
		}
		if (type.equals("DATE")) {
			return "DATE";
		}
		if (type.equals("DATETIME")) {
			return "DATETIME";
		}
		if (type.equals("TIMESTAMP")) {
			return "TIMESTAMP";
		}
		if (type.equals("VARCHAR")) {
			String len = column.getLength();
			return "VARCHAR2(" + ((len != null) ? len : "255") + ")";
		}
		throw new IllegalArgumentException("unsupported column type: " + type);
	}

	public String getSequenceName(String name) {
		if (name.length() <= 30) {
			return name;
		}
		System.out.println("Warning: Sequence:" + name + " trimmed to 30");
		return name.substring(0, 30);
	}

	public String getIndexName(IndexType index) {
		String name = index.getName();
		if (name.length() <= 30) {
			return name;
		}
		System.out.println("Warning: Index:" + name + " trimmed to 30");
		return name.substring(0, 30);
	}

	public String getConstraintName(String name) {
		if (name.length() <= 30) {
			return name;
		}
		System.out.println("Warning: Constraint:" + name + " trimmed to 30");
		return name.substring(0, 30);
	}

	public String getDropTableSuffix(TableType table) {
		return " CASCADE CONSTRAINTS";
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
		return "ROWNUM";
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

    public String buildDirectSql(String dSql, int requestedCount, int offset)
    {
        boolean needOffset = (offset != 0);
        boolean needRequestedCount = (requestedCount != -1);
        boolean providerCanOptimizeRowNum = this.canOptimizeRowNum();

        if (!needRequestedCount && !needOffset)
        {
            return dSql;
        }
        String rowNum = this.getRowNum();
        if (!needOffset && providerCanOptimizeRowNum)
        {
            return "SELECT * FROM (" + dSql + ") DTA WHERE " + rowNum + "<=?";
        }
        StringBuffer sb = new StringBuffer("SELECT * FROM (SELECT DTA.*," +
                rowNum + " RN FROM (" + dSql + ") DTA");
        if (needRequestedCount && providerCanOptimizeRowNum)
        {
            sb.append(" WHERE ");
            sb.append(rowNum);
            sb.append("<=?");
            needRequestedCount = false;
        }
        sb.append(") DTA2 WHERE ");
        if (needRequestedCount)
        {
            sb.append("RN<=?");
        }
        if (needRequestedCount && needOffset)
        {
            sb.append(" AND ");
        }
        if (needOffset)
        {
            sb.append("RN>?");
        }
        return new String(sb);
    }
	
}
