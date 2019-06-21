/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;

import com.google.common.collect.MapMaker;
import com.sitewhere.grpc.client.spi.client.ITenantManagementApiChannel;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.ServiceNotAvailableException;
import com.sitewhere.spi.microservice.configuration.ITenantPathInfo;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Tenant engine manager implementation.
 * 
 * @param <I>
 * @param <T>
 */
public class TenantEngineManager<I extends IFunctionIdentifier, T extends IMicroserviceTenantEngine>
	extends LifecycleComponent implements ITenantEngineManager<T> {

    /** Max number of tenants being added/removed concurrently */
    private static final int MAX_CONCURRENT_TENANT_OPERATIONS = 5;

    /** Max time to wait for tenant to be bootstrapped from template */
    private static final long MAX_WAIT_FOR_TENANT_BOOTSTRAPPED = 60 * 1000;

    /** Map of tenant engines that have been initialized */
    private ConcurrentMap<UUID, T> initializedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines that failed to initialize */
    private ConcurrentMap<UUID, T> failedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines in the process of initializing */
    private ConcurrentMap<UUID, ITenant> initializingTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines in the process of shutting down */
    private ConcurrentMap<UUID, UUID> stoppingTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** List of tenant ids waiting for an engine to be created */
    private BlockingDeque<UUID> tenantInitializationQueue = new LinkedBlockingDeque<>();

    /** List of tenant ids waiting for an engine to be shut down */
    private BlockingDeque<UUID> tenantShutdownQueue = new LinkedBlockingDeque<>();

    /** Executor for tenant operations */
    private ExecutorService tenantOperations;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Handles threading for tenant operations.
	this.tenantOperations = Executors.newFixedThreadPool(MAX_CONCURRENT_TENANT_OPERATIONS,
		new TenantOperationsThreadFactory());

	// Initialize tenant engines.
	initializeTenantEngines();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	tenantOperations.execute(new TenantEngineStarter(getMicroservice()));
	tenantOperations.execute(new TenantEngineStopper(getMicroservice()));
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Stop and remove all tenant engines.
	removeAllTenantEngines();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Shut down any tenant operations.
	if (tenantOperations != null) {
	    tenantOperations.shutdown();
	}

	// Shut down tenant management API channel.
	if (getTenantManagementApiChannel() != null) {
	    getTenantManagementApiChannel().terminate(monitor);
	}

	super.terminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * getTenantEngineByTenantId(java.util.UUID)
     */
    @Override
    public T getTenantEngineByTenantId(UUID id) throws SiteWhereException {
	T engine = getInitializedTenantEngines().get(id);
	if (engine == null) {
	    engine = getFailedTenantEngines().get(id);
	}
	return engine;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * assureTenantEngineAvailable(java.util.UUID)
     */
    @Override
    public T assureTenantEngineAvailable(UUID tenantId) throws TenantEngineNotAvailableException {
	try {
	    T engine = getTenantEngineByTenantId(tenantId);
	    if (engine == null) {
		throw new TenantEngineNotAvailableException("No tenant engine found for tenant id.");
	    } else if (engine.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		throw new TenantEngineNotAvailableException("Requested tenant engine failed initialization.");
	    } else if (engine.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw new TenantEngineNotAvailableException("Requested tenant engine failed to start.");
	    } else if (engine.getLifecycleStatus() != LifecycleStatus.Started) {
		throw new TenantEngineNotAvailableException("Requested tenant engine has not started.");
	    }
	    return engine;
	} catch (SiteWhereException e) {
	    throw new TenantEngineNotAvailableException(e);
	}
    }

    /**
     * Initialize tenant engines by inspecting the list of tenant configurations,
     * loading tenant information, then creating a tenant engine for each.
     * 
     * @throws SiteWhereException
     */
    protected void initializeTenantEngines() throws SiteWhereException {
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    String tenantConfig = getMultitenantMicroservice().getInstanceTenantsConfigurationPath();
	    if (curator.checkExists().forPath(tenantConfig) != null) {
		List<String> tenantIds = curator.getChildren().forPath(tenantConfig);
		if (tenantIds.size() > 0) {
		    getLogger()
			    .info(String.format("Queueing %d tenant engines for initialization...", tenantIds.size()));
		} else {
		    getLogger().info("No tenants found. Skipping tenant engine initialization.");
		}
		for (String tenantIdStr : tenantIds) {
		    UUID tenantId = UUID.fromString(tenantIdStr);
		    if (getTenantEngineByTenantId(tenantId) == null) {
			String configured = getMultitenantMicroservice()
				.getInstanceTenantConfiguredIndicatorPath(tenantId);
			// Only initialize tenants which have been bootstrapped.
			if (curator.checkExists().forPath(configured) != null) {
			    if (!getTenantInitializationQueue().contains(tenantId)) {
				getTenantInitializationQueue().offer(tenantId);
			    }
			}
		    }
		}
	    } else {
		throw new SiteWhereException("Zookeeper path for tenant configurations was not found.");
	    }
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception while processing tenant engines for initialization.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * getTenantEngineForPathInfo(com.sitewhere.spi.microservice.configuration.
     * ITenantPathInfo)
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngineForPathInfo(ITenantPathInfo pathInfo) throws SiteWhereException {
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
     * restartTenantEngine(java.util.UUID)
     */
    @Override
    public void restartTenantEngine(UUID tenantId) throws SiteWhereException {
	// Shut down and remove existing tenant engine.
	removeTenantEngine(tenantId);
	getLogger().info("Tenant engine shut down successfully. Queueing for restart...");

	// Add to queue for restart.
	getTenantInitializationQueue().offer(tenantId);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * restartAllTenantEngines()
     */
    @Override
    public void restartAllTenantEngines() throws SiteWhereException {
	if (getInitializedTenantEngines().size() > 0) {
	    getLogger().info(
		    String.format("Queueing %d tenant engines for restart...", getInitializedTenantEngines().size()));
	    getInitializedTenantEngines().forEach((tenantId, engine) -> {
		try {
		    restartTenantEngine(tenantId);
		} catch (SiteWhereException e) {
		    getLogger().error(String.format("Unable to restart tenant engine '%s'.", tenantId));
		}
	    });
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * removeTenantEngine(java.util.UUID)
     */
    @Override
    public void removeTenantEngine(UUID tenantId) throws SiteWhereException {
	IMicroserviceTenantEngine engine = getInitializedTenantEngines().get(tenantId);
	if (engine != null) {
	    // Remove initialized engine if one exists.
	    getTenantShutdownQueue().add(tenantId);
	} else {
	    // Remove failed engine if one exists.
	    engine = getFailedTenantEngines().get(tenantId);
	    if (engine != null) {
		getFailedTenantEngines().remove(tenantId);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * removeAllTenantEngines()
     */
    @Override
    public void removeAllTenantEngines() throws SiteWhereException {
	if (getInitializedTenantEngines().size() > 0) {
	    getLogger().info(
		    String.format("Queueing %d tenant engines for shutdown...", getInitializedTenantEngines().size()));
	    getInitializedTenantEngines().forEach((tenantId, engine) -> {
		try {
		    removeTenantEngine(tenantId);
		} catch (SiteWhereException e) {
		    getLogger().error(String.format("Unable to remove tenant engine '%s'.", tenantId));
		}
	    });
	}
    }

    public ConcurrentMap<UUID, T> getInitializedTenantEngines() {
	return initializedTenantEngines;
    }

    public ConcurrentMap<UUID, T> getFailedTenantEngines() {
	return failedTenantEngines;
    }

    public void setFailedTenantEngines(ConcurrentMap<UUID, T> failedTenantEngines) {
	this.failedTenantEngines = failedTenantEngines;
    }

    public ConcurrentMap<UUID, ITenant> getInitializingTenantEngines() {
	return initializingTenantEngines;
    }

    public void setInitializingTenantEngines(ConcurrentMap<UUID, ITenant> initializingTenantEngines) {
	this.initializingTenantEngines = initializingTenantEngines;
    }

    public void setInitializedTenantEngines(ConcurrentMap<UUID, T> initializedTenantEngines) {
	this.initializedTenantEngines = initializedTenantEngines;
    }

    public ConcurrentMap<UUID, UUID> getStoppingTenantEngines() {
	return stoppingTenantEngines;
    }

    public void setStoppingTenantEngines(ConcurrentMap<UUID, UUID> stoppingTenantEngines) {
	this.stoppingTenantEngines = stoppingTenantEngines;
    }

    public BlockingDeque<UUID> getTenantInitializationQueue() {
	return tenantInitializationQueue;
    }

    public void setTenantInitializationQueue(BlockingDeque<UUID> tenantInitializationQueue) {
	this.tenantInitializationQueue = tenantInitializationQueue;
    }

    public BlockingDeque<UUID> getTenantShutdownQueue() {
	return tenantShutdownQueue;
    }

    public void setTenantShutdownQueue(BlockingDeque<UUID> tenantShutdownQueue) {
	this.tenantShutdownQueue = tenantShutdownQueue;
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

	public TenantEngineStarter(IMicroservice<?> microservice) {
	    super(microservice, null);
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() {
	    getLogger().info("Starting to process tenant startup queue.");
	    while (true) {
		UUID tenantId = null;
		ITenant tenant = null;
		try {
		    tenantId = getTenantInitializationQueue().take();
		    getLogger().info(String.format("Starting processing for tenant id '%s'.", tenantId.toString()));
		    tenant = getTenantManagementApiChannel().getTenant(tenantId);
		} catch (InterruptedException e) {
		    getLogger().info("Tenant startup queue shutting down.");
		    return;
		} catch (ServiceNotAvailableException e) {
		    getLogger().info("Tenant API not available yet. Tenant will be queued again.");
		    getTenantInitializationQueue().add(tenantId);
		    continue;
		} catch (SiteWhereException e) {
		    getLogger().error("Exception in tenant lookup. Tenant will be queued again.", e);
		    getTenantInitializationQueue().add(tenantId);
		    continue;
		} catch (Throwable e) {
		    getLogger().error("Unhandled exception in tenant lookup. Tenant will be queued again.", e);
		    getTenantInitializationQueue().add(tenantId);
		    continue;
		}

		// Verify that multiple threads don't start duplicate engines.
		if (getInitializingTenantEngines().get(tenantId) != null) {
		    getLogger().debug("Skipping initialization for existing tenant engine '" + tenantId + "'.");
		    continue;
		}

		// If tenant doesn't exist, skip startup.
		if (tenant == null) {
		    getLogger().error(String.format("Unable to locate tenant'%s'. Skipping engine startup.", tenantId));
		    continue;
		}

		try {
		    // Start tenant initialization.
		    if (getTenantEngineByTenantId(tenantId) == null) {
			startTenantEngine(tenant);
		    } else {
			getLogger().debug("Tenant engine already exists for '" + tenantId + "'.");
		    }
		} catch (SiteWhereException e) {
		    getLogger().warn("Exception starting tenant engine.", e);
		} catch (Throwable e) {
		    getLogger().warn("Unhandled exception starting tenant engine.", e);
		}
	    }
	}

	/**
	 * Start engine for a tenant.
	 * 
	 * @param tenant
	 * @throws SiteWhereException
	 */
	protected void startTenantEngine(ITenant tenant) throws SiteWhereException {
	    T created = null;
	    try {
		// Mark that an engine is being initialized.
		getInitializingTenantEngines().put(tenant.getId(), tenant);
		getLogger().info("Creating tenant engine for '" + tenant.getName() + "'...");

		created = getMultitenantMicroservice().createTenantEngine(tenant);
		created.setTenantEngine(created); // Required for nested components.

		// Configuration files must be present before initialization.
		getLogger().info("Verifying tenant '" + tenant.getName() + "' configuration available.");
		waitForTenantConfigured(tenant);

		// Initialize new engine.
		getLogger().info("Intializing tenant engine for '" + tenant.getName() + "'.");
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Initialize tenant engine."), getMicroservice());
		long start = System.currentTimeMillis();
		getMicroservice().initializeNestedComponent(created, monitor, true);

		// Mark tenant engine as initialized and remove failed engine if present.
		getInitializedTenantEngines().put(tenant.getId(), created);
		getFailedTenantEngines().remove(tenant.getId());

		getLogger().info("Tenant engine for '" + tenant.getName() + "' initialized in "
			+ (System.currentTimeMillis() - start) + "ms.");

		// Start new engine.
		getLogger().info("Starting tenant engine for '" + created.getTenant().getName() + "'.");
		monitor = new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Start tenant engine."),
			created.getMicroservice());
		start = System.currentTimeMillis();
		created.lifecycleStart(monitor);
		if (created.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw created.getLifecycleError();
		}
		getLogger().info("Tenant engine for '" + created.getTenant().getName() + "' started in "
			+ (System.currentTimeMillis() - start) + "ms.");
	    } catch (Throwable t) {
		// Keep map of failed tenant engines.
		if (created != null) {
		    getFailedTenantEngines().put(tenant.getId(), created);
		}

		getLogger().error("Unable to initialize tenant engine for '" + tenant.getName() + "'.", t);
		throw new SiteWhereException(t);
	    } finally {
		// Make sure that tenant is cleared from the pending map.
		getInitializingTenantEngines().remove(tenant.getId());
	    }
	}

	/**
	 * Wait until tenant configuration has been copied before starting
	 * initialization.
	 * 
	 * @param tenant
	 * @throws SiteWhereException
	 */
	protected void waitForTenantConfigured(ITenant tenant) throws SiteWhereException {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    try {
		long deadline = System.currentTimeMillis() + MAX_WAIT_FOR_TENANT_BOOTSTRAPPED;
		while ((deadline - System.currentTimeMillis()) > 0) {
		    if (curator.checkExists().forPath(getMultitenantMicroservice()
			    .getInstanceTenantConfiguredIndicatorPath(tenant.getId())) != null) {
			return;
		    }
		    Thread.sleep(3000);
		}
		throw new SiteWhereException("Tenant not bootstrapped within time limit. Aborting");
	    } catch (Throwable t) {
		throw new SiteWhereException("Unable to wait for tenant configuration bootstrap.", t);
	    }
	}
    }

    /**
     * Processes the list of tenants waiting for tenant engines to be stopped.
     * 
     * @author Derek
     */
    private class TenantEngineStopper extends SystemUserRunnable {

	public TenantEngineStopper(IMicroservice<?> microservice) {
	    super(microservice, null);
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() {
	    getLogger().info("Starting to process tenant shutdown queue.");
	    while (true) {
		try {
		    // Get next tenant id from the queue and look up the tenant.
		    UUID tenantId = getTenantShutdownQueue().take();

		    // Verify that multiple threads don't start duplicate engines.
		    if (getStoppingTenantEngines().get(tenantId) != null) {
			getLogger().debug("Skipping shutdown for engine already stopping '" + tenantId + "'.");
			continue;
		    }

		    // Look up tenant and add it to initializing tenants map.
		    getStoppingTenantEngines().put(tenantId, tenantId);

		    // Start tenant shutdown.
		    T engine = getTenantEngineByTenantId(tenantId);
		    if (engine != null) {
			// Remove from list of initialized engines.
			getInitializedTenantEngines().remove(tenantId);

			// Stop tenant engine.
			getLogger().info("Stopping tenant engine for '" + engine.getTenant().getName() + "'.");
			ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
				new LifecycleProgressContext(1, "Stop tenant engine."), engine.getMicroservice());
			long start = System.currentTimeMillis();
			engine.lifecycleStop(monitor);
			if (engine.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
			    throw engine.getLifecycleError();
			}
			getLogger().info("Tenant engine for '" + engine.getTenant().getName() + "' stopped in "
				+ (System.currentTimeMillis() - start) + "ms.");

			getLogger().info("Terminating tenant engine for '" + engine.getTenant().getName() + "'.");
			monitor = new LifecycleProgressMonitor(
				new LifecycleProgressContext(1, "Terminate tenant engine."), engine.getMicroservice());
			start = System.currentTimeMillis();
			engine.lifecycleTerminate(monitor);
			if (engine.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
			    throw engine.getLifecycleError();
			}
			getLogger().info("Tenant engine for '" + engine.getTenant().getName() + "' terminated in "
				+ (System.currentTimeMillis() - start) + "ms.");
		    } else {
			getLogger().info("Tenant engine does not exist for '" + tenantId + "'.");
		    }
		} catch (SiteWhereException e) {
		    getLogger().warn("Exception stopping tenant engine.", e);
		} catch (Throwable e) {
		    getLogger().warn("Unhandled exception stopping tenant engine.", e);
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    protected MultitenantMicroservice<I, T> getMultitenantMicroservice() {
	return ((MultitenantMicroservice<I, T>) getMicroservice());
    }

    protected ITenantManagementApiChannel<?> getTenantManagementApiChannel() {
	return getMultitenantMicroservice().getTenantManagementApiChannel();
    }

    /** Used for naming tenant operation threads */
    private class TenantOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Tenant Ops " + counter.incrementAndGet());
	}
    }
}
