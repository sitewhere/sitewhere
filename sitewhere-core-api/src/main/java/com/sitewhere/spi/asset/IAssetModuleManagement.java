/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Asset management interface for interacting with asset modules.
 * 
 * @author Derek
 */
public interface IAssetModuleManagement {

    /**
     * Get list of asset module descriptors.
     * 
     * @param type
     * @return
     * @throws SiteWhereException
     */
    public List<IAssetModuleDescriptor> listAssetModuleDescriptors(AssetType type) throws SiteWhereException;

    /**
     * Get descriptor for asset module given its unique id.
     * 
     * @param moduleId
     * @return
     * @throws SiteWhereException
     */
    public IAssetModuleDescriptor getAssetModuleDescriptor(String moduleId) throws SiteWhereException;

    /**
     * Search asset module for assets matching criteria.
     * 
     * @param moduleId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public List<IAsset> searchAssetModule(String moduleId, String criteria) throws SiteWhereException;

    /**
     * Refresh all managed modules.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void refreshModules(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Get referenced asset.
     * 
     * @param reference
     * @return
     * @throws SiteWhereException
     */
    public IAsset getAsset(IAssetReference reference) throws SiteWhereException;
}