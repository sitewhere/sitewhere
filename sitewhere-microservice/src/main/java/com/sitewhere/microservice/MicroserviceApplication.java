/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import javax.annotation.PostConstruct;

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
public abstract class MicroserviceApplication implements IMicroserviceApplication {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    @PostConstruct
    public void start() {
	IMicroservice service = getMicroservice();
	try {
	    // Initialize microservice.
	    LifecycleProgressMonitor initMonitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Initialize " + service.getName()));
	    service.initialize(initMonitor);

	    // Start microservice.
	    LifecycleProgressMonitor startMonitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Start " + service.getName()));
	    service.start(startMonitor);

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