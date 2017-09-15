package com.sitewhere.microservice.spi.configuration;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.SiteWhereException;

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
     * Get current configuration state.
     * 
     * @return
     */
    public ConfigurationState getConfigurationState();

    /**
     * Wait for configuration to be loaded.
     * 
     * @throws SiteWhereException
     */
    public void waitForConfigurationReady() throws SiteWhereException;

    /**
     * Indicates if configuration has been cached from Zk.
     * 
     * @return
     */
    public boolean isConfigurationCacheReady();

    /**
     * Get configuration data for the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException;
}