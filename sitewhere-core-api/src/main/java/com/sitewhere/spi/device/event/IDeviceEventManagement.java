/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.List;
import java.util.UUID;

import org.reactivestreams.Processor;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for device event management operations.
 * 
 * @author Derek
 */
public interface IDeviceEventManagement extends ITenantEngineLifecycleComponent {

    /**
     * Add a batch of events for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param batch
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch)
	    throws SiteWhereException;

    /**
     * Get a device event by id.
     * 
     * @param deviceId
     * @param eventId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEvent getDeviceEventById(UUID deviceId, UUID eventId) throws SiteWhereException;

    /**
     * Get a device event by alternate (external) id.
     * 
     * @param deviceId
     * @param alternateId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEvent getDeviceEventByAlternateId(UUID deviceId, String alternateId) throws SiteWhereException;

    /**
     * Stream information for creating multiple events across (potentially) multiple
     * assignments.
     * 
     * @return
     */
    public Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> streamDeviceAssignmentCreateEvents();

    /**
     * Add measurements for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param measurements
     * @return
     * @throws SiteWhereException
     */
    public IDeviceMeasurements addDeviceMeasurements(UUID deviceAssignmentId,
	    IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException;

    /**
     * Gets device measurement entries for assignments based on criteria.
     * 
     * @param assignmentIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device measurements for one more areas.
     * 
     * @param areaIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add location for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceLocation addDeviceLocation(UUID deviceAssignmentId, IDeviceLocationCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device location entries for one or more assignments.
     * 
     * @param assignmentIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device locations for one or more areas.
     * 
     * @param areaIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add alert for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlert addDeviceAlert(UUID deviceAssignmentId, IDeviceAlertCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device alert entries for one or more assignments.
     * 
     * @param assignmentIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device alerts for one or more areas.
     * 
     * @param areaIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAreas(List<UUID> areaIds, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Add a chunk of stream data for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param stream
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStreamData addDeviceStreamData(UUID deviceAssignmentId, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException;

    /**
     * Get a single chunk of data from a device stream.
     * 
     * @param deviceAssignmentId
     * @param streamId
     * @param sequenceNumber
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStreamData getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber)
	    throws SiteWhereException;

    /**
     * List all chunks of data in a device assignment that belong to a given stream
     * and meet the criteria.
     * 
     * @param assignmentId
     * @param streamId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID assignmentId, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add a device command invocation event for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommandInvocation addDeviceCommandInvocation(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException;

    /**
     * List device command invocations one or more assignments based on criteria.
     * 
     * @param assignmentIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device command invocations for one or more areas.
     * 
     * @param areaIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List responses associated with a command invocation.
     * 
     * @param deviceId
     * @param invocationId
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID deviceId, UUID invocationId)
	    throws SiteWhereException;

    /**
     * Adds a new device command response event.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommandResponse addDeviceCommandResponse(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException;

    /**
     * List device command response entries for one or more assignments.
     * 
     * @param assignmentIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device command responses for one or more areas.
     * 
     * @param areaIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Adds a new device state change event.
     * 
     * @param deviceAssignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStateChange addDeviceStateChange(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device state change entries for one or more assignments.
     * 
     * @param assignmentIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device state changes for one or more areas.
     * 
     * @param areaIds
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;
}