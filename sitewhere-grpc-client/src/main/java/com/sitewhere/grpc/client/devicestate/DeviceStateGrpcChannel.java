/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.devicestate;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateBlockingStub;
import com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateStub;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Channel that allows for communication with a remote device state GRPC server.
 * 
 * @author Derek
 */
public class DeviceStateGrpcChannel extends MultitenantGrpcChannel<DeviceStateBlockingStub, DeviceStateStub> {

    public DeviceStateGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	super(tracerProvider, host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public DeviceStateBlockingStub createBlockingStub() {
	return DeviceStateGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public DeviceStateStub createAsyncStub() {
	return DeviceStateGrpc.newStub(getChannel());
    }
}