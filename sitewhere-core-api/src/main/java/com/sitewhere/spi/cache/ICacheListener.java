/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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