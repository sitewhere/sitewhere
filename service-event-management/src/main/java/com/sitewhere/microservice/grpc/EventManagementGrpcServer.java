/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import com.sitewhere.event.spi.grpc.IEventManagementGrpcServer;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;

/**
 * Hosts a GRPC server that handles device event management requests.
 */
public class EventManagementGrpcServer extends MultitenantGrpcServer implements IEventManagementGrpcServer {

    public EventManagementGrpcServer(IEventManagementMicroservice microservice) {
	super(new EventManagementRouter(microservice), IGrpcSettings.DEFAULT_API_PORT,
		IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}