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
package com.sitewhere.event.persistence.influxdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.influxdb.dto.Point;

import com.google.inject.Inject;
import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
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
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Implementation of {@link IDeviceEventManagement} that stores events in
 * InfluxDB.
 */
public class InfluxDbDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Client */
    private InfluxDbClient client;

    /**
     * Prefix to compare against when adding user defined tags from assignment meta
     * data
     */
    private final String ASSIGNMENT_META_DATA_TAG_PREFIX = "INFLUX_TAG_";

    /** Assignment meta data tag to check for user defined retention policy */
    private final String ASSIGNMENT_META_DATA_RETENTION_POLICY = "INFLUX_RETENTION_POLICY";

    @Inject
    public InfluxDbDeviceEventManagement(InfluxDbClient client) {
	super(LifecycleComponentType.DataStore);
	this.client = client;
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#initializeParameters(
     * )
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
	getClient().initializeParameters();
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getClient().initialize(monitor);
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
	getClient().start(monitor);
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

    /**
     * Add any user defined tags from assignment metadata. A tag should be prefixed
     * with ASSIGNMENT_META_DATA_TAG_PREFIX i.e INFLUX_TAG_displayName. The prefix
     * will be removed and a new tag created using the remaining characters as the
     * tag name with value metadata.key assigned to it.
     *
     * @param context
     * @param builder
     */
    protected void addUserDefinedTags(IDeviceEventContext context, Point.Builder builder) {
	Map<String, String> assignmentMetaData = context.getDeviceAssignmentMetadata();

	if (assignmentMetaData != null) {
	    for (Map.Entry<String, String> metaData : assignmentMetaData.entrySet()) {
		String metaDataKey = metaData.getKey().trim();
		if (metaDataKey.length() == 0) {
		    continue;
		}

		String metaDataValue = metaData.getValue();
		if (metaDataValue == null) {
		    continue;
		}

		metaDataValue = metaDataValue.trim();
		if (metaDataValue.length() == 0) {
		    continue;
		}

		if (metaDataKey.startsWith(ASSIGNMENT_META_DATA_TAG_PREFIX)
			&& metaDataKey.length() > ASSIGNMENT_META_DATA_TAG_PREFIX.length()) {
		    InfluxDbDeviceEvent.addUserDefinedTag(metaDataKey.replaceFirst(ASSIGNMENT_META_DATA_TAG_PREFIX, ""),
			    metaDataValue, builder);
		}
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(java
     * .util.UUID)
     */
    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
	return InfluxDbDeviceEvent.getEventById(eventId, getClient());
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	return InfluxDbDeviceEvent.getEventByAlternateId(alternateId, getClient());
    }

    /**
     * Check if the user has specific a retention policy in the assignment meta-data
     * If so, override the default one.
     * 
     * @param context
     * @return
     */
    private String getAssignmentSpecificRetentionPolicy(IDeviceEventContext context) {
	String policy = context.getDeviceAssignmentMetadata().get(ASSIGNMENT_META_DATA_RETENTION_POLICY);

	if (policy == null) {
	    return getClient().getConfiguration().getRetention();
	}
	return policy;
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
	    DeviceMeasurement mxs = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(context, request);
	    Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
	    InfluxDbDeviceMeasurement.saveToBuilder(mxs, builder);
	    addUserDefinedTags(context, builder);
	    getClient().getInflux().write(getClient().getConfiguration().getDatabase(),
		    getAssignmentSpecificRetentionPolicy(context), builder.build());
	    result.add(mxs);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return InfluxDbDeviceEvent.searchByIndex(index, entityIds, DeviceEventType.Measurement, criteria, getClient(),
		IDeviceMeasurement.class);
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
	    Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
	    InfluxDbDeviceLocation.saveToBuilder(location, builder);
	    addUserDefinedTags(context, builder);
	    getClient().getInflux().write(getClient().getConfiguration().getDatabase(),
		    getAssignmentSpecificRetentionPolicy(context), builder.build());
	    result.add(location);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return InfluxDbDeviceEvent.searchByIndex(index, entityIds, DeviceEventType.Location, criteria, getClient(),
		IDeviceLocation.class);
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
	    Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
	    InfluxDbDeviceAlert.saveToBuilder(alert, builder);
	    addUserDefinedTags(context, builder);
	    getClient().getInflux().write(getClient().getConfiguration().getDatabase(),
		    getAssignmentSpecificRetentionPolicy(context), builder.build());
	    result.add(alert);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return InfluxDbDeviceEvent.searchByIndex(index, entityIds, DeviceEventType.Alert, criteria, getClient(),
		IDeviceAlert.class);
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
	    Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
	    InfluxDbDeviceCommandInvocation.saveToBuilder(ci, builder);
	    addUserDefinedTags(context, builder);
	    getClient().getInflux().write(getClient().getConfiguration().getDatabase(),
		    getAssignmentSpecificRetentionPolicy(context), builder.build());
	    result.add(ci);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return InfluxDbDeviceEvent.searchByIndex(index, entityIds, DeviceEventType.CommandInvocation, criteria,
		getClient(), IDeviceCommandInvocation.class);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId)
	    throws SiteWhereException {
	return InfluxDbDeviceCommandResponse.getResponsesForInvocation(invocationId, getClient().getInflux(),
		getClient().getConfiguration().getDatabase());
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
	    DeviceCommandResponse cr = DeviceEventManagementPersistence.deviceCommandResponseCreateLogic(context,
		    request);
	    Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
	    InfluxDbDeviceCommandResponse.saveToBuilder(cr, builder);
	    addUserDefinedTags(context, builder);
	    getClient().getInflux().write(getClient().getConfiguration().getDatabase(),
		    getAssignmentSpecificRetentionPolicy(context), builder.build());
	    result.add(cr);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return InfluxDbDeviceEvent.searchByIndex(index, entityIds, DeviceEventType.CommandResponse, criteria,
		getClient(), IDeviceCommandResponse.class);
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
	    DeviceStateChange sc = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(context, request);
	    Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
	    InfluxDbDeviceStateChange.saveToBuilder(sc, builder);
	    addUserDefinedTags(context, builder);
	    getClient().getInflux().write(getClient().getConfiguration().getDatabase(),
		    getAssignmentSpecificRetentionPolicy(context), builder.build());
	    result.add(sc);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return InfluxDbDeviceEvent.searchByIndex(index, entityIds, DeviceEventType.StateChange, criteria, getClient(),
		IDeviceStateChange.class);
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(id);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}
	return assignment;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    public InfluxDbClient getClient() {
	return client;
    }

    public void setClient(InfluxDbClient client) {
	this.client = client;
    }
}