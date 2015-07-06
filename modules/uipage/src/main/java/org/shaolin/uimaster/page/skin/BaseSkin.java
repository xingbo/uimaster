package org.shaolin.uimaster.page.skin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.shaolin.uimaster.html.layout.IUISkin;

public abstract class BaseSkin implements IUISkin {
	private final Map<String, String> paramMap;

	public BaseSkin() {
		paramMap = new HashMap<String, String> ();
		initParam();
	}

	public final void setParam(String name, String value) throws JspException {
		if (!paramMap.containsKey(name)) {
			throw new JspException("the parameter '" + name + "' is not defined in the skin.");
		}
		paramMap.put(name, value);
	}

	public final String[] getParamNames() {
		return (String[]) paramMap.keySet().toArray(new String[] {});
	}

	protected final String getParam(String name) {
		return (String) paramMap.get(name);
	}

	protected final void addParam(String name, String defaultValue) {
		paramMap.put(name, defaultValue);
	}

	protected abstract void initParam();

}
