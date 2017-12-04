/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds Apache Ignite caching support to device management API channel.
 * 
 * @author Derek
 */
public class CachedDeviceManagementApiChannel extends DeviceManagementApiChannel {

    /** Device specification cache */
    private ICacheProvider<String, IDeviceSpecification> deviceSpecificationCache;

    /** Device cache */
    private ICacheProvider<String, IDevice> deviceCache;

    /** Device assignment cache */
    private ICacheProvider<String, IDeviceAssignment> deviceAssignmentCache;

    public CachedDeviceManagementApiChannel(IMicroservice microservice, String host) {
	super(microservice, host);
	this.deviceSpecificationCache = new DeviceManagementCacheProviders.DeviceSpecificationCache(microservice,
		false);
	this.deviceCache = new DeviceManagementCacheProviders.DeviceCache(microservice, false);
	this.deviceAssignmentCache = new DeviceManagementCacheProviders.DeviceAssignmentCache(microservice, false);
    }

    /*
     * @see com.sitewhere.grpc.model.client.DeviceManagementApiChannel#
     * getDeviceSpecificationByToken(java.lang.String)
     */
    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceSpecification specification = getDeviceSpecificationCache().getCacheEntry(tenant, token);
	if (specification != null) {
	    getLogger().trace("Using cached information for specification '" + token + "'.");
	    return specification;
	} else {
	    getLogger().trace("No cached information for specification '" + token + "'.");
	}
	return super.getDeviceSpecificationByToken(token);
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceByHardwareId(java.
     * lang.String)
     */
    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice device = getDeviceCache().getCacheEntry(tenant, hardwareId);
	if (device != null) {
	    getLogger().trace("Using cached information for device '" + hardwareId + "'.");
	    return device;
	} else {
	    getLogger().trace("No cached information for device '" + hardwareId + "'.");
	}
	return super.getDeviceByHardwareId(hardwareId);
    }

    /*
     * @see com.sitewhere.grpc.model.client.DeviceManagementApiChannel#
     * getDeviceAssignmentByToken(java.lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment assignment = getDeviceAssignmentCache().getCacheEntry(tenant, token);
	if (assignment != null) {
	    getLogger().trace("Using cached information for assignment '" + token + "'.");
	    return assignment;
	} else {
	    getLogger().trace("No cached information for assignment '" + token + "'.");
	}
	return super.getDeviceAssignmentByToken(token);
    }

    protected ICacheProvider<String, IDeviceSpecification> getDeviceSpecificationCache() {
	return deviceSpecificationCache;
    }

    protected void setDeviceSpecificationCache(ICacheProvider<String, IDeviceSpecification> deviceSpecificationCache) {
	this.deviceSpecificationCache = deviceSpecificationCache;
    }

    protected ICacheProvider<String, IDevice> getDeviceCache() {
	return deviceCache;
    }

    protected void setDeviceCache(ICacheProvider<String, IDevice> deviceCache) {
	this.deviceCache = deviceCache;
    }

    protected ICacheProvider<String, IDeviceAssignment> getDeviceAssignmentCache() {
	return deviceAssignmentCache;
    }

    protected void setDeviceAssignmentCache(ICacheProvider<String, IDeviceAssignment> deviceAssignmentCache) {
	this.deviceAssignmentCache = deviceAssignmentCache;
    }
}