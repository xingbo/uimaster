package org.shaolin.bmdp.runtime.be;

/**
 * Interface of Business Entity which needs to be historical and persistent.
 * 
 */
public interface IHistoryEntity extends IPersistentEntity {
	public final static String VERSION = "_version";
	public final static String STARTTIME = "_starttime";
	public final static String ENDTIME = "_endtime";
	public final static String OPTUSERID = "_optuserid";
	
	/**
	 * get version
	 * 
	 * @return version
	 */
	public int getVersion();

	/**
	 * set version
	 * 
	 * @param version
	 *            which is 0 when create and increase when record history.
	 */
	public void setVersion(int version);

	/**
	 * get start time
	 * 
	 * @return start time
	 */
	public long getStarttime();

	/**
	 * set start time
	 * 
	 * @param starttime
	 *            time which is the start time of object.
	 */
	public void setStarttime(long starttime);

	/**
	 * get endtime
	 * 
	 * @return endtime
	 */
	public long getEndtime();

	/**
	 * set endtime
	 * 
	 * @param endtime
	 *            which is the end time of object.
	 */
	public void setEndtime(long endtime);
	
	/**
	 * 
	 * @param optUserId
	 */
	public void setOptUserId(long optUserId);
	
	/**
	 * 
	 * @return
	 */
	public long getOptUserId();

}
