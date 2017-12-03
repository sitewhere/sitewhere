/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.asset.spi.modules.IAssetModule;
import com.sitewhere.asset.spi.modules.IAssetModuleManager;
import com.sitewhere.rest.model.asset.AssetModuleDescriptor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModuleDescriptor;
import com.sitewhere.spi.asset.IAssetModuleManagement;
import com.sitewhere.spi.asset.IAssetReference;

/**
 * Adapter that wraps an {@link IAssetModuleManager} to provide the
 * {@link IAssetModuleManagement} API.
 * 
 * @author Derek
 */
public class AssetModuleManagementAdapter implements IAssetModuleManagement {

    /** Asset module manager */
    private IAssetModuleManager assetModuleManager;

    public AssetModuleManagementAdapter(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#listAssetModuleDescriptors(com
     * .sitewhere.spi.asset.AssetType)
     */
    @Override
    public List<IAssetModuleDescriptor> listAssetModuleDescriptors(AssetType type) throws SiteWhereException {
	List<IAssetModule<?>> modules = getAssetModuleManager().listModules();
	List<IAssetModuleDescriptor> descriptors = new ArrayList<IAssetModuleDescriptor>();
	for (IAssetModule<?> module : modules) {
	    descriptors.add(createDescriptor(module));
	}
	return descriptors;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#getAssetModuleDescriptor(java.
     * lang.String)
     */
    @Override
    public IAssetModuleDescriptor getAssetModuleDescriptor(String moduleId) throws SiteWhereException {
	IAssetModule<?> module = getAssetModuleManager().getModule(moduleId);
	return createDescriptor(module);
    }

    /**
     * Create descriptor for asset module.
     * 
     * @param module
     * @return
     */
    public static IAssetModuleDescriptor createDescriptor(IAssetModule<?> module) {
	AssetModuleDescriptor descriptor = new AssetModuleDescriptor();
	descriptor.setId(module.getId());
	descriptor.setName(module.getName());
	descriptor.setAssetType(module.getAssetType());
	return descriptor;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#searchAssetModule(java.lang.
     * String, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IAsset> searchAssetModule(String moduleId, String criteria) throws SiteWhereException {
	return (List<IAsset>) getAssetModuleManager().search(moduleId, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#getAsset(com.sitewhere.spi.
     * asset.IAssetReference)
     */
    @Override
    public IAsset getAsset(IAssetReference reference) throws SiteWhereException {
	return getAssetModuleManager().getAssetById(reference.getModule(), reference.getId());
    }

    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }
}