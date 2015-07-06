package org.shaolin.bmdp.runtime.cache;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class LRUCache<K, V> extends AbstractCache<K, V> {

	private static final long serialVersionUID = 5725496657725425429L;
	
	/** least recently used linked list item */
	private LinkedListItem<K, V> lruEntry = null;

	/** most recently used linked list item */
	private LinkedListItem<K, V> mruEntry = null;

	private volatile int maxCacheSize;

	private static final int NEW = 0;
	// private static final int INLIST = 1;
	private static final int REMOVED = 2;

	private static final Object LOCK = new Object();

	private static final Logger logger = Logger.getLogger(LRUCache.class);

	public LRUCache(String name, int maxSize, boolean needSynchronize) {
		super(name, needSynchronize);
		maxCacheSize = maxSize;
	}

	public V get(K key) {
		LinkedListItem<K, V> entry = (LinkedListItem<K, V>) super.get(key);
		if (entry == null) {
			return null;
		}
		synchronized (LOCK) {
			makeMRU(entry);
		}
		return entry.value;
	}

	protected V localPut(K key, V value) {
		fireWriteEvent();
		if (map.containsKey(key))
			plusWriteHitCount();

		LinkedListItem<K, V> newEntry = new LinkedListItem<K, V>(key, value);
		LinkedListItem<K, V> oldEntry = null;
		synchronized (map) {
			oldEntry = (LinkedListItem<K, V>) map.get(key);
			synchronized (LOCK) {
				if (oldEntry == null) {
					removeLRUIfNecessary();
				} else {
					removeEntry(oldEntry);
				}
				map.put(key, newEntry);
				addMRU(newEntry);
			}
		}
		V oldValue = (oldEntry == null) ? null : oldEntry.value;
		itemRemoved(oldValue);
		itemAdded(value);
		return oldValue;
	}

	protected V localPutIfAbsent(K key, V value) {
		fireWriteEvent();
		if (map.containsKey(key))
			plusWriteHitCount();

		synchronized (map) {
			LinkedListItem<K, V> oldEntry = (LinkedListItem<K, V>) map.get(key);
			if (oldEntry != null) {
				return oldEntry.value;
			}
			LinkedListItem<K, V> newEntry = new LinkedListItem<K, V>(key, value);
			synchronized (LOCK) {
				removeLRUIfNecessary();
				map.putIfAbsent(key, newEntry);
				addMRU(newEntry);
			}
		}
		itemAdded(value);
		return null;
	}

	public V localRemove(K key) {
		fireWriteEvent();
		if (map.containsKey(key))
			plusWriteHitCount();

		LinkedListItem<K, V> entry = null;
		synchronized (map) {
			entry = (LinkedListItem<K, V>) map.remove(key);
			if (entry == null) {
				return null;
			}
			synchronized (LOCK) {
				removeEntry(entry);
			}
		}
		V oldValue = entry.value;
		itemRemoved(oldValue);
		return oldValue;
	}

	public int getMaxSize() {
		return maxCacheSize;
	}

	public void setMaxSize(int maxSize) {
		maxCacheSize = maxSize;
	}

	private void makeMRU(LinkedListItem<K, V> anEntry) {
		if (anEntry.status == REMOVED) // the entry was just removed
		{
			return;
		}

		// is mru?
		if (anEntry == mruEntry) {
			return;
		}

		// is lru?
		if (lruEntry == anEntry) {
			lruEntry = lruEntry.next;
			lruEntry.previous = null;
		} else {
			// alter the list
			LinkedListItem<K, V> aPrev = anEntry.previous;
			LinkedListItem<K, V> aNext = anEntry.next;
			// anEntry is not lruEntry, so aPrev must be not null
			aPrev.next = aNext;
			// anEntry is not mruEntry, so aNext must be not null
			aNext.previous = aPrev;
		}

		anEntry.previous = mruEntry;
		anEntry.next = null;
		mruEntry.next = anEntry;
		mruEntry = anEntry;
	}

	private void addMRU(LinkedListItem<K, V> anEntry) {
		if (mruEntry == null) // empty?
		{
			mruEntry = anEntry;
			lruEntry = anEntry;
			return;
		}
		anEntry.previous = mruEntry;
		mruEntry.next = anEntry;
		mruEntry = anEntry;
	}

	private void removeLRUIfNecessary() {
		if (size() >= maxCacheSize) {
			LinkedListItem<K, V> entry = (LinkedListItem) map.remove(lruEntry.key);
			if (entry != lruEntry) {
				 throw new IllegalStateException("LRUCache internal error");
			}
			removeEntry(entry);
			itemRemoved(entry.value);
		}
	}

	private void removeEntry(LinkedListItem<K, V> anEntry) {
		// alter the list
		LinkedListItem<K, V> aPrev = anEntry.previous;
		LinkedListItem<K, V> aNext = anEntry.next;

		if (aNext != null) {
			aNext.previous = aPrev;
		} else // this entry is mru
		{
			mruEntry = aPrev;
		}

		if (aPrev != null) {
			aPrev.next = aNext;
		} else // this entry is lru
		{
			lruEntry = aNext;
		}

		anEntry.status = REMOVED;
	}

	@SuppressWarnings("unchecked")
	protected V getValue(V value) {
		return ((LinkedListItem<K, V>) value).value;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("map:");
		sb.append(map);
		sb.append(",lru:");
		sb.append(lruEntry);
		sb.append(",mru:");
		sb.append(mruEntry);
		return new String(sb);
	}

	/**
	 * A double link list.
	 */
	private static class LinkedListItem<K, V> implements Serializable {
		
		private static final long serialVersionUID = 1L;

		public LinkedListItem(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public final K key;
		public final V value;

		/** previous node */
		public LinkedListItem<K, V> previous = null;

		/** next node */
		public LinkedListItem<K, V> next = null;

		public int status = NEW;

		public String toString() {
			return "$" + key + "->" + value + ":"
					+ (previous == null ? "null" : previous.key)
					+ (next == null ? "null" : next.key) + "$";
		}

	}// class LinkedList

}
