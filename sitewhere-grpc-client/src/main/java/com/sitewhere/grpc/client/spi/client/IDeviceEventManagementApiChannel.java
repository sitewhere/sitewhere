/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.client;

import java.util.List;
import java.util.UUID;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Provides an {@link IMultitenantApiChannel} that supplies the
 * {@link IDeviceEventManagement}. API.
 * 
 * @author Derek
 */
public interface IDeviceEventManagementApiChannel<T extends MultitenantGrpcChannel<?, ?>>
	extends IMultitenantApiChannel<T> {

    /**
     * Add a batch of events for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param batch
     */
    public void addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch,
	    StreamObserver<IDeviceEventBatchResponse> observer) throws SiteWhereException;

    /**
     * Get a device event by id.
     * 
     * @param eventId
     * @param observer
     * @throws SiteWhereException
     */
    public void getDeviceEventById(UUID eventId, StreamObserver<IDeviceEvent> observer) throws SiteWhereException;

    /**
     * Get a device event by alternate (external) id.
     * 
     * @param alternateId
     * @param observer
     * @throws SiteWhereException
     */
    public void getDeviceEventByAlternateId(String alternateId, StreamObserver<IDeviceEvent> observer)
	    throws SiteWhereException;

    /**
     * Add measurements for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param observer
     * @param requests
     * @throws SiteWhereException
     */
    public void addDeviceMeasurements(UUID deviceAssignmentId, StreamObserver<IDeviceMeasurement> observer,
	    IDeviceMeasurementCreateRequest... requests) throws SiteWhereException;

    /**
     * List device measurement entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceMeasurement>> observer)
	    throws SiteWhereException;

    /**
     * Add locations for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param observer
     * @param requests
     * @throws SiteWhereException
     */
    public void addDeviceLocations(UUID deviceAssignmentId, StreamObserver<IDeviceLocation> observer,
	    IDeviceLocationCreateRequest... requests) throws SiteWhereException;

    /**
     * List device location entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceLocation>> observer)
	    throws SiteWhereException;

    /**
     * Add alerts for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param observer
     * @param requests
     * @throws SiteWhereException
     */
    public void addDeviceAlerts(UUID deviceAssignmentId, StreamObserver<IDeviceAlert> observer,
	    IDeviceAlertCreateRequest... requests) throws SiteWhereException;

    /**
     * List device location entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceAlert>> observer)
	    throws SiteWhereException;

    /**
     * Add a device command invocation events for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param observer
     * @param requests
     * @throws SiteWhereException
     */
    public void addDeviceCommandInvocations(UUID deviceAssignmentId, StreamObserver<IDeviceCommandInvocation> observer,
	    IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException;

    /**
     * List device command invocation events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceCommandInvocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceCommandInvocation>> observer)
	    throws SiteWhereException;

    /**
     * List responses associated with a command invocation.
     * 
     * @param invocationId
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceCommandInvocationResponses(UUID invocationId,
	    StreamObserver<ISearchResults<IDeviceCommandResponse>> observer) throws SiteWhereException;

    /**
     * Adds a new device command response events.
     * 
     * @param deviceAssignmentId
     * @param observer
     * @param requests
     * @throws SiteWhereException
     */
    public void addDeviceCommandResponses(UUID deviceAssignmentId, StreamObserver<IDeviceCommandResponse> observer,
	    IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException;

    /**
     * List device command response events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceCommandResponse>> observer)
	    throws SiteWhereException;

    /**
     * Adds a new device state change events.
     * 
     * @param deviceAssignmentId
     * @param observer
     * @param requests
     * @throws SiteWhereException
     */
    public void addDeviceStateChanges(UUID deviceAssignmentId, StreamObserver<IDeviceStateChange> observer,
	    IDeviceStateChangeCreateRequest... requests) throws SiteWhereException;

    /**
     * List device state change events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceStateChange>> observer)
	    throws SiteWhereException;
}