/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Base class for cache providers.
 * 
 * @author Derek
 *
 * @param <K>
 * @param <V>
 */
public abstract class CacheProvider<K, V> extends LifecycleComponent implements ICacheProvider<K, V> {

    /** Cache identifier */
    private CacheIdentifier cacheIdentifier;

    /** Cache manager */
    private CacheManager cacheManager;

    /** Key type */
    private Class<K> keyType;

    /** Value type */
    private Class<V> valueType;

    /** Cache for global objects */
    private Cache<K, V> globalCache;

    /** Map of tenant-specific caches */
    private Map<UUID, Cache<K, V>> tenantCaches = new HashMap<>();

    /** Maximum cache size */
    private int maximumSize;

    /** Time to live in seconds */
    private int ttlInSeconds;

    public CacheProvider(CacheIdentifier cacheIdentifier, Class<K> keyType, Class<V> valueType, int maximumSize,
	    int ttlInSeconds) {
	this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
	this.cacheIdentifier = cacheIdentifier;
	this.keyType = keyType;
	this.valueType = valueType;
	this.maximumSize = maximumSize;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	getCacheManager().init();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getCacheManager().close();
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider#setCacheEntry(com.
     * sitewhere.spi.tenant.ITenant, java.lang.Object, java.lang.Object)
     */
    @Override
    public void setCacheEntry(ITenant tenant, K key, V value) throws SiteWhereException {
	getLogger().debug("Caching value for '" + key.toString() + "'.");
	getCache(tenant).put(key, value);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider#getCacheEntry(com.
     * sitewhere.spi.tenant.ITenant, java.lang.Object)
     */
    @Override
    public V getCacheEntry(ITenant tenant, K key) throws SiteWhereException {
	V result = getCache(tenant).get(key);
	if (result != null) {
	    getLogger().debug("Found cached value for '" + key.toString() + "'.");
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider#removeCacheEntry(
     * com.sitewhere.spi.tenant.ITenant, java.lang.Object)
     */
    @Override
    public void removeCacheEntry(ITenant tenant, K key) throws SiteWhereException {
	getCache(tenant).remove(key);
    }

    /**
     * Get cache (create if not found).
     * 
     * @return
     * @throws SiteWhereException
     */
    protected Cache<K, V> getCache(ITenant tenant) throws SiteWhereException {
	if (tenant == null) {
	    Cache<K, V> cache = getGlobalCache();
	    if (cache == null) {
		cache = createCache(null);
		this.globalCache = cache;
	    }
	    return cache;
	} else {
	    Cache<K, V> cache = getTenantCaches().get(tenant.getId());
	    if (cache == null) {
		cache = createCache(tenant);
		getTenantCaches().put(tenant.getId(), cache);
	    }
	    return cache;
	}
    }

    /**
     * Create a new cache for the given tenant (or null for global).
     * 
     * @param tenant
     * @return
     */
    protected Cache<K, V> createCache(ITenant tenant) {
	String alias = (tenant != null) ? getCacheIdentifier().getCacheKey() + "-" + tenant.getId().toString()
		: getCacheIdentifier().getCacheKey();
	return getCacheManager().createCache(alias, getCacheConfiguration());
    }

    /**
     * Get cache configuration.
     * 
     * @return
     */
    protected CacheConfiguration<K, V> getCacheConfiguration() {
	// return CacheConfigurationBuilder
	// .newCacheConfigurationBuilder(getKeyType(), getValueType(),
	// ResourcePoolsBuilder.heap(getMaximumSize()))
	// .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(getTtlInSeconds()))).build();
	return CacheConfigurationBuilder
		.newCacheConfigurationBuilder(getKeyType(), getValueType(), ResourcePoolsBuilder.heap(getMaximumSize()))
		.build();
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

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheProvider#getTtlInSeconds()
     */
    @Override
    public int getTtlInSeconds() {
	return ttlInSeconds;
    }

    public void setTtlInSeconds(int ttlInSeconds) {
	this.ttlInSeconds = ttlInSeconds;
    }

    protected CacheManager getCacheManager() {
	return cacheManager;
    }

    protected Class<K> getKeyType() {
	return keyType;
    }

    protected Class<V> getValueType() {
	return valueType;
    }

    protected Cache<K, V> getGlobalCache() {
	return globalCache;
    }

    protected Map<UUID, Cache<K, V>> getTenantCaches() {
	return tenantCaches;
    }
}