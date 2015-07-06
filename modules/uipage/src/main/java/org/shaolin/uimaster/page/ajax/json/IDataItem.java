package org.shaolin.uimaster.page.ajax.json;

import java.util.Map;

public interface IDataItem extends java.io.Serializable {
	public final static String ENTITY_NAME = "org.shaolin.uimaster.page.ajax.json.DataItem";

	// getter methods block

	/**
	 * get uiid
	 * 
	 * @return uiid
	 */
	public java.lang.String getUiid();

	/**
	 * get parent
	 * 
	 * @return parent
	 */
	public java.lang.String getParent();

	/**
	 * get sibling
	 * 
	 * @return sibling
	 */
	public java.lang.String getSibling();

	/**
	 * get jsHandler
	 * 
	 * @return jsHandler
	 */
	public java.lang.String getJsHandler();

	/**
	 * get data
	 * 
	 * @return data
	 */
	public java.lang.String getData();

	/**
	 * get js
	 * 
	 * @return js
	 */
	public java.lang.String getJs();

	/**
	 * get frameInfo
	 * 
	 * @return frameInfo
	 */
	public java.lang.String getFrameInfo();

	// setter methods block

	/**
	 * set uiid
	 */
	public void setUiid(java.lang.String uiid);

	/**
	 * set parent
	 */
	public void setParent(java.lang.String parent);

	/**
	 * set sibling
	 */
	public void setSibling(java.lang.String sibling);

	/**
	 * set jsHandler
	 */
	public void setJsHandler(java.lang.String jsHandler);

	/**
	 * set data
	 */
	public void setData(java.lang.String data);

	/**
	 * set js
	 */
	public void setJs(java.lang.String js);

	/**
	 * set frameInfo
	 */
	public void setFrameInfo(java.lang.String frameInfo);

	/**
	 * additional items
	 * 
	 * @param name
	 * @param value
	 */
	public void addItem(String name, String value);

	public String getItem(String name);

	/**
	 * json string.
	 * 
	 * @return
	 */
	public String getItems();

	public void setLayout(boolean isLayout);

	public boolean isLayout();
}