/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

public class TenantEngineScriptSynchronizer extends ScriptSynchronizer {

    /** Tenant engine */
    private IMicroserviceTenantEngine tenantEngine;

    /** File system root */
    private File fileSystemRoot;

    /** Zookeeper root path */
    private String zkScriptRootPath;

    public TenantEngineScriptSynchronizer(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationAdded(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationAdded(SiteWhereInstance instance) {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationUpdated(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationUpdated(SiteWhereInstance instance) {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationDeleted(io.sitewhere.k8s.crd.instance.SiteWhereInstance)
     */
    @Override
    public void onConfigurationDeleted(SiteWhereInstance instance) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#
     * getFileSystemRoot()
     */
    @Override
    public File getFileSystemRoot() {
	return fileSystemRoot;
    }

    public void setFileSystemRoot(File fileSystemRoot) {
	this.fileSystemRoot = fileSystemRoot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#
     * getZkScriptRootPath()
     */
    @Override
    public String getZkScriptRootPath() {
	return zkScriptRootPath;
    }

    public void setZkScriptRootPath(String zkScriptRootPath) {
	this.zkScriptRootPath = zkScriptRootPath;
    }

    protected IMicroserviceTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    protected void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}