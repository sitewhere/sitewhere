/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.cache;

/**
 * Provides settings which control how a cache is to be configured.
 */
public interface ICacheConfiguration {

    /**
     * Get maximum size for cache.
     * 
     * @return
     */
    public int getMaximumSize();

    /**
     * Set maximum size for cache.
     * 
     * @param value
     */
    public void setMaximumSize(int value);

    /**
     * Get time to live in seconds.
     * 
     * @return
     */
    public int getTtlInSeconds();

    /**
     * Set time to live in seconds.
     * 
     * @param value
     */
    public void setTtlInSeconds(int value);

    /**
     * Indicates whether cache is enabled.
     * 
     * @return
     */
    public boolean isEnabled();

    /**
     * Set cache enablement.
     * 
     * @param value
     */
    public void setEnabled(boolean value);
}
