/*
 * ExpiringLRUCache.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.common;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

/**
 * Extends {@link LRUMap} with an expiration check.
 * 
 * @author Derek
 * 
 * @param <K>
 * @param <V>
 */
public class ExpiringLRUCache<K, V> {

	/** Default expiration period in milliseconds */
	private static final int DEFAULT_EXPIRATION = 30 * 1000;

	/** Hashmap for storage */
	private Map<K, CacheEntry<V>> map;

	/** Entry expiration time in milliseconds */
	private int expirationInMS = DEFAULT_EXPIRATION;

	/** Cache statistics */
	private CacheStatistics statistics = new CacheStatistics(this);

	@SuppressWarnings("unchecked")
	public ExpiringLRUCache(int size, int expirationInMs) {
		map = Collections.synchronizedMap(new LRUMap(size));
		this.expirationInMS = expirationInMs;
	}

	/**
	 * Add a new entry to the cache. Removes any existing entry.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value) {
		CacheEntry<V> entry = new CacheEntry<V>(new Date(), value);
		map.put(key, entry);
	}

	/**
	 * Get value if it has not expired.
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key) {
		CacheEntry<V> entry = map.get(key);
		if (entry == null) {
			return null;
		}
		if ((entry.getTimestamp().getTime() - System.currentTimeMillis()) > expirationInMS) {
			map.remove(key);
			return null;
		}
		return entry.getValue();
	}

	/**
	 * Get number of entries in cache.
	 * 
	 * @return
	 */
	public int getSize() {
		return map.size();
	}

	/**
	 * Get cache statistics.
	 * 
	 * @return
	 */
	public CacheStatistics getStatistics() {
		return statistics;
	}

	/** Class for cache entry */
	public static class CacheEntry<V> {

		/** Timestamp for expiration check */
		private Date timestamp;

		/** Stored value */
		private V value;

		public CacheEntry(Date timestamp, V value) {
			this.timestamp = timestamp;
			this.value = value;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public V getValue() {
			return value;
		}
	}

	public static class CacheStatistics {

		/** Cache */
		private ExpiringLRUCache<?, ?> cache;

		/** Number of misses */
		private int cacheMisses = 0;

		/** Number of hits */
		private int cacheHits = 0;

		public CacheStatistics(ExpiringLRUCache<?, ?> cache) {
			this.cache = cache;
		}

		public void hit() {
			cacheHits++;
		}

		public void miss() {
			cacheMisses++;
		}

		public int getCacheSize() {
			return cache.getSize();
		}

		public int getCacheMisses() {
			return cacheMisses;
		}

		public int getCacheHits() {
			return cacheHits;
		}

		public double getHitRatio() {
			if ((cacheHits + cacheMisses) == 0) {
				return 0.0;
			}
			return cacheHits / (cacheHits + cacheMisses);
		}

		public String toString() {
			return "Hits: " + getCacheHits() + " Misses: " + getCacheMisses() + " Ratio: " + getHitRatio();
		}
	}
}