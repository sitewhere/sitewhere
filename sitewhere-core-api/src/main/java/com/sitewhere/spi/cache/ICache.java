/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.cache;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for a cache that stores objects by id.
 * 
 * @author Derek
 * 
 * @param <K>
 * @param <V>
 */
public interface ICache<K, V> {

    /**
     * Get the cache type.
     * 
     * @return the type of cache
     */
    public CacheType getType();

    /**
     * Get value based on a given key.
     * 
     * @param key
     *            unique key
     * @return corresponding value or null if not found
     * @throws SiteWhereException
     *             if implementation can not get value
     */
    public V get(K key) throws SiteWhereException;

    /**
     * Add or replace value for the given key.
     * 
     * @param key
     *            unique key
     * @param value
     *            new or replacement value
     * @throws SiteWhereException
     *             if implementation can not put value
     */
    public void put(K key, V value) throws SiteWhereException;

    /**
     * Remove an element from the cache.
     * 
     * @param key
     *            unique key for object to remove
     * @throws SiteWhereException
     *             if implementation can not remove value
     */
    public void remove(K key) throws SiteWhereException;

    /**
     * Clear all elements from cache.
     * 
     * @throws SiteWhereException
     *             if implementation can not clear cache
     */
    public void clear() throws SiteWhereException;

    /**
     * Get count of elements currently in cache.
     * 
     * @return count of cache elements
     * @throws SiteWhereException
     *             if implementation can not get count
     */
    public int getElementCount() throws SiteWhereException;

    /**
     * Get the number of requests made to the cache.
     * 
     * @return count of requests made to cache
     * @throws SiteWhereException
     *             if implementation can not get count
     */
    public long getRequestCount() throws SiteWhereException;

    /**
     * Get the number of cache hits.
     * 
     * @return count of cache hits
     * @throws SiteWhereException
     *             if implementation can not count hits
     */
    public long getHitCount() throws SiteWhereException;
}