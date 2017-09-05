/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.cache;

import com.sitewhere.spi.cache.CacheType;

/**
 * Used for marshaling information about a cache.
 * 
 * @author Derek
 */
public class CacheInformation {

    /** Cache type */
    private CacheType cacheType;

    /** Number of elements */
    private long elementCount;

    /** Number of requests */
    private long requestCount;

    /** Number of hits */
    private long hitCount;

    /** Hit ratio */
    private double hitRatio;

    public CacheType getCacheType() {
	return cacheType;
    }

    public void setCacheType(CacheType cacheType) {
	this.cacheType = cacheType;
    }

    public long getElementCount() {
	return elementCount;
    }

    public void setElementCount(long elementCount) {
	this.elementCount = elementCount;
    }

    public long getRequestCount() {
	return requestCount;
    }

    public void setRequestCount(long requestCount) {
	this.requestCount = requestCount;
    }

    public long getHitCount() {
	return hitCount;
    }

    public void setHitCount(long hitCount) {
	this.hitCount = hitCount;
    }

    public double getHitRatio() {
	return hitRatio;
    }

    public void setHitRatio(double hitRatio) {
	this.hitRatio = hitRatio;
    }
}