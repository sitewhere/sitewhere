/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset;

import java.util.List;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModuleDescriptor;
import com.sitewhere.spi.asset.IAssetModuleManagement;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Wraps an asset module management implementation. Subclasses can implement
 * only the methods they need to override.
 * 
 * @author Derek
 */
public class AssetModuleManagementDecorator extends LifecycleComponentDecorator<IAssetModuleManagement>
	implements IAssetModuleManagement {

    public AssetModuleManagementDecorator(IAssetModuleManagement delegate) {
	super(delegate);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	getDelegate().setTenantEngine(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return getDelegate().getTenantEngine();
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#listAssetModuleDescriptors(com
     * .sitewhere.spi.asset.AssetType)
     */
    @Override
    public List<IAssetModuleDescriptor> listAssetModuleDescriptors(AssetType type) throws SiteWhereException {
	return getDelegate().listAssetModuleDescriptors(type);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#getAssetModuleDescriptor(java.
     * lang.String)
     */
    @Override
    public IAssetModuleDescriptor getAssetModuleDescriptor(String moduleId) throws SiteWhereException {
	return getDelegate().getAssetModuleDescriptor(moduleId);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#searchAssetModule(java.lang.
     * String, java.lang.String)
     */
    @Override
    public List<IAsset> searchAssetModule(String moduleId, String criteria) throws SiteWhereException {
	return getDelegate().searchAssetModule(moduleId, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManagement#getAsset(com.sitewhere.spi.
     * asset.IAssetReference)
     */
    @Override
    public IAsset getAsset(IAssetReference reference) throws SiteWhereException {
	return getDelegate().getAsset(reference);
    }
}