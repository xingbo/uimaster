package org.shaolin.uimaster.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.shaolin.uimaster.page.exception.UIComponentNotFoundException;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: considering add this to the cache.
 * 
 * @author Administrator
 *
 */
public class PageWidgetsContext implements java.io.Serializable {

	private static final Logger logger = LoggerFactory.getLogger(PageWidgetsContext.class);
	
	private final Map<String, HTMLWidgetType> components = 
			new HashMap<String, HTMLWidgetType>();
	
	private final String pageName;
	
	public PageWidgetsContext(String pageName) {
		this.pageName = pageName;
	}
	
	public void loadComponent(HTMLSnapshotContext context, 
			String uiid, String entityName, boolean isEntity) {
		UIFormObject uientityObject = null;
		if (isEntity) {
			uientityObject = PageCacheManager.getUIFormObject(entityName);
		} else {
			UIPageObject pageObject = PageCacheManager.getUIPageObject(entityName);
			uientityObject = pageObject.getUIForm();
		}
		
		if (logger.isDebugEnabled()) {
        	logger.debug("Create html components for Entity: {}", entityName);
        }
		
		Iterator<String> i = uientityObject.getAllComponentID();
		while(i.hasNext()) {
			String compId = i.next();
			Map<String, Object> propMap = uientityObject.getComponentProperty(compId);
			Map eventMap = uientityObject.getComponentEvent(compId);
			String UIID = uiid + compId;
	        Boolean readOnly = Boolean.FALSE;
	        Map tempMap = new HashMap();
	        HTMLLayoutType layout = HTMLUtil.getHTMLLayoutType("CellLayoutType");
			
	        HTMLWidgetType component = HTMLUtil.getHTMLUIComponent(
	        		UIID, context, propMap, eventMap, readOnly, 
	        		tempMap, layout, false);
	        component.setPrefix(context.getHTMLPrefix());
	        if (component instanceof HTMLReferenceEntityType) {
				HTMLReferenceEntityType oldReferObject = ((HTMLReferenceEntityType) component);
				oldReferObject.setType((String)propMap.get("referenceEntity"));
			}

	        components.put(component.getName(), component);
	        if (logger.isDebugEnabled()) {
	        	logger.debug("Create component: {}, type: {}", component.getName(), component);
	        }
		}
	}
	
	public HTMLWidgetType getComponent(String uiid) 
		throws UIComponentNotFoundException {
		if (components.containsKey(uiid)) {
			return components.get(uiid);
		} else {
			throw new UIComponentNotFoundException(
					"the component does not exist in the cache. uiid: " + uiid );
		}
	}
	
	public String getPageName() {
		return this.pageName;
	}
	
	public void printAllComponents() {
		 if (logger.isTraceEnabled())
         {
			 StringBuffer sb = new StringBuffer();
			 sb.append("\n\nPrint all created UIComponents in page: ").append(pageName).append("\n");
			 
			 Iterator<String> f = components.keySet().iterator();
			 while(f.hasNext()) {
         		String uiid = f.next();
         		Object object = components.get(uiid);
         		sb.append("  Widget: ").append(uiid).append("=").append(object);
         		sb.append("\n");
             }
             logger.trace(sb.toString());
         }
	}
	
}
