/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.operations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.microservice.configuration.ConfigurationState;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Operation that starts a microservice configuration and allows the completion
 * result to be tracked asynchronously.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class StartConfigurationOperation<T extends IConfigurableMicroservice<?>>
	extends CompletableConfigurationOperation<T> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(StartConfigurationOperation.class);

    /** Tenant engine being started */
    private T microservice;

    public StartConfigurationOperation(T microservice, CompletableFuture<T> completableFuture) {
	super(completableFuture);
	this.microservice = microservice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public T call() throws Exception {
	try {
	    // Start microservice.
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Start microservice configuration."), getMicroservice());
	    long start = System.currentTimeMillis();
	    getMicroservice().configurationStart(getMicroservice().getGlobalApplicationContext(),
		    getMicroservice().getLocalApplicationContext(), monitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	    LOGGER.debug("Microservice configuration '" + getMicroservice().getName() + "' started in "
		    + (System.currentTimeMillis() - start) + "ms.");
	    getMicroservice().setConfigurationState(ConfigurationState.Started);
	    getCompletableFuture().complete(getMicroservice());
	    return getMicroservice();
	} catch (Throwable t) {
	    LOGGER.error("Unable to start microservice configuration '" + getMicroservice().getName() + "'.", t);
	    getMicroservice().setConfigurationState(ConfigurationState.Failed);
	    getCompletableFuture().completeExceptionally(t);
	    throw t;
	}
    }

    public T getMicroservice() {
	return microservice;
    }

    public void setMicroservice(T microservice) {
	this.microservice = microservice;
    }

    public static <T extends IConfigurableMicroservice<?>> CompletableFuture<T> createCompletableFuture(T microservice,
	    ExecutorService executor) {
	CompletableFuture<T> completableFuture = new CompletableFuture<T>();
	executor.submit(new StartConfigurationOperation<T>(microservice, completableFuture));
	return completableFuture;
    }
}
