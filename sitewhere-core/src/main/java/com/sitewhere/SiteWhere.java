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
     * Stop embedded Zookeeper instance.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void stopEmbeddedZookeeper(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ZOOKEEPER.lifecycleStop(monitor);
	if (ZOOKEEPER.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    LOGGER.error("Exception while shutting down embedded Zookeeper.", ZOOKEEPER.getLifecycleError());
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
     * Stop embedded Kafka instance.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void stopEmbeddedKafka(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	KAFKA.lifecycleStop(monitor);
	if (KAFKA.getLifecycleStatus() == LifecycleStatus.LifecycleError) {
	    LOGGER.error("Exception while shutting down embedded Zookeeper.", KAFKA.getLifecycleError());
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
	// Start embedded Zookeeper instance.
	LOGGER.info("\n\nStarting Embedded Zookeeper instance...\n");
	startEmbeddedZookeeper(monitor);

	// Start embedded Kafka instance.
	LOGGER.info("\n\nStarting Embedded Kafka instance...\n");
	startEmbeddedKafka(monitor);
    }

    /**
     * Called to shut down the SiteWhere server.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop Kafka and Zookeeper instances.
	stopEmbeddedKafka(monitor);
	stopEmbeddedZookeeper(monitor);
    }

    public static ISiteWhereServer getServer() {
	return null;
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

    /**
     * Access the embedded Zookeeper instance.
     * 
     * @return
     */
    public static EmbeddedZookeeper getZookeeper() {
	if (ZOOKEEPER == null) {
	    throw new RuntimeException("Zookeeper has not been initialized.");
	}
	return ZOOKEEPER;
    }
}