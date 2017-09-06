/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.data.Stat;

import com.sitewhere.microservice.Microservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Microservice that provides instance management functionality.
 * 
 * @author Derek
 */
public class InstanceManagement extends Microservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Instance Management";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Verify or create folder for instance information.
	start.addStep(verifyOrCreateInstanceFolder());

	// Execute initialization steps.
	start.execute(monitor);
    }

    /**
     * Verify that a Zk node exists to hold instance configuration information.
     * Create the folder if it does not exist. Other microservices block while
     * waiting on this node to be created.
     * 
     * @return
     */
    public ILifecycleStep verifyOrCreateInstanceFolder() {
	return new SimpleLifecycleStep("Verify or create folder for instance information") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		try {
		    Stat existing = getZookeeperConfigurationManager().getCurator().checkExists()
			    .forPath(getInstanceNodePath());
		    if (existing == null) {
			LOGGER.info("Zookeeper node for instance not found. Creating.");
			String path = getZookeeperConfigurationManager().getCurator().create()
				.forPath(getInstanceNodePath());
			LOGGER.info("Created " + path + ".");
		    } else {
			LOGGER.info("Found Zookeeper node for instance.");
		    }
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		}
	    }
	};
    }

    /**
     * Get path for storing instance information.
     * 
     * @return
     */
    protected String getInstanceNodePath() {
	return "/" + getInstanceId();
    }
}