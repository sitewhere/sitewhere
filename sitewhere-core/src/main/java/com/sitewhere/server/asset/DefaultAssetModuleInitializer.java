/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.asset.request.AssetCategoryCreateRequest;
import com.sitewhere.rest.model.asset.request.HardwareAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.LocationAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.PersonAssetCreateRequest;
import com.sitewhere.server.asset.filesystem.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.server.asset.IAssetModelInitializer;

/**
 * Used to load the default asset categories and assets used by the default
 * device data. The server only offers this functionality if no asset categories
 * exist.
 * 
 * @author Derek
 */
public class DefaultAssetModuleInitializer implements IAssetModelInitializer {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/**
	 * Indiates whether model should be initialized if no console is available
	 * for input
	 */
	private boolean initializeIfNoConsole = false;

	/** Asset management implementation */
	private IAssetManagement assetManagement;

	/** Asset categories to be created */
	private AssetCategoryCreationInfo[] ASSET_CATEGORY_DATA = {
			buildAssetCategoryRequest("fs-persons", "Default Identity Management", AssetType.Person,
					"person-assets.xml"),
			buildAssetCategoryRequest("fs-devices", "Default Device Management", AssetType.Device, "device-assets.xml"),
			buildAssetCategoryRequest("fs-hardware", "Default Hardware Management", AssetType.Hardware,
					"hardware-assets.xml"),
			buildAssetCategoryRequest("fs-locations", "Default Location Management", AssetType.Location,
					"location-assets.xml") };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.asset.IAssetModelInitializer#initialize(com.
	 * sitewhere. spi.configuration.ITenantConfigurationResolver,
	 * com.sitewhere.spi.asset.IAssetManagement)
	 */
	@Override
	public void initialize(ITenantConfigurationResolver configuration, IAssetManagement assetManagement)
			throws SiteWhereException {
		this.assetManagement = assetManagement;

		// Create asset categories.
		createCategories(configuration);
	}

	/**
	 * Helper method for populating an asset category.
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @param fileName
	 * @return
	 */
	public static AssetCategoryCreationInfo buildAssetCategoryRequest(String id, String name, AssetType type,
			String fileName) {
		AssetCategoryCreationInfo info = new AssetCategoryCreationInfo();
		info.setFileName(fileName);

		AssetCategoryCreateRequest request = new AssetCategoryCreateRequest();
		request.setId(id);
		request.setName(name);
		request.setAssetType(type);

		info.setRequest(request);
		return info;
	}

	/**
	 * Create asset categories.
	 * 
	 * @param assetsFolder
	 * @throws SiteWhereException
	 */
	protected void createCategories(ITenantConfigurationResolver resolver) throws SiteWhereException {
		for (AssetCategoryCreationInfo info : ASSET_CATEGORY_DATA) {
			getAssetManagement().createAssetCategory(info.getRequest());
			AssetType type = info.getRequest().getAssetType();

			switch (type) {
			case Device:
			case Hardware: {
				createHardwareAssets(info.getRequest().getId(), type, getAssetResource(resolver, info.getFileName()));
				break;
			}
			case Person: {
				createPersonAssets(info.getRequest().getId(), getAssetResource(resolver, info.getFileName()));
				break;
			}
			case Location: {
				createLocationAssets(info.getRequest().getId(), getAssetResource(resolver, info.getFileName()));
				break;
			}
			}
		}
		LOGGER.info("Created " + ASSET_CATEGORY_DATA.length + " categories.");
	}

	/**
	 * Create all hardware assets for a given category.
	 * 
	 * @param categoryId
	 * @param type
	 * @param resource
	 * @throws SiteWhereException
	 */
	protected void createHardwareAssets(String categoryId, AssetType type, IResource resource)
			throws SiteWhereException {
		List<HardwareAsset> assets = MarshalUtils.loadHardwareAssets(new ByteArrayInputStream(resource.getContent()),
				type);
		for (HardwareAsset asset : assets) {
			HardwareAssetCreateRequest hw = new HardwareAssetCreateRequest();
			hw.setId(asset.getId());
			hw.setName(asset.getName());
			hw.setImageUrl(asset.getImageUrl());
			hw.setSku(asset.getSku());
			hw.setDescription(asset.getDescription());
			hw.getProperties().putAll(asset.getProperties());
			getAssetManagement().createHardwareAsset(categoryId, hw);
		}
	}

	/**
	 * Create all person assets for a given category.
	 * 
	 * @param categoryId
	 * @param resource
	 * @throws SiteWhereException
	 */
	protected void createPersonAssets(String categoryId, IResource resource) throws SiteWhereException {
		List<PersonAsset> assets = MarshalUtils.loadPersonAssets(new ByteArrayInputStream(resource.getContent()));
		for (PersonAsset asset : assets) {
			PersonAssetCreateRequest person = new PersonAssetCreateRequest();
			person.setId(asset.getId());
			person.setName(asset.getName());
			person.setImageUrl(asset.getImageUrl());
			person.setUserName(asset.getUserName());
			person.setEmailAddress(asset.getEmailAddress());
			person.getProperties().putAll(asset.getProperties());
			person.getRoles().addAll(asset.getRoles());
			getAssetManagement().createPersonAsset(categoryId, person);
		}
	}

	/**
	 * Create all locatino assets for a given category.
	 * 
	 * @param categoryId
	 * @param resource
	 * @throws SiteWhereException
	 */
	protected void createLocationAssets(String categoryId, IResource resource) throws SiteWhereException {
		List<LocationAsset> assets = MarshalUtils.loadLocationAssets(new ByteArrayInputStream(resource.getContent()));
		for (LocationAsset asset : assets) {
			LocationAssetCreateRequest loc = new LocationAssetCreateRequest();
			loc.setId(asset.getId());
			loc.setName(asset.getName());
			loc.setImageUrl(asset.getImageUrl());
			loc.setLatitude(asset.getLatitude());
			loc.setLongitude(asset.getLongitude());
			loc.setElevation(asset.getElevation());
			loc.getProperties().putAll(asset.getProperties());
			getAssetManagement().createLocationAsset(categoryId, loc);
		}
	}

	/**
	 * Get the asset configuration resource based on filename.
	 * 
	 * @param resolver
	 * @param filename
	 * @return
	 * @throws SiteWhereException
	 */
	protected IResource getAssetResource(ITenantConfigurationResolver resolver, String filename)
			throws SiteWhereException {
		IResource resource = resolver.getAssetResource(filename);
		if (resource == null) {
			throw new SiteWhereException("Asset module resource missing. Looking for: " + filename);
		}
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.IModelInitializer#isInitializeIfNoConsole()
	 */
	public boolean isInitializeIfNoConsole() {
		return initializeIfNoConsole;
	}

	public void setInitializeIfNoConsole(boolean initializeIfNoConsole) {
		this.initializeIfNoConsole = initializeIfNoConsole;
	}

	public IAssetManagement getAssetManagement() {
		return assetManagement;
	}

	public void setAssetManagement(IAssetManagement assetManagement) {
		this.assetManagement = assetManagement;
	}

	/**
	 * Wraps asset creation request and category id.
	 * 
	 * @author Derek
	 */
	public static class AssetCategoryCreationInfo {

		/** Get the create request */
		private AssetCategoryCreateRequest request;

		/** File name that contains data */
		private String fileName;

		public AssetCategoryCreateRequest getRequest() {
			return request;
		}

		public void setRequest(AssetCategoryCreateRequest request) {
			this.request = request;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}
}