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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author wushaol
 *
 * @param <K>
 * @param <V>
 */
abstract class AbstractCache<K, V> implements ICache<K, V> {

	private final String name;
	private volatile boolean needSynchronize;
	private volatile boolean needStatistics = true;

	private long readCount;
	private long writeCount;
	private long readHitCount;
	private long writeHitCount;
	protected long refreshIntervalMinutes = -1L;
	private String description;

	/**
	 * the value inside this class might not always be 'V' type, but requires returning V type.
	 */
	protected final ConcurrentMap<K, Object> map = new ConcurrentHashMap<K, Object>();

	private static final Object READ_LOCK = new Object();
	private static final Object WRITE_LOCK = new Object();

	private static final long serialVersionUID = 5756695363194673927L;

	protected AbstractCache(String name, boolean needSynchronize) {
		this.name = name;
		this.needSynchronize = needSynchronize;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Collection<V> getValues() {
		return (Collection<V>) map.values();
	}
	
	@SuppressWarnings("unchecked")
	public V get(K key) {
		Object value = map.get(key);
		if (needStatistics) {
			synchronized (READ_LOCK) {
				readCount++;
				if (value != null) {
					readHitCount++;
				}
			}
		}
		return (V) value;
	}

	public boolean containsKey(K key) {
		return this.get(key) != null;
	}

	public V put(K key, V value) {
		V oldValue = localPut(key, value);
		if (oldValue != null && !oldValue.equals(value))
			syncOnPut(key, value, oldValue);
		return oldValue;
	}

	@SuppressWarnings("unchecked")
	protected V localPut(K key, V value) {
		fireWriteEvent();
		if (map.containsKey(key))
			plusWriteHitCount();
		Object oldValue = map.put(key, value);
		itemRemoved(oldValue);
		itemAdded(value);
		return (V) oldValue;
	}

	public V putIfAbsent(K key, V value) {
		return localPutIfAbsent(key, value);
	}

	@SuppressWarnings("unchecked")
	protected V localPutIfAbsent(K key, V value) {
		fireWriteEvent();
		if (map.containsKey(key))
			plusWriteHitCount();

		Object oldValue = map.putIfAbsent(key, value);
		if (oldValue == null) {
			itemAdded(value);
		}
		return (V) oldValue;
	}

	public V remove(K key) {
		V oldValue = localRemove(key);
		syncOnRemove(key, oldValue);
		return oldValue;
	}

	@SuppressWarnings("unchecked")
	public V localRemove(K key) {
		fireWriteEvent();
		if (map.containsKey(key))
			plusWriteHitCount();

		Object oldValue = map.remove(key);
		itemRemoved(oldValue);
		return (V) oldValue;
	}

	public void clear() {
		localClear();
	}

	public void localClear() {
		// iterator the keySet to call itemRemoved
		for (Iterator<K> it = map.keySet().iterator(); it.hasNext();) {
			K key = it.next();
			localRemove(key);
		}
	}

	public int size() {
		return map.size();
	}

	public Map<K, V> getCacheData() {
		Map<K, V> data = new HashMap<K, V>();
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			K key = entry.getKey();
			V value = getValue(entry.getValue());
			data.put(key, value);
		}
		return data;
	}

	public CacheInfoImpl getInfo() {
		return new CacheInfoImpl(name, getMaxSize(), needSynchronize,
				needStatistics, size(), readCount, writeCount, readHitCount,
				writeHitCount, refreshIntervalMinutes, description);
	}

	public void setRefreshInterval(long minutes) {
		this.refreshIntervalMinutes = minutes;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected abstract V getValue(V value);

	public abstract int getMaxSize();

	protected void fireWriteEvent() {
		if (needStatistics) {
			synchronized (WRITE_LOCK) {
				writeCount++;
			}
		}
	}

	protected void plusWriteHitCount() {
		if (needStatistics) {
			synchronized (WRITE_LOCK) {
				writeHitCount++;
			}
		}
	}

	protected void itemAdded(Object obj) {
		if (obj instanceof ICacheItem) {
			((ICacheItem) obj).added();
		}
	}

	protected void itemRemoved(Object obj) {
		if (obj instanceof ICacheItem) {
			((ICacheItem) obj).removed();
		}
	}

	private void syncOnPut(Object key, Object value, Object oldValue) {
		// TODO:
	}

	private void syncOnRemove(Object key, Object oldValue) {
		// TODO:
	}

}
