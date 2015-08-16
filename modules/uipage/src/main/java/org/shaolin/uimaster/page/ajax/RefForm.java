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
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.PageDispatcher;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.cache.ODFormObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.od.ODProcessor;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Code Example for uientity:
 * <br>
 * <br>Panel rootPanel = (Panel)@page.getElement("Form");
 * <br>String ui = "bmiasia.ebos.appbase.test.uientity.EmployeeH";
 * <br>RefForm refEntity = new RefForm("refEntity1",ui);
 * <br>rootPanel.append(refEntity);
 * <br>
 * <br>
 * Code Example for OD mapping when data to ui:
 * <br>
 * <br>Panel rootPanel = (Panel)@page.getElement("Form");
 * <br>String ui = "bmiasia.ebos.appbase.test.uientity.EmployeeH";
 * <br>String od = "bmiasia.ebos.appbase.test.od.EmployeeH";
 * <br>java.util.Map map = new java.util.HashMap();
 * <br>bmiasia.ebos.appbase.test.be.Employee employee = new bmiasia.ebos.appbase.test.be.Employee();
 * <br>employee.setName("ddddddddddddd");
 * <br>map.put("Employee",employee);
 * <br>RefEntity refEntity = new RefEntity("refEntity1",ui,od,map);
 * <br>rootPanel.append(refEntity);
 * <br>
 * <br>
 * if you want to get value when ui to data, you must add a SetVariable action in Server Operation
 * which is Page Out or which is UI2Data of the od component,such as:<br>
 * $name = @page.getElement('name');//get value of refEntity1's name. put this script in bmiasia.ebos.appbase.test.od.EmployeeH component.
 *
 */
public class RefForm extends Container implements Serializable
{
    private static final long serialVersionUID = -1744731434666233557L;

    private static Logger logger = LoggerFactory.getLogger(RefForm.class);

    private HTMLReferenceEntityType copy;
    
    private Panel form;

    private String html;

    private String uiid;
    
    private Map functionReconfigurationMap;
    
    private Map propertyReconfigurationMap;
    
    private Map variableReconfigurationMap;

    private Map inputParams;

    public RefForm(String uiid, String uiEntityName)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, uiEntityName, new CellLayout());
        this.uiid = uiid;
        this.setListened(true);
    }

    /**
     *
     * @param uiid
     * @param uiEntityName
     * @param odMapperName
     * @param inputParams
     *
     * data to ui.
     *
     * @throws Exception
     */
    public RefForm(String uiid, String uiEntityName, Map inputParams)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, uiEntityName, new CellLayout());

        this.inputParams = inputParams;
        this.uiid = AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid;

        this.setListened(true);
    }

    public RefForm(String id, String uiEntityName, Layout layout)
    {
        super(id, layout);
        setUIEntityName(uiEntityName);
    }

    public void setCopy(HTMLReferenceEntityType copy) {
    	this.copy = copy;
    }
    
    public HTMLReferenceEntityType getCopy() {
    	return this.copy;
    }
    
    public void setForm(Panel form)
    {
        this.form = form;
    }

    public Panel getForm()
    {
        return form;
    }

    /**
     * @return the functionReconfigurationMap
     */
    public Map getFunctionReconfigurationMap()
    {
        return functionReconfigurationMap;
    }

    /**
     * @param functionReconfigurationMap the functionReconfigurationMap to set
     */
    public void setFunctionReconfigurationMap(Map functionReconfigurationMap)
    {
        this.functionReconfigurationMap = functionReconfigurationMap;
    }

    /**
     * @return the propertyReconfigurationMap
     */
    public Map getPropertyReconfigurationMap()
    {
        return propertyReconfigurationMap;
    }

    /**
     * @param propertyReconfigurationMap the propertyReconfigurationMap to set
     */
    public void setPropertyReconfigurationMap(Map propertyReconfigurationMap)
    {
        this.propertyReconfigurationMap = propertyReconfigurationMap;
    }

    /**
     * @return the variableReconfigurationMap
     */
    public Map getVariableReconfigurationMap()
    {
        return variableReconfigurationMap;
    }

    /**
     * @param variableReconfigurationMap the variableReconfigurationMap to set
     */
    public void setVariableReconfigurationMap(Map variableReconfigurationMap)
    {
        this.variableReconfigurationMap = variableReconfigurationMap;
    }

    public Map ui2Data()
    {
    	return ui2Data(this.inputParams);
    }
    
    public Map ui2Data(Map inputParams)
    {
        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("[ui2Data] uientity: "+this.getUIEntityName());
            }
            AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
            HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(ajaxContext.getRequest());
            htmlContext.setFormName(this.getUIEntityName());
            htmlContext.setIsDataToUI(false);//Don't set prefix in here.
            htmlContext.setAjaxWidgetMap(AjaxActionHelper.getFrameMap(ajaxContext.getRequest()));
            htmlContext.setHTMLPrefix("");

            ODFormObject odEntityObject = PageCacheManager.getODFormObject(this.getUIEntityName());
            HTMLReferenceEntityType newReferObject = new HTMLReferenceEntityType(htmlContext, this.getId(), this.getUIEntityName());
            newReferObject.setPrefix("");
            inputParams = (inputParams == null)?new HashMap():inputParams;
            inputParams.put(odEntityObject.getUiParamName(), newReferObject);
            htmlContext.setODMapperData(inputParams);
            callODMapper(htmlContext, this.getUIEntityName());

            Map referenceEntityMap = new HashMap();
            htmlContext.setRefEntityMap(referenceEntityMap);
            Map result = htmlContext.getODMapperData();
            if(logger.isDebugEnabled())
            {
                if(result != null)
                {
                    logger.debug("OD Mapping Result: "+result.toString());
                }
                else
                {
                    logger.debug("OD Mapping Result is null!");
                }
            }
            return result;
        }
        catch(Exception ex)
        {
			throw new IllegalStateException("Call UI[" + this.getUIEntityName()
					+ "] to Data error: " + ex.getMessage(), ex);
        }
    }

    public String getUiid()
    {
        return this.uiid;
    }

    public String generateJS()
    {
    	StringBuffer sb = new StringBuffer();
        try {
        	AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        	StringWriter writer = new StringWriter();
        	HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(ajaxContext.getRequest(), writer);
        	htmlContext.setIsDataToUI(true);
        	htmlContext.setFormName(this.getUIEntityName());
        	UIFormObject formObject = PageCacheManager.getUIFormObject(this.getUIEntityName());
        	formObject.getJSPathSet(htmlContext, sb, Collections.emptyMap());
		} catch (Exception ex) {
			logger.error("Error in building up structure for entity: " + uiid, ex);
		} 
    	
        sb.append("defaultname.").append(this.getId());
        sb.append(" = new ").append(getJsName()).append("('");
        sb.append(this.getId()).append(".');");

        return sb.toString();
    }
    
    public String generateJSWithoutJsPath()
    {
    	StringBuffer sb = new StringBuffer();
        sb.append("defaultname.").append(this.getId());
        sb.append(" = new ").append(getJsName()).append("('");
        sb.append(this.getId()).append(".');");

        return sb.toString();
    }

    private String getJsName()
    {
        return this.getUIEntityName().replace('.', '_');
    }

    public String generateHTML()
    {
        if (html == null)
        {
            buildUpRefEntity();
        }
        return html == null ? "" : html;
    }

    void buildUpRefEntity()
    {
        try
        {
            AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
            StringWriter writer = new StringWriter();
            HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(ajaxContext.getRequest(), writer);
            htmlContext.setIsDataToUI(true);
            htmlContext.setFormName(this.getUIEntityName());
            ODFormObject odEntityObject = PageCacheManager.getODFormObject(this.getUIEntityName());
            HTMLReferenceEntityType newReferObject = new HTMLReferenceEntityType(htmlContext,
                    this.getId(), this.getUIEntityName());
            if (inputParams == null)
            {
                inputParams = new HashMap();
            }
            inputParams.put(odEntityObject.getUiParamName(), newReferObject);
            htmlContext.setHTMLPrefix("");
            htmlContext.setODMapperData(inputParams);
            callODMapper(htmlContext, this.getUIEntityName());
            Map referenceEntityMap = new HashMap();
            htmlContext.setRefEntityMap(referenceEntityMap);

            inputParams.remove(odEntityObject.getUiParamName());// cannot be serializable!
            String id = this.getId();
            htmlContext.setHTMLPrefix(id+".");
            htmlContext.setDIVPrefix(id.replace('.','-')+'-');
            htmlContext.setReconfigFunction(functionReconfigurationMap);
            htmlContext.setReconfigProperty(propertyReconfigurationMap);
            htmlContext.setReconfigVariable(variableReconfigurationMap);
            htmlContext.printHTMLAttributeValues();

            htmlContext.setAjaxWidgetMap(AjaxActionHelper.getAjaxContext()
                    .getFrameComponentMap(this.getFrameInfo()));
            String oldFrameInfo = (String)htmlContext.getRequest().getAttribute("_framePagePrefix");
            htmlContext.getRequest().setAttribute("_framePagePrefix", this.getFrameInfo());
            
            //for a RefEntity uientity2.uientity1, firstly, 
            //call AjaxComponentSecurityUtil.loadSecurityMap to load all the security controls configured on higher level(in this case, uientity2, uipage)
            //in this method, the process sequence is from inside to outside
            //then call HTMLUIEntity.parse to process the security controls configured on lower level(in this case, security controls configured within uientity1)
            //in this method, the process sequence is from outside to inside
            UIFormObject entity = HTMLUtil.parseUIEntity(this.getUIEntityName());
            
            OOEEContext ooeeContext = OOEEContextFactory.createOOEEContext();
            DefaultEvaluationContext evaContext = new DefaultEvaluationContext();
            Iterator<Map.Entry<String, Object>> i = inputParams.entrySet().iterator();
            while (i.hasNext()) {
            	Map.Entry<String, Object> entry = i.next();
            	evaContext.setVariableValue(entry.getKey(), entry.getValue());
            }
            ooeeContext.setDefaultEvaluationContext(evaContext);
            ooeeContext.setEvaluationContextObject(ODContext.LOCAL_TAG, evaContext);
            
            PageDispatcher dispatcher = new PageDispatcher(entity, ooeeContext);
            dispatcher.forwardForm(htmlContext, 0,
                    isReadOnly(), new HTMLReferenceEntityType(htmlContext, id));
            htmlContext.getRequest().setAttribute("_framePagePrefix",oldFrameInfo);
            html = writer.getBuffer().toString();
//            form = (Panel)htmlContext.getAJAXComponent(getId() + ".Form");
        }
        catch (Exception ex)
        {
            logger.error("Error in building up structure for entity: " + uiid, ex);
        }
    }
    
    public void callODMapper(HTMLSnapshotContext htmlContext, String odmapperName) throws ODException
    {
        if (logger.isDebugEnabled())
            logger.debug("callODMapper odmapper name: " + odmapperName);

        ODProcessor processor = new ODProcessor(htmlContext, odmapperName, -1);
        processor.process();
    }
    
    private ModalWindow window = null;
    private CallBack callBack;
    private List<CallBack> callBackList;
    
    /**
     * This is a very interesting API design for flying a form over the parent page.
     * 
     * @param title required
     * @param callBack optional when the window is closed.
     */
    public void openInWindows(String title, CallBack callBack) {
    	openInWindows(title, callBack, -1, -1, false);
    }
    
    public void openInWindows(String title, CallBack callBack, int width, int height) {
    	openInWindows(title, callBack, width, height, false);
    }
    
    public void openInWindows(String title, CallBack callBack, int width, int height, boolean showCloseBtn) {
    	window = new ModalWindow(this.getUiid() + "-Dialog", title, this);
        window.setFixable(true);
        window.setShowCloseBtn(showCloseBtn);
        if (width > 0) {
        	window.setBounds(-1, -1, width, height);
        }
        window.open();
        
        this.callBack = callBack;
    }
    
    public void addWindowsClosedCallBack(CallBack caller) {
    	if (callBackList == null) {
    		callBackList = new ArrayList<CallBack>();
    	}
    	callBackList.add(caller);
    }
    
	public void closeIfinWindows() {
		if (window != null) {
			window.close();

			if (callBack != null) {
				callBack.execute();
			}
			if (callBackList != null) {
				for (CallBack caller : callBackList) {
					caller.execute();
				}
			}
		}
		
		this.remove();
	}
	
	public void closeIfinWindows(boolean skipCallBack) {
		if (window != null) {
			window.close();

			if (!skipCallBack) {
				if (callBack != null) {
					callBack.execute();
				}
				if (callBackList != null) {
					for (CallBack caller : callBackList) {
						caller.execute();
					}
				}
			}
		}
		
		this.remove();
	}
	
	public boolean isInWindows() {
		return window != null;
	}
    
    public void remove()
    {
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if(ajaxContext == null)
            return;
        if(ajaxContext.existElmByAbsoluteId(getId(), getFrameInfo()))
        {
            Map map = AjaxActionHelper.getFrameMap(ajaxContext.getRequest());
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                String uiid = (String) entry.getKey();
                if(uiid.startsWith(this.getId()+"."))
                {
                    if(logger.isDebugEnabled())
                        logger.debug("Remove component["+uiid+"] in cache of ui map.");
                    iterator.remove();
                }
            }
    
            String parentID = null;
            if ( getHtmlLayout() != null )
            {
                getHtmlLayout().remove();
                if ( getHtmlLayout().parent != null )
                {
                    parentID = getHtmlLayout().parent.getId();
                }
            }
            IDataItem dataItem = AjaxActionHelper.createRemoveItem(parentID, getId());
            dataItem.setFrameInfo(getFrameInfo());
            ajaxContext.addDataItem(dataItem);
            ajaxContext.removeElement(getId(),getFrameInfo());
        }
    }
}
