/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spi;

import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.microservice.spi.security.ISystemUser;
import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import io.opentracing.Tracer;

/**
 * Functionality common to all SiteWhere microservices.
 * 
 * @author Derek
 */
public interface IMicroservice extends ILifecycleComponent {

    /**
     * Get name shown for microservice.
     * 
     * @return
     */
    public String getName();

    /**
     * Get unique microservice identifier.
     * 
     * @return
     */
    public String getIdentifier();

    /**
     * Get settings for SiteWhere instance.
     * 
     * @return
     */
    public IInstanceSettings getInstanceSettings();

    /**
     * Get tracer implementation.
     * 
     * @return
     */
    public Tracer getTracer();

    /**
     * Get token management interface.
     * 
     * @return
     */
    public ITokenManagement getTokenManagement();

    /**
     * Get system superuser.
     * 
     * @return
     */
    public ISystemUser getSystemUser();

    /**
     * Code executed after microservice has been started.
     */
    public void afterMicroserviceStarted();

    /**
     * Get Zookeeper node path for instance.
     * 
     * @return
     */
    public String getInstanceZkPath();

    /**
     * Get Zookeeper path for instance configuration.
     * 
     * @return
     */
    public String getInstanceConfigurationPath();

    /**
     * Get path for marker used to indicate instance is bootstrapped.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceBootstrappedMarker() throws SiteWhereException;

    /**
     * Get Zookeeper manager.
     * 
     * @return
     */
    public IZookeeperManager getZookeeperManager();

    /**
     * Wait for SiteWhere instance configuration metadata to become initialized
     * before proceeding.
     * 
     * @throws SiteWhereException
     */
    public void waitForInstanceInitialization() throws SiteWhereException;
}