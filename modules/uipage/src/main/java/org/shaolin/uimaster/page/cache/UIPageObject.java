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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.uimaster.page.MobilitySupport;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.security.UserContext;

public class UIPageObject implements java.io.Serializable {
	private static final long serialVersionUID = -3835708230178517577L;

	private static final String DEFAULT_LOCALE = "DEFAULT_LOCALE";

	private String entityName = null;

	private UIFormObject ui = null;

	private Map<String, String> cssCodeMap = new HashMap<String, String>();

	private Map<String, List<String>> importCSSCodeMap = new HashMap<String, List<String>>();

	private final StringBuffer pageCSS = new StringBuffer();
	
	private Map<String, List<String>> importMobCSSCodeMap = new HashMap<String, List<String>>();

	private final StringBuffer mobPageCSS = new StringBuffer();
	
	private boolean hasMobilePage = false;

	public UIPageObject(String entityName) {
		this.entityName = entityName;
		load();
	}

	private void load() {
		UIPage entity = IServerServiceManager.INSTANCE.getEntityManager()
				.getEntity(entityName, UIPage.class);
		
		try {
			IServerServiceManager.INSTANCE.getEntityManager()
				.getEntity(entityName + MobilitySupport.MOB_PAGE_SUFFIX, UIPage.class);
			hasMobilePage = true;
        } catch (EntityNotFoundException e) {
        	hasMobilePage = false;
        }
		
		ui = new UIFormObject(entityName, entity);

		addCSS(DEFAULT_LOCALE, false);
		addCSS(DEFAULT_LOCALE, true);
		
		String importCSS = WebConfig.getImportCSS(entityName);
		String cssCode = "<link rel=\"stylesheet\" href=\"" + importCSS
				+ "\" type=\"text/css\">\n";
		cssCodeMap.put(DEFAULT_LOCALE, cssCode);

		importCSS();
		importMobCSS();
	}

	private void addCSS(String locale, boolean isMobile) {
		List<String> importCSSCode = new ArrayList<String>();
		String[] css = WebConfig.getSingleCommonCSS(entityName);
		if (css != null) {
			for (int i = 0; i < css.length; i++) {
				importCSSCode.add("<link rel=\"stylesheet\" href=\"" + css[i]
						+ "\" type=\"text/css\">\n");
			}
		}
		String[] common = WebConfig.getCommonCss();
		if (isMobile) {
			common = WebConfig.getCommonMobCss();
		}
		for (int i = 0; common != null && i < common.length; i++) {
			importCSSCode.add("<link rel=\"stylesheet\" href=\"" + common[i]
					+ "\" type=\"text/css\">\n");
		}
		if (isMobile) {
			importMobCSSCodeMap.put(locale, importCSSCode);
		} else {
			importCSSCodeMap.put(locale, importCSSCode);
		}
	}

	private void addCSSFile(String locale) {
		String cssCode = "<link rel=\"stylesheet\" href=\""
				+ WebConfig.getImportCSS(entityName)
				+ "\" type=\"text/css\">\n";
		cssCodeMap.put(locale, cssCode);
	}

	private void importCSS() {
//		String userLocale = LocaleContext.getUserLocale();
//		if (userLocale != null && !userLocale.trim().equals(DEFAULT_LOCALE)) {
//			if (!importCSSCodeMap.containsKey(userLocale)) {
//				addCSS(userLocale, false);
//				addCSSFile(userLocale);
//			}
//		} else {
//			userLocale = DEFAULT_LOCALE;
//		}

		List<String> importCSSCode = (List<String>) importCSSCodeMap.get(DEFAULT_LOCALE);
		Iterator<String> iterator = importCSSCode.iterator();
		while (iterator.hasNext()) {
			String code = iterator.next();
			pageCSS.append(code);
		}
		pageCSS.append((String) cssCodeMap.get(DEFAULT_LOCALE));
	}
	
	private void importMobCSS() {
//		String userLocale = LocaleContext.getUserLocale();
//		if (userLocale != null && !userLocale.trim().equals(DEFAULT_LOCALE)) {
//			if (!importCSSCodeMap.containsKey(userLocale)) {
//				addCSS(userLocale, true);
//				addCSSFile(userLocale);
//			}
//		} else {
//			userLocale = DEFAULT_LOCALE;
//		}

		List<String> importCSSCode = (List<String>) importMobCSSCodeMap.get(DEFAULT_LOCALE);
		Iterator<String> iterator = importCSSCode.iterator();
		while (iterator.hasNext()) {
			String code = iterator.next();
			mobPageCSS.append(code);
		}
		mobPageCSS.append((String) cssCodeMap.get(DEFAULT_LOCALE));
	}

	public UIFormObject getUIForm() {
		if (UserContext.isMobileRequest() && hasMobilePage()) {
			return PageCacheManager.getUIPageObject(getRuntimeEntityName()).ui;
		}
		
		return ui;
	}

	public String getRuntimeEntityName() {
		if (UserContext.isMobileRequest() && hasMobilePage()) {
			return entityName + MobilitySupport.MOB_PAGE_SUFFIX;
		}
		return entityName;
	}

	public UIFormObject getUIFormObject() {
		if (UserContext.isMobileRequest() && hasMobilePage()) {
			return PageCacheManager.getUIPageObject(getRuntimeEntityName()).ui;
		}
		
		return ui;
	}

	public StringBuffer getPageCSS() {
		return pageCSS;
	}
	
	public StringBuffer getMobPageCSS() {
		return mobPageCSS;
	}
	
	public boolean hasMobilePage() {
		return hasMobilePage;
	}
}
