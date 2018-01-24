/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurationListener;
import com.sitewhere.spi.microservice.scripting.IScriptManager;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages runtime scripting support for a tenant engine.
 * 
 * @author Derek
 */
public class TenantEngineScriptManager extends TenantEngineLifecycleComponent
	implements IScriptManager, IConfigurationListener {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Get content root for scripts */
    private String scriptContentRoot;

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManager#resolve(java.lang.
     * String)
     */
    @Override
    public IScriptMetadata resolve(String scriptId) throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	getTenantEngine().getMicroservice().getConfigurationMonitor().getListeners().add(this);
	this.scriptContentRoot = getTenantEngine().getMicroservice().getScriptManagement()
		.getScriptContentZkPath(getTenantEngine().getTenant().getId());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.terminate(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	if (isScriptContent(path)) {
	    String relativePath = getRelativePath(path);
	    try {
		getTenantEngine().getTenantScriptSynchronizer().add(relativePath);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to synchronize script: " + relativePath, e);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	if (isScriptContent(path)) {
	    // Handle updated script.
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	if (isScriptContent(path)) {
	    // Handle deleted script.
	}
    }

    /**
     * Checks whether path is script content.
     * 
     * @param path
     * @return
     */
    protected boolean isScriptContent(String path) {
	if (path.startsWith(getScriptContentRoot())) {
	    if (path.substring(getScriptContentRoot().length()).length() == 0) {
		return false;
	    }
	    return true;
	}
	return false;
    }

    /**
     * Get path relative to content root.
     * 
     * @param path
     * @return
     */
    protected String getRelativePath(String path) {
	return path.substring(getScriptContentRoot().length() + 1);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getScriptContentRoot() {
	return scriptContentRoot;
    }

    public void setScriptContentRoot(String scriptContentRoot) {
	this.scriptContentRoot = scriptContentRoot;
    }
}