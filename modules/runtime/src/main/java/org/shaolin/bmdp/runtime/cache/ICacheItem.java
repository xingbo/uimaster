package org.shaolin.bmdp.runtime.cache;

/**
 * Item in object cache.
 *
 */
public interface ICacheItem
{
    /**
     * Invoked by ObjectCache after this object is added.
     */
    void added();
    
    /**
     * Invoked by ObjectCache before this object is removed.
     */
    void removed();

}
