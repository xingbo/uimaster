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
package org.shaolin.bmdp.workflow.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A in-memory lock manager to emulate key-based lock.
 * 
 * @param <T>
 *            Key type
 */
public final class LockManager<T> {
	private static final class LockWithCounter {
		private final Lock lock;
		private final AtomicInteger counter;

		public LockWithCounter() {
			lock = new ReentrantLock();
			counter = new AtomicInteger(2);
			lock.lock();
		}

		public int decrementAndGet() {
			return counter.decrementAndGet();
		}

		public int incrementAndGet() {
			return counter.incrementAndGet();
		}

		public void lock() {
			lock.lock();
		}

		public void unlock() {
			lock.unlock();
		}
	}

	private final ConcurrentMap<T, LockWithCounter> lockMap = new ConcurrentHashMap<T, LockWithCounter>();

	/**
	 * Acquire the lock of the key.
	 * 
	 * @param key
	 */
	public void acquireLock(T key) {
		LockWithCounter lock = lockMap.get(key);
		if (lock == null) {
			lock = new LockWithCounter();
			LockWithCounter existedLock = lockMap.putIfAbsent(key, lock);
			if (existedLock == null) {
				lock.decrementAndGet();
				return;
			} else {
				lock.unlock();
				lock = existedLock;
			}
		}
		lock.incrementAndGet();
		lock.lock();
		if (lockMap.get(key) != lock) {
			lock.unlock();
			acquireLock(key);
		}
	}

	/**
	 * Attach lock with current thread.
	 * 
	 * The method can only be used with the tryLock method.
	 * 
	 * @param key
	 */
	public void attachLock(T key) {
		lockMap.get(key).lock();
	}

	/**
	 * Detach the lock from the thread.
	 * 
	 * The method can only be used with the tryLock method.
	 * 
	 * @param key
	 */
	public void detachLock(T key) {
		lockMap.get(key).unlock();
	}

	/**
	 * Check the key is locked or not.
	 * 
	 * @param key
	 * @return
	 */
	public boolean isLocked(T key) {
		return lockMap.containsKey(key);
	}

	/**
	 * Release the lock of the key.
	 * 
	 * @param key
	 */
	public void releaseLock(T key) {
		LockWithCounter lock = lockMap.get(key);
		if (lock.decrementAndGet() == 0) {
			lockMap.remove(key);
		}
		lock.unlock();
	}

	public int size() {
		return lockMap.size();
	}

	/**
	 * Try to lock the key.
	 * 
	 * Return true if locked, otherwise return false.
	 * 
	 * @param key
	 * @return
	 */
	public boolean tryLock(T key) {
		LockWithCounter lock = lockMap.get(key);
		if (lock != null) {
			return false;
		} else {
			lock = new LockWithCounter();
			LockWithCounter existedLock = lockMap.putIfAbsent(key, lock);
			if (existedLock == null) {
				lock.decrementAndGet();
				return true;
			} else {
				lock.unlock();
				return false;
			}
		}
	}
}
