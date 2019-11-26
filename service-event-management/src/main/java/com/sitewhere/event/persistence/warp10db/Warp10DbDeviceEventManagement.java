/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.rest.model.device.event.*;
import com.sitewhere.rest.model.search.DeviceMeasurementsSearchResults;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.*;
import com.sitewhere.spi.device.event.request.*;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.warp10.Warp10DbClient;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;
import com.sitewhere.warp10.rest.QueryParams;

import java.util.*;

public class Warp10DbDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Client */
    private Warp10DbClient client;

    public Warp10DbDeviceEventManagement() {
        super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        if (getClient() == null) {
            throw new SiteWhereException("No warp 10 client configured.");
        }
        getClient().start(monitor);
    }

    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch) throws SiteWhereException {
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        return DeviceEventManagementPersistence.deviceEventBatchLogic(assignment, batch, this);
    }

    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_ID, eventId.toString());
        List<GTSOutput> founds = getClient().findGTS(queryParams);

        if(founds != null && founds.size() > 0) {
            return  Warp10DeviceEventManagementPersistence.unmarshalEvent(founds.get(0));
        }
        return null;
    }

    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_ALTERNATE_ID, alternateId);
        List<GTSOutput> founds = getClient().findGTS(queryParams);

        if(founds != null && founds.size() > 0) {
            return  Warp10DeviceEventManagementPersistence.unmarshalEvent(founds.get(0));
        }
        return null;
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
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

    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
        List<IDeviceMeasurement> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

        for (IDeviceMeasurementCreateRequest request : requests) {
            DeviceMeasurement measurements = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(request, assignment);
            GTSInput gtsMeasurement = Warp10DeviceMeasurement.toGTS(measurements, false);
            int ingress = getClient().insertGTS(gtsMeasurement);
            if(ingress == 200) {
                result.add(measurements);
            }
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurement.name());

        List<IDeviceMeasurement> results = new ArrayList();
        for (UUID uuid: entityIds) {
            queryParams.addParameter(getFieldForIndex(index), uuid.toString());
            List<GTSOutput> fetch = getClient().findGTS(queryParams);

            for (GTSOutput gtsOutput : fetch) {
                DeviceMeasurement deviceMeasurement = Warp10DeviceMeasurement.fromGTS(gtsOutput, false);
                results.add(deviceMeasurement);
            }
        }
        return new DeviceMeasurementsSearchResults(results);
    }

    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... requests) throws SiteWhereException {
        List<IDeviceLocation> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

        for (IDeviceLocationCreateRequest request : requests) {
            DeviceLocation location = DeviceEventManagementPersistence.deviceLocationCreateLogic(assignment, request);
            GTSInput gtsLocations = Warp10DeviceLocation.toGTS(location, false);

            int ingress = getClient().insertGTS(gtsLocations);
            if(ingress == 200) {
                result.add(location);
            }
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());

        List<IDeviceLocation> results = new ArrayList();
        for (UUID uuid: entityIds) {
            queryParams.addParameter(getFieldForIndex(index), uuid.toString());
            List<GTSOutput> fetch = getClient().findGTS(queryParams);
            for (GTSOutput gtsOutput : fetch) {
                DeviceLocation deviceLocation = Warp10DeviceLocation.fromGTS(gtsOutput, false);
                results.add(deviceLocation);
            }
        }
        return new SearchResults<IDeviceLocation>(results);
    }

    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... requests) throws SiteWhereException {
        List<IDeviceAlert> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

        for (IDeviceAlertCreateRequest request : requests) {
            DeviceAlert alert = DeviceEventManagementPersistence.deviceAlertCreateLogic(assignment, request);
            GTSInput gtsAlert = Warp10DeviceAlert.toGTS(alert, false);
            int ingress = getClient().insertGTS(gtsAlert);
            if(ingress == 200) {
                result.add(alert);
            }
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());

        List<IDeviceAlert> results = new ArrayList();
        for (UUID uuid: entityIds) {
            queryParams.addParameter(getFieldForIndex(index), uuid.toString());
            List<GTSOutput> fetch = getClient().findGTS(queryParams);
            for (GTSOutput gtsOutput : fetch) {
                DeviceAlert deviceAlert = Warp10DeviceAlert.fromGTS(gtsOutput, false);
                results.add(deviceAlert);
            }
        }
        return new SearchResults<IDeviceAlert>(results);
    }

    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId, IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
        List<IDeviceCommandInvocation> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

        for (IDeviceCommandInvocationCreateRequest request : requests) {
            IDeviceCommand command = getCachedDeviceManagement().getDeviceCommandByToken(assignment.getDeviceTypeId(),
             request.getCommandToken());
            DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(assignment,
             command, request);

            GTSInput gtsCi = Warp10DeviceCommandInvocation.toGTS(ci);

            int ingress = getClient().insertGTS(gtsCi);
            if(ingress == 200) {
                result.add(ci);
            }
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {

        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
        //queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_DATE, "-1");
        queryParams.setStartDate(criteria.getStartDate());
        queryParams.setEndDate(criteria.getEndDate());

        List<IDeviceCommandInvocation> results = new ArrayList();
        for (UUID uuid: entityIds) {
            queryParams.addParameter(getFieldForIndex(index), uuid.toString());
            List<GTSOutput> fetch = getClient().findGTS(queryParams);
            for (GTSOutput gtsOutput : fetch) {
                DeviceCommandInvocation deviceCommandInvocation = Warp10DeviceCommandInvocation.fromGTS(gtsOutput);
                results.add(deviceCommandInvocation);
            }
        }
        return new SearchResults<IDeviceCommandInvocation>(results);
    }

    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId, IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
        List<IDeviceCommandResponse> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

        for (IDeviceCommandResponseCreateRequest request : requests) {
            DeviceCommandResponse response = DeviceEventManagementPersistence.deviceCommandResponseCreateLogic(assignment, request);
            GTSInput gtsResponse = Warp10DeviceCommandResponse.toGTS(response);
            int ingress = getClient().insertGTS(gtsResponse);

            if(ingress == 200) {
                result.add(response);
            }
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
        queryParams.addParameter(Warp10DeviceCommandResponse.PROP_ORIGINATING_EVENT_ID, DeviceEventType.CommandResponse.name());
        //queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_DATE, "-1");

        List<IDeviceCommandResponse> results = new ArrayList();
        List<GTSOutput> fetch = getClient().findGTS(queryParams);
        for (GTSOutput gtsOutput : fetch) {
            DeviceCommandResponse deviceCommandResponse = Warp10DeviceCommandResponse.fromGTS(gtsOutput);
            results.add(deviceCommandResponse);
        }
        return new SearchResults<IDeviceCommandResponse>(results);
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_DATE, "-1"); //TODO REVISAR

        List<IDeviceCommandResponse> results = new ArrayList();
        for (UUID uuid: entityIds) {
            queryParams.addParameter(getFieldForIndex(index), uuid.toString());
            List<GTSOutput> fetch = getClient().findGTS(queryParams);

            for (GTSOutput gtsOutput : fetch) {
                DeviceCommandResponse deviceCommandResponse = Warp10DeviceCommandResponse.fromGTS(gtsOutput);
                results.add(deviceCommandResponse);
            }
        }
        return new SearchResults<IDeviceCommandResponse>(results);
    }

    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
        List<IDeviceStateChange> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);

        for (IDeviceStateChangeCreateRequest request : requests) {
            DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(assignment, request);
            GTSInput gtsState = Warp10DeviceStateChange.toGTS(state);
            int ingress = getClient().insertGTS(gtsState);
            if(ingress == 200) {
                result.add(state);
            }
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {

        QueryParams queryParams = QueryParams.builder();
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
        queryParams.addParameter(Warp10DeviceEvent.PROP_EVENT_DATE, "-1"); //TODO REVISAR
        queryParams.setStartDate(criteria.getStartDate());
        queryParams.setEndDate(criteria.getEndDate());

        List<IDeviceStateChange> results = new ArrayList();
        for (UUID uuid: entityIds) {
            queryParams.addParameter(getFieldForIndex(index), uuid.toString());
            List<GTSOutput> fetch = getClient().findGTS(queryParams);

            for (GTSOutput gtsOutput : fetch) {
                DeviceStateChange deviceStateChange = Warp10DeviceStateChange.fromGTS(gtsOutput);
                results.add(deviceStateChange);
            }
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
        return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getCachedDeviceManagement();
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
