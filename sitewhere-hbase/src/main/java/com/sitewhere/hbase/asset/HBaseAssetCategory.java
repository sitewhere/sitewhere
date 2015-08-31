/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.asset;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere asset categories.
 * 
 * @author Derek
 */
public class HBaseAssetCategory {

	/** Length of group identifier (subset of 8 byte long) */
	public static final int IDENTIFIER_LENGTH = 4;

	/** Used to look up row keys from tokens */
	public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

		@Override
		public UniqueIdCounterMap getMap(IHBaseContext context) {
			return context.getAssetIdManager().getAssetKeys();
		}

		@Override
		public byte getTypeIdentifier() {
			return AssetsRecordType.AssetCategory.getType();
		}

		@Override
		public byte getPrimaryIdentifier() {
			return AssetCategorySubtype.AssetCategory.getType();
		}

		@Override
		public int getKeyIdLength() {
			return IDENTIFIER_LENGTH;
		}

		@Override
		public void throwInvalidKey() throws SiteWhereException {
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetCategoryId, ErrorLevel.ERROR);
		}
	};

	/**
	 * Create a new asset category.
	 * 
	 * @param context
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static AssetCategory createAssetCategory(IHBaseContext context, IAssetCategoryCreateRequest request)
			throws SiteWhereException {
		if (getAssetCategoryById(context, request.getId()) != null) {
			throw new SiteWhereSystemException(ErrorCode.AssetCategoryIdInUse, ErrorLevel.ERROR);
		}

		// Add new key to table.
		String id = KEY_BUILDER.getMap(context).useExistingId(request.getId());

		// Use common logic so all backend implementations work the same.
		AssetCategory category = SiteWherePersistence.assetCategoryCreateLogic(request);

		Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
		return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(),
				ISiteWhereHBase.ASSETS_TABLE_NAME, category, id, KEY_BUILDER, qualifiers);
	}

	/**
	 * Get an asset category by unique id.
	 * 
	 * @param context
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public static AssetCategory getAssetCategoryById(IHBaseContext context, String id)
			throws SiteWhereException {
		if (KEY_BUILDER.getMap(context).getValue(id) == null) {
			return null;
		}
		return HBaseUtils.get(context, ISiteWhereHBase.ASSETS_TABLE_NAME, id, KEY_BUILDER,
				AssetCategory.class);
	}

	/**
	 * Update an existing asset category.
	 * 
	 * @param context
	 * @param id
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IAssetCategory updateAssetCategory(IHBaseContext context, String id,
			IAssetCategoryCreateRequest request) throws SiteWhereException {
		AssetCategory updated = assertAssetCategory(context, id);
		SiteWherePersistence.assetCategoryUpdateLogic(request, updated);
		return HBaseUtils.put(context, context.getPayloadMarshaler(), ISiteWhereHBase.ASSETS_TABLE_NAME,
				updated, id, KEY_BUILDER);
	}

	/**
	 * Get list of asset categories that match the given criteria.
	 * 
	 * @param context
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IAssetCategory> listAssetCategories(IHBaseContext context,
			ISearchCriteria criteria) throws SiteWhereException {
		Comparator<AssetCategory> comparator = new Comparator<AssetCategory>() {

			public int compare(AssetCategory a, AssetCategory b) {
				return a.getName().compareTo(b.getName());
			}

		};
		IFilter<AssetCategory> filter = new IFilter<AssetCategory>() {

			public boolean isExcluded(AssetCategory item) {
				return false;
			}
		};
		return HBaseUtils.getFilteredList(context, ISiteWhereHBase.ASSETS_TABLE_NAME, KEY_BUILDER, true,
				IAssetCategory.class, AssetCategory.class, filter, criteria, comparator);
	}

	/**
	 * Delete an asset category.
	 * 
	 * @param context
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public static AssetCategory deleteAssetCategory(IHBaseContext context, String id)
			throws SiteWhereException {
		// TODO: Delete all assets.
		return HBaseUtils.forcedDelete(context, context.getPayloadMarshaler(),
				ISiteWhereHBase.ASSETS_TABLE_NAME, id, KEY_BUILDER, AssetCategory.class);
	}

	/**
	 * Get an asset category by id. Throw an exception if not found.
	 * 
	 * @param context
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public static AssetCategory assertAssetCategory(IHBaseContext context, String id)
			throws SiteWhereException {
		AssetCategory existing = getAssetCategoryById(context, id);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetCategoryId, ErrorLevel.ERROR);
		}
		return existing;
	}
}