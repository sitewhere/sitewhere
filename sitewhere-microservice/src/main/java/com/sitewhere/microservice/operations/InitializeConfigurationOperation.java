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

import org.springframework.context.ApplicationContext;

import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.ConfigurationState;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Operation that initializes a microservice and allows the completion result to
 * be tracked asynchronously.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class InitializeConfigurationOperation<T extends IConfigurableMicroservice<?>>
	extends CompletableConfigurationOperation<T> {

    /** Tenant engine being started */
    private T microservice;

    public InitializeConfigurationOperation(T microservice, CompletableFuture<T> completableFuture) {
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
	getMicroservice().getLogger().info("Configuration for '" + getMicroservice().getName() + "' initializing.");
	try {
	    // Load microservice configuration.
	    getMicroservice().setConfigurationState(ConfigurationState.Loading);
	    byte[] global = getMicroservice().getInstanceManagementConfigurationData();
	    if (global == null) {
		throw new SiteWhereException("Global instance management file not found.");
	    }
	    ApplicationContext globalContext = ConfigurationUtils.buildGlobalContext(getMicroservice(), global,
		    getMicroservice().getSpringProperties(), getMicroservice().getMicroserviceApplicationContext());

	    String path = getMicroservice().getConfigurationPath();
	    ApplicationContext localContext = null;
	    if (path != null) {
		String fullPath = getMicroservice().getInstanceConfigurationPath() + "/" + path;
		getMicroservice().getLogger()
			.info(String.format("Loading configuration from Zookeeper at path '%s'", fullPath));
		byte[] data = getMicroservice().getConfigurationMonitor().getConfigurationDataFor(fullPath);
		if (data != null) {
		    localContext = ConfigurationUtils.buildSubcontext(data, getMicroservice().getSpringProperties(),
			    globalContext);
		} else {
		    throw new SiteWhereException("Required microservice configuration not found: " + fullPath);
		}
	    }

	    // Store contexts for later use.
	    getMicroservice().setGlobalApplicationContext(globalContext);
	    getMicroservice().setLocalApplicationContext(localContext);
	    getMicroservice().setConfigurationState(ConfigurationState.Stopped);

	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Initialize microservice configuration."), getMicroservice());
	    long start = System.currentTimeMillis();
	    getMicroservice().getLogger().info("Initializing from updated configuration...");
	    getMicroservice().configurationInitialize(getMicroservice().getGlobalApplicationContext(),
		    getMicroservice().getLocalApplicationContext(), monitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	    getMicroservice().getLogger().info("Microservice configuration '" + getMicroservice().getName()
		    + "' initialized in " + (System.currentTimeMillis() - start) + "ms.");
	    getMicroservice().setConfigurationState(ConfigurationState.Initialized);
	    getCompletableFuture().complete(getMicroservice());
	    return getMicroservice();
	} catch (Throwable t) {
	    getMicroservice().getLogger()
		    .error("Unable to initialize microservice configuration '" + getMicroservice().getName() + "'.", t);
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
	executor.submit(new InitializeConfigurationOperation<T>(microservice, completableFuture));
	return completableFuture;
    }
}
