/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.microservice.grpc.MultitenantGrpcServer;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;

/**
 * Hosts a GRPC server that handles device management requests.
 * 
 * @author Derek
 */
public class DeviceManagementGrpcServer extends MultitenantGrpcServer implements IDeviceManagementGrpcServer {

    public DeviceManagementGrpcServer(IDeviceManagementMicroservice microservice) {
	super(new DeviceManagementRouter(microservice), IGrpcSettings.DEFAULT_API_PORT,
		IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}