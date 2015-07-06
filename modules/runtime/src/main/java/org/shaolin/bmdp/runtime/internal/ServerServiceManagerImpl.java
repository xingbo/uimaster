package org.shaolin.bmdp.runtime.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.ce.ConstantServiceImpl;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.IConstantService;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IRegistry;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Here is only one server service manager while running on multiple
 * applications. To reduce our cost of memory consuming.
 * 
 */
public class ServerServiceManagerImpl implements IServerServiceManager {

	private static Logger logger = LoggerFactory.getLogger(ServerServiceManagerImpl.class);

	private String masterNodeName;
	
	private final Registry registry;

	private final IEntityManager entityManager;

	private final Map<Class<?>, IServiceProvider> services = new HashMap<Class<?>, IServiceProvider>();

	private final Map<String, IAppServiceManager> applications = new HashMap<String, IAppServiceManager>();
	
	private final IConstantService constantService;
	
	private Object hibernateSessionFactory;
	
	private Object hibernateConfiguration;
	
	public ServerServiceManagerImpl() {
		this.registry = Registry.getInstance();
		this.entityManager = new EntityManager();
		this.constantService = new ConstantServiceImpl();
	}
	
	@Override
	public String getMasterNodeName() {
		return masterNodeName;
	}

	public void setMasterNodeName(String masterNodeName) {
		this.masterNodeName = masterNodeName;
	}

	@Override
	public IRegistry getRegistry() {
		return registry;
	}

	@Override
	public IEntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public IConstantService getConstantService() {
		return constantService;
	}
	
	@Override
	public void addApplication(String name, IAppServiceManager app) {
		logger.info("Add an application: " + name);
		applications.put(name, app);
	}
	
	@Override
	public IAppServiceManager removeApplication(String name) {
		logger.info("Remove an application: " + name);
		return applications.remove(name);
	}

	@Override
	public IAppServiceManager getApplication(String name) {
		return applications.get(name);
	}
	
	@Override
	public String[] getApplicationNames() {
		Set<String> keys = applications.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	@Override
	public void register(IServiceProvider service) {
		if (services.containsKey(service.getServiceInterface())) {
			throw new IllegalArgumentException(
					"The server service has already registered!"
							+ service.getServiceInterface());
		}
		logger.info("Register service: " + service.getServiceInterface());
		services.put(service.getServiceInterface(), service);
	}

	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> serviceClass) {
		return (T) services.get(serviceClass);
	}

	public int getServiceSize() {
		return this.services.size();
	}

	@Override
	public Object getHibernateConfiguration() {
		return hibernateConfiguration;
	}

	public void setHibernateConfiguration(Object hibernateConfiguration) {
		this.hibernateConfiguration = hibernateConfiguration;
	}

	@Override
	public Object getHibernateSessionFactory() {
		return hibernateSessionFactory;
	}

	public void setHibernateSessionFactory(Object sessionFactory) {
		this.hibernateSessionFactory = sessionFactory;
	}

}
