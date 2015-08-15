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

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.DataParamType;
import org.shaolin.bmdp.datamodel.page.DirectComponentMappingType;
import org.shaolin.bmdp.datamodel.page.ExpressionParamType;
import org.shaolin.bmdp.datamodel.page.UIComponentParamType;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.od.ODContext;

public class DirectComponentMapping extends ComponentMapping {

	private static final Logger logger = Logger
			.getLogger(DirectComponentMappingType.class);

	private static final String EVAL_TYPE = "value";

	private final DirectComponentMappingType type;

	private final DataParam dataParam;
	
	public DirectComponentMapping(DirectComponentMappingType type) {
		super(type);
		this.type = type;
		DataParamType dataComp = type.getDataComponent();
		if (dataComp instanceof ComponentParamType) {
			dataParam = new ComponentParam((ComponentParamType) dataComp);
		} else if (dataComp instanceof ExpressionParamType) {
			dataParam = new ExpressionParam((ExpressionParamType) dataComp);
		} else {
			dataParam = new UIComponentParam((UIComponentParamType) dataComp);
		}
	}

	@Override
	public void parse(OOEEContext context, ODObject odObject)
			throws ParsingException {
		super.parse(context);
		
		if (dataParam == null) {
			throw new I18NRuntimeException(
					ExceptionConstants.EBOS_ODMAPPER_002,
					new Object[] { type.getUIComponent() });
		}

		OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
		DefaultParsingContext defaultPContext = (DefaultParsingContext) context
				.getParsingContextObject(ODContext.LOCAL_TAG);
		ooeeContext.setParsingContextObject(ODContext.LOCAL_TAG,
				defaultPContext);
		ooeeContext.setDefaultParsingContext(defaultPContext);

		if (logger.isDebugEnabled()) {
			logger.debug("[Direct Mapping]: parse mapping: "
					+ dataParam.toString());
		}
		
		dataParam.parseDataToUI(ooeeContext);
		dataParam.parseUIToData(ooeeContext, type);
	}

	@Override
	public void execute(ODContext odContext) throws ODException {
		if (odContext.isDataToUI())
			convertDataToUI(odContext);
	}

	public void execute(ODContext odContext, boolean isSingleColumnMapping,
			String columnName) throws ODException {
		if (odContext.isDataToUI())
			convertDataToUI(odContext);
	}

	public void convertDataToUI(ODContext odContext) throws ODException {
		try {
			if (dataParam == null) {
				throw new I18NRuntimeException(
						ExceptionConstants.EBOS_ODMAPPER_002,
						new Object[] { type.getUIComponent() });
			}
			
			String paramName = odContext.getUiEntity().getName();
			paramName += ComponentMappingHelper.getSubComponentPath(type
					.getUIComponent());
			Object paramValue = dataParam.executeDataToUI(odContext);
			if (logger.isInfoEnabled())
				logger.info("[Direct Mapping]: paramName: " + paramName
						+ ", value: " + paramValue);
			odContext.getHtmlContext().addHTMLAttribute(paramName, EVAL_TYPE,
					paramValue);
		} catch (EvaluationException e) {
			throw new ODException("[Direct Mapping]: mapping name: "
					+ type.getName(), e);
		}
	}

	public void convertUIToData(ODContext odContext) {
	}

}