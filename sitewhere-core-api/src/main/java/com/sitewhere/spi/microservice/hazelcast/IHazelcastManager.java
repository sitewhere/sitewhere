/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manage Apache Ignite data grid which is used for replicated caching across
 * microservices.
 * 
 * @author Derek
 */
public interface IHazelcastManager extends ILifecycleComponent {

    /**
     * Get Hazelcast instance.
     * 
     * @return
     */
    public HazelcastInstance getHazelcastInstance();
}