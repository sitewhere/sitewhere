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