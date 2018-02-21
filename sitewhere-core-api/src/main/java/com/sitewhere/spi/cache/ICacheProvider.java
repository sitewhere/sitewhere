/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.cache;

import org.apache.commons.logging.Log;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Provides access to a cache hosted on the Ignite data grid.
 * 
 * @author Derek
 *
 * @param <K>
 * @param <V>
 */
public interface ICacheProvider<K, V> {

    /**
     * Set a cache entry.
     * 
     * @param tenant
     * @param key
     * @param value
     * @throws SiteWhereException
     */
    public void setCacheEntry(ITenant tenant, K key, V value) throws SiteWhereException;

    /**
     * Get a cache entry. Null if not found.
     * 
     * @param tenant
     * @param key
     * @return
     * @throws SiteWhereException
     */
    public V getCacheEntry(ITenant tenant, K key) throws SiteWhereException;

    /**
     * Remove an existing cache entry.
     * 
     * @param tenant
     * @param key
     * @throws SiteWhereException
     */
    public void removeCacheEntry(ITenant tenant, K key) throws SiteWhereException;

    /**
     * Get logger for cache.
     * 
     * @return
     */
    public Log getLogger();
}