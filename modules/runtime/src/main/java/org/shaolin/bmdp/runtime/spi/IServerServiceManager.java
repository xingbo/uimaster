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

import org.shaolin.bmdp.runtime.internal.ServerServiceManagerImpl;

/**
 * 
 * @author wushaol
 */
public interface IServerServiceManager {

	public static final IServerServiceManager INSTANCE = new ServerServiceManagerImpl();

	/**
	 * Get the name of the master node.
	 * 
	 * @return
	 */
	public String getMasterNodeName();
	
	/**
	 * Get registry
	 * 
	 * @return
	 */
	IRegistry getRegistry();

	/**
	 * Get the shared entity manager.
	 * 
	 * @return
	 */
	IEntityManager getEntityManager();

	/**
	 * Get system constant service.
	 * 
	 * @return
	 */
	IConstantService getConstantService();
	
	/**
	 * Get system scheduler service.
	 * 
	 * @return
	 */
	ISchedulerService getSchedulerService();
	
	/**
	 * Register a normal service.
	 * 
	 * @param service
	 */
	void register(IServiceProvider service);

	/**
	 * Get service.
	 * 
	 * @param serviceClass
	 * @return
	 */
	<T> T getService(Class<T> serviceClass);

	/**
	 * Add an application.
	 * 
	 * @param name
	 * @param app
	 */
	void addApplication(String name, IAppServiceManager app);

	/**
	 * Get an application.
	 * 
	 * @param name
	 * @return
	 */
	IAppServiceManager getApplication(String name);

	/**
	 * This method is only invoked while stutting down the server.
	 * 
	 * @param name
	 * @return
	 */
	IAppServiceManager removeApplication(String name);
	
	/**
	 * Get all applications' names.
	 * 
	 * @return
	 */
	String[] getApplicationNames();

	/********DB access APIs*********/
	Object getHibernateConfiguration();


	Object getHibernateSessionFactory();
}
