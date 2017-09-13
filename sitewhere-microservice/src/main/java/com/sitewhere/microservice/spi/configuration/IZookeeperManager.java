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