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
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
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

        GTSInput gtsInput = GTSInput.builder();
        gtsInput.setTs(1382441207762000L);
        gtsInput.setLat(51.501988);
        gtsInput.setLon(0.005953);
        gtsInput.setElev(1L);
        gtsInput.setValue(79.16);
        gtsInput.setName("some.sensor.model.humidity");

        Map labels = new HashMap<String, String>();
        labels.put("xbeeId", "XBee_40670F0D");
        labels.put("moteId", "53");
        labels.put("area", "8");

        gtsInput.setLabels(labels);
        getClient().getWarp10RestClient().ingress(gtsInput);
        List<GTSOutput> outputs = getClient().getWarp10RestClient().fetch("now=1435091737000000&timespan=-10&selector=~.*{}&format=json");


        return null;
    }

    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
        return null;
    }

    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
        return null;
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     *
     * @param token
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
        Warp10DeviceMeasurement warp10DeviceMeasurement = new Warp10DeviceMeasurement();

        for (IDeviceMeasurementCreateRequest request : requests) {
            DeviceMeasurement measurements = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(request, assignment);
            GTSInput gtsMeasurement = warp10DeviceMeasurement.convert(measurements);
            getClient().getWarp10RestClient().ingress(gtsMeasurement);
        }

        //TODO: change selector
        List<GTSOutput> out = getClient().getWarp10RestClient().fetch("now=1435091737000000&timespan=-10&selector=~.*{}&format=json");

        return result;
    }

    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... request) throws SiteWhereException {
        return null;
    }

    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... request) throws SiteWhereException {
        return null;
    }

    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId, IDeviceCommandInvocationCreateRequest... request) throws SiteWhereException {
        return null;
    }

    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId, IDeviceCommandResponseCreateRequest... request) throws SiteWhereException {
        return null;
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest... request) throws SiteWhereException {
        return null;
    }

    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
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
}
