/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.microservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc.MicroserviceManagementBlockingStub;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc.MicroserviceManagementStub;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Channel that allows for communication with a remote microservice mangement
 * GRPC server.
 * 
 * @author Derek
 */
public class MicroserviceManagementGrpcChannel
	extends GrpcChannel<MicroserviceManagementBlockingStub, MicroserviceManagementStub> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(MicroserviceManagementGrpcChannel.class);

    public MicroserviceManagementGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	super(tracerProvider, host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public MicroserviceManagementBlockingStub createBlockingStub() {
	return MicroserviceManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public MicroserviceManagementStub createAsyncStub() {
	return MicroserviceManagementGrpc.newStub(getChannel());
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