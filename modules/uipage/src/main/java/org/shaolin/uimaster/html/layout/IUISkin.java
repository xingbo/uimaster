package org.shaolin.uimaster.html.layout;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

public interface IUISkin {
	/**
	 * set UISkin parameter.
	 */
	public void setParam(String name, String value) throws JspException;

	/**
	 * decide whether to invke generateHTML way of UI Component,default false.
	 */
	public boolean isOverwrite();

	/**
	 * AttributeMap is defined in UISkin,the way overrides and references the
	 * same name of UI Component in UISkin.
	 */
	public Map getAttributeMap(HTMLWidgetType component);

	/**
	 * Generate html code,it is invoked before the invoke of generateBeginHTML
	 * in UI Component. To get the parameters that defined in UISkin, use
	 * getParam(name) method. To get system variable, use context.getXXX()
	 * method.
	 */
	public void generatePreCode(HTMLWidgetType component) throws IOException;

	/**
	 * Generate html code,it is invoked after the invoke of generateEndHTML in
	 * UI Component.
	 */
	public void generatePostCode(HTMLWidgetType component) throws IOException;

	// --------------------------------------------
	// The following Api is mainly for the usage of Studio.
	// --------------------------------------------
	/**
	 * get the type of the UISkin widget
	 */
	public Class<?> getUIComponentType();

	/**
	 * get Array of UISkin Parameter Name
	 */
	public String[] getParamNames();

}
