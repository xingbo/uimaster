package org.shaolin.bmdp.runtime.be;

import java.io.Serializable;
import java.util.List;

import org.shaolin.bmdp.datamodel.bediagram.MemberType;

/**
 * Interface of Business Entity. All Business Entity must implement this
 * interface.
 * 
 * @author Shaolin
 */
public interface IBusinessEntity extends Serializable {
	
	/**
	 * Gets list of MemberType.
	 * 
	 * @return List the list of MemberType.
	 */
	public List<MemberType> getMemberList();
	
	public IBusinessEntity createEntity();
	
}
