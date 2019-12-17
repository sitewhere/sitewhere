/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.microservice;

import com.sitewhere.asset.configuration.AssetManagementTenantConfiguration;
import com.sitewhere.asset.configuration.AssetManagementTenantEngineModule;
import com.sitewhere.asset.grpc.AssetManagementImpl;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements asset
 * management functionality.
 */
public class AssetManagementTenantEngine extends MicroserviceTenantEngine<AssetManagementTenantConfiguration>
	implements IAssetManagementTenantEngine {

    /** Asset management persistence API */
    private IAssetManagement assetManagement;

    /** Responds to asset management GRPC requests */
    private AssetManagementGrpc.AssetManagementImplBase assetManagementImpl;

    public AssetManagementTenantEngine(SiteWhereTenantEngine tenantEngineResource) {
	super(tenantEngineResource);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<AssetManagementTenantConfiguration> getConfigurationClass() {
	return AssetManagementTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationModule()
     */
    @Override
    public ITenantEngineModule<AssetManagementTenantConfiguration> getConfigurationModule() {
	return new AssetManagementTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	this.assetManagement = (IAssetManagement) getInjector().getInstance(IAssetManagement.class);
	this.assetManagementImpl = new AssetManagementImpl((IAssetManagementMicroservice) getMicroservice(),
		getAssetManagement());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize asset management persistence.
	init.addInitializeStep(this, getAssetManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start asset management persistence.
	start.addStartStep(this, getAssetManagement(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(io.sitewhere.k8s.crd.tenant.engine.dataset.
     * TenantEngineDatasetTemplate,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(TenantEngineDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	// String scriptName = String.format("%s.groovy",
	// template.getMetadata().getName());
	// Path path = getScriptSynchronizer().add(getScriptContext(),
	// ScriptType.Initializer, scriptName,
	// template.getSpec().getConfiguration().getBytes());
	//
	// Execute remote calls as superuser.
	// Authentication previous =
	// SecurityContextHolder.getContext().getAuthentication();
	// try {
	// SecurityContextHolder.getContext()
	// .setAuthentication(getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant()));
	//
	// getLogger().info(String.format("Applying bootstrap script '%s'.", path));
	// GroovyAssetModelInitializer initializer = new
	// GroovyAssetModelInitializer(getGroovyConfiguration(), path);
	// initializer.initialize(getAssetManagement());
	// } catch (Throwable e) {
	// getLogger().error("Unhandled exception in bootstrap script.", e);
	// throw new SiteWhereException(e);
	// } finally {
	// SecurityContextHolder.getContext().setAuthentication(previous);
	// }
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop asset management persistence.
	stop.addStopStep(this, getAssetManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagementImpl()
     */
    @Override
    public AssetManagementGrpc.AssetManagementImplBase getAssetManagementImpl() {
	return assetManagementImpl;
    }

    public void setAssetManagementImpl(AssetManagementGrpc.AssetManagementImplBase assetManagementImpl) {
	this.assetManagementImpl = assetManagementImpl;
    }
}