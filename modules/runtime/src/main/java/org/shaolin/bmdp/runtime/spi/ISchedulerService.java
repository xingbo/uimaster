package org.shaolin.bmdp.runtime.spi;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.shaolin.bmdp.runtime.internal.CustThreadFactory;

public class ISchedulerService implements IServiceProvider, ILifeCycleProvider {

	// it must be static list that we made for whole system.
	private static final ConcurrentHashMap<String, ExecutorService> poolList = 
			new ConcurrentHashMap<String, ExecutorService>();
	
	@Override
	public Class getServiceInterface() {
		return ISchedulerService.class;
	}
	
	@Override
	public void startService() {
		//Runtime.getRuntime().availableProcessors() * 2
	}
	
	@Override
	public boolean readyToStop() {
		return true;
	}
	
	@Override
	public void stopService() {
		Enumeration<String> keys = poolList.keys();
		while (keys.hasMoreElements()) {
			poolList.get(keys.nextElement()).shutdown();
		}
		poolList.clear();
	}
	@Override
	public void reload() {
		
	}
	
	@Override
	public int getRunLevel() {
		return 0;
	}
	
	public ScheduledExecutorService createScheduler(String appName, String moduleName, int size) {
		String key = appName + "-" + moduleName;
		if (!poolList.containsKey(key)) {
			ScheduledExecutorService pool = Executors.newScheduledThreadPool(size, 
					new CustThreadFactory(key));
			poolList.putIfAbsent(key, pool);
		}
		return (ScheduledExecutorService)poolList.get(key);
	}
	
	public ExecutorService createExecutorService(String appName, String moduleName, int size) {
		String key = appName + "-" + moduleName;
		if (!poolList.containsKey(key)) {
			poolList.putIfAbsent(key, Executors.newFixedThreadPool(size, 
					new CustThreadFactory(key)));
		}
		return poolList.get(key);
	}
	
}
