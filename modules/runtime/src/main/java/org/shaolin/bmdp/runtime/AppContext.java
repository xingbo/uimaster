package org.shaolin.bmdp.runtime;

import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;

/**
 * The application context will be created in the application initializer 
 * and bind with the servlet context.
 * 
 * @author wushaol
 *
 */
public class AppContext {

	private static ThreadLocal<IAppServiceManager> appContext = new ThreadLocal<IAppServiceManager>();

	public static void register(IAppServiceManager appContext0) {
		appContext.set(appContext0);
	}

	public static IAppServiceManager get() {
		return appContext.get();
	}

	public static boolean isMasterNode() {
		return IServerServiceManager.INSTANCE.getMasterNodeName().equals(AppContext.get().getAppName());
	}
	
}
