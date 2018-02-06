/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.zookeeper;

import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IZookeeperManager;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Handles microservice configuration by interacting with Apache Zookeeper.
 * 
 * @author Derek
 */
public class ZookeeperManager extends LifecycleComponent implements IZookeeperManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Base namespace for all SiteWhere Zookeeper artifacts */
    private static final String SITEWHERE_ZK_NAMESPACE = "sitewhere";

    /** Max time in seconds to wait for Zookeeper connection */
    private static final int MAX_ZK_WAIT_SECS = 30;

    /** Curator client */
    private CuratorFramework curator;

    @Autowired
    private IInstanceSettings instanceSettings;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
     * Connect to Zookeeper.
     * 
     * @throws SiteWhereException
     */
    protected void connect() throws SiteWhereException {
	String zk = getInstanceSettings().getZookeeperHost() + ":" + getInstanceSettings().getZookeeperPort();
	RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
	this.curator = CuratorFrameworkFactory.builder().namespace(SITEWHERE_ZK_NAMESPACE).connectString(zk)
		.retryPolicy(retryPolicy).build();
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
     * com.sitewhere.microservice.spi.configuration.IZookeeperManager#getCurator
     * ()
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

    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    public void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }
}