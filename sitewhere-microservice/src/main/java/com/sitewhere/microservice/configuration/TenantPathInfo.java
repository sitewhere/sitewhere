/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.ITenantPathInfo;

/**
 * Holds path information for a file relative to the tenant configuration
 * folder.
 */
public class TenantPathInfo implements ITenantPathInfo {

    /** Tenant id */
    private UUID tenantId;

    /** Relative path */
    private String path;

    /**
     * Compute path info for a full path using the context of a microservice.
     * 
     * @param fullPath
     * @param microservice
     * @return
     * @throws SiteWhereException
     */
    public static TenantPathInfo compute(String fullPath, IConfigurableMicroservice<?> microservice)
	    throws SiteWhereException {
	String tenantsRoot = microservice.getInstanceTenantsConfigurationPath() + "/";
	if (fullPath.startsWith(tenantsRoot)) {
	    String tenantPath = fullPath.substring(tenantsRoot.length());
	    if (tenantPath.length() > 1) {
		int firstSlash = tenantPath.indexOf('/');
		if (firstSlash != -1) {
		    TenantPathInfo info = new TenantPathInfo();
		    info.setTenantId(UUID.fromString(tenantPath.substring(0, firstSlash)));
		    info.setPath(tenantPath.substring(firstSlash + 1));
		    return info;
		}
	    }
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.ITenantPathInfo#getTenantId()
     */
    @Override
    public UUID getTenantId() {
	return tenantId;
    }

    public void setTenantId(UUID tenantId) {
	this.tenantId = tenantId;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.ITenantPathInfo#getPath()
     */
    @Override
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }
}