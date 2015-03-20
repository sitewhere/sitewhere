/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.cache;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for a cache that stores objects by id.
 * 
 * @author Derek
 * 
 * @param <K>
 * @param <V>
 */
public interface ICache<K, V> {

	/**
	 * Get the cache type.
	 * 
	 * @return
	 */
	public CacheType getType();

	/**
	 * Get value based on a given key.
	 * 
	 * @param key
	 * @return
	 * @throws SiteWhereException
	 */
	public V get(K key) throws SiteWhereException;

	/**
	 * Add or replace value for the given key.
	 * 
	 * @param key
	 * @param Value
	 * @throws SiteWhereException
	 */
	public void put(K key, V value) throws SiteWhereException;

	/**
	 * Remove an element from the cache.
	 * 
	 * @param key
	 * @throws SiteWhereException
	 */
	public void remove(K key) throws SiteWhereException;

	/**
	 * Clear all elements from cache.
	 * 
	 * @throws SiteWhereException
	 */
	public void clear() throws SiteWhereException;

	/**
	 * Get count of elements currently in cache.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public int getElementCount() throws SiteWhereException;

	/**
	 * Get the number of requests made to the cache.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public long getRequestCount() throws SiteWhereException;

	/**
	 * Get the number of cache hits.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public long getHitCount() throws SiteWhereException;
}