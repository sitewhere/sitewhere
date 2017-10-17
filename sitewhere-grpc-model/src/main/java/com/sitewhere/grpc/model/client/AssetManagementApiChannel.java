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

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.AssetModelConverter;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
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
import com.sitewhere.grpc.service.GListAssetCategoriesRequest;
import com.sitewhere.grpc.service.GListAssetCategoriesResponse;
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
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

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

    /** Asset management GRPC channel */
    private AssetManagementGrpcChannel grpcChannel;

    public AssetManagementApiChannel(AssetManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
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
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_CREATE_ASSET_CATEGORY);
	    GCreateAssetCategoryRequest.Builder grequest = GCreateAssetCategoryRequest.newBuilder();
	    grequest.setRequest(AssetModelConverter.asGrpcAssetCategoryCreateRequest(request));
	    GCreateAssetCategoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createAssetCategory(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory()) : null;
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
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID);
	    GGetAssetCategoryByIdRequest.Builder grequest = GGetAssetCategoryByIdRequest.newBuilder();
	    grequest.setId(categoryId);
	    GGetAssetCategoryByIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetCategoryById(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_GET_ASSET_CATEGORY_BY_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateAssetCategory(java.lang.
     * String, com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_UPDATE_ASSET_CATEGORY);
	    GUpdateAssetCategoryRequest.Builder grequest = GUpdateAssetCategoryRequest.newBuilder();
	    grequest.setId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcAssetCategoryCreateRequest(request));
	    GUpdateAssetCategoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateAssetCategory(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory()) : null;
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
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_LIST_ASSET_CATEGORIES);
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
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#deleteAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY);
	    GDeleteAssetCategoryRequest.Builder grequest = GDeleteAssetCategoryRequest.newBuilder();
	    grequest.setId(categoryId);
	    GDeleteAssetCategoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteAssetCategory(grequest.build());
	    IAssetCategory response = (gresponse.hasAssetCategory())
		    ? AssetModelConverter.asApiAssetCategory(gresponse.getAssetCategory()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_DELETE_ASSET_CATEGORY, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createPersonAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET);
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
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updatePersonAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_UPDATE_PERSON_ASSET);
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
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createHardwareAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET);
	    GCreateHardwareAssetRequest.Builder grequest = GCreateHardwareAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcHardwareAssetCreateRequest(request));
	    GCreateHardwareAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createHardwareAsset(grequest.build());
	    IHardwareAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiHardwareAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_CREATE_PERSON_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateHardwareAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset updateHardwareAsset(String categoryId, String assetId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET);
	    GUpdateHardwareAssetRequest.Builder grequest = GUpdateHardwareAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    grequest.setRequest(AssetModelConverter.asGrpcHardwareAssetCreateRequest(request));
	    GUpdateHardwareAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateHardwareAsset(grequest.build());
	    IHardwareAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiHardwareAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_UPDATE_HARDWARE_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createLocationAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET);
	    GCreateLocationAssetRequest.Builder grequest = GCreateLocationAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setRequest(AssetModelConverter.asGrpcLocationAssetCreateRequest(request));
	    GCreateLocationAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createLocationAsset(grequest.build());
	    ILocationAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiLocationAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.METHOD_CREATE_LOCATION_ASSET, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateLocationAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset updateLocationAsset(String categoryId, String assetId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_UPDATE_LOCATION_ASSET);
	    GUpdateLocationAssetRequest.Builder grequest = GUpdateLocationAssetRequest.newBuilder();
	    grequest.setCategoryId(categoryId);
	    grequest.setAssetId(assetId);
	    grequest.setRequest(AssetModelConverter.asGrpcLocationAssetCreateRequest(request));
	    GUpdateLocationAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateLocationAsset(grequest.build());
	    ILocationAsset response = (gresponse.hasAsset())
		    ? AssetModelConverter.asApiLocationAsset(gresponse.getAsset()) : null;
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
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_GET_ASSET_BY_ID);
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
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_DELETE_ASSET);
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
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(String categoryId, ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(AssetManagementGrpc.METHOD_LIST_ASSETS);
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public AssetManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(AssetManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
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