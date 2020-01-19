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
import com.sitewhere.asset.persistence.rdb.entity.RdbAsset;
import com.sitewhere.asset.persistence.rdb.entity.RdbAssetType;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.api.asset.AssetManagementRequestBuilder;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.rdb.RdbProviderInformation;
import com.sitewhere.rdb.RdbTenantEngine;
import com.sitewhere.rdb.providers.postgresql.Postgres95Provider;
import com.sitewhere.rdb.providers.postgresql.PostgresConnectionInfo;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements asset
 * management functionality.
 */
public class AssetManagementTenantEngine extends RdbTenantEngine<AssetManagementTenantConfiguration>
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
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<AssetManagementTenantConfiguration> createConfigurationModule() {
	return new AssetManagementTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getProviderInformation()
     */
    @Override
    public RdbProviderInformation<?> getProviderInformation() {
	PostgresConnectionInfo connInfo = new PostgresConnectionInfo();
	connInfo.setHostname("sitewhere-postgresql");
	connInfo.setPort(5432);
	connInfo.setUsername("syncope");
	connInfo.setPassword("syncope");
	return new Postgres95Provider(connInfo);
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getEntityClasses()
     */
    @Override
    public Class<?>[] getEntityClasses() {
	return new Class<?>[] { RdbAsset.class, RdbAssetType.class };
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	this.assetManagement = getInjector().getInstance(IAssetManagement.class);
	this.assetManagementImpl = new AssetManagementImpl((IAssetManagementMicroservice) getMicroservice(),
		getAssetManagement());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * setDatasetBootstrapBindings(com.sitewhere.microservice.scripting.Binding)
     */
    @Override
    public void setDatasetBootstrapBindings(Binding binding) throws SiteWhereException {
	binding.setVariable(IScriptVariables.VAR_ASSET_MANAGEMENT_BUILDER,
		new AssetManagementRequestBuilder(getAssetManagement()));
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.tenantInitialize(monitor);

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
	super.tenantStart(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start asset management persistence.
	start.addStartStep(this, getAssetManagement(), true);

	// Execute startup steps.
	start.execute(monitor);
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

	super.tenantStop(monitor);
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagementImpl()
     */
    @Override
    public AssetManagementGrpc.AssetManagementImplBase getAssetManagementImpl() {
	return assetManagementImpl;
    }
}