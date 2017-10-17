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