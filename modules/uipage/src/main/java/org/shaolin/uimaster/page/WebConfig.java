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
package org.shaolin.uimaster.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.uimaster.page.flow.WebflowConstants;

public class WebConfig {

	public static final String DEFAULT_LOGIN_PATH = "/login.do";
	public static final String DEFAULT_ACTION_PATH = "/webflow.do";
	public static final String DEFAULT_INDEX_PAGE = "/jsp/index.jsp";
	public static final String DEFAULT_LOGIN_PAGE = "/jsp/login.jsp";
	public static final String DEFAULT_ILOGIN_PAGE = "/jsp/ilogin.jsp";
	public static final String DEFAULT_MAIN_PAGE = "/jsp/main.jsp";
	public static final String DEFAULT_ERROR_PAGE = "/jsp/common/Failure.jsp";
	public static final String DEFAULT_TIMEOUT_PAGE = "/jsp/common/sessionTimeout.jsp";
	
	public static final String KEY_REQUEST_UIENTITY = "_UIEntity";
	
	private static String servletContextPath = "";
	
	public static class WebConfigFastCache {
		public static final String WebContextRoot = "${webcontext}";
		public static final String ResourceContextRoot = "${resourceContext}";
		final String hiddenValueMask;
		final String cssRootPath;
		final String jsRootPath;
		final String ajaxServiceURL;
		final String frameWrap;
		final boolean isJAAS;
		final boolean isFormatHTML;
		final boolean hotdeployeable;
		final String loginPath;
		final String actionPath;
		final String indexPage;
		final String loginPage;
		final String iloginPage;
		final String mainPage;
		final String errorPage;
		final String nopermissionPage;
		final String timeoutPage;
		final String hasAjaxErrorHandler;
		final String[] commoncss;
		final String[] commonjs;
		final Map<String, String[]> singleCommonCss;
		final Map<String, String[]> singleCommonJs;
		
		public WebConfigFastCache() {
			Registry instance = Registry.getInstance();
			hiddenValueMask = instance.getValue(
					"/System/webConstant/hiddenValueMask");
			cssRootPath = ResourceContextRoot + "/css";
			jsRootPath = ResourceContextRoot + "/js";
			ajaxServiceURL = WebContextRoot + instance.getValue(
					"/System/webConstant/ajaxServiceURL");
			frameWrap = WebContextRoot + instance.getValue(
					"/System/webConstant/frameWrap");
			isJAAS = "true".equals(instance.getValue(
					"/System/webConstant/isJAAS"));
			isFormatHTML = "true".equals(instance.getValue(
					"/System/webConstant/formatHTML"));
			hotdeployeable = "true".equals(instance.getValue(
					"/System/webConstant/hotdeployeable"));
			loginPath = WebContextRoot + instance.getValue(
					"/System/webConstant/loginPath");
			actionPath = WebContextRoot + instance.getValue(
					"/System/webConstant/actionPath");
			indexPage = WebContextRoot + instance.getValue(
					"/System/webConstant/indexPage");
			loginPage = WebContextRoot + instance.getValue(
					"/System/webConstant/loginPage");
			iloginPage = WebContextRoot + instance.getValue(
					"/System/webConstant/iloginPage");
			mainPage = WebContextRoot + instance.getValue(
					"/System/webConstant/mainPage");
			errorPage = instance.getValue(
					"/System/webConstant/errorPage");
			nopermissionPage = instance.getValue(
					"/System/webConstant/nopermissionPage");
			timeoutPage = instance.getValue(
					"/System/webConstant/timeoutPage");
			hasAjaxErrorHandler = instance.getValue(
					"/System/webConstant/ajaxHandlingError");
			
			Collection<String> values = (Collection<String>)
					instance.getNodeItems("/System/webConstant/commoncss").values();
			commoncss = values.toArray(new String[values.size()]);
			for (int i=0; i<commoncss.length; i++) {
				if (!commoncss[i].startsWith("http")) {
					commoncss[i] = ResourceContextRoot + commoncss[i];
				}
			}
			values = instance.getNodeItems("/System/webConstant/commonjs").values();
			commonjs = values.toArray(new String[values.size()]);
			for (int i=0; i<commonjs.length; i++) {
				if (!commonjs[i].startsWith("http")) {
					commonjs[i] = ResourceContextRoot + commonjs[i];
				}
			}
			
			singleCommonCss = new HashMap<String, String[]>();
			String commonssPath = "/System/webConstant/commoncss";
			List<String> children = instance.getNodeChildren(commonssPath);
			if (children != null && children.size() > 0) {
				for (String child: children) {
					values = (Collection<String>)instance.getNodeItems(commonssPath + "/" + child).values();
					String[] items = values.toArray(new String[values.size()]);
					for (int i=0; i<items.length; i++) {
						items[i] = ResourceContextRoot + items[i];
					}
					singleCommonCss.put(child, items);
				}
			}
			
			singleCommonJs = new HashMap<String, String[]>();
			String commonjsPath = "/System/webConstant/commonjs";
			children = instance.getNodeChildren(commonjsPath);
			if (children != null && children.size() > 0) {
				for (String child: children) {
					values = (Collection<String>)instance.getNodeItems(commonjsPath + "/" + child).values();
					String[] items = values.toArray(new String[values.size()]);
					for (int i=0; i<items.length; i++) {
						items[i] = ResourceContextRoot + items[i];
					}
					singleCommonJs.put(child, items);
				}
			}
		}
	}
	
	private static WebConfigFastCache getCacheObject() {
		Registry instance = Registry.getInstance();
		if(!instance.existInFastCache("webconfig")) {
			WebConfigFastCache fastCache = new WebConfigFastCache();
			instance.putInFastCache("webconfig", fastCache);
		}
		return (WebConfigFastCache)instance.readFromFastCache("webconfig");
	}
	
	public static String getHiddenValueMask() {
		return getCacheObject().hiddenValueMask;
	}

	public static Hashtable<?, ?> getInitialContext() {
		Map<String, String> items = Registry.getInstance().getNodeItems(
				"/System/jndi");
		// TODO: doing values replacement.
		return new Hashtable(items);
	}

	public static String getUserLocale(HttpServletRequest request) {
		// read from session
		HttpSession session = request.getSession(true);
		String locale = (String) session
				.getAttribute(WebflowConstants.USER_LOCALE_KEY);
		if (locale != null) {
			return locale;
		}

		// read from cookie
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0, n = cookies.length; i < n; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(WebflowConstants.USER_LOCALE_KEY)) {
					locale = cookie.getValue();
					if (locale != null) {
						session.setAttribute(WebflowConstants.USER_LOCALE_KEY,
								locale);
						return locale;
					}
				}
			}
		}

		return null;
	}

	public static void setServletContextPath(String servletContextPath) {
		WebConfig.servletContextPath = servletContextPath;
	}
	
	public static String getRealPath(String relativePath) {
		return servletContextPath + relativePath;
	}
	
	private static String uploadFileRoot = "";
	
	public static void setUploadFileRoot(String path) {
		uploadFileRoot = path;
	}
	
	public static String getUploadFileRoot() {
		return uploadFileRoot + "/" + AppContext.get().getAppName();
	}
	
	public static String getWebRoot() {
		return "/" + AppContext.get().getAppName();
	}
	
	public static String getWebContextRoot() {
		return getWebRoot();
	}
	
	public static String getResourceContextRoot() {
		return "/uimaster";
	}
	
	private static String resourcePath = null;

	public static String getResourcePath() {
		return resourcePath;
	}
	
	public static void setResourcePath(String path) {
		resourcePath = path;
	}
	
	public static String getCssRootPath() {
		return getCacheObject().cssRootPath;
	}
	
	public static String getJsRootPath() {
		return getCacheObject().jsRootPath;
	}
	
	public static String getAjaxServiceURI() {
		return getCacheObject().ajaxServiceURL;
	}

	public static String getFrameWrap() {
		return getCacheObject().frameWrap;
	}

	public static String getActionPath() {
		return getCacheObject().actionPath;
	}

	public static String getLoginPath() {
		return getCacheObject().loginPath;
	}

	public static String getIndexPage() {
		return getCacheObject().indexPage;
	}

	public static String getLoginPage() {
		return getCacheObject().loginPage;
	}

	public static String getILoginPage() {
		return getCacheObject().iloginPage;
	}

	public static String getMainPage() {
		return getCacheObject().mainPage;
	}

	public static String getErrorPage() {
		return getCacheObject().errorPage;
	}

	public static String getNoPermissionPage() {
		return getCacheObject().nopermissionPage;
	}
	
	public static String getTimeoutPage() {
		return getCacheObject().timeoutPage;
	}

	public static String getTimeStamp() {
		return "1";
	}
	
	public static boolean hasAjaxErrorHandler() {
		return "true".equals(getCacheObject().hasAjaxErrorHandler);
	}

	public static boolean getIsJAAS() {
		return getCacheObject().isJAAS;
	}
	
	public static boolean isFormatHTML() {
		return getCacheObject().isFormatHTML;
	}
	
	public static boolean enableHotDeploy() {
		return getCacheObject().hotdeployeable;
	}
	
	public static String getImportJS(String entityName) {
		String name = entityName.replace('.', File.separatorChar);
		return WebConfigFastCache.ResourceContextRoot + "/js/" + name + ".js";
	}
	
	public static String replaceCssWebContext(String str) {
		return str.replace(WebConfigFastCache.ResourceContextRoot, WebConfig.getResourceContextRoot());
	}
	
	public static String replaceJsWebContext(String str) {
		return str.replace(WebConfigFastCache.ResourceContextRoot, WebConfig.getResourceContextRoot());
	}
	
	public static String replaceWebContext(String str) {
		return str.replace(WebConfigFastCache.WebContextRoot, WebConfig.getWebContextRoot());
	}
	
	/**
	 * no needs the root applied.
	 * @param entityName
	 * @return
	 */
	public static String getImportCSS(String entityName) {
		String name = entityName.replace('.', File.separatorChar);
		return WebConfigFastCache.ResourceContextRoot + "/css/" + name + ".css";
	}

	public static String[] getCommonCss() {
		return getCacheObject().commoncss;
	}

	public static String[] getCommonJs() {
		return getCacheObject().commonjs;
	}
	
	public static String[] getSingleCommonJS(String entityName) {
		String pack = entityName.substring(0, entityName.lastIndexOf('.'));
		Set<String> keys = getCacheObject().singleCommonJs.keySet();
		List<String> results = new ArrayList<String>();
		for (String key : keys) {
			if (key.startsWith("*")) {
				String keyA = key.substring(1, 2);
				if (keyA.endsWith(".*")) {
					keyA = keyA.substring(0, keyA.length() - 2);
					if (pack.indexOf(keyA) != -1) {
						String[] vs = getCacheObject().singleCommonJs.get(key);
						for (String v: vs) {
							results.add(v);
						}
					}
				} else {
					if (pack.lastIndexOf(keyA) != -1) {
						String[] vs = getCacheObject().singleCommonJs.get(key);
						for (String v: vs) {
							results.add(v);
						}
					}
				}
			}
			if (key.endsWith(".*")) {
				String keyPack = key.substring(0, key.length() - 2);
				if (pack.startsWith(keyPack)) {
					String[] vs = getCacheObject().singleCommonJs.get(key);
					for (String v: vs) {
						results.add(v);
					}
				}
			}
		}
		// keep the load sequence.
		for (String key : keys) {
			if (key.equals(pack)) {
				results.add(key);
			} 
		} 
		return results.toArray(new String[results.size()]);
	}
	
	public static String[] getSingleCommonCSS(String entityName) {
		String pack = entityName.substring(0, entityName.lastIndexOf('.'));
		Set<String> keys = getCacheObject().singleCommonCss.keySet();
		List<String> results = new ArrayList<String>();
		for (String key : keys) {
			if (key.endsWith(".*")) {
				String keyPack = key.substring(0, key.length() - 2);
				if (pack.startsWith(keyPack)) {
					String[] vs = getCacheObject().singleCommonCss.get(key);
					for (String v: vs) {
						results.add(v);
					}
				}
			}
		}
		// keep the load sequence.
		for (String key : keys) {
			if (key.equals(pack)) {
				results.add(key);
			} 
		} 
		return results.toArray(new String[results.size()]);
	}

	private static Map skinSettingMap = new ConcurrentHashMap();

	/**
	 * get css root path
	 * 
	 * @param entityName
	 *            the ui entity name
	 * 
	 * @return css root path
	 * 
	 */
	public static String getJspUrl(String entityName) {
		return getJspUrl(entityName, "");
	}

	/**
	 * suffix: UIENTITY_JSP_SUFFIX,ODMAPPER_JSP_SUFFIX,UIPAGE_HTML_JSP_SUFFIX
	 */
	public static String getJspUrl(String entityName, String componentType) {
		// TODO:
		return "";
	}

	public static String getSystemUISkin(String componentTypeName) {
		return (String) skinSettingMap.get(componentTypeName);
	}

	public static String getDefaultJspName(String entityName) {
		int index = entityName.indexOf(".uientity.");
		if (index != -1) {
			return entityName.substring(0, index) + ".uientityhtml."
					+ entityName.substring(index + 10);
		}

		index = entityName.indexOf(".uipage.");
		if (index != -1) {
			return entityName.substring(0, index) + ".uipagehtml."
					+ entityName.substring(index + 8);
		}

		index = entityName.indexOf(".od.");
		if (index != -1) {
			return entityName.substring(0, index) + ".odhtml."
					+ entityName.substring(index + 4);
		}

		return entityName + "HTML";
	}

	/**
	 * Determine whether entity name and pattern is matched Currently use java
	 * package like styles, support ** for all package Maybe we will use regular
	 * expression pattern sometime later
	 */
	private static boolean matchPattern(String entityName, String pattern) {
		boolean matched = false;
		if (entityName.equals(pattern)) {
			matched = true;
		} else if (pattern.endsWith(".**")) {
			String packageName = pattern.substring(0, pattern.length() - 3);
			if (entityName.startsWith(packageName)) {
				matched = true;
			}
		}
		return matched;
	}

	private static String convertJSPath(String input) {
		int curIndex = input.indexOf('.');
		if (curIndex == -1) {
			return input + ".js";
		}
		int nextIndex = input.indexOf('.', curIndex + 1);
		if (nextIndex == -1) {
			return input;
		}
		StringBuffer sb = new StringBuffer(input.substring(0, curIndex));
		do {
			sb.append('/');
			sb.append(input.substring(curIndex + 1, nextIndex));

			curIndex = nextIndex;
			nextIndex = input.indexOf('.', curIndex + 1);
		} while (nextIndex != -1);
		sb.append(input.substring(curIndex));
		return new String(sb);
	}

}
