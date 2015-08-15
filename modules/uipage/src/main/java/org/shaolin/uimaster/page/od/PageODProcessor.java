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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.PageOutType;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.PageWidgetsContext;
import org.shaolin.uimaster.page.cust.IODPagePlugin;
import org.shaolin.uimaster.page.cust.ODPagePlugin;
import org.shaolin.uimaster.page.exception.ODProcessException;

/**
 * Processing Data to UI/UI to Data operation.
 * 
 * page in steps:
 *  1. set the variables of page in were evaluated by web flow parameters(by context).
 *  2. set the variables of page od were evaluated by page in parameters(by context).
 *  3. execute server operations(processor).
 *  
 */
public class PageODProcessor 
{
	private static final Logger logger = Logger.getLogger(PageODProcessor.class);
	
	private HTMLSnapshotContext htmlContext;
	
	private String pageName ;
	
	public PageODProcessor(HTMLSnapshotContext context, String pageName) 
	{
		this.htmlContext = context;
		this.pageName = pageName;
	}

	public EvaluationContext process()throws ODProcessException 
	{
		try 
		{
			if(logger.isTraceEnabled())
	        {
				logger.trace("\n\n\n");
				logger.trace("Start ui page entity processor.");
	        	htmlContext.printHTMLAttributeValues();
	        }
			IODPagePlugin plugger = new ODPagePlugin();
			ODPageContext odPageContext = new ODPageContext( htmlContext, pageName );
			odPageContext.setDeepLevel(0);
			odPageContext.initContext();
			
			if(!odPageContext.isEnableProcess())
			{
				if(logger.isDebugEnabled())
					logger.debug("OD mapping dosen't have configed, stop to process ui.");
				return new DefaultEvaluationContext();
			}
			if ( !odPageContext.getUiEntityName().equals(htmlContext.getODMapperName()) ) {
				throw new ODProcessException(ExceptionConstants.EBOS_ODMAPPER_052,
						new Object[]{htmlContext.getODMapperName(), odPageContext.getUiEntityName()});
			}
			
			if ( odPageContext.isDataToUI() )
			{
				PageWidgetsContext uiPageContext = new PageWidgetsContext(pageName);
				uiPageContext.loadComponent(htmlContext, "", pageName, false);
				htmlContext.setPageWidgetContext(uiPageContext);
				if(logger.isDebugEnabled()) {
					logger.debug("----->Execute page in.");
				}
				try
				{
					if (logger.isDebugEnabled()) {
						String[] keys = odPageContext.getLocalVariableKeys();
						if (keys != null) {
							EvaluationContext dEContext = odPageContext
									.getEvaluationContextObject(ODContext.LOCAL_TAG);
							for (int i = 0; i < keys.length; i++) {
								try {
									Object variableValue = dEContext
											.getVariableValue(keys[i]);
									if (variableValue == null) {
										logger.debug("the local variable["
												+ keys[i] + "] value is null.");
									}
								} catch (Exception e) {
								}
							}
						}
					}
    				
    				odPageContext.executePageIn();
    				plugger.postInExecute(odPageContext, htmlContext);
    				
    				htmlContext.setHTMLPrefix( odPageContext.getHtmlPrefix() );
    				htmlContext.setFormName(odPageContext.getUiEntityName());
    			}
    			finally
    			{
    			    LocaleContext.popDataLocale();
    			}
			}
			else 
			{
				try
				{
    				PageOutType outType = odPageContext.getOutNode();
					if (logger.isDebugEnabled()) {
						String[] keys = odPageContext.getLocalVariableKeys();
						if (keys != null) {
							EvaluationContext dEContext = odPageContext
									.getEvaluationContextObject(ODContext.LOCAL_TAG);
							for (int i = 0; i < keys.length; i++) {
								try {
									Object variableValue = dEContext
											.getVariableValue(keys[i]);
									if (variableValue == null)
										logger.debug("the local variable["
												+ keys[i] + "] value is null.");
								} catch (Exception e) {
								}
							}
						}
					}
					if(logger.isDebugEnabled()) {
						logger.debug("----->Execute page out: " + outType.getName());
					}
					odPageContext.executePageOut(outType.getName());
					plugger.postOutExecute(odPageContext, htmlContext);
    				
    				Map<String, Object> outResult = new HashMap<String, Object>();
    				String[] keys = odPageContext.getLocalVariableKeys();
					if (keys != null) {
						EvaluationContext dEContext = odPageContext
								.getEvaluationContextObject(ODContext.LOCAL_TAG);
						for (String key: keys) {
							if (!odPageContext.isOutVariable(key)) {
								continue;
							}
							try {
								
								Object variableValue = dEContext
										.getVariableValue(key);
								outResult.put(key, variableValue);
							} catch (Exception e) {
							}
						}
					}
    				
                    htmlContext.setHTMLPrefix(odPageContext.getHtmlPrefix());
                    htmlContext.setFormName(odPageContext.getUiEntityName());
    				htmlContext.setODMapperData(outResult);
    			}
    			finally
    			{
    			    LocaleContext.popDataLocale();
    			}
			}
			
			if(logger.isTraceEnabled())
	        {
				logger.info("UI page entity processor stop.\n\n");
	        	htmlContext.printHTMLAttributeValues();
	        }
			return odPageContext;
		}
		catch(Throwable e)
		{
			throw new ODProcessException(ExceptionConstants.EBOS_ODMAPPER_067, e, new Object[]{pageName});
		}
	}

}
