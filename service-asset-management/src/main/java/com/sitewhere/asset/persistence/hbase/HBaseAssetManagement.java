/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.hbase;

import java.util.UUID;

import org.apache.hadoop.hbase.regionserver.BloomType;

import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * HBase implementation of {@link IAssetManagement} interface.
 * 
 * @author Derek
 */
public class HBaseAssetManagement extends TenantEngineLifecycleComponent implements IAssetManagement {

    /** Used to communicate with HBase */
    private ISiteWhereHBaseClient client;

    /** Injected payload encoder */
    private IPayloadMarshaler payloadMarshaler;

    /** Supplies context to implementation methods */
    private HBaseContext context;

    /** Asset id manager */
    private AssetIdManager assetIdManager;

    public HBaseAssetManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create context from configured options.
	this.context = new HBaseContext();
	context.setTenant(getTenantEngine().getTenant());
	context.setClient(getClient());
	context.setPayloadMarshaler(getPayloadMarshaler());

	ensureTablesExist();

	// Create device id manager instance.
	assetIdManager = new AssetIdManager();
	assetIdManager.load(context);
	context.setAssetIdManager(assetIdManager);
    }

    /**
     * Make sure that all SiteWhere tables exist, creating them if necessary.
     * 
     * @throws SiteWhereException
     */
    protected void ensureTablesExist() throws SiteWhereException {
	SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.UID_TABLE_NAME, BloomType.ROW);
	SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.ASSETS_TABLE_NAME, BloomType.ROW);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAssetType(com.sitewhere.spi.
     * asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAssetType(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetType(java.util.UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetType(java.util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssetTypes(com.sitewhere.spi.
     * search.area.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAsset(com.sitewhere.spi.asset.
     * request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAsset(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.util.UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.util.UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(com.sitewhere.spi.search.
     * asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented yet for HBase asset managment.");
    }

    public ISiteWhereHBaseClient getClient() {
	return client;
    }

    public void setClient(ISiteWhereHBaseClient client) {
	this.client = client;
    }

    public IPayloadMarshaler getPayloadMarshaler() {
	return payloadMarshaler;
    }

    public void setPayloadMarshaler(IPayloadMarshaler payloadMarshaler) {
	this.payloadMarshaler = payloadMarshaler;
    }
}