 	
package org.shaolin.uimaster.page;

import java.awt.ComponentOrientation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.ajax.AFile;
import org.shaolin.uimaster.page.ajax.AList;
import org.shaolin.uimaster.page.ajax.Button;
import org.shaolin.uimaster.page.ajax.CellLayout;
import org.shaolin.uimaster.page.ajax.CheckBox;
import org.shaolin.uimaster.page.ajax.CheckBoxGroup;
import org.shaolin.uimaster.page.ajax.ComboBox;
import org.shaolin.uimaster.page.ajax.Hidden;
import org.shaolin.uimaster.page.ajax.Image;
import org.shaolin.uimaster.page.ajax.Label;
import org.shaolin.uimaster.page.ajax.Link;
import org.shaolin.uimaster.page.ajax.Panel;
import org.shaolin.uimaster.page.ajax.PasswordField;
import org.shaolin.uimaster.page.ajax.RadioButton;
import org.shaolin.uimaster.page.ajax.RadioButtonGroup;
import org.shaolin.uimaster.page.ajax.RefForm;
import org.shaolin.uimaster.page.ajax.Table;
import org.shaolin.uimaster.page.ajax.TextArea;
import org.shaolin.uimaster.page.ajax.TextField;
import org.shaolin.uimaster.page.ajax.Tree;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.DataItem;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.IRequestData;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.ajax.json.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ajax Context for the whole page operations. It has initialized in HTMLUIEntity.getAjaxContext
 * 
 * @author swu
 */
public class AjaxContext extends OpExecuteContext implements Serializable
{
    private static final long serialVersionUID = -1744731434567233557L;

    private static final Logger logger = LoggerFactory.getLogger(AjaxContext.class);

    /**
     * what is object of all comps of the current page.
     */
    public static final String AJAX_COMP_MAP = "_ajax_page_component_map";

    public static final String GLOBAL_PAGE = "#GLOBAL#";
    
    /**
     * request action data set.
     */
    public static final String AJAX_USER_EVENT = "_ajaxUserEvent";

    public static final String AJAX_UIID = "_uiid";

    public static final String AJAX_DATA = "_data";

    public static final String AJAX_ACTION_PAGE = "_actionPage";

    public static final String AJAX_FRAME_PREFIX = "_framePrefix";

    public static final String AJAX_BA = "_baName";

    /**
     * for response action name.
     */
    public static final String AJAX_ACTION_TYPE = "_actionType";

    /**
     * for the current uientity/uipage variables of 'Variable' node.
     */
    public static final String CURRENT_ACCESS_VARIABLE = "__CURRENT_ACCESS_VARIABLE_";

    private transient HttpServletRequest request;
    
    private transient HttpServletResponse response;

    private Map uiMap;

    /**
     * what is this operation corresponds to the entity.
     */
    private String entityUiid = "";

    private String entityPrefix = "";

    private boolean isLTR = true;

    //TODO: Whether the current page can be edited. support for security.
    private boolean pageEditableFlag = true;

    private Widget parentComp;

    private Map<String, Widget> ajaxComponentMap;
    
    private final List<IRequestData> ajaxContextParams;

    /**
     * request parameters.
     */
    private final IRequestData requestData;

    /**
     * Linear array list.
     */
    private final ArrayList<JSONObject> dataItems;
    
    public AjaxContext(Map<?, ?> uiMap, IRequestData requestData)
    {
        this.uiMap = uiMap;
        this.dataItems = new ArrayList<JSONObject>();
        this.requestData = requestData;
        this.ajaxContextParams = new ArrayList<IRequestData>();
    }

    public void setAJAXComponentMap(Map<String, Widget> ajaxComponentMap)
    {
        this.ajaxComponentMap = ajaxComponentMap;
    }

    public Map<String, Widget> getAJAXComponentMap()
    {
        return ajaxComponentMap;
    }

    public void addAJAXComponent(String compID, Widget component)
    {
        ajaxComponentMap.put(compID, component);
    }

    public Widget getAJAXComponent(String compID)
    {
        return (Widget)ajaxComponentMap.get(compID);
    }

    public void setParentComp(Widget parentComp)
    {
        this.parentComp = parentComp;
    }

    public Widget getParentComp()
    {
        return parentComp;
    }

    public void appendAJAXComponent(Widget component)
    {
        if (parentComp instanceof CellLayout)
        {
            ((CellLayout)parentComp).addComponent(component);
        }
        else if (parentComp instanceof Panel)
        {
            if (component instanceof CellLayout)
            {
                ((Panel)parentComp).addLayout((CellLayout)component);
            }
            else
            {
                CellLayout tempLayout = new CellLayout(); // TODO: need to make sure!
                tempLayout.addComponent(component);
                ((Panel)parentComp).addLayout(tempLayout);
            }
        }
        else if (parentComp instanceof RefForm)
        {
            ((RefForm)parentComp).setForm((Panel)component);
        }
    }
    
    public void registerPageAjaxContext(String pageName)
    {
        try
        {
            Map<?, ?> uiMap = AjaxActionHelper.getFrameMap(request);
            IRequestData requestData = new RequestData();
            requestData.setEntityName(pageName);
            requestData.setFrameId(request.getParameter("_framePrefix"));
            requestData.setEntityUiid("");
            requestData.setUiid("");
            AjaxContext context = new AjaxContext(uiMap, requestData);
            context.initData();
            context.setRequest(request, response);
            AjaxActionHelper.createAjaxContext(context);
        }
        catch (EvaluationException e)
        {
            logger.warn("Fail to register Page Ajax Context: " + e.getMessage(), e);
        }
    }

    public void removePageAjaxContext()
    {
        AjaxActionHelper.removeAjaxContext();
    }

    /**
     * only for support UI customization.
     * 
     * @param requestData
     */
    public void addAJAXContextParams(IRequestData requestData)
    {
        ajaxContextParams.add(requestData);
    }

    public List getAJAXContextParams()
    {
        return ajaxContextParams;
    }

    public void clearAJAXContextParams()
    {
        ajaxContextParams.clear();
    }
    
    public HttpServletRequest getRequest()
    {
        return request;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }
    
    public void initData() throws EvaluationException
    {
        this.entityUiid = requestData.getEntityUiid();
        if (entityUiid.length() > 0)
            this.entityPrefix = entityUiid + ".";

        DefaultEvaluationContext globalEContext = new DefaultEvaluationContext();
        Map accessibleVars = (Map)uiMap
                .get(AjaxContext.CURRENT_ACCESS_VARIABLE + this.entityPrefix);
        if (accessibleVars != null && !accessibleVars.isEmpty())
        {
            Iterator iterator = accessibleVars.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                globalEContext.setVariableValue((String)entry.getKey(), entry.getValue());
            }
        }
        this.setEvaluationContextObject("@", globalEContext);
        globalEContext.setVariableValue("request", request);
        globalEContext.setVariableValue("page", this);
        globalEContext.setVariableValue("eventsource", this.getElement(requestData.getUiid()));
    }

    public void setRequest(HttpServletRequest request, HttpServletResponse response) throws EvaluationException
    {
        setHttpRequest(request);
        this.response = response;

        DefaultEvaluationContext globalEContext = (DefaultEvaluationContext)this
                .getEvaluationContextObject("@");
        globalEContext.setVariableValue("request", request);
    }

    public void setHttpRequest(HttpServletRequest request)
    {
        this.request = request;
        this.setLTR(request);
    }

    private void setLTR(HttpServletRequest request)
    {
        String userLocale = WebConfig.getUserLocale(request);
        ComponentOrientation ce = ComponentOrientation.getOrientation(
        		ResourceUtil.getLocaleObject(userLocale));
        isLTR = ce.isLeftToRight();
    }

    public IRequestData getRequestData()
    {
        return this.requestData;
    }

    public String getParameter(String key)
    {
        return (String)this.requestData.getData().get(key);
    }

    public String getFrameId() {
    	return request.getParameter("_framePrefix");
    }
    
    public int itemSize()
    {
        return dataItems.size();
    }
    
    /**
     * customized data item.
     * 
     * @param dataItem
     */
    public void addDataItem(IDataItem dataItem)
    {
        dataItems.add( new JSONObject(dataItem) );
    }

    public void executeJavaScript(String script)
    {
        executeJavaScript(script, requestData.getFrameId());
    }

    public void executeJavaScript(String script, String frameInfo)
    {
        IDataItem item = new DataItem();
        item.setJsHandler(IJSHandlerCollections.JAVASCRIPT);
        item.setJs(script);
        item.setFrameInfo(frameInfo);
        dataItems.add(new JSONObject(item));
    }

    /**
     * This method not only generate JSON, also clear all the items.
     */
    public String getDataAsJSON()
    {
        JSONArray json = new JSONArray();
        for (int i = 0; i < dataItems.size(); i++)
        {
            json.put(dataItems.get(i));
        }
        
        dataItems.clear();
        return json.toString();
    }

    /**
     * @throws EvaluationException
     */
    public void synchVariables() throws EvaluationException
    {
        Map accessibleVars = (Map)uiMap
                .get(AjaxContext.CURRENT_ACCESS_VARIABLE + this.entityPrefix);
        if (accessibleVars != null && !accessibleVars.isEmpty())
        {
            EvaluationContext eContext = this.getEvaluationContextObject("@");
            Iterator iterator = accessibleVars.keySet().iterator();
            while (iterator.hasNext())
            {
                String key = (String)iterator.next();
                accessibleVars.put(key, eContext.getVariableValue(key));
            }
        }
    }

    public String getEntityPrefix()
    {
        return entityPrefix;
    }

    public String getEntityUiid()
    {
        return entityUiid;
    }

    public String getEntityName()
    {
        return requestData.getEntityName();
    }

    /**
     * by relative id adjust whether is existed this component in the component tree.
     * 
     * @param uiid
     * @return
     */
    public boolean existElement(String uiid)
    {
    	if (uiid.equals(this.getEntityUiid())) {
    		return uiMap.containsKey(uiid);
    	}
    	String realUiid = uiid;
    	if (this.entityPrefix != null && !this.entityPrefix.isEmpty()) {
    		if (!uiid.startsWith(this.entityPrefix)) {
    			realUiid = this.entityPrefix + uiid;
    		}
    	}
        return uiMap.containsKey(realUiid);
    }

    /**
     * by absolute id adjust whether is existed this component in the component tree, also input
     * instance id equals that instance id of the uiMap.
     * 
     * @param comp
     * @return uiMap.containsKey(comp.getId()) && comp == uiMap.get(comp.getId())
     */
    public boolean existElement(Widget comp)
    {
        String frameName = comp.getFrameInfo();
        if (frameName == null)
        {
            return false;
        }
        Map map = getFrameComponentMap(frameName);
        return map.containsKey(comp.getId()) && comp == map.get(comp.getId());
    }

    public boolean existElmByAbstId(String absoluteUiid, String frameName)
    {
        return existElmByAbsoluteId(absoluteUiid, frameName);
    }
    
    /**
     * by absolute id adjust whether is existed this component in the component tree.
     * 
     * @param absoluteUiid
     * @param frameName
     * @return
     */
    public boolean existElmByAbsoluteId(String absoluteUiid, String frameName)
    {
        Map frameMap = getFrameComponentMap(frameName);
        if (frameMap == null)
        {
            return false;
        }
        else
        {
            return frameMap.containsKey(absoluteUiid);
        }
    }

    /**
     * get element by id.
     * 
     * @param uiid
     * @return Component
     */
    public Widget getElement(String uiid)
    {
    	if (uiid == null) {
    		throw new IllegalArgumentException("The ui widget id can be null.");
    	}
    	String realUiid = uiid;
    	if (!uiid.equals(this.getEntityUiid())) {
    		if (this.entityPrefix != null && !this.entityPrefix.isEmpty()) {
    			if (!uiid.startsWith(this.entityPrefix)) {
    				realUiid = this.entityPrefix + uiid;
    			}
    		}
    	}
        Widget comp = (Widget)uiMap.get(realUiid);
        if (comp == null) {
            logger.debug("The ui widget can not be found by this " + realUiid + " id. try to use the original id: "+uiid);
            comp = (Widget)uiMap.get(uiid);
            if (comp == null) {
            	logger.warn("The ui widget can not be found by either " + realUiid + " or "+ uiid);
            }
        }
        return comp;
    }

    /**
     * Get an element from a frame
     * 
     * @param absoluteUiid
     * @param frameName
     * @return Component
     */
    public Widget getElementByAbsoluteId(String absoluteUiid, String frameName)
    {
        Map frameMap = getFrameComponentMap(frameName);
		if (frameMap == null) {
			return null;
		} else {
			return (Widget) frameMap.get(absoluteUiid);
		}
    }
    
    public Widget getElementByAbsoluteId(String absoluteUiid)
    {
    	Widget comp = (Widget)uiMap.get(absoluteUiid);
        if (comp == null) {
        	logger.warn("The ui widget can not be found by absolute id: " + absoluteUiid);
        }
        return comp;
    }

    /**
     * Get a frame component map from a given frame name
     * 
     * @param frameName
     * @return
     */
    public Map getFrameComponentMap(String frameName)
    {
        if (frameName == null)
        {
            logger.debug("No frame name specified, use this context's default frame.");
            return uiMap;
        }
        Map ajaxComponentMap = AjaxActionHelper.getAjaxWidgetMap(request.getSession());
        if (frameName.equals(""))
        {
            frameName = "#GLOBAL#";
        }
        Map frameMap;
        frameMap = (Map)ajaxComponentMap.get(frameName);
        if (frameMap == null)
        {
            logger.error("Cannot find a specific frame. Frame name: " + frameName);
            return null;
        }
        return frameMap;
    }
    
    public boolean addElement(Widget comp)
    {
        return addElement(comp.getId(), comp, comp.getFrameInfo());
    }

    /**
     * add an element.
     * 
     * @param absoluteUiid
     * @param comp
     * @return if returned true, input component added into the component tree.
     */
    public boolean addElement(String absoluteUiid, Widget comp, String frameName)
    {
        Map frameMap = getFrameComponentMap(frameName);
		if (frameMap == null) {
			logger.warn("addElement operation failed: frame not found");
			return false;
		}
		if (frameMap.containsKey(absoluteUiid)) {
			logger.warn("addElement[" + absoluteUiid + "]: it has existed, override it!");
		}
        comp.setFrameInfo(frameName);
        frameMap.put(absoluteUiid, comp);
        return true;
    }
    
    public Widget removeElement(String uiid)
    {
    	if (uiid.equals(this.getEntityUiid())) {
    		return (Widget)uiMap.remove(uiid);
    	}
    	String realUiid = uiid;
    	if (this.entityPrefix != null && !this.entityPrefix.isEmpty()) {
    		if (!uiid.startsWith(this.entityPrefix)) {
    			realUiid = this.entityPrefix + uiid;
    		}
    	}
        return (Widget)uiMap.remove(realUiid);
    }

    /**
     * @param absoluteUiid
     */
    public void removeElement(String absoluteUiid, String frameName)
    {
        Map frameMap = getFrameComponentMap(frameName);
        if (frameMap == null)
        {
            logger.warn("removeElement failed: frame not found");
        }
        else
        {
            if (frameMap.containsKey(absoluteUiid))
            {
                frameMap.remove(absoluteUiid);
            }
        }
    }
   
    public void removeFramePage(String frameId) {
    	logger.info("remove frame page: " + frameId);
    	Map ajaxComponentMap = AjaxActionHelper.getAjaxWidgetMap(request.getSession());
    	ajaxComponentMap.remove(frameId);
    }
    
    public RefForm removeForm(String uiid) {
    	return (RefForm)removeElement(uiid);
    }
    
    public RefForm getRefForm(String uiid)
    {
        return (RefForm)this.getElement(uiid);
    }
    
    public TextField getTextField(String uiid)
    {
    	return (TextField)this.getElement(uiid);
    }
	
    public TextArea getTextArea(String uiid)
    {
        return (TextArea)this.getElement(uiid);
    }
    
    public Table getTable(String uiid) 
    {
    	return (Table)this.getElement(uiid);
    }
    
    public Button getButton(String uiid)
    {
        return (Button)this.getElement(uiid);
    }
    
    public PasswordField getPasswordField(String uiid)
    {
        return (PasswordField)this.getElement(uiid);
    }
    
    public Hidden getHidden(String uiid)
    {
        return (Hidden)this.getElement(uiid);
    }
    
    public Label getLabel(String uiid)
    {
        return (Label)this.getElement(uiid);
    }
    
    public Link getLink(String uiid)
    {
        return (Link)this.getElement(uiid);
    }
    
    public AFile getFile(String uiid)
    {
        return (AFile)this.getElement(uiid);
    }
    
    public Image getImage(String uiid)
    {
        return (Image)this.getElement(uiid);
    }
    
    public CheckBox getCheckBox(String uiid)
    {
        return (CheckBox)this.getElement(uiid);
    }
    
    public RadioButton getRadioButton(String uiid)
    {
        return (RadioButton)this.getElement(uiid);
    }
    
    public ComboBox getComboBox(String uiid)
    {
        return (ComboBox)this.getElement(uiid);
    }
    
    public RadioButtonGroup getRadioBtnGroup(String uiid)
    {
        return (RadioButtonGroup)this.getElement(uiid);
    }
    
    public AList getList(String uiid)
    {
        return (AList)this.getElement(uiid);
    }
    
    public CheckBoxGroup getCheckBoxGroup(String uiid)
    {
        return (CheckBoxGroup)this.getElement(uiid);
    }
    
    public Panel getPanel(String uiid)
    {
        return (Panel)this.getElement(uiid);
    }
    
    public Tree getTree(String uiid)
    {
        return (Tree)this.getElement(uiid);
    }
    
    public boolean isLTR()
    {
        return isLTR;
    }

    public void printUiMap()
    {
    }

    public Map getVariables(String entityPrefix)
    {
        if (entityPrefix == null)
        {
            entityPrefix = "";
        }
        return (Map)uiMap.get(AjaxContext.CURRENT_ACCESS_VARIABLE + entityPrefix);
    }

    /**
     * @return if true means that the current page can be edited by the current user
     */
    public boolean isPageEditable()
    {
        return pageEditableFlag;
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
}
