/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

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

    /**
     * Get paths for configuration files needed by microservice (excluding global
     * instance configuration).
     * 
     * @return
     * @throws SiteWhereException
     */
    public String[] getConfigurationPaths() throws SiteWhereException;

    /**
     * Get path for instance management configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceManagementConfigurationPath() throws SiteWhereException;

    /**
     * Get data for instance management configuration file.
     * 
     * @return
     * @throws SiteWhereException
     */
    public byte[] getInstanceManagementConfigurationData() throws SiteWhereException;

    /**
     * Subpath of instance configuration that contains tenant configuration data.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceTenantsConfigurationPath() throws SiteWhereException;

    /**
     * Get path for tenant configuration.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceTenantConfigurationPath(String tenantId) throws SiteWhereException;

    /**
     * Get path to Zk node that indicates a tenant has been bootstrapped with
     * template data.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceTenantBootstrappedIndicatorPath(String tenantId) throws SiteWhereException;

    /**
     * Initializes microservice components based on Spring contexts that were
     * loaded.
     * 
     * @param global
     * @param contexts
     * @throws SiteWhereException
     */
    public void initializeFromSpringContexts(ApplicationContext global, Map<String, ApplicationContext> contexts)
	    throws SiteWhereException;

    /**
     * Get instance global context.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ApplicationContext getInstanceGlobalContext() throws SiteWhereException;

    /**
     * Get global contexts indexed by path.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Map<String, ApplicationContext> getGlobalContexts() throws SiteWhereException;

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
     * Initialize components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep initializeDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Start components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep startDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Stop components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep stopDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;
}