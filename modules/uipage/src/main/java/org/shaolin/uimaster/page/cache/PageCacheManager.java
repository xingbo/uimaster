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

import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.javacc.exception.ParsingException;

/**
 * The ui entities shared for the whole system, which can be multiple applications running.
 * 
 * @author wushaol
 *
 */
public class PageCacheManager {
	private static final String PAGE_OD_CACHE = "__sys_uipage_odcache";

	private static final String OD_CACHE = "__sys_od_cache";

	private static final String UI_CACHE = "__sys_uipage_cache";
	
	private static final String UI_Entity_CACHE = "__sys_uientity_cache";

    private static ICache<String, UIPageObject> uipageCache;
    
    private static ICache<String, UIFormObject> uiformCache;
	
	private static ICache<String, ODPageObject> pageODCache;

	private static ICache<String, ODFormObject> odCache;
	
	static {
		uipageCache = CacheManager.getInstance().getCache(UI_CACHE, String.class, 
				UIPageObject.class);
		uiformCache = CacheManager.getInstance().getCache(UI_Entity_CACHE, String.class, 
				UIFormObject.class);
		pageODCache = CacheManager.getInstance().getCache(PAGE_OD_CACHE,
				String.class, ODPageObject.class);
		odCache = CacheManager.getInstance().getCache(OD_CACHE, String.class,
				ODFormObject.class);
	}

	public static ODPageObject getODPageEntityObject(String pageName)
			throws ParsingException, ClassNotFoundException, EntityNotFoundException {
		ODPageObject odPageEntity = pageODCache.get(pageName);
		if (odPageEntity == null) {
			odPageEntity = new ODPageObject(pageName);
			ODPageObject old = pageODCache.putIfAbsent(pageName, odPageEntity);
			odPageEntity = (old != null) ? old : odPageEntity;
		} 
		return odPageEntity;
	}

	public static ODFormObject getODFormObject(String formName)
			throws ParsingException, ClassNotFoundException, EntityNotFoundException {
		ODFormObject odEntityObject = odCache.get(formName);
		if (odEntityObject == null) {
			odEntityObject = new ODFormObject(formName);
			ODFormObject old = odCache.putIfAbsent(formName, odEntityObject);
			odEntityObject = (old != null) ? old : odEntityObject;
		} 
		return odEntityObject;
	}
	
	public static UIPageObject getUIPageObject(String pageName) throws EntityNotFoundException {
		UIPageObject pageObject = uipageCache.get(pageName);
		if (pageObject == null) {
			pageObject = new UIPageObject(pageName);
			UIPageObject old = uipageCache.putIfAbsent(pageName, pageObject);
			pageObject = (old != null) ? old : pageObject;
		} 
		return pageObject;
	}
	
	public static UIFormObject getUIFormObject(String entityName) throws EntityNotFoundException {
		UIFormObject formObject = uiformCache.get(entityName);
		if (formObject == null) {
			formObject = new UIFormObject(entityName);
			UIFormObject old = uiformCache.putIfAbsent(entityName, formObject);
			formObject = (old != null) ? old : formObject;
		}
		return formObject;
	}
	
	public static boolean isUIPage(String entityName) {
		return uipageCache.containsKey(entityName);
	}
	
	public static void removeUIPageCache(String pageName) {
		pageODCache.remove(pageName);
		uipageCache.remove(pageName);
	}
	
	public static void removeUIFormCache(String pageName) {
		odCache.remove(pageName);
		uiformCache.remove(pageName);
	}

}
