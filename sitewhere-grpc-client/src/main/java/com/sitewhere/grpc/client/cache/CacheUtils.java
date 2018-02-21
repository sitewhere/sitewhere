/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;

/**
 * Utility methods for common handling of cache-related functionality.
 * 
 * @author Derek
 */
public class CacheUtils {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(CacheUtils.class);

    /**
     * Log object returned from a cache hit.
     * 
     * @param cached
     * @throws SiteWhereException
     */
    public static void logCacheHit(Object cached) throws SiteWhereException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Returning cached object:\n\n" + MarshalUtils.marshalJsonAsPrettyString(cached));
	}
    }

    /**
     * Log object updated in cache.
     * 
     * @param cached
     * @throws SiteWhereException
     */
    public static void logCacheUpdated(Object cached) throws SiteWhereException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Adding/updating cached object:\n\n" + MarshalUtils.marshalJsonAsPrettyString(cached));
	}
    }

    /**
     * Log object removed from cache.
     * 
     * @param cached
     * @throws SiteWhereException
     */
    public static void logCacheRemoved(String key) throws SiteWhereException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Removing cached object with key: " + key);
	}
    }
}