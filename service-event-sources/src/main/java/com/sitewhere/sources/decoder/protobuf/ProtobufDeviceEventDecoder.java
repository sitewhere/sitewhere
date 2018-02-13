/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.communication.protobuf.proto.Sitewhere.SiteWhere;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.DeviceAlert;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.DeviceLocation;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.DeviceMeasurements;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.DeviceStream;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.DeviceStreamData;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.Measurement;
import com.sitewhere.communication.protobuf.proto.Sitewhere.Model.Metadata;
import com.sitewhere.communication.protobuf.proto.Sitewhere.SiteWhere.Acknowledge;
import com.sitewhere.communication.protobuf.proto.Sitewhere.SiteWhere.DeviceStreamDataRequest;
import com.sitewhere.communication.protobuf.proto.Sitewhere.SiteWhere.Header;
import com.sitewhere.communication.protobuf.proto.Sitewhere.SiteWhere.RegisterDevice;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStreamDataCreateRequest;
import com.sitewhere.rest.model.device.event.request.SendDeviceStreamDataRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Decodes a message payload that was previously encoded using the Google
 * Protocol Buffers with the SiteWhere proto.
 * 
 * @author Derek
 */
public class ProtobufDeviceEventDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public ProtobufDeviceEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, Object> payloadMetadata)
	    throws EventDecodeException {
	try {
	    ByteArrayInputStream stream = new ByteArrayInputStream(payload);
	    Header header = SiteWhere.Header.parseDelimitedFrom(stream);
	    List<IDecodedDeviceRequest<?>> results = new ArrayList<IDecodedDeviceRequest<?>>();
	    switch (header.getCommand()) {
	    case SEND_REGISTRATION: {
		RegisterDevice register = RegisterDevice.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded registration for: " + register.getHardwareId());
		DeviceRegistrationRequest request = new DeviceRegistrationRequest();
		request.setHardwareId(register.getHardwareId());
		request.setDeviceTypeToken(register.getDeviceTypeToken());
		if (register.hasAreaToken()) {
		    request.setAreaToken(register.getAreaToken());
		}

		List<Metadata> pbmeta = register.getMetadataList();
		Map<String, String> metadata = new HashMap<String, String>();
		for (Metadata meta : pbmeta) {
		    metadata.put(meta.getName(), meta.getValue());
		}
		request.setMetadata(metadata);

		DecodedDeviceRequest<IDeviceRegistrationRequest> decoded = new DecodedDeviceRequest<IDeviceRegistrationRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(register.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case SEND_ACKNOWLEDGEMENT: {
		Acknowledge ack = Acknowledge.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded acknowledge for: " + ack.getHardwareId());
		DeviceCommandResponseCreateRequest request = new DeviceCommandResponseCreateRequest();
		request.setOriginatingEventId(header.getOriginator());
		request.setResponse(ack.getMessage());

		DecodedDeviceRequest<IDeviceCommandResponseCreateRequest> decoded = new DecodedDeviceRequest<IDeviceCommandResponseCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(ack.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case SEND_DEVICE_MEASUREMENTS: {
		DeviceMeasurements dm = DeviceMeasurements.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded measurement for: " + dm.getHardwareId());
		DeviceMeasurementsCreateRequest request = new DeviceMeasurementsCreateRequest();
		List<Measurement> measurements = dm.getMeasurementList();
		for (Measurement current : measurements) {
		    request.addOrReplaceMeasurement(current.getMeasurementId(), current.getMeasurementValue());
		}

		if (dm.hasUpdateState()) {
		    request.setUpdateState(dm.getUpdateState());
		}

		List<Metadata> pbmeta = dm.getMetadataList();
		Map<String, String> metadata = new HashMap<String, String>();
		for (Metadata meta : pbmeta) {
		    metadata.put(meta.getName(), meta.getValue());
		}
		request.setMetadata(metadata);

		if (dm.hasEventDate()) {
		    request.setEventDate(new Date(dm.getEventDate()));
		} else {
		    request.setEventDate(new Date());
		}

		DecodedDeviceRequest<IDeviceMeasurementsCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementsCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(dm.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case SEND_DEVICE_LOCATION: {
		DeviceLocation location = DeviceLocation.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded location for: " + location.getHardwareId());
		DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
		request.setLatitude(location.getLatitude());
		request.setLongitude(location.getLongitude());
		request.setElevation(location.getElevation());

		if (location.hasUpdateState()) {
		    request.setUpdateState(location.getUpdateState());
		}

		List<Metadata> pbmeta = location.getMetadataList();
		Map<String, String> metadata = new HashMap<String, String>();
		for (Metadata meta : pbmeta) {
		    metadata.put(meta.getName(), meta.getValue());
		}
		request.setMetadata(metadata);

		if (location.hasEventDate()) {
		    request.setEventDate(new Date(location.getEventDate()));
		} else {
		    request.setEventDate(new Date());
		}

		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(location.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case SEND_DEVICE_ALERT: {
		DeviceAlert alert = DeviceAlert.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded alert for: " + alert.getHardwareId());
		DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
		request.setType(alert.getAlertType());
		request.setMessage(alert.getAlertMessage());
		request.setLevel(AlertLevel.Info);

		if (alert.hasUpdateState()) {
		    request.setUpdateState(alert.getUpdateState());
		}

		List<Metadata> pbmeta = alert.getMetadataList();
		Map<String, String> metadata = new HashMap<String, String>();
		for (Metadata meta : pbmeta) {
		    metadata.put(meta.getName(), meta.getValue());
		}
		request.setMetadata(metadata);

		if (alert.hasEventDate()) {
		    request.setEventDate(new Date(alert.getEventDate()));
		} else {
		    request.setEventDate(new Date());
		}

		DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(alert.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case SEND_DEVICE_STREAM: {
		DeviceStream devStream = DeviceStream.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded stream for: " + devStream.getHardwareId());
		DeviceStreamCreateRequest request = new DeviceStreamCreateRequest();
		request.setStreamId(devStream.getStreamId());
		request.setContentType(devStream.getContentType());

		List<Metadata> pbmeta = devStream.getMetadataList();
		Map<String, String> metadata = new HashMap<String, String>();
		for (Metadata meta : pbmeta) {
		    metadata.put(meta.getName(), meta.getValue());
		}
		request.setMetadata(metadata);

		DecodedDeviceRequest<IDeviceStreamCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(devStream.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case SEND_DEVICE_STREAM_DATA: {
		DeviceStreamData streamData = DeviceStreamData.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded stream data for: " + streamData.getHardwareId());
		DeviceStreamDataCreateRequest request = new DeviceStreamDataCreateRequest();
		request.setStreamId(streamData.getStreamId());
		request.setSequenceNumber(streamData.getSequenceNumber());
		request.setData(streamData.getData().toByteArray());

		List<Metadata> pbmeta = streamData.getMetadataList();
		Map<String, String> metadata = new HashMap<String, String>();
		for (Metadata meta : pbmeta) {
		    metadata.put(meta.getName(), meta.getValue());
		}
		request.setMetadata(metadata);

		if (streamData.hasEventDate()) {
		    request.setEventDate(new Date(streamData.getEventDate()));
		} else {
		    request.setEventDate(new Date());
		}

		DecodedDeviceRequest<IDeviceStreamDataCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamDataCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(streamData.getHardwareId());
		decoded.setRequest(request);
		return results;
	    }
	    case REQUEST_DEVICE_STREAM_DATA: {
		DeviceStreamDataRequest request = DeviceStreamDataRequest.parseDelimitedFrom(stream);
		LOGGER.debug("Decoded stream data request for: " + request.getHardwareId());
		SendDeviceStreamDataRequest send = new SendDeviceStreamDataRequest();
		send.setStreamId(request.getStreamId());
		send.setSequenceNumber(request.getSequenceNumber());

		DecodedDeviceRequest<ISendDeviceStreamDataRequest> decoded = new DecodedDeviceRequest<ISendDeviceStreamDataRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator());
		}
		results.add(decoded);
		decoded.setHardwareId(request.getHardwareId());
		decoded.setRequest(send);
		return results;
	    }
	    default: {
		throw new SiteWhereException(
			"Unable to decode message. Type not supported: " + header.getCommand().name());
	    }
	    }
	} catch (IOException e) {
	    throw new EventDecodeException("Unable to decode protobuf message.", e);
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