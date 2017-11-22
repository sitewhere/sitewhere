/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.ignite.client;

import com.sitewhere.grpc.model.client.DeviceManagementApiChannel;
import com.sitewhere.grpc.model.client.DeviceManagementGrpcChannel;
import com.sitewhere.microservice.ignite.DeviceManagementCacheProviders;
import com.sitewhere.microservice.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.ignite.IIgniteCacheProvider;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds Apache Ignite caching support to device management API channel.
 * 
 * @author Derek
 */
public class CachedDeviceManagementApiChannel extends DeviceManagementApiChannel {

    /** Device cache */
    private IIgniteCacheProvider<String, IDevice> deviceCache;

    /** Device assignment cache */
    private IIgniteCacheProvider<String, IDeviceAssignment> deviceAssignmentCache;

    public CachedDeviceManagementApiChannel(IMicroservice microservice, DeviceManagementGrpcChannel grpcChannel) {
	super(grpcChannel);
	this.deviceCache = new DeviceManagementCacheProviders.DeviceCache(microservice, false);
	this.deviceAssignmentCache = new DeviceManagementCacheProviders.DeviceAssignmentCache(microservice, false);
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

    protected IIgniteCacheProvider<String, IDevice> getDeviceCache() {
	return deviceCache;
    }

    protected void setDeviceCache(IIgniteCacheProvider<String, IDevice> deviceCache) {
	this.deviceCache = deviceCache;
    }

    protected IIgniteCacheProvider<String, IDeviceAssignment> getDeviceAssignmentCache() {
	return deviceAssignmentCache;
    }

    protected void setDeviceAssignmentCache(IIgniteCacheProvider<String, IDeviceAssignment> deviceAssignmentCache) {
	this.deviceAssignmentCache = deviceAssignmentCache;
    }
}