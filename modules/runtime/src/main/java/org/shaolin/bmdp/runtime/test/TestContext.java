package org.shaolin.bmdp.runtime.test;

import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;

public class TestContext {

	protected static final AppServiceManagerImpl appContext = new AppServiceManagerImpl("test", TestContext.class.getClassLoader());
	
	static {
		AppContext.register(appContext);
	}
	
}
