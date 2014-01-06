/*
 * HBaseDeviceEvent.java 
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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.device.DeviceAlert;
import com.sitewhere.rest.model.device.DeviceEvent;
import com.sitewhere.rest.model.device.DeviceLocation;
import com.sitewhere.rest.model.device.DeviceMeasurements;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceEvent;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere device events.
 * 
 * @author Derek
 */
public class HBaseDeviceEvent {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseDeviceEvent.class);

	/** Time interval in seconds to use for buckets */
	private static final int BUCKET_INTERVAL = 60 * 60;

	/**
	 * Create a new device measurements entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceMeasurements createDeviceMeasurements(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignment.getToken());
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		byte[] rowkey = getRowKey(assnKey, time);
		byte[] qualifier = getQualifier(DeviceAssignmentRecordType.Measurement, time);

		// Create measurements object and marshal to JSON.
		DeviceMeasurements measurements =
				SiteWherePersistence.deviceMeasurementsCreateLogic(request, assignment);
		String id = getEncodedEventId(rowkey, qualifier);
		measurements.setId(id);
		byte[] json = MarshalUtils.marshalJson(measurements);

		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, qualifier, json);
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create measurements.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}

		return measurements;
	}

	/**
	 * List measurements associated with an assignment based on the given criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceMeasurements> listDeviceMeasurements(ISiteWhereHBaseClient hbase,
			String assnToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches =
				getEventRowsForAssignment(hbase, assnToken, DeviceAssignmentRecordType.Measurement, criteria);
		return convertMatches(matches, DeviceMeasurements.class);
	}

	/**
	 * List device measurements associated with a site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(
			ISiteWhereHBaseClient hbase, String siteToken, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Pager<byte[]> matches =
				getEventRowsForSite(hbase, siteToken, DeviceAssignmentRecordType.Measurement, criteria);
		return convertMatches(matches, DeviceMeasurements.class);
	}

	/**
	 * Create a new device location entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceLocation createDeviceLocation(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceLocationCreateRequest request) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignment.getToken());
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		byte[] rowkey = getRowKey(assnKey, time);
		byte[] qualifier = getQualifier(DeviceAssignmentRecordType.Location, time);

		DeviceLocation location = SiteWherePersistence.deviceLocationCreateLogic(assignment, request);
		String id = getEncodedEventId(rowkey, qualifier);
		location.setId(id);
		byte[] json = MarshalUtils.marshalJson(location);

		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, qualifier, json);
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create location.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}

		return location;
	}

	/**
	 * List locations associated with an assignment based on the given criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceLocation> listDeviceLocations(ISiteWhereHBaseClient hbase,
			String assnToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches =
				getEventRowsForAssignment(hbase, assnToken, DeviceAssignmentRecordType.Location, criteria);
		return convertMatches(matches, DeviceLocation.class);
	}

	/**
	 * List device locations associated with a site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceLocation> listDeviceLocationsForSite(ISiteWhereHBaseClient hbase,
			String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches =
				getEventRowsForSite(hbase, siteToken, DeviceAssignmentRecordType.Location, criteria);
		return convertMatches(matches, DeviceLocation.class);
	}

	/**
	 * Create a new device alert entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceAlert createDeviceAlert(ISiteWhereHBaseClient hbase, IDeviceAssignment assignment,
			IDeviceAlertCreateRequest request) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignment.getToken());
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		byte[] rowkey = getRowKey(assnKey, time);
		byte[] qualifier = getQualifier(DeviceAssignmentRecordType.Alert, time);

		// Create alert and marshal to JSON.
		DeviceAlert alert = SiteWherePersistence.deviceAlertCreateLogic(assignment, request);
		String id = getEncodedEventId(rowkey, qualifier);
		alert.setId(id);
		byte[] json = MarshalUtils.marshalJson(alert);

		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, qualifier, json);
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create alert.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}

		return alert;
	}

	/**
	 * List alerts associated with an assignment based on the given criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAlert> listDeviceAlerts(ISiteWhereHBaseClient hbase, String assnToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches =
				getEventRowsForAssignment(hbase, assnToken, DeviceAssignmentRecordType.Alert, criteria);
		return convertMatches(matches, DeviceAlert.class);
	}

	/**
	 * List device alerts associated with a site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceAlert> listDeviceAlertsForSite(ISiteWhereHBaseClient hbase,
			String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
		Pager<byte[]> matches =
				getEventRowsForSite(hbase, siteToken, DeviceAssignmentRecordType.Alert, criteria);
		return convertMatches(matches, DeviceAlert.class);
	}

	/**
	 * Find all event rows associated with a device assignment and return cells that match
	 * the search criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param eventType
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Pager<byte[]> getEventRowsForAssignment(ISiteWhereHBaseClient hbase, String assnToken,
			DeviceAssignmentRecordType eventType, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assnToken);
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}

		// Note: Because time values are inverted, start and end keys are reversed.
		byte[] startKey = null, endKey = null;
		if (criteria.getStartDate() != null) {
			startKey = getRowKey(assnKey, criteria.getEndDate().getTime());
		} else {
			startKey = getAbsoluteStartKey(assnKey);
		}
		if (criteria.getEndDate() != null) {
			endKey = getRowKey(assnKey, criteria.getStartDate().getTime());
		} else {
			endKey = getAbsoluteEndKey(assnKey);
		}

		HTableInterface events = null;
		ResultScanner scanner = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(startKey);
			scan.setStopRow(endKey);
			scanner = events.getScanner(scan);

			Pager<byte[]> pager = new Pager<byte[]>(criteria);
			Iterator<Result> results = scanner.iterator();
			while (results.hasNext()) {
				Result current = results.next();
				Map<byte[], byte[]> cells = current.getFamilyMap(ISiteWhereHBase.FAMILY_ID);
				for (byte[] qual : cells.keySet()) {
					byte[] value = cells.get(qual);
					if ((qual.length > 3) && (qual[3] == eventType.getType())) {
						Date eventDate = getDateForEventKeyValue(current.getRow(), qual);
						if ((criteria.getStartDate() != null) && (eventDate.before(criteria.getStartDate()))) {
							continue;
						}
						if ((criteria.getEndDate() != null) && (eventDate.after(criteria.getEndDate()))) {
							continue;
						}
						pager.process(value);
					}
				}
			}
			return pager;
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning event rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(events);
		}

	}

	/**
	 * Decodes the event date encoded in the rowkey and qualifier for events.
	 * 
	 * @param kv
	 * @return
	 */
	protected static Date getDateForEventKeyValue(byte[] key, byte[] qualifier) {
		byte[] work = new byte[8];
		work[4] = (byte) ~key[7];
		work[5] = (byte) ~key[8];
		work[6] = (byte) ~key[9];
		work[7] = (byte) ~key[10];
		long base = Bytes.toLong(work);
		work = new byte[8];
		work[5] = (byte) ~qualifier[0];
		work[6] = (byte) ~qualifier[1];
		work[7] = (byte) ~qualifier[2];
		long offset = Bytes.toLong(work);
		return new Date((base + offset) * 1000);
	}

	/**
	 * Find all event rows associated with a site and return values that match the search
	 * criteria. TODO: This is not optimized at all and will take forever in cases where
	 * there are ton of assignments and events. It has to go through every record
	 * associated with the site. It works for now though.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param eventType
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Pager<byte[]> getEventRowsForSite(ISiteWhereHBaseClient hbase, String siteToken,
			DeviceAssignmentRecordType eventType, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Long siteId = IdManager.getInstance().getSiteKeys().getValue(siteToken);
		if (siteId == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		byte[] startPrefix = HBaseSite.getAssignmentRowKey(siteId);
		byte[] afterPrefix = HBaseSite.getAfterAssignmentRowKey(siteId);

		HTableInterface events = null;
		ResultScanner scanner = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(startPrefix);
			scan.setStopRow(afterPrefix);
			scanner = events.getScanner(scan);

			List<DatedByteArray> matches = new ArrayList<DatedByteArray>();
			Iterator<Result> results = scanner.iterator();
			while (results.hasNext()) {
				Result current = results.next();
				byte[] key = current.getRow();
				if (key.length > 7) {
					Map<byte[], byte[]> cells = current.getFamilyMap(ISiteWhereHBase.FAMILY_ID);
					for (byte[] qual : cells.keySet()) {
						byte[] value = cells.get(qual);
						if ((qual.length > 3) && (qual[3] == eventType.getType())) {
							Date eventDate = getDateForEventKeyValue(key, qual);
							if ((criteria.getStartDate() != null)
									&& (eventDate.before(criteria.getStartDate()))) {
								continue;
							}
							if ((criteria.getEndDate() != null) && (eventDate.after(criteria.getEndDate()))) {
								continue;
							}
							matches.add(new DatedByteArray(eventDate, value));
						}
					}
				}
			}
			Collections.sort(matches, Collections.reverseOrder());
			Pager<byte[]> pager = new Pager<byte[]>(criteria);
			for (DatedByteArray match : matches) {
				pager.process(match.getJson());
			}
			return pager;
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning event rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(events);
		}
	}

	/**
	 * Used for ordering events without having to unmarshal all of the byte arrays to do
	 * it.
	 * 
	 * @author Derek
	 */
	private static class DatedByteArray implements Comparable<DatedByteArray> {

		private Date date;

		private byte[] json;

		public DatedByteArray(Date date, byte[] json) {
			this.date = date;
			this.json = json;
		}

		protected Date getDate() {
			return date;
		}

		protected byte[] getJson() {
			return json;
		}

		public int compareTo(DatedByteArray other) {
			return this.getDate().compareTo(other.getDate());
		}
	}

	/**
	 * Converts matching rows to {@link SearchResults} for web service response.
	 * 
	 * @param matches
	 * @param jsonType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <I extends IDeviceEvent, D extends DeviceEvent> SearchResults<I> convertMatches(
			Pager<byte[]> matches, Class<D> jsonType) {
		ObjectMapper mapper = new ObjectMapper();
		List<I> results = new ArrayList<I>();
		for (byte[] jsonBytes : matches.getResults()) {
			try {
				D event = mapper.readValue(jsonBytes, jsonType);
				results.add((I) event);
			} catch (Throwable e) {
				LOGGER.error("Unable to read JSON value into event object.", e);
			}
		}
		return new SearchResults<I>(results, matches.getTotal());
	}

	/**
	 * Gets the absolute first possible event key for cases where a start timestamp is not
	 * specified.
	 * 
	 * @param assnKey
	 * @return
	 */
	protected static byte[] getAbsoluteStartKey(byte[] assnKey) {
		ByteBuffer buffer = ByteBuffer.allocate(assnKey.length + 4);
		buffer.put(assnKey);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		buffer.put((byte) 0x00);
		return buffer.array();
	}

	/**
	 * Gets the absolute first possible event key for cases where a start timestamp is not
	 * specified.
	 * 
	 * @param assnKey
	 * @return
	 */
	protected static byte[] getAbsoluteEndKey(byte[] assnKey) {
		ByteBuffer buffer = ByteBuffer.allocate(assnKey.length + 4);
		buffer.put(assnKey);
		buffer.put((byte) 0xff);
		buffer.put((byte) 0xff);
		buffer.put((byte) 0xff);
		buffer.put((byte) 0xff);
		return buffer.array();
	}

	/**
	 * Get the event time used to calculate row key and qualifier.
	 * 
	 * @param event
	 * @return
	 */
	protected static long getEventTime(IDeviceEventCreateRequest event) {
		return (event.getEventDate() != null) ? event.getEventDate().getTime() : System.currentTimeMillis();
	}

	/**
	 * Get row key for a given event type and time.
	 * 
	 * @param assnToken
	 * @param eventType
	 * @param time
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getRowKey(byte[] assnKey, long time) throws SiteWhereException {
		time = time / 1000;
		long bucket = time - (time % BUCKET_INTERVAL);
		byte[] bucketBytes = Bytes.toBytes(bucket);
		ByteBuffer buffer = ByteBuffer.allocate(assnKey.length + 4);
		buffer.put(assnKey);
		buffer.put((byte) ~bucketBytes[4]);
		buffer.put((byte) ~bucketBytes[5]);
		buffer.put((byte) ~bucketBytes[6]);
		buffer.put((byte) ~bucketBytes[7]);
		return buffer.array();
	}

	/**
	 * Get column qualifier for storing the event.
	 * 
	 * @param type
	 * @param time
	 * @return
	 */
	public static byte[] getQualifier(DeviceAssignmentRecordType eventType, long time) {
		time = time / 1000;
		long offset = time % BUCKET_INTERVAL;
		byte[] offsetBytes = Bytes.toBytes(offset);
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put((byte) ~offsetBytes[5]);
		buffer.put((byte) ~offsetBytes[6]);
		buffer.put((byte) ~offsetBytes[7]);
		buffer.put(eventType.getType());
		return buffer.array();
	}

	/**
	 * Creates a base 64 encoded String for unique event key.
	 * 
	 * @param rowkey
	 * @param qualifier
	 * @return
	 */
	public static String getEncodedEventId(byte[] rowkey, byte[] qualifier) {
		ByteBuffer buffer = ByteBuffer.allocate(rowkey.length + qualifier.length);
		buffer.put(rowkey);
		buffer.put(qualifier);
		return DatatypeConverter.printBase64Binary(buffer.array());
	}
}