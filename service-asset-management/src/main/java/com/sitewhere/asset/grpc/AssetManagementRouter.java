/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.grpc.service.GCreateAssetRequest;
import com.sitewhere.grpc.service.GCreateAssetResponse;
import com.sitewhere.grpc.service.GCreateAssetTypeRequest;
import com.sitewhere.grpc.service.GCreateAssetTypeResponse;
import com.sitewhere.grpc.service.GDeleteAssetRequest;
import com.sitewhere.grpc.service.GDeleteAssetResponse;
import com.sitewhere.grpc.service.GDeleteAssetTypeRequest;
import com.sitewhere.grpc.service.GDeleteAssetTypeResponse;
import com.sitewhere.grpc.service.GGetAssetByIdRequest;
import com.sitewhere.grpc.service.GGetAssetByIdResponse;
import com.sitewhere.grpc.service.GGetAssetByTokenRequest;
import com.sitewhere.grpc.service.GGetAssetByTokenResponse;
import com.sitewhere.grpc.service.GGetAssetTypeByIdRequest;
import com.sitewhere.grpc.service.GGetAssetTypeByIdResponse;
import com.sitewhere.grpc.service.GGetAssetTypeByTokenRequest;
import com.sitewhere.grpc.service.GGetAssetTypeByTokenResponse;
import com.sitewhere.grpc.service.GListAssetTypesRequest;
import com.sitewhere.grpc.service.GListAssetTypesResponse;
import com.sitewhere.grpc.service.GListAssetsRequest;
import com.sitewhere.grpc.service.GListAssetsResponse;
import com.sitewhere.grpc.service.GUpdateAssetRequest;
import com.sitewhere.grpc.service.GUpdateAssetResponse;
import com.sitewhere.grpc.service.GUpdateAssetTypeRequest;
import com.sitewhere.grpc.service.GUpdateAssetTypeResponse;
import com.sitewhere.microservice.grpc.GrpcKeys;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class AssetManagementRouter extends AssetManagementGrpc.AssetManagementImplBase
	implements IGrpcRouter<AssetManagementGrpc.AssetManagementImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(AssetManagementRouter.class);

    /** Parent microservice */
    private IAssetManagementMicroservice microservice;

    public AssetManagementRouter(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public AssetManagementGrpc.AssetManagementImplBase getTenantImplementation(StreamObserver<?> observer) {
	String token = GrpcKeys.TENANT_CONTEXT_KEY.get();
	if (token == null) {
	    throw new RuntimeException("Tenant token not found in request.");
	}
	try {
	    IAssetManagementTenantEngine engine = getMicroservice().assureTenantEngineAvailable(token);
	    return engine.getAssetManagementImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAssetType(com.sitewhere.grpc.service.GCreateAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAssetType(GCreateAssetTypeRequest request,
	    StreamObserver<GCreateAssetTypeResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createAssetType(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAssetType(com.sitewhere.grpc.service.GUpdateAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAssetType(GUpdateAssetTypeRequest request,
	    StreamObserver<GUpdateAssetTypeResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateAssetType(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetTypeById(com.sitewhere.grpc.service.GGetAssetTypeByIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeById(GGetAssetTypeByIdRequest request,
	    StreamObserver<GGetAssetTypeByIdResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAssetTypeById(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetTypeByToken(com.sitewhere.grpc.service.GGetAssetTypeByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeByToken(GGetAssetTypeByTokenRequest request,
	    StreamObserver<GGetAssetTypeByTokenResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAssetTypeByToken(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAssetType(com.sitewhere.grpc.service.GDeleteAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAssetType(GDeleteAssetTypeRequest request,
	    StreamObserver<GDeleteAssetTypeResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteAssetType(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssetTypes(com.sitewhere.grpc.service.GListAssetTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssetTypes(GListAssetTypesRequest request,
	    StreamObserver<GListAssetTypesResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAssetTypes(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAsset(com.sitewhere.grpc.service.GCreateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAsset(GCreateAssetRequest request, StreamObserver<GCreateAssetResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createAsset(request, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetById(com.sitewhere.grpc.service.GGetAssetByIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetById(GGetAssetByIdRequest request, StreamObserver<GGetAssetByIdResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAssetById(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetByToken(com.sitewhere.grpc.service.GGetAssetByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetByToken(GGetAssetByTokenRequest request,
	    StreamObserver<GGetAssetByTokenResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAssetByToken(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAsset(com.sitewhere.grpc.service.GUpdateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAsset(GUpdateAssetRequest request, StreamObserver<GUpdateAssetResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateAsset(request, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAsset(com.sitewhere.grpc.service.GDeleteAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAsset(GDeleteAssetRequest request, StreamObserver<GDeleteAssetResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteAsset(request, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssets(com.sitewhere.grpc.service.GListAssetsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssets(GListAssetsRequest request, StreamObserver<GListAssetsResponse> responseObserver) {
	AssetManagementGrpc.AssetManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAssets(request, responseObserver);
	}
    }

    public IAssetManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}