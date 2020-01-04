/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.search.DeviceMeasurementsSearchResults;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.warp10.Warp10DbClient;
import com.sitewhere.warp10.Warp10Persistence;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;
import com.sitewhere.warp10.rest.QueryParams;

public class Warp10DbDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Warp 10 client */
    private Warp10DbClient client;

    public Warp10DbDeviceEventManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getClient() == null) {
	    throw new SiteWhereException("No warp 10 client configured.");
	}
	getClient().start(monitor);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceEventBatch(java.util.UUID,
     * com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	return DeviceEventManagementPersistence.deviceEventBatchLogic(assignment, batch, this);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * getDeviceEventById(java.util.UUID)
     */
    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_ID, eventId.toString());
	List<GTSOutput> founds = getClient().findGTS(queryParams);

	if (founds != null && founds.size() > 0) {
	    return Warp10DeviceEventManagementPersistence.unmarshalEvent(founds.get(0));
	}
	return null;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_ALTERNATE_ID, alternateId);
	List<GTSOutput> founds = getClient().findGTS(queryParams);

	if (founds != null && founds.size() > 0) {
	    return Warp10DeviceEventManagementPersistence.unmarshalEvent(founds.get(0));
	}
	return null;
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     *
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
	IDeviceAssignment assignment = getCachedDeviceManagement().getDeviceAssignment(id);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}
	return assignment;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceMeasurements(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId,
	    IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
	List<IDeviceMeasurement> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

	for (IDeviceMeasurementCreateRequest request : requests) {
	    DeviceMeasurement measurements = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(request,
		    assignment);
	    GTSInput gtsMeasurement = Warp10DeviceMeasurement.toGTS(measurements, false);
	    int ingress = getClient().insertGTS(gtsMeasurement);
	    if (ingress == 200) {
		result.add(measurements);
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceMeasurementsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurement.name());
	queryParams.addParameter(getFieldForIndex(index),
		entityIds.stream().map(Object::toString).collect(Collectors.joining("|")));
	Warp10Persistence.addDateSearchCriteria(queryParams, criteria);

	List<IDeviceMeasurement> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);
	for (GTSOutput gtsOutput : fetch) {
	    DeviceMeasurement deviceMeasurement = Warp10DeviceMeasurement.fromGTS(gtsOutput, false);
	    results.add(deviceMeasurement);
	}

	return new DeviceMeasurementsSearchResults(results);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceLocations(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... requests)
	    throws SiteWhereException {
	List<IDeviceLocation> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

	for (IDeviceLocationCreateRequest request : requests) {
	    DeviceLocation location = DeviceEventManagementPersistence.deviceLocationCreateLogic(assignment, request);
	    GTSInput gtsLocations = Warp10DeviceLocation.toGTS(location, false);

	    int ingress = getClient().insertGTS(gtsLocations);
	    if (ingress == 200) {
		result.add(location);
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	queryParams.addParameter(getFieldForIndex(index),
		entityIds.stream().map(Object::toString).collect(Collectors.joining("|")));
	Warp10Persistence.addDateSearchCriteria(queryParams, criteria);

	List<IDeviceLocation> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);
	for (GTSOutput gtsOutput : fetch) {
	    DeviceLocation deviceLocation = Warp10DeviceLocation.fromGTS(gtsOutput, false);
	    results.add(deviceLocation);
	}
	return new SearchResults<IDeviceLocation>(results);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.event.IDeviceEventManagement#addDeviceAlerts(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... requests)
	    throws SiteWhereException {
	List<IDeviceAlert> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

	for (IDeviceAlertCreateRequest request : requests) {
	    DeviceAlert alert = DeviceEventManagementPersistence.deviceAlertCreateLogic(assignment, request);
	    GTSInput gtsAlert = Warp10DeviceAlert.toGTS(alert, false);
	    int ingress = getClient().insertGTS(gtsAlert);
	    if (ingress == 200) {
		result.add(alert);
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
	queryParams.addParameter(getFieldForIndex(index),
		entityIds.stream().map(Object::toString).collect(Collectors.joining("|")));
	Warp10Persistence.addDateSearchCriteria(queryParams, criteria);

	List<IDeviceAlert> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);
	for (GTSOutput gtsOutput : fetch) {
	    DeviceAlert deviceAlert = Warp10DeviceAlert.fromGTS(gtsOutput, false);
	    results.add(deviceAlert);
	}
	return new SearchResults<IDeviceAlert>(results);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceCommandInvocations(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
	List<IDeviceCommandInvocation> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

	for (IDeviceCommandInvocationCreateRequest request : requests) {
	    IDeviceCommand command = getCachedDeviceManagement().getDeviceCommandByToken(assignment.getDeviceTypeId(),
		    request.getCommandToken());
	    DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(assignment,
		    command, request);

	    GTSInput gtsCi = Warp10DeviceCommandInvocation.toGTS(ci);

	    int ingress = getClient().insertGTS(gtsCi);
	    if (ingress == 200) {
		result.add(ci);
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {

	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
	queryParams.addParameter(getFieldForIndex(index),
		entityIds.stream().map(Object::toString).collect(Collectors.joining("|")));
	Warp10Persistence.addDateSearchCriteria(queryParams, criteria);

	List<IDeviceCommandInvocation> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);
	for (GTSOutput gtsOutput : fetch) {
	    DeviceCommandInvocation deviceCommandInvocation = Warp10DeviceCommandInvocation.fromGTS(gtsOutput);
	    results.add(deviceCommandInvocation);
	}
	return new SearchResults<IDeviceCommandInvocation>(results);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceCommandResponses(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
	List<IDeviceCommandResponse> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

	for (IDeviceCommandResponseCreateRequest request : requests) {
	    DeviceCommandResponse response = DeviceEventManagementPersistence
		    .deviceCommandResponseCreateLogic(assignment, request);
	    GTSInput gtsResponse = Warp10DeviceCommandResponse.toGTS(response);
	    int ingress = getClient().insertGTS(gtsResponse);

	    if (ingress == 200) {
		result.add(response);
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId)
	    throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	queryParams.addParameter(Warp10DeviceCommandResponse.PROP_ORIGINATING_EVENT_ID,
		DeviceEventType.CommandResponse.name());

	List<IDeviceCommandResponse> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);
	for (GTSOutput gtsOutput : fetch) {
	    DeviceCommandResponse deviceCommandResponse = Warp10DeviceCommandResponse.fromGTS(gtsOutput);
	    results.add(deviceCommandResponse);
	}
	return new SearchResults<IDeviceCommandResponse>(results);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	queryParams.addParameter(getFieldForIndex(index),
		entityIds.stream().map(Object::toString).collect(Collectors.joining("|")));
	Warp10Persistence.addDateSearchCriteria(queryParams, criteria);

	List<IDeviceCommandResponse> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);
	for (GTSOutput gtsOutput : fetch) {
	    DeviceCommandResponse deviceCommandResponse = Warp10DeviceCommandResponse.fromGTS(gtsOutput);
	    results.add(deviceCommandResponse);
	}
	return new SearchResults<IDeviceCommandResponse>(results);
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceStateChanges(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId,
	    IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
	List<IDeviceStateChange> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceStateChangeCreateRequest request : requests) {
	    DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(assignment,
		    request);
	    GTSInput gtsState = Warp10DeviceStateChange.toGTS(state);
	    int ingress = getClient().insertGTS(gtsState);
	    if (ingress == 200) {
		result.add(state);
	    }
	}
	return result;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * listDeviceStateChangesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {

	QueryParams queryParams = QueryParams.builder();
	queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
	queryParams.addParameter(getFieldForIndex(index),
		entityIds.stream().map(Object::toString).collect(Collectors.joining("|")));
	Warp10Persistence.addDateSearchCriteria(queryParams, criteria);

	List<IDeviceStateChange> results = new ArrayList<>();
	List<GTSOutput> fetch = getClient().findGTS(queryParams);

	for (GTSOutput gtsOutput : fetch) {
	    DeviceStateChange deviceStateChange = Warp10DeviceStateChange.fromGTS(gtsOutput);
	    results.add(deviceStateChange);
	}

	return new SearchResults<IDeviceStateChange>(results);
    }

    public Warp10DbClient getClient() {
	return client;
    }

    public void setClient(Warp10DbClient client) {
	this.client = client;
    }

    protected IDeviceManagement getCachedDeviceManagement() {
	return ((IEventManagementMicroservice) getMicroservice()).getDeviceManagementApiChannel();
    }

    protected static String getFieldForIndex(DeviceEventIndex index) throws SiteWhereException {
	switch (index) {
	case Area: {
	    return Warp10DeviceEvent.PROP_AREA_ID;
	}
	case Asset: {
	    return Warp10DeviceEvent.PROP_ASSET_ID;
	}
	case Assignment: {
	    return Warp10DeviceEvent.PROP_DEVICE_ASSIGNMENT_ID;
	}
	case Customer: {
	    return Warp10DeviceEvent.PROP_CUSTOMER_ID;
	}
	}
	throw new SiteWhereException("Unknown index: " + index.name());
    }
}
