package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.sitewhere.grpc.model.CommonModel.GOptionalBoolean;
import com.sitewhere.grpc.model.CommonModel.GOptionalDouble;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.DeviceEventModel.GAlertLevel;
import com.sitewhere.grpc.model.DeviceEventModel.GAlertSource;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEvent;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlert;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEvent;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventBatchCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventBatchResponse;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocation;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurements;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementsCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStreamData;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStreamDataCreateRequest;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStreamDataCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;

/**
 * Convert device event entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class EventModelConverter {

    /**
     * Copy common device event create request fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static void copyApiDeviceEventCreateRequest(GDeviceEventCreateRequest grpc, DeviceEventCreateRequest api)
	    throws SiteWhereException {
	api.setAlternateId(grpc.hasAlternateId() ? grpc.getAlternateId().getValue() : null);
	api.setEventDate(grpc.hasEventDate() ? CommonModelConverter.asDate(grpc.getEventDate()) : null);
	api.setUpdateState(grpc.hasUpdateState() ? grpc.getUpdateState().getValue() : false);
	api.setMetadata(grpc.getMetadataMap());
    }

    /**
     * Convert generic device event create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventCreateRequest asApiDeviceEventCreateRequest(GDeviceEventCreateRequest grpc)
	    throws SiteWhereException {
	DeviceEventCreateRequest api = new DeviceEventCreateRequest();
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc, api);
	return api;
    }

    /**
     * Create GRPC device event create request common base values.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventCreateRequest createGrpcDeviceEventCreateRequest(IDeviceEventCreateRequest api)
	    throws SiteWhereException {
	GDeviceEventCreateRequest.Builder grpc = GDeviceEventCreateRequest.newBuilder();
	if (api.getAlternateId() != null) {
	    grpc.setAlternateId(GOptionalString.newBuilder().setValue(api.getAlternateId()).build());
	}
	if (api.getEventDate() != null) {
	    grpc.setEventDate(CommonModelConverter.asGrpcTimestamp(api.getEventDate()));
	}
	if (api.isUpdateState()) {
	    grpc.setUpdateState(GOptionalBoolean.newBuilder().setValue(true).build());
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Copy common device event fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static void copyApiDeviceEvent(GDeviceEvent grpc, DeviceEvent api) throws SiteWhereException {
	api.setAlternateId(grpc.getAlternateId());
	api.setEventDate(grpc.hasEventDate() ? CommonModelConverter.asDate(grpc.getEventDate()) : null);
	api.setMetadata(grpc.getMetadataMap());
    }

    /**
     * Create GRPC device event common base values.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEvent createGrpcDeviceEvent(IDeviceEvent api) throws SiteWhereException {
	GDeviceEvent.Builder grpc = GDeviceEvent.newBuilder();
	grpc.setAlternateId(api.getAlternateId());
	if (api.getEventDate() != null) {
	    grpc.setEventDate(CommonModelConverter.asGrpcTimestamp(api.getEventDate()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device measurements create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurementsCreateRequest asApiDeviceMeasurementsCreateRequest(
	    GDeviceMeasurementsCreateRequest grpc) throws SiteWhereException {
	DeviceMeasurementsCreateRequest api = new DeviceMeasurementsCreateRequest();
	api.getMeasurements().putAll(grpc.getMeasurementsMap());
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device measurements create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceMeasurementsCreateRequest asGrpcDeviceMeasurementsCreateRequest(
	    IDeviceMeasurementsCreateRequest api) throws SiteWhereException {
	GDeviceMeasurementsCreateRequest.Builder grpc = GDeviceMeasurementsCreateRequest.newBuilder();
	grpc.putAllMeasurements(api.getMeasurements());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device measurements create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceMeasurementsCreateRequest> asApiDeviceMeasurementsCreateRequests(
	    List<GDeviceMeasurementsCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceMeasurementsCreateRequest> api = new ArrayList<DeviceMeasurementsCreateRequest>();
	for (GDeviceMeasurementsCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceMeasurementsCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device measurements create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceMeasurementsCreateRequest> asGrpcDeviceMeasurementsCreateRequests(
	    List<IDeviceMeasurementsCreateRequest> apis) throws SiteWhereException {
	List<GDeviceMeasurementsCreateRequest> grpcs = new ArrayList<GDeviceMeasurementsCreateRequest>();
	for (IDeviceMeasurementsCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceMeasurementsCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device measurements event from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurements asApiDeviceMeasurements(GDeviceMeasurements grpc) throws SiteWhereException {
	DeviceMeasurements api = new DeviceMeasurements();
	api.getMeasurements().putAll(grpc.getMeasurementsMap());
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device measurements event from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceMeasurements asGrpcDeviceMeasurements(IDeviceMeasurements api) throws SiteWhereException {
	GDeviceMeasurements.Builder grpc = GDeviceMeasurements.newBuilder();
	grpc.putAllMeasurements(api.getMeasurements());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device measurements from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceMeasurements> asApiDeviceMeasurements(List<GDeviceMeasurements> grpcs)
	    throws SiteWhereException {
	List<DeviceMeasurements> api = new ArrayList<DeviceMeasurements>();
	for (GDeviceMeasurements grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceMeasurements(grpc));
	}
	return api;
    }

    /**
     * Convert device measurements from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceMeasurements> asGrpcDeviceMeasurements(List<IDeviceMeasurements> apis)
	    throws SiteWhereException {
	List<GDeviceMeasurements> grpcs = new ArrayList<GDeviceMeasurements>();
	for (IDeviceMeasurements api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceMeasurements(api));
	}
	return grpcs;
    }

    /**
     * Convert alert source from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AlertSource asApiAlertSource(GAlertSource grpc) throws SiteWhereException {
	switch (grpc) {
	case ALERT_SOURCE_DEVICE:
	    return AlertSource.Device;
	case ALERT_SOURCE_SYSTEM:
	    return AlertSource.System;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown alert source: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert alert source from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAlertSource asGrpcAlertSource(AlertSource api) throws SiteWhereException {
	switch (api) {
	case Device:
	    return GAlertSource.ALERT_SOURCE_DEVICE;
	case System:
	    return GAlertSource.ALERT_SOURCE_SYSTEM;
	}
	throw new SiteWhereException("Unknown alert source: " + api.name());
    }

    /**
     * Convert alert level from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AlertLevel asApiAlertLevel(GAlertLevel grpc) throws SiteWhereException {
	switch (grpc) {
	case ALERT_LEVEL_CRITICAL:
	    return AlertLevel.Critical;
	case ALERT_LEVEL_ERROR:
	    return AlertLevel.Error;
	case ALERT_LEVEL_INFO:
	    return AlertLevel.Info;
	case ALERT_LEVEL_WARNING:
	    return AlertLevel.Warning;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown alert level: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert alert level from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAlertLevel asGrpcAlertLevel(AlertLevel api) throws SiteWhereException {
	switch (api) {
	case Critical:
	    return GAlertLevel.ALERT_LEVEL_CRITICAL;
	case Error:
	    return GAlertLevel.ALERT_LEVEL_ERROR;
	case Info:
	    return GAlertLevel.ALERT_LEVEL_INFO;
	case Warning:
	    return GAlertLevel.ALERT_LEVEL_WARNING;
	}
	throw new SiteWhereException("Unknown alert level: " + api.name());
    }

    /**
     * Convert device alert create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlertCreateRequest asApiDeviceAlertCreateRequest(GDeviceAlertCreateRequest grpc)
	    throws SiteWhereException {
	DeviceAlertCreateRequest api = new DeviceAlertCreateRequest();
	api.setSource(EventModelConverter.asApiAlertSource(grpc.getSource()));
	api.setLevel(EventModelConverter.asApiAlertLevel(grpc.getLevel()));
	api.setType(grpc.getType());
	api.setMessage(grpc.getAlertMessage());
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device alert create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlertCreateRequest asGrpcDeviceAlertCreateRequest(IDeviceAlertCreateRequest api)
	    throws SiteWhereException {
	GDeviceAlertCreateRequest.Builder grpc = GDeviceAlertCreateRequest.newBuilder();
	grpc.setSource(EventModelConverter.asGrpcAlertSource(api.getSource()));
	grpc.setLevel(EventModelConverter.asGrpcAlertLevel(api.getLevel()));
	grpc.setType(grpc.getType());
	grpc.setAlertMessage(api.getMessage());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device alert create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceAlertCreateRequest> asApiDeviceAlertCreateRequests(List<GDeviceAlertCreateRequest> grpcs)
	    throws SiteWhereException {
	List<DeviceAlertCreateRequest> api = new ArrayList<DeviceAlertCreateRequest>();
	for (GDeviceAlertCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceAlertCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device alert create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceAlertCreateRequest> asGrpcDeviceAlertCreateRequests(List<IDeviceAlertCreateRequest> apis)
	    throws SiteWhereException {
	List<GDeviceAlertCreateRequest> grpcs = new ArrayList<GDeviceAlertCreateRequest>();
	for (IDeviceAlertCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceAlertCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device alert from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlert asApiDeviceAlert(GDeviceAlert grpc) throws SiteWhereException {
	DeviceAlert api = new DeviceAlert();
	api.setSource(EventModelConverter.asApiAlertSource(grpc.getSource()));
	api.setLevel(EventModelConverter.asApiAlertLevel(grpc.getLevel()));
	api.setType(grpc.getType());
	api.setMessage(grpc.getAlertMessage());
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device alert from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlert asGrpcDeviceAlert(IDeviceAlert api) throws SiteWhereException {
	GDeviceAlert.Builder grpc = GDeviceAlert.newBuilder();
	grpc.setSource(EventModelConverter.asGrpcAlertSource(api.getSource()));
	grpc.setLevel(EventModelConverter.asGrpcAlertLevel(api.getLevel()));
	grpc.setType(grpc.getType());
	grpc.setAlertMessage(api.getMessage());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device alerts from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceAlert> asApiDeviceAlerts(List<GDeviceAlert> grpcs) throws SiteWhereException {
	List<DeviceAlert> api = new ArrayList<DeviceAlert>();
	for (GDeviceAlert grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceAlert(grpc));
	}
	return api;
    }

    /**
     * Convert device alerts from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceAlert> asGrpcDeviceAlerts(List<IDeviceAlert> apis) throws SiteWhereException {
	List<GDeviceAlert> grpcs = new ArrayList<GDeviceAlert>();
	for (IDeviceAlert api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceAlert(api));
	}
	return grpcs;
    }

    /**
     * Convert device location create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocationCreateRequest asApiDeviceLocationCreateRequest(GDeviceLocationCreateRequest grpc)
	    throws SiteWhereException {
	DeviceLocationCreateRequest api = new DeviceLocationCreateRequest();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : null);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : null);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : null);
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device location create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceLocationCreateRequest asGrpcDeviceLocationCreateRequest(IDeviceLocationCreateRequest api)
	    throws SiteWhereException {
	GDeviceLocationCreateRequest.Builder grpc = GDeviceLocationCreateRequest.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device location create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceLocationCreateRequest> asApiDeviceLocationCreateRequests(
	    List<GDeviceLocationCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceLocationCreateRequest> api = new ArrayList<DeviceLocationCreateRequest>();
	for (GDeviceLocationCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceLocationCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device location create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceLocationCreateRequest> asGrpcDeviceLocationCreateRequests(
	    List<IDeviceLocationCreateRequest> apis) throws SiteWhereException {
	List<GDeviceLocationCreateRequest> grpcs = new ArrayList<GDeviceLocationCreateRequest>();
	for (IDeviceLocationCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceLocationCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device location from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocation asApiDeviceLocation(GDeviceLocation grpc) throws SiteWhereException {
	DeviceLocation api = new DeviceLocation();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : null);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : null);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : null);
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device location create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceLocation asGrpcDeviceLocation(IDeviceLocation api) throws SiteWhereException {
	GDeviceLocation.Builder grpc = GDeviceLocation.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device locations from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceLocation> asApiDeviceLocations(List<GDeviceLocation> grpcs) throws SiteWhereException {
	List<DeviceLocation> api = new ArrayList<DeviceLocation>();
	for (GDeviceLocation grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceLocation(grpc));
	}
	return api;
    }

    /**
     * Convert device locations from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceLocation> asGrpcDeviceLocations(List<IDeviceLocation> apis) throws SiteWhereException {
	List<GDeviceLocation> grpcs = new ArrayList<GDeviceLocation>();
	for (IDeviceLocation api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceLocation(api));
	}
	return grpcs;
    }

    /**
     * Convert device stream data create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStreamDataCreateRequest asApiDeviceStreamDataCreateRequest(GDeviceStreamDataCreateRequest grpc)
	    throws SiteWhereException {
	DeviceStreamDataCreateRequest api = new DeviceStreamDataCreateRequest();
	api.setStreamId(grpc.getStreamId());
	api.setSequenceNumber(grpc.getSequenceNumber());
	api.setData(grpc.getData().toByteArray());
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device stream data create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStreamDataCreateRequest asGrpcDeviceStreamDataCreateRequest(IDeviceStreamDataCreateRequest api)
	    throws SiteWhereException {
	GDeviceStreamDataCreateRequest.Builder grpc = GDeviceStreamDataCreateRequest.newBuilder();
	grpc.setStreamId(api.getStreamId());
	grpc.setSequenceNumber(api.getSequenceNumber());
	grpc.setData(ByteString.copyFrom(api.getData()));
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device stream data create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceStreamDataCreateRequest> asApiDeviceStreamDataCreateRequests(
	    List<GDeviceStreamDataCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceStreamDataCreateRequest> api = new ArrayList<DeviceStreamDataCreateRequest>();
	for (GDeviceStreamDataCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceStreamDataCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device stream data create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceStreamDataCreateRequest> asGrpcDeviceStreamDataCreateRequests(
	    List<IDeviceStreamDataCreateRequest> apis) throws SiteWhereException {
	List<GDeviceStreamDataCreateRequest> grpcs = new ArrayList<GDeviceStreamDataCreateRequest>();
	for (IDeviceStreamDataCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceStreamDataCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device stream data from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStreamData asApiDeviceStreamData(GDeviceStreamData grpc) throws SiteWhereException {
	DeviceStreamData api = new DeviceStreamData();
	api.setStreamId(grpc.getStreamId());
	api.setSequenceNumber(grpc.getSequenceNumber());
	api.setData(grpc.getData().toByteArray());
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device stream data create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStreamData asGrpcDeviceStreamData(IDeviceStreamData api) throws SiteWhereException {
	GDeviceStreamData.Builder grpc = GDeviceStreamData.newBuilder();
	grpc.setStreamId(api.getStreamId());
	grpc.setSequenceNumber(api.getSequenceNumber());
	grpc.setData(ByteString.copyFrom(api.getData()));
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device stream data from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceStreamData> asApiDeviceStreamData(List<GDeviceStreamData> grpcs)
	    throws SiteWhereException {
	List<DeviceStreamData> api = new ArrayList<DeviceStreamData>();
	for (GDeviceStreamData grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceStreamData(grpc));
	}
	return api;
    }

    /**
     * Convert device stream data from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceStreamData> asGrpcDeviceStreamData(List<IDeviceStreamData> apis)
	    throws SiteWhereException {
	List<GDeviceStreamData> grpcs = new ArrayList<GDeviceStreamData>();
	for (IDeviceStreamData api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceStreamData(api));
	}
	return grpcs;
    }

    /**
     * Convert device event batch from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventBatch asApiDeviceEventBatch(GDeviceEventBatchCreateRequest grpc)
	    throws SiteWhereException {
	DeviceEventBatch api = new DeviceEventBatch();
	api.setHardwareId(grpc.getHardwareId());
	api.setMeasurements(EventModelConverter.asApiDeviceMeasurementsCreateRequests(grpc.getMeasurementsList()));
	api.setAlerts(EventModelConverter.asApiDeviceAlertCreateRequests(grpc.getAlertsList()));
	api.setLocations(EventModelConverter.asApiDeviceLocationCreateRequests(grpc.getLocationsList()));
	return api;
    }

    /**
     * Convert device event batch from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventBatchCreateRequest asGrpcDeviceEventBatch(IDeviceEventBatch api)
	    throws SiteWhereException {
	GDeviceEventBatchCreateRequest.Builder grpc = GDeviceEventBatchCreateRequest.newBuilder();
	grpc.setHardwareId(api.getHardwareId());
	grpc.addAllMeasurements(EventModelConverter.asGrpcDeviceMeasurementsCreateRequests(api.getMeasurements()));
	grpc.addAllAlerts(EventModelConverter.asGrpcDeviceAlertCreateRequests(api.getAlerts()));
	grpc.addAllLocations(EventModelConverter.asGrpcDeviceLocationCreateRequests(api.getLocations()));
	return grpc.build();
    }

    /**
     * Convert device event batch response from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventBatchResponse asGrpcDeviceEventBatchResponse(IDeviceEventBatchResponse api)
	    throws SiteWhereException {
	GDeviceEventBatchResponse.Builder grpc = GDeviceEventBatchResponse.newBuilder();
	grpc.addAllMeasurements(EventModelConverter.asGrpcDeviceMeasurements(api.getCreatedMeasurements()));
	grpc.addAllAlerts(EventModelConverter.asGrpcDeviceAlerts(api.getCreatedAlerts()));
	grpc.addAllLocations(EventModelConverter.asGrpcDeviceLocations(api.getCreatedLocations()));
	return grpc.build();
    }

    /**
     * Convert generic event from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAnyDeviceEvent asGenericDeviceEvent(IDeviceEvent api) throws SiteWhereException {
	GAnyDeviceEvent.Builder grpc = GAnyDeviceEvent.newBuilder();
	switch (api.getEventType()) {
	case Measurements:
	    grpc.setMeasurements(EventModelConverter.asGrpcDeviceMeasurements((IDeviceMeasurements) api));
	    break;
	case Alert:
	    grpc.setAlert(EventModelConverter.asGrpcDeviceAlert((IDeviceAlert) api));
	    break;
	case Location:
	    grpc.setLocation(EventModelConverter.asGrpcDeviceLocation((IDeviceLocation) api));
	    break;
	default:
	    throw new SiteWhereException("Unable to convert event to GRPC. " + api.getClass().getName());
	}

	return grpc.build();
    }
}