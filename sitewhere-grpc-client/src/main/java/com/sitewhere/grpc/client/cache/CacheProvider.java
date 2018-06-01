/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import com.hazelcast.core.IMap;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastProvider;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Base class for cache providers.
 * 
 * @author Derek
 *
 * @param <K>
 * @param <V>
 */
public abstract class CacheProvider<K, V> implements ICacheProvider<K, V> {

    /** Cache prefix for global caches */
    private static final String GLOBAL_CACHE_INDICATOR = "_global_";

    /** Hazelcast provider */
    private IHazelcastProvider hazelcastProvider;

    /** Cache identifier */
    private CacheIdentifier cacheIdentifier;

    /** Maximum cache size */
    private int maximumSize;

    public CacheProvider(IHazelcastProvider hazelcastProvider, CacheIdentifier cacheIdentifier, int maximumSize) {
	this.hazelcastProvider = hazelcastProvider;
	this.cacheIdentifier = cacheIdentifier;
	this.maximumSize = maximumSize;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider#setCacheEntry(com.
     * sitewhere.spi.tenant.ITenant, java.lang.Object, java.lang.Object)
     */
    @Override
    public void setCacheEntry(ITenant tenant, K key, V value) throws SiteWhereException {
	if (getCache(tenant) != null) {
	    getLogger().trace("Caching value for '" + key.toString() + "'.");
	    getCache(tenant).put(key, value);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider#getCacheEntry(com.
     * sitewhere.spi.tenant.ITenant, java.lang.Object)
     */
    @Override
    public V getCacheEntry(ITenant tenant, K key) throws SiteWhereException {
	if (getCache(tenant) != null) {
	    V result = getCache(tenant).get(key);
	    if (result != null) {
		getLogger().trace("Found cached value for '" + key.toString() + "'.");
		return result;
	    }
	} else {
	    getLogger().debug("Accessing cache before Hazelcast has been initialized.");
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider#removeCacheEntry(
     * com.sitewhere.spi.tenant.ITenant, java.lang.Object)
     */
    @Override
    public void removeCacheEntry(ITenant tenant, K key) throws SiteWhereException {
	if (getCache(tenant) != null) {
	    getCache(tenant).remove(key);
	}
    }

    /**
     * Get cache (create if not found).
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IMap<K, V> getCache(ITenant tenant) throws SiteWhereException {
	boolean hzInitialized = (getHazelcastProvider().getHazelcastInstance() != null)
		&& (getHazelcastProvider().getHazelcastInstance().getLifecycleService().isRunning());
	if (hzInitialized) {
	    String cacheName = getCacheNameForTenant(tenant);
	    return getHazelcastProvider().getHazelcastInstance().getMap(cacheName);
	} else {
	    getLogger().debug("Trying to access uninitialized cache.");
	    return null;
	}
    }

    /**
     * Get unique cache name for tenant.
     * 
     * @param tenant
     * @return
     */
    protected String getCacheNameForTenant(ITenant tenant) {
	String tenantId = (tenant != null) ? tenant.getId().toString() : GLOBAL_CACHE_INDICATOR;
	return getCacheIdentifier().getCacheKey() + ":" + tenantId;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheProvider#getCacheIdentifier()
     */
    @Override
    public CacheIdentifier getCacheIdentifier() {
	return cacheIdentifier;
    }

    public void setCacheIdentifier(CacheIdentifier cacheIdentifier) {
	this.cacheIdentifier = cacheIdentifier;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheProvider#getMaximumSize()
     */
    @Override
    public int getMaximumSize() {
	return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
	this.maximumSize = maximumSize;
    }

    public IHazelcastProvider getHazelcastProvider() {
	return hazelcastProvider;
    }

    public void setHazelcastProvider(IHazelcastProvider hazelcastProvider) {
	this.hazelcastProvider = hazelcastProvider;
    }
}