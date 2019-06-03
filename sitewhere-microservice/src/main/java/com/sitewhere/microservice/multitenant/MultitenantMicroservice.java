/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.UUID;

import com.sitewhere.grpc.client.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.client.tenant.TenantManagementApiChannel;
import com.sitewhere.microservice.configuration.ConfigurableMicroservice;
import com.sitewhere.microservice.configuration.TenantPathInfo;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.ITenantPathInfo;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Microservice that contains engines for multiple tenants.
 */
public abstract class MultitenantMicroservice<I extends IFunctionIdentifier, T extends IMicroserviceTenantEngine>
	extends ConfigurableMicroservice<I> implements IMultitenantMicroservice<I, T> {

    /** Tenant management API demux */
    private ITenantManagementApiChannel<?> tenantManagementApiChannel;

    /** Tenant engine manager */
    private ITenantEngineManager<T> tenantEngineManager = new TenantEngineManager<>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * initialize(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create GRPC components.
	createGrpcComponents();

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize tenant management API channel.
	init.addInitializeStep(this, getTenantManagementApiChannel(), true);

	// Initialize tenant engine manager.
	init.addInitializeStep(this, getTenantEngineManager(), true);

	// Execute initialization steps.
	init.execute(monitor);

	// Wait for microservice to be configured.
	waitForConfigurationReady();

	// Call logic for initializing microservice subclass.
	microserviceInitialize(monitor);
    }

    /**
     * Create components that interact via GRPC.
     */
    private void createGrpcComponents() {
	this.tenantManagementApiChannel = new TenantManagementApiChannel(getInstanceSettings());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start tenant mangement API channel.
	start.addStartStep(this, getTenantManagementApiChannel(), true);

	// Start tenant engine manager.
	start.addStartStep(this, getTenantEngineManager(), true);

	// Execute startup steps.
	start.execute(monitor);

	// Call logic for starting microservice subclass.
	microserviceStart(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Call logic for stopping microservice subclass.
	microserviceStop(monitor);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop tenant engine manager.
	stop.addStopStep(this, getTenantEngineManager());

	// Stop tenant management API channel.
	stop.addStopStep(this, getTenantManagementApiChannel());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * terminate(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Shut down tenant management API channel.
	if (getTenantManagementApiChannel() != null) {
	    getTenantManagementApiChannel().terminate(monitor);
	}

	super.terminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineByTenantId(java.util.UUID)
     */
    @Override
    public T getTenantEngineByTenantId(UUID tenantId) throws SiteWhereException {
	return getTenantEngineManager().getTenantEngineByTenantId(tenantId);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * assureTenantEngineAvailable(java.util.UUID)
     */
    @Override
    public T assureTenantEngineAvailable(UUID tenantId) throws TenantEngineNotAvailableException {
	return getTenantEngineManager().assureTenantEngineAvailable(tenantId);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineForPathInfo(com.sitewhere.spi.microservice.configuration.
     * ITenantPathInfo)
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngineForPathInfo(ITenantPathInfo pathInfo) throws SiteWhereException {
	return getTenantEngineManager().getTenantEngineForPathInfo(pathInfo);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationPath()
     */
    @Override
    public String getConfigurationPath() throws SiteWhereException {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	if (isConfigurationCacheReady()) {
	    try {
		TenantPathInfo pathInfo = TenantPathInfo.compute(path, this);
		IMicroserviceTenantEngine engine = getTenantEngineManager().getTenantEngineForPathInfo(pathInfo);
		if (engine != null) {
		    engine.onConfigurationAdded(pathInfo.getPath(), data);
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Error processing configuration addition.", e);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantConfiguration(java.util.UUID)
     */
    @Override
    public byte[] getTenantConfiguration(UUID tenantId) throws SiteWhereException {
	T engine = getTenantEngineByTenantId(tenantId);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	return engine.getModuleConfiguration();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * updateTenantConfiguration(java.util.UUID, byte[])
     */
    @Override
    public void updateTenantConfiguration(UUID tenantId, byte[] content) throws SiteWhereException {
	T engine = getTenantEngineByTenantId(tenantId);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	engine.updateModuleConfiguration(content);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	if (isConfigurationCacheReady()) {
	    try {
		// Detect global configuration update and inform all engines.
		if (getInstanceManagementConfigurationPath().equals(path)) {
		    ((IMultitenantMicroservice<?, ?>) getMicroservice()).restartConfiguration();
		    getTenantEngineManager().restartAllTenantEngines();
		}

		// Otherwise, only report updates to tenant-specific paths.
		else {
		    TenantPathInfo pathInfo = TenantPathInfo.compute(path, this);
		    IMicroserviceTenantEngine engine = getTenantEngineForPathInfo(pathInfo);
		    if (engine != null) {
			engine.onConfigurationUpdated(pathInfo.getPath(), data);
		    }
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Error processing configuration update.", e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	if (isConfigurationCacheReady()) {
	    try {
		TenantPathInfo pathInfo = TenantPathInfo.compute(path, this);
		IMicroserviceTenantEngine engine = getTenantEngineForPathInfo(pathInfo);
		if (engine != null) {
		    engine.onConfigurationDeleted(pathInfo.getPath());
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Error processing configuration delete.", e);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * getTenantEngineManager()
     */
    @Override
    public ITenantEngineManager<T> getTenantEngineManager() {
	return tenantEngineManager;
    }

    public ITenantManagementApiChannel<?> getTenantManagementApiChannel() {
	return tenantManagementApiChannel;
    }
}