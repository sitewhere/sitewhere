/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc.user;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.user.grpc.IUserManagementGrpcServer;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;

/**
 * Hosts a GRPC server that handles user management requests.
 */
public class UserManagementGrpcServer extends GrpcServer implements IUserManagementGrpcServer {

    public UserManagementGrpcServer(IInstanceManagementMicroservice<?> microservice, IUserManagement userManagement) {
	super(new UserManagementImpl(microservice, userManagement), IGrpcSettings.USER_MANAGEMENT_API_PORT,
		IGrpcSettings.USER_MANAGEMENT_API_HEALTH_PORT);
    }
}