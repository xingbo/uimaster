package org.shaolin.uimaster.page.od;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.ODMappingType;
import org.shaolin.bmdp.exceptions.BusinessOperationException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.cache.ODFormObject;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.od.mappings.ComponentMapping;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;

public class ODEntityContext extends ODContext 
{
	private static Logger logger = Logger.getLogger(ODEntityContext.class);
	
	/**
     * OD component meta-info.
     */
    private ODMappingType odDescriptor;
    
    private ODFormObject odEntityObject;
    
    public ODEntityContext( String odEntityName, HTMLSnapshotContext htmlContext )
    {
        super( htmlContext, false );
        this.odEntityName = odEntityName;
    }
    
    /**
     * 
     * @throws ODProcessException
     * @throws EvaluationException
     * @throws ParsingException
     * @throws BusinessOperationException
     * @throws RepositoryException
     * @throws ClassNotFoundException
     */
    public void initContext() throws ODProcessException, EvaluationException, ParsingException, BusinessOperationException, ClassNotFoundException
    {
    	if(logger.isInfoEnabled())
    		logger.info("Initialize UI Form: " + odEntityName + 
    				",htmlPrefix: " + htmlContext.getHTMLPrefix());	
		
    	this.odEntityObject = PageCacheManager.getODFormObject(odEntityName);
		this.uiEntityName = odEntityObject.getUIEntityName();
		this.isODBaseRule = odEntityObject.isODBaseRule();
		this.isODBaseType = odEntityObject.isODBaseType();
		this.odDescriptor = odEntityObject.getODEntity();
    	
    	int debugCount = 0;
    	DefaultEvaluationContext localEContext = odEntityObject.getLocalEContext();
    	if( !inputParamValues.isEmpty() )
    	{
    		if(logger.isDebugEnabled())
    			logger.info("----->The 'page in' is receiving values from web flow parameters:");
    		String[] keys = odEntityObject.getParamKeys();
    		for (int i = 0; i < keys.length; i++)
    		{
    			String keyName = keys[i];
    			if( inputParamValues.containsKey(keyName) && inputParamValues.get(keyName) != null )
    			{
    				localEContext.setVariableValue(keyName, inputParamValues.get(keyName));
    				if(logger.isDebugEnabled())
    				{
    					Object obj = inputParamValues.get(keyName);
    					StringBuffer sb = new StringBuffer();
    					sb.append("Input parameter[");
    					sb.append(debugCount);
    					sb.append("]: ");
    					sb.append(keyName);
    					sb.append(", value: ");
    					sb.append(obj);
    					sb.append(", class: ");
    					sb.append(obj.getClass());
    					logger.debug(sb.toString());
    					debugCount++;
    				}
    			}
    		}
    	}
    	
		Set keySet = inputParamValues.keySet();
		for (Object key: keySet) {
			Object value = inputParamValues.get(key);
			if (value instanceof HTMLReferenceEntityType) {
				uiEntity = (HTMLReferenceEntityType)value;
				break;
			}
		}
		if (uiEntity == null || !HTMLSnapshotContext.isInstance(uiEntityName, uiEntity))
			throw new ODProcessException(ExceptionConstants.EBOS_ODMAPPER_049,new Object[]{uiEntity.getUIEntityName(), uiEntityName});
    	if(logger.isDebugEnabled())
            logger.debug("UI Reference Entity uiid: "+this.uiEntity.getId());
    	
    	DefaultEvaluationContext defaultEContext = new DefaultEvaluationContext();
    	defaultEContext.setVariableValue("context", htmlContext);
    	defaultEContext.setVariableValue("odContext", this);
    	this.setDefaultEvaluationContext(defaultEContext);
    	this.setEvaluationContextObject(GLOBAL_TAG, defaultEContext);
    	
    	this.setEvaluationContextObject(LOCAL_TAG, localEContext);
    	this.setExternalEvaluationContext(this);
    	if( !isDataToUI )
    	{
    		DefaultEvaluationContext globalContext = new DefaultEvaluationContext();
    		globalContext.setVariableValue("context", htmlContext);
    		globalContext.setVariableValue("odContext", this);
    		globalContext.setVariableValue(AJAX_UICOMP_NAME, this);
    		this.setEvaluationContextObject(GLOBAL_TAG, defaultEContext);
    	}
		
    	if(logger.isInfoEnabled())
		{
			String[] keys = odEntityObject.getParamKeys();
			if(keys != null)
			{
				EvaluationContext dEContext = this.getEvaluationContextObject(ODContext.LOCAL_TAG);
				for (int i = 0; i < keys.length; i++ )
				{
					try
					{
						Object variableValue = dEContext.getVariableValue(keys[i]);
						if(variableValue == null)
							logger.info("Local variable["+keys[i]+"] value is null.");
					}catch(Exception e){}
				}
			}
		}
    }
	
	public String evalDataLocale() throws EvaluationException
	{
		//TODO:
//	    return ODContextHelper.evalDataLocale(getPageInDescritpor().getDataLocale(),
//	            this, ODContext.GLOBAL_TAG, odPageEntityObject.getPageInLocaleConfigs());
		return "";
	}
	
	public void executeAllMappings() throws ODException {
		List<ComponentMapping> mappings = odEntityObject.getAllMappings();
		for (ComponentMapping mapping: mappings) {
			mapping.execute(this);
		}
	}
	
	public void executeMapping(String name) throws ODException {
		List<ComponentMapping> mappings = odEntityObject.getAllMappings();
		for (ComponentMapping mapping : mappings) {
			if (mapping.getMappingName().equals(name)) {
				mapping.execute(this);
			}
		}
	}
	
	public String getUiParamName() {
		return odEntityObject.getUiParamName();
	}
	
	public void executeDataToUI() throws EvaluationException
	{
		odDescriptor.getDataToUIMappingOperation().evaluate(this);
	}
	
	public void executeUITOData() throws EvaluationException
	{
		odDescriptor.getUIToDataMappingOperation().evaluate(this);
	}
	
	public DefaultParsingContext getLocalPContext() 
    {
    	return odEntityObject.getLocalPContext();
    }
    
	public ODObject getODObject() 
	{
		return odEntityObject;
	}
}
