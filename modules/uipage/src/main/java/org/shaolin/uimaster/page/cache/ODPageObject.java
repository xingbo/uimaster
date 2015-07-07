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
package org.shaolin.uimaster.page.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamScopeType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.DirectComponentMappingType;
import org.shaolin.bmdp.datamodel.page.DynamicUIMappingType;
import org.shaolin.bmdp.datamodel.page.PageInType;
import org.shaolin.bmdp.datamodel.page.PageODMappingType;
import org.shaolin.bmdp.datamodel.page.PageOutType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.od.ODContextHelper;
import org.shaolin.uimaster.page.od.ODPageContext;
import org.shaolin.uimaster.page.od.mappings.ComponentMapping;
import org.shaolin.uimaster.page.od.mappings.DirectComponentMapping;
import org.shaolin.uimaster.page.od.mappings.DynamicUIComponentMapping;
import org.shaolin.uimaster.page.od.mappings.SimpleComponentMapping;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODPageObject extends ODObject implements java.io.Serializable
{
    private static final long serialVersionUID = -1328112393440558301L;
    
    private static Logger logger = LoggerFactory.getLogger(ODPageObject.class);
    
    /**
     * whether is or not exist od mapping in current page entity.
     */
    private boolean isExistOdMapping = true;
    
    /**
     * Page In meta-info.
     */
    private PageInType pageInDescritpor;

	/**
     * Page out meta-infos.
     */
    private List<PageOutType> outs;
    
    /**
     * Declare parameters of OD.
     */
    private List<ParamType> localParamTypes;
	
    private final List<ComponentMapping> compMappings = 
    		new ArrayList<ComponentMapping>();
    
    /**
     * key set.
     */
    private String[] localKeys;
    
    private Map pageOutKeys = new HashMap();
    
    /**
     * whether is or not the out node had been parsed.
     */
    private Map isParseOutNode = new HashMap();
    
    /**
     * last modify time for uipage component.
     */
    private long lastModifyTime;

	public ODPageObject(String name) throws ParsingException, ClassNotFoundException
    {
		super();
		
		this.name = name;
        
		load();
    }
    
	private void clear() 
    {
		this.pageOutKeys.clear();
		this.isParseOutNode.clear();
		
		this.uiEntityName = "";
		this.isExistOdMapping = true;
		this.pageInDescritpor = null;
		this.outs = null;
		this.localParamTypes = null;
		this.localKeys = null;
		
		clearODObject();
    }
	
    private void load() throws ClassNotFoundException, ParsingException 
    {
    	if(logger.isInfoEnabled())
    	{
    		logger.info("Load page od: "+name);  
    	}

        UIPage pageDescriptor = IServerServiceManager.INSTANCE.getEntityManager().getEntity(name, UIPage.class);
        PageODMappingType odDescriptor = pageDescriptor.getODMapping();
    	this.uiEntityName = pageDescriptor.getEntityName();
		if(logger.isDebugEnabled())
    		logger.debug("The page entity name is: "+uiEntityName); 
    	if(odDescriptor == null && pageDescriptor.getIn() == null && pageDescriptor.getOuts() == null )
    	{
    		this.isExistOdMapping = false;
    		logger.warn("This ui page dosen't have exsited od mapping!"); 
    	}
    		
    	this.pageInDescritpor = pageDescriptor.getIn();
    	this.outs = pageDescriptor.getOuts();
    	this.localParamTypes = odDescriptor.getDataEntities();
    	this.localKeys = new String[localParamTypes.size()];
    	for (int i = 0; i < localParamTypes.size(); i++)
		{
    		localKeys[i] = localParamTypes.get(i).getName();
		}
		
    	if(logger.isDebugEnabled())
    	{
    		logger.debug("Initial parsing context.");  
    		logger.debug("Local parsing context["+ODContext.LOCAL_TAG+"] variable list:");
    	}
        DefaultParsingContext localPContext = ODContextHelper.getParsingContext( localParamTypes );
        localPContext.setVariableClass(ODPageContext.UIPageEntity_MARK, HTMLReferenceEntityType.class);
        
        //DefaultParsingContext inPagePContext = ODContextHelper.getParsingContext( inParamTypes );
        DefaultParsingContext defaultPContext = new DefaultParsingContext();
        defaultPContext.setVariableClass("context", HTMLSnapshotContext.class);
        defaultPContext.setVariableClass("odContext", ODContext.class);
        
        opContext.setDefaultParsingContext(defaultPContext);
        opContext.setParsingContextObject(ODContext.LOCAL_TAG, localPContext);
        opContext.setParsingContextObject(ODContext.GLOBAL_TAG, defaultPContext);
        opContext.setExternalParseContext(opContext);
        
        parseCompMapping(odDescriptor);
        
        parseInNode();
        
        if(logger.isDebugEnabled())
        	logger.debug("---------->Parse od component end.");  
    }
    
    private void parseCompMapping(PageODMappingType odDescriptor) throws ParsingException {
		List<ComponentMappingType> tempCompMappings = odDescriptor.getComponentMappings();
		for (ComponentMappingType compMapping: tempCompMappings)
    	{
			if(logger.isDebugEnabled()) {
	    		logger.debug("Parse component mapping: {}", compMapping.getName());  
			}
			
			if (compMapping instanceof SimpleComponentMappingType) {
				SimpleComponentMapping simpleCM = 
						new SimpleComponentMapping((SimpleComponentMappingType)compMapping);
				simpleCM.parse(opContext, this);
				compMappings.add(simpleCM);
			} else if (compMapping instanceof DynamicUIMappingType) {
				DynamicUIComponentMapping dynamicCM = 
						new DynamicUIComponentMapping((DynamicUIMappingType)compMapping);
				dynamicCM.parse(opContext, this);
				compMappings.add(dynamicCM);
			} else {
				DirectComponentMapping directCM = 
						new DirectComponentMapping((DirectComponentMappingType)compMapping);
				directCM.parse(opContext, this);
				compMappings.add(directCM);
			}
    	}
	}
    
	private void parseInNode() throws ParsingException 
	{
		if(pageInDescritpor == null) {
			return;
		}
		
    	ExpressionType serverOperation = pageInDescritpor.getServerOperation();
    	if (serverOperation == null) {
    		ExpressionType dataToUIExpr = new ExpressionType();
    		dataToUIExpr.setExpressionString("{@odContext.executeAllMappings();}");
    		pageInDescritpor.setServerOperation(dataToUIExpr);
    	}
		if(logger.isDebugEnabled()) {
    		logger.debug("Parse op type: {}", pageInDescritpor.getServerOperation().getExpressionString()); 
		}
		pageInDescritpor.getServerOperation().parse(opContext);
	}

	public boolean isExistOdMapping() {
		return isExistOdMapping;
	}
	
	public String[] getLocalKeys()
	{
		return localKeys;
	}
    
	public boolean isOutVariable(String varName) {
		for (ParamType param : localParamTypes) {
			if (varName.equals(param.getName())
					&& (param.getScope() == ParamScopeType.IN_OUT 
						|| param.getScope() == ParamScopeType.OUT )) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isInVariable(String varName) {
		for (ParamType param : localParamTypes) {
			if (varName.equals(param.getName())
					&& (param.getScope() == ParamScopeType.IN_OUT 
						|| param.getScope() == ParamScopeType.IN)) {
				return true;
			}
		}
		return false;
	}
	
    public List<ComponentMapping> getAllMappings(){
    	return this.compMappings;
    }
    
    public List<PageOutType> getOuts() {
		return outs;
	}
    
    public PageOutType getOut(String outName)
    {
    	for (PageOutType out: outs)
        {
			if(out.getName().equals(outName))
				return out;
        }
    	throw new I18NRuntimeException(ExceptionConstants.EBOS_ODMAPPER_059,new Object[]{outName});
	}

	public PageInType getPageInDescritpor() {
		return pageInDescritpor;
	}
	
	private void checkUpdate() throws ParsingException, ClassNotFoundException
    {
		if (!WebConfig.enableHotDeploy()) {
			return;
		}
		long currentTime = System.currentTimeMillis();
    	refresh();
    }
	
	synchronized private void refresh() throws ParsingException, ClassNotFoundException
    {
		//notice: if two threads access in here,
		//the first thread change lastCheckTime, the second thread will skip operation.
		File compFile = null;
        long modifyTime = compFile.lastModified();
        if ( modifyTime != lastModifyTime )
        {
            if ( logger.isInfoEnabled() )
                logger.info("the page od: " + name + " has been changed, start to reload it.");
            
            this.clear();
            this.load();
            lastModifyTime = modifyTime;
        }
    }
    
    public DefaultEvaluationContext getLocalEContext() throws EvaluationException, ParsingException {
    	
    	if(logger.isDebugEnabled())
			logger.info("----->Initial 'page od'["+ODContext.LOCAL_TAG+"] variables.");
    	
        Map localVariableValues = new HashMap(); 
		for (ParamType localVar: localParamTypes)
    	{
    		String key = localVar.getName();
    		Object value = ODContextHelper.evalVariableExpression(localVar);
    		localVariableValues.put(key, value);
    		
    		if(logger.isDebugEnabled())
    		{
    			StringBuffer sb = new StringBuffer();
    			sb.append("Local variable name ");
    			sb.append(key);
    			if(localVar.getDefault() != null)
    			{
    				sb.append(", default value expression: ");
        			sb.append(localVar.getDefault().getExpressionString());
    			}
    			sb.append(", variable value: ");
    			sb.append(value);
    			logger.debug(sb.toString());
    		}
    	}
		return ODContextHelper.getEvalContext(localParamTypes, localVariableValues);
	}
	
}
