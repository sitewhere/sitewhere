/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetBootstrapManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Handles process of bootstrapping tenant engine with data based on the dataset
 * template chosen at tenant creation.
 */
public class DatasetBootstrapManager extends TenantEngineLifecycleComponent implements IDatasetBootstrapManager {

    /** Provides background thread */
    private ExecutorService executor;

    /** Indicates whether engine has been boostrapped */
    private AtomicBoolean tenantEngineBootsrapped = new AtomicBoolean(false);

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
	executor = Executors.newSingleThreadExecutor(new DatasetBootstrapThreadFactory());
	executor.submit(new TenantDatasetBootstrapper());
    }

    /**
     * Handles tenant engine dataset bootstrap in background thread.
     */
    private class TenantDatasetBootstrapper implements Runnable {

	@Override
	public void run() {
	    // try {
	    // // Wait for any required datasets to bootstrap before starting.
	    // IFunctionIdentifier[] required =
	    // getTenantEngine().getTenantBootstrapPrerequisites();
	    // for (IFunctionIdentifier function : required) {
	    // getTenantEngine().waitForTenantDatasetBootstrapped(function);
	    // }
	    //
	    // // Lock the module and check whether tenant needs bootstrap.
	    // getLogger().info("Getting lock for testing tenant engine bootstrap state for
	    // '"
	    // + getTenantEngine().getTenant().getName() + "'.");
	    // CuratorFramework curator =
	    // getMicroservice().getZookeeperManager().getCurator();
	    // InterProcessMutex lock = new InterProcessMutex(curator,
	    // getTenantEngine().getModuleLockPath());
	    // try {
	    // lock.acquire();
	    // tenantEngineBootsrapped.set(false);
	    // if
	    // (curator.checkExists().forPath(getTenantEngine().getTenantDatasetBootstrappedPath())
	    // == null) {
	    // getLogger().info("Tenant engine '" + getTenantEngine().getTenant().getName()
	    // + "' not bootstrapped. Bootstrapping...");
	    // bootstrapTenantEngineDataset(getTenantEngine(), curator);
	    // } else {
	    // getLogger().info("Tenant engine '" + getTenantEngine().getTenant().getName()
	    // + "' already bootstrapped.");
	    // }
	    // tenantEngineBootsrapped.set(true);
	    // } finally {
	    // lock.release();
	    // }
	    // } catch (Throwable e) {
	    // getLogger().error("Exception bootstrapping tenant engine.", e);
	    // }
	}

	/**
	 * Bootstrap the dataset for a tenant engine as specified in the tenant dataset
	 * template.
	 * 
	 * @param curator
	 * @throws SiteWhereException
	 */
	// protected void bootstrapTenantEngineDataset(IMicroserviceTenantEngine engine,
	// CuratorFramework curator)
	// throws SiteWhereException {
	// ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
	// new LifecycleProgressContext(1, "Bootstrap tenant engine."),
	// engine.getMicroservice());
	// String tenantName = engine.getTenant().getName();
	// long start = System.currentTimeMillis();
	//
	// // Run provisioning logic.
	// getTenantEngine().lifecycleProvision(monitor);
	//
	// // Execute tenant bootstrap.
	// IDatasetTemplate template = engine.getDatasetTemplate();
	// engine.tenantBootstrap(template, monitor);
	//
	// try {
	// curator.create().forPath(engine.getTenantDatasetBootstrappedPath());
	// getLogger().info("Tenant engine for '" + tenantName + "' bootstrapped in "
	// + (System.currentTimeMillis() - start) + "ms.");
	// } catch (Exception e) {
	// getLogger().info("Error marking tenant engine '" + tenantName + "' as
	// bootstrapped.");
	// }
	// }
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.ITenantEngineBootstrapManager#
     * isTenantEngineBootstrapped()
     */
    @Override
    public boolean isTenantEngineBootstrapped() {
	return tenantEngineBootsrapped.get();
    }

    /** Used for naming tenant operation threads */
    private class DatasetBootstrapThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Data Bootstrap");
	}
    }
}
