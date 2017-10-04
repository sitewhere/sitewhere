/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.hbase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.bitcoin.core.Base58;
import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadEncoding;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
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
    private static Logger LOGGER = LogManager.getLogger();

    /** Size of a row in milliseconds */
    private static final long ROW_IN_MS = (1 << 24);

    /**
     * List measurements associated with an assignment based on the given
     * criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceEvent> listDeviceEvents(IHBaseContext context, String assnToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, null, criteria);
	return convertMatches(context, matches);
    }

    /**
     * Create a new device measurements entry for an assignment.
     * 
     * @param context
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceMeasurements createDeviceMeasurements(IHBaseContext context, IDeviceAssignment assignment,
	    IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
	long time = getEventTime(request);
	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assignment.getToken());
	if (assnKey == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	byte[] rowkey = getRowKey(assnKey, time);
	byte[] qualifier = getQualifier(EventRecordType.Measurement, time, context.getPayloadMarshaler().getEncoding());

	// Create measurements object and marshal to JSON.
	DeviceMeasurements measurements = DeviceEventManagementPersistence.deviceMeasurementsCreateLogic(request,
		assignment);
	String id = getEncodedEventId(rowkey, qualifier);
	measurements.setId(id);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceMeasurements(measurements);

	Put put = new Put(rowkey);
	put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	context.getDeviceEventBuffer().add(put);

	// Update state if requested.
	if (request.isUpdateState()) {
	    context.getAssignmentStateManager().addMeasurements(assignment.getToken(), measurements);
	}

	return measurements;
    }

    /**
     * List measurements associated with an assignment based on the given
     * criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceMeasurements> listDeviceMeasurements(IHBaseContext context, String assnToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, EventRecordType.Measurement,
		criteria);
	return convertMatches(context, matches);
    }

    /**
     * List device measurements associated with a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(IHBaseContext context,
	    String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForSite(context, siteToken, EventRecordType.Measurement, criteria);
	return convertMatches(context, matches);
    }

    /**
     * Create a new device location entry for an assignment.
     * 
     * @param context
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceLocation createDeviceLocation(IHBaseContext context, IDeviceAssignment assignment,
	    IDeviceLocationCreateRequest request) throws SiteWhereException {
	long time = getEventTime(request);
	byte[] rowkey = getEventRowKey(context, assignment, time);
	byte[] qualifier = getQualifier(EventRecordType.Location, time, context.getPayloadMarshaler().getEncoding());

	DeviceLocation location = DeviceEventManagementPersistence.deviceLocationCreateLogic(assignment, request);
	String id = getEncodedEventId(rowkey, qualifier);
	location.setId(id);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceLocation(location);

	Put put = new Put(rowkey);
	put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	context.getDeviceEventBuffer().add(put);

	// Update state if requested.
	if (request.isUpdateState()) {
	    context.getAssignmentStateManager().addLocation(assignment.getToken(), location);
	}

	return location;
    }

    /**
     * List locations associated with an assignment based on the given criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceLocation> listDeviceLocations(IHBaseContext context, String assnToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, EventRecordType.Location, criteria);
	return convertMatches(context, matches);
    }

    /**
     * List device locations associated with a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceLocation> listDeviceLocationsForSite(IHBaseContext context, String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForSite(context, siteToken, EventRecordType.Location, criteria);
	return convertMatches(context, matches);
    }

    /**
     * Create a new device alert entry for an assignment.
     * 
     * @param context
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceAlert createDeviceAlert(IHBaseContext context, IDeviceAssignment assignment,
	    IDeviceAlertCreateRequest request) throws SiteWhereException {
	long time = getEventTime(request);
	byte[] rowkey = getEventRowKey(context, assignment, time);
	byte[] qualifier = getQualifier(EventRecordType.Alert, time, context.getPayloadMarshaler().getEncoding());

	// Create alert and marshal to JSON.
	DeviceAlert alert = DeviceEventManagementPersistence.deviceAlertCreateLogic(assignment, request);
	String id = getEncodedEventId(rowkey, qualifier);
	alert.setId(id);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceAlert(alert);

	Put put = new Put(rowkey);
	put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	context.getDeviceEventBuffer().add(put);

	// Update state if requested.
	if (request.isUpdateState()) {
	    context.getAssignmentStateManager().addAlert(assignment.getToken(), alert);
	}

	return alert;
    }

    /**
     * List alerts associated with an assignment based on the given criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceAlert> listDeviceAlerts(IHBaseContext context, String assnToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, EventRecordType.Alert, criteria);
	return convertMatches(context, matches);
    }

    /**
     * List device alerts associated with a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceAlert> listDeviceAlertsForSite(IHBaseContext context, String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForSite(context, siteToken, EventRecordType.Alert, criteria);
	return convertMatches(context, matches);
    }

    /**
     * Create a new device stream data event for an assignment.
     * 
     * @param context
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceStreamData createDeviceStreamData(IHBaseContext context, IDeviceAssignment assignment,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	// DeviceStreamData sdata =
	// DeviceEventManagementPersistence.deviceStreamDataCreateLogic(assignment,
	// request);
	//
	// // Save data in streams table.
	// byte[] assnKey =
	// context.getDeviceIdManager().getAssignmentKeys().getValue(assignment.getToken());
	// byte[] streamKey = HBaseDeviceStream.getDeviceStreamKey(assnKey,
	// request.getStreamId());
	//
	// // Save event with reference to stream key.
	// long time = getEventTime(request);
	// byte[] eventKey = getEventRowKey(context, assignment, time);
	// byte[] qualifier = getQualifier(EventRecordType.StreamData, time,
	// context.getPayloadMarshaler().getEncoding());
	//
	// // Set the unique id.
	// String id = getEncodedEventId(eventKey, qualifier);
	// sdata.setId(id);
	//
	// // Save key rather than blob.
	// sdata.setData(streamKey);
	// byte[] payload =
	// context.getPayloadMarshaler().encodeDeviceStreamData(sdata);
	//
	// Put put = new Put(eventKey);
	// put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	// context.getDeviceEventBuffer().add(put);
	//
	// return sdata;
	throw new SiteWhereException("Fix table layout to separate device management from event management");
    }

    /**
     * List device stream data that meets the given criteria. Note that the
     * actual blob data is not stored with the event, so it has to be merged
     * into the results list on the fly.
     * 
     * @param context
     * @param assignment
     * @param streamId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceStreamData> listDeviceStreamData(IHBaseContext context,
	    IDeviceAssignment assignment, String streamId, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	// First, get data for all streams since streamId is not in the event
	// key.
	DateRangeSearchCriteria allCriteria = new DateRangeSearchCriteria(1, 0, criteria.getStartDate(),
		criteria.getEndDate());
	Pager<EventMatch> allMatches = getEventRowsForAssignment(context, assignment.getToken(),
		EventRecordType.StreamData, allCriteria);
	SearchResults<IDeviceStreamData> allResults = convertMatches(context, allMatches);

	// Manually match for stream id.
	// TODO: This process is inefficient and needs to be reviewed.
	Pager<IDeviceStreamData> pager = new Pager<IDeviceStreamData>(criteria);
	for (IDeviceStreamData current : allResults.getResults()) {
	    if (!current.getStreamId().equals(streamId)) {
		continue;
	    }
	    pager.process(HBaseDeviceStreamData.getDeviceStreamData(context, assignment, streamId,
		    current.getSequenceNumber()));
	}
	return new SearchResults<IDeviceStreamData>(pager.getResults(), pager.getTotal());
    }

    /**
     * Create a new device command invocation entry for an assignment.
     * 
     * @param context
     * @param assignment
     * @param command
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceCommandInvocation createDeviceCommandInvocation(IHBaseContext context,
	    IDeviceAssignment assignment, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	long time = getEventTime(request);
	byte[] rowkey = getEventRowKey(context, assignment, time);
	byte[] qualifier = getQualifier(EventRecordType.CommandInvocation, time,
		context.getPayloadMarshaler().getEncoding());

	// Create a command invocation and marshal to JSON.
	DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(assignment,
		request);
	String id = getEncodedEventId(rowkey, qualifier);
	ci.setId(id);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceCommandInvocation(ci);

	Put put = new Put(rowkey);
	put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	context.getDeviceEventBuffer().add(put);

	return ci;
    }

    /**
     * Get a {@link IDeviceEvent} by unique id.
     * 
     * @param context
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent getDeviceEvent(IHBaseContext context, String id) throws SiteWhereException {
	return getEventById(context, id);
    }

    /**
     * List command invocations associated with an assignment based on the given
     * criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(IHBaseContext context,
	    String assnToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, EventRecordType.CommandInvocation,
		criteria);
	return convertMatches(context, matches);
    }

    /**
     * List device command invocations associated with a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(IHBaseContext context,
	    String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForSite(context, siteToken, EventRecordType.CommandInvocation,
		criteria);
	return convertMatches(context, matches);
    }

    /**
     * Create a device state change event.
     * 
     * @param context
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceStateChange createDeviceStateChange(IHBaseContext context, IDeviceAssignment assignment,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException {
	long time = getEventTime(request);
	byte[] rowkey = getEventRowKey(context, assignment, time);
	byte[] qualifier = getQualifier(EventRecordType.StateChange, time, context.getPayloadMarshaler().getEncoding());

	// Create a state change and marshal to JSON.
	DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(assignment, request);
	String id = getEncodedEventId(rowkey, qualifier);
	state.setId(id);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceStateChange(state);

	Put put = new Put(rowkey);
	put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	context.getDeviceEventBuffer().add(put);

	// Update state if requested.
	if (request.isUpdateState()) {
	    context.getAssignmentStateManager().addStateChange(assignment.getToken(), state);
	}

	return state;
    }

    /**
     * List state changes associated with an assignment based on the given
     * criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceStateChange> listDeviceStateChanges(IHBaseContext context, String assnToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, EventRecordType.StateChange,
		criteria);
	return convertMatches(context, matches);
    }

    /**
     * List device state changes associated with a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceStateChange> listDeviceStateChangesForSite(IHBaseContext context,
	    String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForSite(context, siteToken, EventRecordType.StateChange, criteria);
	return convertMatches(context, matches);
    }

    /**
     * Create a device command response.
     * 
     * @param context
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceCommandResponse createDeviceCommandResponse(IHBaseContext context,
	    IDeviceAssignment assignment, IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	long time = getEventTime(request);
	byte[] rowkey = getEventRowKey(context, assignment, time);
	byte[] qualifier = getQualifier(EventRecordType.CommandResponse, time,
		context.getPayloadMarshaler().getEncoding());

	// Create a state change and marshal to JSON.
	DeviceCommandResponse cr = DeviceEventManagementPersistence.deviceCommandResponseCreateLogic(assignment,
		request);
	String id = getEncodedEventId(rowkey, qualifier);
	cr.setId(id);
	byte[] payload = context.getPayloadMarshaler().encodeDeviceCommandResponse(cr);

	Put put = new Put(rowkey);
	put.addColumn(ISiteWhereHBase.FAMILY_ID, qualifier, payload);
	context.getDeviceEventBuffer().add(put);

	linkDeviceCommandResponseToInvocation(context, cr);
	return cr;
    }

    /**
     * Creates a link to the command response by incrementing(or creating) a
     * counter and sequential entries under the original invocation. TODO: Note
     * that none of this is transactional, so it is currently possible for
     * responses not to be correctly linked back to the original invocation if
     * the calls in this method fail.
     * 
     * @param context
     * @param response
     * @throws SiteWhereException
     */
    protected static void linkDeviceCommandResponseToInvocation(IHBaseContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	String originator = response.getOriginatingEventId();
	if (originator == null) {
	    return;
	}

	byte[][] keys = getDecodedEventId(originator);
	byte[] row = keys[0];
	byte[] qual = keys[1];

	Table events = null;
	try {
	    events = getEventsTableInterface(context);
	    // Increment the result counter.
	    qual[3] = EventRecordType.CommandResponseCounter.getType();
	    long counter = events.incrementColumnValue(row, ISiteWhereHBase.FAMILY_ID, qual, 1);
	    byte[] counterBytes = Bytes.toBytes(counter);

	    // Add new response entry row under the invocation.
	    qual[3] = EventRecordType.CommandResponseEntry.getType();
	    ByteBuffer seqkey = ByteBuffer.allocate(qual.length + counterBytes.length);
	    seqkey.put(qual);
	    seqkey.put(counterBytes);

	    Put put = new Put(row);
	    put.addColumn(ISiteWhereHBase.FAMILY_ID, seqkey.array(), response.getId().getBytes());
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
     * @param context
     * @param invocationId
     * @return
     * @throws SiteWhereException
     */
    protected static SearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(IHBaseContext context,
	    String invocationId) throws SiteWhereException {
	byte[][] keys = getDecodedEventId(invocationId);
	byte[] row = keys[0];
	byte[] qual = keys[1];

	Table events = null;
	List<IDeviceCommandResponse> responses = new ArrayList<IDeviceCommandResponse>();
	try {
	    events = getEventsTableInterface(context);

	    Get get = new Get(row);
	    Result result = events.get(get);
	    Map<byte[], byte[]> cells = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);

	    byte[] match = qual;
	    match[3] = EventRecordType.CommandResponseEntry.getType();
	    for (byte[] curr : cells.keySet()) {
		if ((curr[0] == match[0]) && (curr[1] == match[1]) && (curr[2] == match[2]) && (curr[3] == match[3])) {
		    byte[] value = cells.get(curr);
		    String responseId = new String(value);
		    responses.add(getDeviceCommandResponse(context, responseId));
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
     * List command responses associated with an assignment based on the given
     * criteria.
     * 
     * @param context
     * @param assnToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceCommandResponse> listDeviceCommandResponses(IHBaseContext context,
	    String assnToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForAssignment(context, assnToken, EventRecordType.CommandResponse,
		criteria);
	return convertMatches(context, matches);
    }

    /**
     * Get a {@link IDeviceCommandResponse} by unique id.
     * 
     * @param context
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceCommandResponse getDeviceCommandResponse(IHBaseContext context, String id)
	    throws SiteWhereException {
	IDeviceEvent event = getEventById(context, id);
	if (event instanceof IDeviceCommandResponse) {
	    return (IDeviceCommandResponse) event;
	}
	throw new SiteWhereException("Event is not a command response.");
    }

    /**
     * List device command responses associated with a site.
     * 
     * @param context
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(IHBaseContext context,
	    String siteToken, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<EventMatch> matches = getEventRowsForSite(context, siteToken, EventRecordType.CommandResponse, criteria);
	return convertMatches(context, matches);
    }

    /**
     * Get the event row key bytes.
     * 
     * @param assignment
     * @param time
     * @return
     * @throws SiteWhereException
     */
    protected static byte[] getEventRowKey(IHBaseContext context, IDeviceAssignment assignment, long time)
	    throws SiteWhereException {
	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assignment.getToken());
	if (assnKey == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	return getRowKey(assnKey, time);
    }

    /**
     * Find all event rows associated with a device assignment and return cells
     * that match the search criteria.
     * 
     * @param context
     * @param assnToken
     * @param eventType
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    protected static Pager<EventMatch> getEventRowsForAssignment(IHBaseContext context, String assnToken,
	    EventRecordType eventType, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	byte[] assnKey = context.getDeviceIdManager().getAssignmentKeys().getValue(assnToken);
	if (assnKey == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}

	// Note: Because time values are inverted, start and end keys are
	// reversed.
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

	Table events = null;
	ResultScanner scanner = null;
	try {
	    events = getEventsTableInterface(context);
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
		    if ((qual.length > 3) && ((eventType == null) || (qual[3] == eventType.getType()))) {
			Date eventDate = getDateForEventKeyValue(current.getRow(), qual);
			if ((criteria.getStartDate() != null) && (eventDate.before(criteria.getStartDate()))) {
			    continue;
			}
			if ((criteria.getEndDate() != null) && (eventDate.after(criteria.getEndDate()))) {
			    continue;
			}
			EventRecordType type = EventRecordType.decode(qual[3]);
			byte[] encoding = getEncodingFromQualifier(qual);
			matches.add(new EventMatch(type, eventDate, value, encoding));
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
     * @param key
     * @param qualifier
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
     * Find all event rows associated with a site and return values that match
     * the search criteria. TODO: This is not optimized at all and will take
     * forever in cases where there are ton of assignments and events. It has to
     * go through every record associated with the site. It works for now
     * though.
     * 
     * @param context
     * @param siteToken
     * @param eventType
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    protected static Pager<EventMatch> getEventRowsForSite(IHBaseContext context, String siteToken,
	    EventRecordType eventType, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	// Long siteId =
	// context.getDeviceIdManager().getSiteKeys().getValue(siteToken);
	// if (siteId == null) {
	// throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken,
	// ErrorLevel.ERROR);
	// }
	// byte[] startPrefix = HBaseSite.getAssignmentRowKey(siteId);
	// byte[] afterPrefix = HBaseSite.getAfterAssignmentRowKey(siteId);
	//
	// Table events = null;
	// ResultScanner scanner = null;
	// try {
	// events = getEventsTableInterface(context);
	// Scan scan = new Scan();
	// scan.setStartRow(startPrefix);
	// scan.setStopRow(afterPrefix);
	// scanner = events.getScanner(scan);
	//
	// List<EventMatch> matches = new ArrayList<EventMatch>();
	// Iterator<Result> results = scanner.iterator();
	// while (results.hasNext()) {
	// Result current = results.next();
	// byte[] key = current.getRow();
	// if (key.length > 7) {
	// Map<byte[], byte[]> cells =
	// current.getFamilyMap(ISiteWhereHBase.FAMILY_ID);
	// for (byte[] qual : cells.keySet()) {
	// byte[] value = cells.get(qual);
	// if ((qual.length > 3) && (qual[3] == eventType.getType())) {
	// Date eventDate = getDateForEventKeyValue(key, qual);
	// if ((criteria.getStartDate() != null) &&
	// (eventDate.before(criteria.getStartDate()))) {
	// continue;
	// }
	// if ((criteria.getEndDate() != null) &&
	// (eventDate.after(criteria.getEndDate()))) {
	// continue;
	// }
	// EventRecordType type = EventRecordType.decode(qual[3]);
	// byte[] encoding = getEncodingFromQualifier(qual);
	// matches.add(new EventMatch(type, eventDate, value, encoding));
	// }
	// }
	// }
	// }
	// Collections.sort(matches, Collections.reverseOrder());
	// Pager<EventMatch> pager = new Pager<EventMatch>(criteria);
	// for (EventMatch match : matches) {
	// pager.process(match);
	// }
	// return pager;
	// } catch (IOException e) {
	// throw new SiteWhereException("Error scanning event rows.", e);
	// } finally {
	// if (scanner != null) {
	// scanner.close();
	// }
	// HBaseUtils.closeCleanly(events);
	// }
	throw new SiteWhereException("Fix table layout to separate device management from event management");
    }

    /**
     * Used for ordering events without having to unmarshal all of the byte
     * arrays to do it.
     * 
     * @author Derek
     */
    private static class EventMatch implements Comparable<EventMatch> {

	private EventRecordType type;

	private Date date;

	private byte[] payload;

	private byte[] encoding;

	public EventMatch(EventRecordType type, Date date, byte[] payload, byte[] encoding) {
	    this.type = type;
	    this.date = date;
	    this.payload = payload;
	    this.encoding = encoding;
	}

	public EventRecordType getType() {
	    return type;
	}

	public Date getDate() {
	    return date;
	}

	public byte[] getPayload() {
	    return payload;
	}

	public byte[] getEncoding() {
	    return encoding;
	}

	public int compareTo(EventMatch other) {
	    return this.getDate().compareTo(other.getDate());
	}
    }

    /**
     * Converts matching rows to {@link SearchResults} for web service response.
     * 
     * @param context
     * @param matches
     * @return
     * @throws SiteWhereException
     */
    @SuppressWarnings("unchecked")
    protected static <I extends IDeviceEvent> SearchResults<I> convertMatches(IHBaseContext context,
	    Pager<EventMatch> matches) throws SiteWhereException {
	List<I> results = new ArrayList<I>();
	for (EventMatch match : matches.getResults()) {
	    Class<? extends IDeviceEvent> type = getEventClassForIndicator(match.getType().getType());
	    try {
		results.add((I) PayloadMarshalerResolver.getInstance().getMarshaler(match.getEncoding())
			.decode(match.getPayload(), type));
	    } catch (Throwable e) {
		LOGGER.error("Unable to read payload value into event object.", e);
	    }
	}
	return new SearchResults<I>(results, matches.getTotal());
    }

    /**
     * Gets the absolute first possible event key for cases where a start
     * timestamp is not specified.
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
     * Gets the absolute first possible event key for cases where a start
     * timestamp is not specified.
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
     * @param assnKey
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
     * @param eventType
     * @param time
     * @param encoding
     * @return
     */
    public static byte[] getQualifier(EventRecordType eventType, long time, PayloadEncoding encoding) {
	byte[] offsetBytes = Bytes.toBytes(time);
	byte[] encodingBytes = encoding.getIndicator();
	ByteBuffer buffer = ByteBuffer.allocate(4 + encodingBytes.length);
	buffer.put((byte) ~offsetBytes[5]);
	buffer.put((byte) ~offsetBytes[6]);
	buffer.put((byte) ~offsetBytes[7]);
	buffer.put(eventType.getType());
	buffer.put(encodingBytes);
	return buffer.array();
    }

    /**
     * Get encoding scheme from qualifier.
     * 
     * @param qualifier
     * @return
     */
    public static byte[] getEncodingFromQualifier(byte[] qualifier) {
	int encLength = qualifier.length - 4;
	return Bytes.tail(qualifier, encLength);
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
     * Decodes an event id into a {@link KeyValue} that can be used to access
     * the data in HBase.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    private static byte[][] getDecodedEventId(String id) throws SiteWhereException {
	// int rowLength = HBaseSite.SITE_IDENTIFIER_LENGTH + 1 +
	// HBaseDeviceAssignment.ASSIGNMENT_IDENTIFIER_LENGTH + 5;
	// try {
	// byte[] decoded = Base58.decode(id);
	// byte[] row = Bytes.head(decoded, rowLength);
	// byte[] qual = Bytes.tail(decoded, decoded.length - rowLength);
	// return new byte[][] { row, qual };
	// } catch (AddressFormatException e) {
	// throw new SiteWhereException("Invalid event id: " + id);
	// }
	throw new SiteWhereException("Fix table layout to separate device management from event management");
    }

    /**
     * Gets an event by unique id.
     * 
     * @param context
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected static IDeviceEvent getEventById(IHBaseContext context, String id) throws SiteWhereException {
	byte[][] keys = getDecodedEventId(id);
	byte[] row = keys[0];
	byte[] qual = keys[1];
	Table events = null;
	try {
	    events = getEventsTableInterface(context);
	    Get get = new Get(row);
	    get.addColumn(ISiteWhereHBase.FAMILY_ID, qual);
	    Result result = events.get(get);
	    byte type = qual[3];
	    Class<? extends IDeviceEvent> eventClass = getEventClassForIndicator(type);

	    if (result != null) {
		byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, qual);
		if (payload != null) {
		    return context.getPayloadMarshaler().decode(payload, eventClass);
		}
	    }
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceEventId, ErrorLevel.ERROR);
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
    protected static Class<? extends IDeviceEvent> getEventClassForIndicator(byte indicator) throws SiteWhereException {
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
	case StreamData: {
	    return DeviceStreamData.class;
	}
	default: {
	    throw new SiteWhereException("Id references unknown event type.");
	}
	}
    }

    /**
     * Get events table based on context.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected static Table getEventsTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.EVENTS_TABLE_NAME);
    }
}