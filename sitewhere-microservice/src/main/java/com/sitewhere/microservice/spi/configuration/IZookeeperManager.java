/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spi.configuration;

import org.apache.curator.framework.CuratorFramework;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages Zookeeper configuration for microservices.
 * 
 * @author Derek
 */
public interface IZookeeperManager extends ILifecycleComponent {

    /**
     * Get connected {@link CuratorFramework} instance.
     * 
     * @return
     */
    public CuratorFramework getCurator();
}