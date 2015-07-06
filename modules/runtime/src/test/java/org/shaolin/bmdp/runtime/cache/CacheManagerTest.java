package org.shaolin.bmdp.runtime.cache;

import org.junit.Assert;
import org.junit.Test;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;

public class CacheManagerTest {

	@Test
	public void test() {
		
		ICache<String, String> block1 = CacheManager.getInstance().getCache("block1", 1, false, String.class, String.class);
		block1.put("test1", "value1");
		block1.put("test2", "value2");
		Assert.assertEquals(1, block1.getMaxSize());
		Assert.assertEquals(1, block1.size());
		
		ICache<String, String> block2 = CacheManager.getInstance().getCache("block2", String.class, String.class);
		block2.put("test1", "value1");
		block2.put("test2", "value2");
		Assert.assertEquals(-1, block2.getMaxSize());
		Assert.assertEquals(2, block2.size());
		
		System.out.println("CacheNames: " + CacheManager.getInstance().getCacheNames());
		System.out.println("cache block2 info: " + CacheManager.getInstance().getCacheInfo("block2").toString());
	}

}
