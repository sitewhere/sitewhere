/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

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

    /** Default Zookeeper connection string */
    private static final String DEFAULT_ZK_CONNECTION = "localhost:2181";

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Zookeeper connection information */
    private String zkConnection = DEFAULT_ZK_CONNECTION;

    /** Zookeeper connection */
    private ZooKeeper zookeeper;

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
	try {
	    zookeeper = new ZooKeeper(getZkConnection(), 3000, new Watcher() {
		public void process(WatchedEvent event) {
		    switch (event.getState()) {
		    case SyncConnected: {
			LOGGER.info("Connected to Zookeeper.");
			try {
			    onConnected(event);
			} catch (SiteWhereException e) {
			    LOGGER.error("Connected handler threw exception.", e);
			}
			break;
		    }
		    default: {
			LOGGER.warn("Zookeeper state was " + event.getState().name());
			break;
		    }
		    }
		}
	    });
	} catch (IOException e) {
	    LOGGER.error("Unable to connect to Zookeeper.", e);
	    throw new SiteWhereException("Unable to connect to Zookeeper.", e);
	}
    }

    /**
     * Disconnect from Zookeeper.
     * 
     * @throws SiteWhereException
     */
    protected void disconnect() throws SiteWhereException {
	if (zookeeper != null) {
	    try {
		zookeeper.close();
	    } catch (InterruptedException e) {
		throw new SiteWhereException("Unable to close Zookeeper connection.", e);
	    }
	}
    }

    /**
     * Called when Zookeeper is connected.
     * 
     * @param event
     * @throws SiteWhereException
     */
    public void onConnected(WatchedEvent event) throws SiteWhereException {
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