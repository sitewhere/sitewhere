package com.sitewhere.microservice.ignite;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Base class for cache providers.
 * 
 * @author Derek
 *
 * @param <K>
 * @param <V>
 */
public abstract class CacheProvider<K, V> implements IIgniteCacheProvider<K, V> {

    /** Parent microservice */
    private IMicroservice microservice;

    /** Global cache identifier */
    private String identifier;

    /** Indicates whether to create cache on startup */
    boolean createIfNotFound;

    /** Cache handle */
    private Map<String, IgniteCache<K, V>> cachesByTenantId = new HashMap<String, IgniteCache<K, V>>();

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
     * Get cache only if it has already been created.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IgniteCache<K, V> getCache(ITenant tenant) throws SiteWhereException {
	IgniteCache<K, V> cache = getCachesByTenantId().get(tenant.getId());
	if (cache == null) {
	    String name = getCacheNameForTenant(tenant);
	    Collection<String> names = getMicroservice().getIgniteManager().getIgnite().cacheNames();
	    if (names.contains(name)) {
		getLogger().info("Found existing cache for tenant " + tenant.getId());
		cache = getMicroservice().getIgniteManager().getIgnite().getOrCreateCache(name);
		getCachesByTenantId().put(tenant.getId(), cache);
	    } else if (isCreateIfNotFound()) {
		getLogger().info("Creating cache for tenant " + tenant.getId());
		CacheConfiguration<K, V> config = new CacheConfiguration<>();
		config.setName(getCacheNameForTenant(tenant));
		config.setCacheMode(CacheMode.REPLICATED);
		cache = getMicroservice().getIgniteManager().getIgnite().createCache(config);
		getCachesByTenantId().put(tenant.getId(), cache);
	    }
	}
	return cache;
    }

    /**
     * Get unique cache name for tenant.
     * 
     * @param tenant
     * @return
     */
    protected String getCacheNameForTenant(ITenant tenant) {
	return getIdentifier() + ":" + ((tenant == null) ? "_global_" : tenant.getId());
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

    protected Map<String, IgniteCache<K, V>> getCachesByTenantId() {
	return cachesByTenantId;
    }

    protected void setCachesByTenantId(Map<String, IgniteCache<K, V>> cachesByTenantId) {
	this.cachesByTenantId = cachesByTenantId;
    }
}