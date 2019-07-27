/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc.instance;

import com.sitewhere.instance.spi.instance.grpc.IInstanceManagementGrpcServer;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.spi.instance.IInstanceManagement;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;

/**
 * Hosts a GRPC server that handles instance management requests.
 */
public class InstanceManagementGrpcServer extends GrpcServer implements IInstanceManagementGrpcServer {

    public InstanceManagementGrpcServer(IInstanceManagementMicroservice<?> microservice,
	    IInstanceManagement instanceManagement) {
	super(new InstanceManagementImpl(microservice, instanceManagement), IGrpcSettings.DEFAULT_API_PORT,
		IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}
