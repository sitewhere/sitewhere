package com.sitewhere.microservice.spi.configuration;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Monitors configuration nodes in Zk and allows microservices to respond to
 * configuration changes.
 * 
 * @author Derek
 */
public interface IConfigurationMonitor extends ILifecycleComponent {

    /**
     * Get configuration data for the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException;

    /**
     * Get list of listeners.
     * 
     * @return
     */
    public List<IConfigurationListener> getListeners();
}