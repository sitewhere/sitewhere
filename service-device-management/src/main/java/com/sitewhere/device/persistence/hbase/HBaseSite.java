/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.hbase;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere sites.
 * 
 * @author Derek
 */
public class HBaseSite {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

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
    public static ISite createSite(IHBaseContext context, ISiteCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Site site = DeviceManagementPersistence.siteCreateLogic(request);

	Long value = context.getDeviceIdManager().getSiteKeys().getNextCounterValue();
	context.getDeviceIdManager().getSiteKeys().create(site.getToken(), value);

	byte[] primary = getPrimaryRowkey(value);

	// Create primary site record.
	byte[] payload = context.getPayloadMarshaler().encodeSite(site);
	byte[] maxLong = Bytes.toBytes(Long.MAX_VALUE);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(primary);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ZONE_COUNTER, maxLong);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, ASSIGNMENT_COUNTER, maxLong);
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
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static Site getSiteByToken(IHBaseContext context, String token) throws SiteWhereException {
	Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(token);
	if (siteId == null) {
	    return null;
	}
	byte[] primary = getPrimaryRowkey(siteId);
	Table sites = null;
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

	    return PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeSite(payload);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to load site by token.", e);
	} finally {
	    HBaseUtils.closeCleanly(sites);
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
    public static Site updateSite(IHBaseContext context, Site site, ISiteCreateRequest request)
	    throws SiteWhereException {
	Site updated = getSiteByToken(context, site.getToken());
	if (updated == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}

	// Use common update logic so that backend implemetations act the
	// same way.
	DeviceManagementPersistence.siteUpdateLogic(request, updated);

	Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(site.getToken());
	byte[] rowkey = getPrimaryRowkey(siteId);
	byte[] payload = context.getPayloadMarshaler().encodeSite(updated);

	Table sites = null;
	try {
	    sites = getSitesTableInterface(context);
	    Put put = new Put(rowkey);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
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
     * @param context
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<ISite> listSites(IHBaseContext context, ISearchCriteria criteria)
	    throws SiteWhereException {
	RegexStringComparator comparator = new RegexStringComparator(REGEX_SITE);
	Pager<ISite> pager = getFilteredSiteRows(context, false, criteria, comparator, null, null, Site.class,
		ISite.class);
	return new SearchResults<ISite>(pager.getResults());
    }

    /**
     * List device assignments for a given site.
     * 
     * @param context
     * @param site
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceAssignment> listDeviceAssignmentsForSite(IHBaseContext context, ISite site,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	Table sites = null;
	ResultScanner scanner = null;
	try {
	    Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(site.getToken());
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
		// TODO: This is inefficient. There should be a filter on the
		// scanner instead.
		if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
		    continue;
		}
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		byte[] state = result.getValue(ISiteWhereHBase.FAMILY_ID, HBaseDeviceAssignment.ASSIGNMENT_STATE);

		if ((type != null) && (payload != null)) {
		    DeviceAssignment assignment = (DeviceAssignment) PayloadMarshalerResolver.getInstance()
			    .getMarshaler(type).decode(payload, DeviceAssignment.class);
		    if (state != null) {
			// DeviceAssignmentState assnState =
			// PayloadMarshalerResolver.getInstance().getMarshaler(type)
			// .decodeDeviceAssignmentState(state);
			// assignment.setState(assnState);
		    }
		    if ((criteria.getStatus() == null) || (criteria.getStatus().equals(assignment.getStatus()))) {
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
	}
    }

    /**
     * List device assignments for a site that have state attached and have a last
     * interaction date within a given date range. TODO: This is not efficient since
     * it iterates through all assignments for a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceAssignment> listDeviceAssignmentsWithLastInteraction(IHBaseContext context,
	    String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Table sites = null;
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
		// TODO: This is inefficient. There should be a filter on the
		// scanner instead.
		if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
		    continue;
		}
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		byte[] state = result.getValue(ISiteWhereHBase.FAMILY_ID, HBaseDeviceAssignment.ASSIGNMENT_STATE);

		if ((type != null) && (payload != null)) {
		    @SuppressWarnings("unused")
		    DeviceAssignment assignment = (DeviceAssignment) PayloadMarshalerResolver.getInstance()
			    .getMarshaler(type).decode(payload, DeviceAssignment.class);
		    if (state != null) { // TODO: Fix assignment state.
			// DeviceAssignmentState assnState =
			// PayloadMarshalerResolver.getInstance().getMarshaler(type)
			// .decodeDeviceAssignmentState(state);
			// assignment.setState(assnState);
			// if (assignment.getState().getLastInteractionDate() !=
			// null) {
			// Date last =
			// assignment.getState().getLastInteractionDate();
			// if ((criteria.getStartDate() != null) &&
			// (criteria.getStartDate().after(last))) {
			// continue;
			// }
			// if ((criteria.getEndDate() != null) &&
			// (criteria.getEndDate().before(last))) {
			// continue;
			// }
			// pager.process(assignment);
			// }
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
	}
    }

    /**
     * List all device assignments for a site that have been marked as missing.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceAssignment> listMissingDeviceAssignments(IHBaseContext context, String siteToken,
	    ISearchCriteria criteria) throws SiteWhereException {
	Table sites = null;
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
		// TODO: This is inefficient. There should be a filter on the
		// scanner
		// instead.
		if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
		    continue;
		}
		byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
		byte[] state = result.getValue(ISiteWhereHBase.FAMILY_ID, HBaseDeviceAssignment.ASSIGNMENT_STATE);

		if ((type != null) && (payload != null)) {
		    @SuppressWarnings("unused")
		    DeviceAssignment assignment = (DeviceAssignment) PayloadMarshalerResolver.getInstance()
			    .getMarshaler(type).decode(payload, DeviceAssignment.class);
		    if (state != null) {
			// DeviceAssignmentState assnState =
			// PayloadMarshalerResolver.getInstance().getMarshaler(type)
			// .decodeDeviceAssignmentState(state);
			// assignment.setState(assnState);
			// if (assignment.getState().getPresenceMissingDate() !=
			// null) {
			// pager.process(assignment);
			// }
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
	}
    }

    /**
     * List device assignments that are associated with a given asset.
     * 
     * @param context
     * @param assetReference
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceAssignment> listDeviceAssignmentsForAsset(IHBaseContext context,
	    IAssetReference assetReference, IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	Pager<IDeviceAssignment> pager = new Pager<IDeviceAssignment>(criteria);
	if (criteria.getSiteToken() != null) {
	    locateDeviceAssignmentsForAsset(context, pager, criteria.getSiteToken(), assetReference.getModule(),
		    assetReference.getId(), criteria);
	} else {
	    SearchResults<ISite> sites = HBaseSite.listSites(context, SearchCriteria.ALL);
	    for (ISite site : sites.getResults()) {
		locateDeviceAssignmentsForAsset(context, pager, site.getToken(), assetReference.getModule(),
			assetReference.getId(), criteria);
	    }
	}
	return new SearchResults<IDeviceAssignment>(pager.getResults(), pager.getTotal());
    }

    /**
     * Find assignments using the given asset type and add matching items to the
     * given pager.
     * 
     * @param context
     * @param pager
     * @param siteToken
     * @param assetModuleId
     * @param assetId
     * @param criteria
     * @throws SiteWhereException
     */
    public static void locateDeviceAssignmentsForAsset(IHBaseContext context, Pager<IDeviceAssignment> pager,
	    String siteToken, String assetModuleId, String assetId, IAssignmentsForAssetSearchCriteria criteria)
	    throws SiteWhereException {
	Table sites = null;
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

	    for (Result result : scanner) {
		// TODO: This is inefficient. There should be a filter on the
		// scanner instead.
		if (result.getRow()[7] != DeviceAssignmentRecordType.DeviceAssignment.getType()) {
		    continue;
		}
		byte[] payloadType = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);

		if ((payloadType != null) && (payload != null)) {
		    IDeviceAssignment assignment = (IDeviceAssignment) PayloadMarshalerResolver.getInstance()
			    .getMarshaler(payloadType).decode(payload, DeviceAssignment.class);
		    boolean sameAssetModule = assetModuleId.equals(assignment.getAssetReference().getModule());
		    boolean sameAssetId = assetId.equals(assignment.getAssetReference().getId());
		    boolean matchingStatus = (criteria.getStatus() == null)
			    || (criteria.getStatus() == assignment.getStatus());
		    if (sameAssetModule && sameAssetId && matchingStatus) {
			pager.process(assignment);
		    }
		}
	    }
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
     * List zones for a given site.
     * 
     * @param context
     * @param site
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IZone> listZonesForSite(IHBaseContext context, ISite site, ISearchCriteria criteria)
	    throws SiteWhereException {
	Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(site.getToken());
	if (siteId == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	byte[] zonePrefix = getZoneRowKey(siteId);
	byte[] after = getAssignmentRowKey(siteId);
	BinaryPrefixComparator comparator = new BinaryPrefixComparator(zonePrefix);
	Pager<IZone> pager = getFilteredSiteRows(context, false, criteria, comparator, zonePrefix, after, Zone.class,
		IZone.class);
	return new SearchResults<IZone>(pager.getResults());
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
	    ISearchCriteria criteria, ByteArrayComparable comparator, byte[] startRow, byte[] stopRow, Class<T> type,
	    Class<I> iface) throws SiteWhereException {
	Table sites = null;
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
		    pager.process(
			    (I) PayloadMarshalerResolver.getInstance().getMarshaler(payloadType).decode(payload, type));
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
     * @param site
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static Site deleteSite(IHBaseContext context, Site site, boolean force) throws SiteWhereException {
	if (site == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	site.setDeleted(true);

	Long siteId = context.getDeviceIdManager().getSiteKeys().getValue(site.getToken());
	byte[] rowkey = getPrimaryRowkey(siteId);
	if (force) {
	    context.getDeviceIdManager().getSiteKeys().delete(site.getToken());
	    Table sites = null;
	    try {
		Delete delete = new Delete(rowkey);
		sites = getSitesTableInterface(context);
		sites.delete(delete);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to delete site.", e);
	    } finally {
		HBaseUtils.closeCleanly(sites);
	    }
	} else {
	    byte[] marker = { (byte) 0x01 };
	    DeviceManagementPersistence.setUpdatedEntityMetadata(site);
	    byte[] updated = context.getPayloadMarshaler().encodeSite(site);
	    Table sites = null;
	    try {
		sites = getSitesTableInterface(context);
		Put put = new Put(rowkey);
		HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, updated);
		put.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
		sites.put(put);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to set deleted flag for site.", e);
	    } finally {
		HBaseUtils.closeCleanly(sites);
	    }
	}
	return site;
    }

    /**
     * Allocate the next zone id and return the new value. (Each id is less than the
     * last)
     * 
     * @param context
     * @param siteId
     * @return
     * @throws SiteWhereException
     */
    public static Long allocateNextZoneId(IHBaseContext context, Long siteId) throws SiteWhereException {
	byte[] primary = getPrimaryRowkey(siteId);
	Table sites = null;
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
    }

    /**
     * Allocate the next assignment id and return the new value. (Each id is less
     * than the last)
     * 
     * @param context
     * @param siteId
     * @return
     * @throws SiteWhereException
     */
    public static Long allocateNextAssignmentId(IHBaseContext context, Long siteId) throws SiteWhereException {
	byte[] primary = getPrimaryRowkey(siteId);
	Table sites = null;
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
    }

    /**
     * Get the unique site identifier based on the long value associated with the
     * site UUID. This will be a subset of the full 8-bit long value.
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
    protected static Table getSitesTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.SITES_TABLE_NAME);
    }
}