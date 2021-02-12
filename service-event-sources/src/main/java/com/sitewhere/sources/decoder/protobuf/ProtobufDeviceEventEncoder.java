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

import java.io.ByteArrayOutputStream;

import com.sitewhere.communication.protobuf.proto.SiteWhere;
import com.sitewhere.communication.protobuf.proto.SiteWhere.DeviceEvent;
import com.sitewhere.communication.protobuf.proto.SiteWhere.DeviceEvent.Command;
import com.sitewhere.communication.protobuf.proto.SiteWhere.DeviceEvent.Header;
import com.sitewhere.communication.protobuf.proto.SiteWhere.GOptionalDouble;
import com.sitewhere.communication.protobuf.proto.SiteWhere.GOptionalFixed64;
import com.sitewhere.communication.protobuf.proto.SiteWhere.GOptionalString;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventEncoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Implementation of {@link IDeviceEventEncoder} that encodes device events into
 * binary using the SiteWhere Google Protocol Buffers format.
 */
public class ProtobufDeviceEventEncoder implements IDeviceEventEncoder<byte[]> {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventEncoder#encode(com.
     * sitewhere .spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    @SuppressWarnings("unchecked")
    public byte[] encode(IDecodedDeviceRequest<?> event) throws SiteWhereException {
	if (event.getRequest() instanceof IDeviceMeasurementCreateRequest) {
	    return encodeDeviceMeasurements((IDecodedDeviceRequest<IDeviceMeasurementCreateRequest>) event);
	} else if (event.getRequest() instanceof IDeviceAlertCreateRequest) {
	    return encodeDeviceAlert((IDecodedDeviceRequest<IDeviceAlertCreateRequest>) event);
	} else if (event.getRequest() instanceof IDeviceLocationCreateRequest) {
	    return encodeDeviceLocation((IDecodedDeviceRequest<IDeviceLocationCreateRequest>) event);
	} else if (event.getRequest() instanceof IDeviceRegistrationRequest) {
	    return encodeDeviceRegistration((IDecodedDeviceRequest<IDeviceRegistrationRequest>) event);
	}
	throw new SiteWhereException("Protobuf encoder encountered unknown event type: " + event.getClass().getName());
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing measurements in a protobuf
     * message.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceMeasurements(IDecodedDeviceRequest<IDeviceMeasurementCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceMeasurementCreateRequest measurements = (IDeviceMeasurementCreateRequest) event.getRequest();
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(event, Command.SendMeasurement);
	    // Payload
	    DeviceEvent.DeviceMeasurement.Builder payloadBuilder = DeviceEvent.DeviceMeasurement.newBuilder();
	    payloadBuilder.setEventDate(GOptionalFixed64.newBuilder().setValue(measurements.getEventDate().getTime()));
	    if (measurements.getMetadata() != null) {
		payloadBuilder.putAllMetadata(measurements.getMetadata());
	    }

	    payloadBuilder.setMeasurementName(GOptionalString.newBuilder().setValue(measurements.getName()));
	    payloadBuilder
		    .setMeasurementValue(GOptionalDouble.newBuilder().setValue(measurements.getValue().doubleValue()));

	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing an alert in a protobuf
     * message.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceAlert(IDecodedDeviceRequest<IDeviceAlertCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceAlertCreateRequest alert = (IDeviceAlertCreateRequest) event.getRequest();
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(event, Command.SendAlert);
	    // Payload
	    DeviceEvent.DeviceAlert.Builder payloadBuilder = DeviceEvent.DeviceAlert.newBuilder();

	    payloadBuilder.setEventDate(GOptionalFixed64.newBuilder().setValue(alert.getEventDate().getTime()));
	    payloadBuilder.setAlertType(GOptionalString.newBuilder().setValue(alert.getType()));
	    payloadBuilder.setAlertMessage(GOptionalString.newBuilder().setValue(alert.getMessage()));
	    payloadBuilder.setLevel(fromModel(alert.getLevel()));
	    if (alert.getMetadata() != null) {
		payloadBuilder.putAllMetadata(alert.getMetadata());
	    }
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing a location in a protobuf
     * message.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceLocation(IDecodedDeviceRequest<IDeviceLocationCreateRequest> event)
	    throws SiteWhereException {
	try {
	    IDeviceLocationCreateRequest location = (IDeviceLocationCreateRequest) event.getRequest();
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(event, Command.SendLocation);
	    // Payload
	    DeviceEvent.DeviceLocation.Builder payloadBuilder = DeviceEvent.DeviceLocation.newBuilder();
	    payloadBuilder.setEventDate(GOptionalFixed64.newBuilder().setValue(location.getEventDate().getTime()));
	    payloadBuilder.setLatitude(GOptionalDouble.newBuilder().setValue(location.getLatitude().doubleValue()));
	    payloadBuilder.setLongitude(GOptionalDouble.newBuilder().setValue(location.getLongitude().doubleValue()));
	    payloadBuilder.setElevation(GOptionalDouble.newBuilder().setValue(location.getElevation().doubleValue()));
	    if (location.getMetadata() != null) {
		payloadBuilder.putAllMetadata(location.getMetadata());
	    }
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Encode a {@link IDecodedDeviceRequest} containing a device registration in a
     * protobuf message.
     * 
     * @param decoded
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceRegistration(IDecodedDeviceRequest<IDeviceRegistrationRequest> decoded)
	    throws SiteWhereException {
	try {
	    IDeviceRegistrationRequest request = (IDeviceRegistrationRequest) decoded.getRequest();
	    // Header
	    DeviceEvent.Header.Builder headerBuilder = builHeader(decoded, Command.SendRegistration);
	    // Payload
	    DeviceEvent.DeviceRegistrationRequest.Builder payloadBuilder = DeviceEvent.DeviceRegistrationRequest
		    .newBuilder();
	    payloadBuilder.setAreaToken(GOptionalString.newBuilder().setValue(request.getAreaToken()));
	    payloadBuilder.setDeviceTypeToken(GOptionalString.newBuilder().setValue(request.getDeviceTypeToken()));
	    payloadBuilder.setCustomerToken(GOptionalString.newBuilder().setValue(request.getCustomerToken()));
	    if (request.getMetadata() != null) {
		payloadBuilder.putAllMetadata(request.getMetadata());
	    }
	    // Write to byte-stream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    headerBuilder.build().writeDelimitedTo(out);
	    payloadBuilder.build().writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    private static SiteWhere.DeviceEvent.AlertLevel fromModel(AlertLevel level) {
	if (level == null)
	    return SiteWhere.DeviceEvent.AlertLevel.Info;
	switch (level) {
	case Info:
	    return SiteWhere.DeviceEvent.AlertLevel.Info;
	case Warning:
	    return SiteWhere.DeviceEvent.AlertLevel.Warning;
	case Error:
	    return SiteWhere.DeviceEvent.AlertLevel.Error;
	case Critical:
	    return SiteWhere.DeviceEvent.AlertLevel.Critical;
	default:
	    return SiteWhere.DeviceEvent.AlertLevel.UNRECOGNIZED;
	}
    }

    private static Header.Builder builHeader(IDecodedDeviceRequest<?> event, DeviceEvent.Command command) {
	DeviceEvent.Header.Builder headerBuilder = DeviceEvent.Header.newBuilder();
	headerBuilder.setCommand(command);
	headerBuilder.setDeviceToken(GOptionalString.newBuilder().setValue(event.getDeviceToken()));
	if (event.getOriginator() != null) {
	    headerBuilder.setOriginator(GOptionalString.newBuilder().setValue(event.getOriginator()));
	}
	return headerBuilder;
    }
}