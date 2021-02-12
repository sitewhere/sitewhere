/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.event.persistence.warp10;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.inject.Inject;
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
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.warp10.Warp10Client;
import com.sitewhere.warp10.Warp10Persistence;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;
import com.sitewhere.warp10.rest.QueryParams;

/**
 * Implementation of {@link IDeviceEventManagement} that interacts with Warp 10.
 */
public class Warp10DeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Warp 10 client */
    private Warp10Client client;

    @Inject
    public Warp10DeviceEventManagement(Warp10Client client) {
	super(LifecycleComponentType.DataStore);
	this.client = client;
    }

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceEventBatch(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(IDeviceEventContext context, IDeviceEventBatch batch)
	    throws SiteWhereException {
	return DeviceEventManagementPersistence.deviceEventBatchLogic(context, batch, this);
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

    /*
     * @see com.sitewhere.microservice.api.event.IDeviceEventManagement#
     * addDeviceMeasurements(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(IDeviceEventContext context,
	    IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
	List<IDeviceMeasurement> result = new ArrayList<>();
	for (IDeviceMeasurementCreateRequest request : requests) {
	    DeviceMeasurement measurements = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(context,
		    request);
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
     * addDeviceLocations(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public List<IDeviceLocation> addDeviceLocations(IDeviceEventContext context,
	    IDeviceLocationCreateRequest... requests) throws SiteWhereException {
	List<IDeviceLocation> result = new ArrayList<>();
	for (IDeviceLocationCreateRequest request : requests) {
	    DeviceLocation location = DeviceEventManagementPersistence.deviceLocationCreateLogic(context, request);
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
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public List<IDeviceAlert> addDeviceAlerts(IDeviceEventContext context, IDeviceAlertCreateRequest... requests)
	    throws SiteWhereException {
	List<IDeviceAlert> result = new ArrayList<>();
	for (IDeviceAlertCreateRequest request : requests) {
	    DeviceAlert alert = DeviceEventManagementPersistence.deviceAlertCreateLogic(context, request);
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
     * addDeviceCommandInvocations(com.sitewhere.spi.device.event.
     * IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(IDeviceEventContext context,
	    IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
	List<IDeviceCommandInvocation> result = new ArrayList<>();
	for (IDeviceCommandInvocationCreateRequest request : requests) {
	    IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(context.getDeviceTypeId(),
		    request.getCommandToken());
	    DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(context,
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
     * addDeviceCommandResponses(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(IDeviceEventContext context,
	    IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
	List<IDeviceCommandResponse> result = new ArrayList<>();
	for (IDeviceCommandResponseCreateRequest request : requests) {
	    DeviceCommandResponse response = DeviceEventManagementPersistence.deviceCommandResponseCreateLogic(context,
		    request);

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
     * addDeviceStateChanges(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(IDeviceEventContext context,
	    IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
	List<IDeviceStateChange> result = new ArrayList<>();
	for (IDeviceStateChangeCreateRequest request : requests) {
	    DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(context, request);

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

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getClient(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getClient(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getClient(), monitor);
    }

    public Warp10Client getClient() {
	return client;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IEventManagementMicroservice) getMicroservice()).getDeviceManagement();
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
