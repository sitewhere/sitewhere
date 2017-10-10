package com.sitewhere.grpc.model.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
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
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

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

    /** Tenant management GRPC channel */
    private DeviceEventManagementGrpcChannel grpcChannel;

    public DeviceEventManagementApiChannel(DeviceEventManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch
     * (java.lang.String, com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH);
	    GAddDeviceEventBatchRequest.Builder grequest = GAddDeviceEventBatchRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceEventBatch(batch));
	    GAddDeviceEventBatchResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addDeviceEventBatch(grequest.build());
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
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID);
	    GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
	    grequest.setId(id);
	    GGetDeviceEventByIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventById(grequest.build());
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent()) : null;
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
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID);
	    GGetDeviceEventByAlternateIdRequest.Builder grequest = GGetDeviceEventByAlternateIdRequest.newBuilder();
	    grequest.setAlternateId(alternateId);
	    GGetDeviceEventByAlternateIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventByAlternateId(grequest.build());
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent()) : null;
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
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(
     * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS);
	    GListDeviceEventsRequest.Builder grequest = GListDeviceEventsRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(EventModelConverter.asGrpcDeviceEventSearchCriteria(criteria));
	    GListDeviceEventsResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceEvents(grequest.build());
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
     * addDeviceMeasurements(java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
	    IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT);
	    GAddMeasurementsForAssignmentRequest.Builder grequest = GAddMeasurementsForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceMeasurementsCreateRequest(measurements));
	    GAddMeasurementsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addMeasurementsForAssignment(grequest.build());
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
     * listDeviceMeasurements(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT);
	    GListMeasurementsForAssignmentRequest.Builder grequest = GListMeasurementsForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listMeasurementsForAssignment(grequest.build());
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
     * listDeviceMeasurementsForSite(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE);
	    GListMeasurementsForSiteRequest.Builder grequest = GListMeasurementsForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listMeasurementsForSite(grequest.build());
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
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(
     * java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT);
	    GAddLocationForAssignmentRequest.Builder grequest = GAddLocationForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	    GAddLocationForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addLocationForAssignment(grequest.build());
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
     * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT);
	    GListLocationsForAssignmentRequest.Builder grequest = GListLocationsForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listLocationsForAssignment(grequest.build());
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
     * listDeviceLocationsForSite(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE);
	    GListLocationsForSiteRequest.Builder grequest = GListLocationsForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listLocationsForSite(grequest.build());
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
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlert(java
     * .lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT);
	    GAddAlertForAssignmentRequest.Builder grequest = GAddAlertForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	    GAddAlertForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addAlertForAssignment(grequest.build());
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
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlerts(
     * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT);
	    GListAlertsForAssignmentRequest.Builder grequest = GListAlertsForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listAlertsForAssignment(grequest.build());
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
     * listDeviceAlertsForSite(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE);
	    GListAlertsForSiteRequest.Builder grequest = GListAlertsForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listAlertsForSite(grequest.build());
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
     * (java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(String assignmentToken, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT);
	    GAddStreamDataForAssignmentRequest.Builder grequest = GAddStreamDataForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStreamDataCreateRequest(request));
	    GAddStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStreamDataForAssignment(grequest.build());
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
     * (java.lang.String, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT);
	    GGetStreamDataForAssignmentRequest.Builder grequest = GGetStreamDataForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setStreamId(streamId);
	    grequest.setSequenceNumber(sequenceNumber);
	    GGetStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getStreamDataForAssignment(grequest.build());
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
     * listDeviceStreamData(java.lang.String, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT);
	    GListStreamDataForAssignmentRequest.Builder grequest = GListStreamDataForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setStreamId(streamId);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStreamDataForAssignment(grequest.build());
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
     * addDeviceCommandInvocation(java.lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT);
	    GAddCommandInvocationForAssignmentRequest.Builder grequest = GAddCommandInvocationForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	    GAddCommandInvocationForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandInvocationForAssignment(grequest.build());
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
     * listDeviceCommandInvocations(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT);
	    GListCommandInvocationsForAssignmentRequest.Builder grequest = GListCommandInvocationsForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForAssignment(grequest.build());
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
     * listDeviceCommandInvocationsForSite(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE);
	    GListCommandInvocationsForSiteRequest.Builder grequest = GListCommandInvocationsForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForSite(grequest.build());
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
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION);
	    GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		    .newBuilder();
	    grequest.setInvocationId(invocationId);
	    GListCommandResponsesForInvocationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForInvocation(grequest.build());
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
     * addDeviceCommandResponse(java.lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT);
	    GAddCommandResponseForAssignmentRequest.Builder grequest = GAddCommandResponseForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	    GAddCommandResponseForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandResponseForAssignment(grequest.build());
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
     * listDeviceCommandResponses(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT);
	    GListCommandResponsesForAssignmentRequest.Builder grequest = GListCommandResponsesForAssignmentRequest
		    .newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForAssignment(grequest.build());
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
     * listDeviceCommandResponsesForSite(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE);
	    GListCommandResponsesForSiteRequest.Builder grequest = GListCommandResponsesForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForSite(grequest.build());
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
     * addDeviceStateChange(java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(String assignmentToken, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT);
	    GAddStateChangeForAssignmentRequest.Builder grequest = GAddStateChangeForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	    GAddStateChangeForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStateChangeForAssignment(grequest.build());
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
     * listDeviceStateChanges(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT);
	    GListStateChangesForAssignmentRequest.Builder grequest = GListStateChangesForAssignmentRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStateChangesForAssignment(grequest.build());
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
     * listDeviceStateChangesForSite(java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_SITE);
	    GListStateChangesForSiteRequest.Builder grequest = GListStateChangesForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStateChangesForSite(grequest.build());
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
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#updateDeviceEvent(
     * java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest)
     */
    @Override
    public IDeviceEvent updateDeviceEvent(String eventId, IDeviceEventCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT);
	    GUpdateDeviceEventRequest.Builder grequest = GUpdateDeviceEventRequest.newBuilder();
	    grequest.setId(eventId);
	    grequest.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(request));
	    GUpdateDeviceEventResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceEvent(grequest.build());
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
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public DeviceEventManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(DeviceEventManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
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