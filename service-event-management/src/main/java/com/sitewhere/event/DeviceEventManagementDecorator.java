/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event;

import java.util.List;
import java.util.UUID;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Allows classes to inject themselves as a facade around an existing device
 * event management implementation. By default all methods just pass calls to
 * the underlying delegate.
 * 
 * @author Derek
 */
public class DeviceEventManagementDecorator extends LifecycleComponentDecorator<IDeviceEventManagement>
	implements IDeviceEventManagement {

    public DeviceEventManagementDecorator(IDeviceEventManagement delegate) {
	super(delegate);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	getDelegate().setTenantEngine(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return getDelegate().getTenantEngine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch
     * (com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(IDeviceAssignment assignment, IDeviceEventBatch batch)
	    throws SiteWhereException {
	return getDelegate().addDeviceEventBatch(assignment, batch);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(
     * java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
	return getDelegate().getDeviceEventById(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	return getDelegate().getDeviceEventByAlternateId(alternateId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceEvent> listDeviceEvents(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceEvents(assignment, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceMeasurements(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
	    IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
	return getDelegate().addDeviceMeasurements(assignment, measurements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurements(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceMeasurements(assignment, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceMeasurementsForAreas(areaIds, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().addDeviceLocation(assignment, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations
     * (com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocations(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceLocations(assignment, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceLocationsForAreas(areaIds, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlert(com.
     * sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().addDeviceAlert(assignment, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlerts(
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlerts(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceAlerts(assignment, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAreas(List<UUID> areaIds, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDeviceAlertsForAreas(areaIds, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStreamData
     * (com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.streaming.IDeviceStream,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(IDeviceAssignment assignment, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	return getDelegate().addDeviceStreamData(assignment, stream, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData
     * (com.sitewhere.spi.device.IDeviceAssignment, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(IDeviceAssignment assignment, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	return getDelegate().getDeviceStreamData(assignment, streamId, sequenceNumber);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStreamData(com.sitewhere.spi.device.IDeviceAssignment,
     * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamData(IDeviceAssignment assignment, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceStreamData(assignment, streamId, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandInvocation(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(IDeviceAssignment assignment,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	return getDelegate().addDeviceCommandInvocation(assignment, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocations(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceCommandInvocations(assignment, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceCommandInvocationsForAreas(areaIds, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.lang.String)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
	    throws SiteWhereException {
	return getDelegate().listDeviceCommandInvocationResponses(invocationId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandResponse(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request. IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(IDeviceAssignment assignment,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	return getDelegate().addDeviceCommandResponse(assignment, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponses(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceCommandResponses(assignment, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceCommandResponsesForAreas(areaIds, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceStateChange(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(IDeviceAssignment assignment,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException {
	return getDelegate().addDeviceStateChange(assignment, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChanges(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChanges(IDeviceAssignment assignment,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceStateChanges(assignment, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceStateChangesForAreas(areaIds, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#updateDeviceEvent(
     * java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest)
     */
    @Override
    public IDeviceEvent updateDeviceEvent(String eventId, IDeviceEventCreateRequest request) throws SiteWhereException {
	return getDelegate().updateDeviceEvent(eventId, request);
    }
}