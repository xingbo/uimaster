package org.shaolin.bmdp.runtime.ce;

import java.io.Serializable;

import org.shaolin.bmdp.i18n.ResourceUtil;

public class ConstantDecorator implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String description;
	private final String i18nInfo;
	private final String icon;

	public ConstantDecorator(String description, String i18nInfo, String icon) {
		this.description = description;
		this.i18nInfo = i18nInfo;
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public String getI18nInfo() {
		return i18nInfo;
	}
	
	public String getI18nValue() {
		if (i18nInfo.indexOf("||") != -1) {
			String[] v = i18nInfo.split("||");
			String displayName = ResourceUtil.getResource(null, v[0], v[1]);
			if (displayName != null) {
				return displayName;
			}
		}
		return this.description;
	}

	public String getIcon() {
		return icon;
	}

}
