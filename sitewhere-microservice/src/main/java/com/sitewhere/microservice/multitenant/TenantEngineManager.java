/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.MapMaker;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.ServiceNotAvailableException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;

/**
 * Tenant engine manager implementation.
 * 
 * @param <I>
 * @param <T>
 */
public class TenantEngineManager<I extends IFunctionIdentifier, T extends IMicroserviceTenantEngine>
	extends LifecycleComponent implements ITenantEngineManager<T> {

    /** Number of seconds between fallback attempts for checking tenant bootstrap */
    @SuppressWarnings("unused")
    private static final int BOOTSTRAP_CHECK_MAX_SECS_BETWEEN_RETRIES = 15;

    /** Max number of tenants being added/removed concurrently */
    private static final int MAX_CONCURRENT_TENANT_OPERATIONS = 5;

    /** Max time to wait for tenant to be bootstrapped from template */
    @SuppressWarnings("unused")
    private static final long MAX_WAIT_FOR_TENANT_BOOTSTRAPPED = 60 * 1000;

    /** Map of tenant engines that have been initialized */
    private ConcurrentMap<String, T> initializedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines that failed to initialize */
    private ConcurrentMap<String, T> failedTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines in the process of initializing */
    private ConcurrentMap<String, ITenant> initializingTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** Map of tenant engines in the process of shutting down */
    private ConcurrentMap<String, String> stoppingTenantEngines = new MapMaker().concurrencyLevel(4).makeMap();

    /** List of tenant ids waiting for an engine to be created */
    private BlockingDeque<String> tenantInitializationQueue = new LinkedBlockingDeque<>();

    /** List of tenant ids waiting for an engine to be shut down */
    private BlockingDeque<String> tenantShutdownQueue = new LinkedBlockingDeque<>();

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
	waitForTenantsBootstrapped();
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

	super.terminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * getTenantEngineByToken(java.lang.String)
     */
    @Override
    public T getTenantEngineByToken(String token) throws SiteWhereException {
	T engine = getInitializedTenantEngines().get(token);
	if (engine == null) {
	    engine = getFailedTenantEngines().get(token);
	}
	return engine;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * assureTenantEngineAvailable(java.lang.String)
     */
    @Override
    public T assureTenantEngineAvailable(String token) throws TenantEngineNotAvailableException {
	try {
	    T engine = getTenantEngineByToken(token);
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
     * Wait for tenants to be bootstrapped by instance management microservice.
     */
    protected void waitForTenantsBootstrapped() {
	// Callable<Boolean> connectCheck = () -> {
	// Stat existing =
	// getMicroservice().getZookeeperManager().getCurator().checkExists()
	// .forPath(getMultitenantMicroservice().getInstanceTenantsBootstrappedMarker());
	// if (existing == null) {
	// throw new SiteWhereException("Tenants not bootstrapped within waiting
	// period.");
	// }
	// return true;
	// };
	// RetryConfig config = new
	// RetryConfigBuilder().retryOnAnyException().retryIndefinitely().withRandomBackoff()
	// .withDelayBetweenTries(Duration.ofSeconds(BOOTSTRAP_CHECK_MAX_SECS_BETWEEN_RETRIES)).build();
	// RetryListener<Boolean> listener = new RetryListener<Boolean>() {
	//
	// @Override
	// public void onEvent(Status<Boolean> status) {
	// getLogger().info(String.format(
	// "Waiting for tenants to be bootstrapped. Attempt %d (total wait so far %dms).
	// Retrying after fallback...",
	// status.getTotalTries(), status.getTotalElapsedDuration().toMillis()));
	// }
	// };
	// new
	// CallExecutorBuilder().config(config).afterFailedTryListener(listener).build().execute(connectCheck);
    }

    /**
     * Initialize tenant engines by inspecting the list of tenant configurations,
     * loading tenant information, then creating a tenant engine for each.
     * 
     * @throws SiteWhereException
     */
    protected void initializeTenantEngines() throws SiteWhereException {
	// CuratorFramework curator =
	// getMicroservice().getZookeeperManager().getCurator();
	// try {
	// ISearchResults<ITenant> tenants = getTenantManagement().listTenants(new
	// TenantSearchCriteria(1, 0));
	// for (ITenant tenant : tenants.getResults()) {
	// if (getTenantEngineByTenantId(tenant.getId()) == null) {
	// String configured = getMultitenantMicroservice()
	// .getInstanceTenantConfiguredIndicatorPath(tenant.getId());
	//
	// // Only initialize tenants which have been bootstrapped.
	// if (curator.checkExists().forPath(configured) != null) {
	// if (!getTenantInitializationQueue().contains(tenant.getId())) {
	// getTenantInitializationQueue().offer(tenant.getId());
	// }
	// }
	// }
	// }
	// } catch (Throwable e) {
	// throw new SiteWhereException("Unhandled exception while processing tenant
	// engines for initialization.", e);
	// }
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * getTenantConfiguration(java.lang.String)
     */
    @Override
    public byte[] getTenantConfiguration(String token) throws SiteWhereException {
	T engine = getTenantEngineByToken(token);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	return engine.getModuleConfiguration();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * updateTenantConfiguration(java.lang.String, byte[])
     */
    @Override
    public void updateTenantConfiguration(String token, byte[] content) throws SiteWhereException {
	T engine = getTenantEngineByToken(token);
	if (engine == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	engine.updateModuleConfiguration(content);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * restartTenantEngine(java.lang.String)
     */
    @Override
    public void restartTenantEngine(String token) throws SiteWhereException {
	// Shut down and remove existing tenant engine.
	removeTenantEngine(token);
	getLogger().info("Tenant engine shut down successfully. Queueing for restart...");

	// Add to queue for restart.
	getTenantInitializationQueue().offer(null);
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
     * @see com.sitewhere.spi.microservice.multitenant.ITenantEngineManager#
     * removeTenantEngine(java.lang.String)
     */
    @Override
    public void removeTenantEngine(String token) throws SiteWhereException {
	IMicroserviceTenantEngine engine = getInitializedTenantEngines().get(token);
	if (engine != null) {
	    // Remove initialized engine if one exists.
	    getTenantShutdownQueue().add(token);
	} else {
	    // Remove failed engine if one exists.
	    engine = getFailedTenantEngines().get(token);
	    if (engine != null) {
		getFailedTenantEngines().remove(token);
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

    public ConcurrentMap<String, T> getInitializedTenantEngines() {
	return initializedTenantEngines;
    }

    public ConcurrentMap<String, T> getFailedTenantEngines() {
	return failedTenantEngines;
    }

    public void setFailedTenantEngines(ConcurrentMap<String, T> failedTenantEngines) {
	this.failedTenantEngines = failedTenantEngines;
    }

    public ConcurrentMap<String, ITenant> getInitializingTenantEngines() {
	return initializingTenantEngines;
    }

    public void setInitializingTenantEngines(ConcurrentMap<String, ITenant> initializingTenantEngines) {
	this.initializingTenantEngines = initializingTenantEngines;
    }

    public void setInitializedTenantEngines(ConcurrentMap<String, T> initializedTenantEngines) {
	this.initializedTenantEngines = initializedTenantEngines;
    }

    public ConcurrentMap<String, String> getStoppingTenantEngines() {
	return stoppingTenantEngines;
    }

    public void setStoppingTenantEngines(ConcurrentMap<String, String> stoppingTenantEngines) {
	this.stoppingTenantEngines = stoppingTenantEngines;
    }

    public BlockingDeque<String> getTenantInitializationQueue() {
	return tenantInitializationQueue;
    }

    public void setTenantInitializationQueue(BlockingDeque<String> tenantInitializationQueue) {
	this.tenantInitializationQueue = tenantInitializationQueue;
    }

    public BlockingDeque<String> getTenantShutdownQueue() {
	return tenantShutdownQueue;
    }

    public void setTenantShutdownQueue(BlockingDeque<String> tenantShutdownQueue) {
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
		String tenantToken = null;
		ITenant tenant = null;
		try {
		    tenantToken = getTenantInitializationQueue().take();
		    getLogger().info(String.format("Starting processing for tenant '%s'.", tenantToken));
		    tenant = getTenantManagement().getTenant(null);
		} catch (InterruptedException e) {
		    getLogger().info("Tenant startup queue shutting down.");
		    return;
		} catch (ServiceNotAvailableException e) {
		    getLogger().info(String.format("Tenant API not available yet (%s). Tenant will be queued again.",
			    e.getMessage()));
		    getTenantInitializationQueue().add(null);
		    continue;
		} catch (SiteWhereException e) {
		    getLogger().error("Exception in tenant lookup. Tenant will be queued again.", e);
		    getTenantInitializationQueue().add(null);
		    continue;
		} catch (Throwable e) {
		    getLogger().error("Unhandled exception in tenant lookup. Tenant will be queued again.", e);
		    getTenantInitializationQueue().add(null);
		    continue;
		}

		// Verify that multiple threads don't start duplicate engines.
		if (getInitializingTenantEngines().get(null) != null) {
		    getLogger().debug("Skipping initialization for existing tenant engine '" + null + "'.");
		    continue;
		}

		// If tenant doesn't exist, skip startup.
		if (tenant == null) {
		    getLogger().error(String.format("Unable to locate tenant'%s'. Skipping engine startup."));
		    continue;
		}

		try {
		    // Start tenant initialization.
		    if (getTenantEngineByToken(tenantToken) == null) {
			startTenantEngine(tenant);
		    } else {
			getLogger().debug("Tenant engine already exists for '" + tenantToken + "'.");
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
		getInitializingTenantEngines().put(tenant.getToken(), tenant);
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
		getInitializedTenantEngines().put(tenant.getToken(), created);
		getFailedTenantEngines().remove(tenant.getToken());

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
		    getFailedTenantEngines().put(tenant.getToken(), created);
		}

		getLogger().error("Unable to initialize tenant engine for '" + tenant.getName() + "'.", t);
		throw new SiteWhereException(t);
	    } finally {
		// Make sure that tenant is cleared from the pending map.
		getInitializingTenantEngines().remove(tenant.getToken());
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
	    // CuratorFramework curator =
	    // getMicroservice().getZookeeperManager().getCurator();
	    // try {
	    // long deadline = System.currentTimeMillis() +
	    // MAX_WAIT_FOR_TENANT_BOOTSTRAPPED;
	    // while ((deadline - System.currentTimeMillis()) > 0) {
	    // if (curator.checkExists().forPath(getMultitenantMicroservice()
	    // .getInstanceTenantConfiguredIndicatorPath(tenant.getId())) != null) {
	    // return;
	    // }
	    // Thread.sleep(3000);
	    // }
	    // throw new SiteWhereException("Tenant not bootstrapped within time limit.
	    // Aborting");
	    // } catch (Throwable t) {
	    // throw new SiteWhereException("Unable to wait for tenant configuration
	    // bootstrap.", t);
	    // }
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
		    String token = getTenantShutdownQueue().take();

		    // Verify that multiple threads don't start duplicate engines.
		    if (getStoppingTenantEngines().get(token) != null) {
			getLogger().debug("Skipping shutdown for engine already stopping '" + token + "'.");
			continue;
		    }

		    // Look up tenant and add it to initializing tenants map.
		    getStoppingTenantEngines().put(token, token);

		    // Start tenant shutdown.
		    T engine = getTenantEngineByToken(token);
		    if (engine != null) {
			// Remove from list of initialized engines.
			getInitializedTenantEngines().remove(token);

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
			getLogger().info("Tenant engine does not exist for '" + token + "'.");
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

    protected ITenantManagement getTenantManagement() {
	return getMultitenantMicroservice().getTenantManagement();
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
