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
package org.shaolin.uimaster.page.od;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

public abstract class ODContext extends OpExecuteContext {

	private static Logger logger = Logger.getLogger(ODContext.class);

	public static final String DATA_TO_UI = "_DATA_TO_UI";

	public static final String UI_TO_DATA = "_UI_TO_DATA";

	public static final String LOCAL_TAG = "$";

	public static final String GLOBAL_TAG = "@";

	public static final String AJAX_COMP_MAP = "_ajax_page_component_map";

	public static final String AJAX_UICOMP_NAME = "page";

	protected HTMLSnapshotContext htmlContext;

	protected transient HttpServletRequest request;

	/**
	 * HTMLComponentType UI entity.
	 */
	protected HTMLReferenceEntityType uiEntity;

	/**
	 * OD component name.
	 * 
	 * in ui page, the od similar with ui. in ui entity, the od different with
	 * ui.
	 * 
	 */
	protected String odEntityName;

	/**
	 * ui component name.
	 */
	protected String uiEntityName;

	/**
	 * the parameter values were inputed by outside page.
	 */
	protected Map inputParamValues;

	/**
	 * is data to ui operation.
	 */
	protected boolean isDataToUI = true;

	/**
	 * whether is independent od(true). or page od(false)
	 */
	protected boolean isPageOD = true;

	/**
	 * is od base rule mapping.
	 */
	protected boolean isODBaseRule = false;

	/**
	 * define the ui entity is Base type (UIEntity primitive type).
	 */
	protected boolean isODBaseType = false;

	/**
	 * this deepLevel indicates call od levels for debugging od module.
	 */
	private int deepLevel = 0;

	public ODContext(HTMLSnapshotContext htmlContext, boolean isPageOD) {
		this.htmlContext = htmlContext;
		this.isPageOD = isPageOD;
		this.request = htmlContext.getRequest();
		this.isDataToUI = htmlContext.getIsDataToUI();
		this.inputParamValues = htmlContext.getODMapperData();
		if (inputParamValues == null) {
			if (logger.isDebugEnabled())
				logger.debug("Web flow put parameters is empty into page.");
			inputParamValues = Collections.EMPTY_MAP;
		}
	}

	public HTMLSnapshotContext getHtmlContext() {
		return htmlContext;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
	public void addTempUserData(String key, Object value) {
		Object v = request.getSession().getAttribute("_TempUserData");
		if (v != null) {
			 ((HashMap)v).put(key, value);
		} else {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put(key, value);
			request.getSession().setAttribute("_TempUserData", data);
		}
	}

	public Object getTempUserData(String key) {
		Object v = request.getSession().getAttribute("_TempUserData");
		if (v != null) {
			return ((HashMap)v).get(key);
		}
		return null;
	}
	
	public Object removeTempUserData(String key) {
		Object v = request.getSession().getAttribute("_TempUserData");
		if (v != null) {
			return ((HashMap)v).get(key);
		}
		return null;
	}
	
	public String getOdEntityName() {
		return odEntityName;
	}

	public String getUiEntityName() {
		return uiEntityName;
	}
	
	public abstract String getUiParamName();

	public Map<String, Object> getLocalVariableValues() {
		DefaultEvaluationContext evalContext = (DefaultEvaluationContext) this
				.getEvaluationContextObject(LOCAL_TAG);
		if (evalContext != null) {
			return evalContext.getVariableObjects();
		}
		return null;
	}

	public Map getGlobalVariableValues() {
		DefaultEvaluationContext evalContext = (DefaultEvaluationContext) this
				.getEvaluationContextObject(GLOBAL_TAG);
		if (evalContext != null) {
			return evalContext.getVariableObjects();
		}
		return null;
	}

	public Object getLocalVariableValue(String variableName) {
		try {
			EvaluationContext evalContext = this
					.getEvaluationContextObject(LOCAL_TAG);
			if (evalContext != null) {
				return evalContext.getVariableValue(variableName);
			}
			return null;
		} catch (EvaluationException ex) {
			throw new I18NRuntimeException(
					ExceptionConstants.EBOS_ODMAPPER_002, ex,
					new Object[] { variableName });

		}
	}

	public Object getGlobalVariableValue(String variableName) {
		try {
			EvaluationContext evalContext = this
					.getEvaluationContextObject(GLOBAL_TAG);
			if (evalContext != null) {
				return evalContext.getVariableValue(variableName);
			}
			return null;
		} catch (EvaluationException ex) {
			throw new I18NRuntimeException(
					ExceptionConstants.EBOS_ODMAPPER_002, ex,
					new Object[] { variableName });
		}
	}

	public abstract void executeAllMappings() throws ODException;
	
	public abstract void executeMapping(String name) throws ODException;
	
	public HTMLWidgetType getUiEntity() {
		return uiEntity;
	}

	public boolean isPageOD() {
		return isPageOD;
	}

	public boolean isDataToUI() {
		return isDataToUI;
	}

	public boolean isODBaseRule() {
		return isODBaseRule;
	}

	public boolean isODBaseType() {
		return isODBaseType;
	}

	public int increaseDeepLevel() {
		return ++deepLevel;
	}
	
	public int getDeepLevel() {
		return deepLevel;
	}

	public void setDeepLevel(int deepLevel) {
		this.deepLevel = deepLevel;
	}

	public abstract DefaultParsingContext getLocalPContext();

//	public abstract List getODLocaleConfigs();

	public abstract ODObject getODObject();

}
