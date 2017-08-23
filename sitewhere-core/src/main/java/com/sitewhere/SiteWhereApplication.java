/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.ISiteWhereApplication;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.system.IVersion;

/**
 * Root Spring Boot application that loads all other SiteWhere artifacts.
 * 
 * @author Derek
 */
@Configuration
public class SiteWhereApplication implements ISiteWhereApplication {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Executor for server operations */
    private ExecutorService executor;

    @PostConstruct
    public void start() {

	if (executor != null) {
	    executor.shutdownNow();
	}
	executor = Executors.newSingleThreadExecutor(new SiteWhereThreadFactory());
	Future<Void> startup = executor.submit(new Callable<Void>() {

	    @Override
	    public Void call() {
		try {
		    ILifecycleProgressMonitor monitor = createStartupProgressMonitor();
		    SiteWhere.start(SiteWhereApplication.this, monitor);
		    LOGGER.info("Server started successfully.");
		    SiteWhere.getServer().logState();
		} catch (ServerStartupException e) {
		    SiteWhere.getServer().setServerStartupError(e);
		    StringBuilder builder = new StringBuilder();
		    builder.append("\n!!!! SiteWhere Server Failed to Start !!!!\n");
		    builder.append("\n");
		    builder.append("Component: " + e.getDescription() + "\n");
		    builder.append("Error: " + e.getComponent().getLifecycleError().getMessage() + "\n");
		    LOGGER.info("\n" + builder.toString() + "\n");
		    System.exit(1);
		} catch (SiteWhereException e) {
		    LOGGER.error("Exception on server startup.", e);
		    StringBuilder builder = new StringBuilder();
		    builder.append("\n!!!! SiteWhere Server Failed to Start !!!!\n");
		    builder.append("\n");
		    builder.append("Error: " + e.getMessage() + "\n");
		    LOGGER.info("\n" + builder.toString() + "\n");
		    System.exit(2);
		} catch (Throwable e) {
		    LOGGER.error("Unhandled exception in server startup.", e);
		    StringBuilder builder = new StringBuilder();
		    builder.append("\n!!!! Unhandled Exception !!!!\n");
		    builder.append("\n");
		    builder.append("Error: " + e.getMessage() + "\n");
		    LOGGER.info("\n" + builder.toString() + "\n");
		    System.exit(3);
		}
		return null;
	    }
	});
	try {
	    startup.get();
	} catch (InterruptedException e) {
	    LOGGER.error("SiteWhere startup interrupted.", e);
	} catch (ExecutionException e) {
	    LOGGER.error("SiteWhere startup failed.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereApplication#getServerClass()
     */
    @Override
    public Class<? extends SiteWhereServer> getServerClass() throws SiteWhereException {
	return SiteWhereServer.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereApplication#getVersionClass()
     */
    @Override
    public Class<? extends IVersion> getVersionClass() throws SiteWhereException {
	return Version.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereApplication#
     * createStartupProgressMonitor()
     */
    @Override
    public ILifecycleProgressMonitor createStartupProgressMonitor() {
	return new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Starting SiteWhere Server"));
    }

    public static void main(String[] args) {
	SpringApplication.run(SiteWhereApplication.class, args);
    }

    /** Used for naming SiteWhere threads */
    public static class SiteWhereThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "SiteWhere Server");
	}
    }
}