/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant.operations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Operation that start a tenant engine.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class StartTenantEngineOperation<T extends IMicroserviceTenantEngine> extends CompletableTenantOperation<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant engine being started */
    private T tenantEngine;

    public StartTenantEngineOperation(T tenantEngine, CompletableFuture<T> completableFuture) {
	super(completableFuture);
	this.tenantEngine = tenantEngine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public T call() throws Exception {
	try {
	    // Start tenant engine.
	    LOGGER.info("Starting tenant engine for '" + getTenantEngine().getTenant().getName() + "'.");
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Start tenant engine."), getTenantEngine().getMicroservice());
	    long start = System.currentTimeMillis();
	    getTenantEngine().lifecycleStart(monitor);
	    if (getTenantEngine().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getTenantEngine().getLifecycleError();
	    }
	    LOGGER.info("Tenant engine for '" + getTenantEngine().getTenant().getName() + "' started in "
		    + (System.currentTimeMillis() - start) + "ms.");
	    getCompletableFuture().complete(getTenantEngine());
	    return getTenantEngine();
	} catch (Throwable t) {
	    LOGGER.error("Unable to start tenant engine for '" + getTenantEngine().getTenant().getName() + "'.", t);
	    getCompletableFuture().completeExceptionally(t);
	    throw t;
	}
    }

    public T getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(T tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    public static <T extends IMicroserviceTenantEngine> CompletableFuture<T> createCompletableFuture(T tenantEngine,
	    ExecutorService executor) {
	CompletableFuture<T> completableFuture = new CompletableFuture<T>();
	executor.submit(new StartTenantEngineOperation<T>(tenantEngine, completableFuture));
	return completableFuture;
    }
}