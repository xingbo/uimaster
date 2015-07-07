/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
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
