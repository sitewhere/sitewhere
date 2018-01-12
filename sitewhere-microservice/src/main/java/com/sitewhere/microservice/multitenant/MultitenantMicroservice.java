/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;

import com.google.common.collect.MapMaker;
import com.sitewhere.grpc.client.spi.client.ITenantManagementApiDemux;
import com.sitewhere.grpc.client.tenant.TenantManagementApiDemux;
import com.sitewhere.microservice.configuration.ConfigurableMicroservice;
import com.sitewhere.microservice.configuration.TenantPathInfo;
import com.sitewhere.microservice.multitenant.operations.BootstrapTenantEngineOperation;
import com.sitewhere.microservice.multitenant.operations.InitializeTenantEngineOperation;
import com.sitewhere.microservice.multitenant.operations.StartTenantEngineOperation;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that contains engines for multiple tenants.
 * 
 * @author Derek
 */
public abstract class MultitenantMicroservice<T extends IMicroserviceTenantEngine> extends ConfigurableMicroservice
	implements IMultitenantMicroservice<T> {

    /** Max number of tenants being added/removed concurrently */
    private static final int MAX_CONCURRENT_TENANT_OPERATIONS = 5;

    /** Tenant management API demux */
    private ITenantManagementApiDemux tenantManagementApiDemux;

    /** Map of tenant engines that have been initialized */
    private ConcurrentMap<String, T> initializedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines that failed to initialize */
    private ConcurrentMap<String, T> failedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines in the process of initializing */
    private ConcurrentMap<String, ITenant> initializingTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** List of tenant ids waiting for an engine to be created */
    private BlockingDeque<String> tenantInitializationQueue = new LinkedBlockingDeque<>();

    /** Executor for tenant operations */
    private ExecutorService tenantOperations;

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

	// Handles threading for tenant operations.
	this.tenantOperations = Executors.newFixedThreadPool(MAX_CONCURRENT_TENANT_OPERATIONS,
		new TenantOperationsThreadFactory());
	tenantOperations.execute(new TenantEngineStarter(this));

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize tenant management API channel.
	init.addInitializeStep(this, getTenantManagementApiDemux(), true);

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
	this.tenantManagementApiDemux = new TenantManagementApiDemux(this);
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
	start.addStartStep(this, getTenantManagementApiDemux(), true);

	// Execute startup steps.
	start.execute(monitor);

	// Call logic for starting microservice subclass.
	microserviceStart(monitor);

	// Initialize tenant engines.
	initializeTenantEngines();
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

	// Stop tenant management API channel.
	stop.addStopStep(this, getTenantManagementApiDemux());

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
	// Shut down any tenant operations.
	if (tenantOperations != null) {
	    tenantOperations.shutdown();
	}
	getTenantManagementApiDemux().terminate(monitor);

	super.terminate(monitor);
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
     * @see com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice#
     * getTenantEngineByTenantId(java.lang.String)
     */
    @Override
    public T getTenantEngineByTenantId(String id) throws SiteWhereException {
	T engine = getInitializedTenantEngines().get(id);
	if (engine == null) {
	    engine = getFailedTenantEngines().get(id);
	}
	return engine;
    }

    /**
     * Initialize tenant engines by inspecting the list of tenant configurations,
     * loading tenant information, then creating a tenant engine for each.
     * 
     * @throws SiteWhereException
     */
    protected void initializeTenantEngines() throws SiteWhereException {
	CuratorFramework curator = getZookeeperManager().getCurator();
	try {
	    if (curator.checkExists().forPath(getInstanceTenantsConfigurationPath()) != null) {
		List<String> tenantIds = curator.getChildren().forPath(getInstanceTenantsConfigurationPath());
		for (String tenantId : tenantIds) {
		    if (getTenantEngineByTenantId(tenantId) == null) {
			if (!getTenantInitializationQueue().contains(tenantId)) {
			    getTenantInitializationQueue().offer(tenantId);
			}
		    }
		}
	    } else {
		getLogger().warn("No tenants currently configured.");
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to create tenant engines.", e);
	}
    }

    /**
     * Get the tenant engine responsible for handling configuration for the given
     * path.
     * 
     * @param pathInfo
     * @return
     * @throws SiteWhereException
     */
    protected IMicroserviceTenantEngine getTenantEngineForPathInfo(TenantPathInfo pathInfo) throws SiteWhereException {
	if (pathInfo != null) {
	    IMicroserviceTenantEngine engine = getTenantEngineByTenantId(pathInfo.getTenantId());
	    if (engine != null) {
		return engine;
	    } else if (!getTenantInitializationQueue().contains(pathInfo.getTenantId())) {
		getTenantInitializationQueue().offer(pathInfo.getTenantId());
	    }
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * restartTenantEngine(java.lang.String)
     */
    @Override
    public void restartTenantEngine(String tenantId) throws SiteWhereException {
	// Shut down and remove existing tenant engine.
	removeTenantEngine(tenantId);
	getLogger().info("Tenant engine shut down successfully. Queueing for restart...");

	// Add to queue for restart.
	getTenantInitializationQueue().offer(tenantId);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * removeTenantEngine(java.lang.String)
     */
    @Override
    public void removeTenantEngine(String tenantId) throws SiteWhereException {
	IMicroserviceTenantEngine engine = getInitializedTenantEngines().get(tenantId);
	if (engine != null) {
	    // Remove initialized engine if one exists.
	    getInitializedTenantEngines().remove(tenantId);

	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Shut down tenant engine."), this);

	    // Stop tenant engine.
	    engine.lifecycleStop(monitor);
	    if (engine.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		getLogger().error("Error while stopping tenant engine.", engine.getLifecycleError());
	    }

	    // Terminate tenant engine.
	    engine.lifecycleTerminate(monitor);
	    if (engine.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		getLogger().error("Error while terminating tenant engine.", engine.getLifecycleError());
	    }
	} else {
	    // Remove failed engine if one exists.
	    engine = getFailedTenantEngines().get(tenantId);
	    if (engine != null) {
		getFailedTenantEngines().remove(tenantId);
	    }
	}
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
		IMicroserviceTenantEngine engine = getTenantEngineForPathInfo(pathInfo);
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
     * getTenantConfiguration(java.lang.String)
     */
    @Override
    public byte[] getTenantConfiguration(String tenantId) throws SiteWhereException {
	T engine = getTenantEngineByTenantId(tenantId);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	return engine.getModuleConfiguration();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * updateTenantConfiguration(java.lang.String, byte[])
     */
    @Override
    public void updateTenantConfiguration(String tenantId, byte[] content) throws SiteWhereException {
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
		    for (T engine : getInitializedTenantEngines().values()) {
			engine.onGlobalConfigurationUpdated();
		    }
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * microserviceInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * microserviceStart(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    public ITenantManagementApiDemux getTenantManagementApiDemux() {
	return tenantManagementApiDemux;
    }

    public void setTenantManagementApiDemux(ITenantManagementApiDemux tenantManagementApiDemux) {
	this.tenantManagementApiDemux = tenantManagementApiDemux;
    }

    public ConcurrentMap<String, T> getInitializedTenantEngines() {
	return initializedTenantEngines;
    }

    public void setInitializedTenantEngines(ConcurrentMap<String, T> initializedTenantEngines) {
	this.initializedTenantEngines = initializedTenantEngines;
    }

    public ConcurrentMap<String, ITenant> getInitializingTenantEngines() {
	return initializingTenantEngines;
    }

    public void setInitializingTenantEngines(ConcurrentMap<String, ITenant> initializingTenantEngines) {
	this.initializingTenantEngines = initializingTenantEngines;
    }

    public ConcurrentMap<String, T> getFailedTenantEngines() {
	return failedTenantEngines;
    }

    public void setFailedTenantEngines(ConcurrentMap<String, T> failedTenantEngines) {
	this.failedTenantEngines = failedTenantEngines;
    }

    public BlockingDeque<String> getTenantInitializationQueue() {
	return tenantInitializationQueue;
    }

    public void setTenantInitializationQueue(BlockingDeque<String> tenantInitializationQueue) {
	this.tenantInitializationQueue = tenantInitializationQueue;
    }

    public ExecutorService getTenantOperations() {
	return tenantOperations;
    }

    public void setTenantOperations(ExecutorService tenantOperations) {
	this.tenantOperations = tenantOperations;
    }

    /**
     * Processes the list of tenants waiting for tenant engines to be started.
     * 
     * @author Derek
     */
    private class TenantEngineStarter extends SystemUserRunnable {

	public TenantEngineStarter(IMicroservice microservice) {
	    super(microservice, null);
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() {
	    while (true) {
		try {
		    // Wait for tenant API available.
		    getTenantManagementApiDemux().waitForApiChannel().waitForApiAvailable();

		    // Get next tenant id from the queue and look up the tenant.
		    String tenantId = getTenantInitializationQueue().take();

		    // Verify that multiple threads don't start duplicate engines.
		    if (getInitializingTenantEngines().get(tenantId) != null) {
			getLogger().debug("Skipping initialization for existing tenant engine '" + tenantId + "'.");
			continue;
		    }

		    // Look up tenant and add it to initializing tenants map.
		    ITenant tenant = getTenantManagementApiDemux().getApiChannel().getTenantById(tenantId);
		    if (tenant == null) {
			throw new SiteWhereException("Unable to locate tenant by id '" + tenantId + "'.");
		    }
		    getInitializingTenantEngines().put(tenantId, tenant);

		    // Start tenant initialization.
		    if (getTenantEngineByTenantId(tenantId) == null) {
			InitializeTenantEngineOperation
				.createCompletableFuture(MultitenantMicroservice.this, tenant, getTenantOperations())
				.thenCompose(engine -> StartTenantEngineOperation.createCompletableFuture(engine,
					getTenantOperations()))
				.thenCompose(engine -> BootstrapTenantEngineOperation.createCompletableFuture(engine,
					getTenantOperations()))
				.exceptionally(t -> {
				    getLogger().error("Unable to bootstrap tenant engine.", t);
				    return null;
				});
		    } else {
			getLogger().info("Tenant engine already exists for '" + tenantId + "'.");
		    }
		} catch (SiteWhereException e) {
		    getLogger().warn("Exception processing tenant engine.", e);
		} catch (Throwable e) {
		    getLogger().warn("Unhandled exception processing tenant engine.", e);
		}
	    }
	}
    }

    /** Used for naming tenant operation threads */
    private class TenantOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Tenant Operations " + counter.incrementAndGet());
	}
    }
}