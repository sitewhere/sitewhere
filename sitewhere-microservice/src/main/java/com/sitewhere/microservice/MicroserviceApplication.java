/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.IMicroserviceApplication;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Base application for SiteWhere microservices.
 * 
 * @author Derek
 */
public abstract class MicroserviceApplication<T extends IMicroservice> implements IMicroserviceApplication<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Run in background thread */
    private boolean runInBackground = false;

    /** Executor for background thread */
    private ExecutorService executor;

    public MicroserviceApplication() {
	this(true);
    }

    public MicroserviceApplication(boolean runInBackground) {
	this.runInBackground = runInBackground;
    }

    @PostConstruct
    public void start() {
	if (isRunInBackground()) {
	    executor = Executors.newSingleThreadExecutor();
	    executor.execute(new StartMicroservice());
	} else {
	    (new StartMicroservice()).run();
	}
    }

    @PreDestroy
    public void stop() {
	if (executor != null) {
	    executor.shutdown();
	    (new StopMicroservice()).run();
	}
    }

    /**
     * Runnable for starting microservice.
     * 
     * @author Derek
     */
    private class StartMicroservice implements Runnable {

	@Override
	public void run() {
	    T service = getMicroservice();
	    try {
		// Initialize microservice.
		LifecycleProgressMonitor initMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Initialize " + service.getName()));
		service.lifecycleInitialize(initMonitor);
		if (service.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		    throw service.getLifecycleError();
		}

		// Start microservice.
		LifecycleProgressMonitor startMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Start " + service.getName()));
		service.lifecycleStart(startMonitor);
		if (service.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw service.getLifecycleError();
		}

		// Execute any post-startup code.
		service.afterMicroserviceStarted();

		// Wait for microservice to terminate.
		while (true) {
		    if (service.getLifecycleStatus() == LifecycleStatus.Terminated) {
			LOGGER.info("Terminated " + service.getName());
			break;
		    }
		    Thread.sleep(1000);
		}
	    } catch (SiteWhereException e) {
		LOGGER.error("Exception on microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice '" + service.getComponentName() + "' failed to start !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		LOGGER.info("\n" + builder.toString() + "\n");
		System.exit(2);
	    } catch (Throwable e) {
		LOGGER.error("Unhandled exception in '" + service.getComponentName() + "' microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		LOGGER.info("\n" + builder.toString() + "\n");
		System.exit(3);
	    }
	}
    }

    /**
     * Runnable for stopping microservice.
     * 
     * @author Derek
     */
    private class StopMicroservice implements Runnable {

	@Override
	public void run() {
	    IMicroservice service = getMicroservice();
	    try {
		// Stop microservice.
		LifecycleProgressMonitor stopMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Stop " + service.getName()));
		service.lifecycleStop(stopMonitor);
		if (service.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw service.getLifecycleError();
		}

		// Terminate microservice.
		LifecycleProgressMonitor termMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Terminate " + service.getName()));
		service.terminate(termMonitor);
	    } catch (SiteWhereException e) {
		LOGGER.error("Exception on microservice shutdown.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice '" + service.getComponentName() + "' failed to shutdown !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		LOGGER.info("\n" + builder.toString() + "\n");
		System.exit(2);
	    } catch (Throwable e) {
		LOGGER.error("Unhandled exception in '" + service.getComponentName() + "' microservice shutdown.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		LOGGER.info("\n" + builder.toString() + "\n");
		System.exit(3);
	    }
	}
    }

    public boolean isRunInBackground() {
	return runInBackground;
    }

    public void setRunInBackground(boolean runInBackground) {
	this.runInBackground = runInBackground;
    }
}