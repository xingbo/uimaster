package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.DataParamType;
import org.shaolin.bmdp.datamodel.page.ExpressionParamType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.datamodel.page.UITabPaneItemType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.html.layout.HTMLPanelLayout;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.PageWidgetsContext;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.cache.ODFormObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.shaolin.uimaster.page.od.ODContext;
import org.shaolin.uimaster.page.spi.IJsGenerator;
import org.shaolin.uimaster.page.widgets.HTMLCellLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLFrameType;

/**
 * Example:
 *  <br>
 *  <br>TabPane taPane = (TabPane)@page.getElement("taPane");
 *  <br>if(taPane == null)
 *  <br>{
 *  <br>    taPane = new TabPane("taPane");
 *  <br>    Panel rootPanel = (Panel)@page.getElement("Form");
 *  <br>    rootPanel.append(taPane);
 *  <br>}
 *  <br>String ui = "bmiasia.ebos.appbase.test.uientity.Employee";
 *  <br>RefEntity refEntity = new RefEntity("refEntitytab1",ui);
 *  <br>taPane.addTab("customer",refEntity);
 *  <br>ui = "bmiasia.ebos.businessmanager.uientity.ExpressionUI";
 *  <br>RefEntity refEntity2 = new RefEntity("refEntitytab2",ui);
 *  <br>taPane.setTabComponentAt(0, refEntity2);
 *  <br>ui = "bmiasia.ebos.appbase.test.uientity.Employee";
 *  <br>RefEntity refEntity3 = new RefEntity("refEntitytab3",ui);
 *  <br>taPane.addTabAt(0,"customer",refEntity3);
 *  <br>taPane.setSelectedIndex(1);
 * @author Pan, Yifeng
 *
 */
public class TabPane extends Container implements Serializable 
{
    private static final long serialVersionUID = -1744731434666233557L;
    private static final String CMD_ADDTAB = "addTab";
    private static final String CMD_REMOVETAB = "removeTab";
    private static final String CMD_SETBODY = "setBody";
    private static final String CMD_SETTITLE = "setTitle";
    private static final String CMD_SETSELECTEDINDEX = "setSelectedIndex";
    private static final String HANDLERNAME = "tabPaneHandler";
    //List of titles(String)
    private List titles=new ArrayList();
    //List of entities(RefEntity)
    private List entities=new ArrayList();
    
    /**
     * ajax loading objects.
     */
    private List<UITabPaneItemType> tabs;
    private UIFormObject ownerEntity;
    private Map<String, Object> vars;
    private PageWidgetsContext widgetContext;
    // clean the ajax loading objects if all tabs loaded.
    private AtomicInteger accessedIndex = new AtomicInteger();
    
    private String uiid;
    private int selectedIndex;
    
    /**
     * 
     * @param uiid 
     * @param titles List of titles(String)
     * @param entities List of bodies(String)
     */
    public TabPane(String uiid)
    {
        this(uiid, new CellLayout());
    }
    
    public TabPane(String id, Layout layout)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() +id, layout);
        this.setListened(true);
        this.uiid = this.getId();
    }
    
    public void addAttribute(String name, Object value, boolean update)
    {
        if(name == null || name.length() == 0)
        {
            return;
        }
        if(name.equals("selectedIndex"))
        {
            selectedIndex = Integer.valueOf(value.toString()).intValue();
        }
    }
    
    /**
     * for ui html type!
     * 
     * @param id
     * @param selectedIndex
     * @param layout
     */
    public TabPane(String id, List<UITabPaneItemType> tabs, Map<String, Object> vars, int selectedIndex, Layout layout)
    {
        super(id, layout);
        this.setListened(true);
        this.tabs = tabs;
        this.vars = vars;
        this.selectedIndex = selectedIndex;
        this.uiid = this.getId();
    }

    public void setOwnerEntity(UIFormObject ownerEntity) {
    	this.ownerEntity = ownerEntity;
    }
    
    public void setPageWidgetContext(PageWidgetsContext widgetContext) {
    	this.widgetContext = widgetContext;
    }
    
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();
        generateWidget(html);

        //Generate the tab
        html.append("<div id=\"");
        html.append(uiid);
        html.append("\" class=\"tab\">");
        html.append("<div class=\"uimaster_tabPane\">");
        
        //Generate the titles' container
        html.append("<div class =\"tab-titles\" id=\"titles-container-");
        html.append(uiid);
        html.append("\" selectedIndex=\"");
        html.append(getSelectedIndex());
        html.append("\">");
        html.append("</div>");
        
        //Generate the bodies of the tabpane
        html.append("<div class=\"tab-bodies\" id=\"bodies-container-");
        html.append(uiid);
        html.append("\">");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</div>");
        return html.toString();
    }
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(300);
        js.append("defaultname.");
        js.append(uiid);
        js.append("=new UIMaster.ui.tab({");
        js.append("uiid:\"").append(uiid).append("\",");
        js.append("ui:elementList[\"");
        js.append(uiid);
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        for ( int i = 0; i < entities.size(); i++ )
        {
            RefForm entity = ((RefForm)entities.get(i));
            if (entity != null )
            {
                js.append(entity.generateJS());
            }
        }
        return js.toString();
    }
    
    public String loadContent(int index) throws JspException, EvaluationException {
    	if (this.tabs == null) {
    		throw new IllegalStateException("Please enable the ajax loading for this tab panel.");
    	}
    	this.selectedIndex = index;
    	this.accessedIndex.incrementAndGet();
    	
    	try {
    	AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
    	HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(ajaxContext.getRequest());
        htmlContext.setFormName(this.getUIEntityName());
        htmlContext.setIsDataToUI(true);//Don't set prefix in here.
        htmlContext.setAjaxWidgetMap(AjaxActionHelper.getFrameMap(ajaxContext.getRequest()));
        htmlContext.setHTMLPrefix("");
        htmlContext.setDIVPrefix("");
        htmlContext.setJSONStyle(true);
        htmlContext.setPageWidgetContext(widgetContext);
    	
    	UITabPaneItemType tab = tabs.get(index);
        String entityPrefix = ajaxContext.getEntityPrefix();
		if (tab.getPanel() != null) {
			DefaultEvaluationContext default1 = new DefaultEvaluationContext();
			if (this.vars != null && !this.vars.isEmpty()) {
				Set<Map.Entry<String, Object>> entries = this.vars.entrySet();
				for (Map.Entry<String, Object> e : entries) {
					default1.setVariableValue(e.getKey(), e.getValue());
				}
			}
			OOEEContext ooee = OOEEContextFactory.createOOEEContext();
			ooee.setDefaultEvaluationContext(default1);
			ooee.setEvaluationContextObject(ODContext.LOCAL_TAG, default1);
			VariableEvaluator ee = new VariableEvaluator(ooee);
        	//ui panel support
        	String UIID = entityPrefix + tab.getPanel().getUIID();
        	HTMLPanelLayout panelLayout = new HTMLPanelLayout(UIID, ownerEntity);
        	TableLayoutConstraintType layoutConstraint = new TableLayoutConstraintType();
            panelLayout.setConstraints(layoutConstraint, ooee);
            panelLayout.setBody(tab.getPanel(), ooee);
        	
            HTMLCellLayoutType layout = new HTMLCellLayoutType(htmlContext, UIID);
            panelLayout.generateComponentHTML(htmlContext, 0, true, Collections.emptyMap(), ee, layout);
            
            IJsGenerator jsGenerator = IServerServiceManager.INSTANCE.getService(IJsGenerator.class);
            StringBuffer js = new StringBuffer();
            js.append(jsGenerator.gen(this.getUIEntityName(), entityPrefix, tab.getPanel()));
            js.append("\ndefaultname.");
            if (entityPrefix != null && entityPrefix.length() > 0) {
            	js.append(entityPrefix).append('.');
            }
            js.append("Form.items.push(elementList['").append(tab.getPanel().getUIID()).append("']);");
            
            IDataItem dataItem = AjaxActionHelper.createAppendItemToTab(this.getId(), UIID);
            dataItem.setData(htmlContext.getHTMLString());
            dataItem.setJs(js.toString());
            dataItem.setFrameInfo(this.getFrameInfo());
			JSONArray array = new JSONArray();
			array.put(new JSONObject(dataItem));
			return array.toString();
			
        } else if (tab.getRefEntity() != null) {
        	//form support
        	UIReferenceEntityType itemRef = (UIReferenceEntityType)tab.getRefEntity();
        	String UIID = entityPrefix + itemRef.getUIID();
            String type = itemRef.getReferenceEntity().getEntityName();
            try {
            	ODFormObject o = PageCacheManager.getODFormObject(type);
				DefaultEvaluationContext default1 = o.getLocalEContext();
				Map<String, Object> inputVars = default1.getVariableObjects();
				try {
					UIPage uipage = AppContext.get().getEntityManager().getEntity(this.getUIEntityName(), UIPage.class);
					List<ComponentMappingType> mappings = uipage.getODMapping().getComponentMappings();
					setRefEntityInputVars(this.vars, inputVars, type, mappings);
				} catch (Exception e) {
					UIEntity uientity = AppContext.get().getEntityManager().getEntity(this.getUIEntityName(), UIEntity.class);
					List<ComponentMappingType> mappings = uientity.getMapping().getComponentMappings();
					setRefEntityInputVars(this.vars, inputVars, type, mappings);
				}
				RefForm form = new RefForm(UIID, type, inputVars);
				
				IDataItem dataItem = AjaxActionHelper.createAppendItemToTab(this.getId(), UIID);
				dataItem.setData(form.generateHTML());
				dataItem.setJs(form.generateJSWithoutJsPath());
				dataItem.setFrameInfo(this.getFrameInfo());
				JSONArray array = new JSONArray();
				array.put(new JSONObject(dataItem));
				return array.toString();
			} catch (ParsingException e1) {
				throw new JspException(e1);
			} catch (ClassNotFoundException e1) {
				throw new JspException(e1);
			}
        } else if (tab.getFrame() != null) {
        	//page support
			HttpServletRequest request = ajaxContext.getRequest();
        	request.setAttribute("_chunkname", tab.getFrame().getChunkName());
        	request.setAttribute("_nodename", tab.getFrame().getNodeName());
        	request.setAttribute("_framePagePrefix", ajaxContext.getFrameId());
        	request.setAttribute("_tabcontent", "true");
        	HTMLFrameType frame = new HTMLFrameType(htmlContext, tab.getUiid());
        	frame.setHTMLAttribute(HTMLFrameType.NEED_SRC, "true");
        	frame.generateBeginHTML(htmlContext, ownerEntity, 0);
        	frame.generateEndHTML(htmlContext, ownerEntity, 0);
        	
        	String UIID = entityPrefix + tab.getUiid();
        	IDataItem dataItem = AjaxActionHelper.createAppendItemToTab(this.getId(), UIID);
            dataItem.setData(htmlContext.getHTMLString());
            dataItem.setFrameInfo(this.getFrameInfo());
			JSONArray array = new JSONArray();
			array.put(new JSONObject(dataItem));
			return array.toString();
        } 
        return "";
    	} finally {
    		if (this.accessedIndex.get() >= this.tabs.size()) {
    			this.tabs = null;
    			this.vars = null;
    			this.widgetContext = null;
    			this.ownerEntity = null;
    			this.accessedIndex = null;
    		}
    	}
    }

	private void setRefEntityInputVars(Map<String, Object> vars, Map<String, Object> inputVars,
			String type, List<ComponentMappingType> mappings)
			throws EvaluationException {
		for (ComponentMappingType m : mappings) {
			if (m instanceof SimpleComponentMappingType) {
				SimpleComponentMappingType sm = (SimpleComponentMappingType) m;
				if (type.equals(sm.getMappingRule().getEntityName())) {
					List<DataParamType> params = sm.getDataComponents();
					if (params == null || params.size() == 0) {
						break;
					}
					for (DataParamType p : params) {
						if (p instanceof ComponentParamType) {
							String pageVarName = ((ComponentParamType)p).getComponentPath();
							String refEntityVarName = ((ComponentParamType)p).getParamName();
							
							inputVars.put(refEntityVarName, vars.get(pageVarName));
						} else if (p instanceof ExpressionParamType) {
							//TODO: improve to evaluate the expression.
							String refEntityVarName = ((ExpressionParamType)p).getParamName();
							inputVars.put(refEntityVarName, vars.get(refEntityVarName));
						}
					}
					break;
				}
			}
		}
	}
    
    /**
     * Adds a component with a title which can be null at the end of the TabPane.
     * @param title
     * @param component
     */
    public void addTab(String title, RefForm component)
    {
        addTabAt(titles.size(), title, component);
    }
    
    /**
     * Adds a component with a title which can be null at index.
     * @param index: must be unique.
     * @param title
     * @param component
     */
    public void addTabAt(int index, String title, RefForm component)
    {
        titles.add(title);
        entities.add(component);
        if(index > entities.size() || index < 0)
        {
            throw new IllegalArgumentException("Fail to add tab in TabPane at " + index);
        }
        this.selectedIndex = index;
        
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setJs(component.generateJS());
        dataItem.setUiid(uiid);
        dataItem.setJsHandler(HANDLERNAME);
        
        Map data = new HashMap();
        data.put("cmd",CMD_ADDTAB);
        data.put("index",String.valueOf(index));
        data.put("title",title);
        data.put("entity",component.generateHTML());
        dataItem.setData((new JSONObject(data)).toString());
        dataItem.setFrameInfo(getFrameInfo());
        ajaxContext.addDataItem(dataItem);
    }
    
    /**
     * Removes the tab and component which corresponds to the specified index.
     * @param index
     */
    public void removeTabAt(int index)
    {
        if(index >= entities.size() || index < 0)
        {
            return;
        }
        entities.remove(index);
        titles.remove(index);
        if(getSelectedIndex() == index)
        {
            this.selectedIndex = 0;
        }
        
        Map data = new HashMap();
        data.put("cmd",CMD_REMOVETAB);
        data.put("index",String.valueOf(index));
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = createData((new JSONObject(data)).toString());
        ajaxContext.addDataItem(dataItem);
    }
    
    /**
     * Sets the component that is responsible for rendering the title for the specified tab
     * @param index
     * @param component
     */
    public void setTabComponentAt(int index, RefForm component)
    {
        if(index >= entities.size() || index < 0)
        {
            return;
        }
        entities.set(index, component);
        
        Map data = new HashMap();
        data.put("cmd",CMD_SETBODY);
        data.put("index",String.valueOf(index));
        data.put("entity",component.generateHTML());
        
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = createData((new JSONObject(data)).toString());
        dataItem.setJs(component.generateJS());
        ajaxContext.addDataItem(dataItem);
    }

    /**
     * Sets the title at index to title which can be null.
     * @param index
     * @param title
     */
    public void setTitleAt(int index, String title)
    {
        if(index >= titles.size() || index < 0)
        {
            return;
        }
        titles.set(index, title);
        
        Map data = new HashMap();
        data.put("cmd",CMD_SETTITLE);
        data.put("index",String.valueOf(index));
        data.put("title",title);
        
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = createData((new JSONObject(data)).toString());
        ajaxContext.addDataItem(dataItem);
    }
    
    private IDataItem createData(String data) 
    {
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setUiid(uiid);
        dataItem.setJsHandler(HANDLERNAME);
        dataItem.setData(data);
        dataItem.setFrameInfo(getFrameInfo());
        return dataItem;
    }
    /**
     * Returns the component at index.
     * @param index
     * @return
     */
    public RefForm getComponentAt(int index)
    {
        if(index >= entities.size() || index < 0)
        {
            return null;
        }
        return (RefForm)entities.get(index);
    }
       
    /**
     * Returns the tab title at index.
     * @param index
     * @return
     */
    public String getTitleAt(int index)
    {
        return (String)titles.get(index);
    }
       
    /**
     * Returns the number of tabs in this tabbedpane
     * @return
     */
    public int getTabCount()
    {
        return titles.size();
    }
      
    /**
     * Returns the index of the tab for the specified component.
     * @param component
     * @return
     */
    public int indexOfComponent(RefForm component)
    {
        return entities.indexOf(component);
    }

    public List getEntities()
    {
        return entities;
    }

    public void setEntities(List entities)
    {
        this.entities = entities;
    }

    /**
     * Return the index of the selected tab.
     * @return
     */
    public int getSelectedIndex()
    {
        return this.selectedIndex;
    }
    
    /**
     * Select a tab.
     * @return
     */
    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
        Map data = new HashMap();
        data.put("cmd",CMD_SETSELECTEDINDEX);
        data.put("index",String.valueOf(selectedIndex));
        
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem dataItem = createData((new JSONObject(data)).toString());
        ajaxContext.addDataItem(dataItem);
    }
    
    public List getTitles()
    {
        return titles;
    }

    public void setTitles(List titles)
    {
        this.titles = titles;
    }

    /**
     * Whether this component can have editPermission.
     */
    @Override
    public boolean isEditPermissionEnabled()
    {
        return false;
    }

}
