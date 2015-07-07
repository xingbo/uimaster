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
