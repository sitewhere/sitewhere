/*
 * CacheAdapter.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.sitewhere.spi.SiteWhereException;
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

	/** Wrapped cache */
	private Ehcache cache;

	public CacheAdapter(Ehcache cache) {
		this.cache = cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.cache.ICache#get(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public V get(K key) throws SiteWhereException {
		Element match = cache.get(key);
		if (match == null) {
			return null;
		}
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
	 * @see com.sitewhere.spi.cache.ICache#getElementCount()
	 */
	@Override
	public int getElementCount() throws SiteWhereException {
		return cache.getSize();
	}
}