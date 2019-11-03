/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.event.Observes;

import com.sitewhere.core.Boilerplate;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceApplication;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 * Base class for SiteWhere microservice application lifecycle.
 */
public abstract class MicroserviceApplication<T extends IMicroservice<?>> implements IMicroserviceApplication<T> {

    /** Executor for background thread */
    private ExecutorService executor = Executors.newSingleThreadExecutor(new MicroserviceThreadFactory());

    /**
     * Called to initialize and start microservice components.
     */
    void onStart(@Observes StartupEvent ev) {
	getMicroservice().getLogger().info("Starting microservice...");
	Future<Integer> futureCode = executor.submit(new StartMicroservice());
	try {
	    int code = futureCode.get();
	    getMicroservice().getLogger().info(String.format("Microservice startup returned with code %d.", code));
	    if (code != 0) {
		getMicroservice().getLogger().info("Exiting due to non-zero return code.");
		System.exit(code);
	    }
	} catch (InterruptedException | ExecutionException e) {
	    getMicroservice().getLogger().info("Exiting due to interrupted startup.");
	    System.exit(1);
	}
    }

    /**
     * Called to shutdown and terminate microservice components.
     */
    void onStop(@Observes ShutdownEvent ev) {
	getMicroservice().getLogger().info("Shutdown signal received. Stopping microservice...");
	Future<Integer> futureCode = executor.submit(new StopMicroservice());
	try {
	    int code = futureCode.get();
	    getMicroservice().getLogger().info(String.format("Microservice shutdown returned with code %d.", code));
	    if (code != 0) {
		getMicroservice().getLogger().info("Exiting due to non-zero return code.");
		System.exit(code);
	    }
	} catch (InterruptedException | ExecutionException e) {
	    getMicroservice().getLogger().info("Exiting due to interrupted shutdown.");
	    System.exit(1);
	}
    }

    /**
     * Runnable for starting microservice.
     * 
     * @author Derek
     */
    private class StartMicroservice implements Callable<Integer> {

	/*
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Integer call() throws Exception {
	    int errorCode = 0;
	    try {
		startMicroservice();
	    } catch (SiteWhereException e) {
		getMicroservice().getLogger().error("Exception on microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice failed to start !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		errorCode = 2;
	    } catch (Throwable e) {
		getMicroservice().getLogger().error("Unhandled exception in microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		errorCode = 3;
	    }
	    return errorCode;
	}

	/**
	 * Start microservice.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startMicroservice() throws SiteWhereException {
	    long start = System.currentTimeMillis();

	    // Display banner indicating service information.
	    List<String> messages = new ArrayList<String>();
	    messages.add(getMicroservice().getName() + " Microservice");
	    messages.add("Version: " + getMicroservice().getVersion().getVersionIdentifier() + "."
		    + getMicroservice().getVersion().getGitRevisionAbbrev());
	    messages.add("Git Revision: " + getMicroservice().getVersion().getGitRevision());
	    messages.add("Build Date: " + getMicroservice().getVersion().getBuildTimestamp());
	    messages.add("Hostname: " + getMicroservice().getHostname());
	    String message = Boilerplate.boilerplate(messages, "*");
	    getMicroservice().getLogger().info("\n" + message + "\n");

	    // Initialize microservice.
	    LifecycleProgressMonitor initMonitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Initialize " + getMicroservice().getName()), getMicroservice());
	    getMicroservice().lifecycleInitialize(initMonitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.InitializationError) {
		getMicroservice().getLogger().info("Error initializing microservice.",
			getMicroservice().getLifecycleError());
		throw getMicroservice().getLifecycleError();
	    }

	    // Start microservice.
	    LifecycleProgressMonitor startMonitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Start " + getMicroservice().getName()), getMicroservice());
	    getMicroservice().lifecycleStart(startMonitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		getMicroservice().getLogger().info("Error starting microservice.",
			getMicroservice().getLifecycleError());
		throw getMicroservice().getLifecycleError();
	    }

	    long total = System.currentTimeMillis() - start;
	    messages.clear();
	    messages.add(getMicroservice().getName() + " Microservice");
	    messages.add("Startup time: " + total + "ms");
	    message = Boilerplate.boilerplate(messages, "*");
	    getMicroservice().getLogger().info("\n" + message + "\n");

	    // Execute any post-startup code.
	    getMicroservice().afterMicroserviceStarted();
	}
    }

    /**
     * Stop the microservice.
     */
    private class StopMicroservice implements Callable<Integer> {

	/*
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Integer call() throws Exception {
	    int errorCode = 0;
	    try {
		stopMicroservice();
	    } catch (SiteWhereException e) {
		getMicroservice().getLogger().error("Exception on microservice shutdown.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice failed to stop !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		errorCode = 2;
	    } catch (Throwable e) {
		getMicroservice().getLogger().error("Unhandled exception in microservice shutdown.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		getMicroservice().getLogger().info("\n" + builder.toString() + "\n");
		errorCode = 3;
	    }
	    return errorCode;
	}

	/**
	 * Stop microservice.
	 * 
	 * @throws SiteWhereException
	 */
	protected void stopMicroservice() throws SiteWhereException {
	    // Stop microservice.
	    LifecycleProgressMonitor stopMonitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Stop " + getMicroservice().getName()), getMicroservice());
	    getMicroservice().lifecycleStop(stopMonitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }

	    // Terminate microservice.
	    LifecycleProgressMonitor termMonitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Terminate " + getMicroservice().getName()), getMicroservice());
	    getMicroservice().lifecycleTerminate(termMonitor);
	    if (getMicroservice().getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw getMicroservice().getLifecycleError();
	    }
	}
    }

    /** Used for naming primary microservice thread */
    private class MicroserviceThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Service Main");
	}
    }
}