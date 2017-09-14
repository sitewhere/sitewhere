package com.sitewhere.microservice.spi;

import java.util.Map;

import com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Microservice that serves a global funcition in a SiteWhere instance.
 * 
 * @author Derek
 */
public interface IGlobalMicroservice extends IConfigurableMicroservice {

    /**
     * Perform microservice initialization.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Called to start microservice after initialization.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Called to stop microservice before termination.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Get paths for configuration files needed by microservice (excluding
     * global instance configuration).
     * 
     * @return
     * @throws SiteWhereException
     */
    public String[] getConfigurationPaths() throws SiteWhereException;

    /**
     * Get path for instance global configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceGlobalConfigurationPath() throws SiteWhereException;

    /**
     * Get data for instance global configuration file.
     * 
     * @return
     * @throws SiteWhereException
     */
    public byte[] getInstanceGlobalConfigurationData() throws SiteWhereException;

    /**
     * Called after all configuration files have been loaded.
     * 
     * @param global
     * @param configs
     * @throws SiteWhereException
     */
    public void onConfigurationsLoaded(byte[] global, Map<String, byte[]> configs) throws SiteWhereException;
}