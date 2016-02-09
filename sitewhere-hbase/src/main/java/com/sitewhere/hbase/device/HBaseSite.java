/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.sitewhere.Tracer;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.server.debug.TracerCategory;

/**
 * HBase specifics for dealing with SiteWhere sites.
 * 
 * @author Derek
 */
public class HBaseSite {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseSite.class);

	/** Length of site identifier (subset of 8 byte long) */
	public static final int SITE_IDENTIFIER_LENGTH = 2;

	/** Column qualifier for zone counter */
	public static final byte[] ZONE_COUNTER = Bytes.toBytes("zonectr");

	/** Column qualifier for assignment counter */
	public static final byte[] ASSIGNMENT_COUNTER = Bytes.toBytes("assnctr");

	/** Regex for getting site rows */
	public static final String REGEX_SITE = "^.{2}\\x00$";

	/** Regex for getting site rows */
	public static final String REGEX_ASSIGNMENT = "^.{7}\\x00$";

	/**
	 * Create a new site.
	 * 
	 * @param context
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static ISite createSite(IHBaseContext context, ISiteCreateRequest request)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "createSite (HBase)", LOGGER);
		try {
			// Use common logic so all backend implementations work the same.
			Site site = SiteWherePersistence.siteCreateLogic(request);

			Long value = context.getDeviceIdManager().getSiteKeys().getNextCounterValue();
			context.getDeviceIdManager().getSiteKeys().create(site.getToken(), value);

			byte[] primary = getPrimaryRowkey(value);

			// Create primary site record.
			byte[] payload = context.getPayloadMarshaler().encodeSite(site);
			byte[] maxLong = Bytes.toBytes(Long.MAX_VALUE);

			HTableInterface sites = null;
			try {
				sites = getSitesTableInterface(context);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				put.add(ISiteWhereHBase.FAMILY_ID, ZONE_COUNTER, maxLong);
				put.add(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_COUNTER, maxLong);
				sites.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to create site.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
			return site;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get a site based on unique token.
	 * 
	 * @param context
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static Site getSiteByToken(IHBaseContext context, String token) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "getSiteByToken (HBase) " + token, LOGGER);
		try {
			if (context.getCacheProvider() != null) {
				ISite result = context.getCacheProvider().getSiteCache().get(token);
				if (result != null) {
					Tracer.info("Returning cached site.", LOGGER);
					return Site.copy(result);
				}
			}
			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(token);
			if (siteId == null) {
				return null;
			}
			byte[] primary = getPrimaryRowkey(siteId);
			HTableInterface sites = null;
			try {
				sites = getSitesTableInterface(context);
				Get get = new Get(primary);
				HBaseUtils.addPayloadFields(get);
				Result result = sites.get(get);

				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				if ((type == null) || (payload == null)) {
					throw new SiteWhereException("Payload fields not found for site.");
				}

				Site site = PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeSite(payload);
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getSiteCache().put(token, site);
				}
				return site;
			} catch (IOException e) {
				throw new SiteWhereException("Unable to load site by token.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Update information for an existing site.
	 * 
	 * @param context
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static Site updateSite(IHBaseContext context, String token, ISiteCreateRequest request)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "updateSite (HBase) " + token, LOGGER);
		try {
			Site updated = getSiteByToken(context, token);
			if (updated == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}

			// Use common update logic so that backend implemetations act the same way.
			SiteWherePersistence.siteUpdateLogic(request, updated);

			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(token);
			byte[] rowkey = getPrimaryRowkey(siteId);
			byte[] payload = context.getPayloadMarshaler().encodeSite(updated);

			HTableInterface sites = null;
			try {
				sites = getSitesTableInterface(context);
				Put put = new Put(rowkey);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				sites.put(put);
				if (context.getCacheProvider() != null) {
					context.getCacheProvider().getSiteCache().put(token, updated);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to update site.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
			return updated;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * List all sites that match the given criteria.
	 * 
	 * @param context
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<ISite> listSites(IHBaseContext context, ISearchCriteria criteria)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "listSites (HBase)", LOGGER);
		try {
			RegexStringComparator comparator = new RegexStringComparator(REGEX_SITE);
			Pager<ISite> pager =
					getFilteredSiteRows(context, false, criteria, comparator, null, null, Site.class,
							ISite.class);
			return new SearchResults<ISite>(pager.getResults());
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * List device assignments for a given site.
	 * 
	 * @param context
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAssignment> listDeviceAssignmentsForSite(IHBaseContext context,
			String siteToken, ISearchCriteria criteria) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall,
				"listDeviceAssignmentsForSite (HBase) " + siteToken, LOGGER);
		HTableInterface sites = null;
		ResultScanner scanner = null;
		try {
			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(siteToken);
			if (siteId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}
			byte[] assnPrefix = getAssignmentRowKey(siteId);
			byte[] after = getAfterAssignmentRowKey(siteId);

			sites = getSitesTableInterface(context);

			Scan scan = new Scan();
			scan.setStartRow(assnPrefix);
			scan.setStopRow(after);
			scanner = sites.getScanner(scan);

			Pager<IDeviceAssignment> pager = new Pager<IDeviceAssignment>(criteria);
			for (Result result : scanner) {
				// TODO: This is inefficient. There should be a filter on the scanner
				// instead.
				if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
					continue;
				}
				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				byte[] state =
						result.getValue(ISiteWhereHBase.FAMILY_ID, HBaseDeviceAssignment.ASSIGNMENT_STATE);

				if ((type != null) && (payload != null)) {
					DeviceAssignment assignment =
							(DeviceAssignment) PayloadMarshalerResolver.getInstance().getMarshaler(
									type).decode(payload, DeviceAssignment.class);
					if (state != null) {
						DeviceAssignmentState assnState =
								PayloadMarshalerResolver.getInstance().getMarshaler(
										type).decodeDeviceAssignmentState(state);
						assignment.setState(assnState);
					}
					pager.process(assignment);
				}
			}
			return new SearchResults<IDeviceAssignment>(pager.getResults(), pager.getTotal());
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning site rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(sites);
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * List device assignments for a site that have state attached and have a last
	 * interaction date within a given date range. TODO: This is not efficient since it
	 * iterates through all assignments for a site.
	 * 
	 * @param context
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAssignment> listDeviceAssignmentsWithLastInteraction(
			IHBaseContext context, String siteToken, IDateRangeSearchCriteria criteria)
					throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall,
				"listDeviceAssignmentsWithLastInteraction (HBase) " + siteToken, LOGGER);
		HTableInterface sites = null;
		ResultScanner scanner = null;
		try {
			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(siteToken);
			if (siteId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}
			byte[] assnPrefix = getAssignmentRowKey(siteId);
			byte[] after = getAfterAssignmentRowKey(siteId);

			sites = getSitesTableInterface(context);

			Scan scan = new Scan();
			scan.setStartRow(assnPrefix);
			scan.setStopRow(after);
			scanner = sites.getScanner(scan);

			Pager<IDeviceAssignment> pager = new Pager<IDeviceAssignment>(criteria);
			for (Result result : scanner) {
				// TODO: This is inefficient. There should be a filter on the scanner
				// instead.
				if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
					continue;
				}
				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				byte[] state =
						result.getValue(ISiteWhereHBase.FAMILY_ID, HBaseDeviceAssignment.ASSIGNMENT_STATE);

				if ((type != null) && (payload != null)) {
					DeviceAssignment assignment =
							(DeviceAssignment) PayloadMarshalerResolver.getInstance().getMarshaler(
									type).decode(payload, DeviceAssignment.class);
					if (state != null) {
						DeviceAssignmentState assnState =
								PayloadMarshalerResolver.getInstance().getMarshaler(
										type).decodeDeviceAssignmentState(state);
						assignment.setState(assnState);
						if (assignment.getState().getLastInteractionDate() != null) {
							Date last = assignment.getState().getLastInteractionDate();
							if ((criteria.getStartDate() != null) && (criteria.getStartDate().after(last))) {
								continue;
							}
							if ((criteria.getEndDate() != null) && (criteria.getEndDate().before(last))) {
								continue;
							}
							pager.process(assignment);
						}
					}
				}
			}
			return new SearchResults<IDeviceAssignment>(pager.getResults(), pager.getTotal());
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning site rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(sites);
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * List device assignments that are associated with a given asset.
	 * 
	 * @param context
	 * @param siteToken
	 * @param assetModuleId
	 * @param assetId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAssignment> listDeviceAssignmentsForAsset(IHBaseContext context,
			String siteToken, String assetModuleId, String assetId, DeviceAssignmentStatus status,
			ISearchCriteria criteria) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall,
				"listDeviceAssignmentsForAsset (HBase) " + siteToken, LOGGER);
		HTableInterface sites = null;
		ResultScanner scanner = null;
		try {
			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(siteToken);
			if (siteId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}
			byte[] assnPrefix = getAssignmentRowKey(siteId);
			byte[] after = getAfterAssignmentRowKey(siteId);

			sites = getSitesTableInterface(context);

			Scan scan = new Scan();
			scan.setStartRow(assnPrefix);
			scan.setStopRow(after);
			scanner = sites.getScanner(scan);

			Pager<IDeviceAssignment> pager = new Pager<IDeviceAssignment>(criteria);
			for (Result result : scanner) {
				// TODO: This is inefficient. There should be a filter on the scanner
				// instead.
				if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
					continue;
				}
				byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);

				if ((payloadType != null) && (payload != null)) {
					IDeviceAssignment assignment =
							(IDeviceAssignment) PayloadMarshalerResolver.getInstance().getMarshaler(
									payloadType).decode(payload, DeviceAssignment.class);
					boolean sameAssetModule = assetModuleId.equals(assignment.getAssetModuleId());
					boolean sameAssetId = assetId.equals(assignment.getAssetId());
					boolean matchingStatus = (status == null) || (status == assignment.getStatus());
					if (sameAssetModule && sameAssetId && matchingStatus) {
						pager.process(assignment);
					}
				}
			}
			return new SearchResults<IDeviceAssignment>(pager.getResults(), pager.getTotal());
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning site rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(sites);
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * List zones for a given site.
	 * 
	 * @param context
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IZone> listZonesForSite(IHBaseContext context, String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "listZonesForSite (HBase) " + siteToken, LOGGER);
		try {
			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(siteToken);
			if (siteId == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}
			byte[] zonePrefix = getZoneRowKey(siteId);
			byte[] after = getAssignmentRowKey(siteId);
			BinaryPrefixComparator comparator = new BinaryPrefixComparator(zonePrefix);
			Pager<IZone> pager =
					getFilteredSiteRows(context, false, criteria, comparator, zonePrefix, after, Zone.class,
							IZone.class);
			return new SearchResults<IZone>(pager.getResults());
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Get filtered results from the Site table.
	 * 
	 * @param context
	 * @param includeDeleted
	 * @param criteria
	 * @param comparator
	 * @param startRow
	 * @param stopRow
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	protected static <T, I> Pager<I> getFilteredSiteRows(IHBaseContext context, boolean includeDeleted,
			ISearchCriteria criteria, ByteArrayComparable comparator, byte[] startRow, byte[] stopRow,
			Class<T> type, Class<I> iface) throws SiteWhereException {
		HTableInterface sites = null;
		ResultScanner scanner = null;
		try {
			sites = getSitesTableInterface(context);
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

			Pager<I> pager = new Pager<I>(criteria);
			for (Result result : scanner) {
				boolean shouldAdd = true;
				byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				byte[] deleted = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED);

				if ((deleted != null) && (!includeDeleted)) {
					shouldAdd = false;
				}

				if ((shouldAdd) && (payloadType != null) && (payload != null)) {
					pager.process((I) PayloadMarshalerResolver.getInstance().getMarshaler(payloadType).decode(
							payload, type));
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
	 * @param context
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static Site deleteSite(IHBaseContext context, String token, boolean force)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "deleteSite (HBase) " + token, LOGGER);
		try {
			Site existing = getSiteByToken(context, token);
			if (existing == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
			}
			existing.setDeleted(true);

			Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(token);
			byte[] rowkey = getPrimaryRowkey(siteId);
			if (force) {
				context.getDeviceIdManager().getSiteKeys().delete(token);
				HTableInterface sites = null;
				try {
					Delete delete = new Delete(rowkey);
					sites = getSitesTableInterface(context);
					sites.delete(delete);
					if (context.getCacheProvider() != null) {
						context.getCacheProvider().getSiteCache().remove(token);
					}
				} catch (IOException e) {
					throw new SiteWhereException("Unable to delete site.", e);
				} finally {
					HBaseUtils.closeCleanly(sites);
				}
			} else {
				byte[] marker = { (byte) 0x01 };
				SiteWherePersistence.setUpdatedEntityMetadata(existing);
				byte[] updated = context.getPayloadMarshaler().encodeSite(existing);
				HTableInterface sites = null;
				try {
					sites = getSitesTableInterface(context);
					Put put = new Put(rowkey);
					HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, updated);
					put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
					sites.put(put);
					if (context.getCacheProvider() != null) {
						context.getCacheProvider().getSiteCache().remove(token);
					}
				} catch (IOException e) {
					throw new SiteWhereException("Unable to set deleted flag for site.", e);
				} finally {
					HBaseUtils.closeCleanly(sites);
				}
			}
			return existing;
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Allocate the next zone id and return the new value. (Each id is less than the last)
	 * 
	 * @param context
	 * @param siteId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextZoneId(IHBaseContext context, Long siteId) throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "allocateNextZoneId (HBase)", LOGGER);
		try {
			byte[] primary = getPrimaryRowkey(siteId);
			HTableInterface sites = null;
			try {
				sites = getSitesTableInterface(context);
				Increment increment = new Increment(primary);
				increment.addColumn(ISiteWhereHBase.FAMILY_ID, ZONE_COUNTER, -1);
				Result result = sites.increment(increment);
				return Bytes.toLong(result.value());
			} catch (IOException e) {
				throw new SiteWhereException("Unable to allocate next zone id.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		} finally {
			Tracer.pop(LOGGER);
		}
	}

	/**
	 * Allocate the next assignment id and return the new value. (Each id is less than the
	 * last)
	 * 
	 * @param context
	 * @param siteId
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextAssignmentId(IHBaseContext context, Long siteId)
			throws SiteWhereException {
		Tracer.push(TracerCategory.DeviceManagementApiCall, "allocateNextAssignmentId (HBase)", LOGGER);
		try {
			byte[] primary = getPrimaryRowkey(siteId);
			HTableInterface sites = null;
			try {
				sites = getSitesTableInterface(context);
				Increment increment = new Increment(primary);
				increment.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_COUNTER, -1);
				Result result = sites.increment(increment);
				return Bytes.toLong(result.value());
			} catch (IOException e) {
				throw new SiteWhereException("Unable to allocate next assignment id.", e);
			} finally {
				HBaseUtils.closeCleanly(sites);
			}
		} finally {
			Tracer.pop(LOGGER);
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
		ByteBuffer rowkey = ByteBuffer.allocate(sid.length + 1);
		rowkey.put(sid);
		rowkey.put(SiteRecordType.Site.getType());
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

	/**
	 * Get sites table based on context.
	 * 
	 * @param context
	 * @return
	 * @throws SiteWhereException
	 */
	protected static HTableInterface getSitesTableInterface(IHBaseContext context) throws SiteWhereException {
		return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.SITES_TABLE_NAME);
	}
}