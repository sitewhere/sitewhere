/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.util.UUID;

import com.sitewhere.grpc.client.cache.CacheUtils;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to device management API channel.
 * 
 * @author Derek
 */
public class CachedDeviceManagementApiChannel extends DeviceManagementApiChannel {

    /** Area cache */
    private ICacheProvider<String, IArea> areaCache;

    /** Area by id cache */
    private ICacheProvider<UUID, IArea> areaByIdCache;

    /** Device type cache */
    private ICacheProvider<String, IDeviceType> deviceTypeCache;

    /** Device type by id cache */
    private ICacheProvider<UUID, IDeviceType> deviceTypeByIdCache;

    /** Device cache */
    private ICacheProvider<String, IDevice> deviceCache;

    /** Device by id cache */
    private ICacheProvider<UUID, IDevice> deviceByIdCache;

    /** Device assignment cache */
    private ICacheProvider<String, IDeviceAssignment> deviceAssignmentCache;

    /** Device assignment by id cache */
    private ICacheProvider<UUID, IDeviceAssignment> deviceAssignmentByIdCache;

    public CachedDeviceManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.areaCache = new DeviceManagementCacheProviders.AreaCache(microservice, false);
	this.areaByIdCache = new DeviceManagementCacheProviders.AreaByIdCache(microservice, false);
	this.deviceTypeCache = new DeviceManagementCacheProviders.DeviceTypeCache(microservice, false);
	this.deviceTypeByIdCache = new DeviceManagementCacheProviders.DeviceTypeByIdCache(microservice, false);
	this.deviceCache = new DeviceManagementCacheProviders.DeviceCache(microservice, false);
	this.deviceByIdCache = new DeviceManagementCacheProviders.DeviceByIdCache(microservice, false);
	this.deviceAssignmentCache = new DeviceManagementCacheProviders.DeviceAssignmentCache(microservice, false);
	this.deviceAssignmentByIdCache = new DeviceManagementCacheProviders.DeviceAssignmentByIdCache(microservice,
		false);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaByToken(java.lang.String)
     */
    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea area = getAreaCache().getCacheEntry(tenant, token);
	if (area != null) {
	    CacheUtils.logCacheHit(area);
	    return area;
	} else {
	    getLogger().trace("No cached information for area '" + token + "'.");
	}
	return super.getAreaByToken(token);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.device.DeviceManagementApiChannel#getArea(java.util
     * .UUID)
     */
    @Override
    public IArea getArea(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea area = getAreaByIdCache().getCacheEntry(tenant, id);
	if (area != null) {
	    CacheUtils.logCacheHit(area);
	    return area;
	} else {
	    getLogger().trace("No cached information for area id '" + id + "'.");
	}
	return super.getArea(id);
    }

    /*
     * @see com.sitewhere.grpc.client.device.DeviceManagementApiChannel#
     * getDeviceTypeByToken(java.lang.String)
     */
    @Override
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType deviceType = getDeviceTypeCache().getCacheEntry(tenant, token);
	if (deviceType != null) {
	    CacheUtils.logCacheHit(deviceType);
	    return deviceType;
	} else {
	    getLogger().trace("No cached information for device type '" + token + "'.");
	}
	return super.getDeviceTypeByToken(token);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.device.DeviceManagementApiChannel#getDeviceType(
     * java.util.UUID)
     */
    @Override
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType deviceType = getDeviceTypeByIdCache().getCacheEntry(tenant, id);
	if (deviceType != null) {
	    CacheUtils.logCacheHit(deviceType);
	    return deviceType;
	} else {
	    getLogger().trace("No cached information for device type id '" + id + "'.");
	}
	return super.getDeviceType(id);
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
	    CacheUtils.logCacheHit(device);
	    return device;
	} else {
	    getLogger().trace("No cached information for device '" + hardwareId + "'.");
	}
	return super.getDeviceByHardwareId(hardwareId);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.device.DeviceManagementApiChannel#getDevice(java.
     * util.UUID)
     */
    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice device = getDeviceByIdCache().getCacheEntry(tenant, deviceId);
	if (device != null) {
	    CacheUtils.logCacheHit(device);
	    return device;
	} else {
	    getLogger().trace("No cached information for device id '" + deviceId + "'.");
	}
	return super.getDevice(deviceId);
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
	    CacheUtils.logCacheHit(assignment);
	    return assignment;
	} else {
	    getLogger().trace("No cached information for assignment '" + token + "'.");
	}
	return super.getDeviceAssignmentByToken(token);
    }

    /*
     * @see com.sitewhere.grpc.client.device.DeviceManagementApiChannel#
     * getDeviceAssignment(java.util.UUID)
     */
    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment assignment = getDeviceAssignmentByIdCache().getCacheEntry(tenant, id);
	if (assignment != null) {
	    CacheUtils.logCacheHit(assignment);
	    return assignment;
	} else {
	    getLogger().trace("No cached information for assignment id '" + id + "'.");
	}
	return super.getDeviceAssignment(id);
    }

    public ICacheProvider<String, IArea> getAreaCache() {
	return areaCache;
    }

    public void setAreaCache(ICacheProvider<String, IArea> areaCache) {
	this.areaCache = areaCache;
    }

    public ICacheProvider<UUID, IArea> getAreaByIdCache() {
	return areaByIdCache;
    }

    public void setAreaByIdCache(ICacheProvider<UUID, IArea> areaByIdCache) {
	this.areaByIdCache = areaByIdCache;
    }

    public ICacheProvider<String, IDeviceType> getDeviceTypeCache() {
	return deviceTypeCache;
    }

    public void setDeviceTypeCache(ICacheProvider<String, IDeviceType> deviceTypeCache) {
	this.deviceTypeCache = deviceTypeCache;
    }

    public ICacheProvider<UUID, IDeviceType> getDeviceTypeByIdCache() {
	return deviceTypeByIdCache;
    }

    public void setDeviceTypeByIdCache(ICacheProvider<UUID, IDeviceType> deviceTypeByIdCache) {
	this.deviceTypeByIdCache = deviceTypeByIdCache;
    }

    protected ICacheProvider<String, IDevice> getDeviceCache() {
	return deviceCache;
    }

    protected void setDeviceCache(ICacheProvider<String, IDevice> deviceCache) {
	this.deviceCache = deviceCache;
    }

    public ICacheProvider<UUID, IDevice> getDeviceByIdCache() {
	return deviceByIdCache;
    }

    public void setDeviceByIdCache(ICacheProvider<UUID, IDevice> deviceByIdCache) {
	this.deviceByIdCache = deviceByIdCache;
    }

    protected ICacheProvider<String, IDeviceAssignment> getDeviceAssignmentCache() {
	return deviceAssignmentCache;
    }

    protected void setDeviceAssignmentCache(ICacheProvider<String, IDeviceAssignment> deviceAssignmentCache) {
	this.deviceAssignmentCache = deviceAssignmentCache;
    }

    public ICacheProvider<UUID, IDeviceAssignment> getDeviceAssignmentByIdCache() {
	return deviceAssignmentByIdCache;
    }

    public void setDeviceAssignmentByIdCache(ICacheProvider<UUID, IDeviceAssignment> deviceAssignmentByIdCache) {
	this.deviceAssignmentByIdCache = deviceAssignmentByIdCache;
    }
}