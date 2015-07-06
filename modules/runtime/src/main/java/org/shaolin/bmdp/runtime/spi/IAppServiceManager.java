package org.shaolin.bmdp.runtime.spi;

/**
 * The application service made for a single application
 * 
 * @author wushaol
 *
 */
public interface IAppServiceManager {

	/**
	 * the name of current application.
	 * 
	 * @return
	 */
	String getAppName();
	
	/**
	 * The life cycle provider only invokes when the system is up.
	 * 
	 * @param provider
	 */
	void registerLifeCycleProvider(ILifeCycleProvider provider);
	
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
	 * Application owners' entity manager. this purposes on the application entity customization.
	 * 
	 * @return
	 */
	IEntityManager getEntityManager();
	
	/**
	 * Get constant service
	 * 
	 * @return
	 */
	IConstantService getConstantService();
	
	/**
	 * Get application class loader(ServletClassLoader)
	 * 
	 * @return
	 */
	ClassLoader getAppClassLoader();
	
	
	/********DB access APIs*********/
	Object getHibernateConfiguration();


	Object getHibernateSessionFactory();

}
