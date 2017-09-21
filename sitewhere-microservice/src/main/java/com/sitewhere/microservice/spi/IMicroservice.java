package com.sitewhere.microservice.spi;

import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

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
     * Get instance id service is associated with.
     * 
     * @return
     */
    public String getInstanceId();

    /**
     * Get token management interface.
     * 
     * @return
     */
    public ITokenManagement getTokenManagement();

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