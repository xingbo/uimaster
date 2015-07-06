/*
 *
 * Copyright 2000-2003 by BMIAsia, Inc.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of BMIAsia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMIAsia.
 *
 * Generated Code, Please DO NOT modify !!
 * Generated on Wed Apr 08 14:04:33 CST 2009
 */

package org.shaolin.uimaster.page.ajax.json;

import java.util.HashMap;

public class DataItem implements IDataItem {
	private static final long serialVersionUID = 0x90B1123CE87B50FFL;

	public DataItem() {

	}

	/**
	 * help is not available
	 */

	protected java.lang.String uiid;

	/**
	 * help is not available
	 */

	protected java.lang.String parent;

	/**
	 * help is not available
	 */

	protected java.lang.String sibling;

	/**
	 * help is not available
	 */

	protected java.lang.String jsHandler;

	/**
	 * help is not available
	 */

	protected java.lang.String data;

	/**
	 * help is not available
	 */

	protected java.lang.String js;

	/**
	 * help is not available
	 */
	protected boolean isLayout;

	/**
	 * 
	 */
	protected java.lang.String frameInfo;

	/**
	 * 
	 */
	protected java.util.Map items;

	/**
	 * get uiid
	 * 
	 * @return uiid
	 */
	public java.lang.String getUiid() {
		return uiid;
	}

	/**
	 * get parent
	 * 
	 * @return parent
	 */
	public java.lang.String getParent() {
		return parent;
	}

	/**
	 * get sibling
	 * 
	 * @return sibling
	 */
	public java.lang.String getSibling() {
		return sibling;
	}

	/**
	 * get jsHandler
	 * 
	 * @return jsHandler
	 */
	public java.lang.String getJsHandler() {
		return jsHandler;
	}

	/**
	 * get data
	 * 
	 * @return data
	 */
	public java.lang.String getData() {
		return data;
	}

	/**
	 * get js
	 * 
	 * @return js
	 */
	public java.lang.String getJs() {
		return js;
	}

	/**
	 * get frameInfo
	 * 
	 * @return frameInfo
	 */
	public java.lang.String getFrameInfo() {
		return frameInfo;
	}

	/**
	 * set uiid
	 */
	public void setUiid(java.lang.String uiid) {

		this.uiid = uiid;

	}

	/**
	 * set parent
	 */
	public void setParent(java.lang.String parent) {

		this.parent = parent;

	}

	/**
	 * set sibling
	 */
	public void setSibling(java.lang.String sibling) {

		this.sibling = sibling;

	}

	/**
	 * set jsHandler
	 */
	public void setJsHandler(java.lang.String jsHandler) {

		this.jsHandler = jsHandler;

	}

	/**
	 * set data
	 */
	public void setData(java.lang.String data) {

		this.data = data;

	}

	/**
	 * set js
	 */
	public void setJs(java.lang.String js) {

		this.js = js;

	}

	public boolean isLayout() {
		return isLayout;
	}

	public void setLayout(boolean isLayout) {
		this.isLayout = isLayout;
	}

	/**
	 * set frameInfo
	 */
	public void setFrameInfo(java.lang.String frameInfo) {
		this.frameInfo = frameInfo;
	}

	public synchronized void addItem(String name, String value) {
		if (this.items == null) {
			this.items = new HashMap();
		}
		this.items.put(name, value);
	}

	public String getItem(String name) {
		if (this.items != null) {
			return (String) this.items.get(name);
		}
		return null;
	}

	public String getItems() {
		return (new JSONObject(this.items)).toString();
	}

	/**
	 * Gets the String format of the business entity.
	 * 
	 * @return String the business entity in String format.
	 */
	public String toString() {
		StringBuffer aBuf = new StringBuffer();
		aBuf.append("DataItem");

		aBuf.append(" : ");

		aBuf.append("uiid");
		aBuf.append("=");
		aBuf.append(uiid);
		aBuf.append(", ");

		aBuf.append("parent");
		aBuf.append("=");
		aBuf.append(parent);
		aBuf.append(", ");

		aBuf.append("sibling");
		aBuf.append("=");
		aBuf.append(sibling);
		aBuf.append(", ");

		aBuf.append("jsHandler");
		aBuf.append("=");
		aBuf.append(jsHandler);
		aBuf.append(", ");

		aBuf.append("data");
		aBuf.append("=");
		aBuf.append(data);
		aBuf.append(", ");

		aBuf.append("js");
		aBuf.append("=");
		aBuf.append(js);
		aBuf.append(", ");

		aBuf.append("frameInfo");
		aBuf.append("=");
		aBuf.append(frameInfo);

		aBuf.append("items");
		aBuf.append("=");
		aBuf.append(items != null ? items.toString() : "");

		return aBuf.toString();
	}

}
