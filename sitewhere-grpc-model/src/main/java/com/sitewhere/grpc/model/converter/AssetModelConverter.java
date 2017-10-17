/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.model.AssetModel.GAnyAsset;
import com.sitewhere.grpc.model.AssetModel.GAsset;
import com.sitewhere.grpc.model.AssetModel.GAssetCategory;
import com.sitewhere.grpc.model.AssetModel.GAssetCategoryCreateRequest;
import com.sitewhere.grpc.model.AssetModel.GAssetCategorySearchCriteria;
import com.sitewhere.grpc.model.AssetModel.GAssetCategorySearchResults;
import com.sitewhere.grpc.model.AssetModel.GAssetCreateRequest;
import com.sitewhere.grpc.model.AssetModel.GAssetSearchCriteria;
import com.sitewhere.grpc.model.AssetModel.GAssetSearchResults;
import com.sitewhere.grpc.model.AssetModel.GHardwareAsset;
import com.sitewhere.grpc.model.AssetModel.GHardwareAssetCreateRequest;
import com.sitewhere.grpc.model.AssetModel.GLocationAsset;
import com.sitewhere.grpc.model.AssetModel.GLocationAssetCreateRequest;
import com.sitewhere.grpc.model.AssetModel.GPersonAsset;
import com.sitewhere.grpc.model.AssetModel.GPersonAssetCreateRequest;
import com.sitewhere.grpc.model.CommonModel.GAssetType;
import com.sitewhere.grpc.model.CommonModel.GOptionalDouble;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.asset.request.AssetCategoryCreateRequest;
import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.asset.request.HardwareAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.LocationAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.PersonAssetCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Convert asset entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class AssetModelConverter {

    /**
     * Convert asset type from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssetType asApiAssetType(GAssetType grpc) throws SiteWhereException {
	switch (grpc) {
	case ASSET_TYPE_DEVICE:
	    return AssetType.Device;
	case ASSET_TYPE_HARDWARE:
	    return AssetType.Hardware;
	case ASSET_TYPE_LOCATION:
	    return AssetType.Location;
	case ASSET_TYPE_PERSON:
	    return AssetType.Person;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown asset type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert asset type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetType asGrpcAssetType(AssetType api) throws SiteWhereException {
	switch (api) {
	case Device:
	    return GAssetType.ASSET_TYPE_DEVICE;
	case Hardware:
	    return GAssetType.ASSET_TYPE_HARDWARE;
	case Location:
	    return GAssetType.ASSET_TYPE_LOCATION;
	case Person:
	    return GAssetType.ASSET_TYPE_PERSON;
	}
	throw new SiteWhereException("Unknown asset type: " + api.name());
    }

    /**
     * Convert asset category create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IAssetCategoryCreateRequest asApiAssetCategoryCreateRequest(GAssetCategoryCreateRequest grpc)
	    throws SiteWhereException {
	AssetCategoryCreateRequest api = new AssetCategoryCreateRequest();
	api.setAssetType(AssetModelConverter.asApiAssetType(grpc.getAssetType()));
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	return api;
    }

    /**
     * Convert asset category create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetCategoryCreateRequest asGrpcAssetCategoryCreateRequest(IAssetCategoryCreateRequest api)
	    throws SiteWhereException {
	GAssetCategoryCreateRequest.Builder grpc = GAssetCategoryCreateRequest.newBuilder();
	grpc.setAssetType(AssetModelConverter.asGrpcAssetType(api.getAssetType()));
	grpc.setId(api.getId());
	grpc.setName(api.getName());
	return grpc.build();
    }

    /**
     * Convert asset category search criteria from API to GRPC.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GAssetCategorySearchCriteria asApiAssetCategorySearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GAssetCategorySearchCriteria.Builder gcriteria = GAssetCategorySearchCriteria.newBuilder();
	if (criteria != null) {
	    gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	}
	return gcriteria.build();
    }

    /**
     * Convert asset category search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IAssetCategory> asApiAssetCategorySearchResults(GAssetCategorySearchResults response)
	    throws SiteWhereException {
	List<IAssetCategory> results = new ArrayList<IAssetCategory>();
	for (GAssetCategory grpc : response.getCategoriesList()) {
	    results.add(AssetModelConverter.asApiAssetCategory(grpc));
	}
	return new SearchResults<IAssetCategory>(results, response.getCount());
    }

    /**
     * Convert asset category from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IAssetCategory asApiAssetCategory(GAssetCategory grpc) throws SiteWhereException {
	AssetCategory api = new AssetCategory();
	api.setAssetType(AssetModelConverter.asApiAssetType(grpc.getAssetType()));
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	return api;
    }

    /**
     * Convert asset category from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetCategory asGrpcAssetCategory(IAssetCategory api) throws SiteWhereException {
	GAssetCategory.Builder grpc = GAssetCategory.newBuilder();
	grpc.setAssetType(AssetModelConverter.asGrpcAssetType(api.getAssetType()));
	grpc.setId(api.getId());
	grpc.setName(api.getName());
	return grpc.build();
    }

    /**
     * Copy common asset fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static void copyAssetCreateRequestFields(GAssetCreateRequest grpc, AssetCreateRequest api)
	    throws SiteWhereException {
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	api.setImageUrl(grpc.getImageUrl());
	api.setProperties(grpc.getPropertiesMap());
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
	grpc.setId(api.getId());
	grpc.setName(api.getName());
	grpc.setImageUrl(api.getImageUrl());
	grpc.putAllProperties(api.getProperties());
	return grpc.build();
    }

    /**
     * Copy common asset fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static void copyAssetFields(GAsset grpc, Asset api) throws SiteWhereException {
	api.setAssetCategoryId(grpc.getAssetCategoryId());
	api.setId(grpc.getId());
	api.setName(grpc.getName());
	api.setType(AssetModelConverter.asApiAssetType(grpc.getAssetType()));
	api.setProperties(grpc.getPropertiesMap());
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
	grpc.setAssetCategoryId(api.getAssetCategoryId());
	grpc.setId(api.getId());
	grpc.setName(api.getName());
	grpc.setAssetType(AssetModelConverter.asGrpcAssetType(api.getType()));
	grpc.putAllProperties(api.getProperties());
	return grpc.build();
    }

    /**
     * Convert person asset create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IPersonAssetCreateRequest asApiPersonAssetCreateRequest(GPersonAssetCreateRequest grpc)
	    throws SiteWhereException {
	PersonAssetCreateRequest api = new PersonAssetCreateRequest();
	api.setUserName(grpc.getUserName());
	api.setEmailAddress(grpc.getEmailAddress());
	api.setRoles(grpc.getRolesList());
	AssetModelConverter.copyAssetCreateRequestFields(grpc.getAsset(), api);
	return api;
    }

    /**
     * Convert person asset create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GPersonAssetCreateRequest asGrpcPersonAssetCreateRequest(IPersonAssetCreateRequest api)
	    throws SiteWhereException {
	GPersonAssetCreateRequest.Builder grpc = GPersonAssetCreateRequest.newBuilder();
	grpc.setUserName(api.getUserName());
	grpc.setEmailAddress(api.getEmailAddress());
	grpc.addAllRoles(api.getRoles());
	grpc.setAsset(AssetModelConverter.asGrpcAssetCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert person asset from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IPersonAsset asApiPersonAsset(GPersonAsset grpc) throws SiteWhereException {
	PersonAsset api = new PersonAsset();
	api.setUserName(grpc.getUserName());
	api.setEmailAddress(grpc.getEmailAddress());
	api.setRoles(grpc.getRolesList());
	AssetModelConverter.copyAssetFields(grpc.getAsset(), api);
	return api;
    }

    /**
     * Convert person asset from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GPersonAsset asGrpcPersonAsset(IPersonAsset api) throws SiteWhereException {
	GPersonAsset.Builder grpc = GPersonAsset.newBuilder();
	grpc.setUserName(api.getUserName());
	grpc.setEmailAddress(api.getEmailAddress());
	grpc.addAllRoles(api.getRoles());
	grpc.setAsset(AssetModelConverter.asGrpcAsset(api));
	return grpc.build();
    }

    /**
     * Convert hardware asset create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IHardwareAssetCreateRequest asApiHardwareAssetCreateRequest(GHardwareAssetCreateRequest grpc)
	    throws SiteWhereException {
	HardwareAssetCreateRequest api = new HardwareAssetCreateRequest();
	api.setSku(grpc.getSku());
	api.setDescription(grpc.getDescription());
	AssetModelConverter.copyAssetCreateRequestFields(grpc.getAsset(), api);
	return api;
    }

    /**
     * Convert hardware asset create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GHardwareAssetCreateRequest asGrpcHardwareAssetCreateRequest(IHardwareAssetCreateRequest api)
	    throws SiteWhereException {
	GHardwareAssetCreateRequest.Builder grpc = GHardwareAssetCreateRequest.newBuilder();
	grpc.setSku(api.getSku());
	grpc.setDescription(api.getDescription());
	grpc.setAsset(AssetModelConverter.asGrpcAssetCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert hardware asset from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IHardwareAsset asApiHardwareAsset(GHardwareAsset grpc) throws SiteWhereException {
	HardwareAsset api = new HardwareAsset();
	api.setSku(grpc.getSku());
	api.setDescription(grpc.getDescription());
	AssetModelConverter.copyAssetFields(grpc.getAsset(), api);
	return api;
    }

    /**
     * Convert hardware asset from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GHardwareAsset asGrpcHardwareAsset(IHardwareAsset api) throws SiteWhereException {
	GHardwareAsset.Builder grpc = GHardwareAsset.newBuilder();
	grpc.setSku(api.getSku());
	grpc.setDescription(api.getDescription());
	grpc.setAsset(AssetModelConverter.asGrpcAsset(api));
	return grpc.build();
    }

    /**
     * Convert location asset create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ILocationAssetCreateRequest asApiLocationAssetCreateRequest(GLocationAssetCreateRequest grpc)
	    throws SiteWhereException {
	LocationAssetCreateRequest api = new LocationAssetCreateRequest();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : null);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : null);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : null);
	AssetModelConverter.copyAssetCreateRequestFields(grpc.getAsset(), api);
	return api;
    }

    /**
     * Convert location asset create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLocationAssetCreateRequest asGrpcLocationAssetCreateRequest(ILocationAssetCreateRequest api)
	    throws SiteWhereException {
	GLocationAssetCreateRequest.Builder grpc = GLocationAssetCreateRequest.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	grpc.setAsset(AssetModelConverter.asGrpcAssetCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert location asset from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ILocationAsset asApiLocationAsset(GLocationAsset grpc) throws SiteWhereException {
	LocationAsset api = new LocationAsset();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : null);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : null);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : null);
	AssetModelConverter.copyAssetFields(grpc.getAsset(), api);
	return api;
    }

    /**
     * Convert location asset from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLocationAsset asGrpcLocationAsset(ILocationAsset api) throws SiteWhereException {
	GLocationAsset.Builder grpc = GLocationAsset.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	grpc.setAsset(AssetModelConverter.asGrpcAsset(api));
	return grpc.build();
    }

    /**
     * Convert generic asset from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IAsset asApiGenericAsset(GAnyAsset grpc) throws SiteWhereException {
	switch (grpc.getAssetCase()) {
	case HARDWARE: {
	    return AssetModelConverter.asApiHardwareAsset(grpc.getHardware());
	}
	case LOCATION: {
	    return AssetModelConverter.asApiLocationAsset(grpc.getLocation());
	}
	case PERSON: {
	    return AssetModelConverter.asApiPersonAsset(grpc.getPerson());
	}
	case ASSET_NOT_SET: {
	    break;
	}
	}
	throw new SiteWhereException("Unable to convert asset to API. " + grpc.getAssetCase().toString());
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
	for (GAnyAsset grpc : response.getAssetsList()) {
	    results.add(AssetModelConverter.asApiGenericAsset(grpc));
	}
	return new SearchResults<IAsset>(results, response.getCount());
    }

    /**
     * Convert asset search criteria from API to GRPC.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GAssetSearchCriteria asGrpcAssetSearchCriteria(ISearchCriteria criteria) throws SiteWhereException {
	GAssetSearchCriteria.Builder gcriteria = GAssetSearchCriteria.newBuilder();
	if (criteria != null) {
	    gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	}
	return gcriteria.build();
    }
}