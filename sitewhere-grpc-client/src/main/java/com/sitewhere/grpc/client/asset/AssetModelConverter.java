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

import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.model.AssetModel.GAsset;
import com.sitewhere.grpc.model.AssetModel.GAssetCreateRequest;
import com.sitewhere.grpc.model.AssetModel.GAssetSearchCriteria;
import com.sitewhere.grpc.model.AssetModel.GAssetSearchResults;
import com.sitewhere.grpc.model.AssetModel.GAssetType;
import com.sitewhere.grpc.model.AssetModel.GAssetTypeCreateRequest;
import com.sitewhere.grpc.model.AssetModel.GAssetTypeSearchCriteria;
import com.sitewhere.grpc.model.AssetModel.GAssetTypeSearchResults;
import com.sitewhere.grpc.model.CommonModel.GAssetCategory;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.asset.request.AssetTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetSearchCriteria;
import com.sitewhere.rest.model.search.asset.AssetTypeSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetCategory;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;

/**
 * Convert asset entities between SiteWhere API model and GRPC model.
 */
public class AssetModelConverter {

    /**
     * Convert asset category from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssetCategory asApiAssetCategory(GAssetCategory grpc) throws SiteWhereException {
	switch (grpc) {
	case ASSET_CATEGORY_DEVICE:
	    return AssetCategory.Device;
	case ASSET_CATEGORY_HARDWARE:
	    return AssetCategory.Hardware;
	case ASSET_CATEGORY_PERSON:
	    return AssetCategory.Person;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown asset type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert asset category from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetCategory asGrpcAssetCategory(AssetCategory api) throws SiteWhereException {
	switch (api) {
	case Device:
	    return GAssetCategory.ASSET_CATEGORY_DEVICE;
	case Hardware:
	    return GAssetCategory.ASSET_CATEGORY_HARDWARE;
	case Person:
	    return GAssetCategory.ASSET_CATEGORY_PERSON;
	}
	throw new SiteWhereException("Unknown asset type: " + api.name());
    }

    /**
     * Convert asset type create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssetTypeCreateRequest asApiAssetTypeCreateRequest(GAssetTypeCreateRequest grpc)
	    throws SiteWhereException {
	AssetTypeCreateRequest api = new AssetTypeCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setAssetCategory(AssetModelConverter.asApiAssetCategory(grpc.getAssetCategory()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert asset type create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetTypeCreateRequest asGrpcAssetTypeCreateRequest(IAssetTypeCreateRequest api)
	    throws SiteWhereException {
	GAssetTypeCreateRequest.Builder grpc = GAssetTypeCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setAssetCategory(AssetModelConverter.asGrpcAssetCategory(api.getAssetCategory()));
	grpc.putAllMetadata(api.getMetadata());
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert asset type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssetType asApiAssetType(GAssetType grpc) throws SiteWhereException {
	AssetType api = new AssetType();
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setAssetCategory(AssetModelConverter.asApiAssetCategory(grpc.getAssetCategory()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert asset type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetType asGrpcAssetType(IAssetType api) throws SiteWhereException {
	GAssetType.Builder grpc = GAssetType.newBuilder();
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setAssetCategory(AssetModelConverter.asGrpcAssetCategory(api.getAssetCategory()));
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert asset type search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssetTypeSearchCriteria asApiAssetTypeSearchCriteria(GAssetTypeSearchCriteria grpc)
	    throws SiteWhereException {
	AssetTypeSearchCriteria api = new AssetTypeSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	return api;
    }

    /**
     * Convert asset type search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetTypeSearchCriteria asGrpcAssetTypeSearchCriteria(IAssetTypeSearchCritiera api)
	    throws SiteWhereException {
	GAssetTypeSearchCriteria.Builder grpc = GAssetTypeSearchCriteria.newBuilder();
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
    }

    /**
     * Convert asset type search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IAssetType> asApiAssetTypeSearchResults(GAssetTypeSearchResults response)
	    throws SiteWhereException {
	List<IAssetType> results = new ArrayList<IAssetType>();
	for (GAssetType grpc : response.getAssetTypesList()) {
	    results.add(AssetModelConverter.asApiAssetType(grpc));
	}
	return new SearchResults<IAssetType>(results, response.getCount());
    }

    /**
     * Copy common asset fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static AssetCreateRequest asApiAssetCreateRequest(GAssetCreateRequest grpc) throws SiteWhereException {
	AssetCreateRequest api = new AssetCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setAssetTypeToken(grpc.getAssetTypeToken());
	api.setName(grpc.getName());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert common asset properties from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetCreateRequest asGrpcAssetCreateRequest(IAssetCreateRequest api) throws SiteWhereException {
	GAssetCreateRequest.Builder grpc = GAssetCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setAssetTypeToken(api.getAssetTypeToken());
	grpc.setName(api.getName());
	grpc.putAllMetadata(api.getMetadata());
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert asset from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Asset asApiAsset(GAsset grpc) throws SiteWhereException {
	Asset api = new Asset();
	api.setAssetTypeId(CommonModelConverter.asApiUuid(grpc.getAssetTypeId()));
	api.setName(grpc.getName());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert common asset information from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAsset asGrpcAsset(IAsset api) throws SiteWhereException {
	GAsset.Builder grpc = GAsset.newBuilder();
	grpc.setAssetTypeId(CommonModelConverter.asGrpcUuid(api.getAssetTypeId()));
	grpc.setName(api.getName());
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert asset search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IAsset> asApiAssetSearchResults(GAssetSearchResults response)
	    throws SiteWhereException {
	List<IAsset> results = new ArrayList<IAsset>();
	for (GAsset grpc : response.getAssetsList()) {
	    results.add(AssetModelConverter.asApiAsset(grpc));
	}
	return new SearchResults<IAsset>(results, response.getCount());
    }

    /**
     * Convert asset search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssetSearchCriteria asApiAssetSearchCriteria(GAssetSearchCriteria grpc) throws SiteWhereException {
	AssetSearchCriteria api = new AssetSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setAssetTypeToken(grpc.hasAssetTypeToken() ? grpc.getAssetTypeToken().getValue() : null);
	return api;
    }

    /**
     * Convert asset search criteria from API to GRPC.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GAssetSearchCriteria asGrpcAssetSearchCriteria(IAssetSearchCriteria api) throws SiteWhereException {
	GAssetSearchCriteria.Builder gcriteria = GAssetSearchCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(api));
	if (api.getAssetTypeToken() != null) {
	    gcriteria.setAssetTypeToken(GOptionalString.newBuilder().setValue(api.getAssetTypeToken()));
	}
	return gcriteria.build();
    }
}