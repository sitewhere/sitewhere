/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.management;

import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.grpc.IMicroserviceManagementGrpcServer;

/**
 * Hosts a GRPC server that handles microservice management requests.
 * 
 * @author Derek
 */
public class MicroserviceManagementGrpcServer extends GrpcServer implements IMicroserviceManagementGrpcServer {

    public MicroserviceManagementGrpcServer(IMicroservice<?> microservice) {
	super(new MicroserviceManagementImpl(microservice), IGrpcSettings.DEFAULT_MANAGEMENT_PORT,
		IGrpcSettings.DEFAULT_MANAGEMENT_HEALTH_PORT);
    }
}