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

import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.microservice.configuration.ConfigurationState;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Operation that terminates a microservice configuration and allows the
 * completion result to be tracked asynchronously.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class TerminateConfigurationOperation<T extends IConfigurableMicroservice<?>>
	extends CompletableConfigurationOperation<T> {

    /** Tenant engine being started */
    private T microservice;

    public TerminateConfigurationOperation(T microservice, CompletableFuture<T> completableFuture) {
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
	getMicroservice().getLogger().info("Configuration for '" + getMicroservice().getName() + "' terminating.");
	try {
	    // Terminate microservice.
	    if (getMicroservice().getLifecycleStatus() != LifecycleStatus.Terminated) {
		ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Terminate microservice configuration."), getMicroservice());
		long start = System.currentTimeMillis();
		getMicroservice().configurationTerminate(getMicroservice().getGlobalApplicationContext(),
			getMicroservice().getLocalApplicationContext(), monitor);
		if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw getMicroservice().getLifecycleError();
		}
		getMicroservice().getLogger().info("Microservice configuration '" + getMicroservice().getName()
			+ "' terminated in " + (System.currentTimeMillis() - start) + "ms.");
	    }
	    getMicroservice().setConfigurationState(ConfigurationState.Unloaded);
	    getCompletableFuture().complete(getMicroservice());
	    return getMicroservice();
	} catch (Throwable t) {
	    getMicroservice().getLogger()
		    .error("Unable to terminate microservice configuration '" + getMicroservice().getName() + "'.", t);
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
	executor.submit(new TerminateConfigurationOperation<T>(microservice, completableFuture));
	return completableFuture;
    }
}
