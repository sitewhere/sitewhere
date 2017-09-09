package com.sitewhere.microservice.spi;

import com.sitewhere.microservice.spi.configuration.IZookeeperConfigurationManager;
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
     * Get name shown for microservice.
     * 
     * @return
     */
    public String getName();

    /**
     * Get Zookeeper configuration manager.
     * 
     * @return
     */
    public IZookeeperConfigurationManager getZookeeperConfigurationManager();

    /**
     * Wait on SiteWhere instance configuration metadata to become initialized
     * before proceeding.
     * 
     * @throws SiteWhereException
     */
    public void waitOnInstanceInitialization() throws SiteWhereException;
}