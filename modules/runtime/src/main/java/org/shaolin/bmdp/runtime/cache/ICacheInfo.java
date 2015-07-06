package org.shaolin.bmdp.runtime.cache;

public interface ICacheInfo {

	long getWriteHitCount();

	String getName();

	int getMaxSize();

	int getSize();

	long getReadCount();

	long getWriteCount();

	long getHitCount();

	long getRefreshIntervalMinutes();

	String getHitRate();

	String getReadWriteRate();

	String getDescription();

}
