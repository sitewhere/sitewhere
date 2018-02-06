/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.service.ScheduleManagementGrpc;
import com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementBlockingStub;
import com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementStub;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Channel that allows for communication with a remote schedule management GRPC
 * server.
 * 
 * @author Derek
 */
public class ScheduleManagementGrpcChannel
	extends MultitenantGrpcChannel<ScheduleManagementBlockingStub, ScheduleManagementStub> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public ScheduleManagementGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	super(tracerProvider, host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public ScheduleManagementBlockingStub createBlockingStub() {
	return ScheduleManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public ScheduleManagementStub createAsyncStub() {
	return ScheduleManagementGrpc.newStub(getChannel());
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