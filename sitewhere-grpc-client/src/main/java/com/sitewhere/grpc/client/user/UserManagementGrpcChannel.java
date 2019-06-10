/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.UserManagementGrpc;
import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementBlockingStub;
import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementStub;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Channel that allows for communication with a remote user mangement GRPC
 * server.
 * 
 * @author Derek
 */
public class UserManagementGrpcChannel extends GrpcChannel<UserManagementBlockingStub, UserManagementStub> {

    public UserManagementGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public UserManagementBlockingStub createBlockingStub() {
	return UserManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public UserManagementStub createAsyncStub() {
	return UserManagementGrpc.newStub(getChannel());
    }
}