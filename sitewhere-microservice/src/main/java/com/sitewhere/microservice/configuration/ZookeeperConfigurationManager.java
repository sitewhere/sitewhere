/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.spi.IZookeeperConfigurationManager;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Handles microservice configuration by interacting with Apache Zookeeper.
 * 
 * @author Derek
 */
public class ZookeeperConfigurationManager extends LifecycleComponent implements IZookeeperConfigurationManager {

    /** Base namespace for all SiteWhere Zookeeper artifacts */
    private static final String SITEWHERE_ZK_NAMESPACE = "sitewhere";

    /** Default Zookeeper connection string */
    private static final String DEFAULT_ZK_CONNECTION = "localhost:2181";

    /** Max time in seconds to wait for Zookeeper connection */
    private static final int MAX_ZK_WAIT_SECS = 30;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Zookeeper connection information */
    private String zkConnection = DEFAULT_ZK_CONNECTION;

    /** Curator client */
    private CuratorFramework curator;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	checkEnvForZkConnect();
	connect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	disconnect();
    }

    /**
     * Check environment variable for Zookeeper connection information.
     */
    protected void checkEnvForZkConnect() {
	String envZkConnect = System.getenv().get(MicroserviceEnvironment.ENV_ZOOKEEPER_CONNECT);
	if (envZkConnect != null) {
	    setZkConnection(envZkConnect);
	    LOGGER.info("Zookeeper connection string loaded from " + MicroserviceEnvironment.ENV_ZOOKEEPER_CONNECT
		    + ": " + envZkConnect);
	}
    }

    /**
     * Connect to Zookeeper.
     * 
     * @throws SiteWhereException
     */
    protected void connect() throws SiteWhereException {
	RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
	this.curator = CuratorFrameworkFactory.builder().namespace(SITEWHERE_ZK_NAMESPACE)
		.connectString(getZkConnection()).retryPolicy(retryPolicy).build();
	getCurator().start();
	try {
	    if (!getCurator().blockUntilConnected(MAX_ZK_WAIT_SECS, TimeUnit.SECONDS)) {
		throw new SiteWhereException("Unable to connect to Zookeeper.");
	    }
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted while connecting to Zookeeper.", e);
	}
    }

    /**
     * Disconnect from Zookeeper.
     * 
     * @throws SiteWhereException
     */
    protected void disconnect() throws SiteWhereException {
	if (getCurator() != null) {
	    getCurator().close();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IZookeeperConfigurationManager#getCurator(
     * )
     */
    public CuratorFramework getCurator() {
	return curator;
    }

    public void setCurator(CuratorFramework curator) {
	this.curator = curator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getZkConnection() {
	return zkConnection;
    }

    public void setZkConnection(String zkConnection) {
	this.zkConnection = zkConnection;
    }
}