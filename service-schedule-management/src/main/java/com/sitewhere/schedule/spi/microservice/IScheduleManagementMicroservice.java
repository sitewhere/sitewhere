/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.spi.microservice;

import com.sitewhere.schedule.spi.grpc.IScheduleManagementGrpcServer;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides schedule management functionality.
 */
public interface IScheduleManagementMicroservice
	extends IMultitenantMicroservice<MicroserviceIdentifier, IScheduleManagementTenantEngine> {

    /**
     * Get schedule management GRPC server.
     * 
     * @return
     */
    public IScheduleManagementGrpcServer getScheduleManagementGrpcServer();
}