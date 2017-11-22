package com.sitewhere.spi.microservice.ignite;

import org.apache.logging.log4j.Logger;

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
public interface IIgniteCacheProvider<K, V> {

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
    public Logger getLogger();
}