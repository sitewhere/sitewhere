/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import com.sitewhere.batch.spi.grpc.IBatchManagementGrpcServer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.microservice.grpc.MultitenantGrpcServer;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;

/**
 * Hosts a GRPC server that handles batch management requests.
 * 
 * @author Derek
 */
public class BatchManagementGrpcServer extends MultitenantGrpcServer implements IBatchManagementGrpcServer {

    public BatchManagementGrpcServer(IBatchOperationsMicroservice microservice) {
	super(new BatchManagementRouter(microservice), IGrpcSettings.DEFAULT_API_PORT,
		IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}