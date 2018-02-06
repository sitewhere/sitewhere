/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.ApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiDemux;
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

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

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
	return new CachedDeviceManagementApiChannel(this, getMicroservice(), host);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}