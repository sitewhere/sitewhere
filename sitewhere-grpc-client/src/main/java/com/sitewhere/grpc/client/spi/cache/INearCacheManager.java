/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.cache;

import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages a Hazelcast client with a near cache that is connected to remote
 * Hazelcast instance which provides the underlying data.
 * 
 * @author Derek
 */
public interface INearCacheManager extends ILifecycleComponent {

    /**
     * Get target cache type to connect to.
     * 
     * @return
     */
    public IFunctionIdentifier getTarget();

    /**
     * Get cache providers served by manager.
     * 
     * @return
     */
    public ICacheProvider<?, ?>[] getCacheProviders();

    /**
     * Set list of cache providers served by the cache. Used to configure near cache
     * for providers.
     * 
     * @param providers
     */
    public void setCacheProviders(ICacheProvider<?, ?>... providers);
}