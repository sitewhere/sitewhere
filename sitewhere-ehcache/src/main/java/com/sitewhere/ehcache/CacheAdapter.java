/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.ehcache;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.CacheType;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;

/**
 * Wraps {@link Cache} with support for generics and {@link ICache} interface for use in
 * {@link IDeviceManagementCacheProvider} implementation.
 * 
 * @author Derek
 * 
 * @param <K>
 * @param <V>
 */
public class CacheAdapter<K, V> implements ICache<K, V> {

	/** Static logger instance */
	// private static Logger LOGGER = Logger.getLogger(CacheAdapter.class);

	/** Cache type */
	private CacheType type;

	/** Wrapped cache */
	private Ehcache cache;

	/** Counts to number of requests */
	private AtomicLong requestCount;

	/** Counts the number of hits */
	private AtomicLong hitCount;

	public CacheAdapter(CacheType type, Ehcache cache) {
		this.type = type;
		this.cache = cache;
		this.requestCount = new AtomicLong();
		this.hitCount = new AtomicLong();
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
	@SuppressWarnings("unchecked")
	public V get(K key) throws SiteWhereException {
		requestCount.incrementAndGet();
		Element match = cache.get(key);
		if (match == null) {
			return null;
		}
		hitCount.incrementAndGet();
		return (V) match.getObjectValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(K key, V value) throws SiteWhereException {
		cache.put(new Element(key, value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#remove(java.lang.Object)
	 */
	@Override
	public void remove(K key) throws SiteWhereException {
		cache.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#getElementCount()
	 */
	@Override
	public int getElementCount() throws SiteWhereException {
		return cache.getSize();
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
}