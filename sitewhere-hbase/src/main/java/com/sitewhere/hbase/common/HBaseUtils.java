/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.Logger;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.PayloadEncoding;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Handle common HBase functionality.
 * 
 * @author Derek
 */
public class HBaseUtils {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(HBaseUtils.class);

	/**
	 * Create or update primary record.
	 * 
	 * @param client
	 * @param marshaler
	 * @param tableName
	 * @param entity
	 * @param token
	 * @param builder
	 * @param qualifiers
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T createOrUpdate(ISiteWhereHBaseClient client, IPayloadMarshaler marshaler,
			byte[] tableName, T entity, String token, IRowKeyBuilder builder, Map<byte[], byte[]> qualifiers)
			throws SiteWhereException {
		byte[] primary = builder.buildPrimaryKey(token);
		byte[] payload = marshaler.encode(entity);

		HTableInterface table = null;
		try {
			table = client.getTableInterface(tableName);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(marshaler.getEncoding(), put, payload);
			for (byte[] key : qualifiers.keySet()) {
				put.add(ISiteWhereHBase.FAMILY_ID, key, qualifiers.get(key));
			}
			table.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create entity data for " + entity.getClass().getName(), e);
		} finally {
			HBaseUtils.closeCleanly(table);
		}

		return entity;
	}

	/**
	 * Save payload.
	 * 
	 * @param client
	 * @param marshaler
	 * @param tableName
	 * @param entity
	 * @param token
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T put(ISiteWhereHBaseClient client, IPayloadMarshaler marshaler, byte[] tableName,
			T entity, String token, IRowKeyBuilder builder) throws SiteWhereException {
		byte[] primary = builder.buildPrimaryKey(token);
		byte[] payload = marshaler.encode(entity);

		HTableInterface table = null;
		try {
			table = client.getTableInterface(tableName);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(marshaler.getEncoding(), put, payload);
			table.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to put JSON data for " + entity.getClass().getName(), e);
		} finally {
			HBaseUtils.closeCleanly(table);
		}

		return entity;
	}

	/**
	 * Get a primary record by token.
	 * 
	 * @param context
	 * @param tableName
	 * @param token
	 * @param builder
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T get(ISiteWhereHBaseClient client, byte[] tableName, String token,
			IRowKeyBuilder builder, Class<T> type) throws SiteWhereException {
		byte[] primary = builder.buildPrimaryKey(token);

		HTableInterface table = null;
		try {
			table = client.getTableInterface(tableName);
			Get get = new Get(primary);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
			Result result = table.get(get);

			byte[] ptype = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
			byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
			if ((ptype == null) || (payload == null)) {
				return null;
			}

			return PayloadMarshalerResolver.getInstance().getMarshaler(ptype).decode(payload, type);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load data for token: " + token, e);
		} finally {
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Get all matching records, sort them, and get matching pages. TODO: This is not
	 * efficient since it always processes all records.
	 * 
	 * @param client
	 * @param tableName
	 * @param builder
	 * @param includeDeleted
	 * @param intf
	 * @param clazz
	 * @param filter
	 * @param criteria
	 * @param comparator
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	public static <I, C> SearchResults<I> getFilteredList(ISiteWhereHBaseClient client, byte[] tableName,
			IRowKeyBuilder builder, boolean includeDeleted, Class<I> intf, Class<C> clazz, IFilter<C> filter,
			ISearchCriteria criteria, Comparator<C> comparator) throws SiteWhereException {
		List<C> results = getRecordList(client, tableName, builder, includeDeleted, clazz, filter);
		Collections.sort(results, comparator);
		Pager<I> pager = new Pager<I>(criteria);
		for (C result : results) {
			pager.process((I) result);
		}
		return new SearchResults<I>(pager.getResults(), pager.getTotal());
	}

	/**
	 * Get list of records that match the given criteria.
	 * 
	 * @param client
	 * @param tableName
	 * @param builder
	 * @param includeDeleted
	 * @param clazz
	 * @param filter
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> List<T> getRecordList(ISiteWhereHBaseClient client, byte[] tableName,
			IRowKeyBuilder builder, boolean includeDeleted, Class<T> clazz, IFilter<T> filter)
			throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = client.getTableInterface(tableName);
			Scan scan = new Scan();
			scan.setStartRow(new byte[] { builder.getTypeIdentifier() });
			scan.setStopRow(new byte[] { (byte) (builder.getTypeIdentifier() + 1) });
			scanner = table.getScanner(scan);

			List<T> results = new ArrayList<T>();
			for (Result result : scanner) {
				byte[] row = result.getRow();

				// Only match primary rows.
				if ((row[0] != builder.getTypeIdentifier())
						|| (row[builder.getKeyIdLength() + 1] != builder.getPrimaryIdentifier())) {
					continue;
				}

				boolean shouldAdd = true;
				byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				byte[] deleted = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED);

				if ((deleted != null) && (!includeDeleted)) {
					shouldAdd = false;
				}

				if ((shouldAdd) && (payload != null)) {
					T instance =
							PayloadMarshalerResolver.getInstance().getMarshaler(payloadType).decode(payload,
									clazz);
					if (!filter.isExcluded(instance)) {
						results.add(instance);
					}
				}
			}
			return results;
		} catch (IOException e) {
			throw new SiteWhereException("Error in list operation.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Delete a primary record by token.
	 * 
	 * @param context
	 * @param tableName
	 * @param token
	 * @param force
	 * @param builder
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T extends MetadataProviderEntity> T delete(ISiteWhereHBaseClient client,
			IPayloadMarshaler marshaler, byte[] tableName, String token, boolean force,
			IRowKeyBuilder builder, Class<T> type) throws SiteWhereException {
		T existing = get(client, tableName, token, builder, type);
		existing.setDeleted(true);

		byte[] primary = builder.buildPrimaryKey(token);
		if (force) {
			builder.deleteReference(token);
			HTableInterface table = null;
			try {
				Delete delete = new Delete(primary);
				table = client.getTableInterface(tableName);
				table.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete data for token: " + token, e);
			} finally {
				HBaseUtils.closeCleanly(table);
			}
		} else {
			byte[] marker = { (byte) 0x01 };
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = marshaler.encode(existing);

			HTableInterface devices = null;
			try {
				devices = client.getTableInterface(tableName);
				Put put = new Put(primary);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE,
						marshaler.getEncoding().getIndicator());
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD, updated);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
				devices.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to flag deleted for token: " + token, e);
			} finally {
				HBaseUtils.closeCleanly(devices);
			}
		}
		return existing;
	}

	/**
	 * Delete an element without the option of undeleting it.
	 * 
	 * @param client
	 * @param marshaler
	 * @param tableName
	 * @param token
	 * @param builder
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T forcedDelete(ISiteWhereHBaseClient client, IPayloadMarshaler marshaler,
			byte[] tableName, String token, IRowKeyBuilder builder, Class<T> type) throws SiteWhereException {
		T existing = get(client, tableName, token, builder, type);

		byte[] primary = builder.buildPrimaryKey(token);
		builder.deleteReference(token);
		HTableInterface table = null;
		try {
			Delete delete = new Delete(primary);
			table = client.getTableInterface(tableName);
			table.delete(delete);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to delete data for token: " + token, e);
		} finally {
			HBaseUtils.closeCleanly(table);
		}
		return existing;
	}

	/**
	 * Adds payload fields to an HBase put.
	 * 
	 * @param context
	 * @param put
	 * @param encoded
	 * @throws SiteWhereException
	 */
	public static void addPayloadFields(PayloadEncoding encoding, Put put, byte[] encoded)
			throws SiteWhereException {
		put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE, encoding.getIndicator());
		put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD, encoded);
	}

	/**
	 * Adds payload fields to an HBase get.
	 * 
	 * @param get
	 * @throws SiteWhereException
	 */
	public static void addPayloadFields(Get get) throws SiteWhereException {
		get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	}

	/**
	 * Prevent having to add custom {@link IOException} handling.
	 * 
	 * @param table
	 * @throws SiteWhereException
	 */
	public static void closeCleanly(HTableInterface table) throws SiteWhereException {
		try {
			if (table != null) {
				table.close();
			}
		} catch (IOException e) {
			throw new SiteWhereException("Exception closing table.", e);
		}
	}
}