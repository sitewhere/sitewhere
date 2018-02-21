/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.TenantManagementGrpc;
import com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementBlockingStub;
import com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementStub;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Channel that allows for communication with a remote tenant management GRPC
 * server.
 * 
 * @author Derek
 */
public class TenantManagementGrpcChannel extends GrpcChannel<TenantManagementBlockingStub, TenantManagementStub> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TenantManagementGrpcChannel.class);

    public TenantManagementGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	super(tracerProvider, host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public TenantManagementBlockingStub createBlockingStub() {
	return TenantManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public TenantManagementStub createAsyncStub() {
	return TenantManagementGrpc.newStub(getChannel());
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