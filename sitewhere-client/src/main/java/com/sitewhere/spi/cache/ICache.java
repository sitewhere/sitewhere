/*
 * ICache.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.cache;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for a cache that stores objects by and id.
 * 
 * @author Derek
 * 
 * @param <K>
 * @param <V>
 */
public interface ICache<K, V> {

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
	 * Get count of elements currently in cache.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public int getElementCount() throws SiteWhereException;
}