package org.shaolin.uimaster.page.od.mappings;

import java.io.Serializable;

import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.DataParamType;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.od.ODContext;

public abstract class DataParam implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final DataParamType type;

	public DataParam(DataParamType type) {
		this.type = type;
	}
	
	public String getParamName() {
		return type.getParamName();
	}

	// ////////from here we plugin our src
	public void parseDataToUI(OOEEContext ooeeContext)
			throws ParsingException {

	}

	public void parseUIToData(OOEEContext ooeeContext, ComponentMappingType mapping)
			throws ParsingException {

	}

	public Object executeDataToUI(ODContext odContext)
			throws EvaluationException {
		return null;
	}

	public void executeUIToData(ODContext odContext, OOEEContext ooeeContext,
			ComponentMappingType mapping) throws EvaluationException {

	}

}