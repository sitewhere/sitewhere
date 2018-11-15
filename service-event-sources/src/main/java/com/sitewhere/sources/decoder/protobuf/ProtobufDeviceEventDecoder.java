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
import java.util.List;
import java.util.Map;

import com.sitewhere.communication.protobuf.proto3.SiteWhere2;
import com.sitewhere.communication.protobuf.proto3.SiteWhere2.DeviceEvent;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Decodes a message payload that was previously encoded using the Google
 * Protocol Buffers with the SiteWhere proto.
 * 
 * @author Derek
 */
public class ProtobufDeviceEventDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

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
	    
	    SiteWhere2.DeviceEvent.Header header = SiteWhere2.DeviceEvent.Header.parseDelimitedFrom(stream);
	    
	    List<IDecodedDeviceRequest<?>> results = new ArrayList<IDecodedDeviceRequest<?>>();
	    switch (header.getCommand()) {
	    case Registration: {
		DeviceEvent.DeviceRegistrationRequest registration = DeviceEvent.DeviceRegistrationRequest.parseDelimitedFrom(stream);		
		getLogger().debug("Decoded registration for: " + header.getDeviceToken().getValue());
		DeviceRegistrationRequest request = new DeviceRegistrationRequest();
		request.setDeviceTypeToken(registration.getDeviceTypeToken().getValue());
		if (registration.hasAreaToken()) {
		    request.setAreaToken(registration.getAreaToken().getValue());
		}
		if (registration.hasCustomerToken()) {
		    request.setCustomerToken(registration.getCustomerToken().getValue());
		}
		Map<String, String> metadata = registration.getMetadataMap();
		request.setMetadata(metadata);

		DecodedDeviceRequest<IDeviceRegistrationRequest> decoded = new DecodedDeviceRequest<IDeviceRegistrationRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator().getValue());
		}
		results.add(decoded);
		decoded.setDeviceToken(header.getDeviceToken().getValue());
		decoded.setRequest(request);
		return results;
	    }
	    case Ackknowledgement:
		break;
	    case UNRECOGNIZED:
	    default: {
		throw new SiteWhereException("Unable to decode message. Type not supported: " + header.getCommand().name());
	    }
	    
//	    case SEND_REGISTRATION: {
//		RegisterDevice register = RegisterDevice.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded registration for: " + register.getHardwareId());
//		DeviceRegistrationRequest request = new DeviceRegistrationRequest();
//		request.setDeviceTypeToken(register.getDeviceTypeToken());
//		if (register.hasAreaToken()) {
//		    request.setAreaToken(register.getAreaToken());
//		}
//
//		List<Metadata> pbmeta = register.getMetadataList();
//		Map<String, String> metadata = new HashMap<String, String>();
//		for (Metadata meta : pbmeta) {
//		    metadata.put(meta.getName(), meta.getValue());
//		}
//		request.setMetadata(metadata);
//
//		DecodedDeviceRequest<IDeviceRegistrationRequest> decoded = new DecodedDeviceRequest<IDeviceRegistrationRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(register.getHardwareId());
//		decoded.setRequest(request);
//		return results;
//	    }
//	    case SEND_ACKNOWLEDGEMENT: {
//		Acknowledge ack = Acknowledge.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded acknowledge for: " + ack.getHardwareId());
//		DeviceCommandResponseCreateRequest request = new DeviceCommandResponseCreateRequest();
//		request.setOriginatingEventId(UUID.fromString(header.getOriginator()));
//		request.setResponse(ack.getMessage());
//
//		DecodedDeviceRequest<IDeviceCommandResponseCreateRequest> decoded = new DecodedDeviceRequest<IDeviceCommandResponseCreateRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(ack.getHardwareId());
//		decoded.setRequest(request);
//		return results;
//	    }
//	    case SEND_DEVICE_MEASUREMENTS: {
//		DeviceMeasurements dm = DeviceMeasurements.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded measurement for: " + dm.getHardwareId());
//		List<Measurement> measurements = dm.getMeasurementList();
//		for (Measurement current : measurements) {
//		    DeviceMeasurementCreateRequest request = new DeviceMeasurementCreateRequest();
//		    request.setName(current.getMeasurementId());
//		    request.setValue(current.getMeasurementValue());
//
//		    if (dm.hasUpdateState()) {
//			request.setUpdateState(dm.getUpdateState());
//		    }
//
//		    List<Metadata> pbmeta = dm.getMetadataList();
//		    Map<String, String> metadata = new HashMap<String, String>();
//		    for (Metadata meta : pbmeta) {
//			metadata.put(meta.getName(), meta.getValue());
//		    }
//		    request.setMetadata(metadata);
//
//		    if (dm.hasEventDate()) {
//			request.setEventDate(new Date(dm.getEventDate()));
//		    } else {
//			request.setEventDate(new Date());
//		    }
//
//		    DecodedDeviceRequest<IDeviceMeasurementCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementCreateRequest>();
//		    if (header.hasOriginator()) {
//			decoded.setOriginator(header.getOriginator());
//		    }
//		    decoded.setDeviceToken(dm.getHardwareId());
//		    decoded.setRequest(request);
//		    results.add(decoded);
//		}
//		return results;
//	    }
//	    case SEND_DEVICE_LOCATION: {
//		DeviceLocation location = DeviceLocation.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded location for: " + location.getHardwareId());
//		DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
//		request.setLatitude(location.getLatitude());
//		request.setLongitude(location.getLongitude());
//		request.setElevation(location.getElevation());
//
//		if (location.hasUpdateState()) {
//		    request.setUpdateState(location.getUpdateState());
//		}
//
//		List<Metadata> pbmeta = location.getMetadataList();
//		Map<String, String> metadata = new HashMap<String, String>();
//		for (Metadata meta : pbmeta) {
//		    metadata.put(meta.getName(), meta.getValue());
//		}
//		request.setMetadata(metadata);
//
//		if (location.hasEventDate()) {
//		    request.setEventDate(new Date(location.getEventDate()));
//		} else {
//		    request.setEventDate(new Date());
//		}
//
//		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(location.getHardwareId());
//		decoded.setRequest(request);
//		return results;
//	    }
//	    case SEND_DEVICE_ALERT: {
//		DeviceAlert alert = DeviceAlert.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded alert for: " + alert.getHardwareId());
//		DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
//		request.setType(alert.getAlertType());
//		request.setMessage(alert.getAlertMessage());
//		request.setLevel(AlertLevel.Info);
//
//		if (alert.hasUpdateState()) {
//		    request.setUpdateState(alert.getUpdateState());
//		}
//
//		List<Metadata> pbmeta = alert.getMetadataList();
//		Map<String, String> metadata = new HashMap<String, String>();
//		for (Metadata meta : pbmeta) {
//		    metadata.put(meta.getName(), meta.getValue());
//		}
//		request.setMetadata(metadata);
//
//		if (alert.hasEventDate()) {
//		    request.setEventDate(new Date(alert.getEventDate()));
//		} else {
//		    request.setEventDate(new Date());
//		}
//
//		DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(alert.getHardwareId());
//		decoded.setRequest(request);
//		return results;
//	    }
//	    case SEND_DEVICE_STREAM: {
//		DeviceStream devStream = DeviceStream.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded stream for: " + devStream.getHardwareId());
//		DeviceStreamCreateRequest request = new DeviceStreamCreateRequest();
//		request.setStreamId(devStream.getStreamId());
//		request.setContentType(devStream.getContentType());
//
//		List<Metadata> pbmeta = devStream.getMetadataList();
//		Map<String, String> metadata = new HashMap<String, String>();
//		for (Metadata meta : pbmeta) {
//		    metadata.put(meta.getName(), meta.getValue());
//		}
//		request.setMetadata(metadata);
//
//		DecodedDeviceRequest<IDeviceStreamCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamCreateRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(devStream.getHardwareId());
//		decoded.setRequest(request);
//		return results;
//	    }
//	    case SEND_DEVICE_STREAM_DATA: {
//		DeviceStreamData streamData = DeviceStreamData.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded stream data for: " + streamData.getHardwareId());
//		DeviceStreamDataCreateRequest request = new DeviceStreamDataCreateRequest();
//		request.setStreamId(streamData.getStreamId());
//		request.setSequenceNumber(streamData.getSequenceNumber());
//		request.setData(streamData.getData().toByteArray());
//
//		List<Metadata> pbmeta = streamData.getMetadataList();
//		Map<String, String> metadata = new HashMap<String, String>();
//		for (Metadata meta : pbmeta) {
//		    metadata.put(meta.getName(), meta.getValue());
//		}
//		request.setMetadata(metadata);
//
//		if (streamData.hasEventDate()) {
//		    request.setEventDate(new Date(streamData.getEventDate()));
//		} else {
//		    request.setEventDate(new Date());
//		}
//
//		DecodedDeviceRequest<IDeviceStreamDataCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamDataCreateRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(streamData.getHardwareId());
//		decoded.setRequest(request);
//		return results;
//	    }
//	    case REQUEST_DEVICE_STREAM_DATA: {
//		DeviceStreamDataRequest request = DeviceStreamDataRequest.parseDelimitedFrom(stream);
//		getLogger().debug("Decoded stream data request for: " + request.getHardwareId());
//		SendDeviceStreamDataRequest send = new SendDeviceStreamDataRequest();
//		send.setStreamId(request.getStreamId());
//		send.setSequenceNumber(request.getSequenceNumber());
//
//		DecodedDeviceRequest<ISendDeviceStreamDataRequest> decoded = new DecodedDeviceRequest<ISendDeviceStreamDataRequest>();
//		if (header.hasOriginator()) {
//		    decoded.setOriginator(header.getOriginator());
//		}
//		results.add(decoded);
//		decoded.setDeviceToken(request.getHardwareId());
//		decoded.setRequest(send);
//		return results;
//	    }
//	    default: {
//		throw new SiteWhereException(
//			"Unable to decode message. Type not supported: " + header.getCommand().name());
//	    }
	    }
	    
	    return results;
	} catch (IOException e) {
	    throw new EventDecodeException("Unable to decode protobuf message.", e);
	}
    }
}