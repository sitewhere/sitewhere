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
package com.sitewhere.sources.decoder.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.communication.protobuf.proto.SiteWhere;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.sources.DecodedDeviceRequest;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Decodes a message payload that was previously encoded using the Google
 * Protocol Buffers with the SiteWhere proto.
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

	    SiteWhere.DeviceEvent.Header header = SiteWhere.DeviceEvent.Header.parseDelimitedFrom(stream);

	    List<IDecodedDeviceRequest<?>> results = new ArrayList<IDecodedDeviceRequest<?>>();
	    switch (header.getCommand()) {
	    case SendRegistration: {
		SiteWhere.DeviceEvent.DeviceRegistrationRequest registration = SiteWhere.DeviceEvent.DeviceRegistrationRequest
			.parseDelimitedFrom(stream);
		getLogger().debug("Decoded registration for: " + header.getDeviceToken().getValue());
		DeviceRegistrationRequest request = new DeviceRegistrationRequest();
		request.setDeviceTypeToken(registration.getDeviceTypeToken().getValue());
		if (registration.hasAreaToken()) {
		    request.setAreaToken(registration.getAreaToken().getValue());
		}
		if (registration.hasAssetToken()) {
		    request.setAssetToken(registration.getAssetToken().getValue());
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
	    case SendAcknowledgement: {
		SiteWhere.DeviceEvent.DeviceAcknowledge ack = SiteWhere.DeviceEvent.DeviceAcknowledge
			.parseDelimitedFrom(stream);
		getLogger().debug("Decoded acknowledge for: " + header.getDeviceToken().getValue());
		DeviceCommandResponseCreateRequest request = new DeviceCommandResponseCreateRequest();
		request.setOriginatingEventId(UUID.fromString(header.getOriginator().getValue()));
		request.setResponse(ack.getMessage().getValue());

		DecodedDeviceRequest<IDeviceCommandResponseCreateRequest> decoded = new DecodedDeviceRequest<IDeviceCommandResponseCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator().getValue());
		}
		results.add(decoded);
		decoded.setDeviceToken(header.getDeviceToken().getValue());
		decoded.setRequest(request);
		return results;
	    }
	    case SendLocation: {
		SiteWhere.DeviceEvent.DeviceLocation location = SiteWhere.DeviceEvent.DeviceLocation
			.parseDelimitedFrom(stream);
		getLogger().debug("Decoded location for: " + header.getDeviceToken().getValue());
		DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
		request.setLatitude(new BigDecimal(location.getLatitude().getValue()));
		request.setLongitude(new BigDecimal(location.getLongitude().getValue()));
		request.setElevation(new BigDecimal(location.getElevation().getValue()));

		if (location.hasUpdateState()) {
		    request.setUpdateState(location.getUpdateState().getValue());
		}
		Map<String, String> metadata = location.getMetadataMap();
		request.setMetadata(metadata);

		if (location.hasEventDate()) {
		    request.setEventDate(new Date(location.getEventDate().getValue()));
		} else {
		    request.setEventDate(new Date());
		}

		DecodedDeviceRequest<IDeviceLocationCreateRequest> decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator().getValue());
		}
		results.add(decoded);
		decoded.setDeviceToken(header.getDeviceToken().getValue());
		decoded.setRequest(request);
		return results;
	    }
	    case SendAlert: {
		SiteWhere.DeviceEvent.DeviceAlert alert = SiteWhere.DeviceEvent.DeviceAlert.parseDelimitedFrom(stream);
		getLogger().debug("Decoded alert for: " + header.getDeviceToken().getValue());
		DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
		request.setType(alert.getAlertType().getValue());
		request.setMessage(alert.getAlertMessage().getValue());
		request.setLevel(fromProtocolBuffer(alert.getLevel()));

		if (alert.hasUpdateState()) {
		    request.setUpdateState(alert.getUpdateState().getValue());
		}
		Map<String, String> metadata = alert.getMetadataMap();
		request.setMetadata(metadata);

		if (alert.hasEventDate()) {
		    request.setEventDate(new Date(alert.getEventDate().getValue()));
		} else {
		    request.setEventDate(new Date());
		}
		DecodedDeviceRequest<IDeviceAlertCreateRequest> decoded = new DecodedDeviceRequest<IDeviceAlertCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator().getValue());
		}
		results.add(decoded);
		decoded.setDeviceToken(header.getDeviceToken().getValue());
		decoded.setRequest(request);
		return results;
	    }
	    case SendMeasurement: {
		SiteWhere.DeviceEvent.DeviceMeasurement dm = SiteWhere.DeviceEvent.DeviceMeasurement
			.parseDelimitedFrom(stream);
		getLogger().debug("Decoded measurement for: " + header.getDeviceToken().getValue());

		DeviceMeasurementCreateRequest request = new DeviceMeasurementCreateRequest();

		request.setName(dm.getMeasurementName().getValue());
		request.setValue(new BigDecimal(dm.getMeasurementValue().getValue()));

		if (dm.hasUpdateState()) {
		    request.setUpdateState(dm.getUpdateState().getValue());
		}

		Map<String, String> metadata = dm.getMetadataMap();
		request.setMetadata(metadata);

		if (dm.hasEventDate()) {
		    request.setEventDate(new Date(dm.getEventDate().getValue()));
		} else {
		    request.setEventDate(new Date());
		}
		DecodedDeviceRequest<IDeviceMeasurementCreateRequest> decoded = new DecodedDeviceRequest<IDeviceMeasurementCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator().getValue());
		}
		decoded.setDeviceToken(header.getDeviceToken().getValue());
		decoded.setRequest(request);
		results.add(decoded);
		return results;
	    }
	    case CreateStream: {
		SiteWhere.DeviceEvent.DeviceStream devStream = SiteWhere.DeviceEvent.DeviceStream
			.parseDelimitedFrom(stream);
		getLogger().debug("Decoded stream for: " + header.getDeviceToken().getValue());
		DeviceStreamCreateRequest request = new DeviceStreamCreateRequest();
		request.setStreamId(devStream.getStreamId().getValue());
		request.setContentType(devStream.getContentType().getValue());

		Map<String, String> metadata = devStream.getMetadataMap();
		request.setMetadata(metadata);

		DecodedDeviceRequest<IDeviceStreamCreateRequest> decoded = new DecodedDeviceRequest<IDeviceStreamCreateRequest>();
		if (header.hasOriginator()) {
		    decoded.setOriginator(header.getOriginator().getValue());
		}
		results.add(decoded);
		decoded.setDeviceToken(header.getDeviceToken().getValue());
		decoded.setRequest(request);
		return results;
	    }
	    case UNRECOGNIZED:
	    default: {
		throw new SiteWhereException(
			"Unable to decode message. Type not supported: " + header.getCommand().name());
	    }

	    // case SEND_DEVICE_STREAM_DATA: {
	    // DeviceStreamData streamData = DeviceStreamData.parseDelimitedFrom(stream);
	    // getLogger().debug("Decoded stream data for: " + streamData.getHardwareId());
	    // DeviceStreamDataCreateRequest request = new DeviceStreamDataCreateRequest();
	    // request.setStreamId(streamData.getStreamId());
	    // request.setSequenceNumber(streamData.getSequenceNumber());
	    // request.setData(streamData.getData().toByteArray());
	    //
	    // List<Metadata> pbmeta = streamData.getMetadataList();
	    // Map<String, String> metadata = new HashMap<String, String>();
	    // for (Metadata meta : pbmeta) {
	    // metadata.put(meta.getName(), meta.getValue());
	    // }
	    // request.setMetadata(metadata);
	    //
	    // if (streamData.hasEventDate()) {
	    // request.setEventDate(new Date(streamData.getEventDate()));
	    // } else {
	    // request.setEventDate(new Date());
	    // }
	    //
	    // DecodedDeviceRequest<IDeviceStreamDataCreateRequest> decoded = new
	    // DecodedDeviceRequest<IDeviceStreamDataCreateRequest>();
	    // if (header.hasOriginator()) {
	    // decoded.setOriginator(header.getOriginator());
	    // }
	    // results.add(decoded);
	    // decoded.setDeviceToken(streamData.getHardwareId());
	    // decoded.setRequest(request);
	    // return results;
	    // }
	    // case REQUEST_DEVICE_STREAM_DATA: {
	    // DeviceStreamDataRequest request =
	    // DeviceStreamDataRequest.parseDelimitedFrom(stream);
	    // getLogger().debug("Decoded stream data request for: " +
	    // request.getHardwareId());
	    // SendDeviceStreamDataRequest send = new SendDeviceStreamDataRequest();
	    // send.setStreamId(request.getStreamId());
	    // send.setSequenceNumber(request.getSequenceNumber());
	    //
	    // DecodedDeviceRequest<ISendDeviceStreamDataRequest> decoded = new
	    // DecodedDeviceRequest<ISendDeviceStreamDataRequest>();
	    // if (header.hasOriginator()) {
	    // decoded.setOriginator(header.getOriginator());
	    // }
	    // results.add(decoded);
	    // decoded.setDeviceToken(request.getHardwareId());
	    // decoded.setRequest(send);
	    // return results;
	    // }
	    // default: {
	    // throw new SiteWhereException(
	    // "Unable to decode message. Type not supported: " +
	    // header.getCommand().name());
	    // }
	    }
	} catch (IOException e) {
	    throw new EventDecodeException("Unable to decode protobuf message.", e);
	}
    }

    private static AlertLevel fromProtocolBuffer(SiteWhere.DeviceEvent.AlertLevel level) {
	switch (level) {
	case Info:
	    return AlertLevel.Info;
	case Warning:
	    return AlertLevel.Warning;
	case Error:
	    return AlertLevel.Error;
	case Critical:
	    return AlertLevel.Critical;
	case UNRECOGNIZED:
	default:
	    return AlertLevel.Info;
	}
    }
}