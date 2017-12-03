/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client.device;

import com.sitewhere.grpc.model.client.ApiDemux;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;

/**
 * Demultiplexes device management requests across one or more API channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class DeviceManagementApiDemux extends ApiDemux<IDeviceManagementApiChannel>
	implements IDeviceManagementApiDemux {

    public DeviceManagementApiDemux(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public String getTargetIdentifier() {
	return IMicroserviceIdentifiers.DEVICE_MANAGEMENT;
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#createApiChannel(java.lang.String)
     */
    @Override
    public IDeviceManagementApiChannel createApiChannel(String host) throws SiteWhereException {
	return new DeviceManagementApiChannel(getMicroservice(), host);
    }
}