package com.sitewhere.asset.persistence;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Persistence logic for asset management components.
 * 
 * @author Derek
 */
public class AssetManagementPersistence extends Persistence {
    /**
     * Common logic for creating an asset category.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static AssetCategory assetCategoryCreateLogic(IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	AssetCategory category = new AssetCategory();

	require(request.getId());
	category.setId(request.getId());

	// Name is required.
	require(request.getName());
	category.setName(request.getName());

	// Type is required.
	requireNotNull(request.getAssetType());
	category.setAssetType(request.getAssetType());

	return category;
    }

    /**
     * Common logic for updating an existing asset category.
     * 
     * @param request
     * @param existing
     * @return
     * @throws SiteWhereException
     */
    public static AssetCategory assetCategoryUpdateLogic(IAssetCategoryCreateRequest request, AssetCategory existing)
	    throws SiteWhereException {
	if (!request.getId().equals(existing.getId())) {
	    throw new SiteWhereException("Can not change the id of an existing asset category.");
	}

	if (request.getAssetType() != existing.getAssetType()) {
	    throw new SiteWhereException("Can not change the asset type of an existing asset category.");
	}

	if (request.getName() != null) {
	    existing.setName(request.getName());
	}

	return existing;
    }

    /**
     * Handle base logic common to all asset types.
     * 
     * @param category
     * @param request
     * @param asset
     * @throws SiteWhereException
     */
    public static void assetCreateLogic(IAssetCategory category, IAssetCreateRequest request, Asset asset)
	    throws SiteWhereException {
	asset.setType(category.getAssetType());

	require(category.getId());
	asset.setAssetCategoryId(category.getId());

	require(request.getId());
	asset.setId(request.getId());

	require(request.getName());
	asset.setName(request.getName());

	require(request.getImageUrl());
	asset.setImageUrl(request.getImageUrl());

	asset.getProperties().putAll(request.getProperties());
    }

    /**
     * Common logic for updating assets.
     * 
     * @param asset
     * @param request
     * @throws SiteWhereException
     */
    public static void assetUpdateLogic(Asset asset, IAssetCreateRequest request) throws SiteWhereException {
	if (!asset.getId().equals(request.getId())) {
	    throw new SiteWhereException("Asset id can not be changed.");
	}

	if (request.getName() != null) {
	    asset.setName(request.getName());
	}
	if (request.getImageUrl() != null) {
	    asset.setImageUrl(request.getImageUrl());
	}
	if (request.getProperties() != null) {
	    asset.getProperties().clear();
	    asset.getProperties().putAll(request.getProperties());
	}
    }

    /**
     * Handle common logic for creating a person asset.
     * 
     * @param category
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static PersonAsset personAssetCreateLogic(IAssetCategory category, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	if (category.getAssetType() != AssetType.Person) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNotAllowed, ErrorLevel.ERROR);
	}

	PersonAsset person = new PersonAsset();
	assetCreateLogic(category, request, person);

	person.setUserName(request.getUserName());
	person.setEmailAddress(request.getEmailAddress());
	person.getRoles().addAll(request.getRoles());

	return person;
    }

    /**
     * Handle common logic for updating a person asset.
     * 
     * @param person
     * @param request
     * @throws SiteWhereException
     */
    public static void personAssetUpdateLogic(PersonAsset person, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	assetUpdateLogic(person, request);

	if (request.getUserName() != null) {
	    person.setUserName(request.getUserName());
	}
	if (request.getEmailAddress() != null) {
	    person.setEmailAddress(request.getEmailAddress());
	}
	if (request.getRoles() != null) {
	    person.getRoles().clear();
	    person.getRoles().addAll(request.getRoles());
	}
    }

    /**
     * Handle common logic for creating a hardware asset.
     * 
     * @param category
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static HardwareAsset hardwareAssetCreateLogic(IAssetCategory category, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	if ((category.getAssetType() != AssetType.Hardware) && (category.getAssetType() != AssetType.Device)) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNotAllowed, ErrorLevel.ERROR);
	}

	HardwareAsset hardware = new HardwareAsset();
	assetCreateLogic(category, request, hardware);

	hardware.setSku(request.getSku());
	hardware.setDescription(request.getDescription());

	return hardware;
    }

    /**
     * Handle common logic for updating a hardware asset.
     * 
     * @param hardware
     * @param request
     * @throws SiteWhereException
     */
    public static void hardwareAssetUpdateLogic(HardwareAsset hardware, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	assetUpdateLogic(hardware, request);

	if (request.getSku() != null) {
	    hardware.setSku(request.getSku());
	}
	if (request.getDescription() != null) {
	    hardware.setDescription(request.getDescription());
	}
    }

    /**
     * Handle common logic for creating a location asset.
     * 
     * @param category
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static LocationAsset locationAssetCreateLogic(IAssetCategory category, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	if (category.getAssetType() != AssetType.Location) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNotAllowed, ErrorLevel.ERROR);
	}

	LocationAsset loc = new LocationAsset();
	assetCreateLogic(category, request, loc);

	loc.setLatitude(request.getLatitude());
	loc.setLongitude(request.getLongitude());
	loc.setElevation(request.getElevation());

	return loc;
    }

    /**
     * Handle common logic for updating a location asset.
     * 
     * @param location
     * @param request
     * @throws SiteWhereException
     */
    public static void locationAssetUpdateLogic(LocationAsset location, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	assetUpdateLogic(location, request);

	if (request.getLatitude() != null) {
	    location.setLatitude(request.getLatitude());
	}
	if (request.getLongitude() != null) {
	    location.setLongitude(request.getLongitude());
	}
	if (request.getElevation() != null) {
	    location.setElevation(request.getElevation());
	}
    }
}