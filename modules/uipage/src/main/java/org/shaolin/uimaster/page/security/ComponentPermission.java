package org.shaolin.uimaster.page.security;

/**
 * 
 * @author wushaol
 *
 */
public class ComponentPermission {
	private String[] viewPermission = new String[0];

	private String[] editPermission = new String[0];

	public ComponentPermission() {
	}

	public ComponentPermission(String[] viewPermission, String[] editPermission) {
		if ((viewPermission != null) && (viewPermission.length > 0)) {
			this.viewPermission = viewPermission;
		}
		if ((editPermission == null) || (editPermission.length <= 0))
			return;
		this.editPermission = editPermission;
	}

	public String[] getViewPermission() {
		return this.viewPermission;
	}

	public String[] getEditPermission() {
		return this.editPermission;
	}

	public void setViewPermission(String[] viewPermission) {
		if ((viewPermission == null) || (viewPermission.length <= 0))
			return;
		this.viewPermission = viewPermission;
	}

	public void setEditPermission(String[] editPermission) {
		if ((editPermission == null) || (editPermission.length <= 0))
			return;
		this.editPermission = editPermission;
	}
}
