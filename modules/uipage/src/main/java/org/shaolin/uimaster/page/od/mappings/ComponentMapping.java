package org.shaolin.uimaster.page.od.mappings;

import java.io.Serializable;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.od.ODContext;

public abstract class ComponentMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final ComponentMappingType type;

	public ComponentMapping(ComponentMappingType type) {
		this.type = type;
	}

	public String getMappingName() {
		return this.type.getName();
	}
	
	public void parse(OOEEContext context) throws ParsingException {
		ExpressionType dataLocaleExpr = type.getDataLocale();
		if (dataLocaleExpr != null) {
			dataLocaleExpr.parse(context);
		}
	}

	public void parse(OOEEContext context, ODObject odObject)
			throws ParsingException {

	}

	public void execute(ODContext odContext) throws ODException {
	}

}