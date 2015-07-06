package org.shaolin.bmdp.workflow.spi;

public interface WorkflowSession {
	
	public static final int COMMIT = 1;
	public static final int ROLLBACK = 0;
	public static final int NOTHING = 2;

	/**
	 * Get the session id.
	 * 
	 * @return The session id.
	 */
	public String getID();

	/**
	 * @see COMMIT
	 * @see ROLLBACK
	 * @see NOTHING
	 * 
	 * @return Get the transaction status flag.
	 */
	public int getTXFlag();

}
