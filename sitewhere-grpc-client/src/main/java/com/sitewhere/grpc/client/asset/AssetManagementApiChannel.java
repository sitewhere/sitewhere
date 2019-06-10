/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;

/**
 * Supports SiteWhere asset management APIs on top of a
 * {@link AssetManagementGrpcChannel}.
 */
public class AssetManagementApiChannel extends MultitenantApiChannel<AssetManagementGrpcChannel>
	implements IAssetManagementApiChannel<AssetManagementGrpcChannel> {

    public AssetManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.AssetManagement, GrpcServiceIdentifier.AssetManagement,
		IGrpcSettings.DEFAULT_API_PORT);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier,
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier, int)
     */
    @Override
    public AssetManagementGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new AssetManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAssetType(com.sitewhere.spi.
     * asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getCreateAssetTypeMethod());
	    GCreateAssetTypeRequest.Builder grequest = GCreateAssetTypeRequest.newBuilder();
	    grequest.setRequest(AssetModelConverter.asGrpcAssetTypeCreateRequest(request));
	    GCreateAssetTypeResponse gresponse = getGrpcChannel().getBlockingStub().createAssetType(grequest.build());
	    IAssetType response = (gresponse.hasAssetType())
		    ? AssetModelConverter.asApiAssetType(gresponse.getAssetType())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getCreateAssetTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getCreateAssetTypeMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAssetType(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getUpdateAssetTypeMethod());
	    GUpdateAssetTypeRequest.Builder grequest = GUpdateAssetTypeRequest.newBuilder();
	    grequest.setAssetTypeId(CommonModelConverter.asGrpcUuid(assetTypeId));
	    grequest.setRequest(AssetModelConverter.asGrpcAssetTypeCreateRequest(request));
	    GUpdateAssetTypeResponse gresponse = getGrpcChannel().getBlockingStub().updateAssetType(grequest.build());
	    IAssetType response = (gresponse.hasAssetType())
		    ? AssetModelConverter.asApiAssetType(gresponse.getAssetType())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getUpdateAssetTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getUpdateAssetTypeMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetType(java.util.UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getGetAssetTypeByIdMethod());
	    GGetAssetTypeByIdRequest.Builder grequest = GGetAssetTypeByIdRequest.newBuilder();
	    grequest.setAssetTypeId(CommonModelConverter.asGrpcUuid(assetTypeId));
	    GGetAssetTypeByIdResponse gresponse = getGrpcChannel().getBlockingStub().getAssetTypeById(grequest.build());
	    IAssetType response = (gresponse.hasAssetType())
		    ? AssetModelConverter.asApiAssetType(gresponse.getAssetType())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getGetAssetTypeByIdMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getGetAssetTypeByIdMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getGetAssetTypeByTokenMethod());
	    GGetAssetTypeByTokenRequest.Builder grequest = GGetAssetTypeByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAssetTypeByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetTypeByToken(grequest.build());
	    IAssetType response = (gresponse.hasAssetType())
		    ? AssetModelConverter.asApiAssetType(gresponse.getAssetType())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getGetAssetTypeByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getGetAssetTypeByTokenMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetType(java.util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getDeleteAssetTypeMethod());
	    GDeleteAssetTypeRequest.Builder grequest = GDeleteAssetTypeRequest.newBuilder();
	    grequest.setAssetTypeId(CommonModelConverter.asGrpcUuid(assetTypeId));
	    GDeleteAssetTypeResponse gresponse = getGrpcChannel().getBlockingStub().deleteAssetType(grequest.build());
	    IAssetType response = (gresponse.hasAssetType())
		    ? AssetModelConverter.asApiAssetType(gresponse.getAssetType())
		    : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getDeleteAssetTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getDeleteAssetTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssetTypes(com.sitewhere.spi.
     * search.area.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getListAssetTypesMethod());
	    GListAssetTypesRequest.Builder grequest = GListAssetTypesRequest.newBuilder();
	    grequest.setCriteria(AssetModelConverter.asGrpcAssetTypeSearchCriteria(criteria));
	    GListAssetTypesResponse gresponse = getGrpcChannel().getBlockingStub().listAssetTypes(grequest.build());
	    ISearchResults<IAssetType> results = AssetModelConverter
		    .asApiAssetTypeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getListAssetTypesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getListAssetTypesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAsset(com.sitewhere.spi.asset.
     * request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getCreateAssetMethod());
	    GCreateAssetRequest.Builder grequest = GCreateAssetRequest.newBuilder();
	    grequest.setRequest(AssetModelConverter.asGrpcAssetCreateRequest(request));
	    GCreateAssetResponse gresponse = getGrpcChannel().getBlockingStub().createAsset(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getCreateAssetMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getCreateAssetMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAsset(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getUpdateAssetMethod());
	    GUpdateAssetRequest.Builder grequest = GUpdateAssetRequest.newBuilder();
	    grequest.setAssetId(CommonModelConverter.asGrpcUuid(assetId));
	    grequest.setRequest(AssetModelConverter.asGrpcAssetCreateRequest(request));
	    GUpdateAssetResponse gresponse = getGrpcChannel().getBlockingStub().updateAsset(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getUpdateAssetMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getUpdateAssetMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.util.UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getGetAssetByIdMethod());
	    GGetAssetByIdRequest.Builder grequest = GGetAssetByIdRequest.newBuilder();
	    grequest.setAssetId(CommonModelConverter.asGrpcUuid(assetId));
	    GGetAssetByIdResponse gresponse = getGrpcChannel().getBlockingStub().getAssetById(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getGetAssetByIdMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getGetAssetByIdMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getGetAssetByTokenMethod());
	    GGetAssetByTokenRequest.Builder grequest = GGetAssetByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAssetByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getAssetByToken(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getGetAssetByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getGetAssetByTokenMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.util.UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getDeleteAssetMethod());
	    GDeleteAssetRequest.Builder grequest = GDeleteAssetRequest.newBuilder();
	    grequest.setAssetId(CommonModelConverter.asGrpcUuid(assetId));
	    GDeleteAssetResponse gresponse = getGrpcChannel().getBlockingStub().deleteAsset(grequest.build());
	    IAsset response = (gresponse.hasAsset()) ? AssetModelConverter.asApiAsset(gresponse.getAsset()) : null;
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getDeleteAssetMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getDeleteAssetMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(com.sitewhere.spi.search.
     * asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, AssetManagementGrpc.getListAssetsMethod());
	    GListAssetsRequest.Builder grequest = GListAssetsRequest.newBuilder();
	    grequest.setCriteria(AssetModelConverter.asGrpcAssetSearchCriteria(criteria));
	    GListAssetsResponse gresponse = getGrpcChannel().getBlockingStub().listAssets(grequest.build());
	    ISearchResults<IAsset> results = AssetModelConverter.asApiAssetSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(AssetManagementGrpc.getListAssetsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(AssetManagementGrpc.getListAssetsMethod(), t);
	}
    }
}