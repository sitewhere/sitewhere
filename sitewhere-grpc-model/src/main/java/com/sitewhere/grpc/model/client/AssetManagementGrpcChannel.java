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

import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementBlockingStub;
import com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementStub;

/**
 * Channel that allows for communication with a remote asset management GRPC
 * server.
 * 
 * @author Derek
 */
public class AssetManagementGrpcChannel extends GrpcChannel<AssetManagementBlockingStub, AssetManagementStub> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public AssetManagementGrpcChannel(String host, int port) {
	super(host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public AssetManagementBlockingStub createBlockingStub() {
	return AssetManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public AssetManagementStub createAsyncStub() {
	return AssetManagementGrpc.newStub(getChannel());
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
