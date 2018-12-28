/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;

/**
 * Provides settings which control how a cache is to be configured.
 */
public class CacheConfiguration implements ICacheConfiguration {

    /** Maximum number of cache entries */
    private int maximumSize;

    /** Max life of cache entries in seconds */
    private int ttlInSeconds;

    /** Indicates if cache is enabled */
    private boolean enabled;

    public CacheConfiguration(int maximumSize, int ttlInSeconds) {
	this.maximumSize = maximumSize;
	this.ttlInSeconds = ttlInSeconds;
	this.enabled = true;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#getMaximumSize()
     */
    @Override
    public int getMaximumSize() {
	return maximumSize;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#setMaximumSize(int)
     */
    @Override
    public void setMaximumSize(int maximumSize) {
	this.maximumSize = maximumSize;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#getTtlInSeconds()
     */
    @Override
    public int getTtlInSeconds() {
	return ttlInSeconds;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#setTtlInSeconds(int)
     */
    @Override
    public void setTtlInSeconds(int ttlInSeconds) {
	this.ttlInSeconds = ttlInSeconds;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	return enabled;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.cache.ICacheConfiguration#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }
}
