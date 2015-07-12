package org.shaolin.bmdp.runtime.cache;

import java.io.Serializable;
import java.text.DecimalFormat;

public class CacheInfoImpl implements ICacheInfo, Serializable
{
    private static final long serialVersionUID = -180301880240998620L;

    private final String name;
    private final int maxSize;
    private final boolean needSynchronize;
    private final boolean needStatistics;
    private final int size;
    private final long readCount;
    private final long writeCount;
    private final long hitCount;
    private final long writeHitCount;
    private final long refreshIntervalMinutes;
    private final String description;

    CacheInfoImpl(String name, int maxSize, boolean needSynchronize, boolean needStatistics,
            int size, long readCount, long writeCount, long hitCount, long writeHitCount, 
            long refreshIntervalMinutes, String description)
    {
        this.name = name;
        this.maxSize = maxSize;
        this.needSynchronize = needSynchronize;
        this.needStatistics = needStatistics;
        this.size = size;
        this.readCount = readCount;
        this.writeCount = writeCount;
        this.hitCount = hitCount;
        this.writeHitCount = writeHitCount;
        this.refreshIntervalMinutes = refreshIntervalMinutes;
        this.description = description;
    }
    
    public long getWriteHitCount() 
    {
        return writeHitCount;
    }

    public String getName()
    {
        return name;
    }
    
    public int getMaxSize()
    {
        return maxSize;
    }
    
    public boolean needSynchronize()
    {
        return needSynchronize;
    }
    
    public boolean needStatistics()
    {
        return needStatistics;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public long getReadCount()
    {
        return readCount;
    }
    
    public long getWriteCount()
    {
        return writeCount;
    }
    
    public long getHitCount()
    {
        return hitCount;
    }
    
    public long getRefreshIntervalMinutes() 
    {
        return refreshIntervalMinutes;
    }
    
    public String getHitRate()
    {
        if (readCount == 0)
        {
            return "N/A";
        }
        return new DecimalFormat("0.0%").format((double)hitCount / readCount);
    }
    
    public String getReadWriteRate()
    {
        if (writeCount == 0)
        {
            return "N/A";
        }
        return new DecimalFormat("0.0").format((double)readCount / writeCount) + " : 1";
    }
    
    public String getDescription() 
    {
        return description;
    }
    
    public String toString()
    {
        return "[name:" + name + ",maxSize:" + maxSize + ",needSynchronize:" + needSynchronize +
                ",needStatistics:" + needStatistics + ",size:" + size + ",readCount:" + readCount +
                ",writeCount:" + writeCount + ",hitCount:" + hitCount + ",writeHitCount:" +writeHitCount +
                ",refreshIntervalMinutes" + refreshIntervalMinutes + ",description" + description;
    }

	@Override
	public boolean isEnabled() {
		return true;
	}
}
