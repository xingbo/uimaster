package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

public class TreeConditions implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String selectedId;

	public String getSelectedId() {
		return selectedId;
	}

	public void setSelectedId(String selectedId) {
		this.selectedId = selectedId;
	}

	public static final TreeConditions createCondition() {
		return new TreeConditions();
	}

}
