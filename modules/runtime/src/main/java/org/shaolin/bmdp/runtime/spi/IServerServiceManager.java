package org.shaolin.bmdp.runtime.spi;

import org.shaolin.bmdp.runtime.internal.ServerServiceManagerImpl;

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
