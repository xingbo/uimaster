package org.shaolin.bmdp.workflow.spi;

public interface Node extends Cloneable {
	
	public Object clone() throws CloneNotSupportedException;

	public String getAppName();

	public String getFlowName();

	public String getNodeName();

	public Node getParent();

	String getTimeoutJobId();

	public void setAppName(String name);

	public void setFlowName(String name);

	public void setNodeName(String name);

	/**
	 * destination node for its super flow
	 * 
	 * @param parent
	 */
	public void setParent(Node parent);

	void setTimeoutJobId(String timeoutJobId);
}
