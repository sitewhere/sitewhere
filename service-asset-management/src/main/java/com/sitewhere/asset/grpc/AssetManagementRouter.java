/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.asset.grpc;

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
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class AssetManagementRouter extends AssetManagementGrpc.AssetManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(AssetManagementRouter.class);

    /** Parent microservice */
    private IAssetManagementMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<IAssetManagementTenantEngine> grpcTenantEngineProvider;

    public AssetManagementRouter(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAssetType(com.sitewhere.grpc.service.GCreateAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAssetType(GCreateAssetTypeRequest request,
	    StreamObserver<GCreateAssetTypeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().createAssetType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAssetType(com.sitewhere.grpc.service.GUpdateAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAssetType(GUpdateAssetTypeRequest request,
	    StreamObserver<GUpdateAssetTypeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().updateAssetType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetTypeById(com.sitewhere.grpc.service.GGetAssetTypeByIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeById(GGetAssetTypeByIdRequest request,
	    StreamObserver<GGetAssetTypeByIdResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().getAssetTypeById(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetTypeByToken(com.sitewhere.grpc.service.GGetAssetTypeByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeByToken(GGetAssetTypeByTokenRequest request,
	    StreamObserver<GGetAssetTypeByTokenResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().getAssetTypeByToken(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAssetType(com.sitewhere.grpc.service.GDeleteAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAssetType(GDeleteAssetTypeRequest request,
	    StreamObserver<GDeleteAssetTypeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().deleteAssetType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssetTypes(com.sitewhere.grpc.service.GListAssetTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssetTypes(GListAssetTypesRequest request,
	    StreamObserver<GListAssetTypesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().listAssetTypes(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAsset(com.sitewhere.grpc.service.GCreateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAsset(GCreateAssetRequest request, StreamObserver<GCreateAssetResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().createAsset(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().getAssetById(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetByToken(com.sitewhere.grpc.service.GGetAssetByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetByToken(GGetAssetByTokenRequest request,
	    StreamObserver<GGetAssetByTokenResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().getAssetByToken(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAsset(com.sitewhere.grpc.service.GUpdateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAsset(GUpdateAssetRequest request, StreamObserver<GUpdateAssetResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().updateAsset(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().deleteAsset(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IAssetManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IAssetManagementTenantEngine tenantEngine) {
		tenantEngine.getAssetManagementImpl().listAssets(request, responseObserver);
	    }
	}, responseObserver);
    }

    protected IAssetManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected GrpcTenantEngineProvider<IAssetManagementTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}