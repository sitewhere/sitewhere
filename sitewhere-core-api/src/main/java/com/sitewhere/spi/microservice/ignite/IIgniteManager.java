/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.ignite;

import org.apache.ignite.Ignite;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manage Apache Ignite data grid which is used for replicated caching across
 * microservices.
 * 
 * @author Derek
 */
public interface IIgniteManager extends ILifecycleComponent {

    /**
     * Get managed Apache Ignite instance.
     * 
     * @return
     */
    public Ignite getIgnite();
}