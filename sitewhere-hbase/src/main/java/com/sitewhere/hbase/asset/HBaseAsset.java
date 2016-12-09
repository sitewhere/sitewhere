/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.asset;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * HBase specifics for dealing with SiteWhere assets.
 * 
 * @author Derek
 */
public class HBaseAsset {

    /** Column qualifier for asset type indicator */
    public static final byte[] ASSET_TYPE_INDICATOR = Bytes.toBytes("t");

    /**
     * Create or update a person asset.
     * 
     * @param context
     * @param categoryId
     * @param originalId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static PersonAsset createOrUpdatePersonAsset(IHBaseContext context, String categoryId, String originalId,
	    IPersonAssetCreateRequest request) throws SiteWhereException {
	IAssetCategory category = HBaseAssetCategory.getAssetCategoryById(context, categoryId);

	// Use common processing logic so all backend implementations work the
	// same.
	PersonAsset asset = SiteWherePersistence.personAssetCreateLogic(category, request);
	byte[] payload = context.getPayloadMarshaler().encodePersonAsset(asset);

	saveAsset(context, categoryId, originalId, request, payload, asset.getType());
	return asset;
    }

    /**
     * Create or update a hardware asset.
     * 
     * @param context
     * @param categoryId
     * @param originalId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static HardwareAsset createOrUpdateHardwareAsset(IHBaseContext context, String categoryId, String originalId,
	    IHardwareAssetCreateRequest request) throws SiteWhereException {
	IAssetCategory category = HBaseAssetCategory.getAssetCategoryById(context, categoryId);

	// Use common processing logic so all backend implementations work the
	// same.
	HardwareAsset asset = SiteWherePersistence.hardwareAssetCreateLogic(category, request);
	byte[] payload = context.getPayloadMarshaler().encodeHardwareAsset(asset);

	saveAsset(context, categoryId, originalId, request, payload, asset.getType());
	return asset;
    }

    /**
     * Create or update a location asset.
     * 
     * @param context
     * @param categoryId
     * @param originalId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static LocationAsset createOrUpdateLocationAsset(IHBaseContext context, String categoryId, String originalId,
	    ILocationAssetCreateRequest request) throws SiteWhereException {
	IAssetCategory category = HBaseAssetCategory.getAssetCategoryById(context, categoryId);

	// Use common processing logic so all backend implementations work the
	// same.
	LocationAsset asset = SiteWherePersistence.locationAssetCreateLogic(category, request);
	byte[] payload = context.getPayloadMarshaler().encodeLocationAsset(asset);

	saveAsset(context, categoryId, originalId, request, payload, asset.getType());
	return asset;
    }

    /**
     * Get an asset by category and unique id.
     * 
     * @param context
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    public static Asset getAsset(IHBaseContext context, String categoryId, String assetId) throws SiteWhereException {
	byte[] assetKey = getAssetKey(context, categoryId, assetId);
	Table assets = null;
	try {
	    assets = getAssetsTableInterface(context);
	    Get get = new Get(assetKey);
	    get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
	    get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	    get.addColumn(ISiteWhereHBase.FAMILY_ID, ASSET_TYPE_INDICATOR);
	    Result result = assets.get(get);

	    byte[] ptype = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
	    byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	    if ((ptype == null) || (payload == null)) {
		return null;
	    }

	    byte[] typeBytes = result.getValue(ISiteWhereHBase.FAMILY_ID, ASSET_TYPE_INDICATOR);
	    Class<?> clazz = getClassForType(typeBytes);

	    return (Asset) PayloadMarshalerResolver.getInstance().getMarshaler(ptype).decode(payload, clazz);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create person asset.", e);
	} finally {
	    HBaseUtils.closeCleanly(assets);
	}
    }

    /**
     * Get class used to marshal a given asset type.
     * 
     * @param typeBytes
     * @return
     * @throws SiteWhereException
     */
    protected static Class<?> getClassForType(byte[] typeBytes) throws SiteWhereException {
	if (typeBytes == null) {
	    throw new SiteWhereException("Asset record does not contain asset type indicator.");
	}
	AssetType type = AssetType.valueOf(new String(typeBytes));
	Class<?> clazz = null;
	switch (type) {
	case Device:
	case Hardware: {
	    clazz = HardwareAsset.class;
	    break;
	}
	case Location: {
	    clazz = LocationAsset.class;
	    break;
	}
	case Person:
	    clazz = PersonAsset.class;
	    break;
	}
	return clazz;
    }

    /**
     * Delete the asset by category and unique id.
     * 
     * @param context
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    public static Asset deleteAsset(IHBaseContext context, String categoryId, String assetId)
	    throws SiteWhereException {
	Asset asset = getAsset(context, categoryId, assetId);
	byte[] assetKey = getAssetKey(context, categoryId, assetId);
	Table assets = null;
	try {
	    assets = getAssetsTableInterface(context);
	    Delete delete = new Delete(assetKey);
	    assets.delete(delete);
	    return asset;
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to delete asset.", e);
	} finally {
	    HBaseUtils.closeCleanly(assets);
	}
    }

    /**
     * List assets for the given category that match the criteria.
     * 
     * @param context
     * @param categoryId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IAsset> listAssets(IHBaseContext context, String categoryId, ISearchCriteria criteria)
	    throws SiteWhereException {
	Table assets = null;
	ResultScanner scanner = null;
	byte[] start = HBaseAssetCategory.KEY_BUILDER.buildSubkey(context, categoryId,
		AssetCategorySubtype.Asset.getType());
	byte[] end = HBaseAssetCategory.KEY_BUILDER.buildSubkey(context, categoryId,
		(byte) (AssetCategorySubtype.Asset.getType() + 1));
	try {
	    assets = getAssetsTableInterface(context);
	    Scan scan = new Scan();
	    scan.setStartRow(start);
	    scan.setStopRow(end);
	    scanner = assets.getScanner(scan);

	    Pager<IAsset> pager = new Pager<IAsset>(criteria);
	    for (Result result : scanner) {
		byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		byte[] typeBytes = result.getValue(ISiteWhereHBase.FAMILY_ID, ASSET_TYPE_INDICATOR);

		if ((payloadType != null) && (payload != null)) {
		    Class<?> clazz = getClassForType(typeBytes);

		    Asset asset = (Asset) PayloadMarshalerResolver.getInstance().getMarshaler(payloadType)
			    .decode(payload, clazz);
		    pager.process(asset);
		}
	    }
	    return new SearchResults<IAsset>(pager.getResults(), pager.getTotal());
	} catch (IOException e) {
	    throw new SiteWhereException("Error in list operation.", e);
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	    HBaseUtils.closeCleanly(assets);
	}
    }

    /**
     * Save an asset.
     * 
     * @param context
     * @param categoryId
     * @param originalId
     * @param request
     * @param payload
     * @throws SiteWhereException
     */
    public static void saveAsset(IHBaseContext context, String categoryId, String originalId,
	    IAssetCreateRequest request, byte[] payload, AssetType type) throws SiteWhereException {
	if ((originalId == null) || (!originalId.equals(request.getId()))) {
	    Asset existing = getAsset(context, categoryId, request.getId());
	    if (existing != null) {
		throw new SiteWhereSystemException(ErrorCode.AssetIdInUse, ErrorLevel.ERROR);
	    }
	}

	String id = (originalId == null) ? request.getId() : originalId;
	byte[] assetKey = getAssetKey(context, categoryId, id);

	Table assets = null;
	try {
	    assets = getAssetsTableInterface(context);
	    Put put = new Put(assetKey);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ASSET_TYPE_INDICATOR, type.name().getBytes());
	    assets.put(put);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create person asset.", e);
	} finally {
	    HBaseUtils.closeCleanly(assets);
	}
    }

    /**
     * Get the key for an asset.
     * 
     * @param context
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    public static byte[] getAssetKey(IHBaseContext context, String categoryId, String assetId)
	    throws SiteWhereException {
	byte[] baseKey = HBaseAssetCategory.KEY_BUILDER.buildSubkey(context, categoryId,
		AssetCategorySubtype.Asset.getType());
	byte[] idBytes = assetId.getBytes();
	ByteBuffer buffer = ByteBuffer.allocate(baseKey.length + idBytes.length);
	buffer.put(baseKey);
	buffer.put(idBytes);
	return buffer.array();
    }

    /**
     * Get assets table based on context.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected static Table getAssetsTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.ASSETS_TABLE_NAME);
    }
}