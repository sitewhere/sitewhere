/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.grpc.model.tracing.DebugParameter;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.GAddAlertForAssignmentRequest;
import com.sitewhere.grpc.service.GAddAlertForAssignmentResponse;
import com.sitewhere.grpc.service.GAddCommandInvocationForAssignmentRequest;
import com.sitewhere.grpc.service.GAddCommandInvocationForAssignmentResponse;
import com.sitewhere.grpc.service.GAddCommandResponseForAssignmentRequest;
import com.sitewhere.grpc.service.GAddCommandResponseForAssignmentResponse;
import com.sitewhere.grpc.service.GAddDeviceEventBatchRequest;
import com.sitewhere.grpc.service.GAddDeviceEventBatchResponse;
import com.sitewhere.grpc.service.GAddLocationForAssignmentRequest;
import com.sitewhere.grpc.service.GAddLocationForAssignmentResponse;
import com.sitewhere.grpc.service.GAddMeasurementsForAssignmentRequest;
import com.sitewhere.grpc.service.GAddMeasurementsForAssignmentResponse;
import com.sitewhere.grpc.service.GAddStateChangeForAssignmentRequest;
import com.sitewhere.grpc.service.GAddStateChangeForAssignmentResponse;
import com.sitewhere.grpc.service.GAddStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GAddStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByIdResponse;
import com.sitewhere.grpc.service.GGetStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GGetStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GListAlertsForAssignmentRequest;
import com.sitewhere.grpc.service.GListAlertsForAssignmentResponse;
import com.sitewhere.grpc.service.GListAlertsForSiteRequest;
import com.sitewhere.grpc.service.GListAlertsForSiteResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForSiteRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForSiteResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForSiteRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForSiteResponse;
import com.sitewhere.grpc.service.GListDeviceEventsRequest;
import com.sitewhere.grpc.service.GListDeviceEventsResponse;
import com.sitewhere.grpc.service.GListLocationsForAssignmentRequest;
import com.sitewhere.grpc.service.GListLocationsForAssignmentResponse;
import com.sitewhere.grpc.service.GListLocationsForSiteRequest;
import com.sitewhere.grpc.service.GListLocationsForSiteResponse;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentRequest;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentResponse;
import com.sitewhere.grpc.service.GListMeasurementsForSiteRequest;
import com.sitewhere.grpc.service.GListMeasurementsForSiteResponse;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentRequest;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentResponse;
import com.sitewhere.grpc.service.GListStateChangesForSiteRequest;
import com.sitewhere.grpc.service.GListStateChangesForSiteResponse;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GUpdateDeviceEventRequest;
import com.sitewhere.grpc.service.GUpdateDeviceEventResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
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
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere device event management APIs on top of a
 * {@link DeviceEventManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class DeviceEventManagementApiChannel extends ApiChannel<DeviceEventManagementGrpcChannel>
	implements IDeviceEventManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public DeviceEventManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new DeviceEventManagementGrpcChannel(tracerProvider, host, port);
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Batch", batch));
	    GAddDeviceEventBatchRequest.Builder grequest = GAddDeviceEventBatchRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceEventBatch(batch));
	    GAddDeviceEventBatchResponse gresponse = getGrpcChannel().getBlockingStub().addDeviceEventBatch(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, grequest.build()));
	    IDeviceEventBatchResponse response = EventModelConverter
		    .asApiDeviceEventBatchResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID,
		    DebugParameter.create("Id", id));
	    GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
	    grequest.setId(id);
	    GGetDeviceEventByIdResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceEventById(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, grequest.build()));
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID,
		    DebugParameter.create("Alternate Id", alternateId));
	    GGetDeviceEventByAlternateIdRequest.Builder grequest = GGetDeviceEventByAlternateIdRequest.newBuilder();
	    grequest.setAlternateId(alternateId);
	    GGetDeviceEventByAlternateIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventByAlternateId(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID, grequest.build()));
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListDeviceEventsRequest.Builder grequest = GListDeviceEventsRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(EventModelConverter.asGrpcDeviceEventSearchCriteria(criteria));
	    GListDeviceEventsResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceEvents(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, grequest.build()));
	    ISearchResults<IDeviceEvent> results = EventModelConverter
		    .asApiDeviceEventSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Request", measurements));
	    GAddMeasurementsForAssignmentRequest.Builder grequest = GAddMeasurementsForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceMeasurementsCreateRequest(measurements));
	    GAddMeasurementsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addMeasurementsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceMeasurements response = EventModelConverter.asApiDeviceMeasurements(gresponse.getMeasurements());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListMeasurementsForAssignmentRequest.Builder grequest = GListMeasurementsForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listMeasurementsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceMeasurements> results = EventModelConverter
		    .asApiDeviceMeasurementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForSite(com.sitewhere.spi.device.ISite,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(ISite site,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE,
		    DebugParameter.create("Site", site), DebugParameter.create("Criteria", criteria));
	    GListMeasurementsForSiteRequest.Builder grequest = GListMeasurementsForSiteRequest.newBuilder();
	    grequest.setSite(DeviceModelConverter.asGrpcSite(site));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForSiteResponse gresponse = getGrpcChannel().getBlockingStub().listMeasurementsForSite(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE,
			    grequest.build()));
	    ISearchResults<IDeviceMeasurements> results = EventModelConverter
		    .asApiDeviceMeasurementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Request", request));
	    GAddLocationForAssignmentRequest.Builder grequest = GAddLocationForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	    GAddLocationForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub().addLocationForAssignment(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT,
			    grequest.build()));
	    IDeviceLocation response = EventModelConverter.asApiDeviceLocation(gresponse.getLocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT,
		    t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListLocationsForAssignmentRequest.Builder grequest = GListLocationsForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listLocationsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceLocation> results = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForSite(com.sitewhere.spi.device.ISite,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(ISite site, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE,
		    DebugParameter.create("Site", site), DebugParameter.create("Criteria", criteria));
	    GListLocationsForSiteRequest.Builder grequest = GListLocationsForSiteRequest.newBuilder();
	    grequest.setSite(DeviceModelConverter.asGrpcSite(site));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForSiteResponse gresponse = getGrpcChannel().getBlockingStub().listLocationsForSite(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE, grequest.build()));
	    ISearchResults<IDeviceLocation> results = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Request", request));
	    GAddAlertForAssignmentRequest.Builder grequest = GAddAlertForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	    GAddAlertForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub().addAlertForAssignment(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT,
			    grequest.build()));
	    IDeviceAlert response = EventModelConverter.asApiDeviceAlert(gresponse.getAlert());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListAlertsForAssignmentRequest.Builder grequest = GListAlertsForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub().listAlertsForAssignment(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT,
			    grequest.build()));
	    ISearchResults<IDeviceAlert> results = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForSite(com.sitewhere.spi.device.ISite,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(ISite site, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE,
		    DebugParameter.create("Site", site), DebugParameter.create("Criteria", criteria));
	    GListAlertsForSiteRequest.Builder grequest = GListAlertsForSiteRequest.newBuilder();
	    grequest.setSite(DeviceModelConverter.asGrpcSite(site));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForSiteResponse gresponse = getGrpcChannel().getBlockingStub().listAlertsForSite(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE, grequest.build()));
	    ISearchResults<IDeviceAlert> results = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Stream", stream),
		    DebugParameter.create("Request", request));
	    GAddStreamDataForAssignmentRequest.Builder grequest = GAddStreamDataForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(stream));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStreamDataCreateRequest(request));
	    GAddStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceStreamData response = EventModelConverter.asApiDeviceStreamData(gresponse.getStreamData());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT,
		    t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Stream Id", streamId),
		    DebugParameter.create("Sequence Number", String.valueOf(sequenceNumber)));
	    GGetStreamDataForAssignmentRequest.Builder grequest = GGetStreamDataForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setStreamId(streamId);
	    grequest.setSequenceNumber(sequenceNumber);
	    GGetStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceStreamData results = EventModelConverter.asApiDeviceStreamData(gresponse.getStreamData());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT,
		    t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Stream Id", streamId),
		    DebugParameter.create("Criteria", criteria));
	    GListStreamDataForAssignmentRequest.Builder grequest = GListStreamDataForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setStreamId(streamId);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceStreamData> results = EventModelConverter
		    .asApiDeviceStreamDataSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Request", request));
	    GAddCommandInvocationForAssignmentRequest.Builder grequest = GAddCommandInvocationForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	    GAddCommandInvocationForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandInvocationForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceCommandInvocation response = EventModelConverter
		    .asApiDeviceCommandInvocation(gresponse.getInvocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListCommandInvocationsForAssignmentRequest.Builder grequest = GListCommandInvocationsForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
			    grequest.build()));
	    ISearchResults<IDeviceCommandInvocation> results = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForSite(com.sitewhere.spi.device.ISite,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(ISite site,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE,
		    DebugParameter.create("Site", site), DebugParameter.create("Criteria", criteria));
	    GListCommandInvocationsForSiteRequest.Builder grequest = GListCommandInvocationsForSiteRequest.newBuilder();
	    grequest.setSite(DeviceModelConverter.asGrpcSite(site));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForSite(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE, grequest.build()));
	    ISearchResults<IDeviceCommandInvocation> results = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION,
		    DebugParameter.create("Invocation Id", invocationId));
	    GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		    .newBuilder();
	    grequest.setInvocationId(invocationId);
	    GListCommandResponsesForInvocationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForInvocation(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Request", request));
	    GAddCommandResponseForAssignmentRequest.Builder grequest = GAddCommandResponseForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	    GAddCommandResponseForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandResponseForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceCommandResponse response = EventModelConverter.asApiDeviceCommandResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListCommandResponsesForAssignmentRequest.Builder grequest = GListCommandResponsesForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForSite(com.sitewhere.spi.device.ISite,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(ISite site,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE,
		    DebugParameter.create("Site", site), DebugParameter.create("Criteria", criteria));
	    GListCommandResponsesForSiteRequest.Builder grequest = GListCommandResponsesForSiteRequest.newBuilder();
	    grequest.setSite(DeviceModelConverter.asGrpcSite(site));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForSite(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Request", request));
	    GAddStateChangeForAssignmentRequest.Builder grequest = GAddStateChangeForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	    GAddStateChangeForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStateChangeForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceStateChange response = EventModelConverter.asApiDeviceStateChange(gresponse.getStateChange());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT, t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment", assignment), DebugParameter.create("Criteria", criteria));
	    GListStateChangesForAssignmentRequest.Builder grequest = GListStateChangesForAssignmentRequest.newBuilder();
	    grequest.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(assignment));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStateChangesForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceStateChange> results = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForSite(com.sitewhere.spi.device.ISite,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(ISite site,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_SITE,
		    DebugParameter.create("Site", site), DebugParameter.create("Criteria", criteria));
	    GListStateChangesForSiteRequest.Builder grequest = GListStateChangesForSiteRequest.newBuilder();
	    grequest.setSite(DeviceModelConverter.asGrpcSite(site));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForSiteResponse gresponse = getGrpcChannel().getBlockingStub().listStateChangesForSite(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_SITE,
			    grequest.build()));
	    ISearchResults<IDeviceStateChange> results = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_SITE, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_SITE,
		    t);
	}
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
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT,
		    DebugParameter.create("Event Id", eventId), DebugParameter.create("Request", request));
	    GUpdateDeviceEventRequest.Builder grequest = GUpdateDeviceEventRequest.newBuilder();
	    grequest.setId(eventId);
	    grequest.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(request));
	    GUpdateDeviceEventResponse gresponse = getGrpcChannel().getBlockingStub().updateDeviceEvent(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT, grequest.build()));
	    IDeviceEvent response = EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}