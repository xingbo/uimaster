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
package org.shaolin.bmdp.runtime.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.IConstantService;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.ILifeCycleProvider;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Here is only one app service manager for one application.
 * 
 */
public class AppServiceManagerImpl implements IAppServiceManager, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(AppServiceManagerImpl.class);

	private final String appName;

	private final transient List<ILifeCycleProvider> lifeCycleProviders = new ArrayList<ILifeCycleProvider>();

	private final transient Map<Class<?>, IServiceProvider> services = new HashMap<Class<?>, IServiceProvider>();
	
	private final transient ClassLoader appClassLoader;

	private transient Object hibernateSessionFactory;
	
	private transient Object hibernateConfiguration;
	
	private final IEntityManager entityManager;
	
	public AppServiceManagerImpl(String appName, ClassLoader appClassLoader) {
		this.appName = appName;
		this.appClassLoader = appClassLoader;
		this.entityManager = new EntityManager(appName);
	}
	
	public String getAppName() {
		return this.appName;
	}
	
	public IEntityManager getEntityManager() {
		return this.entityManager;
	}
	
	public Object getHibernateConfiguration() {
		return hibernateConfiguration;
	}

	public void setHibernateConfiguration(Object hibernateConfiguration) {
		this.hibernateConfiguration = hibernateConfiguration;
	}

	public Object getHibernateSessionFactory() {
		return hibernateSessionFactory;
	}

	public void setHibernateSessionFactory(Object sessionFactory) {
		this.hibernateSessionFactory = sessionFactory;
	}
	
	public ClassLoader getAppClassLoader() {
		return this.appClassLoader;
	}
	
	@Override
	public IConstantService getConstantService() {
		return IServerServiceManager.INSTANCE.getConstantService();
	}

	@Override
	public void registerLifeCycleProvider(ILifeCycleProvider provider) {
		logger.info("Register life cycle service: " + provider + ", this: "
				+ this.hashCode());
		lifeCycleProviders.add(provider);
	}

	public void startLifeCycleProviders() {
		List<ILifeCycleProvider> temp = new ArrayList<ILifeCycleProvider>();
		for (ILifeCycleProvider provider : lifeCycleProviders) {
			temp.add(provider);
		}

		int i = 0;
		while (temp.size() > 0) {
			for (int j = 0; j < temp.size(); j++) {
				if (temp.get(j).getRunLevel() == i) {
					ILifeCycleProvider p = temp.remove(j);
					logger.info("Start life cycle service: " + p);
					p.startService();
					i = 0;
					continue;
				}
			}
			i++;
		}
	}

	public void stopLifeCycleProviders() {
		List<ILifeCycleProvider> temp = new ArrayList<ILifeCycleProvider>();
		for (ILifeCycleProvider provider : lifeCycleProviders) {
			temp.add(provider);
		}

		int i = 0;
		while (temp.size() > 0) {
			for (int j = 0; j < temp.size(); j++) {
				if (temp.get(j).getRunLevel() == i) {
					ILifeCycleProvider p = temp.remove(j);
					logger.info("Stop life cycle service: " + p);
					p.stopService();
					i = 0;
					continue;
				}
			}
			i++;
		}
	}

	@Override
	public void register(IServiceProvider service) {
		if (services.containsKey(service.getServiceInterface())) {
			logger.warn("The application service has already registered!"
							+ service.getServiceInterface());
		}
		logger.info("Register service: " + service.getServiceInterface());
		services.put(service.getServiceInterface(), service);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> serviceClass) {
		return (T) services.get(serviceClass);
	}

	public int getServiceSize() {
		return this.services.size();
	}

	public int getLifeCycleServiceSize() {
		return this.lifeCycleProviders.size();
	}
}
