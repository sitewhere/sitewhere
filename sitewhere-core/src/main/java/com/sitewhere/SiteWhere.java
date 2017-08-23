/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.kafka.EmbeddedKafkaCluster;
import com.sitewhere.kafka.EmbeddedZookeeper;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.ISiteWhereApplication;
import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.system.IVersion;

/**
 * Main class for accessing core SiteWhere functionality.
 * 
 * @author Derek
 */
public class SiteWhere {

    /** Private logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Embedded zookeeper instance */
    private static EmbeddedZookeeper ZOOKEEPER;

    /** Embedded Kafka cluster */
    private static EmbeddedKafkaCluster KAFKA;

    /** Singleton server instance */
    private static ISiteWhereServer SERVER;

    /** SiteWhere version information */
    private static IVersion VERSION;

    /**
     * Start embedded Zookeeper instance.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void startEmbeddedZookeeper(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ZOOKEEPER = new EmbeddedZookeeper();
	ZOOKEEPER.initialize(monitor);
	if (ZOOKEEPER.getLifecycleStatus() == LifecycleStatus.InitializationError) {
	    LOGGER.error("Exception while initializing embedded Zookeeper.", ZOOKEEPER.getLifecycleError());
	}
	ZOOKEEPER.start(monitor);
	if (ZOOKEEPER.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    LOGGER.error("Exception while starting embedded Zookeeper.", ZOOKEEPER.getLifecycleError());
	}
    }

    /**
     * Start embedded Kafka instance.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void startEmbeddedKafka(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	KAFKA = new EmbeddedKafkaCluster();
	KAFKA.initialize(monitor);
	if (KAFKA.getLifecycleStatus() == LifecycleStatus.InitializationError) {
	    LOGGER.error("Exception while initializing embedded Kafka broker.", KAFKA.getLifecycleError());
	}
	KAFKA.start(monitor);
	if (KAFKA.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    LOGGER.error("Exception while starting embedded Kafka broker.", KAFKA.getLifecycleError());
	}
    }

    /**
     * Called once to bootstrap the SiteWhere server.
     * 
     * @param application
     * @param monitor
     * @throws SiteWhereException
     */
    public static void start(ISiteWhereApplication application, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	Class<? extends IVersion> version = application.getVersionClass();
	Class<? extends ISiteWhereServer> server = application.getServerClass();
	try {
	    // Start embedded Zookeeper instance.
	    LOGGER.info("\n\nStarting Embedded Zookeeper instance...\n");
	    startEmbeddedZookeeper(monitor);

	    // Start embedded Kafka instance.
	    LOGGER.info("\n\nStarting Embedded Kafka instance...\n");
	    startEmbeddedKafka(monitor);

	    LOGGER.info("\n\nStarting SiteWhere...\n");
	    VERSION = version.newInstance();
	    SERVER = server.newInstance();

	    // Initialize server.
	    SERVER.lifecycleInitialize(monitor);
	    if (SERVER.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		LOGGER.error("Exception while initializing server.", SERVER.getLifecycleError());
	    }

	    // Start server.
	    SERVER.lifecycleStart(monitor);
	    if (SERVER.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
		throw SERVER.getLifecycleError();
	    }
	} catch (InstantiationException e) {
	    throw new SiteWhereException("Unable to create SiteWhere server instance.", e);
	} catch (IllegalAccessException e) {
	    throw new SiteWhereException("Unable to access SiteWhere server class.", e);
	}
    }

    /**
     * Called to shut down the SiteWhere server.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop server.
	SERVER.lifecycleStop(monitor);
	if (SERVER.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    LOGGER.error("Exception while shutting down server.", SERVER.getLifecycleError());
	}

	// Terminate server.
	SERVER.lifecycleTerminate(monitor);
	if (SERVER.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    LOGGER.error("Exception while terminating server.", SERVER.getLifecycleError());
	}
    }

    /**
     * Get the singleton SiteWhere server instance.
     * 
     * @return
     */
    public static ISiteWhereServer getServer() {
	if (SERVER == null) {
	    throw new RuntimeException("SiteWhere server has not been initialized.");
	}
	return SERVER;
    }

    /**
     * Determine whether server is available.
     * 
     * @return
     */
    public static boolean isServerAvailable() {
	return ((SERVER != null && (SERVER.getLifecycleStatus() == LifecycleStatus.Started)));
    }

    /**
     * Get singleton version information instance.
     * 
     * @return
     */
    public static IVersion getVersion() {
	if (VERSION == null) {
	    throw new RuntimeException("SiteWhere server has not been initialized.");
	}
	return VERSION;
    }
}