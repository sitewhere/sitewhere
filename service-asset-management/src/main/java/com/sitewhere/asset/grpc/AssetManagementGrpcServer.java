/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.grpc;

import com.sitewhere.asset.spi.grpc.IAssetManagementGrpcServer;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.microservice.grpc.MultitenantGrpcServer;

/**
 * Hosts a GRPC server that handles asset management requests.
 * 
 * @author Derek
 */
public class AssetManagementGrpcServer extends MultitenantGrpcServer implements IAssetManagementGrpcServer {

    public AssetManagementGrpcServer(IAssetManagementMicroservice microservice) {
	super(microservice, new AssetManagementRouter(microservice));
    }
}