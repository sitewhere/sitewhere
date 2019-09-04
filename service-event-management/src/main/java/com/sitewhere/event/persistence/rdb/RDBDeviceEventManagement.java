/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.rdb;

import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.rdb.DbClient;
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
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Simeon Chen
 */
public class RDBDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Injected with global SiteWhere relational database client */
    private DbClient dbClient;

    public DbClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(DbClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * Constructor
     */
    public RDBDeviceEventManagement() {
        super(LifecycleComponentType.DataStore);
    }

    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch) throws SiteWhereException {
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        return DeviceEventManagementPersistence.deviceEventBatchLogic(assignment, batch, this);
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
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
        List<IDeviceMeasurement> result = new ArrayList<>();
        return result;
    }

    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
         return null;
    }

    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... requests) throws SiteWhereException {
        List<IDeviceLocation> result = new ArrayList<>();
        return result;
    }

    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
      return null;
    }

    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... requests) throws SiteWhereException {
        List<IDeviceAlert> result = new ArrayList<>();

        return result;
    }

    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
       return null;
    }

    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId, IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
        List<IDeviceCommandInvocation> result = new ArrayList<>();
        return result;
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
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId, IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
        List<IDeviceCommandResponse> result = new ArrayList<>();
        return result;
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
        List<IDeviceStateChange> result = new ArrayList<>();

        return result;
    }

    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        return null;
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     *
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = dbClient.getDbManager().getDeviceAssignmentRepository().findById(id);
        if (!opt.isPresent()) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
        }
        return opt.get();
    }

    protected IDeviceManagement getDeviceManagement() {
        return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }
}
