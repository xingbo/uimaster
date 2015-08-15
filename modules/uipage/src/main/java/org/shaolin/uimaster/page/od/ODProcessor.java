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

import org.apache.log4j.Logger;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.PageWidgetsContext;
import org.shaolin.uimaster.page.cust.IODEntityPlugin;
import org.shaolin.uimaster.page.cust.ODEntityPlugin;
import org.shaolin.uimaster.page.exception.ODEntityProcessException;

/**
 * Processing Data to UI/UI to Data operation.
 * 
 */
public class ODProcessor 
{
	private static final Logger logger = Logger.getLogger(ODProcessor.class);
	
	private HTMLSnapshotContext htmlContext;

	private String odEntityName;
	
	private int deepLevel;
	
	public ODProcessor(HTMLSnapshotContext context, String odEntityName, int deepLevel) 
	{
		this.htmlContext = context;
		this.odEntityName = odEntityName;
		this.deepLevel = deepLevel;
	}

	public ODEntityContext process() throws ODEntityProcessException 
	{
		try 
		{
			IODEntityPlugin plugger = new ODEntityPlugin(); 
			ODEntityContext odContext = new ODEntityContext(odEntityName, htmlContext);
			odContext.setDeepLevel(++deepLevel);//next deep level.
			
			if(logger.isDebugEnabled())
			{
				logger.debug("\n\n");
				logger.debug("Start od entity processor( **[the depth of level is "+odContext.getDeepLevel()+".]** ).");
			}
			
			LocaleContext.pushDataLocale(odContext.evalDataLocale());
			
			odContext.initContext();
			
			String uientity = odContext.getUiEntityName();
			String uiid = odContext.getUiEntity().getUIID();
			
			if ( odContext.isDataToUI() )
			{
				if (htmlContext.getPageWidgetContext() == null) {
					// only does uientity mapping.
					PageWidgetsContext uiPageContext = new PageWidgetsContext(uientity);
					htmlContext.setPageWidgetContext(uiPageContext);
				}
				htmlContext.getPageWidgetContext().loadComponent(
						htmlContext, uiid + ".", uientity, true);
				if(logger.isDebugEnabled()) {
		    		logger.debug("----->Processing od entity data to ui...");
				}
				
				plugger.preData2UIExecute(odContext, htmlContext);
				odContext.executeDataToUI();
				plugger.postData2UIExecute(odContext, htmlContext);
			}
			else 
			{
				if(logger.isDebugEnabled()) {
		    		logger.debug("----->Processing od entity ui to data...");
				}
				
				plugger.preUI2DataExecute(odContext, htmlContext);
				odContext.executeUITOData();
		        plugger.postUI2DataExecute(odContext, htmlContext);
		        
				htmlContext.setODMapperData(odContext.getLocalVariableValues());	
			}
			
			if(logger.isDebugEnabled()) {
	    		logger.debug("OD entity processor stop ( **[the depth of level is "+odContext.getDeepLevel()+".]** ).");
			}
			return odContext;
		}
		catch(Throwable e)
		{
			throw new ODEntityProcessException(
			        ExceptionConstants.EBOS_ODMAPPER_068, e, new Object[]{odEntityName});
		}
		finally 
		{
			LocaleContext.popDataLocale();
		}
	}
	
}
