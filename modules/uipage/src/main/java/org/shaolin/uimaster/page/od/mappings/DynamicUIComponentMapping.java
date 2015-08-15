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

import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.DataParamType;
import org.shaolin.bmdp.datamodel.page.DynamicUIMappingType;
import org.shaolin.bmdp.datamodel.page.ExpressionParamType;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.ajax.Panel;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.ODContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicUIComponentMapping extends ComponentMapping {

	private static final Logger logger = LoggerFactory.getLogger(DynamicUIComponentMapping.class);

	public static final String JSON_VALUE = "jsonValue";
	
	public static final String FILTER = "filter";
	
	private final DynamicUIMappingType type;

	private final List<DataParam> dataParams = new ArrayList<DataParam>();

	public DynamicUIComponentMapping(DynamicUIMappingType type) {
		super(type);
		this.type = type;
		
		List<DataParamType> dataComps = type.getDataComponents();
		for (DataParamType dataComp : dataComps) {
			if (dataComp instanceof ComponentParamType) {
				dataParams.add(new ComponentParam((ComponentParamType) dataComp));
			} else if (dataComp instanceof ExpressionParamType) {
				dataParams.add(new ExpressionParam((ExpressionParamType) dataComp));
			} else {
				logger.warn("[Dynamic UI Mapping]: unsupport data mapping type: " + dataComp);
			}
		}

		if (!IODMappingConverter.UI_WIDGET_TYPE.equals(type.getUIComponent().getParamName())) {
			type.getUIComponent().setParamName(IODMappingConverter.UI_WIDGET_TYPE);
		}
	}

	@Override
	public void parse(OOEEContext context, ODObject odObject)
			throws ParsingException {
		super.parse(context);

		if (logger.isDebugEnabled()) {
			logger.debug("[Dynamic UI Mapping]: parse mapping: {}, ui panel: {}", 
					new Object[]{type.getName(), type.getUIComponent().getComponentPath()});
		}
		
		try {
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultParsingContext defaultPContext = (DefaultParsingContext) context
					.getParsingContextObject(ODContext.LOCAL_TAG);
			defaultPContext.setVariableClass(DynamicUIComponentMapping.JSON_VALUE, String.class);
			ooeeContext.setParsingContextObject(ODContext.LOCAL_TAG,
					defaultPContext);
			ooeeContext.setDefaultParsingContext(defaultPContext);
			
			for (DataParam dataParam : dataParams) {
				dataParam.parseDataToUI(ooeeContext);
				dataParam.parseUIToData(ooeeContext, type);
			}

		} catch (Exception e) {
			throw new ParsingException("[Dynamic UI Mapping]: parse mapping: "
					+ type.getName() + ", ui panel: "
					+ type.getUIComponent().getComponentPath(), e);
		}
	}

	@Override
	public void execute(ODContext odContext) throws ODException {
		if (odContext.isDataToUI())
			convertDataToUI(odContext);
		else
			convertUIToData(odContext);
	}

	private void convertDataToUI(ODContext odContext) throws ODException {
		try {
			for (DataParam dataParam : dataParams) {
				String paramName = dataParam.getParamName();
				Object paramValue = dataParam.executeDataToUI(odContext);
	
				odContext.getHtmlContext().getODMapperData().put(paramName, paramValue);
				if (logger.isTraceEnabled()) {
					logger.trace("[Dynamic UI Mapping]: data paramName: {}, paramValue: {}",
							new Object[] {paramName, paramValue});
				}
			}
		} catch (EvaluationException e) {
			throw new ODException("[Dynamic UI Mapping]: data to ui mapping name: "
					+ type.getName(), e);
		} 
	}

	private void convertUIToData(ODContext odContext) throws ODException {
		OOEEContext uiToDataExpreContext = OOEEContextFactory.createOOEEContext();
		uiToDataExpreContext.setDefaultEvaluationContext(odContext.getEvaluationContextObject(ODContext.LOCAL_TAG));
		
		String uiid = odContext.getUiEntity().getName() + '.' + this.type.getUIComponent().getComponentPath();
		Panel ajaxPanel = AjaxActionHelper.getAjaxContext().getPanel(uiid);
		String data = ajaxPanel.retriveData();
		odContext.getHtmlContext().getODMapperData().put(JSON_VALUE, data);
		try {
			for (DataParam dataParam : dataParams) {
				dataParam.executeUIToData(odContext, uiToDataExpreContext, type);
			}
			if (logger.isDebugEnabled())
				logger.debug("[Dynamic UI Mapping]: ui to data end.");
		} catch (EvaluationException e) {
			throw new ODException("[Dynamic UI Mapping]: ui to data mapping name: "
					+ type.getName(), e);
		} 
	}

}