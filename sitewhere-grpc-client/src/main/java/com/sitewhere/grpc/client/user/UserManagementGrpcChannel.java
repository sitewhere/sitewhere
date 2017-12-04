/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.UserManagementGrpc;
import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementBlockingStub;
import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementStub;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Channel that allows for communication with a remote user mangement GRPC
 * server.
 * 
 * @author Derek
 */
public class UserManagementGrpcChannel extends GrpcChannel<UserManagementBlockingStub, UserManagementStub> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public UserManagementGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	super(tracerProvider, host, port);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}