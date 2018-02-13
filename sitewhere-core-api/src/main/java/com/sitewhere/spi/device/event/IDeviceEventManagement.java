/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
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
     * @param assignment
     * @param batch
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEventBatchResponse addDeviceEventBatch(IDeviceAssignment assignment, IDeviceEventBatch batch)
	    throws SiteWhereException;

    /**
     * Get a device event by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException;

    /**
     * Get a device event by alternate (external) id.
     * 
     * @param alternateId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException;

    /**
     * List all events for the given assignment that meet the search criteria.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceEvent> listDeviceEvents(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add measurements for a given device assignment.
     * 
     * @param assignment
     * @param measurements
     * @return
     * @throws SiteWhereException
     */
    public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
	    IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException;

    /**
     * Gets device measurement entries for an assignment based on criteria.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device measurements for an area.
     * 
     * @param area
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForArea(IArea area,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add location for a given device assignment.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
	    throws SiteWhereException;

    /**
     * Gets device location entries for an assignment.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceLocation> listDeviceLocations(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device locations for an area.
     * 
     * @param area
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceLocation> listDeviceLocationsForArea(IArea area, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Add alert for a given device assignment.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
	    throws SiteWhereException;

    /**
     * Gets the most recent device alert entries for an assignment.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAlert> listDeviceAlerts(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device alerts for an area.
     * 
     * @param area
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAlert> listDeviceAlertsForArea(IArea area, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Add a chunk of stream data for a given device assignment.
     * 
     * @param assignment
     * @param stream
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStreamData addDeviceStreamData(IDeviceAssignment assignment, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException;

    /**
     * Get a single chunk of data from a device stream.
     * 
     * @param assignment
     * @param streamId
     * @param sequenceNumber
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStreamData getDeviceStreamData(IDeviceAssignment assignment, String streamId, long sequenceNumber)
	    throws SiteWhereException;

    /**
     * List all chunks of data in a device assignment that belong to a given stream
     * and meet the criteria.
     * 
     * @param assignment
     * @param streamId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStreamData> listDeviceStreamData(IDeviceAssignment assignment, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Add a device command invocation event for the given assignment.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommandInvocation addDeviceCommandInvocation(IDeviceAssignment assignment,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException;

    /**
     * Gets device command invocations for an assignment based on criteria.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device command invocations for an area.
     * 
     * @param area
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForArea(IArea area,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List responses associated with a command invocation.
     * 
     * @param invocationId
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
	    throws SiteWhereException;

    /**
     * Adds a new device command response event.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommandResponse addDeviceCommandResponse(IDeviceAssignment assignment,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException;

    /**
     * Gets the most recent device command response entries for an assignment.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device command responses for an area.
     * 
     * @param area
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForArea(IArea area,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Adds a new device state change event.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStateChange addDeviceStateChange(IDeviceAssignment assignment,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException;

    /**
     * Gets the most recent device state change entries for an assignment.
     * 
     * @param assignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStateChange> listDeviceStateChanges(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * List device state changes for an area.
     * 
     * @param area
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForArea(IArea area,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Update information for an existing event.
     * 
     * @param eventId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceEvent updateDeviceEvent(String eventId, IDeviceEventCreateRequest request) throws SiteWhereException;
}