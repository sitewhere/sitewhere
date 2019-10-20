/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Base class for cache providers.
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

    /** Cache configuration */
    private ICacheConfiguration cacheConfiguration;

    /** Cache for global objects */
    private Cache<K, V> globalCache;

    /** Map of tenant-specific caches */
    private Map<UUID, Cache<K, V>> tenantCaches = new HashMap<>();

    public CacheProvider(CacheIdentifier cacheIdentifier, Class<K> keyType, Class<V> valueType,
	    ICacheConfiguration cacheConfiguration) {
	this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
	this.cacheIdentifier = cacheIdentifier;
	this.keyType = keyType;
	this.valueType = valueType;
	this.cacheConfiguration = cacheConfiguration;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
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
	if ((value != null) && (getCacheConfiguration().isEnabled())) {
	    getCache(tenant).put(key, value);
	} else {
	    getCache(tenant).remove(key);
	}
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
	    synchronized (tenantCaches) {
		Cache<K, V> cache = globalCache;
		if (cache == null) {
		    cache = createCache(null);
		    this.globalCache = cache;
		}
		return cache;
	    }
	} else {
	    synchronized (tenantCaches) {
		Cache<K, V> cache = tenantCaches.get(null);
		if (cache == null) {
		    cache = createCache(tenant);
		    tenantCaches.put(null, cache);
		}
		return cache;
	    }
	}
    }

    /**
     * Create a new cache for the given tenant (or null for global).
     * 
     * @param tenant
     * @return
     */
    protected Cache<K, V> createCache(ITenant tenant) {
	String alias = (tenant != null) ? getCacheIdentifier().getCacheKey() + "-" + tenant.getToken().toString()
		: getCacheIdentifier().getCacheKey();
	return getCacheManager().createCache(alias, buildCacheConfiguration());
    }

    /**
     * Get cache configuration.
     * 
     * @return
     */
    protected CacheConfiguration<K, V> buildCacheConfiguration() {
	return CacheConfigurationBuilder
		.newCacheConfigurationBuilder(getKeyType(), getValueType(),
			ResourcePoolsBuilder.heap(getCacheConfiguration().getMaximumSize()))
		.withExpiry(ExpiryPolicyBuilder
			.timeToLiveExpiration(Duration.ofSeconds(getCacheConfiguration().getTtlInSeconds())))
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
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheProvider#getCacheConfiguration()
     */
    @Override
    public ICacheConfiguration getCacheConfiguration() {
	return cacheConfiguration;
    }

    public void setCacheConfiguration(ICacheConfiguration cacheConfiguration) {
	this.cacheConfiguration = cacheConfiguration;
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