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
import com.sitewhere.rest.model.search.DeviceMeasurementsSearchResults;
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
        return null;
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
            GTSInput gtsMeasurement = Warp10DeviceMeasurement.toGTS(measurements);
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
        queryParams.addParameter(getFieldForIndex(index), entityIds.get(0).toString());
        List<GTSOutput> fetch = getClient().findGTS(queryParams);

        List<IDeviceMeasurement> results = new ArrayList();
        for (GTSOutput gtsOutput : fetch) {
            DeviceMeasurement deviceMeasurement = Warp10DeviceMeasurement.fromGTS(gtsOutput);
            results.add(deviceMeasurement);
        }
        return new DeviceMeasurementsSearchResults(results);
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
