/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default implementation of {@link IHBaseContext}.
 * 
 * @author Derek *
 */
public class HBaseContext implements IHBaseContext {

    /** Tenant */
    private ITenant tenant;

    /** Client implementation */
    private ISiteWhereHBaseClient client;

    /** Configured payload encoder */
    private IPayloadMarshaler payloadMarshaler;

    /** Device event buffer */
    private IDeviceEventBuffer deviceEventBuffer;

    /** Device id manager */
    private IDeviceIdManager deviceIdManager;

    /** Asset id manager */
    private IAssetIdManager assetIdManager;

    /** Schedule id manager */
    private IScheduleIdManager scheduleIdManager;

    /** User id manager */
    private IUserIdManager userIdManager;

    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }

    public ISiteWhereHBaseClient getClient() {
	return client;
    }

    public void setClient(ISiteWhereHBaseClient client) {
	this.client = client;
    }

    public IPayloadMarshaler getPayloadMarshaler() {
	return payloadMarshaler;
    }

    public void setPayloadMarshaler(IPayloadMarshaler payloadMarshaler) {
	this.payloadMarshaler = payloadMarshaler;
    }

    public IDeviceEventBuffer getDeviceEventBuffer() {
	return deviceEventBuffer;
    }

    public void setDeviceEventBuffer(IDeviceEventBuffer deviceEventBuffer) {
	this.deviceEventBuffer = deviceEventBuffer;
    }

    public IDeviceIdManager getDeviceIdManager() {
	return deviceIdManager;
    }

    public void setDeviceIdManager(IDeviceIdManager deviceIdManager) {
	this.deviceIdManager = deviceIdManager;
    }

    public IAssetIdManager getAssetIdManager() {
	return assetIdManager;
    }

    public void setAssetIdManager(IAssetIdManager assetIdManager) {
	this.assetIdManager = assetIdManager;
    }

    public IScheduleIdManager getScheduleIdManager() {
	return scheduleIdManager;
    }

    public void setScheduleIdManager(IScheduleIdManager scheduleIdManager) {
	this.scheduleIdManager = scheduleIdManager;
    }

    public IUserIdManager getUserIdManager() {
	return userIdManager;
    }

    public void setUserIdManager(IUserIdManager userIdManager) {
	this.userIdManager = userIdManager;
    }
}