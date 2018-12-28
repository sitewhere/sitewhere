/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import com.sitewhere.grpc.client.MultitenantApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Demultiplexes device event management requests across one or more API
 * channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class DeviceEventManagementApiDemux extends MultitenantApiDemux<IDeviceEventManagementApiChannel<?>>
	implements IDeviceEventManagementApiDemux {

    public DeviceEventManagementApiDemux(boolean cacheEnabled) {
	super(cacheEnabled);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public IFunctionIdentifier getTargetIdentifier() {
	return MicroserviceIdentifier.EventManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiDemux#createApiChannel(java.lang.String,
     * boolean)
     */
    @Override
    public IDeviceEventManagementApiChannel<?> createApiChannel(String host, boolean enableCaching)
	    throws SiteWhereException {
	return new DeviceEventManagementApiChannel(this, host, getMicroservice().getInstanceSettings().getGrpcPort());
    }
}