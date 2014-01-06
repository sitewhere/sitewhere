/*
 * HBaseSite.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.WritableByteArrayComparable;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere sites.
 * 
 * @author Derek
 */
public class HBaseSite {

	/** Length of site identifier (subset of 8 byte long) */
	public static final int SITE_IDENTIFIER_LENGTH = 2;

	/** Column qualifier for zone counter */
	public static final byte[] ZONE_COUNTER = Bytes.toBytes("zonectr");

	/** Column qualifier for assignment counter */
	public static final byte[] ASSIGNMENT_COUNTER = Bytes.toBytes("assnctr");

	/** Regex for getting site rows */
	public static final String REGEX_SITE = "^.{2}$";

	/**
	 * Create a new site.
	 * 
	 * @param hbase
	 * @param uids
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static ISite createSite(ISiteWhereHBaseClient hbase, ISiteCreateRequest request)
			throws SiteWhereException {
		String uuid = IdManager.getInstance().getSiteKeys().createUniqueId();
		Long value = IdManager.getInstance().getSiteKeys().getValue(uuid);
		byte[] primary = getPrimaryRowkey(value);

		// Use common logic so all backend implementations work the same.
		Site site = SiteWherePersistence.siteCreateLogic(request, uuid);

		// Create primary site record.
		byte[] json = MarshalUtils.marshalJson(site);
		byte[] maxLong = Bytes.toBytes(Long.MAX_VALUE);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			put.add(ISiteWhereHBase.FAMILY_ID, ZONE_COUNTER, maxLong);
			put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_COUNTER, maxLong);
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create site.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
		return site;
	}

	/**
	 * Get a site based on unique token.
	 * 
	 * @param hbase
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static Site getSiteByToken(ISiteWhereHBaseClient hbase, String token) throws SiteWhereException {
		Long siteId = IdManager.getInstance().getSiteKeys().getValue(token);
		if (siteId == null) {
			return null;
		}
		byte[] primary = getPrimaryRowkey(siteId);
		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Get get = new Get(primary);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = sites.get(get);
			if (result.size() != 1) {
				throw new SiteWhereException("Expected one JSON entry for site and found: " + result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), Site.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load site by token.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * Update information for an existing site.
	 * 
	 * @param hbase
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static Site updateSite(ISiteWhereHBaseClient hbase, String token, ISiteCreateRequest request)
			throws SiteWhereException {
		Site updated = getSiteByToken(hbase, token);
		if (updated == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.siteUpdateLogic(request, updated);

		Long siteId = IdManager.getInstance().getSiteKeys().getValue(token);
		byte[] rowkey = getPrimaryRowkey(siteId);
		byte[] json = MarshalUtils.marshalJson(updated);

		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			sites.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to update site.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
		return updated;
	}

	/**
	 * List all sites that match the given criteria.
	 * 
	 * @param hbase
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<ISite> listSites(ISiteWhereHBaseClient hbase, ISearchCriteria criteria)
			throws SiteWhereException {
		RegexStringComparator comparator = new RegexStringComparator(REGEX_SITE);
		Pager<byte[]> pager = getFilteredSiteRows(hbase, false, criteria, comparator, null, null);
		List<ISite> response = new ArrayList<ISite>();
		for (byte[] match : pager.getResults()) {
			response.add(MarshalUtils.unmarshalJson(match, Site.class));
		}
		return new SearchResults<ISite>(response, pager.getTotal());
	}

	/**
	 * List device assignments for a given site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAssignment> listDeviceAssignmentsForSite(ISiteWhereHBaseClient hbase,
			String siteToken, ISearchCriteria criteria) throws SiteWhereException {
		Long siteId = IdManager.getInstance().getSiteKeys().getValue(siteToken);
		if (siteId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		byte[] assnPrefix = getAssignmentRowKey(siteId);
		byte[] after = getAfterAssignmentRowKey(siteId);
		BinaryPrefixComparator comparator = new BinaryPrefixComparator(assnPrefix);
		Pager<byte[]> pager = getFilteredSiteRows(hbase, false, criteria, comparator, assnPrefix, after);
		List<IDeviceAssignment> response = new ArrayList<IDeviceAssignment>();
		for (byte[] match : pager.getResults()) {
			response.add(MarshalUtils.unmarshalJson(match, DeviceAssignment.class));
		}
		return new SearchResults<IDeviceAssignment>(response, pager.getTotal());
	}

	/**
	 * List zones for a given site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IZone> listZonesForSite(ISiteWhereHBaseClient hbase, String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		Long siteId = IdManager.getInstance().getSiteKeys().getValue(siteToken);
		if (siteId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		byte[] zonePrefix = getZoneRowKey(siteId);
		byte[] after = getAssignmentRowKey(siteId);
		BinaryPrefixComparator comparator = new BinaryPrefixComparator(zonePrefix);
		Pager<byte[]> pager = getFilteredSiteRows(hbase, false, criteria, comparator, zonePrefix, after);
		List<IZone> response = new ArrayList<IZone>();
		for (byte[] match : pager.getResults()) {
			response.add(MarshalUtils.unmarshalJson(match, Zone.class));
		}
		return new SearchResults<IZone>(response, pager.getTotal());
	}

	/**
	 * Get json associated with various rows in the site table based on regex filters.
	 * 
	 * @param hbase
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static Pager<byte[]> getFilteredSiteRows(ISiteWhereHBaseClient hbase, boolean includeDeleted,
			ISearchCriteria criteria, WritableByteArrayComparable comparator, byte[] startRow, byte[] stopRow)
			throws SiteWhereException {
		HTableInterface sites = null;
		ResultScanner scanner = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			RowFilter matcher = new RowFilter(CompareOp.EQUAL, comparator);
			Scan scan = new Scan();
			if (startRow != null) {
				scan.setStartRow(startRow);
			}
			if (stopRow != null) {
				scan.setStopRow(stopRow);
			}
			scan.setFilter(matcher);
			scanner = sites.getScanner(scan);

			Pager<byte[]> pager = new Pager<byte[]>(criteria);
			for (Result result : scanner) {
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
			throw new SiteWhereException("Error scanning site rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * Delete an existing site.
	 * 
	 * @param hbase
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static Site deleteSite(ISiteWhereHBaseClient hbase, String token, boolean force)
			throws SiteWhereException {
		Site existing = getSiteByToken(hbase, token);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		existing.setDeleted(true);

		Long siteId = IdManager.getInstance().getSiteKeys().getValue(token);
		byte[] rowkey = getPrimaryRowkey(siteId);
		if (force) {
			IdManager.getInstance().getSiteKeys().delete(token);
			HTableInterface sites = null;
			try {
				Delete delete = new Delete(rowkey);
				sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				sites.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete site.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		} else {
			byte[] marker = { (byte) 0x01 };
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = MarshalUtils.marshalJson(existing);
			HTableInterface sites = null;
			try {
				sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
				Put put = new Put(rowkey);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, updated);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
				sites.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to set deleted flag for site.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		}
		return existing;
	}

	/**
	 * Allocate the next zone id and return the new value. (Each id is less than the last)
	 * 
	 * @param hbase
	 * @param siteId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextZoneId(ISiteWhereHBaseClient hbase, Long siteId) throws SiteWhereException {
		byte[] primary = getPrimaryRowkey(siteId);
		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Increment increment = new Increment(primary);
			increment.addColumn(ISiteWhereHBase.FAMILY_ID, ZONE_COUNTER, -1);
			Result result = sites.increment(increment);
			return Bytes.toLong(result.value());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to allocate next zone id.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * Allocate the next assignment id and return the new value. (Each id is less than the
	 * last)
	 * 
	 * @param hbase
	 * @param siteId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextAssignmentId(ISiteWhereHBaseClient hbase, Long siteId)
			throws SiteWhereException {
		byte[] primary = getPrimaryRowkey(siteId);
		HTableInterface sites = null;
		try {
			sites = hbase.getTableInterface(ISiteWhereHBase.SITES_TABLE_NAME);
			Increment increment = new Increment(primary);
			increment.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_COUNTER, -1);
			Result result = sites.increment(increment);
			return Bytes.toLong(result.value());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to allocate next assignment id.", e);
		} finally {
			HBaseUtils.closeCleanly(sites);
		}
	}

	/**
	 * Get the unique site identifier based on the long value associated with the site
	 * UUID. This will be a subset of the full 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getSiteIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[SITE_IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - SITE_IDENTIFIER_LENGTH, result, 0, SITE_IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Get primary row key for a given site.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getPrimaryRowkey(Long siteId) {
		byte[] sid = getSiteIdentifier(siteId);
		ByteBuffer rowkey = ByteBuffer.allocate(sid.length);
		rowkey.put(sid);
		return rowkey.array();
	}

	/**
	 * Get zone row key for a given site.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getZoneRowKey(Long siteId) {
		byte[] sid = getSiteIdentifier(siteId);
		ByteBuffer rowkey = ByteBuffer.allocate(sid.length + 1);
		rowkey.put(sid);
		rowkey.put(SiteRecordType.Zone.getType());
		return rowkey.array();
	}

	/**
	 * Get device assignment row key for a given site.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getAssignmentRowKey(Long siteId) {
		byte[] sid = getSiteIdentifier(siteId);
		ByteBuffer rowkey = ByteBuffer.allocate(sid.length + 1);
		rowkey.put(sid);
		rowkey.put(SiteRecordType.Assignment.getType());
		return rowkey.array();
	}

	/**
	 * Get key that marks finish of assignment records for a site.
	 * 
	 * @param siteId
	 * @return
	 */
	public static byte[] getAfterAssignmentRowKey(Long siteId) {
		byte[] sid = getSiteIdentifier(siteId);
		ByteBuffer rowkey = ByteBuffer.allocate(sid.length + 1);
		rowkey.put(sid);
		rowkey.put(SiteRecordType.End.getType());
		return rowkey.array();
	}
}
