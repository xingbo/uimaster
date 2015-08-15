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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.shaolin.bmdp.datamodel.page.PageInType;
import org.shaolin.bmdp.datamodel.page.PageOutType;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.cache.ODPageObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.mappings.ComponentMapping;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODPageContext extends ODContext
{
	private static Logger logger = LoggerFactory.getLogger(ODPageContext.class);
	
	public static final String UIPageEntity_MARK = "UIEntity";
	
	public static final String OUT_NAME = "_outname";
	
    private String htmlPrefix;
    
    private ODPageObject odPageEntityObject;
    
	/**
	 * out node name.
	 */
	private String outName;
	
	private final String pageName;
	
	public ODPageContext(HTMLSnapshotContext htmlContext, String pageName)
    {
        super(htmlContext, true);
        this.pageName = pageName;
    }
    
    /**
     * 1. set local variables and default values.
     * 2. the local variable values gotten by input parameters.
     * 
     * @throws ODProcessException
     * @throws EvaluationException
     * @throws ParsingException
     * @throws ClassNotFoundException
     * @throws RepositoryException 
     */
	public void initContext() throws ODProcessException, EvaluationException, ParsingException, ClassNotFoundException
    {
		this.odPageEntityObject = PageCacheManager.getODPageEntityObject(pageName);
        this.uiEntityName = odPageEntityObject.getUIEntityName();
	    this.htmlPrefix = ""; // page is the top level.

		if(logger.isInfoEnabled()) {
            logger.info("Process UI Page: {}", this.uiEntityName);  
		}
		
		uiEntity = new HTMLReferenceEntityType(htmlContext, htmlPrefix, uiEntityName);
		
		DefaultEvaluationContext defaultEContext = new DefaultEvaluationContext();
    	defaultEContext.setVariableValue("context", htmlContext);
    	defaultEContext.setVariableValue("odContext", this);
    	this.setDefaultEvaluationContext(defaultEContext);
		this.setExternalEvaluationContext(this);
		
		DefaultEvaluationContext localEContext = odPageEntityObject.getLocalEContext();
		localEContext.setVariableValue(UIPageEntity_MARK, uiEntity);
		Map<String, Object> inputData = htmlContext.getODMapperData();
		if (inputData != null && !inputData.isEmpty()) {
			Set<Entry<String, Object>> entries = inputData.entrySet();
			for (Entry<String, Object> entry: entries) {
				localEContext.setVariableValue(entry.getKey(), entry.getValue());
				if(logger.isDebugEnabled()) {
		            logger.debug("Pass the input value into page od variable: " + entry.getKey());  
				}
			}
		}
		this.setEvaluationContextObject(LOCAL_TAG, localEContext);
		this.setEvaluationContextObject(GLOBAL_TAG, defaultEContext);
		if (!this.isDataToUI()) {
			if (AjaxActionHelper.getAjaxContext() == null) {
				AjaxContext.registerPageAjaxContext(uiEntityName, htmlContext.getRequest());
			}
			defaultEContext.setVariableValue(AJAX_UICOMP_NAME, AjaxActionHelper.getAjaxContext());
			this.outName = htmlContext.getRequest().getParameter(ODPageContext.OUT_NAME);
		}
    }
    
	public boolean isEnableProcess() 
    {
    	return odPageEntityObject.isExistOdMapping();
    }
	
	public String getHtmlPrefix() 
	{
		return htmlPrefix;
	}
	
    public PageInType getPageInDescritpor() 
    {
		return odPageEntityObject.getPageInDescritpor();
	}

    public void executePageIn() throws EvaluationException
	{
    	PageInType inType = getPageInDescritpor();
    	if (inType == null) {
    		return;
    	}
    	inType.getServerOperation().evaluate(this);
	}
	
	public void executePageOut(String outNode) throws EvaluationException, ODException
	{
		PageOutType pageOutType = getOutNode();
		if (pageOutType.getServerOperation() != null 
				&& pageOutType.getServerOperation().getExpressionString() != null) {
			pageOutType.getServerOperation().evaluate(this);
		} else {
			executeAllMappings();
		}
	}
    
    public void executeAllMappings() throws ODException {
		List<ComponentMapping> mappings = odPageEntityObject.getAllMappings();
		for (ComponentMapping mapping: mappings) {
			mapping.execute(this);
		}
	}
	
	public void executeMapping(String name) throws ODException {
		List<ComponentMapping> mappings = odPageEntityObject.getAllMappings();
		for (ComponentMapping mapping : mappings) {
			if (mapping.getMappingName().equals(name)) {
				mapping.execute(this);
			}
		}
	}
    
	public String getUiParamName() {
		return UIPageEntity_MARK;
	}
	
    public PageOutType getOutNode() 
    {
		return odPageEntityObject.getOut(this.outName);
	}
    
    public DefaultParsingContext getLocalPContext() 
    {
    	return odPageEntityObject.getLocalPContext();
    }
    
    public String getOutName() 
    {
		return outName;
	}
    
    public String[] getLocalVariableKeys()
    {
    	return odPageEntityObject.getLocalKeys();
    }

    public boolean isInVariable(String varName) {
    	return odPageEntityObject.isInVariable(varName);
    }
    
    public boolean isOutVariable(String varName) {
    	return odPageEntityObject.isOutVariable(varName);
    }
    
    public List<String> getODLocaleConfigs()
    {
    	//TODO:
//        return odPageEntityObject.getPageOdLocaleConfigs();
    	return Collections.emptyList();
    }
    
	public ODObject getODObject() 
	{
		return odPageEntityObject;
	}
	
}
