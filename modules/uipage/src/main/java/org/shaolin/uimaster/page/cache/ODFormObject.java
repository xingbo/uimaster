package org.shaolin.uimaster.page.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.DirectComponentMappingType;
import org.shaolin.bmdp.datamodel.page.DynamicUIMappingType;
import org.shaolin.bmdp.datamodel.page.ODMappingType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.od.ODContextHelper;
import org.shaolin.uimaster.page.od.mappings.ComponentMapping;
import org.shaolin.uimaster.page.od.mappings.DirectComponentMapping;
import org.shaolin.uimaster.page.od.mappings.DynamicUIComponentMapping;
import org.shaolin.uimaster.page.od.mappings.SimpleComponentMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODFormObject extends ODObject implements java.io.Serializable
{
    private static final long serialVersionUID = -1328112393440558300L;
    
    private static Logger logger = LoggerFactory.getLogger(ODFormObject.class);
    
    /**
     * Declare parameters of OD.
     */
    private List<ParamType> localParamTypes;
	
    /**
     * local variable names.
     */
    private String[] paramKeys;
    
    private final List<ComponentMapping> compMappings = 
    		new ArrayList<ComponentMapping>();
    
	/**
     * od base rule suffix.
     */
    public static final String OD_BASE_RULE_UITYPE = "org.shaolin.uimaster.page.od.rules";
    
    public static final String OD_BASE_UITYPE = "org.shaolin.uimaster.page.html";
    
    private ODMappingType odDescriptor;
    
    public final String UIEntity_MARK ;
    
    /**
     * is od base rule mapping.
     */
    private boolean isODBaseRule = false;
    
    /**
     * define the ui entity is Base type (UIEntity primitive type).
     */
    protected boolean isODBaseType = false;
    
    /**
     * last modify time for od component.
     */
    private long lastModifyTime;

    
    private File compFile = null;
    
	public ODFormObject(String name) throws ParsingException, ClassNotFoundException
    {
		super();
		
		this.name = name;
        this.UIEntity_MARK = "UIEntity_" + name.substring(name.lastIndexOf('.') + 1);
        
		load();
    }
	
	private void clear() 
    {
		this.localParamTypes = null;
		this.paramKeys  = null;
		this.isODBaseRule = false;
		this.isODBaseType = false;
		
		clearODObject();
    }
    
	private void load() throws ClassNotFoundException, ParsingException 
    {
    	try
    	{
	    	if(logger.isInfoEnabled())
	    	{
	    		logger.info("Load od component: "+name);  
	    	}

	    	UIEntity entity = IServerServiceManager.INSTANCE.getEntityManager()
	    			.getEntity(name, UIEntity.class);
	    	odDescriptor = entity.getMapping();
	    	if (odDescriptor == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No UI mapping information defined.");
				}
	    		return;
	    	}
	    	
			uiEntityName = name;
			if(logger.isDebugEnabled())
	    		logger.debug("The ui entity name is: "+uiEntityName); 
	    	
    		if(uiEntityName.startsWith(OD_BASE_UITYPE))
			{
				this.isODBaseType = true;
				if(logger.isDebugEnabled())
	    			logger.debug( "This is base type ui entity.");
			}
			
			localParamTypes = mergeParamTypes(odDescriptor);
			paramKeys = new String[localParamTypes.size()];
	    	for (int i = 0; i < localParamTypes.size(); i++)
	    	{
	    		paramKeys[i] = localParamTypes.get(i).getName();
	    	}
	    	
	    	if(logger.isDebugEnabled())
	    		logger.debug("Initial ["+ODContext.LOCAL_TAG+"] parsing context and default parsing context.");  
	    	
	    	DefaultParsingContext localPContext = ODContextHelper.getParsingContext(localParamTypes);
	    	
	    	DefaultParsingContext globalPContext = new DefaultParsingContext();
	    	globalPContext.setVariableClass(ODContext.AJAX_UICOMP_NAME, ODContext.class);
	    	globalPContext.setVariableClass("context", HTMLSnapshotContext.class);
	    	globalPContext.setVariableClass("odContext", ODContext.class);
	    	
	    	opContext.setDefaultParsingContext(globalPContext);
	    	opContext.setParsingContextObject(ODContext.LOCAL_TAG, localPContext);
	    	opContext.setParsingContextObject(ODContext.GLOBAL_TAG, globalPContext);
	    	opContext.setExternalParseContext(opContext);
	    	
	        parseOps(odDescriptor);
	        
	        //parse data locale expression
            ExpressionType dataLocaleExpr = odDescriptor.getDataLocale();
            if (dataLocaleExpr != null)
            {
                dataLocaleExpr.parse(opContext);
            }
	        
	        if(logger.isDebugEnabled())
	        	logger.debug("---------->Parse od component end.");  
	    }
    	catch (ParsingException e) 
		{
			throw e;
		}
    }
    
	private void parseOps(ODMappingType odDescriptor) throws ParsingException, ClassNotFoundException 
	{
		List<ComponentMappingType> tempCompMappings = odDescriptor.getComponentMappings();
		for (ComponentMappingType compMapping: tempCompMappings)
    	{
			if(logger.isDebugEnabled())
	    		logger.debug("Parse component mapping: {}", compMapping.getName()); 
			
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
		
		if (odDescriptor.getDataToUIMappingOperation() == null) {
			ExpressionType dataToUIExpr = new ExpressionType();
			dataToUIExpr.setExpressionString("{@odContext.executeAllMappings();}");
			odDescriptor.setDataToUIMappingOperation(dataToUIExpr);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Parse data to ui operation: {}", 
					odDescriptor.getDataToUIMappingOperation().getExpressionString());  
		}
		odDescriptor.getDataToUIMappingOperation().parse(opContext);
		
		if (odDescriptor.getUIToDataMappingOperation() == null) {
			ExpressionType uiToDataExpr = new ExpressionType();
			uiToDataExpr.setExpressionString("{@odContext.executeAllMappings();}");
			odDescriptor.setUIToDataMappingOperation(uiToDataExpr);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Parse ui to data operation: {}",
					odDescriptor.getUIToDataMappingOperation().getExpressionString());  
		}
		odDescriptor.getUIToDataMappingOperation().parse(opContext);
    	
	}

    private List<ParamType> mergeParamTypes(ODMappingType odDescriptor) 
	{
    	int size = odDescriptor.getUIEntities().size() + odDescriptor.getDataEntities().size();
    	List<ParamType> paramTypes = new ArrayList<ParamType>(size);
    	
    	ParamType uiEntity = new ParamType();
    	uiEntity.setCategory(VariableCategoryType.UI_ENTITY);
    	uiEntity.setName(UIEntity_MARK);
    	TargetEntityType target = new TargetEntityType();
    	target.setEntityName(this.uiEntityName);
    	uiEntity.setType(target);
    	
    	paramTypes.add(uiEntity);
    	paramTypes.addAll(odDescriptor.getDataEntities());
		return paramTypes;
	}
	
    public List<ComponentMapping> getAllMappings(){
    	return this.compMappings;
    }
    
	public ODMappingType getODEntity() {
		return this.odDescriptor;
	}
	
    public boolean isODBaseRule() {
		return isODBaseRule;
	}

    public boolean isODBaseType()
    {
    	return isODBaseType;
    }
    
    public String[] getParamKeys() {
		return paramKeys;
	}
    
    public String getUiParamName() {
		return UIEntity_MARK;
	}
	
	public DefaultEvaluationContext getLocalEContext() throws EvaluationException, ParsingException 
	{
		if(logger.isDebugEnabled())
			logger.debug("----->Initial 'UI Param'["+ODContext.LOCAL_TAG+"] variables and 'Data Params'["+ODContext.LOCAL_TAG+"] variables.");
		
	    Map<String, Object> localVariableValues = new HashMap<String, Object>(); 
		for (ParamType localParamType : localParamTypes) {
    		String key = localParamType.getName();
    		Object value = ODContextHelper.evalVariableExpression(localParamType);
    		
    		localVariableValues.put(key, value);
    		if(logger.isDebugEnabled())
    		{
    			StringBuffer sb = new StringBuffer();
    			sb.append("Local variable name: ");
    			sb.append(key);
    			if(localParamType.getDefault() != null)
    			{
    				sb.append(", default value expression: ");
        			sb.append(localParamType.getDefault().getExpressionString());
    			}
    			sb.append(", variable value: ");
    			sb.append(value);
    			logger.debug(sb.toString());
    		}
    	}
		return ODContextHelper.getEvalContext(localParamTypes, localVariableValues);
	}

}
