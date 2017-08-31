/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.hbase;

import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.ProtobufPayloadMarshaler;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
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
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * HBase implementation of {@link IAssetManagement} interface.
 * 
 * @author Derek
 */
public class HBaseAssetManagement extends TenantLifecycleComponent implements IAssetManagement {

    /** Static logger instance */
    private static final Logger LOGGER = LogManager.getLogger();

    /** Used to communicate with HBase */
    private ISiteWhereHBaseClient client;

    /** Injected payload encoder */
    private IPayloadMarshaler payloadMarshaler = new ProtobufPayloadMarshaler();

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
	context.setTenant(getTenant());
	context.setClient(getClient());
	context.setPayloadMarshaler(getPayloadMarshaler());

	ensureTablesExist();

	// Create device id manager instance.
	assetIdManager = new AssetIdManager();
	assetIdManager.load(context);
	context.setAssetIdManager(assetIdManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#createAssetCategory(com.
     * sitewhere.spi. asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
	return HBaseAssetCategory.createAssetCategory(context, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory getAssetCategory(String categoryId) throws SiteWhereException {
	return HBaseAssetCategory.getAssetCategoryById(context, categoryId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateAssetCategory(java.lang.
     * String, com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	return HBaseAssetCategory.updateAssetCategory(context, categoryId, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#listAssetCategories(com.
     * sitewhere.spi. search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAssetCategory> listAssetCategories(ISearchCriteria criteria) throws SiteWhereException {
	return HBaseAssetCategory.listAssetCategories(context, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#deleteAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
	return HBaseAssetCategory.deleteAssetCategory(context, categoryId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createPersonAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	return HBaseAsset.createOrUpdatePersonAsset(context, categoryId, null, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updatePersonAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	return HBaseAsset.createOrUpdatePersonAsset(context, categoryId, assetId, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createHardwareAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	return HBaseAsset.createOrUpdateHardwareAsset(context, categoryId, null, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateHardwareAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset updateHardwareAsset(String categoryId, String assetId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	return HBaseAsset.createOrUpdateHardwareAsset(context, categoryId, assetId, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createLocationAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	return HBaseAsset.createOrUpdateLocationAsset(context, categoryId, null, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateLocationAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset updateLocationAsset(String categoryId, String assetId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	return HBaseAsset.createOrUpdateLocationAsset(context, categoryId, assetId, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException {
	return HBaseAsset.getAsset(context, categoryId, assetId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
	return HBaseAsset.deleteAsset(context, categoryId, assetId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(String categoryId, ISearchCriteria criteria) throws SiteWhereException {
	return HBaseAsset.listAssets(context, categoryId, criteria);
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