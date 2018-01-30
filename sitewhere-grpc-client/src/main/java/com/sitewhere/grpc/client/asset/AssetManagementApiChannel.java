/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.AssetModel.GAnyAsset;
import com.sitewhere.grpc.model.AssetModel.GAssetModuleDescriptor;
import com.sitewhere.grpc.model.converter.AssetModelConverter;
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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetModuleDescriptor;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere asset management APIs on top of a
 * {@link AssetManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class AssetManagementApiChannel extends ApiChannel<AssetManagementGrpcChannel>
	implements IAssetManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public AssetManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new AssetManagementGrpcChannel(tracerProvider, host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#createAssetCategory(com.
     * sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_CREATE_ASSET_CATEGORY);
	    GCreateAssetCategoryRequest.Builder grequest = GCreateAssetCategoryRequest.newBuilder();
	    grequest.setRequest(AssetModelConverter.asGrpcAssetCategoryCreateRequest(request));
	    GCreateAssetCategoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createAssetCategory(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_CREATE_ASSET_CATEGORY, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_CREATE_ASSET_CATEGORY, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory getAssetCategory(String categoryId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID);
	    GGetAssetCategoryByIdRequest.Builder grequest = GGetAssetCategoryByIdRequest.newBuilder();
	    grequest.setId(categoryId);
	    GGetAssetCategoryByIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetCategoryById(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAssetCategory(java.lang.
     * String, com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_UPDATE_ASSET_CATEGORY);
	    GUpdateAssetCategoryRequest.Builder grequest = GUpdateAssetCategoryRequest.newBuilder();
	    grequest.setId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcAssetCategoryCreateRequest(request));
	    GUpdateAssetCategoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateAssetCategory(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_UPDATE_ASSET_CATEGORY, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_UPDATE_ASSET_CATEGORY, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#listAssetCategories(com.
     * sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAssetCategory> listAssetCategories(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_LIST_ASSET_CATEGORIES);
	    GListAssetCategoriesRequest.Builder grequest = GListAssetCategoriesRequest.newBuilder();
	    grequest.setCriteria(AssetModelConverter.asApiAssetCategorySearchCriteria(criteria));
	    GListAssetCategoriesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listAssetCategories(grequest.build());
	    ISearchResults<IAssetCategory> results = AssetModelConverter
		    .asApiAssetCategorySearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_LIST_ASSET_CATEGORIES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_LIST_ASSET_CATEGORIES, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY);
	    GDeleteAssetCategoryRequest.Builder grequest = GDeleteAssetCategoryRequest.newBuilder();
	    grequest.setId(categoryId);
	    GDeleteAssetCategoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteAssetCategory(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#createPersonAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET);
	    GCreatePersonAssetRequest.Builder grequest = GCreatePersonAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcPersonAssetCreateRequest(request));
	    GCreatePersonAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createPersonAsset(grequest.build());
	    IPersonAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiPersonAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#updatePersonAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_UPDATE_PERSON_ASSET);
	    GUpdatePersonAssetRequest.Builder grequest = GUpdatePersonAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    grequest.setRequest(AssetModelConverter.asGrpcPersonAssetCreateRequest(request));
	    GUpdatePersonAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updatePersonAsset(grequest.build());
	    IPersonAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiPersonAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_UPDATE_PERSON_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_UPDATE_PERSON_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#createHardwareAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET);
	    GCreateHardwareAssetRequest.Builder grequest = GCreateHardwareAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcHardwareAssetCreateRequest(request));
	    GCreateHardwareAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createHardwareAsset(grequest.build());
	    IHardwareAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiHardwareAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#updateHardwareAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset updateHardwareAsset(String categoryId, String assetId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET);
	    GUpdateHardwareAssetRequest.Builder grequest = GUpdateHardwareAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    grequest.setRequest(AssetModelConverter.asGrpcHardwareAssetCreateRequest(request));
	    GUpdateHardwareAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateHardwareAsset(grequest.build());
	    IHardwareAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiHardwareAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#createLocationAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET);
	    GCreateLocationAssetRequest.Builder grequest = GCreateLocationAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcLocationAssetCreateRequest(request));
	    GCreateLocationAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createLocationAsset(grequest.build());
	    ILocationAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiLocationAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#updateLocationAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset updateLocationAsset(String categoryId, String assetId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_UPDATE_LOCATION_ASSET);
	    GUpdateLocationAssetRequest.Builder grequest = GUpdateLocationAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    grequest.setRequest(AssetModelConverter.asGrpcLocationAssetCreateRequest(request));
	    GUpdateLocationAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateLocationAsset(grequest.build());
	    ILocationAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiLocationAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_UPDATE_LOCATION_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_UPDATE_LOCATION_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_GET_ASSET_BY_ID);
	    GGetAssetByIdRequest.Builder grequest = GGetAssetByIdRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    GGetAssetByIdResponse gresponse = getGrpcChannel().getBlockingStub().getAssetById(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiGenericAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_GET_ASSET_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_GET_ASSET_BY_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_DELETE_ASSET);
	    GDeleteAssetRequest.Builder grequest = GDeleteAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    GDeleteAssetResponse gresponse = getGrpcChannel().getBlockingStub().deleteAsset(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiGenericAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_DELETE_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_DELETE_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#listAssets(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(String categoryId, ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_LIST_ASSETS);
	    GListAssetsRequest.Builder grequest = GListAssetsRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setCriteria(AssetModelConverter.asGrpcAssetSearchCriteria(criteria));
	    GListAssetsResponse gresponse = getGrpcChannel().getBlockingStub().listAssets(grequest.build());
	    ISearchResults<IAsset> results = AssetModelConverter.asApiAssetSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_LIST_ASSETS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_LIST_ASSETS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#listAssetModuleDescriptors(com
     * .sitewhere.spi.asset.AssetType)
     */
    @Override
    public List<IAssetModuleDescriptor> listAssetModuleDescriptors(AssetType type) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_LIST_ASSET_MODULE_DESCRIPTORS);
	    GListAssetModuleDescriptorsRequest.Builder grequest = GListAssetModuleDescriptorsRequest.newBuilder();
	    if (type != null) {
		grequest.setAssetType(AssetModelConverter.asGrpcAssetType(type));
	    }
	    GListAssetModuleDescriptorsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listAssetModuleDescriptors(grequest.build());
	    List<IAssetModuleDescriptor> results = new ArrayList<IAssetModuleDescriptor>();
	    for (GAssetModuleDescriptor descriptor : gresponse.getAssetModuleDescriptorList()) {
		results.add(AssetModelConverter.asApiAssetModuleDescriptor(descriptor));
	    }
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_LIST_ASSET_MODULE_DESCRIPTORS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_LIST_ASSET_MODULE_DESCRIPTORS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#getAssetModuleDescriptor(java.
     * lang.String)
     */
    @Override
    public IAssetModuleDescriptor getAssetModuleDescriptor(String moduleId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_GET_ASSET_MODULE_DESCRIPTOR_BY_MODULE_ID);
	    GGetAssetModuleDescriptorByModuleIdRequest.Builder grequest = GGetAssetModuleDescriptorByModuleIdRequest
		    .newBuilder();
	    grequest.setModuleId(moduleId);
	    GGetAssetModuleDescriptorByModuleIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetModuleDescriptorByModuleId(grequest.build());
	    IAssetModuleDescriptor response = (gresponse.hasAssetModuleDescriptor())
		    ? AssetModelConverter.asApiAssetModuleDescriptor(gresponse.getAssetModuleDescriptor())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_DESCRIPTOR_BY_MODULE_ID,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    AssetManagementGrpc.METHOD_GET_ASSET_MODULE_DESCRIPTOR_BY_MODULE_ID, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#searchAssetModule(java.lang.
     * String, java.lang.String)
     */
    @Override
    public List<IAsset> searchAssetModule(String moduleId, String criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSETS_BY_CRITERIA);
	    GGetAssetModuleAssetsByCriteriaRequest.Builder grequest = GGetAssetModuleAssetsByCriteriaRequest
		    .newBuilder();
	    grequest.setModuleId(moduleId);
	    grequest.setCriteria(criteria);
	    GGetAssetModuleAssetsByCriteriaResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetModuleAssetsByCriteria(grequest.build());
	    List<IAsset> results = new ArrayList<IAsset>();
	    for (GAnyAsset asset : gresponse.getAssetList()) {
		results.add(AssetModelConverter.asApiGenericAsset(asset));
	    }
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSETS_BY_CRITERIA, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSETS_BY_CRITERIA,
		    t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#getAsset(com.sitewhere.spi.
     * asset.IAssetReference)
     */
    @Override
    public IAsset getAsset(IAssetReference reference) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSET);
	    GGetAssetModuleAssetRequest.Builder grequest = GGetAssetModuleAssetRequest.newBuilder();
	    grequest.setModuleId(reference.getModule());
	    grequest.setAssetId(reference.getId());
	    GGetAssetModuleAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetModuleAsset(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiGenericAsset(gresponse.getAsset())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_GET_ASSET_MODULE_ASSET, t);
	}
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