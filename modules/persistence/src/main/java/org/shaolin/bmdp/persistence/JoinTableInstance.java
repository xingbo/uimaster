package org.shaolin.bmdp.persistence;

import org.shaolin.bmdp.datamodel.rdbdiagram.JoinTableType;

public class JoinTableInstance {

	private JoinTableType type;
	
	private String srcid;
	
	private String tarid;
	
	public JoinTableInstance(JoinTableType type) {
		this.type = type;
		this.srcid = type.getSrcPKColumn();
		this.tarid = type.getTarPKColumn();
	}
	
	public String getSrcid() {
		return srcid;
	}

	public void setSrcid(String srcid) {
		this.srcid = srcid;
	}

	public String getTarid() {
		return tarid;
	}

	public void setTarid(String tarid) {
		this.tarid = tarid;
	}

}
