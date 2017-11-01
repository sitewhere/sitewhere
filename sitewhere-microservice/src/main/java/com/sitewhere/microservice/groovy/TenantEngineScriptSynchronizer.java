/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.groovy;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IScriptSynchronizer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

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
	super(tenantEngine.getMicroservice());
	this.tenantEngine = tenantEngine;
	setFileSystemRoot(computeFilesystemPathForTenant());
	setZkScriptRootPath(computeZkScriptRootPathForTenant());
    }

    /**
     * Compute relative path for storing tenant scripts.
     * 
     * @return
     */
    protected File computeFilesystemPathForTenant() {
	File root = new File(getMicrosevice().getInstanceSettings().getFileSystemStorageRoot());
	File tenants = new File(root, TENANTS_SUBPATH);
	File tenant = new File(tenants, getTenantEngine().getTenant().getId());
	if (!tenant.getParentFile().exists()) {
	    tenant.getParentFile().mkdirs();
	}
	return tenant;
    }

    /**
     * Compute Zk path for tenant scripts.
     * 
     * @return
     */
    protected String computeZkScriptRootPathForTenant() {
	try {
	    return getTenantEngine().getTenantConfigurationPath();
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Unable to calculate Zk script root path for tenant.", e);
	}
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

    public IMicroserviceTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}