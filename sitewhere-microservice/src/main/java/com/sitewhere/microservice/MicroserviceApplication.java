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
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceApplication;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

import io.opentracing.ActiveSpan;

/**
 * Base application for SiteWhere microservices.
 * 
 * @author Derek
 */
@ComponentScan
public abstract class MicroserviceApplication<T extends IMicroservice> implements IMicroserviceApplication<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Executor for background thread */
    private ExecutorService executor;

    @PostConstruct
    public void start() {
	executor = Executors.newSingleThreadExecutor(new MicroserviceThreadFactory());
	executor.execute(new StartMicroservice());
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
	    try {
		startMicroservice();
	    } catch (SiteWhereException e) {
		LOGGER.error("Exception on microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Microservice failed to start !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		LOGGER.info("\n" + builder.toString() + "\n");
		System.exit(2);
	    } catch (Throwable e) {
		LOGGER.error("Unhandled exception in microservice startup.", e);
		StringBuilder builder = new StringBuilder();
		builder.append("\n!!!! Unhandled Exception !!!!\n");
		builder.append("\n");
		builder.append("Error: " + e.getMessage() + "\n");
		LOGGER.info("\n" + builder.toString() + "\n");
		System.exit(3);
	    }
	    waitForTermination();
	}

	/**
	 * Start microservice.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startMicroservice() throws SiteWhereException {
	    T service = getMicroservice();

	    ActiveSpan span = null;
	    try {
		span = service.getTracer().buildSpan("Start microservice").startActive();

		// Initialize microservice.
		LifecycleProgressMonitor initMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Initialize " + service.getName()), service);
		service.lifecycleInitialize(initMonitor);
		if (service.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		    TracerUtils.handleErrorInTracerSpan(span, service.getLifecycleError());
		    throw service.getLifecycleError();
		}

		// Start microservice.
		LifecycleProgressMonitor startMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Start " + service.getName()), service);
		service.lifecycleStart(startMonitor);
		if (service.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    TracerUtils.handleErrorInTracerSpan(span, service.getLifecycleError());
		    throw service.getLifecycleError();
		}

		// Execute any post-startup code.
		service.afterMicroserviceStarted();
	    } finally {
		TracerUtils.finishTracerSpan(span);
	    }
	}

	/**
	 * Wait for application to terminate.
	 */
	protected void waitForTermination() {
	    T service = getMicroservice();

	    // Wait for microservice to terminate.
	    while (true) {
		if (service.getLifecycleStatus() == LifecycleStatus.Terminated) {
		    LOGGER.info("Terminated " + service.getName());
		    break;
		}
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    LOGGER.warn("Microservice shutting down.");
		    return;
		}
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
			new LifecycleProgressContext(1, "Stop " + service.getName()), service);
		service.lifecycleStop(stopMonitor);
		if (service.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		    throw service.getLifecycleError();
		}

		// Terminate microservice.
		LifecycleProgressMonitor termMonitor = new LifecycleProgressMonitor(
			new LifecycleProgressContext(1, "Terminate " + service.getName()), service);
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

    /** Used for naming primary microservice thread */
    private class MicroserviceThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice Main");
	}
    }
}