/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.grpc;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.asset.spi.modules.IAssetModule;
import com.sitewhere.asset.spi.modules.IAssetModuleManager;
import com.sitewhere.grpc.model.AssetModel.GAssetCategorySearchResults;
import com.sitewhere.grpc.model.AssetModel.GAssetSearchResults;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.AssetModelConverter;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.grpc.service.GCreateAssetCategoryRequest;
import com.sitewhere.grpc.service.GCreateAssetCategoryResponse;
import com.sitewhere.grpc.service.GCreateHardwareAssetRequest;
import com.sitewhere.grpc.service.GCreateHardwareAssetResponse;
import com.sitewhere.grpc.service.GCreateLocationAssetRequest;
import com.sitewhere.grpc.service.GCreateLocationAssetResponse;
import com.sitewhere.grpc.service.GCreatePersonAssetRequest;
import com.sitewhere.grpc.service.GCreatePersonAssetResponse;
import com.sitewhere.grpc.service.GDeleteAssetCategoryRequest;
import com.sitewhere.grpc.service.GDeleteAssetCategoryResponse;
import com.sitewhere.grpc.service.GDeleteAssetRequest;
import com.sitewhere.grpc.service.GDeleteAssetResponse;
import com.sitewhere.grpc.service.GGetAssetByIdRequest;
import com.sitewhere.grpc.service.GGetAssetByIdResponse;
import com.sitewhere.grpc.service.GGetAssetCategoryByIdRequest;
import com.sitewhere.grpc.service.GGetAssetCategoryByIdResponse;
import com.sitewhere.grpc.service.GGetAssetModuleAssetRequest;
import com.sitewhere.grpc.service.GGetAssetModuleAssetResponse;
import com.sitewhere.grpc.service.GGetAssetModuleAssetsByCriteriaRequest;
import com.sitewhere.grpc.service.GGetAssetModuleAssetsByCriteriaResponse;
import com.sitewhere.grpc.service.GGetAssetModuleDescriptorByModuleIdRequest;
import com.sitewhere.grpc.service.GGetAssetModuleDescriptorByModuleIdResponse;
import com.sitewhere.grpc.service.GListAssetCategoriesRequest;
import com.sitewhere.grpc.service.GListAssetCategoriesResponse;
import com.sitewhere.grpc.service.GListAssetModuleDescriptorsRequest;
import com.sitewhere.grpc.service.GListAssetModuleDescriptorsResponse;
import com.sitewhere.grpc.service.GListAssetsRequest;
import com.sitewhere.grpc.service.GListAssetsResponse;
import com.sitewhere.grpc.service.GUpdateAssetCategoryRequest;
import com.sitewhere.grpc.service.GUpdateAssetCategoryResponse;
import com.sitewhere.grpc.service.GUpdateHardwareAssetRequest;
import com.sitewhere.grpc.service.GUpdateHardwareAssetResponse;
import com.sitewhere.grpc.service.GUpdateLocationAssetRequest;
import com.sitewhere.grpc.service.GUpdateLocationAssetResponse;
import com.sitewhere.grpc.service.GUpdatePersonAssetRequest;
import com.sitewhere.grpc.service.GUpdatePersonAssetResponse;
import com.sitewhere.rest.model.asset.AssetModuleDescriptor;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleDescriptor;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for asset management GRPC requests.
 * 
 * @author Derek
 */
public class AssetManagementImpl extends AssetManagementGrpc.AssetManagementImplBase {

    /** Asset management persistence */
    private IAssetManagement assetManagement;

    /** Asset module manager */
    private IAssetModuleManager assetModuleManager;

    public AssetManagementImpl(IAssetManagement assetManagement, IAssetModuleManager assetModuleManager) {
	this.assetManagement = assetManagement;
	this.assetModuleManager = assetModuleManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createAssetCategory(com.sitewhere.grpc.service. GCreateAssetCategoryRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAssetCategory(GCreateAssetCategoryRequest request,
	    StreamObserver<GCreateAssetCategoryResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_CREATE_ASSET_CATEGORY);
	    IAssetCategoryCreateRequest apiRequest = AssetModelConverter
		    .asApiAssetCategoryCreateRequest(request.getRequest());
	    IAssetCategory apiResult = getAssetManagement().createAssetCategory(apiRequest);
	    GCreateAssetCategoryResponse.Builder response = GCreateAssetCategoryResponse.newBuilder();
	    response.setAssetCategory(AssetModelConverter.asGrpcAssetCategory(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_CREATE_ASSET_CATEGORY, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetCategoryById(com.sitewhere.grpc.service.
     * GGetAssetCategoryByIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetCategoryById(GGetAssetCategoryByIdRequest request,
	    StreamObserver<GGetAssetCategoryByIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID);
	    IAssetCategory apiResult = getAssetManagement().getAssetCategory(request.getId());
	    GGetAssetCategoryByIdResponse.Builder response = GGetAssetCategoryByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssetCategory(AssetModelConverter.asGrpcAssetCategory(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateAssetCategory(com.sitewhere.grpc.service. GUpdateAssetCategoryRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAssetCategory(GUpdateAssetCategoryRequest request,
	    StreamObserver<GUpdateAssetCategoryResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_UPDATE_ASSET_CATEGORY);
	    IAssetCategoryCreateRequest apiRequest = AssetModelConverter
		    .asApiAssetCategoryCreateRequest(request.getRequest());
	    IAssetCategory apiResult = getAssetManagement().updateAssetCategory(request.getId(), apiRequest);
	    GUpdateAssetCategoryResponse.Builder response = GUpdateAssetCategoryResponse.newBuilder();
	    response.setAssetCategory(AssetModelConverter.asGrpcAssetCategory(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_UPDATE_ASSET_CATEGORY, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssetCategories(com.sitewhere.grpc.service. GListAssetCategoriesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssetCategories(GListAssetCategoriesRequest request,
	    StreamObserver<GListAssetCategoriesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_LIST_ASSET_CATEGORIES);
	    ISearchResults<IAssetCategory> apiResult = getAssetManagement()
		    .listAssetCategories(CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListAssetCategoriesResponse.Builder response = GListAssetCategoriesResponse.newBuilder();
	    GAssetCategorySearchResults.Builder results = GAssetCategorySearchResults.newBuilder();
	    for (IAssetCategory api : apiResult.getResults()) {
		results.addCategories(AssetModelConverter.asGrpcAssetCategory(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_LIST_ASSET_CATEGORIES, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * deleteAssetCategory(com.sitewhere.grpc.service. GDeleteAssetCategoryRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAssetCategory(GDeleteAssetCategoryRequest request,
	    StreamObserver<GDeleteAssetCategoryResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY);
	    IAssetCategory apiResult = getAssetManagement().deleteAssetCategory(request.getId());
	    GDeleteAssetCategoryResponse.Builder response = GDeleteAssetCategoryResponse.newBuilder();
	    response.setAssetCategory(AssetModelConverter.asGrpcAssetCategory(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createHardwareAsset(com.sitewhere.grpc.service. GCreateHardwareAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createHardwareAsset(GCreateHardwareAssetRequest request,
	    StreamObserver<GCreateHardwareAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_CREATE_HARDWARE_ASSET);
	    IHardwareAssetCreateRequest apiRequest = AssetModelConverter
		    .asApiHardwareAssetCreateRequest(request.getRequest());
	    IHardwareAsset apiResult = getAssetManagement().createHardwareAsset(request.getCategoryId(), apiRequest);
	    GCreateHardwareAssetResponse.Builder response = GCreateHardwareAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcHardwareAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_CREATE_HARDWARE_ASSET, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateHardwareAsset(com.sitewhere.grpc.service. GUpdateHardwareAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateHardwareAsset(GUpdateHardwareAssetRequest request,
	    StreamObserver<GUpdateHardwareAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET);
	    IHardwareAssetCreateRequest apiRequest = AssetModelConverter
		    .asApiHardwareAssetCreateRequest(request.getRequest());
	    IHardwareAsset apiResult = getAssetManagement().updateHardwareAsset(request.getCategoryId(),
		    request.getAssetId(), apiRequest);
	    GUpdateHardwareAssetResponse.Builder response = GUpdateHardwareAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcHardwareAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createPersonAsset(com.sitewhere.grpc.service.GCreatePersonAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createPersonAsset(GCreatePersonAssetRequest request,
	    StreamObserver<GCreatePersonAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET);
	    IPersonAssetCreateRequest apiRequest = AssetModelConverter
		    .asApiPersonAssetCreateRequest(request.getRequest());
	    IPersonAsset apiResult = getAssetManagement().createPersonAsset(request.getCategoryId(), apiRequest);
	    GCreatePersonAssetResponse.Builder response = GCreatePersonAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcPersonAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updatePersonAsset(com.sitewhere.grpc.service.GUpdatePersonAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updatePersonAsset(GUpdatePersonAssetRequest request,
	    StreamObserver<GUpdatePersonAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_UPDATE_PERSON_ASSET);
	    IPersonAssetCreateRequest apiRequest = AssetModelConverter
		    .asApiPersonAssetCreateRequest(request.getRequest());
	    IPersonAsset apiResult = getAssetManagement().updatePersonAsset(request.getCategoryId(),
		    request.getAssetId(), apiRequest);
	    GUpdatePersonAssetResponse.Builder response = GUpdatePersonAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcPersonAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_UPDATE_PERSON_ASSET, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * createLocationAsset(com.sitewhere.grpc.service. GCreateLocationAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createLocationAsset(GCreateLocationAssetRequest request,
	    StreamObserver<GCreateLocationAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET);
	    ILocationAssetCreateRequest apiRequest = AssetModelConverter
		    .asApiLocationAssetCreateRequest(request.getRequest());
	    ILocationAsset apiResult = getAssetManagement().createLocationAsset(request.getCategoryId(), apiRequest);
	    GCreateLocationAssetResponse.Builder response = GCreateLocationAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcLocationAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * updateLocationAsset(com.sitewhere.grpc.service. GUpdateLocationAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateLocationAsset(GUpdateLocationAssetRequest request,
	    StreamObserver<GUpdateLocationAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_UPDATE_LOCATION_ASSET);
	    ILocationAssetCreateRequest apiRequest = AssetModelConverter
		    .asApiLocationAssetCreateRequest(request.getRequest());
	    ILocationAsset apiResult = getAssetManagement().updateLocationAsset(request.getCategoryId(),
		    request.getAssetId(), apiRequest);
	    GUpdateLocationAssetResponse.Builder response = GUpdateLocationAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcLocationAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_UPDATE_LOCATION_ASSET, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_BY_ID);
	    IAsset apiResult = getAssetManagement().getAsset(request.getCategoryId(), request.getAssetId());
	    GGetAssetByIdResponse.Builder response = GGetAssetByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setAsset(AssetModelConverter.asGrpcGenericAsset(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_GET_ASSET_BY_ID, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_DELETE_ASSET);
	    IAsset apiResult = getAssetManagement().deleteAsset(request.getCategoryId(), request.getAssetId());
	    GDeleteAssetResponse.Builder response = GDeleteAssetResponse.newBuilder();
	    response.setAsset(AssetModelConverter.asGrpcGenericAsset(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_DELETE_ASSET, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_LIST_ASSETS);
	    ISearchResults<IAsset> apiResult = getAssetManagement().listAssets(request.getCategoryId(),
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListAssetsResponse.Builder response = GListAssetsResponse.newBuilder();
	    GAssetSearchResults.Builder results = GAssetSearchResults.newBuilder();
	    for (IAsset api : apiResult.getResults()) {
		results.addAssets(AssetModelConverter.asGrpcGenericAsset(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_LIST_ASSETS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * listAssetModuleDescriptors(com.sitewhere.grpc.service.
     * GListAssetModuleDescriptorsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAssetModuleDescriptors(GListAssetModuleDescriptorsRequest request,
	    StreamObserver<GListAssetModuleDescriptorsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_LIST_ASSET_MODULE_DESCRIPTORS);
	    List<IAssetModule<?>> apiResult = getAssetModuleManager().listModules();
	    List<IAssetModuleDescriptor> descriptors = new ArrayList<IAssetModuleDescriptor>();
	    for (IAssetModule<?> result : apiResult) {
		descriptors.add(getDescriptorFor(result));
	    }

	    GListAssetModuleDescriptorsResponse.Builder response = GListAssetModuleDescriptorsResponse.newBuilder();
	    for (IAssetModuleDescriptor api : descriptors) {
		response.addAssetModuleDescriptor(AssetModelConverter.asGrpcAssetModuleDescriptor(api));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_LIST_ASSET_MODULE_DESCRIPTORS, e);
	    responseObserver.onError(e);
	}
    }

    /**
     * Create a descriptor for the given asset module.
     * 
     * @param module
     * @return
     */
    protected IAssetModuleDescriptor getDescriptorFor(IAssetModule<?> module) {
	AssetModuleDescriptor descriptor = new AssetModuleDescriptor();
	descriptor.setId(module.getId());
	descriptor.setName(module.getName());
	descriptor.setAssetType(module.getAssetType());
	return descriptor;
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetModuleDescriptorByModuleId(com.sitewhere.grpc.service.
     * GGetAssetModuleDescriptorByModuleIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetModuleDescriptorByModuleId(GGetAssetModuleDescriptorByModuleIdRequest request,
	    StreamObserver<GGetAssetModuleDescriptorByModuleIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_DESCRIPTOR_BY_MODULE_ID);
	    IAssetModule<?> apiResult = getAssetModuleManager().getModule(request.getModuleId());
	    IAssetModuleDescriptor descriptor = getDescriptorFor(apiResult);
	    GGetAssetModuleDescriptorByModuleIdResponse.Builder response = GGetAssetModuleDescriptorByModuleIdResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setAssetModuleDescriptor(AssetModelConverter.asGrpcAssetModuleDescriptor(descriptor));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_DESCRIPTOR_BY_MODULE_ID, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetModuleAssetsByCriteria(com.sitewhere.grpc.service.
     * GGetAssetModuleAssetsByCriteriaRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetModuleAssetsByCriteria(GGetAssetModuleAssetsByCriteriaRequest request,
	    StreamObserver<GGetAssetModuleAssetsByCriteriaResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSETS_BY_CRITERIA);
	    List<? extends IAsset> apiResult = getAssetModuleManager().search(request.getModuleId(),
		    request.getCriteria());

	    GGetAssetModuleAssetsByCriteriaResponse.Builder response = GGetAssetModuleAssetsByCriteriaResponse
		    .newBuilder();
	    for (IAsset api : apiResult) {
		response.addAsset(AssetModelConverter.asGrpcGenericAsset(api));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSETS_BY_CRITERIA, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.AssetManagementGrpc.AssetManagementImplBase#
     * getAssetModuleAsset(com.sitewhere.grpc.service.GGetAssetModuleAssetRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetModuleAsset(GGetAssetModuleAssetRequest request,
	    StreamObserver<GGetAssetModuleAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSET);
	    IAsset apiResult = getAssetModuleManager().getAssetById(request.getModuleId(), request.getAssetId());
	    GGetAssetModuleAssetResponse.Builder response = GGetAssetModuleAssetResponse.newBuilder();
	    if (apiResult != null) {
		response.setAsset(AssetModelConverter.asGrpcGenericAsset(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSET, e);
	    responseObserver.onError(e);
	}
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }
}