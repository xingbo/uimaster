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
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.uimaster.page.WebConfig;

public class UIPageObject implements java.io.Serializable {
	private static final long serialVersionUID = -3835708230178517577L;

	private static final String DEFAULT_LOCALE = "DEFAULT_LOCALE";

	private String entityName = null;

	private UIFormObject ui = null;

	private Map<String, String> cssCodeMap = new HashMap<String, String>();

	private Map<String, List<String>> importCSSCodeMap = new HashMap<String, List<String>>();

	private final StringBuffer pageCSS = new StringBuffer();

	public UIPageObject(String entityName) {
		this.entityName = entityName;
		load();
	}

	private void load() {
		UIPage entity = IServerServiceManager.INSTANCE.getEntityManager()
				.getEntity(entityName, UIPage.class);
		ui = new UIFormObject(entityName, entity);

		addCSS(DEFAULT_LOCALE);

		String importCSS = WebConfig.getImportCSS(entityName);
		String cssCode = "<link rel=\"stylesheet\" href=\"" + importCSS
				+ "\" type=\"text/css\">\n";
		cssCodeMap.put(DEFAULT_LOCALE, cssCode);

		importCSS();
	}

	private void addCSS(String locale) {
		List<String> importCSSCode = new ArrayList<String>();
		String[] css = WebConfig.getSingleCommonCSS(entityName);
		if (css != null) {
			for (int i = 0; i < css.length; i++) {
				importCSSCode.add("<link rel=\"stylesheet\" href=\"" + css[i]
						+ "\" type=\"text/css\">\n");
			}
		}
		String[] common = WebConfig.getCommonCss();
		for (int i = 0; common != null && i < common.length; i++) {
			importCSSCode.add("<link rel=\"stylesheet\" href=\"" + common[i]
					+ "\" type=\"text/css\">\n");
		}
		importCSSCodeMap.put(locale, importCSSCode);
	}

	private void addCSSFile(String locale) {
		String cssCode = "<link rel=\"stylesheet\" href=\""
				+ WebConfig.getImportCSS(entityName)
				+ "\" type=\"text/css\">\n";
		cssCodeMap.put(locale, cssCode);
	}

	private void importCSS() {
		String userLocale = LocaleContext.getUserLocale();
		if (userLocale != null && !userLocale.trim().equals("EBOS_DEFAULT")) {
			if (!importCSSCodeMap.containsKey(userLocale)) {
				addCSS(userLocale);
				addCSSFile(userLocale);
			}
		} else {
			userLocale = DEFAULT_LOCALE;
		}

		List<String> importCSSCode = (List<String>) importCSSCodeMap.get(userLocale);
		Iterator<String> iterator = importCSSCode.iterator();
		while (iterator.hasNext()) {
			String code = iterator.next();
			pageCSS.append(code);
		}
		pageCSS.append((String) cssCodeMap.get(userLocale));
	}

	public Map getComponentProperty(String componentID) {
		return ui.getComponentProperty(componentID);
	}

	public UIFormObject getUIForm() {
		return ui;
	}

	public String getEntityName() {
		return entityName;
	}

	public UIFormObject getUi() {
		return ui;
	}

	public StringBuffer getPageCSS() {
		return pageCSS;
	}
}
