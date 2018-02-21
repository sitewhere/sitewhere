/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc.DeviceEventManagementBlockingStub;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc.DeviceEventManagementStub;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Channel that allows for communication with a remote device event management
 * GRPC server.
 * 
 * @author Derek
 */
public class DeviceEventManagementGrpcChannel
	extends MultitenantGrpcChannel<DeviceEventManagementBlockingStub, DeviceEventManagementStub> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DeviceEventManagementApiChannel.class);

    public DeviceEventManagementGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	super(tracerProvider, host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public DeviceEventManagementBlockingStub createBlockingStub() {
	return DeviceEventManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public DeviceEventManagementStub createAsyncStub() {
	return DeviceEventManagementGrpc.newStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}