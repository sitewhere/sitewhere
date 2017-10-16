/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementBlockingStub;
import com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementStub;

/**
 * Channel that allows for communication with a remote device management GRPC
 * server.
 * 
 * @author Derek
 */
public class DeviceManagementGrpcChannel
	extends MultitenantGrpcChannel<DeviceManagementBlockingStub, DeviceManagementStub> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public DeviceManagementGrpcChannel(String host, int port) {
	super(host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public DeviceManagementBlockingStub createBlockingStub() {
	return DeviceManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public DeviceManagementStub createAsyncStub() {
	return DeviceManagementGrpc.newStub(getChannel());
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