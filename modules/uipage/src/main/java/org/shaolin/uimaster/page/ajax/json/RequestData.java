package org.shaolin.uimaster.page.ajax.json;

public class RequestData implements IRequestData {
	private static final long serialVersionUID = 0x90B1123CE87B50FFL;

	protected String getBusinessEntityName() {
		return "org.shaolin.uimaster.page.ajax.json.RequestData";
	}

	public RequestData() {
		data = new java.util.HashMap();
	}

	/**
	 * help is not available
	 */

	protected java.lang.String uiid = "";

	/**
	 * help is not available
	 */

	protected java.lang.String entityUiid = "";

	/**
	 * help is not available
	 */

	protected java.util.Map data;

	protected String frameId = "";

	protected java.lang.String entityName;

	protected String bAName;

	/**
	 * get uiid
	 * 
	 * @return uiid
	 */
	public java.lang.String getUiid() {
		return uiid;
	}

	/**
	 * get entityUiid
	 * 
	 * @return entityUiid
	 */
	public java.lang.String getEntityUiid() {
		return entityUiid;
	}

	/**
	 * get data
	 * 
	 * @return data
	 */
	public java.util.Map getData() {
		return data;
	}

	/**
	 * set uiid
	 */
	public void setUiid(java.lang.String uiid) {

		this.uiid = uiid;

	}

	/**
	 * set entityUiid
	 */
	public void setEntityUiid(java.lang.String entityUiid) {

		this.entityUiid = entityUiid;

	}

	/**
	 * set data
	 */
	public void setData(java.util.Map data) {

		this.data = data;

	}

	/**
	 * Gets the String format of the business entity.
	 * 
	 * @return String the business entity in String format.
	 */
	public String toString() {
		StringBuffer aBuf = new StringBuffer();
		aBuf.append("org.shaolin.uimaster.page.ajax.json.RequestData");

		aBuf.append(" : ");

		aBuf.append("uiid");
		aBuf.append("=");
		aBuf.append(uiid);
		aBuf.append(", ");

		aBuf.append("entityUiid");
		aBuf.append("=");
		aBuf.append(entityUiid);
		aBuf.append(", ");

		aBuf.append("bAName");
		aBuf.append("=");
		aBuf.append(bAName);
		aBuf.append(", ");

		aBuf.append("entityName");
		aBuf.append("=");
		aBuf.append(entityName);
		return aBuf.toString();
	}

	public String getFrameId() {
		return frameId;
	}

	public void setFrameId(String frameId) {
		this.frameId = frameId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getBAName() {
		return bAName;
	}

	public void setBAName(String bAName) {
		this.bAName = bAName;
	}

}
