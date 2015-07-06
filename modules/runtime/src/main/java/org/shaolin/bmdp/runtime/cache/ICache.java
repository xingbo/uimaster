package org.shaolin.bmdp.runtime.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface ICache<K, V> extends Serializable {
	
	public String getName();

	public int getMaxSize();

	public void setMaxSize(int maxSize);

	public void setRefreshInterval(long minutes);

	public void setDescription(String description);

	public boolean containsKey(K key);

	public Collection<V> getValues();
	
	public V get(K key);

	public V put(K key, V value);

	public V putIfAbsent(K key, V value);

	public V remove(K key);

	public void clear();

	public int size();

	public V localRemove(K key);

	public void localClear();

	public Map<K, V> getCacheData();

	public CacheInfoImpl getInfo();

}
