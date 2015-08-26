/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import com.sitewhere.hbase.device.IDeviceEventBuffer;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.user.ITenant;

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

	/** Configured cache provider */
	private IDeviceManagementCacheProvider cacheProvider;

	/** Configured payload encoder */
	private IPayloadMarshaler payloadMarshaler;

	/** Device event buffer */
	private IDeviceEventBuffer deviceEventBuffer;

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

	public IDeviceManagementCacheProvider getCacheProvider() {
		return cacheProvider;
	}

	public void setCacheProvider(IDeviceManagementCacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
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
}