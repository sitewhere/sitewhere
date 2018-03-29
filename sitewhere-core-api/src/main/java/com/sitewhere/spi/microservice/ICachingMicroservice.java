/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import com.sitewhere.spi.microservice.hazelcast.IHazelcastManager;

/**
 * Microservice that provides access to a Hazelcast cache.
 * 
 * @author Derek
 */
public interface ICachingMicroservice {

    /**
     * Get Hazelcast manager component.
     * 
     * @return
     */
    public IHazelcastManager getHazelcastManager();
}