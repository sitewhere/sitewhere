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
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IScriptSynchronizer} that copies tenant-level
 * scripts from Zookeeper to the local filesystem of a microservice.
 * 
 * @author Derek
 */
public class TenantEngineScriptSynchronizer extends ScriptSynchronizer {

    /** Subpath that holds tenant scripts */
    private static final String TENANTS_SUBPATH = "tenants";

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
	setFileSystemRoot(computeFilesystemPathForTenant());
	// setZkScriptRootPath(((IMultitenantMicroservice<?, ?>)
	// getMicroservice()).getScriptManagement()
	// .getScriptContentZkPath(getMicroservice().getIdentifier(),
	// getTenantEngine().getTenant().getId()));
    }

    /**
     * Compute relative path for storing tenant scripts.
     * 
     * @return
     */
    protected File computeFilesystemPathForTenant() {
	File root = new File(getMicroservice().getInstanceSettings().getFileSystemStorageRoot());
	File tenants = new File(root, TENANTS_SUBPATH);
	File tenant = new File(tenants, getTenantEngine().getTenant().getToken());
	if (!tenant.getParentFile().exists()) {
	    tenant.getParentFile().mkdirs();
	}
	return tenant;
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