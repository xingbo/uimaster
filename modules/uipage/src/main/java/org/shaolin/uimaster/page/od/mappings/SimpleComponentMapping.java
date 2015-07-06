package org.shaolin.uimaster.page.od.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.DataParamType;
import org.shaolin.bmdp.datamodel.page.ExpressionParamType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.datamodel.page.UIComponentParamType;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.RefForm;
import org.shaolin.uimaster.page.cache.ODFormObject;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.od.BaseRulesHelper;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.od.ODEntityContext;
import org.shaolin.uimaster.page.od.ODProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleComponentMapping extends ComponentMapping {

	private static final Logger logger = LoggerFactory
			.getLogger(SimpleComponentMapping.class);

	private final SimpleComponentMappingType type;

	private final List<DataParam> dataParams = new ArrayList<DataParam>();

	private final UIComponentParam uiDataParam;

	private final int paramCount;
	
	public SimpleComponentMapping(SimpleComponentMappingType type) {
		super(type);
		this.type = type;
		
		List<DataParamType> dataComps = type.getDataComponents();
		for (DataParamType dataComp : dataComps) {
			if (dataComp instanceof ComponentParamType) {
				dataParams.add(new ComponentParam((ComponentParamType) dataComp));
			} else if (dataComp instanceof ExpressionParamType) {
				dataParams.add(new ExpressionParam((ExpressionParamType) dataComp));
			} else {
				dataParams.add(new UIComponentParam((UIComponentParamType) dataComp));
			}
		}

		if (type.getUIComponents().size() == 0) {
			throw new I18NRuntimeException(
					ExceptionConstants.EBOS_ODMAPPER_002);
		}
		if (type.getUIComponents().size() > 1) {
			logger.warn("[Simple Mapping]: only support mapping one ui component with more data components!");
		}
		List<UIComponentParamType> iuComps = type.getUIComponents();
		if (!IODMappingConverter.UI_WIDGET_TYPE.equals(iuComps.get(0).getParamName())) {
			iuComps.get(0).setParamName(IODMappingConverter.UI_WIDGET_TYPE);
		}
		uiDataParam = new UIComponentParam(iuComps.get(0));
		
		paramCount = type.getUIComponents().size()
					+ type.getDataComponents().size();
	}

	@Override
	public void parse(OOEEContext context, ODObject odObject)
			throws ParsingException {
		super.parse(context);

		if (logger.isDebugEnabled()) {
			logger.debug("[Simple Mapping]: parse mapping: {}, mapping rule: {}", 
					new Object[]{type.getName(), type.getMappingRule().getEntityName()});
		}
		
		String ruleName = type.getMappingRule().getEntityName();
		List<DataParamType> dataComponents = type.getDataComponents();
		if (ruleName.startsWith("org.shaolin.uimaster.page.od.rules.")) {
			IODMappingConverter baseRule = BaseRulesHelper.createRule(ruleName);
			List<DataParamType> dataComps = dataComponents;
			for (DataParamType dataComp : dataComps) {
				if (dataComp instanceof ComponentParamType) {
					String varName = ((ComponentParamType)dataComp).getParamName();
					if (!baseRule.getDataEntityClassInfo().containsKey(varName)) {
						throw new ParsingException("This var " + varName 
								+ " is not defined in the mapping rule " + ruleName);
					}
				}
			}
		} else {
			try {
				ODFormObject uiform = PageCacheManager.getODFormObject(ruleName);
				String[] vars = uiform.getParamKeys();
				if ((dataComponents != null &&  dataComponents.size() > 0)
						&& (vars == null || vars.length ==0)) {
					throw new ParsingException("Please remove outside mapping variable! " +
							"There is no any variable defined in the mapping rule " + ruleName);
				}
				
				List<String> varList = Arrays.asList(vars);
				List<DataParamType> dataComps = dataComponents;
				for (DataParamType dataComp : dataComps) {
					if (dataComp instanceof ComponentParamType) {
						String varName = ((ComponentParamType)dataComp).getParamName();
						if (!varList.contains(varName)) {
							throw new ParsingException("This var " + varName 
									+ " is not defined in the mapping rule " + ruleName);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				throw new ParsingException(e.getMessage(), e);
			}
		}
		
		try {
			OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
			DefaultParsingContext defaultPContext = (DefaultParsingContext) context
					.getParsingContextObject(ODContext.LOCAL_TAG);
			ooeeContext.setParsingContextObject(ODContext.LOCAL_TAG,
					defaultPContext);
			ooeeContext.setDefaultParsingContext(defaultPContext);
			
			String targetEntityName = type.getMappingRule().getEntityName();
			if (BaseRulesHelper.isBaseRuleClass(targetEntityName)) {
				IODMappingConverter rule = (IODMappingConverter)
						BaseRulesHelper.getBaseRuleClass(targetEntityName).newInstance();
				
				DefaultParsingContext parsingContext = new DefaultParsingContext();
				
				Map<String, Class<?>> classes= rule.getDataEntityClassInfo();
				Iterator<Entry<String, Class<?>>> i = classes.entrySet().iterator();
				while (i.hasNext()) {
					Entry<String, Class<?>> entry = i.next();
					parsingContext.setVariableClass(entry.getKey(), entry.getValue());
				}
				
				Map<String, Class<?>> classes1= rule.getUIEntityClassInfo();
				Iterator<Entry<String, Class<?>>> i1 = classes1.entrySet().iterator();
				while (i1.hasNext()) {
					Entry<String, Class<?>> entry = i1.next();
					parsingContext.setVariableClass(entry.getKey(), entry.getValue());
				}
				ooeeContext.setParsingContextObject(ODContext.GLOBAL_TAG, parsingContext);
			} else {
				ODFormObject odEntityObject = PageCacheManager
						.getODFormObject(targetEntityName);
				ooeeContext.setParsingContextObject(ODContext.GLOBAL_TAG,
						odEntityObject.getLocalPContext());
			}
			
			for (DataParam dataParam : dataParams) {
				dataParam.parseDataToUI(ooeeContext);
				dataParam.parseUIToData(ooeeContext, type);
			}

			uiDataParam.parseDataToUI(ooeeContext);
			uiDataParam.parseUIToData(ooeeContext, type);
		} catch (Exception e) {
			throw new ParsingException("[Simple Mapping]: parse mapping: "
					+ type.getName() + ", mapping rule: "
					+ type.getMappingRule().getEntityName(), e);
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
			if (logger.isDebugEnabled()) {
				if (odContext.isDataToUI()) {
					logger.debug("[Simple Mapping]: data to ui mapping name: {}", type.getName());
				} else {
					logger.debug("[Simple Mapping]: ui to data mapping name: {}", type.getName());
				}
			}
			
			Map<String, Object> paramMap = new HashMap<String, Object>(paramCount);
			for (DataParam dataParam : dataParams) {
				String paramName = dataParam.getParamName();
				Object paramValue = dataParam.executeDataToUI(odContext);
				paramMap.put(paramName, paramValue);

				if (logger.isTraceEnabled()) {
					logger.trace("[Simple Mapping]: data paramName: {}, paramValue: {}",
							new Object[] {paramName, paramValue});
				}
			}
			
			Object paramValue = uiDataParam.executeDataToUI(odContext);
			String paramName = uiDataParam.getParamName();
			if (odContext.isDataToUI()) {
				paramMap.put(paramName, paramValue);
			} else {
				paramMap.put(IODMappingConverter.UI_WIDGET_ID, paramValue);
			}
			if (logger.isTraceEnabled()) {
				logger.trace("[Simple Mapping]: ui paramName: {}, paramValue: {}", 
						new Object[] {paramName, paramValue});
			}
			callODMapping(paramMap, odContext);
		} catch (ODException e) {
			throw e;
		} catch (EvaluationException e) {
			throw new ODException("[Simple Mapping]: data to ui mapping name: "
					+ type.getName(), e);
		}
	}

	/**
	 * Create empty parameters before ui2data. that's why we have to 
	 * invoke convertDataToUI() first. all UIComponentType will be 
	 * created by PageComponentContext, and access them through.
	 * 
	 * @param odContext
	 * @throws ODException
	 */
	private void convertUIToData(ODContext odContext) throws ODException {
		try {
			this.convertDataToUI(odContext);//remind this.
			
			if (logger.isDebugEnabled())
				logger.debug("[Simple Mapping]: ui to data mapping name: {}", 
					type.getName());

			Map<String, Object> resultMap = odContext.getHtmlContext().getODMapperData();
			if (resultMap == null || resultMap.isEmpty())
				return;
			if (logger.isTraceEnabled())
				logger.trace("[Simple Mapping]: ui to data resultMap: {}", 
					resultMap);

			DefaultEvaluationContext globalEContext = new DefaultEvaluationContext();
			Iterator<String> iterator = resultMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				globalEContext.setVariableValue(key, resultMap.get(key));
			}
			OOEEContext uiToDataExpreContext = OOEEContextFactory
					.createOOEEContext();
			uiToDataExpreContext.setEvaluationContextObject(
					ODContext.LOCAL_TAG,
					odContext.getEvaluationContextObject(ODContext.LOCAL_TAG));
			uiToDataExpreContext.setEvaluationContextObject(
					ODContext.GLOBAL_TAG, globalEContext);

			for (DataParam dataParam : dataParams) {
				dataParam.executeUIToData(odContext, uiToDataExpreContext, type);
			}

			if (logger.isDebugEnabled())
				logger.debug("[Simple Mapping]: ui to data end.");
		} catch (EvaluationException e) {
			throw new ODException("[Simple Mapping]: ui to data mapping name: "
					+ type.getName(), e);
		} catch (ODException e) {
			throw e;
		}
	}

	private void callODMapping(Map<String, Object> paramMap, ODContext odContext)
			throws ODException, EvaluationException {
		String mappingRuleName = type.getMappingRule().getEntityName();

		if (logger.isDebugEnabled())
			logger.debug("Call od mapping : {}", mappingRuleName);
		odContext.getHtmlContext().setODMapperData(paramMap);
		callODMapper(odContext, mappingRuleName);
	}
	
	private void callODMapper(ODContext odContext, String odmapperName) throws ODException {
		if (odmapperName == null) {
			return;
		}

		HTMLSnapshotContext htmlContext = odContext.getHtmlContext();
		Map<String, Object> odMapperData = htmlContext.getODMapperData();
		if (odmapperName.startsWith("org.shaolin.uimaster.page.od.rules.")) {
			if (logger.isTraceEnabled()) {
				logger.trace("call od base rule: {} ( **[the depth of level is {}.]** )", 
						new Object[] {odmapperName, (odContext.increaseDeepLevel())});
			}
			try {
				if (odmapperName.indexOf("Date") > 0) {
					Object value = htmlContext.getRequest()
							.getSession(true).getAttribute(
									WebflowConstants.CLIENT_TIMEZONE_OFFSET);
					if (odMapperData.containsKey("PropValues")) {
						 ((Map) odMapperData.get("PropValues")).
						 	put(WebflowConstants.CLIENT_TIMEZONE_OFFSET, value);
					} else {
						Map propMap = new HashMap();
						propMap.put(WebflowConstants.CLIENT_TIMEZONE_OFFSET, value);
						odMapperData.put("PropValues", propMap);
					}
				}
				IODMappingConverter converter = BaseRulesHelper
						.createRule(odmapperName);
				if (htmlContext.getIsDataToUI()) {
					converter.setInputData(odMapperData);
					converter.pushDataToWidget(htmlContext);
				} else {
					converter.setInputData(odMapperData);
					converter.pullDataFromWidget(htmlContext);
					htmlContext.setODMapperData(converter.getOutputData());
				}
			} catch (UIConvertException ex) {
				throw ex;
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("callODMapper odmapper name: " + odmapperName);
			}
			long start = System.currentTimeMillis();
			if (!odContext.isDataToUI()) {
				String uiid = odMapperData.get(IODMappingConverter.UI_WIDGET_ID).toString();
				RefForm refEntity = (RefForm)AjaxActionHelper.getCachedAjaxWidget(uiid, htmlContext);
				odMapperData.put(uiDataParam.getParamName(), refEntity.getCopy());
			}
			ODProcessor processor = new ODProcessor(htmlContext, odmapperName, odContext.getDeepLevel());
			ODEntityContext evaContext = processor.process();
			// save context for the ui form variables evaluation.
			htmlContext.setODMapperContext(evaContext.getUiEntity().getUIID(), evaContext);
			
			long end = System.currentTimeMillis();
			long time = end - start;
			if (logger.isInfoEnabled()) {
				logger.info("od entity execution time: " + time + "ms");
			}
		}
	}

}