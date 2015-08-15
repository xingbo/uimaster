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