/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.grpc;

import com.sitewhere.event.spi.grpc.IEventManagementGrpcServer;
import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

/**
 * Hosts a GRPC server that handles device event management requests.
 * 
 * @author Derek
 */
public class EventManagementGrpcServer extends GrpcServer implements IEventManagementGrpcServer {

    public EventManagementGrpcServer(IMicroservice microservice, IDeviceEventManagement deviceEventManagement) {
	super(microservice, new EventManagementImpl(deviceEventManagement));
    }
}