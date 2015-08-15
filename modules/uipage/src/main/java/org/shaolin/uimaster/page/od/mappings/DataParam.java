/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
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