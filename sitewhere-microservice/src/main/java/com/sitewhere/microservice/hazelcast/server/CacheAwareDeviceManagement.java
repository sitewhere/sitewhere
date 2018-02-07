/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast.server;

import java.util.Map;
import java.util.UUID;

import com.sitewhere.device.DeviceManagementDecorator;
import com.sitewhere.grpc.client.cache.CacheUtils;
import com.sitewhere.grpc.client.device.DeviceManagementCacheProviders;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps {@link IDeviceManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareDeviceManagement extends DeviceManagementDecorator {

    /** Site cache */
    private ICacheProvider<String, ISite> siteCache;

    /** Site by id cache */
    private ICacheProvider<UUID, ISite> siteByIdCache;

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

    public CacheAwareDeviceManagement(IDeviceManagement delegate, IMicroservice microservice) {
	super(delegate);
	this.siteCache = new DeviceManagementCacheProviders.SiteCache(microservice, true);
	this.siteByIdCache = new DeviceManagementCacheProviders.SiteByIdCache(microservice, true);
	this.deviceTypeCache = new DeviceManagementCacheProviders.DeviceTypeCache(microservice, true);
	this.deviceTypeByIdCache = new DeviceManagementCacheProviders.DeviceTypeByIdCache(microservice, true);
	this.deviceCache = new DeviceManagementCacheProviders.DeviceCache(microservice, true);
	this.deviceByIdCache = new DeviceManagementCacheProviders.DeviceByIdCache(microservice, true);
	this.deviceAssignmentCache = new DeviceManagementCacheProviders.DeviceAssignmentCache(microservice, true);
	this.deviceAssignmentByIdCache = new DeviceManagementCacheProviders.DeviceAssignmentByIdCache(microservice,
		true);
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createSite(com.sitewhere.spi.
     * device.request.ISiteCreateRequest)
     */
    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	ISite result = super.createSite(request);
	getSiteCache().setCacheEntry(tenant, result.getToken(), result);
	getSiteByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see com.sitewhere.device.DeviceManagementDecorator#getSiteByToken(java.lang.
     * String)
     */
    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	ISite result = super.getSiteByToken(token);
	if ((result != null) && (getSiteCache().getCacheEntry(tenant, token) == null)) {
	    getSiteCache().setCacheEntry(tenant, result.getToken(), result);
	    getSiteByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see com.sitewhere.device.DeviceManagementDecorator#getSite(java.util.UUID)
     */
    @Override
    public ISite getSite(UUID id) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	ISite result = super.getSite(id);
	if ((result != null) && (getSiteByIdCache().getCacheEntry(tenant, id) == null)) {
	    getSiteCache().setCacheEntry(tenant, result.getToken(), result);
	    getSiteByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateSite(java.util.UUID,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(UUID id, ISiteCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	ISite result = super.updateSite(id, request);
	getSiteCache().setCacheEntry(tenant, result.getToken(), result);
	getSiteByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#deleteSite(java.util.UUID,
     * boolean)
     */
    @Override
    public ISite deleteSite(UUID id, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	ISite result = super.deleteSite(id, force);
	getSiteCache().removeCacheEntry(tenant, result.getToken());
	getSiteByIdCache().removeCacheEntry(tenant, result.getId());
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
	getDeviceCache().setCacheEntry(tenant, result.getHardwareId(), result);
	getDeviceByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#getDeviceByHardwareId(java.
     * lang.String)
     */
    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDevice result = super.getDeviceByHardwareId(hardwareId);
	if ((result != null) && (getDeviceCache().getCacheEntry(tenant, hardwareId) == null)) {
	    getDeviceCache().setCacheEntry(tenant, result.getHardwareId(), result);
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
	    getDeviceCache().setCacheEntry(tenant, result.getHardwareId(), result);
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
	getDeviceCache().setCacheEntry(tenant, result.getHardwareId(), result);
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
	getDeviceCache().removeCacheEntry(tenant, result.getHardwareId());
	getDeviceByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getHardwareId());
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
	getDeviceCache().removeCacheEntry(tenant, device.getHardwareId());
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
     * com.sitewhere.device.DeviceManagementDecorator#updateDeviceAssignmentMetadata
     * (java.util.UUID, java.util.Map)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(UUID id, Map<String, String> metadata)
	    throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.updateDeviceAssignmentMetadata(id, metadata);
	getDeviceAssignmentCache().setCacheEntry(tenant, result.getToken(), result);
	getDeviceAssignmentByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateDeviceAssignmentStatus(
     * java.util.UUID, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(UUID id, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IDeviceAssignment result = super.updateDeviceAssignmentStatus(id, status);
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

    public ICacheProvider<String, ISite> getSiteCache() {
	return siteCache;
    }

    public void setSiteCache(ICacheProvider<String, ISite> siteCache) {
	this.siteCache = siteCache;
    }

    public ICacheProvider<UUID, ISite> getSiteByIdCache() {
	return siteByIdCache;
    }

    public void setSiteByIdCache(ICacheProvider<UUID, ISite> siteByIdCache) {
	this.siteByIdCache = siteByIdCache;
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