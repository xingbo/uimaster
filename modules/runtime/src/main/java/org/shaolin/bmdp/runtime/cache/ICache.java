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
