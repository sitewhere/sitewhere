/*
 * HBaseUtils.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Handle common HBase functionality.
 * 
 * @author Derek
 */
public class HBaseUtils {

	/**
	 * Create a primary record.
	 * 
	 * @param hbase
	 * @param tableName
	 * @param entity
	 * @param token
	 * @param builder
	 * @param qualifiers
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T create(ISiteWhereHBaseClient hbase, byte[] tableName, T entity, String token,
			IRowKeyBuilder builder, Map<byte[], byte[]> qualifiers) throws SiteWhereException {
		byte[] primary = builder.buildPrimaryKey(token);
		byte[] json = MarshalUtils.marshalJson(entity);

		HTableInterface table = null;
		try {
			table = hbase.getTableInterface(tableName);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
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
	 * Put JSON content for a primary record.
	 * 
	 * @param hbase
	 * @param tableName
	 * @param entity
	 * @param token
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T putJson(ISiteWhereHBaseClient hbase, byte[] tableName, T entity, String token,
			IRowKeyBuilder builder) throws SiteWhereException {
		byte[] primary = builder.buildPrimaryKey(token);
		byte[] json = MarshalUtils.marshalJson(entity);

		HTableInterface table = null;
		try {
			table = hbase.getTableInterface(tableName);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
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
	 * @param hbase
	 * @param tableName
	 * @param token
	 * @param builder
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T> T get(ISiteWhereHBaseClient hbase, byte[] tableName, String token,
			IRowKeyBuilder builder, Class<T> type) throws SiteWhereException {
		byte[] primary = builder.buildPrimaryKey(token);

		HTableInterface table = null;
		try {
			table = hbase.getTableInterface(tableName);
			Get get = new Get(primary);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = table.get(get);
			if (result.size() != 1) {
				throw new SiteWhereException("Expected one JSON entry and found: " + result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), type);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load data for token: " + token, e);
		} finally {
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Get a filtered list of the primary record type.
	 * 
	 * @param hbase
	 * @param tableName
	 * @param builder
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static Pager<byte[]> getFilteredList(ISiteWhereHBaseClient hbase, byte[] tableName,
			IRowKeyBuilder builder, boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = hbase.getTableInterface(tableName);
			Scan scan = new Scan();
			scan.setStartRow(new byte[] { builder.getTypeIdentifier() });
			scan.setStopRow(new byte[] { (byte) (builder.getTypeIdentifier() + 1) });
			scanner = table.getScanner(scan);

			Pager<byte[]> pager = new Pager<byte[]>(criteria);
			for (Result result : scanner) {
				byte[] row = result.getRow();

				// Only match primary rows.
				if ((row[0] != builder.getTypeIdentifier())
						|| (row[builder.getKeyIdLength() + 1] != builder.getPrimaryIdentifier())) {
					continue;
				}

				boolean shouldAdd = true;
				byte[] json = null;
				for (KeyValue column : result.raw()) {
					byte[] qualifier = column.getQualifier();
					if ((Bytes.equals(ISiteWhereHBase.DELETED, qualifier)) && (!includeDeleted)) {
						shouldAdd = false;
					}
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = column.getValue();
					}
				}
				if ((shouldAdd) && (json != null)) {
					pager.process(json);
				}
			}
			return pager;
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning device network rows.", e);
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
	 * @param hbase
	 * @param tableName
	 * @param token
	 * @param force
	 * @param builder
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static <T extends MetadataProviderEntity> T delete(ISiteWhereHBaseClient hbase, byte[] tableName,
			String token, boolean force, IRowKeyBuilder builder, Class<T> type) throws SiteWhereException {
		T existing = get(hbase, tableName, token, builder, type);
		existing.setDeleted(true);

		byte[] primary = builder.buildPrimaryKey(token);
		if (force) {
			builder.deleteReference(token);
			HTableInterface table = null;
			try {
				Delete delete = new Delete(primary);
				table = hbase.getTableInterface(tableName);
				table.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete data for token: " + token, e);
			} finally {
				HBaseUtils.closeCleanly(table);
			}
		} else {
			byte[] marker = { (byte) 0x01 };
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = MarshalUtils.marshalJson(existing);

			HTableInterface devices = null;
			try {
				devices = hbase.getTableInterface(tableName);
				Put put = new Put(primary);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, updated);
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