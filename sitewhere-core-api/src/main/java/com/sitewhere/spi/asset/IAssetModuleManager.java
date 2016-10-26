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
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Interface for interacting with the asset module manager.
 * 
 * @author dadams
 */
public interface IAssetModuleManager extends ITenantLifecycleComponent {

    /**
     * Get asset module by unique id.
     * 
     * @param assetModuleId
     * @return
     * @throws SiteWhereException
     */
    public IAssetModule<?> getModule(String assetModuleId) throws SiteWhereException;

    /**
     * Get the list of asset modules.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IAssetModule<?>> listModules() throws SiteWhereException;

    /**
     * Calls the refresh method on each asset module and returns a list of
     * responses.
     * 
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public List<ICommandResponse> refreshModules(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Refresh modules loaded from datastore.
     * 
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public List<ICommandResponse> refreshDatastoreModules(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Finds an asset in a given module.
     * 
     * @param assetModuleId
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException;

    /**
     * Search an asset module for assets matching the given criteria.
     * 
     * @param assetModuleId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public List<? extends IAsset> search(String assetModuleId, String criteria) throws SiteWhereException;
}