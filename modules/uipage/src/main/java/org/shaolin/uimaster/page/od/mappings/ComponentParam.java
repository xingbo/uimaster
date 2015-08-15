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

import java.util.Map;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.DynamicUIMappingType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionUtil;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.od.BaseRulesHelper;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.od.rules.UITextWithNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentParam extends DataParam {

	private static final Logger logger = LoggerFactory
			.getLogger(ComponentParam.class);

	private ExpressionType d2UExpression;

	private ExpressionType u2DExpression;

	private ComponentParamType type;

	public ComponentParam(ComponentParamType type) {
		super(type);
		this.type = type;
	}

	@Override
	public void parseDataToUI(OOEEContext ooeeContext)
			throws ParsingException {
		String componentPath = type.getComponentPath();
		String dataToUIExpression = ComponentMappingHelper
				.getIndexedComponentPath(componentPath, false, "");
		dataToUIExpression = ODContext.LOCAL_TAG + dataToUIExpression;
		if (logger.isDebugEnabled())
			logger.debug("dataToUIExpression[{}={}]: {}", 
					new Object[] {type.getParamName(), type.getComponentPath(), dataToUIExpression});

		d2UExpression = new ExpressionType();
		d2UExpression.setExpressionString(dataToUIExpression);
		d2UExpression.parse(ooeeContext);
	}

	@Override
	public void parseUIToData(OOEEContext ooeeContext, ComponentMappingType mapping)
			throws ParsingException {
		if (!ComponentMappingHelper.isDirectComponentPath(type.getComponentPath())) {
			if (mapping instanceof SimpleComponentMappingType) {
				SimpleComponentMappingType scm = (SimpleComponentMappingType) mapping;
				String paramName = type.getParamName();
				Class<?> paramClass;
				try {
					paramClass = getODDataParamClass(scm.getMappingRule()
							.getEntityName(), paramName);
				} catch (ParsingException e) {
					throw e;
				} catch (Exception e) {
					throw new ParsingException(e.getMessage(), e);
				}
				Class componentPathClazz = ComponentMappingHelper.getComponentPathClass(type.getComponentPath(),
											(DefaultParsingContext)ooeeContext.getParsingContextObject(ODContext.LOCAL_TAG));
				String componentPathExpre = ExpressionGeneratorUtil.getFromWrapperClassString(paramName, paramClass, ODContext.GLOBAL_TAG);
				if(componentPathClazz != paramClass)
				{
					componentPathExpre = "(" + VariableUtil.getVariableClassName(componentPathClazz) + ")" + componentPathExpre;
				}
				String uiToDataExpression = ComponentMappingHelper
						.getIndexedComponentPath(type.getComponentPath(), true,
								componentPathExpre);
				uiToDataExpression = ODContext.LOCAL_TAG + uiToDataExpression;
				if (logger.isDebugEnabled()) {
					logger.debug("uiToDataExpression[{}={}]: {}", 
							new Object[] {type.getComponentPath(), type.getParamName(), uiToDataExpression});
				}
				if (paramClass != null && paramClass.isPrimitive()) {
					paramClass = ComponentMappingHelper
							.getBigPrimitiveClass(paramClass);
				}
				String objectName = ComponentMappingHelper
						.getObjectNameOfComponentPath(type.getComponentPath());
				Class<?> objectClass = ooeeContext.getParsingContextObject(
						ODContext.LOCAL_TAG).getVariableClass(objectName);
				
				OOEEContext tempOOEEContext = OOEEContextFactory
						.createOOEEContext();
				DefaultParsingContext localPContext = new DefaultParsingContext();
				localPContext.setVariableClass(objectName, objectClass);
				DefaultParsingContext globalPContext = new DefaultParsingContext();
				globalPContext.setVariableClass(paramName, paramClass);
				tempOOEEContext.setParsingContextObject(ODContext.LOCAL_TAG,
						localPContext);
				tempOOEEContext.setParsingContextObject(ODContext.GLOBAL_TAG,
						globalPContext);
				
				u2DExpression = new ExpressionType();
				u2DExpression.setExpressionString(uiToDataExpression);
				u2DExpression.parse(tempOOEEContext);
			} else if (mapping instanceof DynamicUIMappingType) {
				type.setParamName(DynamicUIComponentMapping.JSON_VALUE);
				String uiToDataExpression = ComponentMappingHelper
						.getIndexedComponentPath(type.getComponentPath(), true, ODContext.GLOBAL_TAG + DynamicUIComponentMapping.JSON_VALUE);
				uiToDataExpression = ODContext.LOCAL_TAG + uiToDataExpression;
				if (logger.isDebugEnabled()) {
					logger.debug("uiToDataExpression[{}]: {}", 
							new Object[] {type.getComponentPath(), uiToDataExpression});
				}
				String objectName = ComponentMappingHelper
						.getObjectNameOfComponentPath(type.getComponentPath());
				Class<?> objectClass = ooeeContext.getParsingContextObject(
						ODContext.LOCAL_TAG).getVariableClass(objectName);
				
				OOEEContext tempOOEEContext = OOEEContextFactory
						.createOOEEContext();
				DefaultParsingContext localPContext = new DefaultParsingContext();
				localPContext.setVariableClass(objectName, objectClass);
				DefaultParsingContext globaPContext = new DefaultParsingContext();
				globaPContext.setVariableClass(DynamicUIComponentMapping.JSON_VALUE, String.class);
				tempOOEEContext.setDefaultParsingContext(localPContext);
				tempOOEEContext.setParsingContextObject(ODContext.LOCAL_TAG, localPContext);
				tempOOEEContext.setParsingContextObject(ODContext.GLOBAL_TAG, globaPContext);
				
				u2DExpression = new ExpressionType();
				u2DExpression.setExpressionString(uiToDataExpression);
				u2DExpression.parse(tempOOEEContext);
			}
		} else {
			if (!ComponentMappingHelper.isDirectComponentPath(type.getComponentPath())) {
				// only direct component mapping in here.
				logger.warn("Dosen't support this type[{0}] for UI to Data operations.", 
							mapping.toString());
			}
		}
	}

	@Override
	public Object executeDataToUI(ODContext odContext)
			throws EvaluationException {
		Object resultObject = null;
		Map<String, Object> paramMap = odContext.getLocalVariableValues();
		String parameter = type.getComponentPath();
		if (ComponentMappingHelper.isDirectComponentPath(parameter)) {
			if (logger.isDebugEnabled())
				logger.debug("Direct component path parameter: " + parameter);
			resultObject = paramMap.get(parameter);
		} else {
			String componentPathName = ComponentMappingHelper
					.getObjectNameOfComponentPath(parameter);
			Object componentPathValue = paramMap.get(componentPathName);

			OOEEContext context = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext localEContext = new DefaultEvaluationContext();
			context.setEvaluationContextObject(ODContext.LOCAL_TAG,
					localEContext);
			localEContext.setVariableValue(componentPathName,
					componentPathValue);

			if (logger.isDebugEnabled()) {
				logger.debug("1.componentPathName: {}, 2.componentPathValue: {}, 3.data to ui expression : {}", 
						new Object[] {componentPathName, componentPathValue, d2UExpression.getExpressionString()});
			}
			resultObject = d2UExpression.evaluate(context);
		}
		return resultObject;
	}

	/**
	 * 
	 * @param odContext
	 * @param ooeeContext
	 * @param mapping
	 * @throws EvaluationException
	 */
	@Override
	public void executeUIToData(ODContext odContext, OOEEContext ooeeContext,
			ComponentMappingType mapping) throws EvaluationException {
		Map<String, Object> resultMap = odContext.getHtmlContext().getODMapperData();
		String paramName = type.getParamName();
		if (!resultMap.containsKey(paramName)) {
			if (logger.isDebugEnabled())
				logger.debug("Warning: ui to data paramName {} does not exist in ODMapperData", paramName);
			return;
		}
		String componentPathName = ComponentMappingHelper
				.getObjectNameOfComponentPath(type.getComponentPath());
		Object paramNameValue = resultMap.get(paramName);

		Object componentPathValue = odContext
				.getLocalVariableValue(componentPathName);
		if (logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("ConvertUIToData :1.paramName: ");
			sb.append(paramName).append(", ");
			sb.append("2.paramNameValue: ");
			sb.append(paramNameValue).append(", ");
			sb.append("3.componentPathName: ");
			sb.append(componentPathName).append(", ");
			sb.append("4.componentPathValue: ");
			sb.append(componentPathValue).append(", ");
			if (u2DExpression != null)
				sb.append("5.ui to data expression: ").append(
						u2DExpression.getExpressionString());

			logger.debug(sb.toString());
		}

		if (ComponentMappingHelper.isDirectComponentPath(type
				.getComponentPath())
				&& mapping instanceof SimpleComponentMappingType) {
			SimpleComponentMappingType scm = (SimpleComponentMappingType) mapping;
			if (scm.getMappingRule().getEntityName()
					.equals(UITextWithNumber.class.getName())
					&& paramName.equals("Number")) {
				// because of the 'Number' attribute is long type, not target
				// type.
				// convert od base rule number value type to local value type.
				try {
					Class<?> clazz;
					if (componentPathValue != null)
						clazz = componentPathValue.getClass();
					else
						clazz = odContext.getLocalPContext().getVariableClass(
								componentPathName);

					clazz = ComponentMappingHelper
							.convertNumbertoPrimitiveType(clazz);
					paramNameValue = ExpressionUtil.getNumericReturnObject(
							paramNameValue, clazz);
					resultMap.put(paramName, paramNameValue);
				} catch (ParsingException ex) {
					logger.debug(ex.getMessage(), ex);
				}
			}
			DefaultEvaluationContext defalutEvalContext = (DefaultEvaluationContext) odContext
					.getEvaluationContextObject(ODContext.LOCAL_TAG);
			defalutEvalContext.setVariableValue(componentPathName, paramNameValue);
		} else {
			OOEEContext context = OOEEContextFactory.createOOEEContext();
			DefaultEvaluationContext localEContext = (DefaultEvaluationContext) odContext
					.getEvaluationContextObject(ODContext.LOCAL_TAG);
			context.setEvaluationContextObject(ODContext.LOCAL_TAG,
					localEContext);
			DefaultEvaluationContext globalEContext = new DefaultEvaluationContext();
			context.setEvaluationContextObject(ODContext.GLOBAL_TAG,
					globalEContext);
			localEContext.setVariableValue(componentPathName,
					componentPathValue);
			globalEContext.setVariableValue(paramName, paramNameValue);

			u2DExpression.evaluate(context);
		}
	}

	private Class<?> getODDataParamClass(String mappingRuleName,
			String variableName) throws ClassNotFoundException, 
			InstantiationException, IllegalAccessException, ParsingException {
		
		if (BaseRulesHelper.isBaseRuleClass(mappingRuleName)) {
			IODMappingConverter rule = (IODMappingConverter)
					BaseRulesHelper.getBaseRuleClass(mappingRuleName).newInstance();
			
			if (rule.getDataEntityClassInfo().containsKey(variableName)) {
				return rule.getDataEntityClassInfo().get(variableName);
			}
			
			if (rule.getUIEntityClassInfo().containsKey(variableName)) {
				return rule.getUIEntityClassInfo().get(variableName);
			}
			
			throw new ParsingException("This variable \"" + variableName + 
					"\" does not exist in the base rule: " + mappingRuleName);
		} else {
			return PageCacheManager.getODFormObject(mappingRuleName)
					.getLocalPContext().getVariableClass(variableName);
		}
	}

}