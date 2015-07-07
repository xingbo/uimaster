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
package org.shaolin.bmdp.runtime.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManager 
{
	private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);
	
	private static final CacheManager instance = new CacheManager();

    private static final ConcurrentMap<String, ICache<?, ?>> cacheMap = new ConcurrentHashMap<String, ICache<?, ?>>();

    private static final ConcurrentMap<String, Timer> timerMap = new ConcurrentHashMap<String, Timer>();

    private static int anonymousCacheCount = 1;

	private CacheManager() {
	}
	
	public static CacheManager getInstance() {
		return instance;
	}
    
    public <K, V> ICache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType)
    {
        return getCache(cacheName, -1, false, keyType, valueType);
    }

	public <K, V> ICache<K, V> getCache(String cacheName, int maxSize, boolean needSynchronize, 
    		Class<K> keyType, Class<V> valueType)
    {
		if (!cacheMap.containsKey(cacheName)) {
			return createCache(cacheName, maxSize, needSynchronize, keyType, valueType);
		}
        return (ICache<K, V>) cacheMap.get(cacheName);
    }
	
	/**
	 * only used for the config server synchronization.
	 * @param cache
	 */
	public void syncCache(ICache cache) {
		cacheMap.put(cache.getName(), cache);
	}

    private <K, V> ICache<K, V> createCache(String cacheName, int maxSize, boolean needSynchronize, 
    		Class<K> keyType, Class<V> valueType)
    {
        if (cacheName == null)
        {
            cacheName = "anonymous" + (anonymousCacheCount++);
            logger.warn("Anonymous cache:{} created", cacheName);
        }

        ICache<K, V> cache;
        if (maxSize == -1)
        {
            cache = new UnlimitedCache<K, V>(cacheName, needSynchronize);
        }
        else if (maxSize <= 0)
        {
            throw new I18NRuntimeException("max size <= 0");
        }
        else
        {
            cache = new LRUCache<K, V>(cacheName, maxSize, needSynchronize);
        }
        
		if (logger.isDebugEnabled()) {
			logger.debug("built a cache block: {}, maxSize: {}",
					new Object[] {cacheName, maxSize});
		}

		ICache<K, V> oldCache = (ICache<K, V>) cacheMap.putIfAbsent(cacheName, cache);
		if (oldCache != null)
        {
            cache = oldCache;
        }
        return cache;
    }

    public void setCacheRefreshInterval(String cacheName, long minutes)
    {
        ICache<?, ?> cache = cacheMap.get(cacheName);
        if (cache == null) {
        	if (logger.isDebugEnabled()) {
    			logger.debug("Failed to update the cache item configuration by name: " + cacheName);
    		}
            return;
        }
        cache.setRefreshInterval(minutes);

        if (minutes == -1L)
        {
            timerMap.remove(cacheName);
            return;
        }
        else
        {
            Timer timer = timerMap.get(cacheName);
            if (timer == null)
                timer = new Timer();
            timer.schedule(new CacheRefreshTask(cache), minutes * 60 * 1000);
            timerMap.put(cacheName, timer);
        }
    }

    public void setCacheMaxSize(String cacheName, int maxSize)
    {
        ICache<?, ?> cache = cacheMap.get(cacheName);
        if (cache == null) {
        	if (logger.isDebugEnabled()) {
    			logger.debug("Failed to update the cache item configuration by name: " + cacheName);
    		}
        	return;
        }
        if (cache instanceof LRUCache)
        {
            if (maxSize > 0)
            {
                cache.localClear();
                cache.setMaxSize(maxSize);
            }
            else if (maxSize == -1)
            {
                ICache<?, ?> newCache = new UnlimitedCache(cacheName, cache.getInfo().needSynchronize());
                cacheMap.replace(cacheName, newCache);
            }
        }
        else if (cache instanceof UnlimitedCache)
        {
            if (maxSize > 0)
            {
                ICache<?, ?> newCache = new LRUCache(cacheName, maxSize, cache.getInfo()
                        .needSynchronize());
                cacheMap.replace(cacheName, newCache);
            }
        }
    }

    public List<String> getCacheNames()
    {
        List<String> cacheNameList = new ArrayList<String>();
        for (Iterator<String> it = cacheMap.keySet().iterator(); it.hasNext();)
        {
            cacheNameList.add(it.next());
        }
        Collections.sort(cacheNameList);
        return cacheNameList;
    }

    public CacheInfoImpl getCacheInfo(String cacheName)
    {
        ICache<?, ?> cache = cacheMap.get(cacheName);
        if (cache == null)
        {
            return null;
        }
        return cache.getInfo();
    }
    
    public int getCacheSize() {
    	return cacheMap.size();
    }
    
    public List<CacheInfoImpl> getCacheBes() {
    	List<CacheInfoImpl> beList = new ArrayList<CacheInfoImpl>();
    	List<String> names = getCacheNames();
    	for (String n : names) {
    		beList.add(getCacheInfo(n));
    	}
    	return beList;
    }
    

    public String estimateCacheSize(String cacheName)
    {
        ICache<?, ?> cache = cacheMap.get(cacheName);
        if (cache == null)
        {
            return "NOT FOUND";
        }
        try
        {
            return SerializeUtil.estimateObjectSizeString(cache);
        }
        catch (Throwable t)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("estimate cache:" + cacheName + " error", t);
            }
            return "N/A";
        }
    }

    class CacheRefreshTask extends TimerTask
    {
        private ICache<?, ?> cache;

        public CacheRefreshTask(ICache<?, ?> cache)
        {
            this.cache = cache;
        }

        public void run()
        {
            cache.localClear();
        }
    }
    
}
