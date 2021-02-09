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

import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.grpc.asset.AssetModelConverter;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.model.AssetModel.GAssetSearchResults;
import com.sitewhere.grpc.model.AssetModel.GAssetTypeSearchResults;
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
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for asset management GRPC requests.
 */
public class AssetManagementImpl extends AssetManagementGrpc.AssetManagementImplBase implements IGrpcApiImplementation {

    /** Handle to asset management microservice */
    private IAssetManagementMicroservice microservice;

    /** Asset management */
    private IAssetManagement assetManagement;

    public AssetManagementImpl(IAssetManagementMicroservice microservice, IAssetManagement assetManagement) {
	this.microservice = microservice;
	this.assetManagement = assetManagement;
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAssetType(com.sitewhere.grpc.service.GCreateAssetTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAssetType(GCreateAssetTypeRequest request,
	    StreamObserver<GCreateAssetTypeResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getCreateAssetTypeMethod());
	    IAssetTypeCreateRequest apiRequest = AssetModelConverter.asApiAssetTypeCreateRequest(request.getRequest());
	    IAssetType apiResult = getAssetManagement().createAssetType(apiRequest);
	    GCreateAssetTypeResponse.Builder response = GCreateAssetTypeResponse.newBuilder();
	    response.setAssetType(AssetModelConverter.asGrpcAssetType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getCreateAssetTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getCreateAssetTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getUpdateAssetTypeMethod());
	    IAssetTypeCreateRequest apiRequest = AssetModelConverter.asApiAssetTypeCreateRequest(request.getRequest());
	    IAssetType apiResult = getAssetManagement()
		    .updateAssetType(CommonModelConverter.asApiUuid(request.getAssetTypeId()), apiRequest);
	    GUpdateAssetTypeResponse.Builder response = GUpdateAssetTypeResponse.newBuilder();
	    response.setAssetType(AssetModelConverter.asGrpcAssetType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getUpdateAssetTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getUpdateAssetTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getGetAssetTypeByIdMethod());
	    IAssetType apiResult = getAssetManagement()
		    .getAssetType(CommonModelConverter.asApiUuid(request.getAssetTypeId()));
	    GGetAssetTypeByIdResponse.Builder response = GGetAssetTypeByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssetType(AssetModelConverter.asGrpcAssetType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getGetAssetTypeByIdMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getGetAssetTypeByIdMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getGetAssetTypeByTokenMethod());
	    IAssetType apiResult = getAssetManagement().getAssetTypeByToken(request.getToken());
	    GGetAssetTypeByTokenResponse.Builder response = GGetAssetTypeByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssetType(AssetModelConverter.asGrpcAssetType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getGetAssetTypeByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getGetAssetTypeByTokenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getDeleteAssetTypeMethod());
	    IAssetType apiResult = getAssetManagement()
		    .deleteAssetType(CommonModelConverter.asApiUuid(request.getAssetTypeId()));
	    GDeleteAssetTypeResponse.Builder response = GDeleteAssetTypeResponse.newBuilder();
	    response.setAssetType(AssetModelConverter.asGrpcAssetType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getDeleteAssetTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getDeleteAssetTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getListAssetTypesMethod());
	    ISearchResults<? extends IAssetType> apiResult = getAssetManagement()
		    .listAssetTypes(AssetModelConverter.asApiAssetTypeSearchCriteria(request.getCriteria()));
	    GListAssetTypesResponse.Builder response = GListAssetTypesResponse.newBuilder();
	    GAssetTypeSearchResults.Builder results = GAssetTypeSearchResults.newBuilder();
	    for (IAssetType api : apiResult.getResults()) {
		results.addAssetTypes(AssetModelConverter.asGrpcAssetType(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getListAssetTypesMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getListAssetTypesMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAsset(com.sitewhere.grpc.service.GCreateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAsset(GCreateAssetRequest request, StreamObserver<GCreateAssetResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getCreateAssetMethod());
	    IAssetCreateRequest apiRequest = AssetModelConverter.asApiAssetCreateRequest(request.getRequest());
	    IAsset apiResult = getAssetManagement().createAsset(apiRequest);
	    GCreateAssetResponse.Builder response = GCreateAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getCreateAssetMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getCreateAssetMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAsset(com.sitewhere.grpc.service.GUpdateAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAsset(GUpdateAssetRequest request, StreamObserver<GUpdateAssetResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getUpdateAssetMethod());
	    IAssetCreateRequest apiRequest = AssetModelConverter.asApiAssetCreateRequest(request.getRequest());
	    IAsset apiResult = getAssetManagement().updateAsset(CommonModelConverter.asApiUuid(request.getAssetId()),
		    apiRequest);
	    GUpdateAssetResponse.Builder response = GUpdateAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getUpdateAssetMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getUpdateAssetMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getGetAssetByIdMethod());
	    IAsset apiResult = getAssetManagement().getAsset(CommonModelConverter.asApiUuid(request.getAssetId()));
	    GGetAssetByIdResponse.Builder response = GGetAssetByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setAsset(AssetModelConverter.asGrpcAsset(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getGetAssetByIdMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getGetAssetByIdMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getGetAssetByTokenMethod());
	    IAsset apiResult = getAssetManagement().getAssetByToken(request.getToken());
	    GGetAssetByTokenResponse.Builder response = GGetAssetByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setAsset(AssetModelConverter.asGrpcAsset(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getGetAssetByTokenMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getGetAssetByTokenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getDeleteAssetMethod());
	    IAsset apiResult = getAssetManagement().deleteAsset(CommonModelConverter.asApiUuid(request.getAssetId()));
	    GDeleteAssetResponse.Builder response = GDeleteAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getDeleteAssetMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getDeleteAssetMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, AssetManagementGrpc.getListAssetsMethod());
	    ISearchResults<? extends IAsset> apiResult = getAssetManagement()
		    .listAssets(AssetModelConverter.asApiAssetSearchCriteria(request.getCriteria()));
	    GListAssetsResponse.Builder response = GListAssetsResponse.newBuilder();
	    GAssetSearchResults.Builder results = GAssetSearchResults.newBuilder();
	    for (IAsset api : apiResult.getResults()) {
		results.addAssets(AssetModelConverter.asGrpcAsset(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(AssetManagementGrpc.getListAssetsMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(AssetManagementGrpc.getListAssetsMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IAssetManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IAssetManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}