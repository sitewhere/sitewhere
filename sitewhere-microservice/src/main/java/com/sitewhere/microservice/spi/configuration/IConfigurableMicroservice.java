package com.sitewhere.microservice.spi.configuration;

import com.sitewhere.microservice.spi.IMicroservice;

/**
 * Microservice that supports dynamic monitoring of configuration.
 * 
 * @author Derek
 */
public interface IConfigurableMicroservice extends IMicroservice {

    /**
     * Get configuration monitor.
     * 
     * @return
     */
    public IConfigurationMonitor getConfigurationMonitor();

    /**
     * Indicates if configuration has been cached from Zk.
     * 
     * @return
     */
    public boolean isConfigurationCacheReady();
}