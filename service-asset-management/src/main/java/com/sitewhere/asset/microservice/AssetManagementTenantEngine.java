/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.microservice;

import com.sitewhere.asset.grpc.AssetManagementImpl;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.microservice.spi.spring.AssetManagementBeans;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements asset
 * management functionality.
 * 
 * @author Derek
 */
public class AssetManagementTenantEngine extends MicroserviceTenantEngine implements IAssetManagementTenantEngine {

    /** Asset management persistence API */
    private IAssetManagement assetManagement;

    /** Responds to asset management GRPC requests */
    private AssetManagementGrpc.AssetManagementImplBase assetManagementImpl;

    public AssetManagementTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.assetManagement = (IAssetManagement) getModuleContext()
		.getBean(AssetManagementBeans.BEAN_ASSET_MANAGEMENT);
	this.assetManagementImpl = new AssetManagementImpl(getAssetManagement());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagementImpl()
     */
    @Override
    public AssetManagementGrpc.AssetManagementImplBase getAssetManagementImpl() {
	return assetManagementImpl;
    }

    public void setAssetManagementImpl(AssetManagementGrpc.AssetManagementImplBase assetManagementImpl) {
	this.assetManagementImpl = assetManagementImpl;
    }
}