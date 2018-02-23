/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurationListener;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Engine that manages operations for a single tenant within an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public interface IMicroserviceTenantEngine extends ITenantEngineLifecycleComponent, IConfigurationListener {

    /**
     * Get parent microservice.
     * 
     * @return
     */
    public IMultitenantMicroservice<?> getMicroservice();

    /**
     * Get tenant hosted by engine.
     * 
     * @return
     */
    public ITenant getTenant();

    /**
     * Get current engine state.
     * 
     * @return
     */
    public ITenantEngineState getCurrentState() throws SiteWhereException;

    /**
     * Get tenant template.
     * 
     * @return
     */
    public ITenantTemplate getTenantTemplate() throws SiteWhereException;

    /**
     * Get script synchronizer for copying/locating scripts.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IScriptSynchronizer getTenantScriptSynchronizer() throws SiteWhereException;

    /**
     * Get script manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IScriptManager getScriptManager() throws SiteWhereException;

    /**
     * Get Groovy configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IGroovyConfiguration getGroovyConfiguration() throws SiteWhereException;

    /**
     * Get Zk configuration path for tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getTenantConfigurationPath() throws SiteWhereException;

    /**
     * Get Zk path for tenant runtime state.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getTenantStatePath() throws SiteWhereException;

    /**
     * Get Spring context that provides beans for module.
     * 
     * @return
     */
    public ApplicationContext getModuleContext();

    /**
     * Get path used for locking operations at the module level.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getModuleLockPath() throws SiteWhereException;

    /**
     * Get name of for module configuration file (without path).
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getModuleConfigurationName() throws SiteWhereException;

    /**
     * Get Zk configuration path for module configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getModuleConfigurationPath() throws SiteWhereException;

    /**
     * Get module configuration data.
     * 
     * @return
     * @throws SiteWhereException
     */
    public byte[] getModuleConfiguration() throws SiteWhereException;

    /**
     * Update module configuration data.
     * 
     * @param content
     * @throws SiteWhereException
     */
    public void updateModuleConfiguration(byte[] content) throws SiteWhereException;

    /**
     * Called when global configuration is updated.
     * 
     * @throws SiteWhereException
     */
    public void onGlobalConfigurationUpdated() throws SiteWhereException;

    /**
     * Get Zk configuration path for module bootstrapped indicator.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getModuleBootstrappedPath() throws SiteWhereException;

    /**
     * Wait a given amount of time for another module to be bootstrapped.
     * 
     * @param identifier
     * @param time
     * @param unit
     * @throws SiteWhereException
     */
    public void waitForModuleBootstrapped(String identifier, long time, TimeUnit unit) throws SiteWhereException;

    /**
     * Executes tenant initialization code. Called after Spring context has been
     * loaded.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Executes tenant startup code.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Bootstrap a tenant with data provided in tenant template.
     * 
     * @param template
     * @param monitor
     * @throws SiteWhereException
     */
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Executes tenant shutdown code.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Initialize components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep initializeDiscoverableBeans(ApplicationContext context) throws SiteWhereException;

    /**
     * Start components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep startDiscoverableBeans(ApplicationContext context) throws SiteWhereException;

    /**
     * Stop components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep stopDiscoverableBeans(ApplicationContext context) throws SiteWhereException;

    /**
     * Terminate components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep terminateDiscoverableBeans(ApplicationContext context) throws SiteWhereException;
}