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

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Base58;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.hbase.common.Pager;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
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

	/** Size of a row in milliseconds */
	private static final long ROW_IN_MS = (1 << 24);

	/**
	 * Create a new device measurements entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @param updateState
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceMeasurements createDeviceMeasurements(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceMeasurementsCreateRequest request, boolean updateState,
			IDeviceManagementCacheProvider cache) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignment.getToken());
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		byte[] rowkey = getRowKey(assnKey, time);
		byte[] qualifier = getQualifier(EventRecordType.Measurement, time);

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

		// Update state if requested.
		if (updateState) {
			DeviceAssignmentState updated =
					SiteWherePersistence.assignmentStateMeasurementsUpdateLogic(assignment, measurements);
			HBaseDeviceAssignment.updateDeviceAssignmentState(hbase, assignment.getToken(), updated, cache);
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
		Pager<EventMatch> matches =
				getEventRowsForAssignment(hbase, assnToken, EventRecordType.Measurement, criteria);
		return convertMatches(matches);
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
		Pager<EventMatch> matches =
				getEventRowsForSite(hbase, siteToken, EventRecordType.Measurement, criteria);
		return convertMatches(matches);
	}

	/**
	 * Create a new device location entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @param updateState
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceLocation createDeviceLocation(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceLocationCreateRequest request, boolean updateState,
			IDeviceManagementCacheProvider cache) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] rowkey = getEventRowKey(assignment, time);
		byte[] qualifier = getQualifier(EventRecordType.Location, time);

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

		// Update state if requested.
		if (updateState) {
			DeviceAssignmentState updated =
					SiteWherePersistence.assignmentStateLocationUpdateLogic(assignment, location);
			HBaseDeviceAssignment.updateDeviceAssignmentState(hbase, assignment.getToken(), updated, cache);
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
		Pager<EventMatch> matches =
				getEventRowsForAssignment(hbase, assnToken, EventRecordType.Location, criteria);
		return convertMatches(matches);
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
		Pager<EventMatch> matches = getEventRowsForSite(hbase, siteToken, EventRecordType.Location, criteria);
		return convertMatches(matches);
	}

	/**
	 * Create a new device alert entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @param updateState
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceAlert createDeviceAlert(ISiteWhereHBaseClient hbase, IDeviceAssignment assignment,
			IDeviceAlertCreateRequest request, boolean updateState, IDeviceManagementCacheProvider cache)
			throws SiteWhereException {
		long time = getEventTime(request);
		byte[] rowkey = getEventRowKey(assignment, time);
		byte[] qualifier = getQualifier(EventRecordType.Alert, time);

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

		// Update state if requested.
		if (updateState) {
			DeviceAssignmentState updated =
					SiteWherePersistence.assignmentStateAlertUpdateLogic(assignment, alert);
			HBaseDeviceAssignment.updateDeviceAssignmentState(hbase, assignment.getToken(), updated, cache);
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
		Pager<EventMatch> matches =
				getEventRowsForAssignment(hbase, assnToken, EventRecordType.Alert, criteria);
		return convertMatches(matches);
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
		Pager<EventMatch> matches = getEventRowsForSite(hbase, siteToken, EventRecordType.Alert, criteria);
		return convertMatches(matches);
	}

	/**
	 * Create a new device command invocation entry for an assignment.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param command
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommandInvocation createDeviceCommandInvocation(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceCommand command,
			IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] rowkey = getEventRowKey(assignment, time);
		byte[] qualifier = getQualifier(EventRecordType.CommandInvocation, time);

		// Create a command invocation and marshal to JSON.
		DeviceCommandInvocation ci =
				SiteWherePersistence.deviceCommandInvocationCreateLogic(assignment, command, request);
		String id = getEncodedEventId(rowkey, qualifier);
		ci.setId(id);
		byte[] json = MarshalUtils.marshalJson(ci);

		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, qualifier, json);
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create command invocation.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}

		return ci;
	}

	/**
	 * Get a {@link IDeviceEvent} by unique id.
	 * 
	 * @param hbase
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceEvent getDeviceEvent(ISiteWhereHBaseClient hbase, String id)
			throws SiteWhereException {
		return getEventById(hbase, id);
	}

	/**
	 * List command invocations associated with an assignment based on the given criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(
			ISiteWhereHBaseClient hbase, String assnToken, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Pager<EventMatch> matches =
				getEventRowsForAssignment(hbase, assnToken, EventRecordType.CommandInvocation, criteria);
		return convertMatches(matches);
	}

	/**
	 * List device command invocations associated with a site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(
			ISiteWhereHBaseClient hbase, String siteToken, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Pager<EventMatch> matches =
				getEventRowsForSite(hbase, siteToken, EventRecordType.CommandInvocation, criteria);
		return convertMatches(matches);
	}

	/**
	 * Create a device state change event.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceStateChange createDeviceStateChange(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		long time = getEventTime(request);
		byte[] rowkey = getEventRowKey(assignment, time);
		byte[] qualifier = getQualifier(EventRecordType.StateChange, time);

		// Create a state change and marshal to JSON.
		DeviceStateChange ci = SiteWherePersistence.deviceStateChangeCreateLogic(assignment, request);
		String id = getEncodedEventId(rowkey, qualifier);
		ci.setId(id);
		byte[] json = MarshalUtils.marshalJson(ci);

		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, qualifier, json);
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create state change.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}

		return ci;
	}

	/**
	 * List state changes associated with an assignment based on the given criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceStateChange> listDeviceStateChanges(ISiteWhereHBaseClient hbase,
			String assnToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
		Pager<EventMatch> matches =
				getEventRowsForAssignment(hbase, assnToken, EventRecordType.StateChange, criteria);
		return convertMatches(matches);
	}

	/**
	 * List device state changes associated with a site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceStateChange> listDeviceStateChangesForSite(
			ISiteWhereHBaseClient hbase, String siteToken, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Pager<EventMatch> matches =
				getEventRowsForSite(hbase, siteToken, EventRecordType.StateChange, criteria);
		return convertMatches(matches);
	}

	/**
	 * Create a device command response.
	 * 
	 * @param hbase
	 * @param assignment
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommandResponse createDeviceCommandResponse(ISiteWhereHBaseClient hbase,
			IDeviceAssignment assignment, IDeviceCommandResponseCreateRequest request)
			throws SiteWhereException {
		long time = getEventTime(request);
		byte[] rowkey = getEventRowKey(assignment, time);
		byte[] qualifier = getQualifier(EventRecordType.CommandResponse, time);

		// Create a state change and marshal to JSON.
		DeviceCommandResponse cr = SiteWherePersistence.deviceCommandResponseCreateLogic(assignment, request);
		String id = getEncodedEventId(rowkey, qualifier);
		cr.setId(id);
		byte[] json = MarshalUtils.marshalJson(cr);

		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Put put = new Put(rowkey);
			put.add(ISiteWhereHBase.FAMILY_ID, qualifier, json);
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create command response.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}

		linkDeviceCommandResponseToInvocation(hbase, cr);
		return cr;
	}

	/**
	 * Creates a link to the command response by incrementing(or creating) a counter and
	 * sequential entries under the original invocation. TODO: Note that none of this is
	 * transactional, so it is currently possible for responses not to be correctly linked
	 * back to the original invocation if the calls in this method fail.
	 * 
	 * @param hbase
	 * @param response
	 * @param responseRow
	 * @param responseQual
	 * @throws SiteWhereException
	 */
	protected static void linkDeviceCommandResponseToInvocation(ISiteWhereHBaseClient hbase,
			IDeviceCommandResponse response) throws SiteWhereException {
		String originator = response.getOriginatingEventId();
		if (originator == null) {
			return;
		}

		HTableInterface events = null;
		KeyValue okeys = null;
		try {
			okeys = getDecodedEventId(originator);
		} catch (SiteWhereException e) {
			throw new SiteWhereException("Originating event id is invalid.", e);
		}

		byte[] qual = okeys.getQualifier();
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);

			// Increment the result counter.
			qual[3] = EventRecordType.CommandResponseCounter.getType();
			long counter = events.incrementColumnValue(okeys.getRow(), ISiteWhereHBase.FAMILY_ID, qual, 1);
			byte[] counterBytes = Bytes.toBytes(counter);

			// Add new response entry row under the invocation.
			qual[3] = EventRecordType.CommandResponseEntry.getType();
			ByteBuffer seqkey = ByteBuffer.allocate(qual.length + counterBytes.length);
			seqkey.put(qual);
			seqkey.put(counterBytes);

			Put put = new Put(okeys.getRow());
			put.add(ISiteWhereHBase.FAMILY_ID, seqkey.array(), response.getId().getBytes());
			events.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to link command response.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}
	}

	/**
	 * Find responses associated with a device command invocation.
	 * 
	 * @param hbase
	 * @param invocationId
	 * @return
	 * @throws SiteWhereException
	 */
	protected static SearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(
			ISiteWhereHBaseClient hbase, String invocationId) throws SiteWhereException {
		KeyValue ikeys = getDecodedEventId(invocationId);

		HTableInterface events = null;
		List<IDeviceCommandResponse> responses = new ArrayList<IDeviceCommandResponse>();
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);

			Get get = new Get(ikeys.getRow());
			Result result = events.get(get);
			Map<byte[], byte[]> cells = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);

			byte[] match = ikeys.getQualifier();
			match[3] = EventRecordType.CommandResponseEntry.getType();
			for (byte[] qual : cells.keySet()) {
				if ((qual[0] == match[0]) && (qual[1] == match[1]) && (qual[2] == match[2])
						&& (qual[3] == match[3])) {
					byte[] value = cells.get(qual);
					String responseId = new String(value);
					responses.add(getDeviceCommandResponse(hbase, responseId));
				}
			}
			return new SearchResults<IDeviceCommandResponse>(responses);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to link command response.", e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}
	}

	/**
	 * List command responses associated with an assignment based on the given criteria.
	 * 
	 * @param hbase
	 * @param assnToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceCommandResponse> listDeviceCommandResponses(
			ISiteWhereHBaseClient hbase, String assnToken, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Pager<EventMatch> matches =
				getEventRowsForAssignment(hbase, assnToken, EventRecordType.CommandResponse, criteria);
		return convertMatches(matches);
	}

	/**
	 * Get a {@link IDeviceCommandResponse} by unique id.
	 * 
	 * @param hbase
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public static IDeviceCommandResponse getDeviceCommandResponse(ISiteWhereHBaseClient hbase, String id)
			throws SiteWhereException {
		IDeviceEvent event = getEventById(hbase, id);
		if (event instanceof IDeviceCommandResponse) {
			return (IDeviceCommandResponse) event;
		}
		throw new SiteWhereException("Event is not a command response.");
	}

	/**
	 * List device command responses associated with a site.
	 * 
	 * @param hbase
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(
			ISiteWhereHBaseClient hbase, String siteToken, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		Pager<EventMatch> matches =
				getEventRowsForSite(hbase, siteToken, EventRecordType.CommandResponse, criteria);
		return convertMatches(matches);
	}

	/**
	 * Get the event row key bytes.
	 * 
	 * @param assignment
	 * @param time
	 * @return
	 * @throws SiteWhereException
	 */
	protected static byte[] getEventRowKey(IDeviceAssignment assignment, long time) throws SiteWhereException {
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assignment.getToken());
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		return getRowKey(assnKey, time);
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
	protected static Pager<EventMatch> getEventRowsForAssignment(ISiteWhereHBaseClient hbase,
			String assnToken, EventRecordType eventType, IDateRangeSearchCriteria criteria)
			throws SiteWhereException {
		byte[] assnKey = IdManager.getInstance().getAssignmentKeys().getValue(assnToken);
		if (assnKey == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}

		// Note: Because time values are inverted, start and end keys are reversed.
		byte[] startKey = null, endKey = null;
		if (criteria.getEndDate() != null) {
			startKey = getRowKey(assnKey, criteria.getEndDate().getTime());
		} else {
			startKey = getAbsoluteStartKey(assnKey);
		}
		if (criteria.getStartDate() != null) {
			endKey = getRowKey(assnKey, criteria.getStartDate().getTime() - ROW_IN_MS);
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

			List<EventMatch> matches = new ArrayList<EventMatch>();
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
						EventRecordType type = EventRecordType.decode(qual[3]);
						matches.add(new EventMatch(type, eventDate, value));
					}
				}
			}
			Collections.sort(matches, Collections.reverseOrder());
			Pager<EventMatch> pager = new Pager<EventMatch>(criteria);
			for (EventMatch match : matches) {
				pager.process(match);
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
		work[0] = (byte) ~key[7];
		work[1] = (byte) ~key[8];
		work[2] = (byte) ~key[9];
		work[3] = (byte) ~key[10];
		work[4] = (byte) ~key[11];
		work[5] = (byte) ~qualifier[0];
		work[6] = (byte) ~qualifier[1];
		work[7] = (byte) ~qualifier[2];
		long time = Bytes.toLong(work);
		return new Date(time);
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
	protected static Pager<EventMatch> getEventRowsForSite(ISiteWhereHBaseClient hbase, String siteToken,
			EventRecordType eventType, IDateRangeSearchCriteria criteria) throws SiteWhereException {
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

			List<EventMatch> matches = new ArrayList<EventMatch>();
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
							EventRecordType type = EventRecordType.decode(qual[3]);
							matches.add(new EventMatch(type, eventDate, value));
						}
					}
				}
			}
			Collections.sort(matches, Collections.reverseOrder());
			Pager<EventMatch> pager = new Pager<EventMatch>(criteria);
			for (EventMatch match : matches) {
				pager.process(match);
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
	private static class EventMatch implements Comparable<EventMatch> {

		private EventRecordType type;

		private Date date;

		private byte[] json;

		public EventMatch(EventRecordType type, Date date, byte[] json) {
			this.type = type;
			this.date = date;
			this.json = json;
		}

		protected EventRecordType getType() {
			return type;
		}

		protected Date getDate() {
			return date;
		}

		protected byte[] getJson() {
			return json;
		}

		public int compareTo(EventMatch other) {
			return this.getDate().compareTo(other.getDate());
		}
	}

	/**
	 * Converts matching rows to {@link SearchResults} for web service response.
	 * 
	 * @param matches
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	protected static <I extends IDeviceEvent> SearchResults<I> convertMatches(Pager<EventMatch> matches)
			throws SiteWhereException {
		ObjectMapper mapper = new ObjectMapper();
		List<I> results = new ArrayList<I>();
		for (EventMatch match : matches.getResults()) {
			Class<? extends IDeviceEvent> marshaller = getEventClassForIndicator(match.getType().getType());
			try {
				I event = (I) mapper.readValue(match.getJson(), marshaller);
				results.add(event);
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
		byte[] bucketBytes = Bytes.toBytes(time);
		ByteBuffer buffer = ByteBuffer.allocate(assnKey.length + 5);
		buffer.put(assnKey);
		buffer.put((byte) ~bucketBytes[0]);
		buffer.put((byte) ~bucketBytes[1]);
		buffer.put((byte) ~bucketBytes[2]);
		buffer.put((byte) ~bucketBytes[3]);
		buffer.put((byte) ~bucketBytes[4]);
		return buffer.array();
	}

	/**
	 * Get column qualifier for storing the event.
	 * 
	 * @param type
	 * @param time
	 * @return
	 */
	public static byte[] getQualifier(EventRecordType eventType, long time) {
		byte[] offsetBytes = Bytes.toBytes(time);
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
		byte[] bytes = buffer.array();
		return Base58.encode(bytes);
	}

	/**
	 * Decodes an event id into a {@link KeyValue} that can be used to access the data in
	 * HBase.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public static KeyValue getDecodedEventId(String id) throws SiteWhereException {
		int rowLength =
				HBaseSite.SITE_IDENTIFIER_LENGTH + 1 + HBaseDeviceAssignment.ASSIGNMENT_IDENTIFIER_LENGTH + 5;
		int qualLength = 4;
		try {
			byte[] decoded = Base58.decode(id);
			if (decoded.length != (rowLength + qualLength)) {
				LOGGER.error("Event id not in expected internal format.");
				return null;
			}
			byte[] row = new byte[rowLength];
			System.arraycopy(decoded, 0, row, 0, rowLength);
			byte[] qual = new byte[qualLength];
			System.arraycopy(decoded, rowLength, qual, 0, qualLength);
			return new KeyValue(row, ISiteWhereHBase.FAMILY_ID, qual);
		} catch (AddressFormatException e) {
			LOGGER.error("Unable to decode event id.", e);
			return null;
		}
	}

	/**
	 * Gets an event by unique id.
	 * 
	 * @param hbase
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	protected static IDeviceEvent getEventById(ISiteWhereHBaseClient hbase, String id)
			throws SiteWhereException {
		KeyValue keys = getDecodedEventId(id);
		if (keys == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceEventId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		HTableInterface events = null;
		try {
			events = hbase.getTableInterface(ISiteWhereHBase.EVENTS_TABLE_NAME);
			Get get = new Get(keys.getRow());
			get.addColumn(ISiteWhereHBase.FAMILY_ID, keys.getQualifier());
			Result result = events.get(get);
			byte type = keys.getQualifier()[3];
			Class<? extends IDeviceEvent> eventClass = getEventClassForIndicator(type);

			if (result != null) {
				byte[] json = result.getValue(ISiteWhereHBase.FAMILY_ID, keys.getQualifier());
				if (json != null) {
					return MarshalUtils.unmarshalJson(json, eventClass);
				}
			}
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceEventId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException e) {
			throw new SiteWhereException(e);
		} finally {
			HBaseUtils.closeCleanly(events);
		}
	}

	/**
	 * Get the REST wrapper class that can be used to unmarshal JSON.
	 * 
	 * @param indicator
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Class<? extends IDeviceEvent> getEventClassForIndicator(byte indicator)
			throws SiteWhereException {
		EventRecordType eventType = EventRecordType.decode(indicator);
		switch (eventType) {
		case Measurement: {
			return DeviceMeasurements.class;
		}
		case Location: {
			return DeviceLocation.class;
		}
		case Alert: {
			return DeviceAlert.class;
		}
		case CommandInvocation: {
			return DeviceCommandInvocation.class;
		}
		case CommandResponse: {
			return DeviceCommandResponse.class;
		}
		case StateChange: {
			return DeviceStateChange.class;
		}
		default: {
			throw new SiteWhereException("Id references unknown event type.");
		}
		}
	}
}