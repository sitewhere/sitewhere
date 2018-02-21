/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.asset.AssetManagementDecorator;
import com.sitewhere.asset.spi.modules.IAssetModuleManager;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Trigger actions based on asset management API calls.
 * 
 * @author Derek
 */
public class AssetManagementTriggers extends AssetManagementDecorator {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(AssetManagementTriggers.class);

    /** Asset module manager */
    private IAssetModuleManager assetModuleManager;

    /** Microservice */
    private IMicroservice microservice;

    public AssetManagementTriggers(IAssetManagement assetManagement, IAssetModuleManager assetModuleManager,
	    IMicroservice microservice) {
	super(assetManagement);
	this.assetModuleManager = assetModuleManager;
	this.microservice = microservice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#createAssetCategory(
     * com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
	IAssetCategory category = super.createAssetCategory(request);
	IAssetModuleManager manager = getAssetModuleManager();
	if ((manager != null) && (manager.getLifecycleStatus() == LifecycleStatus.Started)) {
	    manager.onAssetCategoryAdded(category, new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Add asset module for new category."), getMicroservice()));
	}
	return category;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#updateAssetCategory(
     * java.lang.String,
     * com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	IAssetCategory category = super.updateAssetCategory(categoryId, request);
	IAssetModuleManager manager = getAssetModuleManager();
	if ((manager != null) && (manager.getLifecycleStatus() == LifecycleStatus.Started)) {
	    manager.onAssetCategoryUpdated(category, new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Reload asset module for updated category."), getMicroservice()));
	}
	return category;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#deleteAssetCategory(
     * java.lang.String)
     */
    @Override
    public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
	IAssetCategory category = super.deleteAssetCategory(categoryId);
	IAssetModuleManager manager = getAssetModuleManager();
	if ((manager != null) && (manager.getLifecycleStatus() == LifecycleStatus.Started)) {
	    manager.onAssetCategoryRemoved(category, new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Remove asset module for deleted category."), getMicroservice()));
	}
	return category;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#createPersonAsset(
     * java.lang .String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	IPersonAsset asset = super.createPersonAsset(categoryId, request);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#updatePersonAsset(
     * java.lang .String, java.lang.String,
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	IPersonAsset asset = super.updatePersonAsset(categoryId, assetId, request);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#createHardwareAsset(
     * java.lang .String,
     * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	IHardwareAsset asset = super.createHardwareAsset(categoryId, request);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#updateHardwareAsset(
     * java.lang .String, java.lang.String,
     * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset updateHardwareAsset(String categoryId, String assetId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	IHardwareAsset asset = super.updateHardwareAsset(categoryId, assetId, request);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#createLocationAsset(
     * java.lang .String,
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	ILocationAsset asset = super.createLocationAsset(categoryId, request);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.asset.AssetManagementDecorator#updateLocationAsset(
     * java.lang .String, java.lang.String,
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset updateLocationAsset(String categoryId, String assetId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	ILocationAsset asset = super.updateLocationAsset(categoryId, assetId, request);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.asset.AssetManagementDecorator#deleteAsset(java.lang
     * .String, java.lang.String)
     */
    @Override
    public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
	IAsset asset = super.deleteAsset(categoryId, assetId);
	refreshAssetModule(categoryId, asset);
	return asset;
    }

    /**
     * Push an updated asset to its associated asset module.
     * 
     * @param categoryId
     * @param asset
     * @throws SiteWhereException
     */
    protected <T extends IAsset> void refreshAssetModule(String categoryId, T asset) throws SiteWhereException {
	IAssetModuleManager manager = getAssetModuleManager();
	if ((manager != null) && (manager.getLifecycleStatus() == LifecycleStatus.Started)) {
	    manager.getModule(categoryId).refresh(new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Refresh asset module."), getMicroservice()));
	}
    }

    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }
}