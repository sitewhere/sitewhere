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

import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.grpc.client.spi.cache.INearCacheManager;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Manages a Hazelcast client with a near cache that is connected to remote
 * Hazelcast instance which provides the underlying data.
 * 
 * @author Derek
 */
public class NearCacheManager extends LifecycleComponent implements INearCacheManager {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(NearCacheManager.class);

    /** Wrapped Hazelcast client instance */
    private HazelcastInstance hazelcastInstance;

    public NearCacheManager(MicroserviceIdentifier identifier) {
    }

    /*
     * @see com.sitewhere.spi.microservice.hazelcast.IHazelcastProvider#
     * getHazelcastInstance()
     */
    @Override
    public HazelcastInstance getHazelcastInstance() {
	return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
	this.hazelcastInstance = hazelcastInstance;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}