/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.*;
import com.sitewhere.spi.device.event.request.*;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.warp10.Warp10DbClient;

import java.util.List;
import java.util.UUID;

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
        return null;
    }

    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementCreateRequest... measurement) throws SiteWhereException {
        return null;
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
}
