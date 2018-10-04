/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.util.UUID;

import com.sitewhere.grpc.client.cache.DeviceManagementCacheProviders;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
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

    public CachedDeviceManagementApiChannel(IApiDemux<?> demux, String host, int port) {
	super(demux, host, port);
	this.areaCache = new DeviceManagementCacheProviders.AreaByTokenCache();
	this.areaByIdCache = new DeviceManagementCacheProviders.AreaByIdCache();
	this.deviceTypeCache = new DeviceManagementCacheProviders.DeviceTypeByTokenCache();
	this.deviceTypeByIdCache = new DeviceManagementCacheProviders.DeviceTypeByIdCache();
	this.deviceCache = new DeviceManagementCacheProviders.DeviceByTokenCache();
	this.deviceByIdCache = new DeviceManagementCacheProviders.DeviceByIdCache();
	this.deviceAssignmentCache = new DeviceManagementCacheProviders.DeviceAssignmentByTokenCache();
	this.deviceAssignmentByIdCache = new DeviceManagementCacheProviders.DeviceAssignmentByIdCache();
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#initialize(com.sitewhere.spi.server.
     * lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getAreaCache(), monitor, true);
	initializeNestedComponent(getAreaByIdCache(), monitor, true);
	initializeNestedComponent(getDeviceTypeCache(), monitor, true);
	initializeNestedComponent(getDeviceTypeByIdCache(), monitor, true);
	initializeNestedComponent(getDeviceCache(), monitor, true);
	initializeNestedComponent(getDeviceByIdCache(), monitor, true);
	initializeNestedComponent(getDeviceAssignmentCache(), monitor, true);
	initializeNestedComponent(getDeviceAssignmentByIdCache(), monitor, true);
	super.initialize(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#start(com.sitewhere.spi.server.lifecycle
     * .ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getAreaCache(), monitor, true);
	startNestedComponent(getAreaByIdCache(), monitor, true);
	startNestedComponent(getDeviceTypeCache(), monitor, true);
	startNestedComponent(getDeviceTypeByIdCache(), monitor, true);
	startNestedComponent(getDeviceCache(), monitor, true);
	startNestedComponent(getDeviceByIdCache(), monitor, true);
	startNestedComponent(getDeviceAssignmentCache(), monitor, true);
	startNestedComponent(getDeviceAssignmentByIdCache(), monitor, true);
	super.start(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#stop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getAreaCache(), monitor);
	stopNestedComponent(getAreaByIdCache(), monitor);
	stopNestedComponent(getDeviceTypeCache(), monitor);
	stopNestedComponent(getDeviceTypeByIdCache(), monitor);
	stopNestedComponent(getDeviceCache(), monitor);
	stopNestedComponent(getDeviceByIdCache(), monitor);
	stopNestedComponent(getDeviceAssignmentCache(), monitor);
	stopNestedComponent(getDeviceAssignmentByIdCache(), monitor);
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaByToken(java.lang.String)
     */
    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea area = getAreaCache().getCacheEntry(tenant, token);
	if (area == null) {
	    area = super.getAreaByToken(token);
	    getAreaCache().setCacheEntry(tenant, token, area);
	}
	return area;
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
	if (area == null) {
	    area = super.getArea(id);
	    getAreaByIdCache().setCacheEntry(tenant, id, area);
	}
	return area;
    }

    /*
     * @see com.sitewhere.grpc.client.device.DeviceManagementApiChannel#
     * getDeviceTypeByToken(java.lang.String)
     */
    @Override
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType deviceType = getDeviceTypeCache().getCacheEntry(tenant, token);
	if (deviceType == null) {
	    deviceType = super.getDeviceTypeByToken(token);
	    getDeviceTypeCache().setCacheEntry(tenant, token, deviceType);
	}
	return deviceType;
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
	if (deviceType == null) {
	    deviceType = super.getDeviceType(id);
	    getDeviceTypeByIdCache().setCacheEntry(tenant, id, deviceType);
	}
	return deviceType;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.device.DeviceManagementApiChannel#getDeviceByToken(
     * java.lang.String)
     */
    @Override
    public IDevice getDeviceByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice device = getDeviceCache().getCacheEntry(tenant, token);
	if (device == null) {
	    device = super.getDeviceByToken(token);
	    getDeviceCache().setCacheEntry(tenant, token, device);
	}
	return device;
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
	if (device == null) {
	    device = super.getDevice(deviceId);
	    getDeviceByIdCache().setCacheEntry(tenant, deviceId, device);
	}
	return device;
    }

    /*
     * @see com.sitewhere.grpc.model.client.DeviceManagementApiChannel#
     * getDeviceAssignmentByToken(java.lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment assignment = getDeviceAssignmentCache().getCacheEntry(tenant, token);
	if (assignment == null) {
	    assignment = super.getDeviceAssignmentByToken(token);
	    getDeviceAssignmentCache().setCacheEntry(tenant, token, assignment);
	}
	return assignment;
    }

    /*
     * @see com.sitewhere.grpc.client.device.DeviceManagementApiChannel#
     * getDeviceAssignment(java.util.UUID)
     */
    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment assignment = getDeviceAssignmentByIdCache().getCacheEntry(tenant, id);
	if (assignment == null) {
	    assignment = super.getDeviceAssignment(id);
	    getDeviceAssignmentByIdCache().setCacheEntry(tenant, id, assignment);
	}
	return assignment;
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