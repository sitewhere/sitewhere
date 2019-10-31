/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.ITenantEngineConfigurationListener;
import com.sitewhere.spi.microservice.groovy.IGroovyConfiguration;
import com.sitewhere.spi.microservice.scripting.IScriptContext;
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.tenant.ITenant;

import io.sitewhere.k8s.crd.tenant.engine.configuration.TenantEngineConfigurationTemplate;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Engine that manages operations for a single tenant within an
 * {@link IMultitenantMicroservice}.
 */
public interface IMicroserviceTenantEngine extends ITenantEngineLifecycleComponent, ITenantEngineConfigurationListener {

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
     * Get tenant engine configuration template.
     * 
     * @return
     * @throws SiteWhereException
     */
    public TenantEngineConfigurationTemplate getConfigurationTemplate() throws SiteWhereException;

    /**
     * Get tenant engine dataset template.
     * 
     * @return
     * @throws SiteWhereException
     */
    public TenantEngineDatasetTemplate getDatasetTemplate() throws SiteWhereException;

    /**
     * Get script synchronizer for copying/locating scripts.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IScriptSynchronizer getScriptSynchronizer() throws SiteWhereException;

    /**
     * Get script manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IScriptManager getScriptManager() throws SiteWhereException;

    /**
     * Gets a script context for this engine.
     * 
     * @return
     */
    public IScriptContext getScriptContext();

    /**
     * Get bootstrap manager.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IDatasetBootstrapManager getBootstrapManager() throws SiteWhereException;

    /**
     * Get Groovy configuration.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IGroovyConfiguration getGroovyConfiguration() throws SiteWhereException;

    /**
     * Get Spring context that provides beans for module.
     * 
     * @return
     */
    public Object getModuleContext();

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
     * Wait for dataset in another tenant engine to be bootstrapped using a backoff
     * policy.
     * 
     * @param identifier
     * @throws SiteWhereException
     */
    public void waitForTenantDatasetBootstrapped(IFunctionIdentifier identifier) throws SiteWhereException;

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
     * Get list of tenant engines in other microservices that must be bootstrapped
     * before bootstrap logic in this engine is executed.
     * 
     * @return
     */
    public IFunctionIdentifier[] getTenantBootstrapPrerequisites();

    /**
     * Bootstrap a tenant with data provided in dataset template.
     * 
     * @param template
     * @param monitor
     * @throws SiteWhereException
     */
    public void tenantBootstrap(TenantEngineDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

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
    public ILifecycleStep initializeDiscoverableBeans(Object context) throws SiteWhereException;

    /**
     * Start components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep startDiscoverableBeans(Object context) throws SiteWhereException;

    /**
     * Stop components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep stopDiscoverableBeans(Object context) throws SiteWhereException;

    /**
     * Terminate components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep terminateDiscoverableBeans(Object context) throws SiteWhereException;
}