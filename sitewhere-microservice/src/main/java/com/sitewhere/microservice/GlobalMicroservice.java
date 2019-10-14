/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import com.sitewhere.microservice.configuration.ConfigurableMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Microservice that serves a global function in a SiteWhere instance.
 */
public abstract class GlobalMicroservice<T extends IFunctionIdentifier> extends ConfigurableMicroservice<T>
	implements IGlobalMicroservice<T> {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * initialize(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Call logic for initializing microservice subclass.
	microserviceInitialize(monitor);

	// Wait for microservice to be configured.
	waitForConfigurationReady();
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
	super.start(monitor);

	// Call logic for starting microservice subclass.
	microserviceStart(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Call logic for stopping microservice subclass.
	microserviceStop(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.IGlobalMicroservice#getConfiguration()
     */
    @Override
    public byte[] getConfiguration() throws SiteWhereException {
	return getConfigurationDataFor(getInstanceConfigurationPath() + "/" + getConfigurationPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	if (isConfigurationCacheReady()) {
	    try {
		if (isConfigurationPath(path)) {
		    getLogger().info("Microservice configuration added.");
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to processing added configuration.", e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	if (isConfigurationCacheReady()) {
	    try {
		if (isConfigurationPath(path)) {
		    getLogger().info("Microservice configuration updated.");
		    restartConfiguration();
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to processing updated configuration.", e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	if (isConfigurationCacheReady()) {
	    try {
		if (isConfigurationPath(path)) {
		    getLogger().info("Microservice configuration deleted.");
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to processing deleted configuration.", e);
	    }
	}
    }

    /**
     * Indicates whether the given path points to a configuration file.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    protected boolean isConfigurationPath(String path) throws SiteWhereException {
	String localConfig = getInstanceConfigurationPath() + "/" + getConfigurationPath();
	String instanceConfig = getInstanceManagementConfigurationPath();
	if ((localConfig.equals(path)) || (instanceConfig.equals(path))) {
	    return true;
	}
	return false;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IGlobalMicroservice#updateConfiguration(byte[]
     * )
     */
    @Override
    public void updateConfiguration(byte[] content) throws SiteWhereException {
	// try {
	// String configPath = getInstanceConfigurationPath() + "/" +
	// getConfigurationPath();
	// CuratorFramework curator = getZookeeperManager().getCurator();
	// if (curator.checkExists().forPath(configPath) == null) {
	// curator.create().forPath(configPath, content);
	// } else {
	// curator.setData().forPath(configPath, content);
	// }
	// } catch (Exception e) {
	// throw new SiteWhereException("Unable to update module configuration.", e);
	// }
    }
}