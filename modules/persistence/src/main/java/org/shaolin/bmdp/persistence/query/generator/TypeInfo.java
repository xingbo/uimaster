package org.shaolin.bmdp.persistence.query.generator;

public class TypeInfo {

	private String typeName = null;
	private String beName = null;
	private Class clazz = null;
	private TableInstance tableInstance = null;
	private boolean hasAlias = false;
	private String colFieldName = null;
	private boolean unconflict = true;
	
	TypeInfo(String typeName, String beName, Class clazz,
			TableInstance tableInstance, boolean hasAlias, String colFieldName) {
		this.typeName = typeName;
		this.beName = beName;
		this.clazz = clazz;
		this.tableInstance = tableInstance;
		this.hasAlias = hasAlias;
		this.colFieldName = colFieldName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getBEName() {
		return beName;
	}

	public Class getClazz() {
		return clazz;
	}

	public TableInstance getTableInstance() {
		return tableInstance;
	}

	public boolean hasAlias() {
		return hasAlias;
	}

	public String getColFieldName() {
		return colFieldName;
	}

	public void setUnconflict(boolean unconflict) {
		this.unconflict = unconflict;
	}

	public boolean isUnconflict() {
		return unconflict;
	}

	public String toString() {
		return "typeName:" + typeName + ", beName:" + beName + ", clazz:"
				+ clazz.getName() + ", tableInstance:" + tableInstance
				+ ", hasAlias:" + hasAlias + ", colFieldName:" + colFieldName
				+ ", unconflict:" + unconflict;
	}

}
