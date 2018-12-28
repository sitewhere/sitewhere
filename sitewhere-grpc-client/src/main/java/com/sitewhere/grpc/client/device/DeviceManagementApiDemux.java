/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import com.sitewhere.grpc.client.MultitenantApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Demultiplexes device management requests across one or more API channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class DeviceManagementApiDemux extends MultitenantApiDemux<IDeviceManagementApiChannel<?>>
	implements IDeviceManagementApiDemux {

    public DeviceManagementApiDemux(boolean cacheEnabled) {
	super(cacheEnabled);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public IFunctionIdentifier getTargetIdentifier() {
	return MicroserviceIdentifier.DeviceManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiDemux#createApiChannel(java.lang.String,
     * boolean)
     */
    @Override
    public IDeviceManagementApiChannel<?> createApiChannel(String host, boolean cacheEnabled)
	    throws SiteWhereException {
	CachedDeviceManagementApiChannel.CacheSettings settings = new CachedDeviceManagementApiChannel.CacheSettings();
	if (!cacheEnabled) {
	    settings.getAreaConfiguration().setEnabled(false);
	    settings.getDeviceTypeConfiguration().setEnabled(false);
	    settings.getDeviceConfiguration().setEnabled(false);
	    settings.getDeviceAssignmentConfiguration().setEnabled(false);
	}
	return new CachedDeviceManagementApiChannel(this, host, getMicroservice().getInstanceSettings().getGrpcPort(),
		settings);
    }
}