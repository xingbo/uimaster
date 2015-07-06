package org.shaolin.bmdp.runtime.spi;

import java.util.List;

import org.shaolin.bmdp.runtime.ce.IConstantEntity;

public interface IConstantService {

	/**
	 * Get a constant.
	 * 
	 * @param ceName
	 * @return
	 */
	public IConstantEntity getConstantEntity(String ceName);
	
	/**
	 * Whether has the constant entity or not.
	 * 
	 * @param ceName
	 * @return
	 */
	public boolean hasConstantEntity(String ceName);
	
	/**
	 * Query for all constants by condition.
	 * 
	 * @param condition
	 * @param offset
	 * @param count
	 * @return
	 */
	public List<IConstantEntity> getServerConstants(IConstantEntity condition, int offset, int count);
	
	/**
	 * Return all constants size.
	 * 
	 * @param condition
	 * @return
	 */
	public int getServerConstantCount(IConstantEntity condition);
	
}
