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

import com.hazelcast.core.ReplicatedMap;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
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

    /** Parent microservice */
    private IMicroservice microservice;

    /** Global cache identifier */
    private String identifier;

    /** Indicates whether to create cache on startup */
    boolean createIfNotFound;

    /** Cache handle */
    private Map<String, ReplicatedMap<K, V>> cachesByTenantId = new HashMap<String, ReplicatedMap<K, V>>();

    public CacheProvider(IMicroservice microservice, String identifier, boolean createIfNotFound) {
	this.microservice = microservice;
	this.identifier = identifier;
	this.createIfNotFound = createIfNotFound;
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
    protected ReplicatedMap<K, V> getCache(ITenant tenant) throws SiteWhereException {
	String tenantId = (tenant != null) ? tenant.getId() : GLOBAL_CACHE_INDICATOR;
	ReplicatedMap<K, V> cache = getCachesByTenantId().get(tenantId);
	boolean hzInitialized = getMicroservice().getHazelcastManager().getHazelcastInstance() != null;
	if ((hzInitialized) && (cache == null)) {
	    String cacheName = getCacheNameForTenant(tenantId);
	    cache = getMicroservice().getHazelcastManager().getHazelcastInstance().getReplicatedMap(cacheName);
	    getCachesByTenantId().put(tenantId, cache);
	}
	return cache;
    }

    /**
     * Get unique cache name for tenant.
     * 
     * @param tenant
     * @return
     */
    protected String getCacheNameForTenant(String tenantId) {
	return getIdentifier() + ":" + tenantId;
    }

    protected IMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    protected String getIdentifier() {
	return identifier;
    }

    protected void setIdentifier(String identifier) {
	this.identifier = identifier;
    }

    protected boolean isCreateIfNotFound() {
	return createIfNotFound;
    }

    protected void setCreateIfNotFound(boolean createIfNotFound) {
	this.createIfNotFound = createIfNotFound;
    }

    protected Map<String, ReplicatedMap<K, V>> getCachesByTenantId() {
	return cachesByTenantId;
    }

    protected void setCachesByTenantId(Map<String, ReplicatedMap<K, V>> cachesByTenantId) {
	this.cachesByTenantId = cachesByTenantId;
    }
}