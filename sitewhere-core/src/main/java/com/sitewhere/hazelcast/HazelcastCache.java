package com.sitewhere.hazelcast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.CacheType;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.cache.ICacheListener;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Cache implementation that uses Hazelcast for storage.
 * 
 * @author Derek
 *
 * @param <T>
 */
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class HazelcastCache<T> implements ICache<String, T>, EntryAddedListener<String, T>,
	EntryRemovedListener<String, T>, EntryUpdatedListener<String, T> {

    /** Parent lifecycle component */
    private ILifecycleComponent parent;

    /** Name of Hazelcast map */
    private String name;

    /** Cache type */
    private CacheType type;

    /** Hazelcast map used as cache */
    private IMap hMap;

    /** Count of total cache requests */
    private AtomicLong requestCount = new AtomicLong();

    /** Count of total cache hits */
    private AtomicLong hitCount = new AtomicLong();

    /** List of cache listeners */
    private List<ICacheListener<T>> listeners = new ArrayList<ICacheListener<T>>();

    public HazelcastCache(ILifecycleComponent parent, String name, CacheType type, boolean monitorUpdates) {
	this.parent = parent;
	this.name = name;
	this.type = type;
	this.hMap = SiteWhere.getServer().getHazelcastConfiguration().getHazelcastInstance().getMap(name);

	if (monitorUpdates) {
	    hMap.addEntryListener(this, true);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#getType()
     */
    @Override
    public CacheType getType() {
	return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#get(java.lang.Object)
     */
    @Override
    public T get(String key) throws SiteWhereException {
	if (getParent().getLifecycleStatus() == LifecycleStatus.Started) {
	    T result = (T) hMap.get(key);
	    requestCount.incrementAndGet();
	    if (result != null) {
		hitCount.incrementAndGet();
	    }
	    return result;
	} else {
	    throw new SiteWhereException("Cache get() called after shutdown.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#put(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void put(String key, T value) throws SiteWhereException {
	if (getParent().getLifecycleStatus() == LifecycleStatus.Started) {
	    hMap.put(key, value);
	} else {
	    throw new SiteWhereException("Cache put() called after shutdown.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#remove(java.lang.Object)
     */
    @Override
    public void remove(String key) throws SiteWhereException {
	if (getParent().getLifecycleStatus() == LifecycleStatus.Started) {
	    hMap.remove(key);
	} else {
	    throw new SiteWhereException("Cache put() called after shutdown.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#clear()
     */
    @Override
    public void clear() throws SiteWhereException {
	if (getParent().getLifecycleStatus() == LifecycleStatus.Started) {
	    hMap.clear();
	} else {
	    throw new SiteWhereException("Cache clear() called after shutdown.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#addListener(com.sitewhere.spi.cache.
     * ICacheListener)
     */
    @Override
    public void addListener(ICacheListener<T> listener) throws SiteWhereException {
	this.listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hazelcast.map.listener.EntryAddedListener#entryAdded(com.hazelcast.
     * core.EntryEvent)
     */
    @Override
    public void entryAdded(EntryEvent<String, T> event) {
	for (ICacheListener<T> listener : listeners) {
	    listener.onEntryAdded(event.getValue());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hazelcast.map.listener.EntryUpdatedListener#entryUpdated(com.
     * hazelcast.core.EntryEvent)
     */
    @Override
    public void entryUpdated(EntryEvent<String, T> event) {
	for (ICacheListener<T> listener : listeners) {
	    listener.onEntryUpdated(event.getValue());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hazelcast.map.listener.EntryRemovedListener#entryRemoved(com.
     * hazelcast.core.EntryEvent)
     */
    @Override
    public void entryRemoved(EntryEvent<String, T> event) {
	for (ICacheListener<T> listener : listeners) {
	    listener.onEntryRemoved(event.getValue());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#getElementCount()
     */
    @Override
    public int getElementCount() throws SiteWhereException {
	if (getParent().getLifecycleStatus() == LifecycleStatus.Started) {
	    return hMap.size();
	} else {
	    throw new SiteWhereException("Cache getElementCount() called after shutdown.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#getRequestCount()
     */
    @Override
    public long getRequestCount() throws SiteWhereException {
	return requestCount.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.cache.ICache#getHitCount()
     */
    @Override
    public long getHitCount() throws SiteWhereException {
	return hitCount.get();
    }

    /**
     * Get unique name for cache.
     * 
     * @param tenant
     * @param name
     * @return
     */
    public static String getNameForTenantCache(ITenant tenant, String name) {
	return HazelcastCache.class.getName() + ":" + tenant.getId() + ":" + name;
    }

    public ILifecycleComponent getParent() {
	return parent;
    }

    public void setParent(ILifecycleComponent parent) {
	this.parent = parent;
    }
}