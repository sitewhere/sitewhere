package com.sitewhere.microservice.spi;

import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Functionality common to all SiteWhere microservices.
 * 
 * @author Derek
 */
public interface IMicroservice extends ILifecycleComponent {

    /**
     * Get instance id service is associated with.
     * 
     * @return
     */
    public String getInstanceId();

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
     * Get name shown for microservice.
     * 
     * @return
     */
    public String getName();

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