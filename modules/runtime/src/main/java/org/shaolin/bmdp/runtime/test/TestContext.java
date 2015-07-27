package org.shaolin.bmdp.runtime.test;

import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;

public class TestContext {

	protected static final AppServiceManagerImpl appContext = new AppServiceManagerImpl("test", TestContext.class.getClassLoader());
	
	static {
		AppContext.register(appContext);
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
		((EntityManager)entityManager).initRuntime();
	}
	
}
