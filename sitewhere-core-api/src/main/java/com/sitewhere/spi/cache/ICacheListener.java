package com.sitewhere.spi.cache;

/**
 * Allows for notification when cache events occur.
 * 
 * @author Derek
 */
public interface ICacheListener<T> {

    /**
     * Called when a cache entry is added.
     * 
     * @param item
     */
    public void onEntryAdded(T item);

    /**
     * Called when a cache entry is updated.
     * 
     * @param item
     */
    public void onEntryUpdated(T item);

    /**
     * Called when a cache entry is removed.
     * 
     * @param item
     */
    public void onEntryRemoved(T item);
}