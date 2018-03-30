/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.cache;

import java.util.UUID;

import com.sitewhere.device.DeviceManagementDecorator;
import com.sitewhere.grpc.client.cache.CacheUtils;
import com.sitewhere.grpc.client.cache.DeviceManagementCacheProviders;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.microservice.ICachingMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps {@link IDeviceManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareDeviceManagement extends DeviceManagementDecorator {

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

    public CacheAwareDeviceManagement(IDeviceManagement delegate, ICachingMicroservice microservice) {
	super(delegate);
	this.areaCache = new DeviceManagementCacheProviders.AreaByTokenCache(microservice.getHazelcastManager());
	this.areaByIdCache = new DeviceManagementCacheProviders.AreaByIdCache(microservice.getHazelcastManager());
	this.deviceTypeCache = new DeviceManagementCacheProviders.DeviceTypeByTokenCache(
		microservice.getHazelcastManager());
	this.deviceTypeByIdCache = new DeviceManagementCacheProviders.DeviceTypeByIdCache(
		microservice.getHazelcastManager());
	this.deviceCache = new DeviceManagementCacheProviders.DeviceByTokenCache(microservice.getHazelcastManager());
	this.deviceByIdCache = new DeviceManagementCacheProviders.DeviceByIdCache(microservice.getHazelcastManager());
	this.deviceAssignmentCache = new DeviceManagementCacheProviders.DeviceAssignmentByTokenCache(
		microservice.getHazelcastManager());
	this.deviceAssignmentByIdCache = new DeviceManagementCacheProviders.DeviceAssignmentByIdCache(
		microservice.getHazelcastManager());
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createArea(com.sitewhere.spi.area.
     * request.IAreaCreateRequest)
     */
    @Override
    public IArea createArea(IAreaCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea result = super.createArea(request);
	getAreaCache().setCacheEntry(tenant, result.getToken(), result);
	getAreaByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see com.sitewhere.device.DeviceManagementDecorator#getAreaByToken(java.lang.
     * String)
     */
    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea result = super.getAreaByToken(token);
	if ((result != null) && (getAreaCache().getCacheEntry(tenant, token) == null)) {
	    getAreaCache().setCacheEntry(tenant, result.getToken(), result);
	    getAreaByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see com.sitewhere.device.DeviceManagementDecorator#getArea(java.util.UUID)
     */
    @Override
    public IArea getArea(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea result = super.getArea(id);
	if ((result != null) && (getAreaByIdCache().getCacheEntry(tenant, id) == null)) {
	    getAreaCache().setCacheEntry(tenant, result.getToken(), result);
	    getAreaByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateArea(java.util.UUID,
     * com.sitewhere.spi.area.request.IAreaCreateRequest)
     */
    @Override
    public IArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea result = super.updateArea(id, request);
	getAreaCache().setCacheEntry(tenant, result.getToken(), result);
	getAreaByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#deleteArea(java.util.UUID,
     * boolean)
     */
    @Override
    public IArea deleteArea(UUID id, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IArea result = super.deleteArea(id, force);
	getAreaCache().removeCacheEntry(tenant, result.getToken());
	getAreaByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createDevice(com.sitewhere.spi
     * .device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice result = super.createDevice(device);
	getDeviceCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceByToken(java.lang.
     * String)
     */
    @Override
    public IDevice getDeviceByToken(String deviceToken) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice result = super.getDeviceByToken(deviceToken);
	if ((result != null) && (getDeviceCache().getCacheEntry(tenant, deviceToken) == null)) {
	    getDeviceCache().setCacheEntry(tenant, result.getToken(), result);
	    getDeviceByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see com.sitewhere.device.DeviceManagementDecorator#getDevice(java.util.UUID)
     */
    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice result = super.getDevice(deviceId);
	if ((result != null) && (getDeviceByIdCache().getCacheEntry(tenant, deviceId) == null)) {
	    getDeviceCache().setCacheEntry(tenant, result.getToken(), result);
	    getDeviceByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice result = super.updateDevice(id, request);
	getDeviceCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#deleteDevice(java.util.UUID,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(UUID id, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice result = super.deleteDevice(id, force);
	getDeviceCache().removeCacheEntry(tenant, result.getToken());
	getDeviceByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createDeviceAssignment(com.
     * sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.createDeviceAssignment(request);
	getDeviceAssignmentCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceAssignmentByIdCache().setCacheEntry(tenant, result.getId(), result);
	IDevice device = super.getDevice(result.getDeviceId());
	getDeviceCache().removeCacheEntry(tenant, device.getToken());
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceAssignmentByToken(
     * java.lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.getDeviceAssignmentByToken(token);
	if ((result != null) && (getDeviceAssignmentCache().getCacheEntry(tenant, token) == null)) {
	    getDeviceAssignmentCache().setCacheEntry(tenant, result.getToken(), result);
	    getDeviceAssignmentByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.getDeviceAssignment(id);
	if ((result != null) && (getDeviceAssignmentByIdCache().getCacheEntry(tenant, id) == null)) {
	    getDeviceAssignmentCache().setCacheEntry(tenant, result.getToken(), result);
	    getDeviceAssignmentByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateDeviceAssignment(java.
     * util.UUID, com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.updateDeviceAssignment(id, request);
	getDeviceAssignmentCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceAssignmentByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#deleteDeviceAssignment(java.
     * util.UUID, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.deleteDeviceAssignment(id, force);
	getDeviceAssignmentCache().removeCacheEntry(tenant, result.getToken());
	getDeviceAssignmentByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#endDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.endDeviceAssignment(id);
	getDeviceAssignmentCache().removeCacheEntry(tenant, result.getToken());
	getDeviceAssignmentByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createDeviceType(com.sitewhere
     * .spi.device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType result = super.createDeviceType(request);
	getDeviceTypeCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceTypeByToken(java.lang
     * .String)
     */
    @Override
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType result = super.getDeviceTypeByToken(token);
	if ((result != null) && (getDeviceTypeCache().getCacheEntry(tenant, token) == null)) {
	    getDeviceTypeCache().setCacheEntry(tenant, result.getToken(), result);
	    getDeviceTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceType(java.util.UUID)
     */
    @Override
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType result = super.getDeviceType(id);
	if ((result != null) && (getDeviceTypeByIdCache().getCacheEntry(tenant, id) == null)) {
	    getDeviceTypeCache().setCacheEntry(tenant, result.getToken(), result);
	    getDeviceTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateDeviceType(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType updateDeviceType(UUID id, IDeviceTypeCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType result = super.updateDeviceType(id, request);
	getDeviceTypeCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#deleteDeviceType(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceType deleteDeviceType(UUID id, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceType result = super.deleteDeviceType(id, force);
	getDeviceTypeCache().removeCacheEntry(tenant, result.getToken());
	getDeviceTypeByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
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