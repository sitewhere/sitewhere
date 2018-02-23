/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.grpc;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
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
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class AssetManagementRouter extends AssetManagementGrpc.AssetManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(AssetManagementRouter.class);

    /** Parent microservice */
    private IAssetManagementMicroservice microservice;

    public AssetManagementRouter(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /**
     * Based on token passed via GRPC header, look up service implementation running
     * in tenant engine.
     * 
     * @return
     */
    protected AssetManagementGrpc.AssetManagementImplBase getTenantImplementation() {
	String tenantId = TenantTokenServerInterceptor.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in asset management request.");
	}
	try {
	    IAssetManagementTenantEngine engine = getMicroservice()
		    .getTenantEngineByTenantId(UUID.fromString(tenantId));
	    if (engine != null) {
		UserContextManager.setCurrentTenant(engine.getTenant());
		return engine.getAssetManagementImpl();
	    }
	    throw new RuntimeException("Tenant engine not found.");
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Error locating tenant engine.", e);
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
	getTenantImplementation().createAssetType(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAssetType(com.sitewhere.grpc.service.GUpdateAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAssetType(GUpdateAssetTypeRequest request,
	    StreamObserver<GUpdateAssetTypeResponse> responseObserver) {
	getTenantImplementation().updateAssetType(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetTypeById(com.sitewhere.grpc.service.GGetAssetTypeByIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeById(GGetAssetTypeByIdRequest request,
	    StreamObserver<GGetAssetTypeByIdResponse> responseObserver) {
	getTenantImplementation().getAssetTypeById(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetTypeByToken(com.sitewhere.grpc.service.GGetAssetTypeByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeByToken(GGetAssetTypeByTokenRequest request,
	    StreamObserver<GGetAssetTypeByTokenResponse> responseObserver) {
	getTenantImplementation().getAssetTypeByToken(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAssetType(com.sitewhere.grpc.service.GDeleteAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAssetType(GDeleteAssetTypeRequest request,
	    StreamObserver<GDeleteAssetTypeResponse> responseObserver) {
	getTenantImplementation().deleteAssetType(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssetTypes(com.sitewhere.grpc.service.GListAssetTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssetTypes(GListAssetTypesRequest request,
	    StreamObserver<GListAssetTypesResponse> responseObserver) {
	getTenantImplementation().listAssetTypes(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAsset(com.sitewhere.grpc.service.GCreateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAsset(GCreateAssetRequest request, StreamObserver<GCreateAssetResponse> responseObserver) {
	getTenantImplementation().createAsset(request, responseObserver);
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
	getTenantImplementation().getAssetById(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetByToken(com.sitewhere.grpc.service.GGetAssetByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetByToken(GGetAssetByTokenRequest request,
	    StreamObserver<GGetAssetByTokenResponse> responseObserver) {
	getTenantImplementation().getAssetByToken(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAsset(com.sitewhere.grpc.service.GUpdateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAsset(GUpdateAssetRequest request, StreamObserver<GUpdateAssetResponse> responseObserver) {
	getTenantImplementation().updateAsset(request, responseObserver);
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
	getTenantImplementation().deleteAsset(request, responseObserver);
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
	getTenantImplementation().listAssets(request, responseObserver);
    }

    public IAssetManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}