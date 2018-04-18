/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.grpc;

import com.sitewhere.microservice.grpc.MultitenantGrpcServer;
import com.sitewhere.schedule.spi.grpc.IScheduleManagementGrpcServer;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;

/**
 * Hosts a GRPC server that handles schedule management requests.
 * 
 * @author Derek
 */
public class ScheduleManagementGrpcServer extends MultitenantGrpcServer implements IScheduleManagementGrpcServer {

    public ScheduleManagementGrpcServer(IScheduleManagementMicroservice microservice) {
	super(new ScheduleManagementRouter(microservice), microservice.getInstanceSettings().getGrpcPort());
    }
}