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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;

/**
 * Operation that bootstraps a tenant with initial data if it has not already
 * been bootstrapped.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class BootstrapTenantEngineOperation<T extends IMicroserviceTenantEngine> extends CompletableTenantOperation<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant engine being started */
    private T tenantEngine;

    public BootstrapTenantEngineOperation(T tenantEngine, CompletableFuture<T> completableFuture) {
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
	ActiveSpan span = null;
	Tracer tracer = getTenantEngine().getMicroservice().getTracer();
	try {
	    String tenantName = getTenantEngine().getTenant().getName();
	    span = tracer.buildSpan("Bootstrap tenant engine '" + tenantName + "'.").startActive();

	    // Lock the module and check whether tenant needs bootstrap.
	    LOGGER.info("Getting lock for testing tenant engine bootstrap state for '" + tenantName + "'.");
	    CuratorFramework curator = getTenantEngine().getMicroservice().getZookeeperManager().getCurator();
	    InterProcessMutex lock = new InterProcessMutex(curator, getTenantEngine().getModuleLockPath());
	    try {
		span.log("Wait for lock on module...");
		lock.acquire();
		if (curator.checkExists().forPath(getTenantEngine().getModuleBootstrappedPath()) == null) {
		    LOGGER.info("Tenant engine '" + tenantName + "' not bootstrapped. Bootstrapping...");
		    bootstrapTenantEngine(curator);
		} else {
		    LOGGER.info("Tenant engine '" + tenantName + "' already bootstrapped.");
		}
	    } finally {
		span.log("Released lock on module.");
		lock.release();
	    }

	    getCompletableFuture().complete(getTenantEngine());
	    return getTenantEngine();
	} catch (Throwable t) {
	    LOGGER.error("Unable to bootstrap tenant engine for '" + getTenantEngine().getTenant().getName() + "'.", t);
	    TracerUtils.handleErrorInTracerSpan(span, t);
	    getCompletableFuture().completeExceptionally(t);
	    throw t;
	} finally {
	    TracerUtils.finishTracerSpan(span);
	}
    }

    /**
     * Bootstrap a tenant engine with data as specified in the tenant template.
     * 
     * @param curator
     * @throws SiteWhereException
     */
    protected void bootstrapTenantEngine(CuratorFramework curator) throws SiteWhereException {
	ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(getTenantEngine().getMicroservice(), 1, "Bootstrap tenant engine."),
		getTenantEngine().getMicroservice());
	String tenantName = getTenantEngine().getTenant().getName();
	long start = System.currentTimeMillis();

	// Execute tenant bootstrap.
	ITenantTemplate template = getTenantEngine().getTenantTemplate();
	getTenantEngine().tenantBootstrap(template, monitor);

	try {
	    curator.create().forPath(getTenantEngine().getModuleBootstrappedPath());
	    LOGGER.info("Tenant engine for '" + getTenantEngine().getTenant().getName() + "' bootstrapped in "
		    + (System.currentTimeMillis() - start) + "ms.");
	} catch (Exception e) {
	    LOGGER.info("Error marking tenant engine '" + tenantName + "' as bootstrapped.");
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
	executor.submit(new BootstrapTenantEngineOperation<T>(tenantEngine, completableFuture));
	return completableFuture;
    }
}